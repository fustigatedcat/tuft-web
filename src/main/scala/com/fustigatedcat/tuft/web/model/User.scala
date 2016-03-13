package com.fustigatedcat.tuft.web.model

import java.security.MessageDigest
import java.sql.Timestamp
import java.util.Base64

import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column
import SquerylMode._

import scala.util.Random

object User {

  def authenticate(username : String, password : String) : Option[User] = TuftDB.user.where(u =>
    u.username === username
  ).headOption match {
    case Some(user) if createHash(password, user.salt) == user.password => Some(user)
    case _ => None
  }

  def createHash(input : String, salt : String) : String = {
    val md = MessageDigest.getInstance("SHA-256")
    Base64.getEncoder.encodeToString(md.digest((input + salt).getBytes("UTF-8")))
  }

  def generateSalt() : String = {
    (for(i <- 0 until 64) yield Random.nextPrintableChar()).mkString
  }

  def getById(id : Long) : Option[User] = TuftDB.user.where(u => u.id === id).headOption

  def searchForUser(username : String) : List[User] = {
    TuftDB.user.where(u => u.username like (username + "%")).toList
  }

}

case class User(@Column("user_id") id : Long,
           @Column("username") username : String,
           @Column("email") email : String,
           @Column("first_name") firstName : String,
           @Column("last_name") lastName : String,
           @Column("password") password : String,
           @Column("salt") salt : String,
           @Column("last_login") lastLogin : Timestamp,
           @Column("avatar") avatar : String) extends KeyedEntity[Long] {

  lazy val connectionRequestsFromMe = TuftDB.userRequestsSent.left(this)

  lazy val connectionRequestsToMe = TuftDB.userRequestsReceived.left(this)

}
