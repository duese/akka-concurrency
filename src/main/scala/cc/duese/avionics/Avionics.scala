package cc.duese.avionics

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.duration._

// the futures created by the ask syntax need an execution context
// on which to run, and we will use the default global instance for that context
import scala.concurrent.ExecutionContext.Implicits.global


object Avionics {
  // needed for '?' below
  implicit val timeout = Timeout(5.seconds)

  val system = ActorSystem("PlaneSimulation")
  val plane = system.actorOf(Props[Plane], "Plane")

  def main(args: Array[String]): Unit = {
    // grab the controls
    val control = Await.result((plane ? Plane.GiveMeControl).mapTo[ActorRef], 5.seconds)
    // takeoff
    system.scheduler.scheduleOnce(200.millis) {
      control ! ControlSurfaces.StickBack(1.0f)
    }
    // level out
    system.scheduler.scheduleOnce(1.second) {
      control ! ControlSurfaces.StickBack(0.0f)
    }
    // climb
    system.scheduler.scheduleOnce(3.second) {
      control ! ControlSurfaces.StickBack(0.5f)
    }
    // level out
    system.scheduler.scheduleOnce(4.second) {
      control ! ControlSurfaces.StickBack(0.0f)
    }
    // shutdown
    system.scheduler.scheduleOnce(5.second) {
      system.terminate()
    }
  }
}
