package it.alessandronatilla.github.scraper.actors

import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive
import akka.routing.RoundRobinPool
import com.typesafe.scalalogging.Logger
import it.alessandronatilla.github.scraper.messages.Messages
import Messages.{PullRequestsIndexPage, Repository}
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory

/**
  * Created by alexander on 08/03/16.
  */
class ClosedPullReqsActor extends Actor {

  val logger = Logger(LoggerFactory.getLogger("github-pr-robber"))


  override def receive: Receive = {
    case Repository(url) =>

      logger info "received message" + Repository(url) + " from " + this.sender()

      val pullRequestsUrl = url + "pulls?q=is:closed is:pr"
      val browser = new Browser
      val docRepo = browser.get(pullRequestsUrl)
      val closedPullRequestsIdx = docRepo >> elementList(".pagination")
      val listPages = closedPullRequestsIdx >> elementList("a")
      val items = listPages map (_ >> text("a")) flatten
      var pages = -1

      try {
        pages = items.filterNot(_ == "Next").last.toInt
      } catch {

        case _: NoSuchElementException => {
          logger.info("CATCHED EXCEPTION, 0 pages available")
          pages = 1
        }
      }

      val router = context.actorOf(Props[IndexPRsActor].withRouter(RoundRobinPool(pages)))

      if (pages != 0) {
        for (page <- 1 to pages) {
          val pageUrl = pullRequestsUrl + "&page=" + page
          logger info "Sending " + PullRequestsIndexPage(pageUrl, url) + " to " + router

          router ! PullRequestsIndexPage(pageUrl, url)
        }
      }
  }

}
