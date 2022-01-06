# 자바로 게임 만들기
게임 이름 : agar.io (아갈캣)
이름 : 전상용
학교 : 영진전문대학교 컴퓨터정보계열 웹데이터베이스 A반
이메일 : tkddyd420@naver.com

# 개발 환경
Eclipse IDE for Java Developers Version : 2020-06(4.16.0)
ORACLE SQL DEVELOPER

# 설명
혼자서 게임을 할 수 있는 오프라인 모드와 게임서버와 채팅서버를 열고 여러 클라이언트와 같이 게임을 할 수 있는 온라인 모드로 구성되어 있습니다.
온라인 모드를 하기 위해서는 자바와 오라클 DB를 연동하기 위한 OJDBC8이 필요합니다.
그 후 오라클 DB를 생성합니다.

### Oracle DB
Table 3개를 만듭니다.
  1. 세포의 이름(NAME), x값(X), y값(Y), 크기(MASS) 컬럼이 있는 CELL 테이블
  2. 먹이의 이름(NAME), x값(X), y값(Y), 크기(MASS) 컬럼이 있는 PARTICLE 테이블
  3. 클라이언트 점수 번호(NUM), 이름(NAME), 점수(SCORE) 컬럼이 있는 SCORE 테이블

### 실행하기 전 DB 변경하기
온라인/게임 서버/GameWorld.java , 온라인/게임클라이언트/ClientMain.java 에서 DB를 연결하는 makeConnection() 메서드를 찾습니다.
그 메서드에서 사용자의 db값을 알맞게 수정하여 DB와 연결합니다.


# 게임 실행 방법
1. 온라인/게임서버/GameServerMain.java 실행
2. 온라인/채팅서버/ServerMain.java 실행
3. 온라인/화면/GameMain.java 실행

위 순서대로 실행하여 서버를 먼저 켠 후 오프라인과 온라인을 할 수 있습니다.
