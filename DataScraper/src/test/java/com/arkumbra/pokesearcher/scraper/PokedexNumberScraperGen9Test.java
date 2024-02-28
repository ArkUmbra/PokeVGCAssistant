package com.arkumbra.pokesearcher.scraper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static com.arkumbra.pokesearcher.scraper.TestUtils.assertOptionalEmpty;
import static org.junit.jupiter.api.Assertions.*;

public class PokedexNumberScraperGen9Test {

  private PokedexScraper scraper;

  @BeforeEach
  public void setUp() {
    this.scraper = new PokedexScraper();
  }

  @Test
  public void testExecute() throws IOException {
    scraper.execute();
  }

  @Test
  public void testDifferentlyFormattedPokemonPages() throws IOException {
    // New SV Poke
    Pokemon tinkaton = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/tinkaton/");
    System.out.println(tinkaton);

    assertFalse(tinkaton.hasAlternateForms());
    assertNames(new Name("Tinkaton"), tinkaton.getName());
    assertPokedex(
        mapOfNullable(
            "Paldea", 281,
            "National", 959,
            "Blueberry", null,
            "Kitakami", null
        ), tinkaton.getPokedexNumber());
    assertEquals(new Abilities(List.of("moldbreaker", "owntempo"), "pickpocket"),
        tinkaton.getDetails().getAbilities());
    assertEquals(new BaseStats(85, 75, 77,70,105,94),
        tinkaton.getDetails().getBaseStats());

    // Pokemon from previous generation that is available in SV
    Pokemon rillaboom = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/rillaboom/");
    System.out.println(rillaboom);

    assertFalse(rillaboom.hasAlternateForms());
    assertNames(new Name("Rillaboom"), rillaboom.getName());
    assertPokedex(
        mapOfNullable(
            "Paldea", null,
            "National", 812,
            "Blueberry", 229,
            "Kitakami", null
        ), rillaboom.getPokedexNumber());
    assertEquals(new Abilities(List.of("overgrow"), "grassysurge"),
        rillaboom.getDetails().getAbilities());
    assertEquals(new BaseStats(100,125, 90, 60, 70, 85),
        rillaboom.getDetails().getBaseStats());


    // No Egg moves
    Pokemon hooh = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/ho-oh/");
    System.out.println(hooh);
    assertNames(new Name("Ho-Oh"), hooh.getName());
    assertPokedex(
        mapOfNullable(
            "Paldea", null,
            "National", 250,
            "Blueberry", null,
            "Kitakami", null
        ), hooh.getPokedexNumber());
    assertFalse(hooh.hasAlternateForms());
    assertEquals(new Abilities(List.of("pressure"), "regenerator"),
        hooh.getDetails().getAbilities());
    assertEquals(new BaseStats(106, 130, 90, 110, 154, 90),
        hooh.getDetails().getBaseStats());
  }

  @Test
  public void testMultiFormPokemonScrapeCorrectly() throws IOException {

    // ------
    // Different types across forms
    Pokemon weezing = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/weezing/");
    System.out.println(weezing);

    assertNames(new Name("Weezing"), weezing.getName());
    assertPokedex(
        mapOfNullable(
            "Paldea", null,
            "National", 110,
            "Blueberry", null,
            "Kitakami", 136
        ), weezing.getPokedexNumber());
    assertTrue(weezing.hasAlternateForms());
    assertEquals(1, weezing.getAlternateForms().size());

    PokemonDetails kantoWeezing = weezing.getDetails();
    assertEquals("Kantonian Form", kantoWeezing.getFormName());
    assertEquals(linkedHashSet(TypeValue.poison), kantoWeezing.getTypes());

    PokemonDetails galarWeezing = weezing.getAlternateForms().get(0);
    assertEquals("Galarian Form", galarWeezing.getFormName());
    assertEquals(linkedHashSet(TypeValue.poison, TypeValue.fairy), galarWeezing.getTypes());

    assertTrue(galarWeezing.getMoveset().getLevelUpMoves()
        .contains(new Pair<>(new MoveAcquire(MoveAcquireType.Relearner), "aromaticmist")));
    assertFalse(kantoWeezing.getMoveset().getLevelUpMoves()
        .contains(new Pair<>(new MoveAcquire(MoveAcquireType.Relearner), "aromaticmist")));

    assertTrue(galarWeezing.getMoveset().getTmMoves()
        .contains(new Pair<>(new MoveAcquire(MoveAcquireType.TM, 79), "dazzlinggleam")));
    assertFalse(kantoWeezing.getMoveset().getTmMoves()
        .contains(new Pair<>(new MoveAcquire(MoveAcquireType.TM, 79), "dazzlinggleam")));

    assertTrue(galarWeezing.getMoveset().getTmMoves()
        .contains(new Pair<>(new MoveAcquire(MoveAcquireType.TM, 91), "toxicspikes")));
    assertTrue(kantoWeezing.getMoveset().getTmMoves()
        .contains(new Pair<>(new MoveAcquire(MoveAcquireType.TM, 91), "toxicspikes")));

    // Apparently Galar Weezing gets all the moves of normal Weezing along with some extra
    kantoWeezing.getMoveset().getLevelUpMoves().forEach(levelUpMove -> {
      assertTrue(galarWeezing.getMoveset().getLevelUpMoves().stream().map(Pair::getRight).toList()
          .contains(levelUpMove.getRight()), "Galar Weezing doesn't learn " + levelUpMove);
    });

    kantoWeezing.getMoveset().getTmMoves().forEach(levelUpMove -> {
      assertTrue(galarWeezing.getMoveset().getTmMoves().stream().map(Pair::getRight).toList()
          .contains(levelUpMove.getRight()), "Galar Weezing doesn't learn " + levelUpMove);
    });

    assertEquals(kantoWeezing.getMoveset().getEggMoves(), galarWeezing.getMoveset().getEggMoves());
    assertEquals(List.of(
          "curse",
          "spite",
          "painsplit",
          "stockpile",
          "spitup",
          "swallow"
        ),
        kantoWeezing.getMoveset().getEggMoves());

    assertEquals(galarWeezing.getBaseStats(), kantoWeezing.getBaseStats());
    assertEquals(new BaseStats(65, 90, 120, 85, 70, 60),
        galarWeezing.getBaseStats());

    assertEquals(2, kantoWeezing.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(kantoWeezing.getAbilities().getStandardAbilityNameIds().contains("levitate"));
    assertTrue(kantoWeezing.getAbilities().getStandardAbilityNameIds().contains("neutralizinggas"));
    assertEquals("stench", kantoWeezing.getAbilities().getHiddenAbilityNameId().get());

    assertEquals(2, galarWeezing.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(galarWeezing.getAbilities().getStandardAbilityNameIds().contains("levitate"));
    assertTrue(galarWeezing.getAbilities().getStandardAbilityNameIds().contains("neutralizinggas"));
    assertEquals("mistysurge", galarWeezing.getAbilities().getHiddenAbilityNameId().get());

    // ------
    // Alola
    Pokemon sandshrew = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/sandshrew/");
    System.out.println(sandshrew);

    assertNames(new Name("Sandshrew"), sandshrew.getName());
    assertTrue(sandshrew.hasAlternateForms());
    assertEquals(1, sandshrew.getAlternateForms().size());

    PokemonDetails kantoSandshrew = sandshrew.getDetails();
    assertEquals("Kantonian Form", kantoSandshrew.getFormName());
    assertEquals(linkedHashSet(TypeValue.ground), kantoSandshrew.getTypes());
    assertFalse(kantoSandshrew.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(kantoSandshrew.getMoveset().getTmMoves().isEmpty());
    assertFalse(kantoSandshrew.getMoveset().getEggMoves().isEmpty());

    PokemonDetails alolaSandshrew = sandshrew.getAlternateForms().get(0);
    assertEquals("Alola Form", alolaSandshrew.getFormName());
    assertEquals(linkedHashSet(TypeValue.ice, TypeValue.steel), alolaSandshrew.getTypes());
    assertFalse(alolaSandshrew.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(alolaSandshrew.getMoveset().getTmMoves().isEmpty());
    assertFalse(alolaSandshrew.getMoveset().getEggMoves().isEmpty());

    assertEquals(1, kantoSandshrew.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(kantoSandshrew.getAbilities().getStandardAbilityNameIds().contains("sandveil"));
    assertEquals("sandrush", kantoSandshrew.getAbilities().getHiddenAbilityNameId().get());

    assertEquals(1, alolaSandshrew.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(alolaSandshrew.getAbilities().getStandardAbilityNameIds().contains("snowcloak"));
    assertEquals("slushrush", alolaSandshrew.getAbilities().getHiddenAbilityNameId().get());



    // -----
    // Use of 'forme'
    // Same types across formes
    Pokemon landorus = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/landorus/");
    System.out.println(landorus);

    assertNames(new Name("Landorus"), landorus.getName());
    assertTrue(landorus.hasAlternateForms());
    assertEquals(1, landorus.getAlternateForms().size());

    PokemonDetails incarnateLandorus = landorus.getDetails();
    assertEquals("Incarnate Forme", incarnateLandorus.getFormName());
    assertEquals(linkedHashSet(TypeValue.ground, TypeValue.flying), incarnateLandorus.getTypes());

    assertEquals(landorus.getDetails().getMoveset(), incarnateLandorus.getMoveset());
    assertFalse(landorus.getDetails().getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(landorus.getDetails().getMoveset().getTmMoves().isEmpty());
    assertTrue(landorus.getDetails().getMoveset().getEggMoves().isEmpty(),
        "Contained egg moves!! " + landorus.getDetails().getMoveset().getEggMoves());

    PokemonDetails therianLandorus = landorus.getAlternateForms().get(0);
    assertEquals("Therian Forme", therianLandorus.getFormName());
    assertEquals(linkedHashSet(TypeValue.ground, TypeValue.flying), therianLandorus.getTypes());


    assertEquals(1, incarnateLandorus.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(incarnateLandorus.getAbilities().getStandardAbilityNameIds().contains("sandforce"));
    assertEquals("sheerforce", incarnateLandorus.getAbilities().getHiddenAbilityNameId().get());

    assertEquals(1, therianLandorus.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(therianLandorus.getAbilities().getStandardAbilityNameIds().contains("intimidate"));
    assertOptionalEmpty(therianLandorus.getAbilities().getHiddenAbilityNameId());

    // -----
    // Extra form due to ability activation
    Pokemon palafin = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/palafin/");
    System.out.println(palafin);

    assertNames(new Name("Palafin"), palafin.getName());
    assertTrue(palafin.hasAlternateForms());
    assertEquals(1, palafin.getAlternateForms().size());

    PokemonDetails zeroPalafin = palafin.getDetails();
    assertEquals("Zero Form", zeroPalafin.getFormName());
    assertEquals(linkedHashSet(TypeValue.water), zeroPalafin.getTypes());

    PokemonDetails heroPalafin = palafin.getAlternateForms().get(0);
    assertEquals("Hero Form", heroPalafin.getFormName());
    assertEquals(linkedHashSet(TypeValue.water), heroPalafin.getTypes());

    // movesets should be the same between forms
    assertEquals(zeroPalafin.getMoveset(), heroPalafin.getMoveset());
    assertFalse(zeroPalafin.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(zeroPalafin.getMoveset().getTmMoves().isEmpty());
    assertFalse(zeroPalafin.getMoveset().getEggMoves().isEmpty());

    assertEquals(heroPalafin.getAbilities(), zeroPalafin.getAbilities());
    assertEquals(1, heroPalafin.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(heroPalafin.getAbilities().getStandardAbilityNameIds().contains("zerotohero"));
    assertOptionalEmpty(heroPalafin.getAbilities().getHiddenAbilityNameId());

    // -----
    // More than 2 forms
    Pokemon deoxys = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/deoxys/");
    System.out.println(deoxys);

    assertNames(new Name("Deoxys"), deoxys.getName());
    assertTrue(deoxys.hasAlternateForms());
    assertEquals(3, deoxys.getAlternateForms().size());

    PokemonDetails normalDeoxys = deoxys.getDetails();
    assertEquals("Normal Forme", normalDeoxys.getFormName());
    assertEquals(linkedHashSet(TypeValue.psychic), normalDeoxys.getTypes());
    assertFalse(normalDeoxys.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(normalDeoxys.getMoveset().getTmMoves().isEmpty());
    assertTrue(normalDeoxys.getMoveset().getEggMoves().isEmpty());
    assertEquals(new BaseStats(50, 150, 50, 150, 50, 150),
        normalDeoxys.getBaseStats());

    PokemonDetails attackDeoxys = deoxys.getAlternateForms().get(0);
    assertEquals("Attack Forme", attackDeoxys.getFormName());
    assertEquals(linkedHashSet(TypeValue.psychic), attackDeoxys.getTypes());
    assertFalse(attackDeoxys.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(attackDeoxys.getMoveset().getTmMoves().isEmpty());
    assertTrue(attackDeoxys.getMoveset().getEggMoves().isEmpty());
    assertEquals(new BaseStats(50, 180, 20, 180, 20, 150),
        attackDeoxys.getBaseStats());

    PokemonDetails defenseDeoxys = deoxys.getAlternateForms().get(1);
    assertEquals("Defense Forme", defenseDeoxys.getFormName());
    assertEquals(linkedHashSet(TypeValue.psychic), defenseDeoxys.getTypes());
    assertFalse(defenseDeoxys.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(defenseDeoxys.getMoveset().getTmMoves().isEmpty());
    assertTrue(defenseDeoxys.getMoveset().getEggMoves().isEmpty());
    assertEquals(new BaseStats(50, 70, 160, 70, 160, 90),
        defenseDeoxys.getBaseStats());

    PokemonDetails speedDeoxys = deoxys.getAlternateForms().get(2);
    assertEquals("Speed Forme", speedDeoxys.getFormName());
    assertEquals(linkedHashSet(TypeValue.psychic), speedDeoxys.getTypes());
    assertFalse(speedDeoxys.getMoveset().getLevelUpMoves().isEmpty());
    assertFalse(speedDeoxys.getMoveset().getTmMoves().isEmpty());
    assertTrue(speedDeoxys.getMoveset().getEggMoves().isEmpty());
    assertEquals(new BaseStats(50, 95, 90, 95, 90, 180),
        speedDeoxys.getBaseStats());

    // All forms get thunderbolt
    Pair<MoveAcquire, String> tmThunderbolt = new Pair<>(new MoveAcquire(MoveAcquireType.TM, 126), "thunderbolt");
    assertTrue(deoxys.getDetails().getMoveset().getTmMoves().contains(tmThunderbolt));
    deoxys.getAlternateForms().forEach(form -> assertTrue(form.getMoveset().getTmMoves().contains(tmThunderbolt)));

    // Only defense and speed form get amnesia
    Pair<MoveAcquire, String> tmAmnesia = new Pair<>(new MoveAcquire(MoveAcquireType.TM, 128), "amnesia");
    assertFalse(deoxys.getDetails().getMoveset().getTmMoves().contains(tmAmnesia));
    deoxys.getAlternateForms().forEach(form -> {
      if (form.getFormName().equals("Defense Forme") || form.getFormName().equals("Speed Forme")) {
        assertTrue(form.getMoveset().getTmMoves().contains(tmAmnesia), "Expected " + form.getFormName() + " to learn Amnesia");
      } else {
        assertFalse(form.getMoveset().getTmMoves().contains(tmAmnesia), "Expected " + form.getFormName() + " to not learn Amnesia");
      }

    });


    assertEquals(normalDeoxys.getAbilities(), attackDeoxys.getAbilities());
    assertEquals(normalDeoxys.getAbilities(), defenseDeoxys.getAbilities());
    assertEquals(normalDeoxys.getAbilities(), speedDeoxys.getAbilities());
    assertEquals(1, normalDeoxys.getAbilities().getStandardAbilityNameIds().size());
    assertTrue(normalDeoxys.getAbilities().getStandardAbilityNameIds().contains("pressure"));
    assertOptionalEmpty(normalDeoxys.getAbilities().getHiddenAbilityNameId());

    // TODO Ogerpon
  }

  @Test
  public void testSplitMultiFormAbilityStrings() {
    // Weezing
    String abilitiesRaw = "Abilities: Levitate - Neutralizing Gas - Stench (Hidden) (Normal Forme) - Levitate- Neutralizing Gas - Misty Surge (Hidden) (Galarian Form)";
//    List<String> formNames = List.of("Normal", "Galarian Form");

    List<String> expected = List.of(
        "Levitate - Neutralizing Gas - Stench (Hidden)",
        "Levitate- Neutralizing Gas - Misty Surge (Hidden)"
    );

    List<String> result = scraper.splitUpAbilitiesHeaderStringForMultiFormPokemon(abilitiesRaw);
    assertEquals(expected, result);

    // Ogerpon
    abilitiesRaw = "Abilities: Defiant (Teal Mask) - Water Absorb (Wellspring Mask) - Mold Breaker (Hearthflame Mask) - Sturdy (Cornerstone Mask)";

    expected = List.of(
        "Defiant",
        "Water Absorb",
        "Mold Breaker",
        "Sturdy"
    );

    result = scraper.splitUpAbilitiesHeaderStringForMultiFormPokemon(abilitiesRaw);
    assertEquals(expected, result);
  }

  private <A> LinkedHashSet<A> linkedHashSet(A... objects) {
    return new LinkedHashSet<>(Arrays.asList(objects));
  }

  private Map<String, Integer> mapOfNullable(Object... keyAndValues) {
    Map<String, Integer> map = new HashMap<>();

    for (int i = 0; i < keyAndValues.length; i += 2) {
      map.put((String)keyAndValues[i], (Integer)keyAndValues[i + 1]);
    }

    return map;
  }

  private void assertNames(Name expected, Name actual) {
    assertEquals(expected.englishName, actual.englishName);
  }

  private void assertPokedex(Map<String, Integer> expectedDex, PokedexNumber pokedexNumber) {
    assertEquals(expectedDex.size(), pokedexNumber.dexNumbers.size(),
        "Pokedex dex entries size did not match. " + expectedDex + ", " + pokedexNumber);

    assertEquals(expectedDex.get("National"), pokedexNumber.dexNumbers.get("National"), "National dex mismatch");
    assertEquals(expectedDex.get("Blueberry"), pokedexNumber.dexNumbers.get("Blueberry"), "Blueberry dex mismatch");
    assertEquals(expectedDex.get("Paldea"), pokedexNumber.dexNumbers.get("Paldea"), "Paldea dex mismatch");
    assertEquals(expectedDex.get("Kitakami"), pokedexNumber.dexNumbers.get("Kitakami"), "Kitakami dex mismatch");
  }

}
