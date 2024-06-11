# srm-api

The API client for SRM

## Dependencies
* [Java 17](https://www.oracle.com/java/technologies/downloads/)
* [Maven 3.0+](https://maven.apache.org/)
* [MySQL 8](https://dev.mysql.com/downloads/mysql/)

## First time setup

### Cloning
```
git clone git@bitbucket.org:scholastic/srm-api.git
cd srm-api
git checkout dev
```

### Database setup

Login with `root` user in MySQL and run:

```
CREATE DATABASE srm CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER 'srm_dev'@'localhost' IDENTIFIED BY 'srm_dev';
GRANT ALL ON srm.* TO 'srm_dev'@'localhost';
FLUSH PRIVILEGES;
```

Confirm user has been created successfully by running:

```
mysql -u srm_dev -p
```

Enter password `srm_dev` when prompted.

### Building/Installing
```
mvn clean install
```

## System properties required to run
```
baServiceBaseUrl
serverRootUrl
jwtSecret
kongToken
```

## Optional global settings
```
shuffleAssessmentAnswers - When set to 'false', assessment issued question answers will not be shuffled.
```

## Running on your local
1. Command line
```
java \
-DbaServiceBaseUrl=http://localhost:8082 \
-DserverRootUrl=http://localhost:8080 \
-DjwtSecret=blahblahblahblahblahblahblahblah \
-DkongToken=blah \
-jar target/srm-api-1.0.0.jar
```
2. IDE (e.g. [Spring Tools](https://spring.io/tools))
```
1. File > Import > Maven > Existing Maven Project
2. Browse for the srm-api directory and complete the import with defaults
3. Make sure you have the required system properties set
4. Right click on srm-api in Project Explorer and select Run As > Spring Boot App
```

## Running Tests + Coverage
```
cd srm-api
mvn clean install
```
Coverage results can be viewed by opening `target/site/jacoco/index.html` in a browser.

## Linting
If you are using an IDE, install the respective [SonarLint plugin](https://www.sonarlint.org/).