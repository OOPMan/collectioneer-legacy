package com.github.oopman.collectioneer.model

import java.sql.Timestamp
import java.time.Instant

import slick.jdbc.JdbcProfile

class DAL(val driver: JdbcProfile) extends Tables with DriverComponent {
  import driver.api._

  val now = Timestamp.from(Instant.now)

  val create = (
    categories.schema ++ collections.schema ++ items.schema ++ collectionItems.schema ++
    collectionParentCollections.schema ++ tags.schema ++ tagCollections.schema ++ tagItems.schema).create

  val populateCollections = collections.map(c => (c.name, c.dateTimeCreated, c.dateTimeModified)) ++= Seq(
    ("Bits", now, now),
    ("Bobs", now, now),
    ("Misc", now, now)
  )
}
