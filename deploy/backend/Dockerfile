FROM maven:3.9.16-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY backend /workspace/backend
RUN cd /workspace/backend && mvn -ntp -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 10001 nova && mkdir -p /app/data && chown -R nova:nova /app
COPY --from=build /workspace/backend/nova-boot/target/nova-boot.jar /app/nova-boot.jar
USER nova
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/nova-boot.jar","--spring.profiles.active=prod"]
