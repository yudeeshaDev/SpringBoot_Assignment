# Employee Management RESTful API

This is a simple RESTful API built using Java Spring Boot to manage a list of employees. It supports CRUD operations (Create, Read, Update, Delete) for managing employee records. The API follows REST conventions and includes basic exception handling, pagination, and search features.

## Features:
- Add an employee
- Retrieve an employee by ID
- Retrieve all employees with pagination
- Update an employee
- Delete an employee
- Search employees by name or department
- Secure API endpoints with API key authentication

## Database:
The API is connected to a MySQL database. Ensure that MySQL is running and accessible for storing employee data.

## Prerequisites:
- JDK 21 
- Maven
- MySQL Database

## Setup Instructions:

### 1. Clone the repository
git clone https://github.com/yudeeshaDev/SpringBoot_Assignment.git

clone to your folder

### 2.Configure the Database
In src/main/resources/application.properties, configure your database connection with local dabase username and password:

### 3.Build the Project

With Maven: mvn clean install

### 4.Run the Application

Start the application using Maven - mvn spring-boot:run

The application will start running on http://localhost:8080

### 5. Test the API

Secure API Access:

Before testing the endpoints in Postman, add the Authorization header with the following value:

    Key: Authorization
    Value: Bearer W8R9xZ5gYpT4xM7Uq2LbN0fVKwQ1vT6oD3GcN8Xz

### 6. API endpoints

POST - http://localhost:8080/api/employees
PUT - http://localhost:8080/api/employees/{id}
GET - http://localhost:8080/api/employees/{id}
DELETE - http://localhost:8080/api/employees/{id}
GET - http://localhost:8080/api/employees?page=0&size=10&searchText=
