# 파일위치
C:\springlms\frontend>
C:\springlms\frontend>npm run dev --host

# 브라우저에서 열기:
LMS-COMPLETE-FINAL/frontend/index.html
```

---

## 🎯 주요 기능

✨ **완벽하게 구현된 기능들:**

### 🔐 인증 시스템
- JWT 기반 Access/Refresh Token
- BCrypt 비밀번호 암호화
- 로그인/회원가입/토큰 갱신

### 👥 사용자 관리
- 역할 기반 접근 제어 (ADMIN/USER)
- 사용자 정보 조회
- 관리자: 전체 사용자 목록

### 📚 시험 관리
- 시험 생성/조회/수정/삭제
- 카테고리별 분류
- 공개/비공개 설정
- 시간 제한, 합격 기준 설정

### ❓ 문제 은행
- 객관식/참거짓/단답형 문제
- 난이도 설정 (EASY/MEDIUM/HARD)
- 정답 및 해설 관리

### 📝 시험 응시
- 시험 응시 기록
- 자동 채점
- 결과 확인

### 📖 API 문서
- Swagger UI 통합
- 모든 API 테스트 가능

---

## 🔑 테스트 계정

### 관리자
- **Username**: `admin`
- **Password**: `admin123`
- **Email**: `admin@lms.com`

### 일반 사용자
- **Username**: `user1`
- **Password**: `admin123`
- **Email**: `user1@lms.com`

---

## 🌐 접속 URL

### 백엔드
- **API**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html

### 프론트엔드
- **웹 앱**: 브라우저에서 `frontend/index.html` 열기

---

## 📊 기술 스택

- ☕ **Java 17**
- 🍃 **Spring Boot 3.4.5**
- 🔒 **Spring Security + JWT 0.12.6**
- 🗃️ **MyBatis 3.0.4**
- 🐬 **MySQL 8.0**
- 📝 **SpringDoc OpenAPI 2.8.3**
- 💚 **Vue.js 3**

---

## 💡 특징

✅ **완벽한 프로덕션 레벨 코드**
- 모든 레이어 완벽 구현 (Controller → Service → Mapper → XML)
- 예외 처리 및 검증
- 트랜잭션 관리

✅ **즉시 실행 가능**
- 설정 파일 완비
- 초기 데이터 포함
- 테스트 계정 제공

✅ **확장 가능한 구조**
- 클린 아키텍처
- RESTful API 설계
- 마이크로서비스 전환 가능

---

## 🎓 프로젝트 구조
```
LMS-COMPLETE-FINAL/
├── database/
│   └── schema.sql                 # DB 스키마 + 초기 데이터
│
├── backend/
│   ├── pom.xml                    # Maven 설정
│   └── src/main/
│       ├── java/com/lms/
│       │   ├── LmsApplication.java
│       │   ├── entity/            # 9개 Entity
│       │   ├── dto/               # 6개 DTO
│       │   ├── mapper/            # 5개 Mapper 인터페이스
│       │   ├── service/           # 2개 Service
│       │   ├── controller/        # 4개 Controller
│       │   ├── security/          # 3개 Security
│       │   └── config/            # 2개 Config
│       └── resources/
│           ├── application.yml    # 설정 파일
│           └── mapper/            # 4개 XML 매퍼
│
├── frontend/
│   └── index.html                 # Vue.js SPA
│
└── README.md                      # 완벽한 가이드
