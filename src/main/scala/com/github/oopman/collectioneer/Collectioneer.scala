package com.github.oopman.collectioneer

import slick.jdbc.H2Profile
import slick.jdbc.JdbcBackend.Database

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{ListView, Menu, MenuBar}
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.{BorderPane, GridPane, HBox}
import scalafx.scene.paint.Color._
import scalafx.scene.paint.{LinearGradient, Stops}
import scalafx.scene.text.Text
import com.github.oopman.collectioneer.model.DAL
import com.github.oopman.collectioneer.ui.CollectionsListView

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object Collectioneer extends JFXApp {
  val db = Database.forConfig("data")
  val dal = new DAL(H2Profile)
  import dal.driver.api._
  val setupPage = db.run(for {
    _ <- dal.create
    _ <- dal.populateCollections
  } yield ())
  Await.result(setupPage, Duration.Inf)

  stage = new PrimaryStage {
    onCloseRequest = { _ => db.close() }

    title = "Collectioneer"
    scene = new Scene {
      fill = White
      content = new BorderPane {
        top = new MenuBar {
          menus = List(
            new Menu("File") {},
            new Menu("Edit") {},
            new Menu("Help") {}
          )
        }
        center = new GridPane {
          val collectionsList = new CollectionsListView {
            items = ObservableBuffer()

            db.run(dal.collections.result.map(collections => {
              items = ObservableBuffer(collections)
            }))
          }
          GridPane.setConstraints(collectionsList, 0, 0, 1, 10)

//          val collectionDetails = new GridPane {
//
//          }
          val collectionDetails = new Text {
            text = "Collection Details and Actions"
          }
          GridPane.setConstraints(collectionDetails, 1, 0, 4, 1)

          val itemList = new ListView[String] {
            items = ObservableBuffer("Item 1", "Item 2", "Item 3")
          }
          GridPane.setConstraints(itemList, 1, 1, 2, 9)

//          val itemDetails = new GridPane  {
//
//          }
          val itemDetails = new Text {
            text = "Item Details and Actions"
          }
          GridPane.setConstraints(itemDetails, 3, 1, 2, 9)

          children = Seq(collectionsList, collectionDetails, itemList, itemDetails)
        }
      }

//      content = new HBox {
//        padding = Insets(20)
//        children = Seq(
//          new Text {
//            text = "Hello "
//            style = "-fx-font-size: 48pt"
//            fill = new LinearGradient(
//              endX = 0,
//              stops = Stops(PaleGreen, SeaGreen))
//          },
//          new Text {
//            text = "World!!!"
//            style = "-fx-font-size: 48pt"
//            fill = new LinearGradient(
//              endX = 0,
//              stops = Stops(Cyan, DodgerBlue)
//            )
//            effect = new DropShadow {
//              color = DodgerBlue
//              radius = 25
//              spread = 0.25
//            }
//          }
//        )
//      }
    }
  }
}
