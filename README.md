# Budgeting Service System
Budgeting Service System is a project provides features for tracking and managing budget, money, users, and trading transactions.


<div style="text-align: center;">
  <img width="491" alt="Screenshot 2024-08-22 at 21 34 38" src="https://github.com/user-attachments/assets/cbd54394-b9be-4ca9-a12c-e774efd4e52e">
</div>

# Technologies
- Java 17
- Spring boot 3.3.0
- Swagger
- PostgreSQL
- Thymeleaf
- Jquery

# Current Feature
- Init the spring boot project, setup README.md, MIT license
- Configure Github action, apply Pull request rules
- Setup packages, initial models and relationships

# Architecture



# Database Modeling


# Setting Up and Running at Local

## Configuration

Include setup steps for PostgreSQL database connection, and any other necessary configurations.

1. Set up PostgreSQL:

    - Install PostgreSQL on your system.
    - Create a new PostgreSQL database for your application.
    
2. Set up environment variable:

    - Clone file **.env.sample** to the new file with name: **.env**
    - Update environment variable in file **.env**

## Run
0. Remember checkstyle
```bash
   ./mvnw checkstyle:checkstyle
```
1. Build the Project
```bash
    ./mvnw clean install  
```
2. Run the Application
```bash
    ./mvnw spring-boot:run
```
3. Note

   - Test account:
   ```bash
       username: sunrise
       password: Test#1234
    ```
# References
