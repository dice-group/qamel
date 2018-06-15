# RESTendpoints for TeBaQA, OfflineQA, Genesis

## Run it via Maven
After cloning/downloading the repository, on the terminal run: 
``` 
cd RESTendpoints/ 
mvn clean install 
mvn spring-boot:run

An example request would look like: 

OfflineQA
``` curl -d "query=What is the birthdate of Barack Obama?&lang=en" -X POST http://localhost:8080/gerbil ```

TeBaQA
``` curl -d "query=What is the birthdate of Barack Obama?&lang=en" -X POST http://localhost:8080/tebaqa ```

