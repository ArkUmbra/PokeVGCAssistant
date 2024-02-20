package com.arkumbra.pokesearcher.scraper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static com.arkumbra.pokesearcher.scraper.TestUtils.assertListContents;
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
  }

  @Test
  public void testMultiFormPokemonScrapeCorrectly() throws IOException {

    // ------
    // Different types across forms
    Pokemon weezing = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/weezing/");
    System.out.println(weezing);

    assertNames(new Name("Weezing"), weezing.getName());
    assertTrue(weezing.hasAlternateForms());
    assertEquals(1, weezing.getAlternateForms().size());

    PokemonDetails kantoWeezing = weezing.getDetails();
    assertEquals("Kantonian Form", kantoWeezing.getFormName());
    assertEquals(linkedHashSet(TypeValue.poison), kantoWeezing.getTypes());

    PokemonDetails galarWeezing = weezing.getAlternateForms().get(0);
    assertEquals("Galarian Form", galarWeezing.getFormName());
    assertEquals(linkedHashSet(TypeValue.poison, TypeValue.fairy), galarWeezing.getTypes());

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

    PokemonDetails alolaSandshrew = sandshrew.getAlternateForms().get(0);
    assertEquals("Alola Form", alolaSandshrew.getFormName());
    assertEquals(linkedHashSet(TypeValue.ice, TypeValue.steel), alolaSandshrew.getTypes());

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

    PokemonDetails therianLandorus = landorus.getAlternateForms().get(0);
    assertEquals("Therian Forme", therianLandorus.getFormName());
    assertEquals(linkedHashSet(TypeValue.ground, TypeValue.flying), therianLandorus.getTypes());

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

    PokemonDetails attackDeoxys = deoxys.getAlternateForms().get(0);
    assertEquals("Attack Forme", attackDeoxys.getFormName());
    assertEquals(linkedHashSet(TypeValue.psychic), attackDeoxys.getTypes());

    PokemonDetails defenseDeoxys = deoxys.getAlternateForms().get(1);
    assertEquals("Defense Forme", defenseDeoxys.getFormName());
    assertEquals(linkedHashSet(TypeValue.psychic), defenseDeoxys.getTypes());

    PokemonDetails speedDeoxys = deoxys.getAlternateForms().get(2);
    assertEquals("Speed Forme", speedDeoxys.getFormName());
    assertEquals(linkedHashSet(TypeValue.psychic), speedDeoxys.getTypes());
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
