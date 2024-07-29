# Melodify - a music streaming platform 🎵🎧🔈

## Introduction 📝

Do you know someone who doesn't like listening to music? We do not. Whether we listen to music in our free time or use it for concentration, we turn to music listening platforms like **Spotify** or **YouTube Music** every day. Although very convenient, their free usage licenses always come with a generous amount of ads that ruin our user experience. For these reasons, our team chose the **Melodify** theme: a client-server application for listening to music as a challenge that would allow us to create the platform of our dreams and give us experience in building client-server applications in **Java**.

<p align="center">
<img width="620px" src="https://github.com/sdvelev/Melodify/blob/main/resources/1.png" alt="home-page">
</p>

## Design ⚙️

The **Melodify** application is based on a client-server architecture. The server implements a **REST API** for working with platform resources through GET, PUT, PATCH, POST and DELETE requests. The application stores information about different types of units: 
- songs that have a genre and multiple artists;
- albums that include these songs;
- users who own multiple playlists;
- a queue that queues up the songs they want to listen to at the moment.
  
There are two types of roles in the application - **regular users** and **administrators**. Additional capabilities of system administrators are that they can add and perform operations on songs, albums, genres, artists, things that regular system clients cannot. To achieve this in terms of **authorization**, the so-called **JWT token (JSON Web Token)** that stores information about the user's unique identifier and whether they have administrative rights.
User information is stored in a **PostgreSQL** database. Media items (such as images and audio files) are stored as files on the server.
The user interacts with the server through a thin client consisting of pages implemented with **HTML**, **CSS** and **JavaScript** technologies, which achieves low resource consumption by avoiding unnecessarily complex frameworks such as React and Angular.
The client exchanges information with the server through requests and the **HTTP** protocol. For working with queries, the **Swagger** Interface can be used, which can be accessed at *localhost:8080/swagger.html*.

## Diagrams, representing architecture and functionalities of the system 📊

<p align="center">
<img width="620px" src="https://github.com/sdvelev/Melodify/blob/main/resources/2.png" alt="rest-architecture-diagram">
</p>

<p align="center">
<img width="800px" src="https://github.com/sdvelev/Melodify/blob/main/resources/3.png" alt="uml-class-diagram">
</p>

<p align="center">
<img width="620px" src="https://github.com/sdvelev/Melodify/blob/main/resources/4.png" alt="entity-relationship-diagram">
</p>

<p align="center">
<img width="800px" src="https://github.com/sdvelev/Melodify/blob/main/resources/5.png" alt="uml-sequence-diagram">
</p>

## Implementation 👨‍💻
### Server-Side
The server is based on the **Spring Application Server** framework - **Tomcat**. **Spring** is a Java framework that provides the infrastructure for building enterprise applications. It relies on the so-called "inversion of control", a design principle that states that it is not the application code that is responsible for the creation and management of objects, but the one responsible for this task is the so-called Spring container (IoC container). The container creates, configures, and binds the objects defined in the application context. It also manages the scope, life cycle, and destruction of these objects.

The business logic is based on:
- models
- data transfer objects
- classes for turning models into data transfer objects and vice versa (mappers)
- repositories
- services
- controllers
- authentication, authorization and validation configurations
  
The **main entities** in the application are defined as **models** representing **Entity Beans**. Classes are defined as entities through the **Java Persistence API** and its implementation - **Hibernate**. So represented, the entities/models cannot be sent over the network. For this purpose, a class is reduced to a Data Transfer Object, which stores only the information needed by the client. Conversely, information received from the client, for example in the request body, is also stored as a Data Transfer Object until it is transformed. A Model is reduced to a Data Transfer Object and vice versa via a Mapper - a class that is dynamically generated via the **MapStruct** library. 

**Repository** interfaces of type **Repository** that inherit from the **JpaRepository** interface allow **CRUD operations** on the database and system models without the explicit execution of SQL queries. 

**Services** represent the business logic of the application. For their development, we have followed the **Single Responsibility Principle**, i.e. a service to use only one repository interface. In cases where composite operations are required (for example, when creating users, playlists are also created for them), we have used the "Facade" design template, creating a new facade service that is composed of more than one other service. We run some of these more complex operations as transactions so that we can rely on their ACID properties in case it fails.

**Controllers** are special classes that define methods that handle HTTP requests. In our case, the HTTP methods we use are:
- GET (to access a resource)
- POST (to create a new resource)
- DELETE (to delete a resource)
- PUT (to replace a resource)
- PATCH (to modify a resource partially)

### Client-Side
The **graphical interface** relies on a **Single-Page Application strategy** that allows music to play without interruption even when the user navigates through different views for albums, artists, playlists and search. The main page contains a container that renders the content of other views using the **JavaScript Fetch API** and **AJAX**.
Each view is a separate **HTML page** that dynamically loads data based on the resource identifier passed as a parameter.

## Testing 📋

A major focus of testing was the controller classes that bind REST API resources to the application's business logic and ensure that only authorized users with the correct requests can use the services. In order to unit test the classes in general in specific scenarios, the **JUnit** and **Mockito** testing libraries were used, which allow mocking of the logic outside the target classes. The result is 100% test pass, 100% class coverage, 93% test coverage of methods, and 82% test coverage of **lines of code in controllers**. Classes defining models, data transfer objects, repositories, and mappers are not testable. In addition, the team also performed **REST API testing** using the **Postman** tool as well. Tests were also carried out on the graphical interface, including scenarios such as trying to play previous/next song when queue is empty.

## Deployment 🔑

Using the **spring-boot-maven-plugin** and the **mvn package** command, a **jar file** is created that contains the necessary **MANIFEST.MF** file specifying the application's base class. The resulting jar file contains an embedded Tomcat server and can be used to run the application on different machines as described below. The application, distributed as a **Melodify.jar** file, can be hosted on **Azure Web Services**, **Google Cloud**, or another cloud service platform. An application distributed as a **Melodify.jar** file can be executed in a virtualization container, for example through **Docker** and **Kubernetes**. Of course, the application can be run on a local machine designated as a server using the command **java -classpath . -jar Melodify.jar [--spring.config.location=file:/…/application.properties]**. An **application.properties** file parameter can also be passed to the command, which changes the default database connection keys.

## Graphical Interface 📄

<p align="center">
<img width="620px" src="https://github.com/sdvelev/Melodify/blob/main/resources/6.png" alt="registration-page">
</p>

<p align="center">
<img width="620px" src="https://github.com/sdvelev/Melodify/blob/main/resources/7.png" alt="login-page">
</p>

<p align="center">
<img width="620px" src="https://github.com/sdvelev/Melodify/blob/main/resources/8.png" alt="home-page">
</p>

<p align="center">
<img width="620px" src="https://github.com/sdvelev/Melodify/blob/main/resources/9.png" alt="search-page">
</p>

<p align="center">
<img width="620px" src="https://github.com/sdvelev/Melodify/blob/main/resources/10.png" alt="album-page">
</p>

<p align="center">
<img width="620px" src="https://github.com/sdvelev/Melodify/blob/main/resources/11.png" alt="playlist-page">
</p>



  

