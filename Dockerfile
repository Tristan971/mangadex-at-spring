FROM fedora:33 as builder

RUN dnf makecache && dnf install -y \
  java-latest-openjdk \
  libsodium

WORKDIR /mangahome
ADD target/mangadex-at-spring.jar .

# Shenandoah should (probably, in most cases) be the best compromise
# compared to ZGC specifically, and also compared to G1/Parallel/CMS
ENV JAVA_TOOL_OPTIONS "-XX:+UseShenandoahGC"

ENTRYPOINT ["java", "-jar", "mangadex-at-spring.jar"]
