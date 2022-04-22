# Sling Utils

This is an OSGI module for Apache Sling with some utilities.
- **JsonQuery**: A simple servlet that provides results of a SQL-2 query in Json format, with pagination. This is useful when traversing JCR repository is not enough. I use this servlet to receive a flat list of nodes to be managed by ML algorithms. With this servlet you can request something like [http://localhost:8080/v1/jcr-query?offset=50000&limit=10&sql=select * from [oak:Unstructured] as n where isdescendantnode(n,'/data/cassazione') and id is not null] and receive a response like this one: <img width="995" alt="image" src="https://user-images.githubusercontent.com/18739184/164780593-9f1d8324-776e-4e15-b129-2a13a208a947.png">

- **NodeCount**: A simple servlet that provides results count of a SQL-2 query. JCR does not provide a "count(*)" function. Servlet uses the Apache Jackrabbit OAK getNodes.getSize() if the **Fast return count** (org.apache.jackrabbit.oak.query.QueryEngineSettingsService) is enabled or count each returned node otherwise. <img width="886" alt="image" src="https://user-images.githubusercontent.com/18739184/164772224-ce8355d3-3f11-4667-8ce3-6beced9c86dc.png"> - .  [http://localhost:8080/v1/count?sql=select * from [oak:Unstructured] as n where isdescendantnode(n,'/data/cassazione') and id is not null]: <img width="996" alt="image" src="https://user-images.githubusercontent.com/18739184/164778418-42cf870e-ca03-4716-9eb7-f4d224542d5b.png">

All the servlets need an authenticated session otherwise the client receives a HTTP 403 Error.


#### Build and Installation

The project is built quite simple:

    mvn clean install

To install the OSGi bundle use the **autoInstallBundle** profile:

    mvn clean install -P autoInstallBundle
