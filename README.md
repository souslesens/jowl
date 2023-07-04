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
docker run -p 9170:9170 jowl
```
----------------
### To Run The Application in the background
```
docker run -d -p 9170:9170 jowl
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

__Base URL__ : http://localhost:9170


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

You choose POST Request , you pass this URL http://localhost:9170/reasoner/inference

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

You choose POST Request , you pass this URL http://localhost:9170/jena/rdftriple

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
| _POST_ | /swrl/alternative_exec_rule | SWRL Rule for classification : Student(x) ->  Person (x) |
| _POST_ | /swrl/exec_rule | SWRL Rule Complex Input :Person(x) ^ hasSibling (x,y) ^ man (y) -> hasBrother(x,y)|


----------------

### Example Of Use For SWRL Part [ api 1]

You open for example Postman ( You don't you know postman ? : [Postman](https://www.postman.com/) )

You choose POST Request , you pass this URL http://localhost:9170/swrl/alternative_exec_rule

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
            "premise":["http://www.example.com/ontology#Student"],
            "conclusion":["http://www.example.com/ontology#Person"]
        
        }
```

----------------
### Example Of Use For SWRL Part [ api 2]

You open for example Postman ( You don't you know postman ? : [Postman](https://www.postman.com/) )

You choose POST Request , you pass this URL http://localhost:9170/swrl/exec_rule

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
        "type": "owl:Class",
        "entities": [
            {
                "name": "http://www.example.com/ontology#Person",
                "var": ["A"]
            },
            {
                "name": "http://www.example.com/ontology#Man",
                "var": ["Y"]
            }
        ]
    },
    {
        "type": "owl:ObjectProperty",
        "entities": [
            {
                "name": "http://www.example.com/ontology#hasSibling",
                "var": ["A", "Y"]
            }
        ]
    }
],
"conclusion":[
    {
        "type": "owl:ObjectProperty",
        "entities": [
            {
                "name": "http://www.example.com/ontology#hasBrother",
                "var": ["A", "Y"]
            }
        ]
    }
]
            

}
```

----------------
### Example Of Use For SWRL Part [ SWRL BUILT IN For compare features  api 3]

You open for example Postman ( You don't you know postman ? : [Postman](https://www.postman.com/) )

You choose POST Request , you pass this URL http://localhost:9170/swrl/exec_rule

You should pass one parameter of those in this List {url,ontologyContentEncoded64,filePath } 
in the JSON Body 

Lets take an example : 

Person(x) ^ hasAge (x,Age) ^ swrlb:greaterThanOrEqual (18) → Adult(x)

x = Amine
if Amine is a person and has an age and his age is greater than 18 then he's Adult
and that's what we infer.

You pick Body -> Raw -> JSON 

```JSON
    {
        "ontologyContentEncoded64" : "PHJkZjpSREYKICAgIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyIKICAgIHhtbG5zOm93bD0iaHR0cDovL3d3dy53My5vcmcvMjAwMi8wNy9vd2wjIgogICAgeG1sbnM6ZXg9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20jIgogICAgeG1sbnM6eHNkPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSMiCiAgICB4bWxuczpyZGZzPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzAxL3JkZi1zY2hlbWEjIgogICAgeG1sbnM6c3dybD0iaHR0cDovL3d3dy53My5vcmcvMjAwMy8xMS9zd3JsIyIKICAgIHhtbG5zOnN3cmxiPSJodHRwOi8vd3d3LnczLm9yZy8yMDAzLzExL3N3cmxiIyI+CgogICAgPG93bDpPbnRvbG9neSByZGY6YWJvdXQ9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20iLz4KCiAgICA8b3dsOkNsYXNzIHJkZjphYm91dD0iaHR0cDovL3d3dy5leGFtcGxlLmNvbSNQZXJzb24iLz4KICAgIDxvd2w6Q2xhc3MgcmRmOmFib3V0PSJodHRwOi8vd3d3LmV4YW1wbGUuY29tI0FkdWx0Ii8+CgogICAgPG93bDpEYXRhUHJvcGVydHkgcmRmOmFib3V0PSJodHRwOi8vd3d3LmV4YW1wbGUuY29tI2hhc0FnZSI+CiAgICAgICAgPHJkZnM6cmFuZ2UgcmRmOnJlc291cmNlPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSNpbnRlZ2VyIi8+CiAgICAgICAgPHJkZnM6ZG9tYWluIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy5leGFtcGxlLmNvbSNQZXJzb24iLz4KICAgIDwvb3dsOkRhdGFQcm9wZXJ0eT4KCiAgICA8b3dsOk5hbWVkSW5kaXZpZHVhbCByZGY6YWJvdXQ9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20jQW5hcyI+CiAgICAgICAgPHJkZjp0eXBlIHJkZjpyZXNvdXJjZT0iaHR0cDovL3d3dy5leGFtcGxlLmNvbSNQZXJzb24iLz4KICAgICAgICA8ZXg6aGFzQWdlIHJkZjpkYXRhdHlwZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjaW50Ij4yNzwvZXg6aGFzQWdlPgogICAgPC9vd2w6TmFtZWRJbmRpdmlkdWFsPgoKICAgIDxvd2w6TmFtZWRJbmRpdmlkdWFsIHJkZjphYm91dD0iaHR0cDovL3d3dy5leGFtcGxlLmNvbSNMb3VqZWluIj4KICAgICAgICA8cmRmOnR5cGUgcmRmOnJlc291cmNlPSJodHRwOi8vd3d3LmV4YW1wbGUuY29tI1BlcnNvbiIvPgogICAgICAgIDxleDpoYXNBZ2UgcmRmOmRhdGF0eXBlPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSNpbnQiPjE0PC9leDpoYXNBZ2U+CiAgICA8L293bDpOYW1lZEluZGl2aWR1YWw+CgogICAgPG93bDpOYW1lZEluZGl2aWR1YWwgcmRmOmFib3V0PSJodHRwOi8vd3d3LmV4YW1wbGUuY29tI0FtaW5lIj4KICAgICAgICA8cmRmOnR5cGUgcmRmOnJlc291cmNlPSJodHRwOi8vd3d3LmV4YW1wbGUuY29tI1BlcnNvbiIvPgogICAgICAgIDxleDpoYXNBZ2UgcmRmOmRhdGF0eXBlPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSNpbnQiPjI0PC9leDpoYXNBZ2U+CiAgICA8L293bDpOYW1lZEluZGl2aWR1YWw+CgogICAgPG93bDpOYW1lZEluZGl2aWR1YWwgcmRmOmFib3V0PSJodHRwOi8vd3d3LmV4YW1wbGUuY29tI1JhbmlhIj4KICAgICAgICA8cmRmOnR5cGUgcmRmOnJlc291cmNlPSJodHRwOi8vd3d3LmV4YW1wbGUuY29tI1BlcnNvbiIvPgogICAgICAgIDxleDpoYXNBZ2UgcmRmOmRhdGF0eXBlPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSNpbnQiPjMwPC9leDpoYXNBZ2U+CiAgICA8L293bDpOYW1lZEluZGl2aWR1YWw+CjwvcmRmOlJERj4K" ,
"premise":[
    {
        "type": "owl:Class",
        "entities": [
            {
                "name": "http://www.example.com/ontology#Person",
                "var": ["X"]
            }
        ]
    },
    {
        "type": "owl:DataProperty",
        "entities": [
            {
                "name": "http://www.example.com#hasAge",
                "var": ["X", "age"]
            }
        ]
    },{
        "type":"swrlb:compare",
        "entities":
        [
            {
                "name": "greaterThanOrEqual",
                "var": ["age"],
                "literal":["18"]
            }
        ]
    }
],
"conclusion":[
    {
        "type": "owl:Class",
        "entities": [
            {
                "name": "http://www.example.com#Adult",
                "var": ["X"]
            }
        ]
    }
]
            

        }
```

----------------
### Example Of Use For SWRL Part [ SWRL BUILT IN For String features  api 4]

You open for example Postman ( You don't you know postman ? : [Postman](https://www.postman.com/) )

You choose POST Request , you pass this URL http://localhost:9170/swrl/exec_rule

You should pass one parameter of those in this List {url,ontologyContentEncoded64,filePath } 
in the JSON Body 

Lets take an example : 

Student(y) ^ hasName (y,name) ^ swrlb:endsWith (name,"s") → Person(x)

If hes student and has a name ends with S then it will be returned as a person 

You pick Body -> Raw -> JSON 

```JSON
    {
        "ontologyContentEncoded64" : "PHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIgogICAgICAgICB4bWxuczpvd2w9Imh0dHA6Ly93d3cudzMub3JnLzIwMDIvMDcvb3dsIyIKICAgICAgICAgeG1sbnM6cmRmcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wMS9yZGYtc2NoZW1hIyIKICAgICAgICAgeG1sbnM6eHNkPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSMiCiAgICAgICAgIHhtbG5zPSJodHRwOi8vd3d3LmV4YW1wbGUuY29tL29udG9sb2d5IyIKICAgICAgICAgeG1sOmJhc2U9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kiPgoKICAgIDxvd2w6T250b2xvZ3kgcmRmOmFib3V0PSJodHRwOi8vd3d3LmV4YW1wbGUuY29tL29udG9sb2d5Ii8+CgogICAgPG93bDpDbGFzcyByZGY6YWJvdXQ9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kjUGVyc29uIi8+CgogICAgPG93bDpDbGFzcyByZGY6YWJvdXQ9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kjU3R1ZGVudCIvPgoKICAgIDxvd2w6RGF0YXR5cGVQcm9wZXJ0eSByZGY6YWJvdXQ9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kjaGFzTmFtZSI+CiAgICAgICAgPHJkZnM6cmFuZ2UgcmRmOnJlc291cmNlPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSNzdHJpbmciLz4KICAgIDwvb3dsOkRhdGF0eXBlUHJvcGVydHk+CgogICAgPG93bDpOYW1lZEluZGl2aWR1YWwgcmRmOmFib3V0PSJodHRwOi8vd3d3LmV4YW1wbGUuY29tL29udG9sb2d5I0FtaW5lIj4KICAgICAgICA8cmRmOnR5cGUgcmRmOnJlc291cmNlPSJodHRwOi8vd3d3LmV4YW1wbGUuY29tL29udG9sb2d5I1N0dWRlbnQiLz4KICAgICAgICA8aGFzTmFtZSByZGY6ZGF0YXR5cGU9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hI3N0cmluZyI+QW1pbmU8L2hhc05hbWU+CiAgICA8L293bDpOYW1lZEluZGl2aWR1YWw+CgogICAgPG93bDpOYW1lZEluZGl2aWR1YWwgcmRmOmFib3V0PSJodHRwOi8vd3d3LmV4YW1wbGUuY29tL29udG9sb2d5I0FuYXMiPgogICAgICAgIDxyZGY6dHlwZSByZGY6cmVzb3VyY2U9Imh0dHA6Ly93d3cuZXhhbXBsZS5jb20vb250b2xvZ3kjU3R1ZGVudCIvPgogICAgICAgIDxoYXNOYW1lIHJkZjpkYXRhdHlwZT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEjc3RyaW5nIj5BbmFzPC9oYXNOYW1lPgogICAgPC9vd2w6TmFtZWRJbmRpdmlkdWFsPgoKPC9yZGY6UkRGPgo=" ,
            "premise":[
    {
        "type": "owl:Class",
        "entities": [
            {
                "name": "http://www.example.com/ontology#Student",
                "var": ["Y"]
            }
        ]
    },
        {
        "type": "owl:DataProperty",
        "entities": [
            {
                "name": "http://www.example.com/ontology#hasName",
                "var": ["Y", "name"]
            }
        ]
    },
    {
        "type":"swrlb:string",
        "entities":
        [
            {
                "name": "endsWith",
                "var": ["name"],
                "literal":["s"]
            }
        ]
    }
],
            "conclusion":[
    {
        "type": "owl:Class",
        "entities": [
            {
                "name": "http://www.example.com/ontology#Person",
                "var": ["Y"]
            }
        ]
    }
]
            

        }
```

----------------
__RML Mapping's APIS__
| Method Type | API  | Description |
| -------- | -------- | -------- |
| _POST_ | /rml/mapping | Transforming multiple data sources into a RDF model in TURTLE format using provided RML mappings. |
| _POST_ | /rml/validateRML | Validating provided RML syntax and reporting any encountered errors or inconsistencies. |
----------------
### Example Of Use For Mapping Part
You open for example Postman.  If you are not familiar with Postman, you can download it from [Postman](https://www.postman.com/) )

Copy and paste the URL http://localhost:9170/rml/mapping into the URL field.

In your request payload, you should pass two key parameters: 'rml' and 'sources'.

The 'rml' parameter accepts a string, which represents your RML mapping configuration.

The 'sources' parameter accepts an array of one or more source objects. Each source object must contain three properties: 'fileName', 'data', and 'format'. 'fileName' is the name to be used for the data source, 'data' contains the base64 encoded data, and 'format' indicates the format of the data.

You pick Body -> Raw -> JSON 

```JSON
{  
    {
"rml": "@prefix rr: <http://www.w3.org/ns/r2rml#>.\n@prefix rml: <http://semweb.mmlab.be/ns/rml#>.\n@prefix ql: <http://semweb.mmlab.be/ns/ql#>.\n@prefix transit: <http://vocab.org/transit/terms/>.\n@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.\n@prefix wgs84_pos: <http://www.w3.org/2003/01/geo/wgs84_pos#>.\n@base <http://example.com/ns#>.\n\n<#AirportMapping> a rr:TriplesMap;\n  rml:logicalSource [\n    rml:source \"temp-files/mapping/Airport.csv\" ;\n    rml:referenceFormulation ql:CSV\n  ];\n  rr:subjectMap [\n    rr:template \"http://airport.example.com/{id}\";\n    rr:class transit:Stop\n  ];\n\n  rr:predicateObjectMap [\n    rr:predicate transit:route;\n    rr:objectMap [\n      rml:reference \"stop\";\n      rr:datatype xsd:int\n      ]\n    ];\n\n  rr:predicateObjectMap [\n    rr:predicate wgs84_pos:lat;\n    rr:objectMap [\n      rml:reference \"latitude\"\n    ]\n  ];\n\n  rr:predicateObjectMap [\n    rr:predicate wgs84_pos:long;\n    rr:objectMap [\n      rml:reference \"longitude\"\n    ]\n  ].",

  "sources": [
    {
      "format": "csv",
      "fileName": "Airport",
      "data": "PHRyYW5zcG9ydD4KICAgIDxidXMgaWQ9IjI1Ij4KICAgICAgICA8cm91dGU+CiAgICAgICAgICAgIDxzdG9wIGlkPSI2NDUiPkludGVybmF0aW9uYWwgQWlycG9ydDwvc3RvcD4KICAgICAgICAgICAgPHN0b3AgaWQ9IjY1MSI+Q29uZmVyZW5jZSBjZW50ZXI8L3N0b3A+CiAgICAgICAgPC9yb3V0ZT4KICAgIDwvYnVzPgo8L3RyYW5zcG9ydD4K"
    },
      {
      "format": "csv",
      "fileName": "Airport",
      "data": "aWQsc3RvcCxsb25naXR1ZGUsbGF0aXR1ZGUKNjUyMywyNSw1MC45MDEzODksNC40ODQ0NDQ="
    }
  ]
}
```

Click the "Send" button on the top right corner of the Postman window.

The API should return a response based on the provided RML mappings. If you see any errors, check if the data are correctly encoded in base64 and that they are valid RML mappings.

**note** 
Please note that the data source referenced in your RML mapping should follow a relative path structure, namely: 'temp-files/mapping/dataFile.format'. Make sure to replace 'dataFile.format' with your corresponding file name and format. For example, if you have a CSV file named 'Airport.csv', your RML mapping should include the path 'temp-files/mapping/Airport.csv'.


### Useful encoders to encode the TEXT

[ENOCDER-64](https://codebeautify.org/xml-to-base64-converter#)

### Useful Link for BuiltIn Function 
[Documentation](http://www.daml.org/swrl/proposal/builtins.html)
