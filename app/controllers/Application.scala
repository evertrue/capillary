package controllers

import com.codahale.metrics.json.MetricsModule
import com.codahale.metrics.{Gauge, Metric, MetricFilter}
import com.fasterxml.jackson.databind.{ObjectMapper, ObjectWriter}
import org.coursera.metrics.datadog.DatadogReporter
import org.coursera.metrics.datadog.DatadogReporter.Expansion._
import org.coursera.metrics.datadog.transport.HttpTransport
import java.io.StringWriter
import java.util.concurrent.TimeUnit
import java.util.EnumSet

import models.Metrics
import models.ZkKafka
import org.coursera.metrics.datadog.TaggedName
import org.coursera.metrics.datadog.TaggedName.TaggedNameBuilder
import play.api.Play.current
import play.api._
import play.api.mvc._

import scala.language.implicitConversions
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object Application extends Controller {

  val validUnits = Some(Set("NANOSECONDS", "MICROSECONDS", "MILLISECONDS", "SECONDS", "MINUTES", "HOURS", "DAYS"))
  val mapper = new ObjectMapper()

  def registryName = Play.configuration.getString("capillary.metrics.name").getOrElse("default")
  def rateUnit     = Play.configuration.getString("capillary.metrics.rateUnit", validUnits).getOrElse("SECONDS")
  def durationUnit = Play.configuration.getString("capillary.metrics.durationUnit", validUnits).getOrElse("SECONDS")
  def showSamples  = Play.configuration.getBoolean("capillary.metrics.showSamples").getOrElse(false)
  def ddAPIKey     = Play.configuration.getString("capillary.metrics.datadog.apiKey")

  val module = new MetricsModule(rateUnit, durationUnit, showSamples)
  mapper.registerModule(module)

  val topologies = ZkKafka.getTopologies

  topologies.map({ t =>
    val deltas = ZkKafka.getTopologyDeltas(t.name+"/"+t.spoutRoot, t.topic)._2
    deltas.map({ d =>
      val name = new TaggedNameBuilder().metricName("kafkaLag")
        .addTag("app", "storm")
        .addTag("topology", t.name)
        .addTag("spout", t.spoutRoot)
        .addTag("consumer", t.name+"/"+t.spoutRoot)
        .addTag("object-type", t.name.split("-").head)
        .addTag("topic", t.topic)
        .addTag("partition", d.partition.toString)
        .build()
        .encode()

      if (!Metrics.metricRegistry.getGauges.containsKey(name)) {
        Metrics.metricRegistry.register(name, new Gauge[Long]() {
          var topoRoot = s"${t.name}/${t.spoutRoot}"
          var topic = s"${t.topic}"
          var partition = d.partition

          override def getValue: Long = {
            ZkKafka.getTopologyDeltas(t.name + "/" + t.spoutRoot, t.topic)._2.filter({ p => p.partition == partition }).head.amount.get
          }
        })
      }
    })
  })

  println("apikey1: " + ddAPIKey)

  ddAPIKey.map({ apiKey =>
    println("Starting Datadog Reporter")
    Logger.info("Starting Datadog Reporter")
    val expansions = EnumSet.of(COUNT, RATE_1_MINUTE, RATE_15_MINUTE, MEDIAN, P95, P99)
    val httpTransport = new HttpTransport.Builder().withApiKey(apiKey).build()
    val reporter = DatadogReporter.forRegistry(Metrics.metricRegistry)
      .withHost(sys.env("DD_AGENT_HOST"))
      .withTransport(httpTransport)
      .withExpansions(expansions)
      .filter(new MetricFilter {
        override def matches(name: String, metric: Metric): Boolean = !TaggedName.decode(name).getEncodedTags().isEmpty()
      })
      .build()

    reporter.start(20, TimeUnit.SECONDS)
  })

  implicit def stringToTimeUnit(s: String) : TimeUnit = TimeUnit.valueOf(s)

  def index = Action { implicit request =>

    val topos = ZkKafka.getTopologies

    Ok(views.html.index(topos))
  }

  def topo(name: String, topoRoot: String, topic: String) = Action { implicit request =>

    val totalsAndDeltas = ZkKafka.getTopologyDeltas(topoRoot, topic)
    val dateFormat  = DateTimeFormat.fullDateTime()
    val generatedAt = new DateTime().toString(dateFormat)
    Ok(views.html.topology(name, topic, totalsAndDeltas._1, totalsAndDeltas._2.toSeq, generatedAt))
  }

  def metrics = Action {
    val writer: ObjectWriter = mapper.writerWithDefaultPrettyPrinter()
    val stringWriter = new StringWriter()
    writer.writeValue(stringWriter, Metrics.metricRegistry)
    Ok(stringWriter.toString).as("application/json").withHeaders("Cache-Control" -> "must-revalidate,no-cache,no-store")
  }
}
