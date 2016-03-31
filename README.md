# pullrequests-scraper
a Github pull request comments scraper based on Akka

# dependencies
- mongodb >= 3.2.3
- sbt >= 0.13.11
- scala 2.11

# usage

First, edit application.conf file located in src/main/resources, inserting mongodb access data.

Then execute, from console, typing:

    $ sbt "run <https://github.com/OWNER/REPONAME>"

example:

    $ sbt "run https://github.com/typesafehub/config/"


All scraped data will be available in the MongoDB instance (DB: "github-pull-requests", collection will have this format: OWNER_REPONAME).
