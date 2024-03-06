package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.scraper.Ability;
import com.arkumbra.pokesearcher.scraper.AbilityDexScraper;
import com.arkumbra.pokesearcher.scraper.AttackDexScraper;
import com.arkumbra.pokesearcher.scraper.Move;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
  public void testInitConstantData() {
    neo4jClient.initializeConstantEnumDataToDatabase();
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

  @Disabled
  @Test
  public void testScrapeAndStoreAllAbility() throws IOException {
    AbilityDexScraper abilityDexScraper = new AbilityDexScraper();

    Set<String> abilityUrls = abilityDexScraper.getAllAbilityDexUrls();

    for (String abilityUrl : abilityUrls) {
      System.out.println(abilityUrl);
      Ability ability = abilityDexScraper.extractAbilityInfo(abilityUrl);
      System.out.println("  " + ability.getName().getEnglishName());
      neo4jClient.storeAbility(ability);
    }

    int abilities = neo4jClient.getNumberOfAbilities();
    System.out.println("Scraped " + abilities + " abilities");
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

  @Test
  public void testUpdateNamesForAbilitiesWithMissingJapaneseNames() {
    // These Japanese ability names are missing from the Serebii ability dex pages, so this test will correct the data
    // assuming it has already been inserted into the DB

    // https://yakkun.com/sv/tokusei_en.htm
    Map<String, String> englishToJapaneseNames = Map.of(
        "Delta Stream", "ルタストリーム",
        "Teraform Zero", "ゼロフォーミング",
        "Poison Puppeteer", "どくくぐつ",
        "Tera Shift", "テラスチェンジ",
        "Tera Shell", "テラスシェル"
    );

    neo4jClient.writeJapaneseName(englishToJapaneseNames);
  }

  @Test
  public void testScrapeAndStoreSingleMove() throws IOException {
    AttackDexScraper attackDexScraper = new AttackDexScraper();
    Move heatCrash = attackDexScraper.extractMoveInfo("https://www.serebii.net/attackdex-sv/heatcrash.shtml");

    neo4jClient.storeMove(heatCrash);
  }

  @Disabled
  @Test
  public void testScrapeAndStoreAllMoves() throws IOException {
    AttackDexScraper attackDexScraper = new AttackDexScraper();
    Set<String> attackDexUrls = attackDexScraper.getAllAttackDexUrls();

    for (String dexUrl : attackDexUrls) {
      try {
        Move move = attackDexScraper.extractMoveInfo(dexUrl);
        neo4jClient.storeMove(move);
      } catch (Exception e) {
        System.out.println(dexUrl);
        e.printStackTrace();
      }
    }
  }

}
