# Order portal server

## Overview

This portal server is a robust backend service built with Spring Boot and MongoDB. 
It provides a range of server-side features including real-time updates, user management, and order management.

## Features

### Real-Time updates

The server uses WebSocket technology to provide real-time updates to the client. 
This ensures that users are always informed about the status of their orders.

### User Management

The server includes a user management system that allows ranking of users. 
This can be used to prioritize orders, offer rewards, or implement any other ranking-based features.

### Order Management

The server provides endpoints for creating and managing orders. 
It handles requests from the client to create orders with ingredients of the user's choice and manage these orders effectively.

### Google Authentication

The server supports authentication using Google accounts. 
This provides a secure and convenient way for users to access the platform.

### MongoDB Integration

The server uses MongoDB as its database. 
This allows for efficient storage and retrieval of data, ensuring high performance even with large amounts of data.

## Installation and Setup

1. **Clone the repository**

    First, you need to clone the Order Portal Server repository to your local machine. You can do this using the following command:

    ```bash
    git clone <repository-url>
    ```

    Replace `<repository-url>` with the URL of your Order Portal Server repository.

2. **Install dependencies**

    Navigate into your new site’s directory and install the necessary dependencies.

    ```bash
    cd order-portal-server
    mvn install
    ```

3. **Change the client URL**

   Open the `application.properties` file in your Spring Boot project and look for the line that starts with `client.url`.
   Replace the existing URL with the URL of your client application.
   For example:

    ```properties
    client.url=http://my-app.com
    ```

    Save the `application.properties` file.
   Please make sure to restart your Spring Boot application so that the changes take effect.

5. **Run the server**

    Start the server using the following command:

    ```bash
    mvn spring-boot:run
    ```

    Your server is now running at `http://localhost:8080`!
