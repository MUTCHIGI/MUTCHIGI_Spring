# 백엔드 인증 후 API 테스트 방법 정리

우선 본 Spring 어플리케이션의 인증방식은 다음과 같습니다.

# 현재는 아래과정 없이 SetupDocs.md에 적힌 인증방식으로 모두 인증이 가능합니다.

## 인증과정
### 1. 특정 domain을 제외한 나머지 domain들은 모두 인증이 필요합니다.
> 인증은 Google에서 제공하는 Oauth2 방식을 이용하며 흔히 사용하는 구글 로그인 API라 생각하면 됩니다.

- 특정 domain들은 인증 없이도 이용이 가능하며 그 domain들은 src/main/java/com/CAUCSD/MUTCHIGI/user/**security/SecurityConfig.java**에서 확인 가능합니다.
- SecurityFilterChain filterChain의 requestMatchers에 적힌 도메인 뒤에 .permitAll 메소드가 붙어있다면 인증없이 허용된 domain 들입니다.


### 2. 그래서 구글 로그인 인증 domain으로 인증을 진행한 이후 구글에서 제공한 토큰을 받고 이를 백엔드에서 다시 새로운 토큰(JWT 형식)으로 발급해줍니다.
> 즉, 구글이 제공한 JWT가 아닌 Spring 서버에서 발급한 JWT만으로 인증이 가능하다는 뜻입니다.

- 다만, 이 과정에서 Front는 구글에서 제공한 토큰을 받아서 저장해 두어야 합니다. (추후 구글 계정 정보 받아와야 하기 때문에)
- 이 방식은 백엔드만으로 구글 JWT를 받아올 방법이 없기 때문에 Postman의 Authentication을 이용합니다.

### 3. Postman의 Authentication으로 구글 JWT 발급받기
> Postman의 Authentication으로 구글 JWT는 다음 순서로 발급받으면 됩니다.

1. Postman의 새로운 Request를 생성합니다.
2. Authorization탭으로 들어갑니다. (Params 다음 탭)
3. Type을 OAuth2로 설정합니다.
4. Configure Token부터 다음과 같이 작성합니다.
   - Token Name : 아무거나 작성
   - Callback URL : http://localhost:8080/login/oauth2/code/google (Google Cloud Console의 리다이렉트 URL입니다.)
   - Auth URL : https://accounts.google.com/o/oauth2/auth
   - Acecess Token URL : https://oauth2.googleapis.com/token
   - Client ID : application.properties의 spring.security.oauth2.client.registration.google.client-id 값
   - Client Secret : application.properties의 spring.security.oauth2.client.registration.google.client-secret 값
   - Scope : email profile
   - Client Authentication : Send as Basic Auth Header 선택
5. Get New Access Token 버튼 클릭
6. Google 로그인 과정 진행
7. 발급 완료

> 발급이 완료되면 Google에서 만료 기한이 10분인 JWT를 발급해줍니다.
> <p> 여기서 id_token으로 Google 계정 정보를 얻어올 수 있습니다.

### 4. 구글 계정 정보 받아오기 APi에 적용
> domain "/auth/google" 위치에 POST로 보내고 있는 API에 위에서 받은 id_token 값을 입력합니다.

- 주의 할 점은 RequestBody에 따옴표 같은 요소는 모두 지우고 id_token을 그대로 복사/붙여넣기 해야합니다.

그러면 지금 (10월 10일) 기준으로 DB에 저장된 userId, email(google), 프로필사진URL, Spring 인증용 Token이 반환됩니다.
- 자세한 사항은 swagger-ui docs 파일을 참고하세요.
