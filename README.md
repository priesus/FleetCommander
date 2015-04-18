# README #

Fleet Commander is a turn-based strategy game inspired by "Strategic Commander" for Palm OS from Zindaware.

## Where can I try it out? ##

* [Fleet Commander](http://priesus.de)

## Which technologies are used ##

* The game rules are coded in Java
* The AngularJS front-end talks to the server via the REST interface
* Currently there is no persistence layer

## How to get it up and running ##

### Prerequisites ###
* Java SDK 8
* Maven 3

### Working with the project ###
* Clone the repository
* **mvn verify** to run Unit & integration tests on the back-end
* **mvn jetty:run** to start the web-app at (http://localhost:80)
* **mvn package** will produce a deployable .war file

## Build Status on CodeShip ##
![build status](https://codeship.com/projects/0aa61220-c7ed-0132-d847-4a390261e3f5/status?branch=master)

