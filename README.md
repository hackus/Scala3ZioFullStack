# Create scala fullstack project using Scala3, ScalaJs, ZIO

## Building the project from scratch
________________
### Server
    1. sbt new scala/scala3.g8
    2. mkdir server
    3. mv src server/
________________
### Client
    npm create vite@4.1.0
    1. -> client
    2. -> vanilla
    3. -> javascript
### Deploy locally
    cd client
    npm install
    npm run dev
### Install scala-js plugin
    cd client
    npm install -D @scala-js/vite-plugin-scalajs@1.0.0
### Install scalably-typed plugin
I Used [ScalaJs-ScalablyTyped](#ScalaJs-ScalablyTyped), I am not 100% sure they are both needed 
however I blindly installed these plugins, will check later if any of them can be omitted

    npm install -S chart.js@2.9.4
    npm install -D @types/chart.js@2.9.29 typescript@4.9.5
________________
## Implementation
________________
### build.sbt
Have used [Medium-ScalaFullstackWebApp](#Medium-ScalaFullstackWebApp) example and
partially [RockTheJvm-ZioFullstackWebApp](#RockTheJvm-ZioFullstackWebApp)

    Create common dir to keep shared code between server and client 
        uphold by -common- in build.sbt    
    Configure multimodule project in build.sbt
        uphold by -root- in build.sbt    
    Configure server
        uphold by -server- in build.sbt
    Configure client
        uphold by -client- in build.sbt

One thing I noticed the way [RockTheJvm-ZioFullstackWebApp](#RockTheJvm-ZioFullstackWebApp) is 
configuring the common is most suitable to the way I want it to be the result is clean and easy 
to understand, try the example from [Medium-ScalaFullstackWebApp](#Medium-ScalaFullstackWebApp) 
to observe the difference.

A very good thing I learned from [IdiomaticSoft](#IdiomaticSoft) was the aggregate function in
multi-module projects.
________________
plugins.sbt

    Add the required plugins.
________________
## Run
________________
* Important

      Start from the client because, when server is started first, client does not start.
* Run Client

      cd client  
      npm run dev
* Run Server    
    To publish use ```sbt publishlocal``` but it is not required
        
      sbt server/run
* Refresh Client
________________

# References:

##### [Medium-ScalaFullstackWebApp](https://medium.com/@samuelfm_amanoe/scala-fullstack-web-application-01a565a3c947)

##### [RockTheJvm-ZioFullstackWebApp](https://rockthejvm.com/articles/zio-full-stack-webapp)

##### [IdiomaticSoft](https://idiomaticsoft.com/post/2023-12-12-fullstack/)

##### [ScalaJs-ScalablyTyped](https://www.scala-js.org/doc/tutorial/scalablytyped.html)

