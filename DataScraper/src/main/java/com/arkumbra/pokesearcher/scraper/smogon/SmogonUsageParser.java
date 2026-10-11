package com.arkumbra.pokesearcher.scraper.smogon;

import com.arkumbra.pokesearcher.scraper.smogon.pojo.UsagePokemon;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SmogonUsageParser {
  private static final Path downloadPath = Path.of("build/smogonUsage.json");

  public List<UsagePokemon> parse(int generation, int year, int month, String regulation) throws IOException {
    // TODO padding
    String url = String.format("https://www.smogon.com/stats/%s-0%s/chaos/gen%svgc2024reg%s-1760.json",
        year, month, generation, regulation);
    URLConnection urlConnection = new URL(url).openConnection();
//    urlConnection.connect();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
    String line = null;

    StringBuilder fileSb = new StringBuilder();
    while ((line = bufferedReader.readLine()) != null) {
//      Files.writeString(downloadPath, line);
      fileSb.append(line);
    }

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION, true);
    mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
//    String file = Files.readString(Path.of("/Users/luke/code/PokeSearcher/src/main/resources/gen9vgc2024regg-1760.json"));
//    String file = Files.readString(Path.of("/Users/luke/code/PokeSearcher/src/main/resources/gen9vgc2024regg-1760.json"));
    String file = fileSb.toString();
    JsonNode rootNode = mapper.readTree(file);

    JsonNode info = rootNode.get("info");
    JsonNode data = rootNode.get("data");

    Iterator<String> fieldNames = data.fieldNames();

    List<UsagePokemon> usagePokemonList = new ArrayList<>();

    while (fieldNames.hasNext()) {
      String pokemonName = fieldNames.next();
      JsonNode pokemon = data.get(pokemonName);
      if (pokemon == null) continue;

      UsagePokemon usagePokemon = mapper.readValue(pokemon.toString(), UsagePokemon.class);
      usagePokemon.setName(pokemonName);
      usagePokemonList.add(usagePokemon);
    }

    Collections.sort(usagePokemonList);
//    usagePokemonList.forEach(System.out::println);
    return usagePokemonList;
  }

}
