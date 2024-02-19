package com.arkumbra.pokesearcher.scraper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PokedexScraperGen9Test {

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
    assertNames(new Name("Tinkaton"), tinkaton.getDetails().getName());
    assertPokedex(
        mapOfNullable(
            "Paldea", 281,
            "National", 959,
            "Blueberry", null,
            "Kitakami", null
        ), tinkaton.getDetails().getPokedex());

    // Pokemon from previous generation that is available in SV
    Pokemon rillaboom = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/rillaboom/");
    System.out.println(rillaboom);

    assertFalse(rillaboom.hasAlternateForms());
    assertNames(new Name("Rillaboom"), rillaboom.getDetails().getName());
    assertPokedex(
        mapOfNullable(
            "Paldea", null,
            "National", 812,
            "Blueberry", 229,
            "Kitakami", null
        ), rillaboom.getDetails().getPokedex());


    // No Egg moves
    Pokemon hooh = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/ho-oh/");
    System.out.println(hooh);
    assertNames(new Name("Ho-Oh"), hooh.getDetails().getName());
    assertPokedex(
        mapOfNullable(
            "Paldea", null,
            "National", 250,
            "Blueberry", null,
            "Kitakami", null
        ), hooh.getDetails().getPokedex());
    assertFalse(hooh.hasAlternateForms());
  }

  @Test
  public void testMultiFormPokemonScrapeCorrectly() throws IOException {
    Pokemon weezing = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/weezing/");
    System.out.println(weezing);

    assertTrue(weezing.hasAlternateForms());
    assertEquals(1, weezing.getAlternateForms().size());

    PokemonDetails kantoWeezing = weezing.getDetails();
    assertNames(new Name("Weezing"), kantoWeezing.getName());
    assertEquals("Kantonian", kantoWeezing.getFormRegion());
    assertEquals(Set.of(TypeValue.poison), kantoWeezing.getTypes());

    PokemonDetails galarWeezing = weezing.getAlternateForms().get(0);
    assertNames(new Name("Weezing"), galarWeezing.getName());
    assertEquals("Galarian", galarWeezing.getFormRegion());
    assertEquals(List.of(TypeValue.poison, TypeValue.fairy), galarWeezing.getTypes());
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

  private void assertPokedex(Map<String, Integer> expectedDex, Pokedex pokedex) {
    assertEquals(expectedDex.size(), pokedex.dexNumbers.size(),
        "Pokedex dex entries size did not match. " + expectedDex + ", " + pokedex);

    assertEquals(expectedDex.get("National"), pokedex.dexNumbers.get("National"), "National dex mismatch");
    assertEquals(expectedDex.get("Blueberry"), pokedex.dexNumbers.get("Blueberry"), "Blueberry dex mismatch");
    assertEquals(expectedDex.get("Paldea"), pokedex.dexNumbers.get("Paldea"), "Paldea dex mismatch");
    assertEquals(expectedDex.get("Kitakami"), pokedex.dexNumbers.get("Kitakami"), "Kitakami dex mismatch");
  }

}
