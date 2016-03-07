package com.fustigatedcat.tuft.ui.snippet

import com.fustigatedcat.tuft.ui.model.{LoggedInUser, LoggedInUserId, User}
import net.liftweb.common.Full
import net.liftweb.http.S
import net.liftweb.util._
import Helpers._

object Authentication {

  def login = if(S.post_?) {
    (S.param("email"), S.param("password")) match {
      case (Full(email), Full(pass)) => User.authenticateUser(email, pass) match {
        case Some(user) => {
          LoggedInUserId(Some(user.id))
          S.redirectTo("/")
        }
        case _ => ".error *" #> "Invalid Username/Password"
      }
      case _ => ".error *" #> "Username/Password is required"
    }
  } else {
    "*" #> ""
  }

  def logout = {
    LoggedInUserId(None)
    LoggedInUser(None)
    S.redirectTo("/")
  }

}
