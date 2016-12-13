package com.github.oopman.collectioneer.database

import slick.jdbc.JdbcProfile

class DAL(val driver: JdbcProfile) extends Tables with DriverComponent {
  import driver.api._

  val create = (
    collections.schema ++ items.schema ++ collectionItems.schema ++ collectionParentCollections.schema ++
    tags.schema ++ tagCollections.schema ++ tagItems.schema).create
}