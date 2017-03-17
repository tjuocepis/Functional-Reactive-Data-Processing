## Reactive Functional Program For Streaming And Analyzing User And Book Data ##

### What is the project about? ###

The project involves streaming and analyzing large data files:

* BX-Users.csv : Contains data about users with user ID, location and age information
* BX-Book-Ratings.csv : Contains data about user bok ratings with user ID, book ISBN and rating information
* BX-Users-TEST.csv : Contains 5 data entries used for testing
* BX-Book-Ratings-TEST.csv : Contains 5 data entries used for testing

### How to use the program? ###

The program acts as a server containing two endpoints:

* http://localhost:9999/ratings-data
* http://localhost:9999/location-data

To interact with the server the user can do GET requests:

* For book ratings data users can get analyzed data for all users in the data set or for a specified user
* http://localhost:9999/ratings-data?user=2349
* http://localhost:9999/ratings-data?user=2363
* http://localhost:9999/ratings-data?user=276704
* http://localhost:9999/ratings-data?user=276866
* Analyzed data contains total number of ratings, average rating, number of data entries with a valid rating, and number
    of data entries with rating of 0 (Rating of 0 means that the user has not rated that book)

* For location data users can get analyzed data for all locations in the data set, for a specific city, for a specific
    state, or for a specific country
* http://localhost:9999/location-data?city=n/a&state=n/a&country=ukraine
* http://localhost:9999/location-data?city=kiev&state=n/a&country=ukraine
* http://localhost:9999/location-data?city=n/a&state=n/a&country=lithuania
* http://localhost:9999/location-data?city=vilnius&state=n/a&country=lithuania
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
* When displaying results for a specified location, if the user chooses to search just by country, when displaying the results the location name
    contains the city and state, however the results are for that specified country which includes ALL the cities. Same for when searching just
    by state with country. The analyzed results are correct for the user's provided location.  You can see this for example when looking at data 
    for country Ukraine vs city Kiev, Ukraine where the former contains more readers than the latter which is true because the latter is more
    specific.  I spent a lot of time trying to fix the bug, but ran out of time since I had to work on my final project for CS441.

### Documention ###

To access documentation, open index.html in the documentation folder
