# ZIO-Http-MongoDB

This is a simple project in which I used ZIO HTTP to create a REST API with MongoDB as the database for storing and retrieving data. 
In this project, I utilized the following technologies:

- **zio-MongoDB**
- **ZIO-JSON**
- **ZIO-HTTP**

# Development guide

Before starting this project, you should have MongoDB installed locally, or you can start a MongoDB container on your local machine.

In my case, I used a Docker instance for MongoDB. To start it, you can simply run the following command:

```docker run --name mongodb -p 27017:27017 -d mongo```

After that to start the whole project just run :

```sbt run ```

To run the test:

```sbt run ```



