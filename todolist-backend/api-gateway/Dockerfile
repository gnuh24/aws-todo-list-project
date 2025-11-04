# ===============================
# 🏗️ Stage 1: Build application
# ===============================
FROM maven:3.9.11-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy file pom.xml để cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy toàn bộ source code và build
COPY src ./src
RUN mvn clean package -DskipTests

# ===============================
# 🚀 Stage 2: Run application
# ===============================
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copy jar từ giai đoạn build sang
COPY --from=builder /app/target/*.jar app.jar

# Cấu hình timezone và JVM options
ENV TZ=Asia/Ho_Chi_Minh
ENV JAVA_OPTS=""

# Expose port (mặc định 8080, chỉnh nếu service khác)
EXPOSE 8080

# Chạy ứng dụng Spring Boot
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
