package com.fustigatedcat.tuft.web.model

import java.sql.Timestamp

import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column

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

}

case class UserConnection(@Column("request_id") id : Long,
                          @Column("requestor_user_id") requestor_user_id : Long,
                          @Column("requestee_user_id") requestee_user_id : Long,
                          @Column("requested_date") requested : Timestamp,
                          @Column("accepted") accepted : Boolean,
                          @Column("accepted_date") accepted_date : Option[Timestamp]) extends KeyedEntity[Long] {

}
