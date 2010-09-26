package com.programmera.timer

import collection.immutable.HashMap
import java.util.concurrent.atomic.AtomicLong


trait UsingTimer {
  def withTimer[T](name: String)(f: => T): T = {
    Timer(name).invoke(f)
  }
}

object Timer {
  /**
   * timers
   */
  private var timerMap = HashMap[String, Timer]()

  /**
   * factory method
   * creates a new timer with a given name
   *
   * @param name id of the new timer
   */
  def addTimer(name: String): Unit = {
    timerMap.get(name) match {
      case None => timerMap += ((name, new TimerImpl()))
      case Some(x) => throw new IllegalArgumentException("Timer " + name + " already created")
   }
  }

   def consumedTime(name: String): Long = {
      timerMap.get(name) match {
      	case Some(x) => x.consumedTime
      	case None => throw new IllegalArgumentException("Timer " + name + " not avaliable")
    }
   }

  /**
   * Retrieve specific timer via apply method
   *
   * @param name  id of the Timer
   * @return Timer with id 
   */
  private[timer] def apply(name: String): Timer = {
    timerMap.get(name) match {
      case Some(x) => x
      case None => throw new java.lang.IllegalArgumentException("Timer " + name + " not avaliable")
    }
  }

  }

 private[timer] trait Timer {

	   /**
	   * @return Long nanoseconds timer
	   */
	  def consumedTime: Long

	  /**
	   * function scope
	   */
	  def invoke[T](f: => T): T
}

/**
 * Timer implementation class
 *
 */
private[timer] class TimerImpl extends Timer
{
  private var _consumedTime = new AtomicLong

  private def consumedTime_=(l: Long) {
    _consumedTime.set(l)
  }
  def consumedTime = _consumedTime.get

  def invoke[T](f: => T): T = {
    val start = System.nanoTime()

    def calcConsumedTime: Unit = {
      val end = System.nanoTime()
      consumedTime = end - start
    }

    try {
      val ret = f
      calcConsumedTime
      ret
    }
    catch {
      case e: Throwable => {
        calcConsumedTime
        throw e
      }
    }
  }
}

