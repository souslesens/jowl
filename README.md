# jowl

<summary>Jowl is a spring boot application to expose some reasoning APIS such as 
checking consistency / satisfaisability and computeinference.
we used Pellet and OWLAPI for developement of this application.</summary>
This application will be in docker and we will provide a full documentation to run it.
### To Build The application Via Docker
```
docker build -t jowl .
```
### To Run The Application
```
docker run -p 8080:8080 jowl
```
### If you don't have docker installed
Visist https://www.docker.com/products/docker-desktop/