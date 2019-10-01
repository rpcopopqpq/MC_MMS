FROM openjdk:8-jdk-alpine

VOLUME /app/mms/logs
EXPOSE 8088

COPY start_mms.sh /app/mms/start_mms.sh
COPY MC_MMS-*.jar /app/mms/MMSServer.jar
COPY lib/ /app/mms/lib/
COPY MMS-configuration/ /app/mms/MMS-configuration/

WORKDIR /app/mms

RUN sed -i 's/\r$//' /app/mms/MMS-configuration/*.*
RUN sed -i 's/\r$//' /app/mms/start_mms.sh
RUN ["chmod", "+x", "/app/mms/start_mms.sh"]
ENTRYPOINT ["/app/mms/start_mms.sh"]

