package com.github.oopman.collectioneer.database

import java.sql.{Clob, Timestamp}
import java.util.UUID

import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape

case class Collection(uuid: UUID, name: String, category: String, description: Clob, dateTimeCreated: Timestamp,
                      dateTimeModified: Timestamp, deleted: Boolean, active: Boolean)

case class Item(uuid: UUID, name: String, version: String, data: Clob, dateTimeCreated: Timestamp,
                dateTimeModified: Timestamp, deleted: Boolean, active: Boolean)

case class CollectionItemAssn(collectionUUID: UUID, itemUUID: UUID, quantity: Option[Int])

case class CollectionParentCollectionAssn(collectionUUID: UUID, parentCollectionUUID: UUID)

trait Tables {
  this: DriverComponent =>

  import driver.api._

  /**
    *
    * @param tag
    */
  class Collections(tag: Tag) extends Table[Collection](tag, "collections") {
    def uuid = column[UUID]("uuid", O.PrimaryKey)
    def name = column[String]("name", O.Unique)
    def category = column[String]("category")
    def description = column[Clob]("description")
    def dateTimeCreated = column[Timestamp]("datetime_created")
    def dateTimeModified = column[Timestamp]("datetime_modified")
    def deleted = column[Boolean]("deleted")
    def active = column[Boolean]("active")

    def categoryIndex = index("idx_category", category)
    def deletedIndex = index("idx_deleted", deleted)
    def activeIndex = index("idx_active", active)

    def * : ProvenShape[(UUID, String, String, Clob, Timestamp, Timestamp, Boolean, Boolean)] =
      (uuid, name, category, description, dateTimeCreated, dateTimeModified, deleted, active)
  }
  val collections = TableQuery[Collections]


  /**
    *
    * @param tag
    */
  class Items(tag: Tag) extends Table[Item](tag, "item") {
    def uuid = column[UUID]("uuid", O.PrimaryKey)
    def name = column[String]("name")
    def version = column[String]("version")
    def data = column[Clob]("data")
    def dateTimeCreated = column[Timestamp]("datetime_created")
    def dateTimeModified = column[Timestamp]("datetime_modified")
    def deleted = column[Boolean]("deleted")
    def active = column[Boolean]("active")

    def nameIndex = index("idx_name", name)
    def versionIndex = index("idx_version", version)
    def deletedIndex = index("idx_deleted", deleted)
    def activeIndex = index("idx_active", active)

    def * : ProvenShape[(UUID, String, String, Clob, Timestamp, Timestamp, Boolean, Boolean)] =
      (uuid, name, version, data, dateTimeCreated, dateTimeModified, deleted, active)
  }
  val items = TableQuery[Items]

  /**
    *
    * @param tag
    */
  class CollectionItemAssns(tag: Tag) extends Table[CollectionItemAssn](tag, "collection_item_assn") {
    def collectionUUID = column[UUID]("collection")
    def itemUUID = column[UUID]("item")
    def quantity = column[Option[Int]]("quantity")

    def pk = primaryKey("pk_collection_item_associations", (collectionUUID, itemUUID))
    def collection = foreignKey("fk_collection", collectionUUID, collections)(_.uuid)
    def item = foreignKey("fk_item", itemUUID, items)(_.uuid)

    def * : ProvenShape[(UUID, UUID, Option[Int])] = (collectionUUID, itemUUID, quantity)
  }
  val collectionItems = TableQuery[CollectionItemAssns]

  /**
    *
    * @param tag
    */
  class CollectionParentCollectionAssns(tag: Tag) extends Table[CollectionParentCollectionAssn](tag, "collection_collection_assn") {
    def collectionUUID = column[UUID]("collection")
    def parentCollectionUUID = column[UUID]("parent_collection")

    def pk = primaryKey("pk_collection_parent_collection_associations", (collectionUUID, parentCollectionUUID))
    def collection = foreignKey("fk_collection", collectionUUID, collections)(_.uuid)
    def parentCollection = foreignKey("fk_parent_collection", parentCollectionUUID, collections)(_.uuid)

    def * : ProvenShape[(UUID, UUID)] = (collectionUUID, parentCollectionUUID)

  }
  val collectionParentCollections = TableQuery[CollectionParentCollectionAssns]

}
