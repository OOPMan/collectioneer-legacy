package com.github.oopman.collectioneer

import java.sql.{Clob, Timestamp}
import java.util.UUID

import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape


/**
  *
  * @param tag
  */
class Collection(tag: Tag) extends Table[(UUID, String, String, Clob, Timestamp, Timestamp, Boolean, Boolean)](tag, "collection") {
  def uuid = column[UUID]("uuid", O.PrimaryKey)
  def name = column[String]("name", O.Unique)
  def `type` = column[String]("type")
  def description = column[Clob]("description")
  def dateTimeCreated = column[Timestamp]("datetime_created")
  def dateTimeModified = column[Timestamp]("datetime_modified")
  def deleted = column[Boolean]("deleted")
  def active = column[Boolean]("active")

  def * : ProvenShape[(UUID, String, String, Clob, Timestamp, Timestamp, Boolean, Boolean)] =
    (uuid, name, `type`, description, dateTimeCreated, dateTimeModified, deleted, active)
}

/**
  *
  * @param tag
  */
class Item(tag: Tag) extends Table[(UUID, String, String, Clob, Timestamp, Timestamp, Boolean, Boolean)](tag, "item") {
  def uuid = column[UUID]("uuid", O.PrimaryKey)
  def name = column[String]("name")
  def version = column[String]("version")
  def data = column[Clob]("data")
  def dateTimeCreated = column[Timestamp]("datetime_created")
  def dateTimeModified = column[Timestamp]("datetime_modified")
  def deleted = column[Boolean]("deleted")
  def active = column[Boolean]("active")

  def * : ProvenShape[(UUID, String, String, Clob, Timestamp, Timestamp, Boolean, Boolean)] =
    (uuid, name, version, data, dateTimeCreated, dateTimeModified, deleted, active)
}
