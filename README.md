# 함께 노래를 맞추는 프로젝트, MUTCHIGI

## Overview

#### 프로젝트 기간
2024.09 ~ 2024.11

#### 팀원 구성
Frontend + AI 2 , Backend 1

#### Github
[Backend](https://github.com/MUTCHIGI/MUTCHIGI_Spring)

#### 유형
Web



## Project

### 프로젝트 소개

누구나 여러 이용자들과 함께 노래 퀴즈를 풀거나 직접 퀴즈를 간단하게 만들어서 즐길 수 있는 콘텐츠를 제공하는 웹서비스입니다.

또한, 노래에 대한 전문성이 높다고 생각하면 개별의 악기들로 분리된 노래를 듣고 퀴즈도 함께 풀어볼 수 있습니다.

### 협업

<details>
<summary>Scrum</summary>
<div markdown="1">

### 디스코드 채널 다양화 및 일정 정보 공유
    
![image](https://github.com/user-attachments/assets/dd047099-4554-479e-ba01-6ab45cfedde5)

</div>
</details>

    
<details>
<summary>Collaboration Tools</summary>
<div markdown="1">

![image 1](https://github.com/user-attachments/assets/dc9fa09a-e27d-4dbf-ae98-9e96075c372b)

![image 2](https://github.com/user-attachments/assets/97080314-bed4-45d3-86ec-57951f7bae41)


</div>
</details>  


    

### **기술 스택**

## **Frontend** 💻
<details>
<summary>보기</summary>
<div markdown="1">

![Untitled 1](https://github.com/user-attachments/assets/6d2bc84f-73ec-4265-a60c-d887b4f17f72)
![Untitled](https://github.com/user-attachments/assets/2f440764-35a4-4f79-8ee5-74f176daa270)
![image 3](https://github.com/user-attachments/assets/ca2fc812-5cba-4325-97cd-cfd408f3b182)


</div>
</details>  




## **Backend**  🖥
<details>
<summary>보기</summary>
<div markdown="1">

![Untitled 2](https://github.com/user-attachments/assets/1f87ecce-e363-4251-8e86-ec6d30c794b1)
![Untitled 3](https://github.com/user-attachments/assets/122ad0a8-e4a9-4e15-9af8-896cfedfbfc7)
![Untitled 4](https://github.com/user-attachments/assets/046102db-ea79-48a3-98d1-4d14504de3a2)
![Untitled 5](https://github.com/user-attachments/assets/0b97c069-86d3-4774-92ae-f0f769447938)
![Untitled 6](https://github.com/user-attachments/assets/92a1c033-0ac3-42d9-b79e-2054825c3883)
![image 4](https://github.com/user-attachments/assets/2482d6aa-1a6d-47a9-b487-e34eb8155640)
![image 5](https://github.com/user-attachments/assets/83e05648-b49a-41f6-93e2-c682cc5d24f8)


</div>
</details>  





## **Database 💾**

<details>
<summary>보기</summary>
<div markdown="1">

![image 7](https://github.com/user-attachments/assets/1a365fd8-6c20-4b83-85e2-c3aa646bf183)
![image 6](https://github.com/user-attachments/assets/161ef0c5-11bf-4d2e-b008-e6be55848365)


</div>
</details>  




## **API ⚙**


<details>
<summary>퀴즈 관련 API</summary>
<div markdown="1">

![image 8](https://github.com/user-attachments/assets/4e7eec86-ee4a-4cba-8576-1e45676108c9)

</div>
</details>  

<details>
<summary>노래(문제) 관련 API</summary>
<div markdown="1">
  
![image 9](https://github.com/user-attachments/assets/0002a9d4-11a1-47c8-9864-a30e594f5d52)

</div>
</details>  

<details>
<summary>멀티플레이 방 관련 API</summary>
<div markdown="1">
  
![image 10](https://github.com/user-attachments/assets/9f8ef3bb-37e6-47ce-9aa6-d514b8c45198)

</div>
</details>  
    
<details>
<summary>악기분리(GCP) 관련 API</summary>
<div markdown="1">
  
![image 11](https://github.com/user-attachments/assets/5e07c145-c7cc-465d-8ba3-b2c2d0d9ebdd)

</div>
</details>  

  
<details>
<summary>인증 API</summary>
<div markdown="1">

![image 12](https://github.com/user-attachments/assets/e82336f2-7a44-414f-9dcb-cfe9664e57de)


</div>
</details>  
    
#### STOMP
    
[STOMP.md](STOMP.md)

[STOMP 문서 Github](https://github.com/MUTCHIGI/MUTCHIGI_Spring/blob/main/docs/STOMP.md)


### 아키텍처

<img width="2236" alt="System_Architecture" src="https://github.com/user-attachments/assets/89959b84-3fe2-40c4-b066-a15c86e77c0a">


### ERD

<img width="3200" alt="DB_Schema_(1)" src="https://github.com/user-attachments/assets/b2d98317-302d-4127-8a2f-a2558a6ea6a2">


# 프로젝트 주요 기능

---

## Oauth2 JWT 로그인

![image 13](https://github.com/user-attachments/assets/e89f3a2c-da02-4786-a911-38e28c19f04c)


- Google Oauth2를 이용해 Spring JWT를 발급 받아 로그인 할 수 있습니다.
- 이 프로젝트의 모든 User정보는 해당 플랫폼으로 제공 받은 것을 이용합니다.

---

## 퀴즈(노래 맞추기) 멀티플레이 기능

### **방 목록 조회 및 참가**

![image 14](https://github.com/user-attachments/assets/948baab5-a2ef-4bb1-9561-92d700c1552e)
![image 16](https://github.com/user-attachments/assets/aa15a723-475e-42a0-acc8-7119a87b4ad8)
![image 15](https://github.com/user-attachments/assets/a2cd0f67-1f20-4459-ac9c-efcfb22b3006)


- 게임이 진행중인 방은 어두운 색으로 표시하고 참가할 수 없도록 하였습니다.
- 비공개 방은 비밀번호를 입력하여야 입장이 가능합니다.
- 방을 옵션(타입 : 기본/악기분리, 모드 :  커스텀/플레이리스트) 에 따라서 조회하고 참가할 수 있습니다.

### 게임 진행 페이지(시작 전)

![image 17](https://github.com/user-attachments/assets/82dfae0b-a68d-419b-a260-df4bfd29ef78)


- 우측의 채팅을 통해 다른 유저들과 소통하고 채팅을 칠 수 있습니다.
- 방장은 중간의 Start! 버튼을 눌러 게임을 시작할 수 있습니다.
- 하단에는 유저의 정보와 채팅 등을 UI로 볼 수 있습니다.
- 방장은 하단의 UI에서 유저를 클릭하여 강퇴 할 수 있습니다.

### 게임 진행 페이지 (진행 중)

![73b800b6-8286-4095-a52a-5cfe89b2586c](https://github.com/user-attachments/assets/fab7d09f-4bfa-4663-94d9-5e86ac487038)
![7a7d7ec0-a517-4632-988f-aceceb9210d4](https://github.com/user-attachments/assets/a0a1d089-ed48-4880-8241-3cf148d360fb)


- 게임이 진행되면 남은 시간과 노래 진행상황을 중간의 Text와 Time Bar를 통해 알 수 있습니다.
- 사전에 입력된 Hint들이 설정한 시간에 따라 나오게 됩니다.
- 만약 모르는 문제가 나온 경우 참가 인원의 과반수가 Skip버튼을 눌러 건너 뛸 수 있습니다.
- 정답은 채팅에 입력한 것을 서버에서 확인하고 정답이 나온 경우 정답자와 실제 정답을 알려줍니다.
- 악기 분리 음악의 경우 정답 이전에는 악기분리된 mp3가 송출되고 정답 이후에는 실제 정답인 노래의 youtubue가 송출됩니다.
- 우 하단의 음량 조절 요소로 음량을 조절할 수 있습니다.
- 사전에 설정해둔 시작 시간에 따라서 노래가 시작됩니다.

---

## 퀴즈(노래 맞추기) 만들기 기능

### 퀴즈 조회 기능

![image 18](https://github.com/user-attachments/assets/218fad09-28f5-4a17-b2d7-f3093894f1da)

![image 19](https://github.com/user-attachments/assets/e1f1a669-5771-4f19-85a2-18bddb9d0d28)


- 이미 생성되어 있는 퀴즈를 선택하여 멀티플레이를 위한 방을 만들 수 있습니다.
- 방 이름, 공개/비공개 및 비밀번호와 인원 수를 설정하여 방을 만들 수 있습니다.
- 원하는 퀴즈가 없다면 직접 퀴즈를 만들 수 있습니다.

### 퀴즈 만들기

![image 20](https://github.com/user-attachments/assets/eb5d12f4-85f7-41f3-826d-956b7f402c04)

![image 21](https://github.com/user-attachments/assets/84402e80-3540-4791-8b8f-04e17f21cfdc)

![image 22](https://github.com/user-attachments/assets/7eb92ccb-5fd2-4483-a7c3-36ef446ac664)

- 원하는 설정으로 퀴즈를 만들 수 있습니다.
- 제목, 설명, 제한시간, 힌트 갯수 및 시간 그리고 썸네일을 설정할 수 있습니다.
- 퀴즈 생성은 노래를 한 개씩 배정하고 싶은 경우 커스텀 모드로, 플레이리스트로 여러 개를 한 번에 배정하고 싶은 경우 플레이리스트 모드로 설정합니다.
- 플레이리스트 모드는 퀴즈를 생성하기 전에 URL을 입력받습니다.

### 노래(문제) 추가

![image 23](https://github.com/user-attachments/assets/eec68405-8137-4506-958a-f417a5f408a9)
![image 24](https://github.com/user-attachments/assets/f37a0651-a709-420c-a4e7-23e51af2b235)
![image 25](https://github.com/user-attachments/assets/1dbeff5e-6ac6-4ed8-bb92-064ce6451d23)


- Youtube URL 입력을 통해 노래를 문제로 변환이 가능합니다.
- 악기 분리의 경우 GCP 서버에 따로 악기 분리 변환을 요청하고 대기합니다. (대략 1~2분 소요)
- 악기 분리의 경우 이미 서버에서 변환된 노래인 경우 변환을 요청하지 않고 서버에 존재하는 그대로 가져옵니다.
- 변환되는 동안 다른 사람이 분리해둔 노래를 추가하거나 들을 수 있습니다.
- 다른 화면에서도 생성 중인 헤더의 Toggle 버튼으로 퀴즈와 해당 퀴즈의 변환 상황을 확인할 수 있습니다.

### 악기 분리 노래 미리 듣기

![image 26](https://github.com/user-attachments/assets/434cce16-08e0-447a-a0b3-312e546d3756)

- AI모델에 의해 변환된 악기분리 음악을 보컬, 베이스, 반주, 드럼 별로 다르게 들어 볼 수 있습니다.
- 해당 노래 line이 진행되는 곳을 누르면 해당 파트의 악기로된 음악을 들을 수 있습니다.

### 노래(문제) 설정

![image 27](https://github.com/user-attachments/assets/3ebd3063-39de-4d4d-81a4-f0c7e902bc79)


- 정답, 힌트, 시작 시간을 설정할 수 있습니다.
- 정답은 OpenAI API로부터 제공받으며 Prompt Engineering을 통해 정답이 될 수 있는 것을 사전에 제공합니다.
- 힌트는 퀴즈 생성 시 설정해 두었던 대로 힌트 Type을 입력할 수 있도록 제공합니다.
- 시작시간은 Youtube 영상에 설정한대로 지정됩니다.
- 악기분리의 경우 해당 시간에서 반주/원본을 바꿔가면서 들어볼 수 있습니다.
