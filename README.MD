# Sling Utils

This is an OSGI module for Apache Sling with some utilities.
- **JsonQuery**: A simple servlet that provides results of a SQL-2 query in Json format, with pagination. This is useful when traversing JCR repository is not enough. I use this servlet to receive a flat list of nodes to be managed by ML algorithms. With this servlet you can request something like [http://localhost:8080/v1/jcr-query?offset=50000&limit=10&sql=select * from [oak:Unstructured] as n where isdescendantnode(n,'/data/cassazione') and id is not null] and receive a response like this one: <img width="1199" alt="image" src="https://user-images.githubusercontent.com/18739184/163135819-0166d76c-5d78-43cc-9aea-1c1204a6c937.png">
- **NodeCount**: A simple servlet that provides results count of a SQL-2 query. JCR does not provide a "count(*)" selector and the getNodes.getSize() method returns -1.  [http://localhost:8080/v1/count?sql=select * from [oak:Unstructured] as n where isdescendantnode(n,'/data/cassazione') and id is not null]: <img width="1210" alt="image" src="https://user-images.githubusercontent.com/18739184/163595003-fb095726-d219-4490-95ae-ab91a41c0643.png">

All the servlets need an authenticated session otherwise the client receives a HTTP 403 Error.


#### Build and Installation

The project is built quite simple:

    mvn clean install

To install the OSGi bundle use the **autoInstallBundle** profile:

    mvn clean install -P autoInstallBundle
