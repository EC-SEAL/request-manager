FROM openjdk:8-jdk-alpine
MAINTAINER Atos
VOLUME /tmp

ADD ./target/rm-ms-0.0.1.jar seal-rm.jar
RUN sh -c 'touch /seal-rm.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /seal-rm.jar" ]

EXPOSE 8063