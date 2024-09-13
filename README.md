# Budgeting Service System
Budgeting Service System is a project provides features for tracking and managing budget, money, users, and trading transactions.

[![sun-rise](https://github.com/khanhduzz/sun-rise/actions/workflows/pipeline-sun.yml/badge.svg)](https://github.com/khanhduzz/sun-rise/actions/workflows/pipeline-sun.yml)
[![coverage](https://img.shields.io/badge/coverage-0%25-red)](https://github.com/khanhduzz/sun-rise/actions/workflows/pipeline-sun.yml)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=khanhduzz_sun-rise&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=khanhduzz_sun-rise) [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=khanhduzz_sun-rise&metric=bugs)](https://sonarcloud.io/summary/new_code?id=khanhduzz_sun-rise) [![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=khanhduzz_sun-rise&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=khanhduzz_sun-rise) [![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=khanhduzz_sun-rise&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=khanhduzz_sun-rise) [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=khanhduzz_sun-rise&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=khanhduzz_sun-rise) [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=khanhduzz_sun-rise&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=khanhduzz_sun-rise) [![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=khanhduzz_sun-rise&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=khanhduzz_sun-rise) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=khanhduzz_sun-rise&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=khanhduzz_sun-rise) [![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=khanhduzz_sun-rise&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=khanhduzz_sun-rise) [![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=khanhduzz_sun-rise&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=khanhduzz_sun-rise)

<div style="text-align: center;">
  <img width="491" alt="Screenshot 2024-08-22 at 21 34 38" src="https://github.com/user-attachments/assets/cbd54394-b9be-4ca9-a12c-e774efd4e52e">
</div>

# Technologies
- Java 17
- Spring boot 3.3.3
- Swagger
- PostgreSQL
- Thymeleaf
- Bootstrap
- Jquery
- Docker
- Sonar cloud for checking clean code.

> *_Running:_* You can run this project with Docker container
> - Run docker on your local machine.
> - Run `mvn clean install` to create `.jar` file
> - Go to the project, run `docker compose build` then `docker compose up` on `terminal/command promt`, then waiting for project starting
> - The project is run on: `localhost:8086/sun/`
> - The postgres container is run on: `localhost:5432`\
>   Default user:\
>     `username: admin`\
>     `password: admin`  
> - The PgAdmin is run on: `localhost:8081`\
>   Defaul pgAdmin user:\
>     `username: admin@sun.com`\
>     `password: admin`\
>   After login to `PgAmin -> Register -> Create new -> Create postgres database`

# Current Feature
- Init the spring boot project, setup README.md, MIT license
- Configure Github action, apply Pull request rules
- Setup packages, initial models and relationships
- Added checkstyle, ci pipeline for checking maven build and test.
- Added docker config to create docker image and container.
- Able to run in local and with docker

# Architecture
<img width="1307" alt="Screenshot 2024-09-13 at 21 21 04" src="https://github.com/user-attachments/assets/34f1230e-e4f6-41b6-b9b4-c8cc1e7d7634">


# Database Modeling
![detb-service-diagram](https://github.com/user-attachments/assets/f60b95b4-bb9a-44cb-8e95-3b5f1987e2d1)

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
       email: sun@sun.com
       password: test1234
    ```
# References

## Documentation & Tools
1. **Spring Boot Documentation**:
   - [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)

2. **Swagger Documentation**:
   - [Swagger OpenAPI Specification](https://swagger.io/specification/)

3. **PostgreSQL Documentation**:
   - [PostgreSQL Official Documentation](https://www.postgresql.org/docs/)

4. **Thymeleaf Documentation**:
   - [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)

5. **Bootstrap Documentation**:
   - [Bootstrap Official Documentation](https://getbootstrap.com/docs/5.3/getting-started/introduction/)

6. **jQuery Documentation**:
   - [jQuery API Documentation](https://api.jquery.com/)

7. **Docker Documentation**:
   - [Docker Documentation](https://docs.docker.com/)

8. **SonarCloud Documentation**:
   - [SonarCloud Documentation](https://sonarcloud.io/documentation/)

## Development & Deployment
1. **Maven Documentation**:
   - [Maven Project Documentation](https://maven.apache.org/guides/index.html)

2. **GitHub Actions Documentation**:
   - [GitHub Actions Documentation](https://docs.github.com/en/actions)

3. **PgAdmin Documentation**:
   - [PgAdmin Documentation](https://www.pgadmin.org/docs/)

## Best Practices & Standards
1. **Clean Code**:
   - [Clean Code by Robert C. Martin](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)

2. **Checkstyle Documentation**:
   - [Checkstyle Documentation](https://checkstyle.sourceforge.io/)

3. **MIT License**:
   - [MIT License Text](https://opensource.org/licenses/MIT)

## Additional Learning Resources
1. **Java 17 Official Documentation**:
   - [Java 17 Documentation](https://docs.oracle.com/en/java/javase/17/)

2. **SonarQube Documentation**:
   - [SonarQube Documentation](https://docs.sonarqube.org/latest/)

3. **JPA & Hibernate Documentation**:
   - [Java Persistence API (JPA) Documentation](https://www.oracle.com/java/technologies/persistence-jsp.html)
   - [Hibernate ORM Documentation](https://hibernate.org/orm/documentation/)
