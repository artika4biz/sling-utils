# Sling Utils

This is an OSGI module for Apache Sling with some utilities.
- **JsonServlet**: A simple servlet that provides results of a SQL-2 query in Json format, with pagination. This is useful when traversing JCR repository is not enough.


#### Build and Installation

The project is built quite simple:

    mvn clean install

To install the OSGi bundle use the project **autoInstallBundle**:

    mvn clean install -P autoInstallBundle