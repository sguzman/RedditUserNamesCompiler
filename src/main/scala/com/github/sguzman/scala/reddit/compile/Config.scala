package com.github.sguzman.scala.reddit.compile

case class Config(title: String = "RedditUserNamesCompiler") extends scopt.OptionParser[Cmd](title) {
  head(title, "1.0")

  opt[String]('u', "cookie")
    .text("COOKIE")
    .required()
    .valueName("<cookie>")
    .minOccurs(1)
    .maxOccurs(1)
    .action((x, c) => c.copy(cookie = x))

  help("help")
    .text("this menu")
}