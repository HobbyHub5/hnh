# Build 스테이지
FROM gradle:8.10.2-jdk17 AS builder

# 작업 디렉토리 설정
WORKDIR /apps

# 의존성 캐싱
COPY build.gradle settings.gradle ./
COPY gradle gradle/
RUN gradle dependencies --no-daemon

# 소스 코드 복사
COPY src src/

# 테스트 스킵하고 빌드
RUN gradle clean build -x test --no-daemon --parallel

# 실행 스테이지
FROM eclipse-temurin:17-jre-jammy

LABEL type="application"

WORKDIR /apps

COPY --from=builder /apps/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

USER nobody

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]