package com.arkumbra.pokesearcher.scraper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AbilityDexScraperGen9Test {

  private AbilityDexScraper scraper;

  @BeforeEach
  public void setUp() {
    this.scraper = new AbilityDexScraper();
  }


  @Test
  public void testGetAllAbilityDexLinks() throws IOException {
    Set<String> urls = scraper.getAllAbilityDexUrls();
    System.out.println(urls);

    assertTrue(urls.contains("https://www.serebii.net/abilitydex/adaptability.shtml"));
    assertTrue(urls.contains("https://www.serebii.net/abilitydex/longreach.shtml"));
    assertTrue(urls.contains("https://www.serebii.net/abilitydex/magicbounce.shtml"));
    assertTrue(urls.contains("https://www.serebii.net/abilitydex/zerotohero.shtml"));
  }

  @Test
  public void testExtractVariousMoves() throws IOException {
    Ability zeroToHero = scraper.extractAbilityInfo("https://www.serebii.net/abilitydex/zerotohero.shtml");
    System.out.println(zeroToHero.getName());

    assertEquals("Zero to Hero", zeroToHero.getName().englishName);
    assertEquals("マイティチェンジ", zeroToHero.getName().japanese);
    assertEquals("The Pokémon transforms into its Hero Form when it switches out.", zeroToHero.getIngameDescription());

    assertTrue(zeroToHero.getMetaData().contains(AbilityMetaData.blocksRolePlayAndDoodle));
    assertTrue(zeroToHero.getMetaData().contains(AbilityMetaData.blocksReceiveAndPowerOfAlchemy));
    assertTrue(zeroToHero.getMetaData().contains(AbilityMetaData.blocksEntrainment));
    assertTrue(zeroToHero.getMetaData().contains(AbilityMetaData.blocksTrace));

    assertTrue(zeroToHero.getMetaData().contains(AbilityMetaData.blocksRolePlayAndDoodle));
    assertTrue(zeroToHero.getMetaData().contains(AbilityMetaData.blocksReceiveAndPowerOfAlchemy));
    assertTrue(zeroToHero.getMetaData().contains(AbilityMetaData.blocksEntrainment));
    assertTrue(zeroToHero.getMetaData().contains(AbilityMetaData.blocksTrace));


    Ability anticipation = scraper.extractAbilityInfo("https://www.serebii.net/abilitydex/anticipation.shtml");
    System.out.println(anticipation.getName());

    assertEquals("Anticipation", anticipation.getName().englishName);
    assertEquals("きけんよち", anticipation.getName().japanese);
    assertEquals("The Pokémon can sense an opposing Pokémon's dangerous moves.", anticipation.getIngameDescription());

    assertFalse(anticipation.getMetaData().contains(AbilityMetaData.blocksRolePlayAndDoodle));
    assertFalse(anticipation.getMetaData().contains(AbilityMetaData.blocksReceiveAndPowerOfAlchemy));
    assertFalse(anticipation.getMetaData().contains(AbilityMetaData.blocksEntrainment));
    assertFalse(anticipation.getMetaData().contains(AbilityMetaData.blocksTrace));

    assertFalse(anticipation.getMetaData().contains(AbilityMetaData.blocksRolePlayAndDoodle));
    assertFalse(anticipation.getMetaData().contains(AbilityMetaData.blocksReceiveAndPowerOfAlchemy));
    assertFalse(anticipation.getMetaData().contains(AbilityMetaData.blocksEntrainment));
    assertFalse(anticipation.getMetaData().contains(AbilityMetaData.blocksTrace));
  }

}
