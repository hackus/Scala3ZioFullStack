package com.learn

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom
import com.learn.Model.*
import zio.schema.codec.JsonCodec

final class State:
  val dataVar = Var(List.empty[DataItem])
  val dataSignal = dataVar.signal
  def addDataItem(item: DataItem): Unit = dataVar.update(_ :+ item)
  def removeDataItem(id: DataItemID): Unit = dataVar.update(_.filter(_.id != id))
end State

@main
def ClientApp(): Unit =
  renderOnDomContentLoaded(dom.document.getElementById("app"), Main.appElement())
end ClientApp

object Main:
  val dataUrl = "http://localhost:9000/data"
  // Codecs from common lib
  val dataItemCodec = JsonCodec.jsonCodec(Model.given_Schema_DataList)
  val saveDialog = dialogTag()
  saveDialog.amend(
    p("Data saved!"),
    button(autoFocus := true, "close", onClick --> { _ => saveDialog.ref.close() })
  )

  // Initialise fe state
  val model = new State
  import model.*

  def appElement(): Element =
    div(
      FetchStream.get(dataUrl) --> decodeData, // Load initial data from BE
      h1("Live Chart"),
      saveDialog,
      renderDataTable(),
    )
  end appElement

  def decodeData(str: String): Unit =
    dataItemCodec.decoder.decodeJson(str) match {
      case Right(d) => dataVar.update(_ => d)
      case Left(e) => println(s"ERROR: $e")
    }
  end decodeData

  def postData(dataList: DataList): EventStream[String] =
    val FetchDataList = FetchStream.withEncoder[DataList](dataItemCodec.encoder.encodeJson(_).toString)
    val jsonContentType = "content-type" -> "application/json; charset=utf-8"
    FetchDataList.post(dataUrl, _.body(dataList), _.headers(jsonContentType))
  end postData


  def renderDataTable(): Element =
    table(
      thead(tr(th("Label"), th("Price"), th("Count"), th("Review"), th("Full price"), th("Action"))),
      tbody(
        children <-- dataSignal.split(_.id) { (id, _, itemSignal) => renderDataItem(id, itemSignal) },
      ),
      tfoot(tr(
        td(button("➕", onClick --> { _ => addDataItem(DataItem()) })),
        // save button stores data in BE
        td(button("💾", onClick.flatMap(_ => postData(dataSignal.now())) --> { _ => saveDialog.ref.showModal() })),
        td(),
        td(),
        td(child.text <-- dataSignal.map(data => "%.2f".format(data.map(_.fullPrice).sum))),
        td(),
      )),
    )
  end renderDataTable

  def renderDataItem(id: DataItemID, itemSignal: Signal[DataItem]): Element =
    tr(
      td(
        inputForString(
          itemSignal.map(_.label),
          makeDataItemUpdater(id, (item, newLabel) => item.copy(label = newLabel))
        )
      ),
      td(
        inputForDouble(
          itemSignal.map(_.price),
          makeDataItemUpdater(id, (item, newPrice) => item.copy(price = newPrice))
        )
      ),
      td(
        inputForInt(
          itemSignal.map(_.count),
          makeDataItemUpdater(id, (item, newCount) => item.copy(count = newCount))
        )
      ),
      td(child.text <-- itemSignal.map(_.review.toString)),
      td(child.text <-- itemSignal.map(item => "%.2f".format(item.fullPrice))),
      td(button("🗑️", onClick --> { _ => removeDataItem(id) })),
    )
  end renderDataItem

  def makeDataItemUpdater[A](id: DataItemID, f: (DataItem, A) => DataItem): Observer[A] =
    dataVar.updater { (data, newValue) =>
      data.map(item => if item.id == id then f(item, newValue) else item)
    }
  end makeDataItemUpdater

  // components for input text
  def inputForString(valueSignal: Signal[String], valueUpdater: Observer[String]): Input =
    input(
      typ := "text",
      value <-- valueSignal,
      onInput.mapToValue --> valueUpdater,
    )
  end inputForString

  def inputForDouble(valueSignal: Signal[Double], valueUpdater: Observer[Double]): Input =
    val strValue = Var[String]("")
    input(
      typ := "text",
      value <-- strValue.signal,
      onInput.mapToValue --> strValue,
      valueSignal --> strValue.updater[Double] { (prevStr, newValue) =>
        if prevStr.toDoubleOption.contains(newValue) then prevStr else newValue.toString
      },
      strValue.signal --> { _.toDoubleOption.foreach(valueUpdater.onNext) },
    )
  end inputForDouble

  def inputForInt(valueSignal: Signal[Int], valueUpdater: Observer[Int]): Input =
    input(
      typ := "text",
      controlled(
        value <-- valueSignal.map(_.toString),
        onInput.mapToValue.map(_.toIntOption).collect { case Some(newCount) => newCount } --> valueUpdater,
      ),
    )
  end inputForInt

end Main