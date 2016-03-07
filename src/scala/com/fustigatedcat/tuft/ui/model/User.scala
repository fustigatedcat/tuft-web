package com.fustigatedcat.tuft.ui.model

import net.liftweb.http.{RequestVar, SessionVar}
import org.apache.commons.codec.digest.DigestUtils
import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column
import SquerylMode._

object LoggedInUserId extends SessionVar[Option[Long]](None)

object LoggedInUser extends RequestVar[Option[User]](None)

object User {

  def areUsersConnected_?(id1 : Long, id2 : Long) : Boolean = {
    from(TuftDB.userConnection)(uc =>
      where(
        (uc.requesteeId === id1 and uc.requestorId === id2) or
          (uc.requesteeId === id2 and uc.requestorId === id1)
      )
      select uc
    ).exists(_.acknowledged)
  }

  def getConnectionsForUser(id : Long) : List[User] = {
    join(TuftDB.userConnection, TuftDB.user)((uc, u) =>
      where(uc.acknowledged === true and u.id <> id)
      select u
      on((uc.requesteeId === id) or uc.requestorId === id)
    ).toList
  }

  def getByOptionalId(idOpt : Option[Long]) : Option[User] = idOpt match {
    case Some(id) => getById(id)
    case _ => None
  }

  def getById(id : Long) : Option[User] = {
    from(TuftDB.user)(u => where(u.id === id) select u).headOption
  }

  def authenticateUser(email : String, password : String) : Option[User] = {
    def validatePassword(user : User) : Boolean =
      user.password.equals(DigestUtils.sha256Hex(password + user.salt))

    from(TuftDB.user)(u => where(u.email === email) select u).headOption.filter(validatePassword)
  }

  def getUserConnectionForUser(me : User, them : User) : Option[UserConnection] = {
    from(TuftDB.userConnection)(uc =>
      where(uc.requestorId === me.id or uc.requesteeId === me.id)
      select uc
    ).headOption
  }

}

case class User( @Column("user_id") id : Long,
                 @Column("first_name") firstName : String,
                 @Column("last_name") lastName : String,
                 @Column("email") email : String,
                 @Column("salt") salt : String,
                 @Column("password") password : String,
                 @Column("biography") biography : String,
                 @Column("avatar") avatar : String) extends KeyedEntity[Long] {

  lazy val tufts = TuftDB.userToTuft.left(this)

  lazy val sentStrands = TuftDB.posterToSentStrands.left(this)

}
