package com.github.oopman.collectioneer.database

import java.sql.{Clob, Timestamp}
import java.util.UUID

import slick.lifted.{Tag => SlickTag}

case class Category(id: Int, left: Int, right: Int, name: String)

case class Collection(uuid: UUID, name: String, category: Option[Int], description: Option[Clob],
                      dateTimeCreated: Timestamp, dateTimeModified: Timestamp,
                      deleted: Boolean, active: Boolean)

case class Item(uuid: UUID, name: String, category: Option[Int], version: Option[String], data: Option[Clob],
                dateTimeCreated: Timestamp, dateTimeModified: Timestamp,
                deleted: Boolean, active: Boolean)

case class CollectionItemAssn(collectionUUID: UUID, itemUUID: UUID, quantity: Option[Int])

case class CollectionParentCollectionAssn(collectionUUID: UUID, parentCollectionUUID: UUID)

case class Tag(name: String, description: Option[Int], data: Option[Clob])

case class TagCollectionAssn(tagName: String, collectionUUID: UUID)

case class TagItemAssn(tagName: String, itemUUID: UUID)

trait Tables {
  this: DriverComponent =>

  import driver.api._

  /**
    *
    * @param tag
    */
  class Categories(tag: SlickTag) extends Table[Category](tag, "categories") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def left = column[Int]("left")
    def right = column[Int]("right")
    def name = column[String]("name")

    def * = (id, left, right, name) <> (Category.tupled, Category.unapply)
  }
  val categories = TableQuery[Categories]

  /**
    *
    * @param tag
    */
  class Collections(tag: SlickTag) extends Table[Collection](tag, "collections") {
    def uuid = column[UUID]("uuid", O.PrimaryKey)
    def name = column[String]("name", O.Unique)
    def category = column[Option[Int]]("category")
    def description = column[Option[Clob]]("description")
    def dateTimeCreated = column[Timestamp]("datetime_created")
    def dateTimeModified = column[Timestamp]("datetime_modified")
    def deleted = column[Boolean]("deleted")
    def active = column[Boolean]("active")

    def deletedIdx = index("idx_collections_deleted", deleted)
    def activeIdx = index("idx_collections_active", active)
    def categoryFk = foreignKey("fk_collections_category", category, categories)(_.id)

    def * = (uuid, name, category, description, dateTimeCreated, dateTimeModified, deleted, active) <> (Collection.tupled, Collection.unapply)
  }
  val collections = TableQuery[Collections]


  /**
    *
    * @param tag
    */
  class Items(tag: SlickTag) extends Table[Item](tag, "items") {
    def uuid = column[UUID]("uuid", O.PrimaryKey)
    def name = column[String]("name")
    def category = column[Option[Int]]("category")
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
    def categoryFk = foreignKey("fk_collections_category", category, categories)(_.id)

    def * = (uuid, name, category, version, data, dateTimeCreated, dateTimeModified, deleted, active) <> (Item.tupled, Item.unapply)
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

    def * = (collectionUUID, itemUUID, quantity) <> (CollectionItemAssn.tupled, CollectionItemAssn.unapply)
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

    def * = (collectionUUID, parentCollectionUUID) <> (CollectionParentCollectionAssn.tupled, CollectionParentCollectionAssn.unapply)

  }
  val collectionParentCollections = TableQuery[CollectionParentCollectionAssns]

  import com.github.oopman.collectioneer.database.{Tag => CollectioneerTag}
  class Tags(tag: SlickTag) extends Table[CollectioneerTag](tag, "tags") {
    def name = column[String]("name", O.PrimaryKey)
    def category = column[Option[Int]]("category")
    def data = column[Option[Clob]]("data")

    def categoryFk = foreignKey("fk_tags_category", category, categories)(_.id)

    def * = (name, category, data) <> (CollectioneerTag.tupled, CollectioneerTag.unapply)
  }
  val tags = TableQuery[Tags]

   /**
    *
    * @param tag
    */
  class TagCollectionAssns(tag: SlickTag) extends Table[TagCollectionAssn](tag, "tag_collection_assns") {
    def tagName = column[String]("tag_name")
    def collectionUUID = column[UUID]("collection_uuid")

    def pk = primaryKey("pk_tag_collection_assns", (tagName, collectionUUID))
    def tagFk = foreignKey("fk_tag_collection_assns_item", tagName, tags)(_.name)
    def collectionFk = foreignKey("fk_tag_collection_assns_collection", collectionUUID, collections)(_.uuid)

    def * = (tagName, collectionUUID) <> (TagCollectionAssn.tupled, TagCollectionAssn.unapply)
  }
  val tagCollections = TableQuery[TagCollectionAssns]

   /**
    *
    * @param tag
    */
  class TagItemAssns(tag: SlickTag) extends Table[TagItemAssn](tag, "tag_item_assns") {
    def tagName = column[String]("tag_name")
    def itemUUID = column[UUID]("item_uuid")

    def pk = primaryKey("pk_tag_item_assns", (tagName, itemUUID))
    def tagFk = foreignKey("fk_tag_item_assns_item", tagName, tags)(_.name)
    def itemFk = foreignKey("fk_tag_item_assns_item", itemUUID, items)(_.uuid)

    def * = (tagName, itemUUID) <> (TagItemAssn.tupled, TagItemAssn.unapply)
  }
  val tagItems = TableQuery[TagItemAssns]
}
