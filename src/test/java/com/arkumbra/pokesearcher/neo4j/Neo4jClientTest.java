package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.controller.PokemonDetail;
import com.arkumbra.pokesearcher.neo4j.search.SearchByNameRequest;
import com.arkumbra.pokesearcher.neo4j.search.SearchByNameResponse;
import com.arkumbra.pokesearcher.rest.PokemonSummary;
import com.arkumbra.pokesearcher.scraper.*;
import com.arkumbra.pokesearcher.scraper.generator.MasterDataCatalogGenerator;
import com.arkumbra.pokesearcher.scraper.pokemon.PokemonCoJpScraper;
import com.arkumbra.pokesearcher.scraper.smogon.SmogonUsageParser;
import com.arkumbra.pokesearcher.scraper.smogon.pojo.UsagePokemon;
import com.arkumbra.pokesearcher.util.SmogonNameConverter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
  public void testScrapeAndStoreAllPokemonForChampions() {
    var generator = new MasterDataCatalogGenerator();
    for (Pokemon pokemon : generator.generateFor(Game.Champions).getAllPokemon()) {
      neo4jClient.storePokemon(pokemon);
    }
  }

  @Test
  public void testScrapeAndStoreAPokemon() throws IOException {

    PokedexScraper scraper = new PokedexScraper();

    neo4jClient.storePokemon(scraper.extractPokemonInfo("https://www.serebii.net/pokedex-sv/calyrex/"));
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
    for (String url : allPokemonDexUrls) {
//    for (int i = 0; i < 50; i++) {
//      String url = iterator.next();
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

  @Test
  public void scrapeAndStoreJapaneseNamesForSinglePokemon() {
    PokemonCoJpScraper pokemonCoJpScraper = new PokemonCoJpScraper();

    Map<String, String> engToJpn = new HashMap<>();

    neo4jClient.getAllPokemon()
        .stream()
        .filter(p -> p.getNationalDexNumber() == 898)
        .forEach(pokemonSummary -> {
          System.out.println(pokemonSummary.getName().getEnglish());
          String jpnName = pokemonCoJpScraper.getJapaneseNameForPokemon(pokemonSummary.getNationalDexNumber());
          engToJpn.put(pokemonSummary.getName().getEnglish(), jpnName);
          System.out.println(jpnName);
        });

    neo4jClient.writeJapaneseName(engToJpn);
  }

  @Test
  public void testFindAllWindMoves() {
    List<MoveSummary> movesSummary = neo4jClient.getMovesSummary(AttackMetaData.wind);
    movesSummary.forEach(s -> System.out.println(s.getIdName()));
    assertEquals(16, movesSummary.size());
  }

//  @Test
//  public void testCreateAndStoreRegulationRules() {
//    String regulationId = "testId";
//
//    RegulationRules testRegulation = new RegulationRules(
//        regulationId,
//        "Test Regulation",
//        new Schedule(
//            ZonedDateTime.of(LocalDateTime.of(2024, 1, 1, 1, 0), ZoneId.of("UTC")),
//            ZonedDateTime.of(LocalDateTime.of(2024, 2, 3, 3, 3), ZoneId.of("UTC"))
//        ),
//        new ValidPokemon()
//            .put(PokedexRegion.National, new IntRange(1, 149)),
//        new ValidPokemon()
//            .put(PokedexRegion.National, 898),
//        3,
//        49,
//        true,
//        true,
//        new BattleTimers(
//            30,
//           31,
//            32,
//            33
//        )
//    );
//
//    neo4jClient.storeRegulation(testRegulation);
//
//    RegulationDetail resultRegDetail = neo4jClient.getRegulationDetail(regulationId);
//    Set<String> restrictedPokemonIds = resultRegDetail.getRestrictedPokemon().stream()
//        .map(p -> p.getIdName())
//        .collect(Collectors.toSet());
//    assertEquals(1, restrictedPokemonIds.size());
//    assertEquals("calyrex", restrictedPokemonIds.iterator().next());
//  }

//  @Test
//  public void testCreateAndStoreRegulationGRules() {
//    String regulationId = "g";
//
//    List<PokemonDetail> restrictedPokemon = lookupPokemonByName(
//        "Mewtwo",
//        "Lugia",
//        "Ho-Oh",
//        "Kyogre",
//        "Groudon",
//        "Rayquaza",
//        "Dialga",
//        "Palkia",
//        "Giratina",
//        "Reshiram",
//        "Zekrom",
//        "Kyurem",
//        "Cosmog",
//        "Cosmoem",
//        "Solgaleo",
//        "Lunala",
//        "Necrozma",
//        "Zacian",
//        "Zamazenta",
//        "Eternatus",
//        "Calyrex",
//
//        "Koraidon",
//        "Miraidon",
//        "Terapagos"
//    );
//
//    List<IntRange> restrictedNatDexNumbers = pokemonToDexIntRanges(restrictedPokemon);
//    List<IntRange> restrictedPaldeaDexNumbers = pokemonToDexIntRanges(restrictedPokemon, (p) -> p.getPokedexNumber().get(PokedexRegion.Paldea));
//    List<IntRange> restrictedKitakamiDexNumbers = pokemonToDexIntRanges(restrictedPokemon, (p) -> p.getPokedexNumber().get(PokedexRegion.Kitakami));
//    List<IntRange> restrictedBlueberryDexNumbers = pokemonToDexIntRanges(restrictedPokemon, (p) -> p.getPokedexNumber().get(PokedexRegion.Blueberry));
//
//    ValidPokemon nonRestrictedPokemon = new ValidPokemon()
//        .put(PokedexRegion.Paldea, new IntRange(1, 400))
//        .put(PokedexRegion.Kitakami, new IntRange(1, 200))
//        .put(PokedexRegion.Blueberry, new IntRange(1, 242));
//
//    restrictedPaldeaDexNumbers.forEach(r -> nonRestrictedPokemon.removeRange(PokedexRegion.Paldea, r));
//    restrictedKitakamiDexNumbers.forEach(r -> nonRestrictedPokemon.removeRange(PokedexRegion.Kitakami, r));
//    restrictedBlueberryDexNumbers.forEach(r -> nonRestrictedPokemon.removeRange(PokedexRegion.Blueberry, r));
//
//    ValidPokemon validRestrictedPokemon = new ValidPokemon();
//    restrictedNatDexNumbers.forEach(range -> validRestrictedPokemon.put(PokedexRegion.National, range));
////        forEach(System.out::println);
//
//
//
//    RegulationRules testRegulation = new RegulationRules(
//        regulationId,
//        "Regulation G",
//        new Schedule(
//            ZonedDateTime.of(LocalDateTime.of(2024, 5, 1, 0, 0), ZoneId.of("UTC")),
//            ZonedDateTime.of(LocalDateTime.of(2024, 8, 31, 23, 59), ZoneId.of("UTC"))
//        ),
//        nonRestrictedPokemon,
//        validRestrictedPokemon,
//        1,
//        50,
//        true,
//        false,
//        new BattleTimers(
//            20 * 60,
//            7 * 60,
//            90,
//            45
//        )
//    );
//
//    neo4jClient.storeRegulation(testRegulation);
//  }

  @Test
  public void testDownloadUsageRatingsFromSmogonAndStore() throws IOException {
    List<UsagePokemon> pokemonUsageList = new SmogonUsageParser().parse(9, 2024, 4, "g");
    System.out.println("Smogon usage file returned list of " + pokemonUsageList.size() + " pokemon");
    int nonMatch = 0;
    for (UsagePokemon pokemonUsage : pokemonUsageList) {
      SmogonNameConverter converted = SmogonNameConverter.getBySmogonName(pokemonUsage.getName());
      if (converted != null) {
        PokemonDetail detail = neo4jClient.getPokemonDetail(converted.getId(), converted.getFormName());
        System.out.println("Using lookup, found detail for " + detail.getName().getEnglish() + ", " + detail.getFormName());
      } else {
        SearchByNameResponse searchByNameResponse = neo4jClient.searchAllElementsByName(new SearchByNameRequest(pokemonUsage.getName()));
        if (searchByNameResponse.records().size() != 1) {
          System.err.println("No matching Pokemon found for name " + pokemonUsage.getName() + ". Found " + searchByNameResponse.records().size());
          nonMatch++;
        }
      }


    }
    System.out.println("Unable to find " + nonMatch + " Pokemon by name");
  }

  private List<PokemonDetail> lookupPokemonByName(String... pokemonNames) {
    Set<String> names = new HashSet<>(Arrays.asList(pokemonNames));

    List<PokemonDetail> pokemonDetails =
        neo4jClient.getAllPokemon().stream()
        .filter(p -> names.contains(p.getName().getEnglish()))
        .map(p -> neo4jClient.getPokemonDetail(p.getIdName(), p.getFormName()))
        .toList();

    return pokemonDetails;
  }

  private List<IntRange> pokemonToDexIntRanges(List<PokemonDetail> pokemonDetails) {
    LinkedList<IntRange> ranges = new LinkedList<>();

    for (PokemonDetail pokemonDetail : pokemonDetails) {
      int num = pokemonDetail.getPokedexNumber().getNational();

      if (! ranges.isEmpty()) {
        if (ranges.getLast().getRightBoundInclusive() == num) {

        } else if (ranges.getLast().getRightBoundInclusive() == num - 1) {
          ranges.getLast().setRightBoundInclusive(num);
        } else {
          ranges.add(new IntRange(num, num));
        }

      } else {
        ranges.add(new IntRange(num, num));
      }
    }

    return ranges;
  }

  private List<IntRange> pokemonToDexIntRanges(List<PokemonDetail> pokemonDetails, DexNumSupplier dexNumSupplier) {
    LinkedList<IntRange> ranges = new LinkedList<>();

    for (PokemonDetail pokemonDetail : pokemonDetails) {
//      int num = pokemonDetail.getPokedexNumber().getNational();
      Integer num = dexNumSupplier.get(pokemonDetail);
      if (num == null) continue;

      if (! ranges.isEmpty()) {
        if (ranges.getLast().getRightBoundInclusive() == num) {

        } else if (ranges.getLast().getRightBoundInclusive() == num - 1) {
          ranges.getLast().setRightBoundInclusive(num);
        } else {
          ranges.add(new IntRange(num, num));
        }

      } else {
        ranges.add(new IntRange(num, num));
      }
    }

    return ranges;
  }

  private interface DexNumSupplier {
    Integer get(PokemonDetail pokemonDetail);
  }

}
