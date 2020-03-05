# sdn-simulator

This is a discrete-event simulation tool for Software-Defined Networks.
The simulator models transport and network layers of SDN.
The tool provides the user the option of generating the result of the study in form of tables and graphs in spreadsheet file.

## Technologies and Frameworks
- Java
- Maven
- Spring Boot
- Spring MVC
- Apache POI
- JUnit5, AssertJ, Mockito
## Features
### Implemented
- Performing Transport and Network layer simulations for SDN
- Providing the simulation results in spreadsheet format
- Providing the user with predefined topologies
### To be implemented
- Web-based user interface
- REST api
## Setup
This project uses Maven. If you have Maven installed on your system, navigate to the root directory of the project and run:
```
mvn clean package
```
If you do not have maven installed, you use:
```
mvnw clean package
```
After the building process, you can go the targer directory and run the jar file with:
```
java -jar [filename].jar
```
