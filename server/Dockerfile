FROM openjdk:11

ENV MEMORY_OPTS="-XX:-HeapDumpOnOutOfMemoryError \
                 -XX:MaxMetaspaceSize=256m \
                 -XX:MetaspaceSize=256m" \
    JAVA_OPTS="-Dserver.port=8099"
    
EXPOSE 8080

#USER "ADMIN"

COPY target /target

WORKDIR /target

ENTRYPOINT exec java ${MEMORY_OPTS} ${JAVA_OPTS} \
-jar server-0.0.1-SNAPSHOT.jar