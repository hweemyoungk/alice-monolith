FROM amazoncorretto:17-alpine
LABEL authors="hweemyoung.k@gmail.com"
LABEL version="0.1.0"

ENV JAVA_OPTS "-Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=default,dev,common,h2,monolith,oauth2_https"

WORKDIR application

COPY ../../../target/alice-monolith-0.1.0-SNAPSHOT.jar ./

#ENTRYPOINT ["java", "-jar", "alice-monolith-0.1.0-SNAPSHOT.jar"]
ENTRYPOINT exec java $JAVA_OPTS -jar alice-monolith-0.1.0-SNAPSHOT.jar