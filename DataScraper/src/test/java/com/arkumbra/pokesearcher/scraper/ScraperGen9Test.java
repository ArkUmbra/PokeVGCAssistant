package com.arkumbra.pokesearcher.scraper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScraperGen9Test {

  private Scraper scraper;

  @BeforeEach
  public void setUp() {
    this.scraper = new Scraper();
  }

  @Test
  public void testExecute() throws IOException {
    scraper.execute();
  }

  @Test
  public void testVDifferentlyFormattedPokemonPages() throws IOException {
    // New SV Poke
    Pokemon tinkaton = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/tinkaton/");
    System.out.println(tinkaton);

    assertNames(new Name("Tinkaton"), tinkaton.name);
    assertPokedex(
        mapOfNullable(
            "Paldea", 281,
            "National", 959,
            "Blueberry", null,
            "Kitakami", null
        ), tinkaton.pokedex);

    // Pokemon from previous generation that is available in SV
    Pokemon rillaboom = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/rillaboom/");
    System.out.println(rillaboom);
    assertNames(new Name("Rillaboom"), rillaboom.name);
    assertPokedex(
        mapOfNullable(
            "Paldea", null,
            "National", 812,
            "Blueberry", 229,
            "Kitakami", null
        ), rillaboom.pokedex);


    // No Egg moves
    Pokemon hooh = scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/ho-oh/");
    System.out.println(hooh);
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

//  private void assertEqualOrNull(Integer expected, Integer actual) {
//    if (expected == null) {
//      assertNull(Actual)
//    }
//  }
}
