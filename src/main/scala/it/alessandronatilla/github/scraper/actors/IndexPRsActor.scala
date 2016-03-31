package it.alessandronatilla.github.scraper.actors

import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive
import akka.routing.RoundRobinPool
import com.typesafe.scalalogging.Logger
import it.alessandronatilla.github.scraper.messages.Messages
import Messages.{PullRequest, PullRequestTotalNumber, PullRequestsIndexPage}
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory


/**
  * Created by alexander on 08/03/16.
  */
class IndexPRsActor extends Actor {
  val logger = Logger(LoggerFactory.getLogger("github-pr-robber"))

  override def receive: Receive = {
    case PullRequestsIndexPage(pageUrl, repo) =>

      val browser = new Browser
      val doc = browser.get(pageUrl)

      //ottengo i link (non assoluti )delle singole PR
      val links: List[String] = doc >> elementList(".issue-title-link") >> attr("href")("a")
      val fullLinks = links.map(link => "https://github.com" + link)
      val terminatorRef = this.context.actorSelection("/user/terminator")

      logger info "Sending " + fullLinks.size + " to terminator. Ref:" + terminatorRef
      terminatorRef ! PullRequestTotalNumber(fullLinks.size)

      //fullLinks foreach println
      logger.info("PAGE=" + pageUrl + ", #links=" + fullLinks.size)
      val router = context.actorOf(Props[PullRequestScrapeActor].withRouter(RoundRobinPool(links.size)))

      for (link <- fullLinks) {
        router ! PullRequest(link)
      }
  }
}
