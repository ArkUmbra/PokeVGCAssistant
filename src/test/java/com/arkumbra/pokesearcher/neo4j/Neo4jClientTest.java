package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.scraper.Ability;
import com.arkumbra.pokesearcher.scraper.AbilityDexScraper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Iterator;

public class Neo4jClientTest {

  private static Neo4jClient neo4jClient;

  @BeforeAll
  public static void setUp() {
    neo4jClient = new Neo4jClient("bolt://localhost:7687", "neo4j", "password");
  }

  @AfterAll
  public static void tearDown() {
    neo4jClient.close();
  }

  @Test
  public void testNeo4j() {
    neo4jClient.printGreeting("hello test");
  }

  @Test
  public void testInitMetaData() {
    neo4jClient.initializeAbilityMetadata();
  }

  @Test
  public void testStoreAbility() throws IOException, InterruptedException {
    AbilityDexScraper abilityDexScraper = new AbilityDexScraper();

    Iterator<String> iterator = abilityDexScraper.getAllAbilityDexUrls().iterator();

    for (int i = 0; i < 20; i++) {
      String abilityUrl = iterator.next();
      Ability ability = abilityDexScraper.extractAbilityInfo(abilityUrl);
      System.out.println(ability.getName().getEnglishName());
      neo4jClient.storeAbility(ability);
    }

    Thread.sleep(2000);

    int abilities = neo4jClient.getNumberOfAbilities();
    System.out.println(abilities);
  }

  @Test
  public void testStoreAbilityWithMetaData() throws IOException, InterruptedException {
    AbilityDexScraper abilityDexScraper = new AbilityDexScraper();
    String abilityUrl = "https://www.serebii.net/abilitydex/quarkdrive.shtml";

    Ability ability = abilityDexScraper.extractAbilityInfo(abilityUrl);
    System.out.println(ability.getName().getEnglishName());

    neo4jClient.storeAbility(ability);
  }

  @Test
  public void testGetNumberOfStoredAbilities() {
    int abilities = neo4jClient.getNumberOfAbilities();
    System.out.println(abilities);
  }
}
