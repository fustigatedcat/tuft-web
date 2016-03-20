package com.fustigatedcat.tuft.web.model

import java.sql.Timestamp

import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column

import SquerylMode._

import scala.language.postfixOps

object UserConnection {

  def connect(us : User, them : User) : Boolean = {
    TuftDB.userConnection.insert(
      UserConnection(
        0,
        us.id,
        them.id,
        new Timestamp(System.currentTimeMillis()),
        false,
        None
      )
    )
    true
  }

  def getConnection(us : User, them : User) : Option[UserConnection] = {
    from(TuftDB.userConnection)(uc => where(
      (uc.requestor_user_id === us.id and uc.requestee_user_id === them.id) or
        (uc.requestee_user_id === us.id and uc.requestor_user_id === them.id)
    )
      select uc
      orderBy(uc.requested asc)
    ).headOption
  }

  def acceptConnectionRequest(us : User, them : User) : Int = {
    TuftDB.userConnection.update(uc =>
      where(uc.requestor_user_id === them.id and uc.requestee_user_id === us.id and uc.accepted === false)
        set(uc.accepted := true, uc.accepted_date := Some(new Timestamp(System.currentTimeMillis())))
    )
  }

}

case class UserConnection(@Column("request_id") id : Long,
                          @Column("requestor_user_id") requestor_user_id : Long,
                          @Column("requestee_user_id") requestee_user_id : Long,
                          @Column("requested_date") requested : Timestamp,
                          @Column("accepted") accepted : Boolean,
                          @Column("accepted_date") accepted_date : Option[Timestamp]) extends KeyedEntity[Long] {

}
