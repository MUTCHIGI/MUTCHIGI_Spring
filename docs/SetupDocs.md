# MUTCHIGI Spring 설정

## Java 설치
> Java 설치 버전 : Java 17

- 자바 설치 및 환경 설정은 OS에 따라 달라질 수 있으므로 래퍼런스를 참고하여 설치하세요.
- 설치 완료 확인은 cmd에서 아래를 통해 확인해주십시오.
```
  C:\> javac -version
  ```
> 결과물 : javac 17.0.11

## Spring 설치
 Spring은 인텔리제이로 Open한 경우 자동으로 설치와 설정을 진행합니다.
> Spring 버전 : 3.3.4 
>
> Spring Security : 6.3.3

## DB 연결
> postgres SQL 버전 : 16.4

postgres 설치 이후 **pgAdmin4**를 통해 **application.properties**에 있는 설정대로 DB를 구축합니다.

- admin id : postgres
- admin pw : qwer1234
- DB name : MUTCHIGI

### DB 확인 방법
> IntelliJ에서 DB를 쉽게 확인 할 수 있습니다.
 
1. 인텔리제이 화면에서 우측 Datbase를 열고 + 버튼을 누릅니다.
2. DataSource의 Postgres를 누릅니다.
3. 위에서 설정한 대로 user, password, Database를 입력해줍니다.
4. Test Connection을 눌러 연결을 확인합니다.
5. 연결이 확인되면 Apply하고 OK로 종료합니다.
6. 즉시 console창이 뜨면 성공입니다.

## Spring 실행

설치가 모두 완료된 이후에는 src>main>java>com>CAUCSD>MUTHIGI 디렉토리의
**MutchigiApplication.java**을 Run해주세요.

localhost:8080 or 127.0.0.1:8080 으로 접속을 시도해보고 Google 로그인 URi로 이동하면 실행 성공입니다.

## Swagger-ui docs 확인방법
**localhost:8080/swagger-ui/index.html or 127.0.0.1:8080/swagger-ui/index.html**로 접속을 시도하세요.

### Swagger Authentication
> 스웨거 인증은 JWT형식으로 발급된 토큰을 입력해야 합니다.

1. localhost:8080 or 127.0.0.1:8080으로 접속하여 Google 로그인을 진행해주세요.
  > (현재는 아래 단계를 진행하지 않아도 로그인해서 토큰만 얻으면 swagger 테스트 진행이 가능합니다) <p>
  > 만약 인증을 없애고 싶다면 (크롬 기준) 개발자 도구 > 애플리케이션 > 쿠키 에 있는 JSESSIONID를 삭제하면 가능해집니다.
2. /token에 발급한 token을 요청하여 이를 복사하세요
3. swagger-ui 우상단에 있는 Authorize에 token을 붙여넣기하고 Authorize로 인증하세요.
4. 그러면 해당 Token을 이용해 인증이 필요한 다른 Api들을 이용할 수 있습니다.


## Google 로그인 API
> 구글 로그인 API 및 전반적인 API는 작성자 "윤도경"에 의해 제어되고 관리되고 있습니다.
> 
>  관련된 오류나 문제점이 있다면 문의바랍니다.
