# 第一步：在容器里用 Maven 打包
FROM maven:3.9.6-eclipse-temurin-21 AS build

# 让容器里的 Maven 走你主机上的 Clash 代理
ENV HTTP_PROXY=http://127.0.0.1:7897 \
    HTTPS_PROXY=http://127.0.0.1:7897 \
    http_proxy=http://127.0.0.1:7897 \
    https_proxy=http://127.0.0.1:7897 \
    NO_PROXY=localhost,127.0.0.1 \
    no_proxy=localhost,127.0.0.1

WORKDIR /app
COPY pom.xml .
COPY src ./src
# 这里的 settings.xml 就是你 backend 目录里的那个
COPY settings.xml /root/.m2/settings.xml

RUN mvn -DskipTests package -X

# 第二步：用 JRE 镜像运行打好的 jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
