package com.fustigatedcat.tuft.ui.model

import org.squeryl.Schema

import SquerylMode._

object TuftDB extends Schema {

  val user = table[User]

  val tuft = table[Tuft]

  val strand = table[Strand]

  val userConnection = table[UserConnection]

  val tuftAffinedUserMap = table[TuftAffinedUserMap]

  on(user)(u => declare(
    u.email is unique,
    u.id is (autoIncremented,primaryKey)
  ))

  on(tuft)(t => declare(
    t.id is (autoIncremented,primaryKey)
  ))

  on(strand)(s => declare(
    s.id is (autoIncremented,primaryKey)
  ))

  on(userConnection)(uc => declare(
    columns(uc.requesteeId, uc.requestorId) are primaryKey
  ))

  on(tuftAffinedUserMap)(taum => declare(
    columns(taum.tuftId, taum.affinedUserId) are primaryKey
  ))

  val userToTuft = oneToManyRelation(user, tuft).via((u, t) => u.id === t.userId)

  val posterToSentStrands = oneToManyRelation(user, strand).via((u, s) => u.id === s.posterId)

  val tuftToStrands = manyToManyRelation(tuft, strand).via[StrandTuftMap]((t, s, stm) => (t.id === stm.tuftId, s.id === stm.strandId))

  val tuftAffinedUsers = manyToManyRelation(tuft, user).via[TuftAffinedUserMap]((t, u, taum) => (taum.tuftId === t.id, taum.affinedUserId === u.id))

}
