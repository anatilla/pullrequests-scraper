package it.alessandronatilla.github.scraper

import akka.actor.{ActorSystem, Props}
import com.typesafe.scalalogging.Logger
import it.alessandronatilla.github.scraper.messages.Messages
import it.alessandronatilla.github.scraper.actors.TerminatorActor
import Messages.Repository
import it.alessandronatilla.github.scraper.actors.{ClosedPullReqsActor, OpenPullReqsActor}
import org.slf4j.LoggerFactory

/**
  * Created by alexander on 08/03/16.
  */
object Main {

  def main(args: Array[String]) {

    val logger = Logger(LoggerFactory.getLogger("github-pr-robber"))

    logger info "starting actor system"

    var url = args(0)
    if (!url.endsWith("/")) url = (url + "/")

    val system = ActorSystem("pull-request-scraper")
    val closedPRs = system.actorOf(Props(classOf[ClosedPullReqsActor]))
    val openPRs = system.actorOf(Props(classOf[OpenPullReqsActor]))
    val terminator = system.actorOf(Props(classOf[TerminatorActor]), name = "terminator")

    closedPRs ! Repository(url)
    logger info "sent " + Repository(url) + " to " + closedPRs
    openPRs ! Repository(url)
    logger info "sent " + Repository(url) + " to " + openPRs

  }


}
