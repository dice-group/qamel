# GerbilQA-Benchmarking-Template
A template for QA systems to benchmark with GERBIL. It implements the web service interface as mentioned here, https://github.com/dice-group/gerbil/wiki/Question-Answering#web-service-interface.

## Running the template via Maven
After cloning/downloading the repository, on the terminal run: 
``` 
cd GerbilQA-Benchmarking-Template/ 
mvn clean install 
mvn spring-boot:run
```
The application will be started on http://localhost:8080. Test it by simply sending an HTTP POST request to http://localhost:8080/gerbil with the parameters: 
- `query`: A UTF-8 encoded String <br>
- `lang`: language of the question <br>

An example request would look like: <br>
``` curl -d "query=What is the capital of Germany?&lang=en" -X POST http://localhost:8080/gerbil ```

To test against GERBIL (http://gerbil-qa.aksw.org/gerbil/config), put the public IP address of your system (instead of localhost) in the webservice URI field. 

# How To Implement

## Requirements 
* Your System is written in Java or
* you have a JAVA Wrapper

## Implementation

First you need to clone the code

`git clone https://github.com/dice-group/GerbilQA-Benchmarking-Template `

Afterwards you need to add your System as a library to the maven pom.xml.

if this is done, you can proceed by creating your SystemWrapper. 
To do so you have to implement a method which receives a question as a String and the correspondent language (e.g. `en` for english or `fr` for french)
You have to provide an `AnswerContainer` Object. 

This `AnswerContainer` consists of tree objects. 
1. The SPARQL Query your system used to retrieve the answers.
2. The answer type (Boolean, Number, Resource (URI), Date) of the answers. (e.g. if your answer is true it is Boolean)
3. The answers as a Set of Strings. (If your answer is a boolean it is simply the string representation of it (e.g. 'true')

There is already an example class you can look at: [ExampleQASystem](https://github.com/dice-group/GerbilQA-Benchmarking-Template/blob/master/src/main/java/org/dice/qa/impl/ExampleQASystem.java)

Another Example is the following:

```java
package org.dice.qa.impl;
import java.util.HashSet;
import java.util.Set;

import org.dice.qa.AbstractQASystem;
import org.dice.qa.AnswerContainer;
import org.dice.qa.AnswerContainer.AnswerType;

public class MyQASystem extends AbstractQASystem{
       //Where as MySystem represents your actual System
       private MySystem system = new MySystem();

        @Override
	public AnswerContainer retrieveAnswers(String question, String lang) {
		//Create an empty container for your answers
		AnswerContainer answers = new AnswerContainer();
		
		//Create your answers as a Set
		Set<String> answerSet = new HashSet<String>();
		/**
		 * Here you have to actually ask your system for answers, the types and the sparql query
		 *
		 * BE AWARE: Literals have to be in full form. E.g.:
		 * "test"@en
		 * "test"
		 * "23.4"^^<http://www.w3.org/2001/XMLSchema#double>
		 * and so on
		 */
		answerSet.addAll(mySystem.retrieveAnswers(question, lang));
		
		//Set your answers
		answers.setAnswers(answerSet);

                //Get the type (RESOURCE, BOOLEAN, NUMBER, DATE)
                AnswerType type = AnswerType.valueOf(mySystem.lastAnswer().getType());
		answers.setType(type);

                //Set the sparql query your system used
		answers.setSparqlQuery(mySystem.lastAnswer().getSparqlQuery());

		return answers;
}
}
```

Then you have to change the following [line 40](https://github.com/dice-group/GerbilQA-Benchmarking-Template/blob/master/src/main/java/org/dice/webservice/GerbilQABenchmarking.java#L40) in the [GerbilQABenchmarking](https://github.com/dice-group/GerbilQA-Benchmarking-Template/blob/master/src/main/java/org/dice/webservice/GerbilQABenchmarking.java) Class

to 
```java
system = new MyQASystem();
```
