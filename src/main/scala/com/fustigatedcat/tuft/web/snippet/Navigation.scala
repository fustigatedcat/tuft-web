package com.fustigatedcat.tuft.web.snippet

import net.liftweb.util._
import Helpers._

object Navigation {

  def user : CssSel = LoggedInUser.is match {
    case Some(user) => {
      "#full_name" #> (user.firstName + " " + user.lastName) &
        "img [src]" #> user.avatar
    }
    case _ => "*" #> "INVALID"
  }

}
