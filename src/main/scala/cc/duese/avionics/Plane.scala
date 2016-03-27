package cc.duese.avionics

import akka.actor.{Actor, ActorLogging, Props}

object Plane {
  // returns the control surface to the actor that asks for them
  case object GiveMeControl
}

// we want the plane to own the altimeter and we're going to do
// that by passing in a specific factory we can use to build the altimeter
class Plane extends Actor with ActorLogging {
  import Plane._

  val altimeter = context.actorOf(Props[Altimeter], "Altimeter")
  val controls = context.actorOf(Props(new ControlSurfaces(altimeter)), "ControlSurfaces")

  override def receive: Receive = {
    case GiveMeControl =>
      log info s"Plane giving control"
      sender ! controls
  }
}
