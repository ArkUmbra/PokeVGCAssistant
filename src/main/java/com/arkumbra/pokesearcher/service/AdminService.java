package com.arkumbra.pokesearcher.service;

import com.arkumbra.pokesearcher.neo4j.Neo4jClient;
import com.arkumbra.pokesearcher.scraper.Ability;
import com.arkumbra.pokesearcher.scraper.AbilityDexScraper;
import com.arkumbra.pokesearcher.scraper.Game;
import com.arkumbra.pokesearcher.scraper.generator.MasterDataCatalogGenerator;
import com.arkumbra.pokesearcher.scraper.model.Pokedex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class AdminService {

  private static final Logger log = LoggerFactory.getLogger(AdminService.class);

  private final Neo4jClient neo4jClient;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();



  private final AbilityDexScraper abilityDexScraper = new AbilityDexScraper();
  private final MasterDataCatalogGenerator generator = new MasterDataCatalogGenerator();

  private final AtomicBoolean running = new AtomicBoolean(false);
  private volatile String currentTask;
  private volatile String statusMessage;
  private volatile String lastCompletedMessage;
  private volatile String lastErrorMessage;

  @Autowired
  public AdminService(Neo4jClient neo4jClient) {
    this.neo4jClient = neo4jClient;
  }

  public boolean isRunning() {
    return running.get();
  }

  public String getCurrentTask() {
    return currentTask;
  }

  public String getStatusMessage() {
    return statusMessage;
  }

  public String getLastCompletedMessage() {
    return lastCompletedMessage;
  }

  public String getLastErrorMessage() {
    return lastErrorMessage;
  }

  public int getPokemonCount() {
    try {
      return neo4jClient.getNumberOfPokemon();
    } catch (Exception e) {
      log.warn("Could not retrieve Pokemon count", e);
      return 0;
    }
  }

  public int getAbilityCount() {
    try {
      return neo4jClient.getNumberOfAbilities();
    } catch (Exception e) {
      log.warn("Could not retrieve Ability count", e);
      return 0;
    }
  }

  public boolean triggerRefreshWholeDataset() {
    if (!running.compareAndSet(false, true)) {
      return false;
    }

    currentTask = "Refresh Whole Dataset";
    statusMessage = "Starting scrape of Champions Pokédex from Serebii...";
    lastErrorMessage = null;

    executor.submit(() -> {
      try {
        statusMessage = "Initializing constant enum data in Neo4j...";
        neo4jClient.initializeConstantEnumDataToDatabase();

        statusMessage = "Scraping Champions Pokédex from Serebii...";
        Pokedex pokedex = generator.generateFor(Game.Champions);

        int count = pokedex.getAllPokemon() != null ? pokedex.getAllPokemon().size() : 0;
        statusMessage = "Storing " + count + " Pokémon into Neo4j database...";
        neo4jClient.storePokedex(pokedex);

        lastCompletedMessage = "Successfully refreshed whole dataset with " + count + " Pokémon!";
        lastErrorMessage = null;
      } catch (Exception e) {
        log.error("Failed to refresh whole dataset", e);
        lastErrorMessage = "Error refreshing whole dataset: " + e.getMessage();
      } finally {
        running.set(false);
        currentTask = null;
        statusMessage = null;
      }
    });

    return true;
  }

  public boolean triggerScrapeAllAbilities() {
    if (!running.compareAndSet(false, true)) {
      return false;
    }

    currentTask = "Scrape All Abilities";
    statusMessage = "Starting ability scraping from Serebii...";
    lastErrorMessage = null;

    executor.submit(() -> {
      try {
        statusMessage = "Initializing constant enum data in Neo4j...";
        neo4jClient.initializeConstantEnumDataToDatabase();

        statusMessage = "Fetching ability URLs from Serebii AbilityDex...";
        Set<String> abilityUrls = abilityDexScraper.getAllAbilityDexUrls();
        int total = abilityUrls.size();
        int count = 0;

        for (String abilityUrl : abilityUrls) {
          count++;
          statusMessage = String.format("Scraping ability %d of %d (%s)...", count, total, abilityUrl);
          try {
            Ability ability = abilityDexScraper.extractAbilityInfo(abilityUrl);
            neo4jClient.storeAbility(ability);
          } catch (Exception e) {
            log.warn("Failed to scrape ability at {}", abilityUrl, e);
          }
        }

        statusMessage = "Applying missing Japanese names for abilities...";
        Map<String, String> englishToJapaneseNames = Map.of(
            "Delta Stream", "ルタストリーム",
            "Teraform Zero", "ゼロフォーミング",
            "Poison Puppeteer", "どくくぐつ",
            "Tera Shift", "テラスチェンジ",
            "Tera Shell", "テラスシェル"
        );
        neo4jClient.writeJapaneseName(englishToJapaneseNames);

        int finalCount = neo4jClient.getNumberOfAbilities();
        lastCompletedMessage = "Successfully scraped and stored " + finalCount + " abilities!";
        lastErrorMessage = null;
      } catch (Exception e) {
        log.error("Failed to scrape abilities", e);
        lastErrorMessage = "Error scraping abilities: " + e.getMessage();
      } finally {
        running.set(false);
        currentTask = null;
        statusMessage = null;
      }
    });

    return true;
  }
}
