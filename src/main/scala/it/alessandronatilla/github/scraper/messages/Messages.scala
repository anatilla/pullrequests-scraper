package it.alessandronatilla.github.scraper.messages

/**
  * Created by alexander on 08/03/16.
  */
object Messages {

  case class Repository(url: String)

  case class PullRequestsIndexPage(url: String, repo: String)

  case class PullRequest(url: String)

  case class PullRequestTotalNumber(num: Int)

  object ScrapedPage


}
