package com.fustigatedcat.tuft.web.snippet

import net.liftweb.http.{GUIDJsExp, SHtml}
import net.liftweb.http.js.JE.{Stringify, JsVar, Str, JsArray}
import net.liftweb.http.js.{JsCmd, JE, JsCmds}
import net.liftweb.json._
import net.liftweb.util._
import Helpers._

import com.fustigatedcat.tuft.web.model.{UserConnection, User}

import scala.xml.NodeSeq

class Profile(user : User) {

  def this() = this(LoggedInUser.is.get)

  def _requestConnection(input : String) : JsCmd = parseOpt(input) match {
    case Some(JArray(List(JString(success),JString(failure)))) => {
      if(UserConnection.connect(LoggedInUser.is.get, user)) {
        JE.Call("success", JBool(true)).cmd
      } else {
        JE.Call("failure", JBool(false)).cmd
      }
    }
    case _ => {
      JsCmds.Alert("Expected only one argument")
    }
  }

  def requestConnection : CssSel = LoggedInUserId.is match {
    case Some(id) if user.id == id => {
      val GUIDJsExp(_, func) = SHtml.ajaxCall(Stringify(JsArray(JsVar("success"),JsVar("failure"))), _requestConnection)
      "*" #> JsCmds.Script(JsCmds.Function("requestConnection", List("success", "failure"), func.cmd))
    }
    case _ => "*" #> NodeSeq.Empty
  }

  def populate : CssSel = {
    ".avatar [src]" #> user.avatar &
      ".first-name" #> user.firstName &
      ".last-name" #> user.lastName &
      ".username" #> user.username
  }

}
