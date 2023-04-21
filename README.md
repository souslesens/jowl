# jowl
Jowl is a spring boot application to expose some reasoning APIS such as 
checking consistency / satisfaisability and computeinference.
we used Pellet and OWLAPI for developement of this application.
This application will be in docker and we will provide a full documentation to run it.
update
to build the application
```
docker build -t jowl .
```
to run the application
```
docker run -p 8080:8080 jowl
```