package it.alessandronatilla.github.scraper.actors

import akka.actor.Actor
import com.typesafe.scalalogging.Logger
import it.alessandronatilla.github.db.DB
import it.alessandronatilla.github.scraper.messages.Messages
import Messages.{PullRequestTotalNumber, ScrapedPage}
import org.slf4j.LoggerFactory

/**
  * Created by alexander on 23/03/16.
  */
class TerminatorActor extends Actor {

  private val logger = Logger(LoggerFactory.getLogger("github-pr-robber"))
  private var numPullRequests: Int = 0
  private var counter: Int = 0

  override def receive: Receive = {
    case PullRequestTotalNumber(num) => {
      numPullRequests += num
      logger info ("received #" + num + " from " + sender() + ". Actually there are " + this.numPullRequests + " totally.")
    }
    case ScrapedPage => {
      logger info "terminated #" + counter + " scraping actions."
      counter += 1
      if (counter == numPullRequests) {
        logger info "Work done. Terminating. Bye bye ;)"
        DB.close()
        this.context.system.terminate()
      }
    }

  }


}
