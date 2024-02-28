package com.arkumbra.pokesearcher.scraper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum TypeEffectiveness {

  grass(
      TypeValue.grass,
      Set.of(TypeValue.fire, TypeValue.bug, TypeValue.ice, TypeValue.flying, TypeValue.poison),
      Set.of(TypeValue.water, TypeValue.grass, TypeValue.electric, TypeValue.ground),
      Set.of()
  ),
  water(
      TypeValue.water,
      Set.of(TypeValue.electric, TypeValue.grass),
      Set.of(TypeValue.fire, TypeValue.water, TypeValue.ice, TypeValue.steel),
      Set.of()
  ),
  fire(
      TypeValue.fire,
      Set.of(TypeValue.water, TypeValue.ground, TypeValue.rock),
      Set.of(TypeValue.fire, TypeValue.grass, TypeValue.ice, TypeValue.bug, TypeValue.steel, TypeValue.fairy),
      Set.of()
  ),
  electric(
      TypeValue.electric,
      Set.of(TypeValue.ground),
      Set.of(TypeValue.electric, TypeValue.steel, TypeValue.flying),
      Set.of()
  ),
  normal(
      TypeValue.normal,
      Set.of(TypeValue.fighting),
      Set.of(),
      Set.of(TypeValue.ghost)
  ),
  fighting(
      TypeValue.fighting,
      Set.of(TypeValue.psychic, TypeValue.fairy, TypeValue.flying),
      Set.of(TypeValue.bug, TypeValue.rock, TypeValue.dark),
      Set.of()
  ),
  ice(
      TypeValue.ice,
      Set.of(TypeValue.fire, TypeValue.rock, TypeValue.fighting, TypeValue.steel),
      Set.of(TypeValue.ice),
      Set.of()
  ),
  psychic(
      TypeValue.psychic,
      Set.of(TypeValue.bug, TypeValue.dark, TypeValue.ghost),
      Set.of(TypeValue.psychic, TypeValue.fighting),
      Set.of()
  ),
  ground(
      TypeValue.ground,
      Set.of(TypeValue.water, TypeValue.grass, TypeValue.ice),
      Set.of(TypeValue.poison, TypeValue.rock),
      Set.of(TypeValue.electric)
  ),
  rock(
      TypeValue.rock,
      Set.of(TypeValue.water, TypeValue.ground, TypeValue.rock),
      Set.of(TypeValue.fire, TypeValue.grass, TypeValue.ice, TypeValue.bug, TypeValue.steel, TypeValue.fairy),
      Set.of()
  ),
  steel(
      TypeValue.steel,
      Set.of(TypeValue.fire, TypeValue.ground, TypeValue.fighting),
      Set.of(
          TypeValue.normal, TypeValue.grass, TypeValue.ice, TypeValue.flying, TypeValue.psychic, TypeValue.bug,
          TypeValue.rock, TypeValue.dragon, TypeValue.steel, TypeValue.fairy
      ),
      Set.of(TypeValue.poison)
  ),
  dragon(
      TypeValue.dragon,
      Set.of(TypeValue.ice, TypeValue.fairy, TypeValue.dragon),
      Set.of(TypeValue.electric, TypeValue.fire, TypeValue.water, TypeValue.grass),
      Set.of()
  ),
  dark(
      TypeValue.dark,
      Set.of(TypeValue.bug, TypeValue.fairy, TypeValue.fighting),
      Set.of(TypeValue.ghost, TypeValue.dark),
      Set.of(TypeValue.psychic)
  ),
  fairy(
      TypeValue.fairy,
      Set.of(TypeValue.poison, TypeValue.steel),
      Set.of(TypeValue.fighting, TypeValue.bug, TypeValue.dark),
      Set.of(TypeValue.dragon)
  ),
  poison(
      TypeValue.poison,
      Set.of(TypeValue.ground, TypeValue.psychic),
      Set.of(TypeValue.grass, TypeValue.fighting, TypeValue.poison, TypeValue.bug, TypeValue.fairy),
      Set.of()
  ),
  ghost(
      TypeValue.ghost,
      Set.of(TypeValue.ghost, TypeValue.dark),
      Set.of(TypeValue.poison, TypeValue.bug),
      Set.of(TypeValue.normal, TypeValue.fighting)
  ),
  flying(
      TypeValue.flying,
      Set.of(TypeValue.electric, TypeValue.ice, TypeValue.rock),
      Set.of(TypeValue.bug, TypeValue.grass, TypeValue.fighting),
      Set.of(TypeValue.ground)
  ),
  bug(
      TypeValue.bug,
      Set.of(TypeValue.fire, TypeValue.flying, TypeValue.rock),
      Set.of(TypeValue.grass, TypeValue.fighting, TypeValue.ground),
      Set.of()
  )
  ;


  private static final Map<TypeValue, TypeEffectiveness> byType = new HashMap<>();
  private final TypeValue type;
  private final Set<TypeValue> weaknesses;
  private final Set<TypeValue> resistances;
  private final Set<TypeValue> immunities;

  static {
    for (TypeEffectiveness value : TypeEffectiveness.values()) {
      TypeEffectiveness.byType.put(value.type, value);
    }
  }

  TypeEffectiveness(
      TypeValue type,
      Set<TypeValue> weaknesses,
      Set<TypeValue> resistances,
      Set<TypeValue> immunities) {

    this.type = type;
    this.weaknesses = weaknesses;
    this.resistances = resistances;
    this.immunities = immunities;
  }

  public static TypeEffectiveness of(TypeValue typeValue) {
    return TypeEffectiveness.byType.get(typeValue);
  }


  private Effectiveness againstSingleType(TypeValue defendingType) {
    TypeEffectiveness that = TypeEffectiveness.of(defendingType);
    
    if (that.immunities.contains(this.type))
      return Effectiveness.noEffect;
    if (that.resistances.contains(this.type))
      return Effectiveness.notVeryEffective2;
    if (that.weaknesses.contains(this.type))
      return Effectiveness.superEffective2x;
    else
      return Effectiveness.effective;
  }

  public Effectiveness against(TypeValue... defendingTypes) {
    Effectiveness result = Effectiveness.effective;

    for (TypeValue defendingType : defendingTypes) {
      result = result.merge(againstSingleType(defendingType));
    }

    return result;
  }

  public TypeValue getType() {
    return type;
  }

  public Set<TypeValue> getWeaknesses() {
    return weaknesses;
  }

  public Set<TypeValue> getResistances() {
    return resistances;
  }

  public Set<TypeValue> getImmunities() {
    return immunities;
  }
}
