package com.arkumbra.pokesearcher.scraper.pokemon;

import com.arkumbra.pokesearcher.scraper.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static com.arkumbra.pokesearcher.scraper.Util.getPageWithDelay;

public class PokemonCoJpScraper {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final String pokemonPageBase = "https://zukan.pokemon.co.jp/detail/%s";

  public String getJapaneseNameForPokemon(int nationalDexNumber) {
    Document doc = getPageWithDelay(String.format(pokemonPageBase, StringUtils.padLeftToFourDigits(nationalDexNumber)));

    Elements scriptBlock = doc.getElementsByTag("script");

    for (Element element : scriptBlock) {
      if (element.id().equals("json-data")) {
        try {
          String json = element.data();
          JsonNode jsonNode = objectMapper.readTree(json);
          return jsonNode.get("pokemon").get("name").textValue();
        } catch (JsonProcessingException e) {
          System.err.println("Exception processing json-data node");
          throw new RuntimeException(e);
        }
      }
    }

    throw new RuntimeException("Could not find name!!");
  }


}
