# jowl

<summary>Jowl is a spring boot application to expose some reasoning APIS such as 
checking consistency / satisfaisability and computeinference.
we used Pellet and OWLAPI for developement of this application.</summary>
This application will be in docker and we will provide a full documentation to run it.

----------------
### To Build The application Via Docker

```
docker build -t jowl .
```

### To Run The Application
```
docker run -p 8080:8080 jowl
```
----------------
### To Run The Application in the background
```
docker run -d -p 8080:8080 jowl
```
----------------
### One Command : To Build && Run The application Via Docker-Compose
```
docker-compose up
```
----------------
### If you don't have docker installed
Refer to the
[Docker Web Site](https://www.docker.com/products/docker-desktop/)

----------------
### API Documentation
After successfully downloading the project and running it, here's the API that you can use

__Base URL__ : https://localhost:8080


__Reasoning's APIS__
| Method Type | API  | Description |
| -------- | -------- | -------- |
| _GET_ | /reasoner/test | Testing if the server is running or not |
| _GET_ | /reasoner/consistency?filePath=<YOUR_FILE_NAME> | Checking if the passed ontology file is consistent or not |
| _GET_ | /reasoner/consistency?url=<YOUR_URL_HERE> | Checking if the passed ontology URL is consistent or not |
| _GET_ | /reasoner/unsatisfiable?filePath=<YOUR_FILE_NAME> | Checking if the passed ontology file has an unsatifiable classes |
| _GET_ | /reasoner/unsatisfiable?url=<YOUR_URL_HERE> | Checking if the passed ontology URL has an unsatifiable classes |
| _GET_ | /reasoner/inference?filePath=<YOUR_FILE_NAME> | Generating Inferences from a file |
| _GET_ | /reasoner/inference?url=<YOUR_URL_HERE> | Generating Inferences from an URL |
| _GET_ | /reasoner/parametres | Get all the predicates associated with their class name |
| _POST_ | /reasoner/consistency | Checking if the passed ontology file is consistent or not : can work with any input  (File , URL , Text) |
| _POST_ | /reasoner/unsatisfiable | Checking if the passed ontology file has an unsatifiable classes : can work with any input  (File , URL , Text) |
| _POST_ | /reasoner/inference | Generating Inferences : can work with any input  (File , URL , Text) |

----------------

### Example Of Use For Reasoning Part

You open for example Postman ( You don't you know postman ? : [Postman](https://www.postman.com/) )

You choose POST Request , you pass this URL https://localhost:8080/reasoner/inference

You should pass one parameter of those in this List {url,ontologyContentEncoded64,filePath } 
in the JSON Body 

For example when u say RangeAndDomain inside the predicate array u mean , Infer All the Range and Domain from the Ontology

You pick Body -> Raw -> JSON 

```JSON
{  
    "ontologyContentEncoded64":"PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIgogICAgICAgICB4bWxuczpyZGZzPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzAxL3JkZi1zY2hlbWEjIgoJIHhtbG5zOm93bD0iaHR0cDovL3d3dy53My5vcmcvMjAwMi8wNy9vd2wjIgoJIHhtbG5zPSJodHRwOi8vd3d3Lnhmcm9udC5jb20vb3dsL29udG9sb2dpZXMvY2FtZXJhLyMiCgkgeG1sbnM6Y2FtZXJhPSJodHRwOi8vd3d3Lnhmcm9udC5jb20vb3dsL29udG9sb2dpZXMvY2FtZXJhLyMiCgkgeG1sOmJhc2U9Imh0dHA6Ly93d3cueGZyb250LmNvbS9vd2wvb250b2xvZ2llcy9jYW1lcmEvIj4KCiAgICA8b3dsOk9udG9sb2d5PgogICAgICAgIDxyZGZzOmNvbW1lbnQ+CiAgICAgICAgQ2FtZXJhIE9XTCBPbnRvbG9neSAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgQXV0aG9yOiBSb2dlciBMLiBDb3N0ZWxsbyAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgIEFja25vd2xlZ2VtZW50czogTWFueSB0aGFua3MgdG8gdGhlIGZvbGxvd2luZyBwZW9wbGUgZm9yICAgIAogICAgICAgICAgICAgICAgICAgICAgIHRoZWlyIGludmFsdWFibGUgaW5wdXQ6ICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICBSaWNoYXJkIE1jQ3VsbG91Z2gsIFl1emhvbmcgUXUsICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgTGVvIFNhdWVybWFubiwgQnJpYW4gTWNCcmlkZSBhbmQgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgIEppbSBGYXJydWdpYS4gICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAKICAgICAgTW9kaWZpZWQgYXMgYSBKZW5hIGV4YW1wbGUgYnkgSWFuIERpY2tpbnNvbgogICAgICAgIDwvcmRmczpjb21tZW50PgogICAgPC9vd2w6T250b2xvZ3k+CgogICAgIDxvd2w6Q2xhc3MgcmRmOklEPSJNb25leSI+CiAgICAgICAgICA8cmRmczpzdWJDbGFzc09mIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMi8wNy9vd2wjVGhpbmciLz4KICAgICA8L293bDpDbGFzcz4KCiAgICAgPG93bDpEYXRhdHlwZVByb3BlcnR5IHJkZjpJRD0iY3VycmVuY3kiPgogICAgICAgICAgPHJkZnM6ZG9tYWluIHJkZjpyZXNvdXJjZT0iI01vbmV5Ii8+CiAgICAgICAgICA8cmRmczpyYW5nZSByZGY6cmVzb3VyY2U9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyIvPgogICAgIDwvb3dsOkRhdGF0eXBlUHJvcGVydHk+CgogICAgIDxvd2w6Q2xhc3MgcmRmOklEPSJSYW5nZSI+CiAgICAgICAgICA8cmRmczpzdWJDbGFzc09mIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMi8wNy9vd2wjVGhpbmciLz4KICAgICA8L293bDpDbGFzcz4KCiAgICAgPG93bDpEYXRhdHlwZVByb3BlcnR5IHJkZjpJRD0ibWluIj4KICAgICAgICAgIDxyZGZzOmRvbWFpbiByZGY6cmVzb3VyY2U9IiNSYW5nZSIvPgogICAgICAgICAgPHJkZnM6cmFuZ2UgcmRmOnJlc291cmNlPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSNmbG9hdCIvPgogICAgIDwvb3dsOkRhdGF0eXBlUHJvcGVydHk+CgogICAgIDxvd2w6RGF0YXR5cGVQcm9wZXJ0eSByZGY6SUQ9Im1heCI+CiAgICAgICAgICA8cmRmczpkb21haW4gcmRmOnJlc291cmNlPSIjUmFuZ2UiLz4KICAgICAgICAgIDxyZGZzOnJhbmdlIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjZmxvYXQiLz4KICAgICA8L293bDpEYXRhdHlwZVByb3BlcnR5PgoKICAgICA8b3dsOkRhdGF0eXBlUHJvcGVydHkgcmRmOklEPSJ1bml0cyI+CiAgICAgICAgICA8cmRmczpkb21haW4gcmRmOnJlc291cmNlPSIjUmFuZ2UiLz4KICAgICAgICAgIDxyZGZzOnJhbmdlIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjc3RyaW5nIi8+CiAgICAgPC9vd2w6RGF0YXR5cGVQcm9wZXJ0eT4KCiAgICAgPG93bDpDbGFzcyByZGY6SUQ9IldpbmRvdyI+CiAgICAgICAgICA8cmRmczpzdWJDbGFzc09mIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMi8wNy9vd2wjVGhpbmciLz4KICAgICA8L293bDpDbGFzcz4KCiAgICAgPGNhbWVyYTpXaW5kb3cgcmRmOklEPSJUaHJvdWdoVGhlTGVucyIvPgogICAgIDxjYW1lcmE6V2luZG93IHJkZjpJRD0iV2luZG93T25Ub3BPZkNhbWVyYSIvPgoKICAgICA8b3dsOkNsYXNzIHJkZjpJRD0iVmlld2VyIj4KICAgICAgICAgPG93bDpvbmVPZiByZGY6cGFyc2VUeXBlPSJDb2xsZWN0aW9uIj4KICAgICAgICAgICAgICAgPGNhbWVyYTpXaW5kb3cgcmRmOmFib3V0PSIjVGhyb3VnaFRoZUxlbnMiLz4KICAgICAgICAgICAgICAgPGNhbWVyYTpXaW5kb3cgcmRmOmFib3V0PSIjV2luZG93T25Ub3BPZkNhbWVyYSIvPgogICAgICAgICAgPC9vd2w6b25lT2Y+CiAgICAgPC9vd2w6Q2xhc3M+CgogICAgIDxvd2w6Q2xhc3MgcmRmOklEPSJQdXJjaGFzZWFibGVJdGVtIj4KICAgICAgICAgIDxyZGZzOnN1YkNsYXNzT2YgcmRmOnJlc291cmNlPSJodHRwOi8vd3d3LnczLm9yZy8yMDAyLzA3L293bCNUaGluZyIvPgogICAgIDwvb3dsOkNsYXNzPgoKICAgICA8b3dsOk9iamVjdFByb3BlcnR5IHJkZjpJRD0iY29zdCI+CiAgICAgICAgICA8cmRmczpkb21haW4gcmRmOnJlc291cmNlPSIjUHVyY2hhc2VhYmxlSXRlbSIvPgogICAgICAgICAgPHJkZnM6cmFuZ2UgcmRmOnJlc291cmNlPSIjTW9uZXkiLz4KICAgICA8L293bDpPYmplY3RQcm9wZXJ0eT4KCiAgICAgPG93bDpDbGFzcyByZGY6SUQ9IkJvZHkiPgogICAgICAgICAgPHJkZnM6c3ViQ2xhc3NPZiByZGY6cmVzb3VyY2U9IiNQdXJjaGFzZWFibGVJdGVtIi8+CiAgICAgPC9vd2w6Q2xhc3M+CgogICAgIDxvd2w6Q2xhc3MgcmRmOklEPSJCb2R5V2l0aE5vbkFkanVzdGFibGVTaHV0dGVyU3BlZWQiPgogICAgICAgICAgPG93bDppbnRlcnNlY3Rpb25PZiByZGY6cGFyc2VUeXBlPSJDb2xsZWN0aW9uIj4KICAgICAgICAgICAgICAgPG93bDpDbGFzcyByZGY6YWJvdXQ9IiNCb2R5Ii8+CiAgICAgICAgICAgICAgIDxvd2w6UmVzdHJpY3Rpb24+CiAgICAgICAgICAgICAgICAgICAgIDxvd2w6b25Qcm9wZXJ0eSByZGY6cmVzb3VyY2U9IiNzaHV0dGVyLXNwZWVkIi8+CiAgICAgICAgICAgICAgICAgICAgIDxvd2w6Y2FyZGluYWxpdHk+MDwvb3dsOmNhcmRpbmFsaXR5PgogICAgICAgICAgICAgICA8L293bDpSZXN0cmljdGlvbj4KICAgICAgICAgIDwvb3dsOmludGVyc2VjdGlvbk9mPgogICAgIDwvb3dsOkNsYXNzPgoKICAgICA8b3dsOkNsYXNzIHJkZjpJRD0iTGVucyI+CiAgICAgICAgICA8cmRmczpzdWJDbGFzc09mIHJkZjpyZXNvdXJjZT0iI1B1cmNoYXNlYWJsZUl0ZW0iLz4KICAgICA8L293bDpDbGFzcz4KCiAgICAgPG93bDpDbGFzcyByZGY6SUQ9IkNhbWVyYSI+CiAgICAgICAgICA8cmRmczpzdWJDbGFzc09mIHJkZjpyZXNvdXJjZT0iI1B1cmNoYXNlYWJsZUl0ZW0iLz4KICAgICA8L293bDpDbGFzcz4KCiAgICAgPG93bDpDbGFzcyByZGY6SUQ9IlNMUiI+CiAgICAgICAgICA8b3dsOmludGVyc2VjdGlvbk9mIHJkZjpwYXJzZVR5cGU9IkNvbGxlY3Rpb24iPgogICAgICAgICAgICAgICA8b3dsOkNsYXNzIHJkZjphYm91dD0iI0NhbWVyYSIvPgogICAgICAgICAgICAgICA8b3dsOlJlc3RyaWN0aW9uPgogICAgICAgICAgICAgICAgICAgICA8b3dsOm9uUHJvcGVydHkgcmRmOnJlc291cmNlPSIjdmlld0ZpbmRlciIvPgogICAgICAgICAgICAgICAgICAgICA8b3dsOmhhc1ZhbHVlIHJkZjpyZXNvdXJjZT0iI1Rocm91Z2hUaGVMZW5zIi8+CiAgICAgICAgICAgICAgIDwvb3dsOlJlc3RyaWN0aW9uPgogICAgICAgICAgPC9vd2w6aW50ZXJzZWN0aW9uT2Y+CiAgICAgPC9vd2w6Q2xhc3M+CgogICAgIDxvd2w6Q2xhc3MgcmRmOklEPSJMYXJnZS1Gb3JtYXQiPgogICAgICAgICAgPHJkZnM6c3ViQ2xhc3NPZiByZGY6cmVzb3VyY2U9IiNDYW1lcmEiLz4KICAgICAgICAgIDxyZGZzOnN1YkNsYXNzT2Y+CiAgICAgICAgICAgICAgIDxvd2w6UmVzdHJpY3Rpb24+CiAgICAgICAgICAgICAgICAgICAgIDxvd2w6b25Qcm9wZXJ0eSByZGY6cmVzb3VyY2U9IiNib2R5Ii8+CiAgICAgICAgICAgICAgICAgICAgIDxvd2w6YWxsVmFsdWVzRnJvbSByZGY6cmVzb3VyY2U9IiNCb2R5V2l0aE5vbkFkanVzdGFibGVTaHV0dGVyU3BlZWQiLz4KICAgICAgICAgICAgICAgPC9vd2w6UmVzdHJpY3Rpb24+CiAgICAgICAgICA8L3JkZnM6c3ViQ2xhc3NPZj4KICAgICA8L293bDpDbGFzcz4KCiAgICAgPG93bDpDbGFzcyByZGY6SUQ9IkRpZ2l0YWwiPgogICAgICAgICAgPHJkZnM6c3ViQ2xhc3NPZiByZGY6cmVzb3VyY2U9IiNDYW1lcmEiLz4KICAgICA8L293bDpDbGFzcz4KCiAgICAgPG93bDpPYmplY3RQcm9wZXJ0eSByZGY6SUQ9InBhcnQiLz4KCiAgICAgPG93bDpPYmplY3RQcm9wZXJ0eSByZGY6SUQ9ImxlbnMiPgogICAgICAgICAgPHJkZnM6c3ViUHJvcGVydHlPZiByZGY6cmVzb3VyY2U9IiNwYXJ0Ii8+CiAgICAgICAgICA8cmRmczpkb21haW4gcmRmOnJlc291cmNlPSIjQ2FtZXJhIi8+CiAgICAgICAgICA8cmRmczpyYW5nZSByZGY6cmVzb3VyY2U9IiNMZW5zIi8+CiAgICAgPC9vd2w6T2JqZWN0UHJvcGVydHk+CgogICAgIDxvd2w6T2JqZWN0UHJvcGVydHkgcmRmOklEPSJib2R5Ij4KICAgICAgICAgIDxyZGZzOnN1YlByb3BlcnR5T2YgcmRmOnJlc291cmNlPSIjcGFydCIvPgogICAgICAgICAgPHJkZnM6ZG9tYWluIHJkZjpyZXNvdXJjZT0iI0NhbWVyYSIvPgogICAgICAgICAgPHJkZnM6cmFuZ2UgcmRmOnJlc291cmNlPSIjQm9keSIvPgogICAgIDwvb3dsOk9iamVjdFByb3BlcnR5PgoKICAgICA8b3dsOk9iamVjdFByb3BlcnR5IHJkZjpJRD0idmlld0ZpbmRlciI+CiAgICAgICAgICA8cmRmOnR5cGUgcmRmOnJlc291cmNlPSJodHRwOi8vd3d3LnczLm9yZy8yMDAyLzA3L293bCNGdW5jdGlvbmFsUHJvcGVydHkiLz4KICAgICAgICAgIDxyZGZzOmRvbWFpbiByZGY6cmVzb3VyY2U9IiNDYW1lcmEiLz4KICAgICAgICAgIDxyZGZzOnJhbmdlIHJkZjpyZXNvdXJjZT0iI1ZpZXdlciIvPgogICAgIDwvb3dsOk9iamVjdFByb3BlcnR5PgoKICAgICA8b3dsOkRhdGF0eXBlUHJvcGVydHkgcmRmOklEPSJzaXplIj4KICAgICAgICAgIDxyZGZzOmRvbWFpbiByZGY6cmVzb3VyY2U9IiNMZW5zIi8+CiAgICAgICAgICA8cmRmczpyYW5nZSByZGY6cmVzb3VyY2U9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyIvPgogICAgIDwvb3dsOkRhdGF0eXBlUHJvcGVydHk+CgogICAgIDxvd2w6RGF0YXR5cGVQcm9wZXJ0eSByZGY6SUQ9ImFwZXJ0dXJlIj4KICAgICAgICAgIDxyZGZzOmRvbWFpbiByZGY6cmVzb3VyY2U9IiNMZW5zIi8+CiAgICAgICAgICA8cmRmczpyYW5nZSByZGY6cmVzb3VyY2U9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyIvPgogICAgIDwvb3dsOkRhdGF0eXBlUHJvcGVydHk+CgogICAgIDxvd2w6T2JqZWN0UHJvcGVydHkgcmRmOklEPSJjb21wYXRpYmxlV2l0aCI+CiAgICAgICAgICA8cmRmczpkb21haW4gcmRmOnJlc291cmNlPSIjTGVucyIvPgogICAgICAgICAgPHJkZnM6cmFuZ2UgcmRmOnJlc291cmNlPSIjQm9keSIvPgogICAgIDwvb3dsOk9iamVjdFByb3BlcnR5PgoKICAgICA8b3dsOk9iamVjdFByb3BlcnR5IHJkZjpJRD0ic2h1dHRlci1zcGVlZCI+CiAgICAgICAgICA8cmRmczpkb21haW4gcmRmOnJlc291cmNlPSIjQm9keSIvPgogICAgICAgICAgPHJkZnM6cmFuZ2UgcmRmOnJlc291cmNlPSIjUmFuZ2UiLz4KICAgICA8L293bDpPYmplY3RQcm9wZXJ0eT4KCiAgICAgPG93bDpEYXRhdHlwZVByb3BlcnR5IHJkZjpJRD0iZm9jYWwtbGVuZ3RoIj4KICAgICAgICAgIDxvd2w6ZXF1aXZhbGVudFByb3BlcnR5IHJkZjpyZXNvdXJjZT0iI3NpemUiLz4KICAgICAgICAgIDxyZGZzOmRvbWFpbiByZGY6cmVzb3VyY2U9IiNMZW5zIi8+CiAgICAgICAgICA8cmRmczpyYW5nZSByZGY6cmVzb3VyY2U9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyIvPgogICAgIDwvb3dsOkRhdGF0eXBlUHJvcGVydHk+CgogICAgIDxvd2w6RGF0YXR5cGVQcm9wZXJ0eSByZGY6SUQ9ImYtc3RvcCI+CiAgICAgICAgICA8b3dsOmVxdWl2YWxlbnRQcm9wZXJ0eSByZGY6cmVzb3VyY2U9IiNhcGVydHVyZSIvPgogICAgICAgICAgPHJkZnM6ZG9tYWluIHJkZjpyZXNvdXJjZT0iI0xlbnMiLz4KICAgICAgICAgIDxyZGZzOnJhbmdlIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjc3RyaW5nIi8+CiAgICAgPC9vd2w6RGF0YXR5cGVQcm9wZXJ0eT4KCjwvcmRmOlJERj4=",
    "predicates":[
           "DomainAndRange","UnionOf"
       ]
    }
```

### Example 2 Of Use For Reasoning Part
 
here example when u say AllOWL inside the predicate array u mean , Infer Anything Possible from the Ontology

the list of the predicate:
1. equivalentClass
2. sameIndividual
3. IntersectionOf
4. UnionOf
5. DisjointClasses
6. differentIndividual
7. HasValue
8. InverseObjectProperties
9. AllValuesFrom
10. SomeValuesFrom
11. DomainAndRange
12. ClassAssertion
13. SubClass
14. DataPropertyCharacteristic
15. EquivalentDataProperty
16. EquivalentObjectProperty
17. SubObjectProperty
18. SubDataPropertyOfAxiom
19. ObjectPropertyCharacteristic
20. SubDataPropertyOfAxiom
21. ComplementOf
22. All_OWL


```JSON
{  
    "ontologyContentEncoded64":"PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIgogICAgICAgICB4bWxuczpyZGZzPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzAxL3JkZi1zY2hlbWEjIgoJIHhtbG5zOm93bD0iaHR0cDovL3d3dy53My5vcmcvMjAwMi8wNy9vd2wjIgoJIHhtbG5zPSJodHRwOi8vd3d3Lnhmcm9udC5jb20vb3dsL29udG9sb2dpZXMvY2FtZXJhLyMiCgkgeG1sbnM6Y2FtZXJhPSJodHRwOi8vd3d3Lnhmcm9udC5jb20vb3dsL29udG9sb2dpZXMvY2FtZXJhLyMiCgkgeG1sOmJhc2U9Imh0dHA6Ly93d3cueGZyb250LmNvbS9vd2wvb250b2xvZ2llcy9jYW1lcmEvIj4KCiAgICA8b3dsOk9udG9sb2d5PgogICAgICAgIDxyZGZzOmNvbW1lbnQ+CiAgICAgICAgQ2FtZXJhIE9XTCBPbnRvbG9neSAgICAgICAgICAgICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgQXV0aG9yOiBSb2dlciBMLiBDb3N0ZWxsbyAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgIEFja25vd2xlZ2VtZW50czogTWFueSB0aGFua3MgdG8gdGhlIGZvbGxvd2luZyBwZW9wbGUgZm9yICAgIAogICAgICAgICAgICAgICAgICAgICAgIHRoZWlyIGludmFsdWFibGUgaW5wdXQ6ICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICBSaWNoYXJkIE1jQ3VsbG91Z2gsIFl1emhvbmcgUXUsICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgTGVvIFNhdWVybWFubiwgQnJpYW4gTWNCcmlkZSBhbmQgICAgICAgIAogICAgICAgICAgICAgICAgICAgICAgICAgIEppbSBGYXJydWdpYS4gICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAKICAgICAgTW9kaWZpZWQgYXMgYSBKZW5hIGV4YW1wbGUgYnkgSWFuIERpY2tpbnNvbgogICAgICAgIDwvcmRmczpjb21tZW50PgogICAgPC9vd2w6T250b2xvZ3k+CgogICAgIDxvd2w6Q2xhc3MgcmRmOklEPSJNb25leSI+CiAgICAgICAgICA8cmRmczpzdWJDbGFzc09mIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMi8wNy9vd2wjVGhpbmciLz4KICAgICA8L293bDpDbGFzcz4KCiAgICAgPG93bDpEYXRhdHlwZVByb3BlcnR5IHJkZjpJRD0iY3VycmVuY3kiPgogICAgICAgICAgPHJkZnM6ZG9tYWluIHJkZjpyZXNvdXJjZT0iI01vbmV5Ii8+CiAgICAgICAgICA8cmRmczpyYW5nZSByZGY6cmVzb3VyY2U9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyIvPgogICAgIDwvb3dsOkRhdGF0eXBlUHJvcGVydHk+CgogICAgIDxvd2w6Q2xhc3MgcmRmOklEPSJSYW5nZSI+CiAgICAgICAgICA8cmRmczpzdWJDbGFzc09mIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMi8wNy9vd2wjVGhpbmciLz4KICAgICA8L293bDpDbGFzcz4KCiAgICAgPG93bDpEYXRhdHlwZVByb3BlcnR5IHJkZjpJRD0ibWluIj4KICAgICAgICAgIDxyZGZzOmRvbWFpbiByZGY6cmVzb3VyY2U9IiNSYW5nZSIvPgogICAgICAgICAgPHJkZnM6cmFuZ2UgcmRmOnJlc291cmNlPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSNmbG9hdCIvPgogICAgIDwvb3dsOkRhdGF0eXBlUHJvcGVydHk+CgogICAgIDxvd2w6RGF0YXR5cGVQcm9wZXJ0eSByZGY6SUQ9Im1heCI+CiAgICAgICAgICA8cmRmczpkb21haW4gcmRmOnJlc291cmNlPSIjUmFuZ2UiLz4KICAgICAgICAgIDxyZGZzOnJhbmdlIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjZmxvYXQiLz4KICAgICA8L293bDpEYXRhdHlwZVByb3BlcnR5PgoKICAgICA8b3dsOkRhdGF0eXBlUHJvcGVydHkgcmRmOklEPSJ1bml0cyI+CiAgICAgICAgICA8cmRmczpkb21haW4gcmRmOnJlc291cmNlPSIjUmFuZ2UiLz4KICAgICAgICAgIDxyZGZzOnJhbmdlIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjc3RyaW5nIi8+CiAgICAgPC9vd2w6RGF0YXR5cGVQcm9wZXJ0eT4KCiAgICAgPG93bDpDbGFzcyByZGY6SUQ9IldpbmRvdyI+CiAgICAgICAgICA8cmRmczpzdWJDbGFzc09mIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMi8wNy9vd2wjVGhpbmciLz4KICAgICA8L293bDpDbGFzcz4KCiAgICAgPGNhbWVyYTpXaW5kb3cgcmRmOklEPSJUaHJvdWdoVGhlTGVucyIvPgogICAgIDxjYW1lcmE6V2luZG93IHJkZjpJRD0iV2luZG93T25Ub3BPZkNhbWVyYSIvPgoKICAgICA8b3dsOkNsYXNzIHJkZjpJRD0iVmlld2VyIj4KICAgICAgICAgPG93bDpvbmVPZiByZGY6cGFyc2VUeXBlPSJDb2xsZWN0aW9uIj4KICAgICAgICAgICAgICAgPGNhbWVyYTpXaW5kb3cgcmRmOmFib3V0PSIjVGhyb3VnaFRoZUxlbnMiLz4KICAgICAgICAgICAgICAgPGNhbWVyYTpXaW5kb3cgcmRmOmFib3V0PSIjV2luZG93T25Ub3BPZkNhbWVyYSIvPgogICAgICAgICAgPC9vd2w6b25lT2Y+CiAgICAgPC9vd2w6Q2xhc3M+CgogICAgIDxvd2w6Q2xhc3MgcmRmOklEPSJQdXJjaGFzZWFibGVJdGVtIj4KICAgICAgICAgIDxyZGZzOnN1YkNsYXNzT2YgcmRmOnJlc291cmNlPSJodHRwOi8vd3d3LnczLm9yZy8yMDAyLzA3L293bCNUaGluZyIvPgogICAgIDwvb3dsOkNsYXNzPgoKICAgICA8b3dsOk9iamVjdFByb3BlcnR5IHJkZjpJRD0iY29zdCI+CiAgICAgICAgICA8cmRmczpkb21haW4gcmRmOnJlc291cmNlPSIjUHVyY2hhc2VhYmxlSXRlbSIvPgogICAgICAgICAgPHJkZnM6cmFuZ2UgcmRmOnJlc291cmNlPSIjTW9uZXkiLz4KICAgICA8L293bDpPYmplY3RQcm9wZXJ0eT4KCiAgICAgPG93bDpDbGFzcyByZGY6SUQ9IkJvZHkiPgogICAgICAgICAgPHJkZnM6c3ViQ2xhc3NPZiByZGY6cmVzb3VyY2U9IiNQdXJjaGFzZWFibGVJdGVtIi8+CiAgICAgPC9vd2w6Q2xhc3M+CgogICAgIDxvd2w6Q2xhc3MgcmRmOklEPSJCb2R5V2l0aE5vbkFkanVzdGFibGVTaHV0dGVyU3BlZWQiPgogICAgICAgICAgPG93bDppbnRlcnNlY3Rpb25PZiByZGY6cGFyc2VUeXBlPSJDb2xsZWN0aW9uIj4KICAgICAgICAgICAgICAgPG93bDpDbGFzcyByZGY6YWJvdXQ9IiNCb2R5Ii8+CiAgICAgICAgICAgICAgIDxvd2w6UmVzdHJpY3Rpb24+CiAgICAgICAgICAgICAgICAgICAgIDxvd2w6b25Qcm9wZXJ0eSByZGY6cmVzb3VyY2U9IiNzaHV0dGVyLXNwZWVkIi8+CiAgICAgICAgICAgICAgICAgICAgIDxvd2w6Y2FyZGluYWxpdHk+MDwvb3dsOmNhcmRpbmFsaXR5PgogICAgICAgICAgICAgICA8L293bDpSZXN0cmljdGlvbj4KICAgICAgICAgIDwvb3dsOmludGVyc2VjdGlvbk9mPgogICAgIDwvb3dsOkNsYXNzPgoKICAgICA8b3dsOkNsYXNzIHJkZjpJRD0iTGVucyI+CiAgICAgICAgICA8cmRmczpzdWJDbGFzc09mIHJkZjpyZXNvdXJjZT0iI1B1cmNoYXNlYWJsZUl0ZW0iLz4KICAgICA8L293bDpDbGFzcz4KCiAgICAgPG93bDpDbGFzcyByZGY6SUQ9IkNhbWVyYSI+CiAgICAgICAgICA8cmRmczpzdWJDbGFzc09mIHJkZjpyZXNvdXJjZT0iI1B1cmNoYXNlYWJsZUl0ZW0iLz4KICAgICA8L293bDpDbGFzcz4KCiAgICAgPG93bDpDbGFzcyByZGY6SUQ9IlNMUiI+CiAgICAgICAgICA8b3dsOmludGVyc2VjdGlvbk9mIHJkZjpwYXJzZVR5cGU9IkNvbGxlY3Rpb24iPgogICAgICAgICAgICAgICA8b3dsOkNsYXNzIHJkZjphYm91dD0iI0NhbWVyYSIvPgogICAgICAgICAgICAgICA8b3dsOlJlc3RyaWN0aW9uPgogICAgICAgICAgICAgICAgICAgICA8b3dsOm9uUHJvcGVydHkgcmRmOnJlc291cmNlPSIjdmlld0ZpbmRlciIvPgogICAgICAgICAgICAgICAgICAgICA8b3dsOmhhc1ZhbHVlIHJkZjpyZXNvdXJjZT0iI1Rocm91Z2hUaGVMZW5zIi8+CiAgICAgICAgICAgICAgIDwvb3dsOlJlc3RyaWN0aW9uPgogICAgICAgICAgPC9vd2w6aW50ZXJzZWN0aW9uT2Y+CiAgICAgPC9vd2w6Q2xhc3M+CgogICAgIDxvd2w6Q2xhc3MgcmRmOklEPSJMYXJnZS1Gb3JtYXQiPgogICAgICAgICAgPHJkZnM6c3ViQ2xhc3NPZiByZGY6cmVzb3VyY2U9IiNDYW1lcmEiLz4KICAgICAgICAgIDxyZGZzOnN1YkNsYXNzT2Y+CiAgICAgICAgICAgICAgIDxvd2w6UmVzdHJpY3Rpb24+CiAgICAgICAgICAgICAgICAgICAgIDxvd2w6b25Qcm9wZXJ0eSByZGY6cmVzb3VyY2U9IiNib2R5Ii8+CiAgICAgICAgICAgICAgICAgICAgIDxvd2w6YWxsVmFsdWVzRnJvbSByZGY6cmVzb3VyY2U9IiNCb2R5V2l0aE5vbkFkanVzdGFibGVTaHV0dGVyU3BlZWQiLz4KICAgICAgICAgICAgICAgPC9vd2w6UmVzdHJpY3Rpb24+CiAgICAgICAgICA8L3JkZnM6c3ViQ2xhc3NPZj4KICAgICA8L293bDpDbGFzcz4KCiAgICAgPG93bDpDbGFzcyByZGY6SUQ9IkRpZ2l0YWwiPgogICAgICAgICAgPHJkZnM6c3ViQ2xhc3NPZiByZGY6cmVzb3VyY2U9IiNDYW1lcmEiLz4KICAgICA8L293bDpDbGFzcz4KCiAgICAgPG93bDpPYmplY3RQcm9wZXJ0eSByZGY6SUQ9InBhcnQiLz4KCiAgICAgPG93bDpPYmplY3RQcm9wZXJ0eSByZGY6SUQ9ImxlbnMiPgogICAgICAgICAgPHJkZnM6c3ViUHJvcGVydHlPZiByZGY6cmVzb3VyY2U9IiNwYXJ0Ii8+CiAgICAgICAgICA8cmRmczpkb21haW4gcmRmOnJlc291cmNlPSIjQ2FtZXJhIi8+CiAgICAgICAgICA8cmRmczpyYW5nZSByZGY6cmVzb3VyY2U9IiNMZW5zIi8+CiAgICAgPC9vd2w6T2JqZWN0UHJvcGVydHk+CgogICAgIDxvd2w6T2JqZWN0UHJvcGVydHkgcmRmOklEPSJib2R5Ij4KICAgICAgICAgIDxyZGZzOnN1YlByb3BlcnR5T2YgcmRmOnJlc291cmNlPSIjcGFydCIvPgogICAgICAgICAgPHJkZnM6ZG9tYWluIHJkZjpyZXNvdXJjZT0iI0NhbWVyYSIvPgogICAgICAgICAgPHJkZnM6cmFuZ2UgcmRmOnJlc291cmNlPSIjQm9keSIvPgogICAgIDwvb3dsOk9iamVjdFByb3BlcnR5PgoKICAgICA8b3dsOk9iamVjdFByb3BlcnR5IHJkZjpJRD0idmlld0ZpbmRlciI+CiAgICAgICAgICA8cmRmOnR5cGUgcmRmOnJlc291cmNlPSJodHRwOi8vd3d3LnczLm9yZy8yMDAyLzA3L293bCNGdW5jdGlvbmFsUHJvcGVydHkiLz4KICAgICAgICAgIDxyZGZzOmRvbWFpbiByZGY6cmVzb3VyY2U9IiNDYW1lcmEiLz4KICAgICAgICAgIDxyZGZzOnJhbmdlIHJkZjpyZXNvdXJjZT0iI1ZpZXdlciIvPgogICAgIDwvb3dsOk9iamVjdFByb3BlcnR5PgoKICAgICA8b3dsOkRhdGF0eXBlUHJvcGVydHkgcmRmOklEPSJzaXplIj4KICAgICAgICAgIDxyZGZzOmRvbWFpbiByZGY6cmVzb3VyY2U9IiNMZW5zIi8+CiAgICAgICAgICA8cmRmczpyYW5nZSByZGY6cmVzb3VyY2U9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyIvPgogICAgIDwvb3dsOkRhdGF0eXBlUHJvcGVydHk+CgogICAgIDxvd2w6RGF0YXR5cGVQcm9wZXJ0eSByZGY6SUQ9ImFwZXJ0dXJlIj4KICAgICAgICAgIDxyZGZzOmRvbWFpbiByZGY6cmVzb3VyY2U9IiNMZW5zIi8+CiAgICAgICAgICA8cmRmczpyYW5nZSByZGY6cmVzb3VyY2U9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyIvPgogICAgIDwvb3dsOkRhdGF0eXBlUHJvcGVydHk+CgogICAgIDxvd2w6T2JqZWN0UHJvcGVydHkgcmRmOklEPSJjb21wYXRpYmxlV2l0aCI+CiAgICAgICAgICA8cmRmczpkb21haW4gcmRmOnJlc291cmNlPSIjTGVucyIvPgogICAgICAgICAgPHJkZnM6cmFuZ2UgcmRmOnJlc291cmNlPSIjQm9keSIvPgogICAgIDwvb3dsOk9iamVjdFByb3BlcnR5PgoKICAgICA8b3dsOk9iamVjdFByb3BlcnR5IHJkZjpJRD0ic2h1dHRlci1zcGVlZCI+CiAgICAgICAgICA8cmRmczpkb21haW4gcmRmOnJlc291cmNlPSIjQm9keSIvPgogICAgICAgICAgPHJkZnM6cmFuZ2UgcmRmOnJlc291cmNlPSIjUmFuZ2UiLz4KICAgICA8L293bDpPYmplY3RQcm9wZXJ0eT4KCiAgICAgPG93bDpEYXRhdHlwZVByb3BlcnR5IHJkZjpJRD0iZm9jYWwtbGVuZ3RoIj4KICAgICAgICAgIDxvd2w6ZXF1aXZhbGVudFByb3BlcnR5IHJkZjpyZXNvdXJjZT0iI3NpemUiLz4KICAgICAgICAgIDxyZGZzOmRvbWFpbiByZGY6cmVzb3VyY2U9IiNMZW5zIi8+CiAgICAgICAgICA8cmRmczpyYW5nZSByZGY6cmVzb3VyY2U9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyIvPgogICAgIDwvb3dsOkRhdGF0eXBlUHJvcGVydHk+CgogICAgIDxvd2w6RGF0YXR5cGVQcm9wZXJ0eSByZGY6SUQ9ImYtc3RvcCI+CiAgICAgICAgICA8b3dsOmVxdWl2YWxlbnRQcm9wZXJ0eSByZGY6cmVzb3VyY2U9IiNhcGVydHVyZSIvPgogICAgICAgICAgPHJkZnM6ZG9tYWluIHJkZjpyZXNvdXJjZT0iI0xlbnMiLz4KICAgICAgICAgIDxyZGZzOnJhbmdlIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjc3RyaW5nIi8+CiAgICAgPC9vd2w6RGF0YXR5cGVQcm9wZXJ0eT4KCjwvcmRmOlJERj4=",
    "predicates":[
           "All_OWL"
       ]
    }
```

----------------

__Jena's APIS__
| Method Type | API  | Description |
| -------- | -------- | -------- |
| _POST_ | /jena/rdftriple | Jena Triple Parsedr : can work and support any format (NQUADS,N3,SHACLC,RDFJSON,NTRIPLES,JSONLD,TURTLE,RDFXML,OWL) and can work with any input type  (File , URL , Text) |

----------------
### Example Of Use for Jena PART .

You open for example Postman ( You don't you know postman ? : [Postman](https://www.postman.com/) )

You choose POST Request , you pass this URL https://localhost:8080/jena/rdftriple

You should pass one parameter of those in this List {url,ontologyContentEncoded64,filePath } 
in the JSON Body 

You pick Body -> Raw -> JSON 

```JSON
    {
        "ontologyContentEncoded64":"PD94bWwgdmVyc2lvbj0iMS4wIj8+CjxyZGY6UkRGIHhtbG5zPSJodHRwOi8vd3d3LnNlbWFudGljd2ViLm9yZy9uYXNyby9vbnRvbG9naWVzLzIwMjMvMy91bnRpdGxlZC1vbnRvbG9neS0xMC8iCiAgICAgeG1sOmJhc2U9Imh0dHA6Ly93d3cuc2VtYW50aWN3ZWIub3JnL25hc3JvL29udG9sb2dpZXMvMjAyMy8zL3VudGl0bGVkLW9udG9sb2d5LTEwLyIKICAgICB4bWxuczpvd2w9Imh0dHA6Ly93d3cudzMub3JnLzIwMDIvMDcvb3dsIyIKICAgICB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiCiAgICAgeG1sbnM6eG1sPSJodHRwOi8vd3d3LnczLm9yZy9YTUwvMTk5OC9uYW1lc3BhY2UiCiAgICAgeG1sbnM6eHNkPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSMiCiAgICAgeG1sbnM6cmRmcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wMS9yZGYtc2NoZW1hIyI+CiAgICA8b3dsOk9udG9sb2d5IHJkZjphYm91dD0iaHR0cDovL3d3dy5zZW1hbnRpY3dlYi5vcmcvbmFzcm8vb250b2xvZ2llcy8yMDIzLzMvdW50aXRsZWQtb250b2xvZ3ktMTAiLz4KICAgIAoKCiAgICA8IS0tIAogICAgLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vCiAgICAvLwogICAgLy8gQ2xhc3NlcwogICAgLy8KICAgIC8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLy8vLwogICAgIC0tPgoKICAgIAoKCiAgICA8IS0tIGh0dHA6Ly93d3cuc2VtYW50aWN3ZWIub3JnL25hc3JvL29udG9sb2dpZXMvMjAyMy8zL3VudGl0bGVkLW9udG9sb2d5LTEwI2JveSAtLT4KCiAgICA8b3dsOkNsYXNzIHJkZjphYm91dD0iaHR0cDovL3d3dy5zZW1hbnRpY3dlYi5vcmcvbmFzcm8vb250b2xvZ2llcy8yMDIzLzMvdW50aXRsZWQtb250b2xvZ3ktMTAjYm95Ii8+CiAgICAKCgogICAgPCEtLSBodHRwOi8vd3d3LnNlbWFudGljd2ViLm9yZy9uYXNyby9vbnRvbG9naWVzLzIwMjMvMy91bnRpdGxlZC1vbnRvbG9neS0xMCNib3lfb3Jfc21hbGwgLS0+CgogICAgPG93bDpDbGFzcyByZGY6YWJvdXQ9Imh0dHA6Ly93d3cuc2VtYW50aWN3ZWIub3JnL25hc3JvL29udG9sb2dpZXMvMjAyMy8zL3VudGl0bGVkLW9udG9sb2d5LTEwI2JveV9vcl9zbWFsbCI+CiAgICAgICAgPG93bDplcXVpdmFsZW50Q2xhc3M+CiAgICAgICAgICAgIDxvd2w6Q2xhc3M+CiAgICAgICAgICAgICAgICA8b3dsOnVuaW9uT2YgcmRmOnBhcnNlVHlwZT0iQ29sbGVjdGlvbiI+CiAgICAgICAgICAgICAgICAgICAgPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9Imh0dHA6Ly93d3cuc2VtYW50aWN3ZWIub3JnL25hc3JvL29udG9sb2dpZXMvMjAyMy8zL3VudGl0bGVkLW9udG9sb2d5LTEwI2JveSIvPgogICAgICAgICAgICAgICAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSJodHRwOi8vd3d3LnNlbWFudGljd2ViLm9yZy9uYXNyby9vbnRvbG9naWVzLzIwMjMvMy91bnRpdGxlZC1vbnRvbG9neS0xMCNzbWFsbCIvPgogICAgICAgICAgICAgICAgPC9vd2w6dW5pb25PZj4KICAgICAgICAgICAgPC9vd2w6Q2xhc3M+CiAgICAgICAgPC9vd2w6ZXF1aXZhbGVudENsYXNzPgogICAgPC9vd2w6Q2xhc3M+CiAgICAKCgogICAgPCEtLSBodHRwOi8vd3d3LnNlbWFudGljd2ViLm9yZy9uYXNyby9vbnRvbG9naWVzLzIwMjMvMy91bnRpdGxlZC1vbnRvbG9neS0xMCNzbWFsbCAtLT4KCiAgICA8b3dsOkNsYXNzIHJkZjphYm91dD0iaHR0cDovL3d3dy5zZW1hbnRpY3dlYi5vcmcvbmFzcm8vb250b2xvZ2llcy8yMDIzLzMvdW50aXRsZWQtb250b2xvZ3ktMTAjc21hbGwiLz4KPC9yZGY6UkRGPgoKCgo8IS0tIEdlbmVyYXRlZCBieSB0aGUgT1dMIEFQSSAodmVyc2lvbiA0LjUuMjUuMjAyMy0wMi0xNVQxOToxNTo0OVopIGh0dHBzOi8vZ2l0aHViLmNvbS9vd2xjcy9vd2xhcGkgLS0+Cgo="}
 ```       
----------------

__SWRL Rules's APIS__
| Method Type | API  | Description |
| -------- | -------- | -------- |
| _POST_ | /SWRL/insertRuleReclassification | SWRL Rule for classification : Student(x) →  Person (x) |
| _POST_ | /SWRL/insertRulePropertyVA | SWRL Rule Complex Input :
 Person(x) ^ hasSibling (x,y) ^ man (y) → hasBrother(x,y)
|
Comming soon the rest of APIS

----------------

### Example Of Use For SWRL Part [ api 1]

You open for example Postman ( You don't you know postman ? : [Postman](https://www.postman.com/) )

You choose POST Request , you pass this URL http://localhost:8080/SWRL/insertRuleReclassification

You should pass one parameter of those in this List {url,ontologyContentEncoded64,filePath } 
in the JSON Body 

Lets take an example : 

Student(x) -> Person(x)

if x = Amine and Amine is a student then Amine is a person 
and that's what we infer.

You pick Body -> Raw -> JSON 

```JSON
    {
        "ontologyContentEncoded64" : "PHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIgogICAgICAgICB4bWxuczpvd2w9Imh0dHA6Ly93d3cudzMub3JnLzIwMDIvMDcvb3dsIyIKICAgICAgICAgeG1sbnM6cmRmcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wMS9yZGYtc2NoZW1hIyIKICAgICAgICAgeG1sbnM6eHNkPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSMiCiAgICAgICAgIHhtbG5zPSJodHRwOi8vd3d3LmV4YW1wbGUuY29tL29udG9sb2d5IyIKICAgICAgICAgeG1sOmJhc2U9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kiPgoKICAgIDxvd2w6T250b2xvZ3kgcmRmOmFib3V0PSJodHRwOi8vd3d3LmV4YW1wbGUuY29tL29udG9sb2d5Ii8+CgogICAgPG93bDpDbGFzcyByZGY6YWJvdXQ9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kjUGVyc29uIi8+CiAgICAKICAgIDxvd2w6Q2xhc3MgcmRmOmFib3V0PSJodHRwOi8vd3d3LmV4YW1wbGUuY29tL29udG9sb2d5I1N0dWRlbnQiLz4KCiAgICA8b3dsOk5hbWVkSW5kaXZpZHVhbCByZGY6YWJvdXQ9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kjQW1pbmUiPgogICAgICAgIDxyZGY6dHlwZSByZGY6cmVzb3VyY2U9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kjU3R1ZGVudCIvPgogICAgPC9vd2w6TmFtZWRJbmRpdmlkdWFsPgo8L3JkZjpSREY+Cg==" ,
            "premise":["Student"],
            "conclusion":["Person"]
        
        }
```

----------------
### Example Of Use For SWRL Part [ api 2]

You open for example Postman ( You don't you know postman ? : [Postman](https://www.postman.com/) )

You choose POST Request , you pass this URL http://localhost:8080/SWRL/insertRuleReclassification

You should pass one parameter of those in this List {url,ontologyContentEncoded64,filePath } 
in the JSON Body 

Lets take an example : 

 Person(x) ^ hasSibling (x,y) ^ man (y) → hasBrother(x,y)
x = Amine and y = Anas
if Amine is a person also Amine has a sibling Anas and Anas is a man then Amine has brother called Anas 
and that's what we infer.

You pick Body -> Raw -> JSON 

```JSON
    {
        "ontologyContentEncoded64" : "PHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIgogICAgICAgICB4bWxuczpvd2w9Imh0dHA6Ly93d3cudzMub3JnLzIwMDIvMDcvb3dsIyIKICAgICAgICAgeG1sbnM6cmRmcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wMS9yZGYtc2NoZW1hIyIKICAgICAgICAgeG1sbnM6eHNkPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSMiCiAgICAgICAgIHhtbG5zPSJodHRwOi8vd3d3LmV4YW1wbGUuY29tL29udG9sb2d5IyIKICAgICAgICAgeG1sOmJhc2U9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kiPgoKICAgIDxvd2w6T250b2xvZ3kgcmRmOmFib3V0PSJodHRwOi8vd3d3LmV4YW1wbGUuY29tL29udG9sb2d5Ii8+CgogICAgPG93bDpDbGFzcyByZGY6YWJvdXQ9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kjUGVyc29uIi8+CiAgICAKICAgIDxvd2w6Q2xhc3MgcmRmOmFib3V0PSJodHRwOi8vd3d3LmV4YW1wbGUuY29tL29udG9sb2d5I0h1bWFuIi8+CiAgICAKICAgIDxvd2w6Q2xhc3MgcmRmOmFib3V0PSJodHRwOi8vd3d3LmV4YW1wbGUuY29tL29udG9sb2d5I1N0dWRlbnQiLz4KCiAgICA8b3dsOkNsYXNzIHJkZjphYm91dD0iaHR0cDovL3d3dy5leGFtcGxlLmNvbS9vbnRvbG9neSNNYW4iLz4KCiAgICA8IS0tIE9iamVjdCBwcm9wZXJ0aWVzIC0tPgogICAgPG93bDpPYmplY3RQcm9wZXJ0eSByZGY6YWJvdXQ9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kjaGFzU2libGluZyIvPgogICAgPG93bDpPYmplY3RQcm9wZXJ0eSByZGY6YWJvdXQ9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kjaGFzQnJvdGhlciIvPgogICAgPCEtLSBJbmRpdmlkdWFscyAtLT4KICAgIDxvd2w6TmFtZWRJbmRpdmlkdWFsIHJkZjphYm91dD0iaHR0cDovL3d3dy5leGFtcGxlLmNvbS9vbnRvbG9neSNBbWluZSI+CiAgICAgICAgPHJkZjp0eXBlIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy5leGFtcGxlLmNvbS9vbnRvbG9neSNQZXJzb24iLz4KICAgIDwvb3dsOk5hbWVkSW5kaXZpZHVhbD4KCiAgICA8b3dsOk5hbWVkSW5kaXZpZHVhbCByZGY6YWJvdXQ9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kjQW5hcyI+CiAgICAgICAgPHJkZjp0eXBlIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy5leGFtcGxlLmNvbS9vbnRvbG9neSNNYW4iLz4KICAgIDwvb3dsOk5hbWVkSW5kaXZpZHVhbD4KCiAgICA8b3dsOk5hbWVkSW5kaXZpZHVhbCByZGY6YWJvdXQ9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kjQW1pbmUiPgogICAgICAgIDxyZGY6dHlwZSByZGY6cmVzb3VyY2U9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kjUGVyc29uIi8+CiAgICAgICAgPGhhc1NpYmxpbmcgcmRmOnJlc291cmNlPSJodHRwOi8vd3d3LmV4YW1wbGUuY29tL29udG9sb2d5I0FuYXMiLz4KICAgIDwvb3dsOk5hbWVkSW5kaXZpZHVhbD4KICAgIAo8L3JkZjpSREY+Cg==" ,
"premise":[
    {
        "type": "class",
        "entities": [
            {
                "name": "Person",
                "var": ["A"]
            },
            {
                "name": "Man",
                "var": ["Y"]
            }
        ]
    },
    {
        "type": "objectProperty",
        "entities": [
            {
                "name": "hasSibling",
                "var": ["A", "Y"]
            }
        ]
    }
],
"conclusion":[
    {
        "type": "objectProperty",
        "entities": [
            {
                "name": "hasBrother",
                "var": ["A", "Y"]
            }
        ]
    }
]
            
}
```

----------------
### Useful encoders to encode the TEXT

[ENOCDER-64](https://codebeautify.org/xml-to-base64-converter#)