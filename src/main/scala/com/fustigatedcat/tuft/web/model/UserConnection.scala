package com.fustigatedcat.tuft.web.model

import java.sql.Timestamp

import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column

case class UserConnection(@Column("request_id") id : Long,
                          @Column("requestor_user_id") requestor_user_id : Int,
                          @Column("requestee_user_id") requestee_user_id : Int,
                          @Column("requested_date") requested : Timestamp,
                          @Column("accepted") accepted : Boolean,
                          @Column("accepted_date") accepted_date : Timestamp) extends KeyedEntity[Long] {

}
