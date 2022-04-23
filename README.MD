# Sling Utils

This is an OSGI module for [Apache Sling](https://sling.apache.org/) and [Adobe AEM](https://business.adobe.com/products/experience-manager/adobe-experience-manager.html) that provides some utilities. All the servlets need an authenticated session otherwise the client receives a HTTP 403 Error.

- **JsonQuery**: A simple servlet that provides results of a SQL-2 query in Json format, with pagination. This is useful when traversing JCR repository is not enough. I use this servlet to receive a flat list of nodes (hundreds of thousands/some millions) to be managed by ML algorithms. With this servlet you can request something like [http://localhost:8080/v1/jcr-query?offset=0&limit=140&sql=select * from [oak:Unstructured] as n where isdescendantnode(n,'/data/cassazione') and id is not null] to obtain only 40 nodes (**limit=40**), starting from the first (**offset=0**) returned node: <img width="995" alt="image" src="https://user-images.githubusercontent.com/18739184/164780593-9f1d8324-776e-4e15-b129-2a13a208a947.png">

- **NodeCount**: A simple servlet that provides results count of a SQL-2 query. JCR does not provide a "count(*)" function. This servlet uses the Apache Jackrabbit OAK NodeIterator.getSize() method if the **Fast return size** is enabled (OSGI configuration with PID org.apache.jackrabbit.oak.query.QueryEngineSettingsService) or count each returned node otherwise. Remember that the NodeIterator.getSize() method counts exactly (just for the Jackrabbit OAK implementation) all the nodes because ACLs are not applied to the results as per the [the official documentation](https://jackrabbit.apache.org/oak/docs/query/query-engine.html). Conversely, this servlet counts only the nodes returned by the query, the only nodes that the ACLs allow to be consulted for the current authenticated session. <img width="1753" alt="image" src="https://user-images.githubusercontent.com/18739184/164893767-23551c86-5077-4347-975c-5d54476303ec.png"><img width="986" alt="image" src="https://user-images.githubusercontent.com/18739184/164893852-b2ea371c-83e9-48d1-be89-59b44ae93e46.png">

#### Build and Installation

The project is built quite simple:

    mvn clean install

To install the OSGi bundle use the **autoInstallBundle** profile:

    mvn clean install -P autoInstallBundle
