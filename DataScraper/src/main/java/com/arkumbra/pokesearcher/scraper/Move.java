package com.arkumbra.pokesearcher.scraper;

import java.math.BigDecimal;
import java.util.Set;

public class Move {

  private String idName;
  private Name name;
  private TypeValue type;
  private AttackCategory category;
  private int pp;
  private int power;
  private int accuracy;
  private BigDecimal critRatio;
  private Priority priority;
  private EffectChance effectChance;
  private MoveTarget moveTarget;
  private Set<AttackMetaData> attackMetaData;

  public String getIdName() {
    return idName;
  }

  public void setIdName(String idName) {
    this.idName = idName;
  }

  public Name getName() {
    return name;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public TypeValue getType() {
    return type;
  }

  public void setType(TypeValue type) {
    this.type = type;
  }

  public AttackCategory getCategory() {
    return category;
  }

  public void setCategory(AttackCategory category) {
    this.category = category;
  }

  public int getPp() {
    return pp;
  }

  public void setPp(int pp) {
    this.pp = pp;
  }

  public int getPower() {
    return power;
  }

  public void setPower(int power) {
    this.power = power;
  }

  public int getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(int accuracy) {
    this.accuracy = accuracy;
  }

  public BigDecimal getCritRatio() {
    return critRatio;
  }

  public void setCritRatio(BigDecimal critRatio) {
    this.critRatio = critRatio;
  }

  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }

  public EffectChance getEffectChance() {
    return effectChance;
  }

  public void setEffectChance(EffectChance effectChance) {
    this.effectChance = effectChance;
  }

  public MoveTarget getMoveTarget() {
    return moveTarget;
  }

  public void setMoveTarget(MoveTarget moveTarget) {
    this.moveTarget = moveTarget;
  }

  public Set<AttackMetaData> getAttackMetaData() {
    return attackMetaData;
  }

  public void setAttackMetaData(Set<AttackMetaData> attackMetaData) {
    this.attackMetaData = attackMetaData;
  }

}
