# Sling Utils

This is an OSGI module for [Apache Sling](https://sling.apache.org/) and [Adobe AEM](https://business.adobe.com/products/experience-manager/adobe-experience-manager.html) that provides some utilities. All the servlets need an authenticated session otherwise the client receives a HTTP 403 Error.

### JsonQuery
A simple servlet that provides node path of SQL-2 query results, in Json format, **with pagination**. This is useful when traversing JCR repository is not enough. I use this servlet to receive a flat list of nodes (hundreds of thousands/some millions) to be managed by ML algorithms. With this servlet you can request something like `http://localhost:8080/v1/jcr-query?offset=0&limit=140&sql=select * from [oak:Unstructured] as n where isdescendantnode(n,'/data/cassazione') and id is not null` to obtain only 40 nodes (**limit=40**), starting from the first (**offset=0**) returned node or from the n-th returned node (**offset=n**):

<img width="995" alt="image" src="https://user-images.githubusercontent.com/18739184/164780593-9f1d8324-776e-4e15-b129-2a13a208a947.png">

### NodeCount
A simple servlet that provides results count of a SQL-2 query. JCR does not provide a "count(*)" function. This servlet is smart enough to use the fastest strategy:
- Using the Apache Jackrabbit OAK NodeIterator.getSize() method; in Apache Jackrabbit OAK implementation, this method provides a valid result just if the **Fast return size** is enabled (OSGI configuration with PID org.apache.jackrabbit.oak.query.QueryEngineSettingsService)
- Counting each returned node otherwise (very slow but it works when needed!).

Remember that the NodeIterator.getSize() method counts exactly (just for the Jackrabbit OAK implementation) all the nodes because ACLs are not applied to the results as per the [the official documentation](https://jackrabbit.apache.org/oak/docs/query/query-engine.html).

#### How to enable the Fast return size option
<img width="1753" alt="image" src="https://user-images.githubusercontent.com/18739184/164893767-23551c86-5077-4347-975c-5d54476303ec.png">

#### Example of a count request when the Fast return size option is enabled (Execution time, 25ms)
<img width="981" alt="image" src="https://user-images.githubusercontent.com/18739184/164901703-2492b4bd-9639-46b5-97fc-97c03d129a25.png">

#### Example of a count request when the Fast return size option is disabled (Execution time, more than 5 seconds)
<img width="984" alt="image" src="https://user-images.githubusercontent.com/18739184/164900990-d1a94ec8-018b-4761-a375-596b1638c943.png">

## When these servlets are useful
Whit just these two servlets I can now analyze millions of documents stored in Apache Jackrabbit OAK, executing less than 20 lines of Pyhton code, like these ones: 
<img width="1518" alt="image" src="https://user-images.githubusercontent.com/18739184/164910461-f0ecad6f-6aea-4c01-9bf5-1d31b4d5f3b7.png">

## Build and Installation

The project is built quite simple:

    mvn clean install

To install the OSGi bundle use the **autoInstallBundle** profile:

    mvn clean install -P autoInstallBundle
