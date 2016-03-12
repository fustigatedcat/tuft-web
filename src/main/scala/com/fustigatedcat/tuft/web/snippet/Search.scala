package com.fustigatedcat.tuft.web.snippet

import com.fustigatedcat.tuft.web.model.User
import net.liftweb.common.Full
import net.liftweb.http.S
import net.liftweb.util._
import Helpers._

import scala.xml.NodeSeq

object Search {

  def searchResults : CssSel = S.param("search_value") match {
    case Full(searchValue) => {
      if(searchValue.startsWith("//")) {
        ".search-result" #> User.searchForUser(searchValue.split(" ")(0).drop(2)).map(user => {
          "a [href]" #> s"/profiles/${user.id}" &
            ".first-name" #> user.firstName &
            ".last-name" #> user.lastName
        }) &
          ".search-error" #> NodeSeq.Empty
      } else {
        ".error-message" #> "Search for a user by username ex. //username"
      }
    }
    case _ => ".error-message" #> "Missing search value"
  }

}
