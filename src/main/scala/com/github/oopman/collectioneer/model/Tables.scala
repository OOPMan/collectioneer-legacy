package com.github.oopman.collectioneer.model

import java.sql.{Clob, Timestamp}

import slick.lifted.{Tag => SlickTag}

case class Category(id: Int, left: Option[Int], right: Option[Int], name: String)

case class Collection(id: Int, name: String, category: Option[Int], description: Option[Clob],
                      dateTimeCreated: Timestamp, dateTimeModified: Timestamp,
                      deleted: Boolean, active: Boolean)

case class Item(id: Int, name: String, category: Option[Int], version: Option[String], data: Option[Clob],
                dateTimeCreated: Timestamp, dateTimeModified: Timestamp,
                deleted: Boolean, active: Boolean)

case class CollectionItemAssn(collectionId: Int, itemId: Int, quantity: Option[Int])

case class CollectionParentCollectionAssn(collectionId: Int, parentCollectionId: Int)

case class Tag(name: String, category: Option[Int], data: Option[Clob])

case class TagCollectionAssn(tagName: String, collectionId: Int)

case class TagItemAssn(tagName: String, itemId: Int)

trait Tables {
  this: DriverComponent =>

  import driver.api._

  /**
    *
    * @param tag
    */
  class Categories(tag: SlickTag) extends Table[Category](tag, "categories") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def left = column[Option[Int]]("left", O.Default(None))
    def right = column[Option[Int]]("right", O.Default(None))
    def name = column[String]("name")

    def * = (id, left, right, name) <> (Category.tupled, Category.unapply)
  }
  val categories = TableQuery[Categories]

  /**
    *
    * @param tag
    */
  class Collections(tag: SlickTag) extends Table[Collection](tag, "collections") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name", O.Unique)
    def category = column[Option[Int]]("category", O.Default(None))
    def description = column[Option[Clob]]("description", O.Default(None))
    def dateTimeCreated = column[Timestamp]("datetime_created")
    def dateTimeModified = column[Timestamp]("datetime_modified")
    def deleted = column[Boolean]("deleted", O.Default(false))
    def active = column[Boolean]("active", O.Default(false))

    def deletedIdx = index("idx_collections_deleted", deleted)
    def activeIdx = index("idx_collections_active", active)
    def categoryFk = foreignKey("fk_collections_category", category, categories)(_.id)

    def * = (id, name, category, description, dateTimeCreated, dateTimeModified, deleted, active) <> (Collection.tupled, Collection.unapply)
  }
  val collections = TableQuery[Collections]


  /**
    *
    * @param tag
    */
  class Items(tag: SlickTag) extends Table[Item](tag, "items") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def category = column[Option[Int]]("category", O.Default(None))
    def version = column[Option[String]]("version", O.Default(None))
    def data = column[Option[Clob]]("data", O.Default(None))
    def dateTimeCreated = column[Timestamp]("datetime_created")
    def dateTimeModified = column[Timestamp]("datetime_modified")
    def deleted = column[Boolean]("deleted", O.Default(false))
    def active = column[Boolean]("active", O.Default(true))

    def nameIdx = index("idx_items_name", name)
    def versionIdx = index("idx_items_version", version)
    def deletedIdx = index("idx_items_deleted", deleted)
    def activeIdx = index("idx_items_active", active)
    def categoryFk = foreignKey("fk_items_category", category, categories)(_.id)
    def nameVersionIdx = index("idx_items_name_version", (name, version), unique = true)

    def * = (id, name, category, version, data, dateTimeCreated, dateTimeModified, deleted, active) <> (Item.tupled, Item.unapply)
  }
  val items = TableQuery[Items]

  /**
    *
    * @param tag
    */
  class CollectionItemAssns(tag: SlickTag) extends Table[CollectionItemAssn](tag, "collection_item_assns") {
    def collectionId = column[Int]("collection_id")
    def itemId = column[Int]("item_id")
    def quantity = column[Option[Int]]("quantity", O.Default(None))

    def pk = primaryKey("pk_collection_item_assns", (collectionId, itemId))
    def collectionFk = foreignKey("fk_collection_item_assns_collection", collectionId, collections)(_.id)
    def itemFk = foreignKey("fk_collection_item_assns_item", itemId, items)(_.id)

    def * = (collectionId, itemId, quantity) <> (CollectionItemAssn.tupled, CollectionItemAssn.unapply)
  }
  val collectionItems = TableQuery[CollectionItemAssns]

  /**
    *
    * @param tag
    */
  class CollectionParentCollectionAssns(tag: SlickTag) extends Table[CollectionParentCollectionAssn](tag, "collection_collection_assns") {
    def collectionId = column[Int]("collection_id")
    def parentCollectionId = column[Int]("parent_collection_id")

    def pk = primaryKey("pk_collection_parent_collection_assns", (collectionId, parentCollectionId))
    def collectionFk = foreignKey("fk_collection_parent_collection_assns_collection", collectionId, collections)(_.id)
    def parentCollectionFk = foreignKey("fk_collection_parent_collection_assns_parent_collection", parentCollectionId, collections)(_.id)

    def * = (collectionId, parentCollectionId) <> (CollectionParentCollectionAssn.tupled, CollectionParentCollectionAssn.unapply)

  }
  val collectionParentCollections = TableQuery[CollectionParentCollectionAssns]

  import com.github.oopman.collectioneer.model.{Tag => CollectioneerTag}
  class Tags(tag: SlickTag) extends Table[CollectioneerTag](tag, "tags") {
    def name = column[String]("name", O.PrimaryKey)
    def category = column[Option[Int]]("category", O.Default(None))
    def data = column[Option[Clob]]("data", O.Default(None))

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
    def collectionId = column[Int]("collection_id")

    def pk = primaryKey("pk_tag_collection_assns", (tagName, collectionId))
    def tagFk = foreignKey("fk_tag_collection_assns_item", tagName, tags)(_.name)
    def collectionFk = foreignKey("fk_tag_collection_assns_collection", collectionId, collections)(_.id)

    def * = (tagName, collectionId) <> (TagCollectionAssn.tupled, TagCollectionAssn.unapply)
  }
  val tagCollections = TableQuery[TagCollectionAssns]

   /**
    *
    * @param tag
    */
  class TagItemAssns(tag: SlickTag) extends Table[TagItemAssn](tag, "tag_item_assns") {
    def tagName = column[String]("tag_name")
    def itemId = column[Int]("item_id")

    def pk = primaryKey("pk_tag_item_assns", (tagName, itemId))
    def tagFk = foreignKey("fk_tag_item_assns_tag", tagName, tags)(_.name)
    def itemFk = foreignKey("fk_tag_item_assns_item", itemId, items)(_.id)

    def * = (tagName, itemId) <> (TagItemAssn.tupled, TagItemAssn.unapply)
  }
  val tagItems = TableQuery[TagItemAssns]
}
