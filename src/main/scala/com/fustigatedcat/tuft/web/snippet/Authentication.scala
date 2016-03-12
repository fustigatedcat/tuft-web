package com.fustigatedcat.tuft.web.snippet

import com.fustigatedcat.tuft.web.model.User
import net.liftweb.http.{SessionVar, RequestVar, S}
import net.liftweb.util._
import Helpers._

import scala.xml.NodeSeq

object LoggedInUser extends RequestVar[Option[User]](None)

object LoggedInUserId extends SessionVar[Option[Long]](None)

object Authentication {

  def auth : CssSel = if(S.post_?) {
    User.authenticate(
      S.param("username").openOrThrowException("Param username is missing"),
      S.param("password").openOrThrowException("Param password is missing")
    ) match {
      case Some(user) => {
        LoggedInUserId(Some(user.id))
        LoggedInUser(Some(user))
        S.redirectTo("/")
      }
      case _ => "#nadda" #> NodeSeq.Empty
    }
  } else {
    "*" #> NodeSeq.Empty
  }

  def logout : CssSel = {
    LoggedInUser(None)
    LoggedInUserId(None)
    S.session.foreach(_.destroySession())
    S.redirectTo("/")
  }

}
