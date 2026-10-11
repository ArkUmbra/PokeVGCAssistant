package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.controller.PokemonDetail;
import com.arkumbra.pokesearcher.neo4j.search.SearchByNameRequest;
import com.arkumbra.pokesearcher.neo4j.search.SearchByNameResponse;
import com.arkumbra.pokesearcher.regulation.RegulationParser;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PokemonChampionsNeo4jClientTest {

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
  public void loadAndStoreRegulationMA() {
    // https://news.pokemon-home.com/en/page/751.html

    String regulationId = "ma";
    LocalDate start = LocalDate.of(2026, 4, 8);
    LocalDate end = LocalDate.of(2026, 6, 17);

    boolean megaEvolutionPermitted = true;
    int numberOfMegaEvolutionsPermitted = 1;

    var validPokemon = new RegulationParser().parseValidPokemon("https://web-view.app.pokemonchampions.jp/battle/pages/events/rs177501629259kmzbny/ja/pokemon.html");
  }

}
