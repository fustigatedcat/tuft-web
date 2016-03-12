package com.fustigatedcat.tuft.web.snippet

import net.liftweb.util._
import Helpers._

import com.fustigatedcat.tuft.web.model.User

class Profile(user : User) {

  def populate : CssSel = {
    ".avatar [src]" #> user.avatar &
      ".first-name" #> user.firstName &
      ".last-name" #> user.lastName &
      ".username" #> user.username
  }

}
