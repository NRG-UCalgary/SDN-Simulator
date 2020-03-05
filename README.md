# sdn-simulator

This is a discrete-event simulator for Software-Defined Networks.

## Technologies and Frameworks
- Java
- Maven
- Spring MVC
- Apache POI
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
