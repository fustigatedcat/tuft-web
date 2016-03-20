package com.fustigatedcat.tuft.web.snippet

import net.liftweb.http.{GUIDJsExp, SHtml}
import net.liftweb.http.js.JE.{Stringify, JsVar, Str, JsArray}
import net.liftweb.http.js.{JsCmd, JE, JsCmds}
import net.liftweb.json._
import net.liftweb.util._
import Helpers._

import com.fustigatedcat.tuft.web.model.{TuftDB, UserConnection, User}

import scala.xml.NodeSeq

class Profile(user : User) {

  val currentUserConnection = LoggedInUser.is match {
    case Some(us) => UserConnection.getConnection(us, user)
    case _ => None
  }

  def _acceptConnection(input : String) : JsCmd = parseOpt(input) match {
    case Some(JString(callback)) => LoggedInUser.is match {
      case Some(us) => {
        UserConnection.acceptConnectionRequest(us, user)
        JE.Call(callback, JBool(true)).cmd
      }
      case _ => JE.Call(callback, JBool(false)).cmd
    }
    case _ => {
      JsCmds.Alert("Expected one argument")
    }
  }

  def acceptConnection : CssSel = LoggedInUserId.is match {
    case Some(id) if user.id != id => {
      val GUIDJsExp(_, func) = SHtml.ajaxCall(Stringify(JsVar("callback")), _acceptConnection)
      "*" #> JsCmds.Script(JsCmds.Function("acceptConnection", List("callback"), func.cmd))
    }
    case _ => "*" #> NodeSeq.Empty
  }

  def _requestConnection(input : String) : JsCmd = parseOpt(input) match {
    case Some(JString(callback)) => {
      JE.Call(callback, JBool(UserConnection.connect(LoggedInUser.is.get, user))).cmd
    }
    case _ => {
      JsCmds.Alert("Expected one argument")
    }
  }

  def requestConnection : CssSel = LoggedInUserId.is match {
    case Some(id) if user.id != id => {
      val GUIDJsExp(_, func) = SHtml.ajaxCall(Stringify(JsVar("callback")), _requestConnection)
      "*" #> JsCmds.Script(JsCmds.Function("requestConnection", List("callback"), func.cmd))
    }
    case _ => "*" #> NodeSeq.Empty
  }

  def connectionManagement : CssSel = LoggedInUser.is match {
    case Some(us) if us.id != user.id => UserConnection.getConnection(us, user) match {
      case Some(uc) if uc.accepted => "section [data-ng-init]" #> "connectionState = 'connected';"
      case Some(uc) if uc.requestor_user_id == us.id => "section [data-ng-init]" #> "connectionState = 'requested';"
      case Some(uc) if !uc.accepted && uc.requestee_user_id == us.id => "section [data-ng-init]" #> "connectionState = 'toAccept';"
      case _ => "section [data-ng-init]" #> "connectionState = 'toRequest';"
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
