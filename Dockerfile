# 使用 Java 基礎映像
FROM openjdk:17-jdk

# 設定工作目錄
WORKDIR /app

# 複製 JAR 文件到容器中
COPY /out/artifacts/tek_chin_jar/tek_chin.jar  /app/api.jar

# 暴露 API 埠
EXPOSE 8080

# 啟動 JAR 文件
CMD ["java", "-Dspring.profiles.active=production", "-jar", "api.jar"]
