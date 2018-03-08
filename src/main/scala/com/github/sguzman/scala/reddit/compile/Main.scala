package com.github.sguzman.scala.reddit.compile

import java.net.URL

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList

import scalaj.http.Http

object Main {
  object User {
    def http(u: URL) =
      Http(u.toString)
        .asString

    def doc(str: String) = JsoupBrowser().parseString(str)

    def scrapeUsers(u: URL) = doc(http(u).body)
      .>>(elementList("a[href]"))
      .map(_.attr("href"))
      .filter(_.startsWith("https://www.reddit.com/user/"))
      .map(_.stripPrefix("https://www.reddit.com/user/"))
  }

  object All {
    def http(cookie: String) =
      Http("https://www.reddit.com/r/all/")
      .header("Cookie", cookie)
      .asString

    def scrapeAll(cookie: String) = Main.User.doc(http(cookie).body)
      .>>(elementList("li.first > a[href]"))
      .map(_.attr("href"))
  }

  def main(args: Array[String]): Unit = {
    val cookie = Config().parse(args, Cmd()).get.cookie
    All.scrapeAll(cookie)
      .par
      .map(new URL(_))
      .flatMap(User.scrapeUsers)
      .toList
      .sorted
      .groupBy(t => t)
      .toList
      .sortBy(_._2.length) foreach println
  }
}
