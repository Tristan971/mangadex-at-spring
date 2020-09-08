FROM openjdk:14

ADD target/mangadex-at-spring.jar /mdas/mangadex-at-spring.jar

WORKDIR /mdas

CMD java -jar mangadex-at-spring.jar
