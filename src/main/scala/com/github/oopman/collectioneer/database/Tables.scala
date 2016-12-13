package com.github.oopman.collectioneer.database

import java.sql.{Clob, Timestamp}
import java.util.UUID

import slick.lifted.ProvenShape
import slick.lifted.{Tag => SlickTag}

case class Collection(uuid: UUID, name: String, category: Option[String], description: Option[Clob],
                      dateTimeCreated: Timestamp, dateTimeModified: Timestamp,
                      deleted: Boolean, active: Boolean)

case class Item(uuid: UUID, name: String, version: Option[String], data: Option[Clob],
                dateTimeCreated: Timestamp, dateTimeModified: Timestamp,
                deleted: Boolean, active: Boolean)

case class CollectionItemAssn(collectionUUID: UUID, itemUUID: UUID, quantity: Option[Int])

case class CollectionParentCollectionAssn(collectionUUID: UUID, parentCollectionUUID: UUID)

case class Tag(name: String, category: Option[String], data: Option[Clob])

trait Tables {
  this: DriverComponent =>

  import driver.api._

  /**
    *
    * @param tag
    */
  class Collections(tag: SlickTag) extends Table[Collection](tag, "collections") {
    def uuid = column[UUID]("uuid", O.PrimaryKey)
    def name = column[String]("name", O.Unique)
    def category = column[Option[String]]("category")
    def description = column[Option[Clob]]("description")
    def dateTimeCreated = column[Timestamp]("datetime_created")
    def dateTimeModified = column[Timestamp]("datetime_modified")
    def deleted = column[Boolean]("deleted")
    def active = column[Boolean]("active")

    def categoryIdx = index("idx_collections_category", category)
    def deletedIdx = index("idx_collections_deleted", deleted)
    def activeIdx = index("idx_collections_active", active)

    def * : ProvenShape[(UUID, String, Option[String], Option[Clob], Timestamp, Timestamp, Boolean, Boolean)] =
      (uuid, name, category, description, dateTimeCreated, dateTimeModified, deleted, active)
  }
  val collections = TableQuery[Collections]


  /**
    *
    * @param tag
    */
  class Items(tag: SlickTag) extends Table[Item](tag, "items") {
    def uuid = column[UUID]("uuid", O.PrimaryKey)
    def name = column[String]("name")
    def version = column[Option[String]]("version")
    def data = column[Option[Clob]]("data")
    def dateTimeCreated = column[Timestamp]("datetime_created")
    def dateTimeModified = column[Timestamp]("datetime_modified")
    def deleted = column[Boolean]("deleted")
    def active = column[Boolean]("active")

    def nameIdx = index("idx_items_name", name)
    def versionIdx = index("idx_items_version", version)
    def deletedIdx = index("idx_items_deleted", deleted)
    def activeIdx = index("idx_items_active", active)

    def * : ProvenShape[(UUID, String, Option[String], Option[Clob], Timestamp, Timestamp, Boolean, Boolean)] =
      (uuid, name, version, data, dateTimeCreated, dateTimeModified, deleted, active)
  }
  val items = TableQuery[Items]

  /**
    *
    * @param tag
    */
  class CollectionItemAssns(tag: SlickTag) extends Table[CollectionItemAssn](tag, "collection_item_assns") {
    def collectionUUID = column[UUID]("collection_uuid")
    def itemUUID = column[UUID]("item_uuid")
    def quantity = column[Option[Int]]("quantity")

    def pk = primaryKey("pk_collection_item_assns", (collectionUUID, itemUUID))
    def collectionFk = foreignKey("fk_collection_item_assns_collection", collectionUUID, collections)(_.uuid)
    def itemFk = foreignKey("fk_collection_item_assns_item", itemUUID, items)(_.uuid)

    def * : ProvenShape[(UUID, UUID, Option[Int])] = (collectionUUID, itemUUID, quantity)
  }
  val collectionItems = TableQuery[CollectionItemAssns]

  /**
    *
    * @param tag
    */
  class CollectionParentCollectionAssns(tag: SlickTag) extends Table[CollectionParentCollectionAssn](tag, "collection_collection_assns") {
    def collectionUUID = column[UUID]("collection_uuid")
    def parentCollectionUUID = column[UUID]("parent_collection_uuid")

    def pk = primaryKey("pk_collection_parent_collection_assns", (collectionUUID, parentCollectionUUID))
    def collectionFk = foreignKey("fk_collection_parent_collection_assns_collection", collectionUUID, collections)(_.uuid)
    def parentCollectionFk = foreignKey("fk_collection_parent_collection_assns_parent_collection", parentCollectionUUID, collections)(_.uuid)

    def * : ProvenShape[(UUID, UUID)] = (collectionUUID, parentCollectionUUID)

  }
  val collectionParentCollections = TableQuery[CollectionParentCollectionAssns]

  class Tags(tag: SlickTag) extends Table[Tag](tag, "tags") {
    def name = column[String]("name", O.PrimaryKey)
    def category = column[Option[String]]("category")
    def data = column[Option[Clob]]("data")

    def categoryIdx = index("idx_tags_category", category)

    def * : ProvenShape[(String, Option[String], Option[Clob])] = (name, category, data)
  }
  val tags = TableQuery[Tags]

   /**
    *
    * @param tag
    */
  class TagCollectionAssns(tag: SlickTag) extends Table[TagCollectionAssn](tag, "tag_collection_assns") {
    def tagUUID = column[String]("tag_name")
    def collectionUUID = column[UUID]("collection_uuid")

    def pk = primaryKey("pk_tag_collection_assns", (tagUUID, collectionUUID))
    def tagFk = foreignKey("fk_tag_collection_assns_item", tagUUID, tags)(_.name)
    def collectionFk = foreignKey("fk_tag_collection_assns_collection", collectionUUID, collections)(_.uuid)

    def * : ProvenShape[(String, UUID)] = (tagUUID, collectionUUID)
  }
  val tagCollections = TableQuery[TagCollectionAssns]

   /**
    *
    * @param tag
    */
  class TagItemAssns(tag: SlickTag) extends Table[TagCollectionAssn](tag, "tag_item_assns") {
    def tagUUID = column[String]("tag_name")
    def itemUUID = column[UUID]("item_uuid")

    def pk = primaryKey("pk_tag_item_assns", (tagUUID, itemUUID))
    def tagFk = foreignKey("fk_tag_item_assns_item", tagUUID, tags)(_.name)
    def itemFk = foreignKey("fk_tag_item_assns_item", itemUUID, items)(_.uuid)

    def * : ProvenShape[(String, UUID)] = (tagUUID, itemUUID)
  }
  val tagItems = TableQuery[TagItemAssns]
}
