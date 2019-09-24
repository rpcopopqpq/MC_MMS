FROM openjdk:8-jdk-alpine

COPY java/MNSDummyForGeocastingTest.java /app/mms/java/MNSDummyForGeocastingTest.java
COPY lib/ /app/mms/lib/
COPY start_mns.sh /app/mms/start_mns.sh

WORKDIR /app/mms

ENV mnsPort 8588
EXPOSE 8588 8589 8590

RUN sed -i 's/\r$//' /app/mms/start_mns.sh
RUN ["chmod", "+x", "/app/mms/start_mns.sh"]
CMD /app/mms/start_mns.sh $mnsPort

