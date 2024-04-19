package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.neo4j.search.SearchByNameRequest;
import com.arkumbra.pokesearcher.neo4j.search.SearchByNameResponse;
import com.arkumbra.pokesearcher.scraper.*;
import com.arkumbra.pokesearcher.scraper.pokemon.PokemonCoJpScraper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
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
      System.out.println(ability.getName().getEnglish());
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
      System.out.println("  " + ability.getName().getEnglish());
      neo4jClient.storeAbility(ability);
    }

    int abilities = neo4jClient.getNumberOfAbilities();
    System.out.println("Scraped " + abilities + " abilities");

    testUpdateNamesForAbilitiesWithMissingJapaneseNames();
    System.out.println("Missing Japanese names have been added");
  }

  @Test
  public void testStoreAbilityWithMetaData() throws IOException, InterruptedException {
    AbilityDexScraper abilityDexScraper = new AbilityDexScraper();
    String abilityUrl = "https://www.serebii.net/abilitydex/quarkdrive.shtml";

    Ability ability = abilityDexScraper.extractAbilityInfo(abilityUrl);
    System.out.println(ability.getName().getEnglish());

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
    System.out.println(englishToJapaneseNames);
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

  @Test
  public void testScrapeAndStoreAPokemon() throws IOException {

    PokedexScraper scraper = new PokedexScraper();

    neo4jClient.storePokemon(scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/deoxys/"));
//    Pokemon pokemon = new PokedexScraper().extractPokemonInfo("https://www.serebii.net/pokedex-sv/rillaboom/");
//    Pokemon pokemon = new PokedexScraper().extractPokemonInfo("https://www.serebii.net/pokedex-sv/tinkaton/");

//    neo4jClient.storePokemon(scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/hawlucha/"));
//    neo4jClient.storePokemon(scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/sceptile/"));
//    neo4jClient.storePokemon(scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/hitmonlee/"));
//    neo4jClient.storePokemon(scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/landorus/"));
//    neo4jClient.storePokemon(scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/pikachu/"));
//    neo4jClient.storePokemon(scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/raichu/"));
//    neo4jClient.storePokemon(scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/palkia/"));
//    neo4jClient.storePokemon(scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/scatterbug/"));
//    neo4jClient.storePokemon(scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/spewpa/"));
//    neo4jClient.storePokemon(scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/vivillon/"));
  }

//  @Disabled
  @Test
  public void testScrapeAndStoreAllPokemon() throws IOException {

    PokedexScraper scraper = new PokedexScraper();

    Set<String> allPokemonDexUrls = scraper.getAllPokemonDexUrls();

//    for (int i = 0; i < allPokemonDexUrls.size(); i++) {

    Iterator<String> iterator = allPokemonDexUrls.iterator();
//    for (String url : allPokemonDexUrls) {
    for (int i = 0; i < 50; i++) {
      String url = iterator.next();
      System.out.println(url);
      try {
        Pokemon pokemon = scraper.extractPokemonInfo(url);
        neo4jClient.storePokemon(pokemon);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }


  @Test
  public void testGetAbilities() {
    neo4jClient.getAllAbilities();
  }

  @Test
  public void testSearchAllElementsByNameInEnglish() {
    SearchByNameResponse results = neo4jClient.searchAllElementsByName(new SearchByNameRequest("alk"));
    System.out.println(results);
  }

  @Test
  public void testSearchAllElementsByNameInJapanese() {
    SearchByNameResponse results = neo4jClient.searchAllElementsByName(new SearchByNameRequest("から"));
    System.out.println(results);
  }

  @Test
  public void scrapeAndStoreJapaneseNamesForAllPokemonInDb() {
    PokemonCoJpScraper pokemonCoJpScraper = new PokemonCoJpScraper();

    Map<String, String> engToJpn = new HashMap<>();

    neo4jClient.getAllPokemon()
        .stream()
        .forEach(pokemonSummary -> {
          System.out.println(pokemonSummary.getName().getEnglish());
          String jpnName = pokemonCoJpScraper.getJapaneseNameForPokemon(pokemonSummary.getNationalDexNumber());
          engToJpn.put(pokemonSummary.getName().getEnglish(), jpnName);
          System.out.println(jpnName);
        });

    neo4jClient.writeJapaneseName(engToJpn);
  }

}
