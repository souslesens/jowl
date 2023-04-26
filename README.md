# jowl

<summary>Jowl is a spring boot application to expose some reasoning APIS such as 
checking consistency / satisfaisability and computeinference.
we used Pellet and OWLAPI for developement of this application.</summary>
This application will be in docker and we will provide a full documentation to run it.

### To Build The application Via Docker
----------------

```
docker build -t jowl .
```
----------------
### To Run The Application
```
docker run -p 8080:8080 -v C:\:/usr/src/app/dir-in-container jowl
```
----------------
### One Command : To Build && Run The application Via Docker-Compose
```
docker-compose up
```
----------------
### If you don't have docker installed
Visist https://www.docker.com/products/docker-desktop/
----------------
### API Documentation
after you successfully downloded the project and u ran it 
here the API , that you can use. 

__Base URL__ : https://localhost:8080

| Method Type | API  | Description |
| -------- | -------- | -------- |
| _GET_ | /reasoner/test | Testing if the server is running or not |
| _GET_ | /reasoner/consistency?filePath=<YOUR_FILE_NAME> | Checking if the passed ontology file is consistent or not |
| _GET_ | /reasoner/consistency?url=<YOUR_URL_HERE> | Checking if the passed ontology URL is consistent or not |
| _GET_ | /reasoner/unsatisfiable?filePath=<YOUR_FILE_NAME> | Checking if the passed ontology file has an unsatifiable classes |
| _GET_ | /reasoner/unsatisfiable?url=<YOUR_URL_HERE> | Checking if the passed ontology URL has an unsatifiable classes |
| _GET_ | /reasoner/inference?filePath=<YOUR_FILE_NAME> | Generating Inferences from a file |
| _GET_ | /reasoner/inference?url=<YOUR_URL_HERE> | Generating Inferences from an URL |
| _POST_ | /reasoner/consistency | Checking if the passed ontology file is consistent or not : can work with any input  (File , URL , Text) |
| _POST_ | /reasoner/inference?url=<YOUR_URL_HERE> | Checking if the passed ontology file has an unsatifiable classes : can work with any input  (File , URL , Text) |
| _POST_ | /reasoner/inference?filePath=<YOUR_FILE_NAME> | Generating Inferences : : can work with any input  (File , URL , Text) |