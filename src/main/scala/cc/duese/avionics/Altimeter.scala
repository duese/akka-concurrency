package cc.duese.avionics

import akka.actor.{ Actor, ActorLogging, ActorSystem, Props }

// the duration package object extends Ints with some timing functionality.
import scala.concurrent.duration._

object Altimeter {
  case class RateChange(amount: Float)
}

/**
  * class as of pages 83 and 84
  */
class Altimeter extends Actor with ActorLogging {
  import Altimeter._

  // execution context needed for scheduler
  implicit val ec = context.dispatcher

  // maximum ceiling of the plane. Unit 'feet#
  val ceiling = 43000

  // max rate of climb for the plane. Unit 'feet per minute'
  val maxRateOfClimb = 5000

  // varying rate of climb depending on the movement of the stick
  var rateOfClimb = 0f

  // current altitude
  var altitude = 0d

  // as time passes, we need to change the altitude based on the time passed.
  // the lastTick allows us to figure out how much time has passed
  var lastTick = System.currentTimeMillis

  // we need to periodically update our altitude.
  // this scheduled message send will tell us when to do that
  val ticker = context.system.scheduler.schedule(100.millis, 100.millis, self, Tick)

  // an internal message we send to ourselves to tell us to update our altitude
  case object Tick

  override def receive: Receive = {
    // our climb rate has changed
    case RateChange(amount) =>
      // truncate the range of 'amount' to [-1, 1] before multiplying
      rateOfClimb = amount.min(1.0f).max(-1.0f) * maxRateOfClimb
      log info s"altimeter changed rate of climb to $rateOfClimb"

    // calculate a new altitude
    case Tick =>
      val tick = System.currentTimeMillis
      altitude += ((tick - lastTick) / 60000.0) * rateOfClimb
      lastTick = tick
  }

  // kill ticker when we stop
  override def postStop(): Unit = ticker.cancel
}