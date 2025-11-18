FROM maven:3.9.6-eclipse-temurin-21 AS build

# 让容器里的 Maven 也走 Clash 代理
ENV HTTP_PROXY=http://127.0.1:7897 \
    HTTPS_PROXY=http://127.0.1:7897 \
    http_proxy=http://127.0.1:7897 \
    https_proxy=http://127.0.1:7897 \
    NO_PROXY=localhost,127.0.0.1 \
    no_proxy=localhost,127.0.0.1

WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY settings.xml /root/.m2/settings.xml

# 复用 Maven 依赖缓存，避免每次下载
RUN --mount=type=cache,target=/root/.m2 mvn -DskipTests package -X

# 运行时阶段改用已存在的 maven 基础镜像，避免额外拉取 jre 镜像
FROM maven:3.9.6-eclipse-temurin-21
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
