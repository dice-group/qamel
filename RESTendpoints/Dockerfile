FROM maven

WORKDIR /

# Config and data
COPY . .

EXPOSE 8080

CMD ["mvn", "clean", "spring-boot:run"]
