package com.arkumbra.pokesearcher.scraper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PokemonDbSpriteScraperTest {

  private final PokemonDbSpriteScraper scraper = new PokemonDbSpriteScraper();

  @Test
  public void testGetSpriteUrls() {
    List<Pair<String, String>> allSpriteUrls = scraper.getAllSpritePageUrls();
    System.out.println(allSpriteUrls);
  }

  @Test
  public void testGetDownloadUrlForBulbasaur() {
    String expectedUrl = "https://img.pokemondb.net/sprites/scarlet-violet/normal/bulbasaur.png";

    String result = scraper.getSpriteDownloadUrl("https://pokemondb.net/sprites/bulbasaur");

    assertEquals(expectedUrl, result);
  }

  @Test
  public void testDownloadSprites() throws IOException {
    Properties properties = System.getProperties();
//    System.out.println(properties);
//
//    Map<String, String> getenv = System.getenv();
//    System.out.println(getenv);
    String outputDir = properties.get("user.dir") + "/build/output/sprites";
    Files.createDirectories(Path.of(outputDir));

    scraper.findAllSpritesAndDownload(properties.get("user.dir") + "/build/output/sprites");

  }
}
