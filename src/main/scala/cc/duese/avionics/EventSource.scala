package cc.duese.avionics

import akka.actor.{Actor, ActorRef}

object EventSource {
  // messages used by listeners to register and unregister themselves
  case class RegisterListener(listener: ActorRef)
  case class UnregisterListener(listener: ActorRef)
}

trait EventSource {
  this: Actor =>
  import EventSource._
  // we're going to use a vector but many structures would be adequate
  var listeners = Vector.empty[ActorRef]

  def sendEvent[T](event: T): Unit = listeners foreach {
    _ ! event
  }

  // we create a specific partial function to handle the messages
  // for out event listener. anything that mixes in our trait
  // will need to compose this receiver.
  def eventSourceReceive: Receive = {
    case RegisterListener(listener) =>
      listeners = listeners :+ listener
    case UnregisterListener(listener) =>
      listeners = listeners filter { _ != listener }
  }
}
