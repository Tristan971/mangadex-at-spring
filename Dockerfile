FROM openjdk:14 as builder

WORKDIR /mdas
COPY target/mangadex-at-spring.jar .
RUN java -Djarmode=layertools -jar mangadex-at-spring.jar extract
RUN rm mangadex-at-spring.jar

FROM openjdk:14

WORKDIR /mdas
COPY --from=builder /mdas/application/ ./
COPY --from=builder /mdas/dependencies/ ./
COPY --from=builder /mdas/snapshot-dependencies/ ./
COPY --from=builder /mdas/spring-boot-loader/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
