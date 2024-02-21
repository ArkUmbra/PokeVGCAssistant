package com.arkumbra.pokesearcher.scraper;

import java.util.Objects;

public class BaseStats {
  
  private final int hp;
  private final int atk;
  private final int def;
  private final int specialAtk;
  private final int specialDef;
  private final int speed;

  private final int total;

  public BaseStats(int hp, int atk, int def, int specialAtk, int specialDef, int speed) {
    this.hp = hp;
    this.atk = atk;
    this.def = def;
    this.specialAtk = specialAtk;
    this.specialDef = specialDef;
    this.speed = speed;

    this.total = hp + atk + def + specialAtk + specialDef + speed;
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

  public int getTotal() {
    return total;
  }

  @Override
  public String toString() {
    return "BaseStats{" +
        "total=" + total +
        ", hp=" + hp +
        ", atk=" + atk +
        ", def=" + def +
        ", spAtk=" + specialAtk +
        ", spDef=" + specialDef +
        ", speed=" + speed +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BaseStats baseStats = (BaseStats) o;
    return hp == baseStats.hp &&
        atk == baseStats.atk &&
        def == baseStats.def &&
        specialAtk == baseStats.specialAtk &&
        specialDef == baseStats.specialDef &&
        speed == baseStats.speed;
  }

  @Override
  public int hashCode() {
    return Objects.hash(hp, atk, def, specialAtk, specialDef, speed);
  }
}
