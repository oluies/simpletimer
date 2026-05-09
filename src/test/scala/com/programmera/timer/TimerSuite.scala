package com.programmera.timer

class TimerSuite extends munit.FunSuite:

  override def beforeEach(context: BeforeEach): Unit =
    Timer.reset()

  test("addTimer registers a new timer with zero consumed time") {
    Timer.addTimer("a")
    assertEquals(Timer.consumedTime("a"), 0L)
  }

  test("addTimer twice with the same name throws") {
    Timer.addTimer("dup")
    intercept[IllegalArgumentException](Timer.addTimer("dup"))
  }

  test("consumedTime on an unknown timer throws") {
    intercept[IllegalArgumentException](Timer.consumedTime("missing"))
  }

  test("withTimer returns the block's value and records elapsed time") {
    Timer.addTimer("sleep")
    val timing = new UsingTimer {}
    val result = timing.withTimer("sleep") {
      Thread.sleep(20)
      42
    }
    assertEquals(result, 42)
    assert(
      Timer.consumedTime("sleep") >= 15_000_000L,
      s"expected >= 15ms, got ${Timer.consumedTime("sleep")} ns"
    )
  }

  test("withTimer records elapsed time even when the block throws") {
    Timer.addTimer("boom")
    val timing = new UsingTimer {}
    intercept[RuntimeException] {
      timing.withTimer("boom") {
        Thread.sleep(5)
        throw new RuntimeException("nope")
      }
    }
    assert(Timer.consumedTime("boom") > 0L)
  }

  test("independent timers track their own elapsed times") {
    Timer.addTimer("one")
    Timer.addTimer("two")
    val timing = new UsingTimer {}
    timing.withTimer("one")(Thread.sleep(5))
    timing.withTimer("two")(Thread.sleep(15))
    assert(Timer.consumedTime("two") > Timer.consumedTime("one"))
  }
