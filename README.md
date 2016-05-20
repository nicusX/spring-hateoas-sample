# spring-hateoas-sample

A working Spring Boot + Spring HATEOAS application implementing a hypermedia API supporting HAL <http://stateless.co/hal_specification.html>

Look at the related blog post: [Implementing HAL hypermedia REST API using Spring HATEOAS](https://opencredo.com/hal-hypermedia-api-spring-hateoas/)

## Installation
* Ensure that Java 8 and Maven 3.2 are installed
* Clone the repo"
    `git clone https://nicus@bitbucket.org/nicus/spring-hateoas-sample.git`
    
## Usage
### Running the Spring Boot app
Navigate to the directory into which you cloned the repo and execute this:
  `mvn spring-boot:run`    
  
Once started you can access the APIs on port 8080, e.g.
  `http://localhost:8080/books`  
  
The port number can be changed by editing the port property in `src/main/resources/application.yml`  
  
### Load sample data
By default, the application database starts empty. To have some sample data loaded at start specify the Spring bean profile "demohateoas.loadsampledata"
    `mvn spring-boot:run -Dspring.profiles.active=sampledata`

## License

Copyright (c) 2015 Open Credo Ltd, Licensed under MIT License 

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.