package com.CAUCSD.MUTCHIGI.quiz;

import com.CAUCSD.MUTCHIGI.user.UserEntity;
import com.CAUCSD.MUTCHIGI.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    // 임시로 상대경로로 폴더 위치 지정
    private final String thumbnailDir = "C:\\thumbnailURL";

    public List<Long> getQuizIDList(int page, int offset, int typeId, int modId, QuizSort sort, String quizTitle){
        PageRequest pageRequest = switch (sort) {
            case DATEAS -> PageRequest.of(page, offset, Sort.by("releaseDate").ascending());
            case DATEDS -> PageRequest.of(page, offset, Sort.by("releaseDate").descending());
            case NAMEAS -> PageRequest.of(page, offset, Sort.by("quizName").ascending());
            case NAMEDS -> PageRequest.of(page, offset, Sort.by("quizName").descending());
            case VIEWAS -> PageRequest.of(page, offset, Sort.by("userPlayCount").ascending());
            case VIEWDS -> PageRequest.of(page, offset, Sort.by("userPlayCount").descending());
        };

        Page<QuizEntity> quizEntities;

        //전체 조회
        // 조회 조건에 따라 적절한 메소드 호출
        if (modId == 0 && typeId == 0) {
            quizEntities = quizRepository.findByQuizNameContaining(quizTitle, pageRequest);
        } else if (modId == 0) {
            quizEntities = quizRepository.findByQuizNameContainingAndTypeId(quizTitle, typeId, pageRequest);
        } else if (typeId == 0) {
            quizEntities = quizRepository.findByQuizNameContainingAndModId(quizTitle, modId, pageRequest);
        } else {
            quizEntities = quizRepository.findByQuizNameContainingAndTypeIdAndModId(quizTitle, typeId, modId, pageRequest);
        }

        return quizEntities.stream()
                .map(QuizEntity::getQuizId)
                .toList();
    }

    public QuizEntity getQuizById(long quizId){
        return quizRepository.findById(quizId).orElse(null);
    }

    public List<QuizEntity> getQuizByIdList(List<Long> quizIdList) throws IOException {
        List<QuizEntity> quizEntities = quizRepository.findAllById(quizIdList);

        return quizEntities;
    }

    public Resource serveImageFromLocalStorage(String filename) throws  IOException{
        Path imageStoragePath = Paths.get(thumbnailDir);
        Path filePath = imageStoragePath.resolve(filename);
        Resource getQuizImageResource = new UrlResource(filePath.toUri());
        return getQuizImageResource;
    }

    public QuizEntity updateQuiz(QuizEntity quizEntity){
        if(!quizRepository.existsById(quizEntity.getQuizId())){
            throw new EntityNotFoundException("Quiz not found while upadating"+ quizEntity.getQuizId());
        }
        return quizRepository.save(quizEntity);
    }

    public List<QuizEntity> getQuizzesByIds(List<Long> quizIds){
        return quizRepository.findAllById(quizIds);
    }

    public QuizEntity createQuiz(QuizDTO quizDTO){
        QuizEntity quizEntity = new QuizEntity();
        //System.out.println("여기에 문제가 발생한건가 이름 : "+ quizDTO.getQuizName());
        LocalTime songPlayTime = LocalTime.of(
                quizDTO.getHour(),
                quizDTO.getMinute(),
                quizDTO.getSecond()
        );

        if(!(quizDTO.getModId() == 1 || quizDTO.getModId() == 2)){
            throw new EntityNotFoundException("Mod id is not 1 or 2");
        }
        if(!(quizDTO.getTypeId()==1 || quizDTO.getTypeId()==2)){
            throw new EntityNotFoundException("Type id is not 1 or 2");
        }

        quizEntity.setSongCount(0);
        quizEntity.setQuizName(quizDTO.getQuizName());
        quizEntity.setQuizDescription(quizDTO.getQuizDescription());
        quizEntity.setReleaseDate(LocalDate.now());
        quizEntity.setUserPlayCount(0);
        quizEntity.setTypeId(quizDTO.getTypeId());
        quizEntity.setModId(quizDTO.getModId());
        quizEntity.setHintCount(quizDTO.getHintCount());
        quizEntity.setSongPlayTime(songPlayTime);
        quizEntity.setUseDisAlg(quizDTO.isUseDisAlg());
        quizEntity.setInstrumentId(quizDTO.getInstrumentId());

        UserEntity user = userRepository.findById(quizDTO.getUserId()).orElse(null);

        if(user == null){
            throw new IllegalArgumentException("User not found" + quizDTO.getUserId());
        }

        quizEntity.setUser(user);

        return quizRepository.save(quizEntity);
    }

    public String saveThumbnailURL(MultipartFile file, long quizId) throws IOException {
        String fileName = quizId + "_" + System.currentTimeMillis() + ".png";
        File thumbnailFile = new File(thumbnailDir, fileName);
        File parentDir = thumbnailFile.getParentFile();
        if(!parentDir.exists()){
            //System.out.println("내부 메서드 진입 테스트 if문" );
            if(!parentDir.mkdir()){
                throw new IOException("디렉토리를 생성할 수 없습니다." + parentDir.getAbsolutePath());
            }
        }
        // quizId로 시작하는 파일이 있는지 확인
        File[] existingFiles = parentDir.listFiles((dir, name) -> name.startsWith(String.valueOf(quizId) + "_"));
        if (existingFiles != null) {
            for (File existingFile : existingFiles) {
                if (existingFile.delete()) {
                    System.out.println("기존 파일 삭제: " + existingFile.getAbsolutePath());
                } else {
                    System.out.println("파일 삭제 실패: " + existingFile.getAbsolutePath());
                }
            }
        }


        //System.out.println("내부 메서드 진입 테스트" + parentDir);
        file.transferTo(thumbnailFile);

        return fileName;
    }

    // 이미지 리사이즈 메서드
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        return resizedImage;
    }

}
