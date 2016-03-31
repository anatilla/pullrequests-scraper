package it.alessandronatilla.github.scraper.actors

import java.util.NoSuchElementException

import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive
import com.typesafe.scalalogging.Logger
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import com.mongodb.casbah.Imports._
import it.alessandronatilla.github.db.DB
import it.alessandronatilla.github.scraper.messages.Messages.{PullRequest, ScrapedPage}


/**
  * Created by alexander on 08/03/16.
  */
class PullRequestScrapeActor extends Actor {

  val logger = Logger(LoggerFactory.getLogger("github-pr-robber"))

  override def receive: Receive = {
    case PullRequest(url) =>

      val collectionOwner = url.split("/")(3)
      val collectionName = url.split("/")(4)
      val collection = collectionOwner + "_" + collectionName
      DB.init(collection)

      val terminatorRef = this.context.actorSelection("/user/terminator")

      val browser = new Browser
      val doc = browser.get(url)

      val title = doc >> text(".js-issue-title")
      val number = doc >> text(".gh-header-number")
      val state = doc >> text(".state")
      val purpose = doc >> text(".flex-table-item-primary")
      val labels = doc >> elementList(".labels").map(_ >> text(".labels"))
      val milestone = doc >> elementList(".sidebar-milestone").map(_ >> text(".sidebar-milestone"))
      val participantList = doc >> elementList(".participant-avatar") >> elementList("a") flatten
      val participants = participantList map (_.attr("href").replaceAll("/", ""))
      val assignee = doc >> elementList(".sidebar-assignee").map(_ >> text(".sidebar-assignee"))
      val conversation = doc >> texts(".js-discussion")
      val stats = doc >> text(".tabnav-tabs")

      val diffStats = doc >> allText(".diffstat")
      var addedLines = ""
      var deletedLines = ""

      try {
        addedLines = diffStats.split(" ").head
        deletedLines = diffStats.split(" ").tail.head
      } catch {
        case e: NoSuchElementException => logger error "CANNOT SCRAPE ADDEDLINES/DELETEDLINES. SETTING THEM TO 0. PR URL=" + url
      }


      val comments = doc >> elementList(".timeline-comment-wrapper").map(_ >> text(".timeline-comment-wrapper"))
      val discussionItems = doc >> elementList(".discussion-item").map(_ >> text(".discussion-item"))

      logger info ("scraped PR with url=" + url)

      DB.persist(
        MongoDBObject("title" -> title) ++
          ("number" -> number) ++
          ("state" -> state) ++
          ("purpose" -> purpose) ++
          ("labels" -> labels) ++
          ("milestone" -> milestone) ++
          ("participants" -> participants) ++
          ("assignee" -> assignee) ++
          ("stats" -> stats) ++
          ("addedLines" -> addedLines) ++
          ("deletedLines" -> deletedLines) ++
          ("conversation" -> conversation) ++
          ("comments" -> comments) ++
          ("discussionItems" -> discussionItems)
      )
      logger info ("persisted PR with url=" + url)
      terminatorRef ! ScrapedPage

  }

}
