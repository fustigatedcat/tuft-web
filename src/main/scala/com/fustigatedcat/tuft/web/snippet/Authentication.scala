package com.fustigatedcat.tuft.web.snippet

import net.liftweb.http.S
import net.liftweb.util._
import Helpers._

import scala.xml.NodeSeq

object Authentication {

  def auth : CssSel = if(S.post_?) {
    "#nadda" #> NodeSeq.Empty
  } else {
    "*" #> NodeSeq.Empty
  }

}
