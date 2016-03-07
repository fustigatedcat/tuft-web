package com.fustigatedcat.tuft.ui.snippet

import com.fustigatedcat.tuft.ui.model._
import net.liftweb.http.js.JE.{JsNull, Str, JsVar, JsTrue}
import net.liftweb.http.{GUIDJsExp, SHtml}
import net.liftweb.http.js.{JE, JsCmds, JsCmd}
import net.liftweb.util._
import Helpers._
import SquerylMode._

import scala.xml.NodeSeq

class UserSnippet(user : User) {

  def this() = this(LoggedInUser.get.get)

  def myProfileLink = LoggedInUser.get match {
    case Some(me) => "a [href]" #> s"/profile/${me.id}"
    case _ => "a [href-]" #> ""
  }

  def sendConnectionRequest(me : Long, them : Long) : JsCmd = {
    val GUIDJsExp(_, js) = SHtml.ajaxCall(JE.JsNull, in => {
      TuftDB.userConnection.insert(UserConnection(me, them, false))
      JsCmds.SetElemById("user-connect", JE.Str("Sent..."), "value") &
        JsCmds.SetElemById("user-connect", JsTrue, "disabled")
    })
    js.cmd
  }

  def acceptConnectionRequest(me : Long, them : Long) : JsCmd = {
    val GUIDJsExp(_, js) = SHtml.ajaxCall(JE.JsNull, in => {
      update(TuftDB.userConnection)(dbuc =>
        where(dbuc.requesteeId === me and dbuc.requestorId === them)
          set(dbuc.acknowledged := true)
      )
      JsCmds.SetElemById("user-connect", JE.Str("Connected"), "value") &
        JsCmds.SetElemById("user-connect", JsTrue, "disabled")
    })
    js.cmd
  }

  def canConnect = LoggedInUser.get match {
    case Some(me) if me.id == user.id => "#user-connect" #> NodeSeq.Empty
    case Some(me) => User.getUserConnectionForUser(me, user) match {
      case Some(UserConnection(_, _, true)) => "#user-connect [disabled+]" #> "disabled" &
        "#user-connect [value]" #> "Connected"
      case Some(UserConnection(me.id, _, _)) => "#user-connect [disabled+]" #> "disabled" &
        "#user-connect [value]" #> "Sent..."
      case Some(UserConnection(them, me.id, _)) => "#user-connect [value]" #> "Accept?" &
        "#user-connect [onclick+]" #> acceptConnectionRequest(me.id, them)
      case _ => "#user-connect [onclick+]" #> sendConnectionRequest(me.id, user.id)
    }
    case _ => "#user-connect [disabled+]" #> "disabled"
  }

  def biography = LoggedInUser.get match {
    case Some(me) if me.id == user.id => {
      val GUIDJsExp(_, js) = SHtml.ajaxCall(JsVar("this", "value"), value => {
        update(TuftDB.user)(u => where(u.id === me.id) set(u.biography := value))
        JsCmds.Noop
      })
      <textarea rows="4" cols="50" onblur={js.toJsCmd}>{me.biography}</textarea>
    }
    case _ => <p>{user.biography}</p>
  }

  def userInfo = {
    "#user-full-name" #> s"${user.firstName} ${user.lastName}" &
      canConnect &
      "user-biography" #> biography
  }

}
