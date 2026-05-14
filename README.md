# 🎓 LMS (Learning Management System)

> Spring Boot 3.x + MyBatis + Vue.js 기반 학습 관리 시스템

---

## 📋 목차

- [기술 스택](#-기술-스택)
- [프로젝트 구조](#-프로젝트-구조)
- [DB 설계](#-db-설계)
- [백엔드 구성](#-백엔드-구성)
- [프론트엔드 구성](#-프론트엔드-구성)
- [실행 방법](#-실행-방법)
- [API 명세](#-api-명세)
- [보안 구성](#-보안-구성)

---

## 🛠 기술 스택

| 구분 | 기술 |
|------|------|
| **Backend** | Spring Boot 3.x, Java 17, MyBatis |
| **Frontend** | Vue.js 3 (SPA, CDN 방식) |
| **Database** | MySQL / MariaDB |
| **인증** | JWT (Access Token) |
| **빌드** | Maven |
| **API 문서** | SpringDoc (Swagger UI) |

---

## 📁 프로젝트 구조

```
LMS-COMPLETE-FINAL/
│
├── database/
│   └── schema.sql                     # DB 스키마 + 초기 데이터
│
├── backend/
│   ├── pom.xml                        # Maven 의존성 설정
│   └── src/main/
│       ├── java/com/lms/
│       │   ├── LmsApplication.java    # 애플리케이션 진입점
│       │   │
│       │   ├── entity/                # JPA/MyBatis 매핑 엔티티 (9개)
│       │   │   ├── User.java
│       │   │   ├── Course.java
│       │   │   ├── Enrollment.java
│       │   │   └── ...
│       │   │
│       │   ├── dto/                   # 요청/응답 DTO (6개)
│       │   │   ├── LoginRequest.java
│       │   │   ├── LoginResponse.java
│       │   │   └── ...
│       │   │
│       │   ├── mapper/                # MyBatis Mapper 인터페이스 (5개)
│       │   │   ├── UserMapper.java
│       │   │   ├── CourseMapper.java
│       │   │   └── ...
│       │   │
│       │   ├── service/               # 비즈니스 로직 (2개)
│       │   │   ├── UserService.java
│       │   │   └── CourseService.java
│       │   │
│       │   ├── controller/            # REST API 컨트롤러 (4개)
│       │   │   ├── AuthController.java
│       │   │   ├── CourseController.java
│       │   │   └── ...
│       │   │
│       │   ├── security/              # JWT 보안 구성 (3개)
│       │   │   ├── JwtTokenProvider.java
│       │   │   ├── JwtAuthFilter.java
│       │   │   └── SecurityConfig.java
│       │   │
│       │   └── config/                # 설정 클래스 (2개)
│       │       ├── WebConfig.java
│       │       └── SwaggerConfig.java
│       │
│       └── resources/
│           ├── application.yml        # 서버/DB/JWT 설정
│           └── mapper/                # MyBatis XML 매퍼 (4개)
│               ├── UserMapper.xml
│               ├── CourseMapper.xml
│               └── ...
│
├── frontend/
│   └── index.html                     # Vue.js SPA (단일 파일)
│
└── README.md
```

---

## 🗄 DB 설계

```bash
# 스키마 및 초기 데이터 적용
mysql -u root -p lms_db < database/schema.sql
```

> `schema.sql`에는 테이블 생성 DDL과 초기 샘플 데이터(INSERT)가 포함되어 있습니다.

---

## ⚙ 백엔드 구성

### application.yml 주요 설정

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lms_db?useSSL=false&serverTimezone=Asia/Seoul
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  mybatis:
    mapper-locations: classpath:mapper/*.xml
    type-aliases-package: com.lms.entity

jwt:
  secret: your-secret-key
  expiration: 86400000   # 24시간 (ms)
```

### 레이어 구조

```
Controller  →  Service  →  Mapper(Interface)  →  XML  →  DB
     ↑               ↑
    DTO           Entity
```

### 주요 의존성 (pom.xml)

```xml
<!-- Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- MyBatis -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>

<!-- SpringDoc (Swagger) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

---

## 🖥 프론트엔드 구성

`frontend/index.html` 단일 파일로 구성된 Vue.js SPA입니다.

```
CDN 방식 (빌드 불필요)
  └── Vue 3
  └── Vue Router
  └── Axios (API 통신)
  └── Bootstrap 5 (UI)
```

**백엔드 API 연동 방식:**

```javascript
// Axios 기본 설정
axios.defaults.baseURL = 'http://localhost:8080/api';

// JWT 토큰 자동 첨부
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
```

---

## 🚀 실행 방법

### 1. 데이터베이스 준비

```bash
# MySQL 접속 후 DB 생성
CREATE DATABASE lms_db CHARACTER SET utf8mb4;

# 스키마 적용
mysql -u root -p lms_db < database/schema.sql
```

### 2. 백엔드 실행

```bash
cd backend

# Maven으로 빌드 및 실행
mvn spring-boot:run

# 또는 jar 빌드 후 실행
mvn clean package -DskipTests
java -jar target/lms-0.0.1-SNAPSHOT.jar
```

### 3. 프론트엔드 실행

```bash
# 별도 빌드 없이 브라우저에서 직접 열기
# (백엔드 실행 후)
open frontend/index.html

# 또는 간단한 로컬 서버 사용
npx serve frontend/
```

### 4. 접속 확인

| 항목 | URL |
|------|-----|
| 프론트엔드 | `http://localhost:5500` (또는 파일 직접 오픈) |
| 백엔드 API | `http://localhost:8080/api` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |

---

## 📡 API 명세

### 인증 (Auth)

| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|----------|
| POST | `/api/auth/login` | 로그인 (JWT 발급) | ❌ |
| POST | `/api/auth/register` | 회원가입 | ❌ |

### 강좌 (Course)

| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|----------|
| GET | `/api/courses` | 전체 강좌 목록 | ❌ |
| GET | `/api/courses/{id}` | 강좌 상세 조회 | ❌ |
| POST | `/api/courses` | 강좌 등록 | ✅ (ADMIN) |
| PUT | `/api/courses/{id}` | 강좌 수정 | ✅ (ADMIN) |
| DELETE | `/api/courses/{id}` | 강좌 삭제 | ✅ (ADMIN) |

### 수강 신청 (Enrollment)

| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|----------|
| POST | `/api/enrollments` | 수강 신청 | ✅ |
| GET | `/api/enrollments/my` | 내 수강 목록 | ✅ |
| DELETE | `/api/enrollments/{id}` | 수강 취소 | ✅ |

---

## 🔐 보안 구성

### JWT 흐름

```
1. 클라이언트 → POST /api/auth/login (id/pw)
2. 서버 → JWT Access Token 발급
3. 클라이언트 → 이후 요청 Header에 포함
   Authorization: Bearer {token}
4. JwtAuthFilter → 토큰 검증 → SecurityContext 등록
```

### 권한 구조

```
ROLE_ADMIN  →  전체 기능 접근
ROLE_USER   →  수강 신청, 강좌 조회
비로그인     →  강좌 목록/상세 조회만 허용
```

### CORS 설정

```java
// WebConfig.java
registry.addMapping("/api/**")
        .allowedOrigins("http://localhost:5500", "http://localhost:3000")
        .allowedMethods("GET", "POST", "PUT", "DELETE")
        .allowedHeaders("*")
        .allowCredentials(true);
```

---

## 📝 개발 참고사항

- MyBatis Mapper XML은 `resources/mapper/*.xml` 위치에 있으며 `application.yml`의 `mapper-locations`로 자동 스캔됩니다.
- JWT Secret Key는 운영 환경에서 반드시 환경변수로 분리하세요.
- Swagger UI는 개발 환경에서만 활성화를 권장합니다 (`springdoc.api-docs.enabled=false`).

---

## 📄 라이선스

본 프로젝트는 학습 목적으로 제작되었습니다.
