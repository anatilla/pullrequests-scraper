# pullrequests-scraper
a Github pull requests comments scraper based on Akka

# dependencies
- mongodb >= 3.2.3
- sbt >= 0.13.11
- scala 2.11

# usage

First, edit application.conf file located in src/main/resources, inserting mongodb access data.

Then execute, from console, typing:

    $ sbt "run <github repository url>"

example:

    $ sbt "run https://github.com/typesafehub/config/"


