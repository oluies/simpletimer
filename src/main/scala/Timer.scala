package com.programmera.timer

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

trait UsingTimer:
  def withTimer[T](name: String)(f: => T): T =
    Timer(name).invoke(f)

object Timer:
  private val timerMap = ConcurrentHashMap[String, Timer]()

  /** Register a new timer under `name`. Throws if one already exists. */
  def addTimer(name: String): Unit =
    val previous = timerMap.putIfAbsent(name, new TimerImpl())
    if previous != null then
      throw new IllegalArgumentException(s"Timer $name already created")

  /** Nanoseconds consumed by the most recent invocation of `name`. */
  def consumedTime(name: String): Long =
    lookup(name).consumedTime

  private[timer] def apply(name: String): Timer = lookup(name)

  private[timer] def reset(): Unit = timerMap.clear()

  private def lookup(name: String): Timer =
    timerMap.get(name) match
      case null => throw new IllegalArgumentException(s"Timer $name not available")
      case t    => t

private[timer] trait Timer:
  def consumedTime: Long
  def invoke[T](f: => T): T

private[timer] class TimerImpl extends Timer:
  private val _consumedTime = new AtomicLong

  def consumedTime: Long = _consumedTime.get

  def invoke[T](f: => T): T =
    val start = System.nanoTime()
    try
      val ret = f
      _consumedTime.set(System.nanoTime() - start)
      ret
    catch
      case e: Throwable =>
        _consumedTime.set(System.nanoTime() - start)
        throw e
