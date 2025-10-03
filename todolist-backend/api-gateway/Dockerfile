# Sử dụng OpenJDK 21 làm base image
FROM openjdk:21

# Đặt thư mục làm việc trong container
WORKDIR /app

# Khai báo biến chứa đường dẫn file JAR
ARG JAR_FILE=target/*.jar

# Sao chép file JAR vào container
COPY ${JAR_FILE} app.jar

# Mở cổng 8080
EXPOSE 8080

# Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
