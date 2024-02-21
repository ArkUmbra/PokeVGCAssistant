package com.arkumbra.pokesearcher.scraper;

public class BaseStats {
  
  private final int hp;
  private final int atk;
  private final int def;
  private final int specialAtk;
  private final int specialDef;
  private final int speed;

  public BaseStats(int hp, int atk, int def, int specialAtk, int specialDef, int speed) {
    this.hp = hp;
    this.atk = atk;
    this.def = def;
    this.specialAtk = specialAtk;
    this.specialDef = specialDef;
    this.speed = speed;
  }

  public int getHp() {
    return hp;
  }

  public int getAtk() {
    return atk;
  }

  public int getDef() {
    return def;
  }

  public int getSpecialAtk() {
    return specialAtk;
  }

  public int getSpecialDef() {
    return specialDef;
  }

  public int getSpeed() {
    return speed;
  }

  @Override
  public String toString() {
    return "BaseStats{" +
        "hp=" + hp +
        ", atk=" + atk +
        ", def=" + def +
        ", spAtk=" + specialAtk +
        ", spDef=" + specialDef +
        ", speed=" + speed +
        '}';
  }
}
