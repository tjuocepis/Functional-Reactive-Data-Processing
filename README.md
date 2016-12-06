<<<<<<< HEAD
## Reactive Functional Program For Streaming And Analyzing User And Book Data ##

### What is the project about? ###
=======
# Reactive Functional Program For Streaming And Analyzing User And Book Data #

### Have to fix bugs when hitting user-data endpoint. If server response error then all concecutive user-data responses give two all user
### responses instead of one for all and one for specific user. Must implement error handling when a user only contains ratings of 0
### Something with the counter is messed up.

### Must also write code to send the rated books sequence to the actors and back to the endpoint
### Would also like to implement combining book ratings data with book information data such as title and etc.
>>>>>>> e52d9dd5fa5521adfd51564ad52f94e9e504aef7

The project involves streaming and analyzing large data files:

* BX-Users.csv : Contains data about users with user ID, location and age information
* BX-Book-Ratings.csv : Contains data about user bok ratings with user ID, book ISBN and rating information
* BX-Users-TEST.csv : Contains 5 data entries used for testing
* BX-Book-Ratings-TEST.csv : Contains 5 data entries used for testing

The program acts as a server containing two endpoints:

* http://localhost:9999/ratings-data
* http://localhost:9999/location-data

To interact with the server the user can do GET requests:

* For book ratings data users can get analyzed data for all users in the data set or for a specified user
* http://localhost:9999/ratings-data?user=2349
* http://localhost:9999/ratings-data?user=2377
* http://localhost:9999/ratings-data?user=276704
* Analyzed data contains total number of ratings, average rating, number of data entries with a valid rating, and number
    of data entries with rating of 0 (Rating of 0 means that the user has not rated that book)

* For location data users can get analyzed data for all locations in the data set, for a specific city, for a specific
    state, or for a specific country
* http://localhost:9999/location-data?city=n/a&state=n/a&country=ukraine
* http://localhost:9999/location-data?city=kiev&state=n/a&country=ukraine
* http://localhost:9999/location-data?city=n/a&state=illinois&country=usa
* http://localhost:9999/location-data?city=n/a&state=california&country=usa
* http://localhost:9999/location-data?city=chicago&state=illinois&country=usa
* http://localhost:9999/location-data?city=san+francisco&state=california&country=usa
* Analyzed data contains total number of readers, average reader's age, number of users with their age provided and
    number of users that did not provide their age

The project is written in Scala and the reactive asynchronous streaming is done using Akka. The project also includes
multiple tests to test each component of the stream separately to ensure it's correctness

### How do I get set up? ###
* To get the program running, clone the repository and from the terminal call sbt run in the root directory of
    the project
* To run the tests call sbt test

### Limitations ###

* If user makes a mistake when providing the user ID or location the server times out
* If no data exists for the provided user ID or location the server times out
* Analyzed data is sent straight back to the client without storage or cache

### Documention ###

To access documentation, open index.html in the documentation folder
