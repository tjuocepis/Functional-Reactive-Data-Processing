## Reactive Functional Program For Streaming And Analyzing User And Book Data ##

### What is the project about? ###

The project involves streaming and analyzing large data files:

* BX-Users.csv : Contains data about users with user ID, location and age information
* BX-Book-Ratings.csv : Contains data about user bok ratings with user ID, book ISBN and rating information
* BX-Users-TEST.csv : Contains 5 data entries used for testing
* BX-Book-Ratings-TEST.csv : Contains 5 data entries used for testing

### Design Rationale ###

I chose to use GraphDSL to create my streaming pipeline, because the syntax is really nice and concise (especially ~> operators for connecting
the pipeline together) and you can do branching of data that comes out of a Flow in order to apply different Flows on the same data.  For example
in analyzeUserRatingsData once I convert the strings coming out of the source to UserBookRating case class instances, I branch those out, because 
I apply averageRatingFlow to one branch to calculate the averages of all the users and I apply filterByUserId to the second branch to filter out 
specific users.  I then branch out the filtered users, because I apply a Flow to one branch to calculate averages just for those users and to the
second branch I apply a Flow that groups the rated books into a Sequence.  (The reason for Sequence is because Flow.grouped(int) returns a 
Sequence and is a nice data structure for book ratings since you can sort it and book ratings will go in sequence from best to worst). The same 
idea about GraphDSL applies to analyzeLocationData stream.

I used case classes to represent instances that hold data of users, book ratings, books and location (location is nested inside user case class 
which my data pipeline takes care of).  Other case classes are used for Actor messaging.

I used one Actor which responds to client's inputs that come from hitting the endpoint.  ResponseActor starts a specific stream based on those
inputs.  If client wants to look at analyzed book ratings, ResponseActor gets a message for that and strats that stream pipeline.  If client
wants to look at analyzed user data, ResponseActor gets a message for that and starts that stream pipeline.  When the pipeline is running and
data goes through all the flows, the analyzed data Sinks to a specifc ActorSubscriber for that specifc type of data.  The ActorSubscriber reads
the results and makes a String response that gets sent to the ResponseActor.  When the response arrives to ResponseActor, the actor will forward
that back to the temporary Actor that gets created in Main from calling the ask() function in order to complete the route and send the answer
back to the client.

For the ResponseActor I chose to use the Singleton pattern to make sure that the two different ActorSubscribers send their responses to the same
actor and no new Actor is created. I also chose to use the Singleton pattern for the ActorSystem and Materializer to make sure that the same
Actor and Materializer are used throughout my program.

### How to user the program? ###

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
    by state with country.  In summary, the location name that is displayed contains the last data entry analyzed instead of the user's provided
    location, however the analyzed results are correct for the user's provided location.

### Documention ###

To access documentation, open index.html in the documentation folder