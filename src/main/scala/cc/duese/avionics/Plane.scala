package cc.duese.avionics

import akka.actor.{Actor, ActorLogging, Props}
import EventSource.RegisterListener

object Plane {
  // returns the control surface to the actor that asks for them
  case object GiveMeControl
}

// we want the plane to own the altimeter and we're going to do
// that by passing in a specific factory we can use to build the altimeter
class Plane extends Actor with ActorLogging {
  import Plane._
  import Altimeter._

  val altimeter = context.actorOf(Props(Altimeter()), "Altimeter")
  val controls = context.actorOf(Props(new ControlSurfaces(altimeter)), "ControlSurfaces")

  override def preStart(): Unit = {
    altimeter ! RegisterListener(self)
  }

  override def receive: Receive = {
    case GiveMeControl =>
      log info s"Plane giving control"
      sender ! controls
    case AltitudeUpdate(altitude) =>
      val string = s"Altitude is now $altitude"
      log info string
      //println(string)
  }
}
