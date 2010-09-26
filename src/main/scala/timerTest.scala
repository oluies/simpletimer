package com.programmera.timer


/**
 * Created by IntelliJ IDEA.
 * User: orjan
 * Date: 2010-sep-25
 * Time: 21:49:39
 * To change this template use File | Settings | File Templates.
 */

class Testing extends UsingTimer {
  def calculate = {

    val tri  = Triangle(3, 4)
    val circ  = Circle(10)

    withTimer("both") {
       val hypotenuse = tri.calculateHypotenuse
	     val area = circ.calculateArea
    }
 }
}

case class Triangle(x: Double, y: Double) {
  def calculateHypotenuse: Double = math.sqrt(x * x + y * y)
}

case class Circle(r: Int) {
  def calculateArea: Double = r * r * math.Pi
}
object testing {
  def main(args : Array[String]) : Unit = {

    Timer.addTimer("both")
    
    val t = new Testing
    t.calculate

    System.out.println("Consumed time both = " + Timer.consumedTime("both"))

  }
}