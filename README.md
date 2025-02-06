# 프로젝트 소개

### Hobby & Hub(H&H)

<img src="https://github.com/user-attachments/assets/e144bf01-3a85-4b0e-839c-5099a819d7ec" width="300" height="300"/>


#### - 취미를 공유하고자 하는 사람들이 모여서 다양한 커뮤니티 활동을 할 수 있게 도와주는 플랫폼
#### - 사회가 발전하면서 개개인의 성향이 강해지고 취미의 분야와 범위도 넓어지면서 같은 취미를 공유하는 사람들의 수가 줄어들고 있습니다. 저희는 이 점의 문제점을 해소하기 위해 하나의 큰 주제를 기반으로 취미를 공유하는 프로젝트를 시작했습니다.

#### 

## 팀 소개

- #### **5(지게 개발하)조** <br>

| name | role |                                            구현 기능                                             |     Github      |
  |:----:|:----:|:--------------------------------------------------------------------------------------------:|:---------------:|
| 천경환  |  리더  |                    그룹(S3, Redis), 관심그룹(Redis, 동시성 제어), 모임, 모임 참여멤버, 부하테스트                    |  <a href="https://github.com/GyeonghwanCheon"><img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=Github&logoColor=white"></a>    |
| 박용재  | 부리더  | 유저,실시간 채팅, Security(인증/인가) 예외처리(Security Handler), JWT 보안(Security, Redis), global Exception |    <a href="https://github.com/SearchColor/"><img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=Github&logoColor=white"></a>    |
| 박재혁  |  팀원  | 관리자 모드(Redis), AWS S3, AWS EC2, AWS ElastiCache for Redis, Docker CI/CD, 서버 배포 및 관리, Swagger | <a href="https://github.com/jaeh3197"><img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=Github&logoColor=white"></a>  |
| 한승완  |  팀원  |                     멤버, 게시글 (캐싱, Redis, 동시성 제어), 댓글, 답글, 게시글 좋아요(동시성 제어)                     | <a href="https://github.com/Dawnfeeling"><img src="https://img.shields.io/badge/Github-181717?style=for-the-badge&logo=Github&logoColor=white"></a>  |
  <br>

## 개발 기간

> 2025.01.02 - 2025.02.07

## 🛠️기술 스택

### 프로그래밍 언어

<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">

### 개발 환경

- **IDE** : IntelliJ
- **JDK** : openjdk version '17.0.2'
- **Framework** : springframework.boot version '3.4.1', Spring Data JPA, Spring Security
- **Library** : Lombok, Bcrypt, Junit, WebSocket, Redis, S3, JWT, Swagger, Redisson
- **Build Tool** : Gradle, Docker
- **Database** : MySQL
- **Messaging** : WebSocket
- **Infra** : AWS EC2, Amazon S3, AWS RDS (MySQL) AWS ElastiCache for Redis, Docker
- **Tool** : Miro, ERD Cloud, Github & git, Postman

## 구현 기능

#### **✨ 유저**

* 회원가입
* 회원탈퇴
* 비밀번호 변경
* 유저 단건 조회
* 로그인

#### **✨ 멤버 및 역할관리**
* 유저 권한 (일반 유저, 관리자)
* 멤버 역할(그룹 관리자, 멤버)

#### **✨ 그룹**
* 그룹 생성 (배경 이미지)
* 그룹 전체 조회(관심 수 기준 랭킹 조회)
* 그룹 단건 조회
* 그룹 수정
* 그룹 선택
* 그룹 삭제

#### **✨ 관심그룹**
* 관심 그룹 찜하기(토글로 찜하기/취소하기 가능)
* 관심 그룹 목록 조회(로그인한 유저가 찜한 관심 그룹만 조회)

#### **✨ 모임**
* 모임 생성
* 모임 수정 (모임 생성자만 수정 가능)
* 모임 조회 (그룹 내 활동했던 모임 모두 조회)
* 모임 삭제 (모임 생성자만 삭제 가능)

#### **✨ 모임참여 멤버**
* 모임 참여
* 모임 참여 멤버 조회 (해당 모임 참여 멤버 조회)
* 모임 참여 변경 (참여/불참 토글로 가능)

#### **✨ 게시글**
* 게시글 생성(이미지 삽입 가능)
* 게시글 수정
* 게시글 삭제
* 게시글 단건 조회(캐싱으로 조회 수 관리)
* 게시글 전체 조회
* 게시글 좋아요 수

#### **✨ 댓글**
* 댓글 생성
* 댓글 수정
* 댓글 삭제

#### **✨ 답글**
* 답글 생성
* 답글 수정
* 답글 삭제

#### **✨ 멤버**
* 그룹 가입 신청
* 그룹 가입 승인
* 멤버 권한 변경(그룹 관리자만 가능)
* 멤버 탈퇴
* 멤버 전체 조회

#### **✨ 관리자 모드**
* 카테고리 생성
* 관리자 생성
* 유저 벤
* 그룹 벤
* 그룹 통계 조회(각 그룹 별 모임 수, 멤버 수, 게시글 수, 관심 수, 조회 기간 설정 가능)

#### **✨ 배포 & CI/CD**
* Docker
* Docker-compose
* GitHub Action
* AWS

#### **✨ 동시성 제어**
* 관심 수
* 조회 수
* 좋아요 수

#### **✨ 스프링 시큐리티**
* 로그인 인증
* 유저 권한
* Access 토큰 재발급

#### **✨ 실시간 채팅**
* WebSocket 통신

## 와이어 프레임

<details>
<summary>와이어 프레임</summary>

- [Miro link](https://miro.com/app/board/uXjVLyScV38=/?share_link_id=987064474658)


</details>

##  ERD

<details>
<summary>ERD</summary>

- [ERD Cloud link](https://www.erdcloud.com/d/sMFf2f4jDXvEoRPzx)

![image](https://github.com/user-attachments/assets/6c835434-40de-48c7-9260-3e940da3720f)
</details>

## 📑 API 명세서

<details>
<summary>API 명세서</summary>

- [team notion link](https://www.notion.so/teamsparta/5-70a9967748554ac485e24504c147eed6)
- 팀 노션 참고
</details>

## 🌟 실행 화면

> postman API Test & MySQL Workbench

(API 명세서 참고 후 이용)