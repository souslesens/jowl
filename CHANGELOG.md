# Changelog

All notable changes to this project will be documented in this file.
## [unreleased]

### 🐛 Bug Fixes

- Use credentials from config

### 📚 Documentation

- Rewrite setup section to use env-var configuration

### 🚜 Refactor

- *(config)* Externalize Virtuoso config via Spring Boot binding

### 🎨 Styling

- Apply google-java-format formatting

### ⚙️ Miscellaneous Tasks

- *(Dockerfile)* Replace deprecated openjdk with amazoncorretto
- Add test and lint workflow
- Add docker image build and push workflow

### 💼 Other

- Matches
- StringEqualIgnoreCase
- UpperCase / swrlb:LowerCase / swrlb:StringLength
- StringConcat First attempt
- NormalizeSpace
- SubString
- Tokenize
- SubstringBefore / swrlb:substringAfter
- Replace
- Translate / Rules for String Covered
- *(docker)* Refactor image build and prune dependencies
- *(docker)* Default Virtuoso credentials and endpoint in compose
- Add spotless google-java-format plugin
