FROM openjdk:8-jdk-alpine

VOLUME /app/mms/jar

COPY jar/MMSServer.jar /app/mms/jar/MMSServer.jar
COPY lib/ /app/mms/lib/
COPY start_mns.sh /app/mms/start_mns.sh

WORKDIR /app/mms

EXPOSE 8588

RUN sed -i 's/\r$//' /app/mms/start_mns.sh
RUN ["chmod", "+x", "/app/mms/start_mns.sh"]
CMD ["/app/mms/start_mns.sh"]

