FROM openjdk:8-jdk-alpine

ENV serverNo 1
EXPOSE 8588

COPY java/MNSDummyForGeocastingTest.java /app/mms/java/MNSDummyForGeocastingTest.java
COPY lib/ /app/mms/lib/
COPY start_mns.sh /app/mms/start_mns.sh

WORKDIR /app/mms

RUN sed -i 's/\r$//' /app/mms/start_mns.sh
RUN ["chmod", "+x", "/app/mms/start_mns.sh"]
ENTRYPOINT /app/mms/start_mns.sh $serverNo

