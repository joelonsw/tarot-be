FROM openjdk:11-jdk-slim

WORKDIR /app

COPY . /app

RUN apt-get update && \
    apt-get install -y curl unzip && \
    curl -L -o sbt.zip https://github.com/sbt/sbt/releases/download/v1.10.5/sbt-1.10.5.zip && \
    unzip sbt.zip -d /opt && \
    ln -s /opt/sbt/bin/sbt /usr/local/bin/sbt

RUN sbt clean compile stage

CMD ["target/universal/stage/bin/tarot-be"]
