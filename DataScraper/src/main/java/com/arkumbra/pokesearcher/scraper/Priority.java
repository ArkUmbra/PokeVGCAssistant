package com.arkumbra.pokesearcher.scraper;

public enum Priority {
  Standard(0),
  PlusOne(1),
  PlusTwo(2),
  PlusThree(3),
  MinusOne(-1),
  MinusTwo(-2),
  MinusThree(-3)
  ;

  private int priority;

  Priority(int priority) {
    this.priority = priority;
  }

  public static Priority ofNumber(int value) {
    for (Priority priority : Priority.values()) {
      if (priority.priority == value)
        return priority;
    }

    throw new RuntimeException("Unsupported priority value provided [" + value + "]");
  }
}
