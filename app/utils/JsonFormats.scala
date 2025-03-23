package utils

import models.ZkKafka
import models.ZkKafka._
import org.apache.commons.codec.digest.DigestUtils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTime
import play.api.i18n.Messages
import play.api.libs.json.Json._
import play.api.libs.json._

object JsonFormats {

  private def optionLongtoJsValue(maybeId: Option[Long]) = maybeId.map({ l => JsNumber(l) }).getOrElse(JsNull)
  
  // Add implicit Reads for Option[Long]
  implicit val optionLongReads: Reads[Option[Long]] = new Reads[Option[Long]] {
    def reads(json: JsValue): JsResult[Option[Long]] = json match {
      case JsNumber(n) => JsSuccess(Some(n.toLong))
      case JsNull => JsSuccess(None)
      case _ => JsError("Expected JsNumber or JsNull")
    }
  }

  implicit object DeltaFormat extends Format[Delta] {

    def reads(json: JsValue): JsResult[Delta] = JsSuccess(Delta(
      partition   = (json \ "partition").as[Int],
      amount      = (json \ "amount").as[Option[Long]],
      current     = (json \ "current").as[Long],
      storm       = (json \ "storm").as[Option[Long]]
    ))

    def writes(o: Delta): JsValue = {

      val doc: Map[String,JsValue] = Map(
        "partition"      -> JsNumber(o.partition),
        "amount"         -> optionLongtoJsValue(o.amount),
        "current"        -> JsNumber(o.current),
        "storm"          -> optionLongtoJsValue(o.storm)
      )
      toJson(doc)
    }
  }
}