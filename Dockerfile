# 使用 Java 基礎映像
FROM openjdk:17-jdk

# 設定工作目錄
WORKDIR /app

# 複製 JAR 文件到容器中
COPY api.jar /app/api.jar

# 暴露 API 埠
EXPOSE 8080

# 啟動 JAR 文件
CMD ["java", "-jar", "your-api.jar"]
