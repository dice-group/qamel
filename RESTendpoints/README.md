# RESTendpoints for TeBaQA, OfflineQA, knowledgeCard

## Run it via Maven
After cloning/downloading the repository, on the terminal run: 
``` 
cd RESTendpoints/ 
mvn clean install 
mvn spring-boot:run
```

An example request would look like: 

OfflineQA
``` curl -d "query=What is the birthdate of Barack Obama?&lang=en" -X POST http://localhost:8080/offline ```

TeBaQA
``` curl -d "query=What is the birthdate of Barack Obama?&lang=en" -X POST http://localhost:8080/tebaqa ```

KnowledgeCards
``` curl -d "url=http://dbpedia.org/resource/Barack_Obama" -X POST http://localhost:8080/knowledgeCard ```

Location Service
``` curl -d "query=What is the architectural style of this building?&lang=en&loc={\"Cologne Cathedral\"}" -X POST http://localhost:8080/location ```



## Run it via Docker
First install docker in your system. For ubuntu you may refer to below link. [https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-16-04]

For windows users [https://docs.docker.com/toolbox/toolbox_install_windows/#step-1-check-your-version]

Move to the parent directory of project and execute the below commands

```
mvn clean install
```
Now to build your image, type the below command.

```
sudo docker build -f Dockerfile -t qamelrest .
```
To run your image, type the below command.
```
sudo docker run -d --restart=always -p 8184:8080 -t qamelrest
```
