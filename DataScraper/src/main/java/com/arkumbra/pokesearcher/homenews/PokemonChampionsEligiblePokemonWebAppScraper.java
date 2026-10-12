package com.arkumbra.pokesearcher.homenews;


import com.arkumbra.pokesearcher.model.PokemonWithSpriteRef;
import com.arkumbra.pokesearcher.model.RegulationEligiblePokemon;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PokemonChampionsEligiblePokemonWebAppScraper {

  public RegulationEligiblePokemon scrapeMonsAndSpriteWithJsoup(String htmlUrl) {
    try {
      // 1. Fetch the HTML
      Document doc = Jsoup.connect(htmlUrl).get();
      String html = doc.html();

      // 2. Automatically locate the CSS and PNG URLs
      String cssUrl = null;
      String imageUrl = null;

      // Search for stylesheet links in the HTML
      Elements stylesheets = doc.select("link[rel=stylesheet]");
      for (Element link : stylesheets) {
        String href = link.attr("abs:href"); // abs:href ensures we get the absolute URL
        if (href.contains("sprite_poke_")) {
          cssUrl = href;
          imageUrl = href.replace(".css", ".png");
          break; // Stop searching once we find the relevant sprite sheet
        }
      }

      if (cssUrl == null) {
        throw new RuntimeException("Could not find a sprite_poke stylesheet in the HTML.");
      }

      System.out.println("Discovered CSS: " + cssUrl);
      System.out.println("Discovered PNG: " + imageUrl);

      // 3. Fetch the discovered CSS file
      Document cssDoc = Jsoup.connect(cssUrl).ignoreContentType(true).get();
      String css = cssDoc.text();

      List<PokemonWithSpriteRef> eligiblePokemon = new ArrayList<>();
      Matcher arrayMatcher = Pattern.compile("const pokemons = (\\[.*?\\]);").matcher(html);

      if (arrayMatcher.find()) {
        String rawArray = arrayMatcher.group(1);
        Matcher matcher = Pattern.compile("\\[\"((\\d+)-(\\d+))\",\\d+,\"(.*?)\"\\]").matcher(rawArray);

        while (matcher.find()) {
          String dexStr = matcher.group(2);
          String variantStr = matcher.group(3).substring(1); // matcher seems to get 3 digits, but we need 2
          String nameRaw = matcher.group(4);

          String nameOnly = nameRaw.contains("(") ? nameRaw.substring(0, nameRaw.indexOf("(")) : nameRaw;
          String variant = nameRaw.contains("(") ? nameRaw.substring(nameOnly.length()).replace("(", "").replace(")", "") : "";

          // Updated Regex: Looks inside the class block [^}]*? for size and position
          String cssRegex = "\\.sprite-poke-ui_PokeIcon_\\d+_" + dexStr + "_" + variantStr + "_\\d+[^}]*?background-size:\\s*([^;]+);[^}]*?background-position:\\s*([^;]+);";
          Matcher cssMatcher = Pattern.compile(cssRegex).matcher(css);

          String spriteBgSize = "";
          String spritePosition = "";

          if (cssMatcher.find()) {
            spriteBgSize = cssMatcher.group(1).trim();     // e.g. "1623.438% 1521.875%"
            spritePosition = cssMatcher.group(2).trim(); // e.g. "0.000% 0.000%"
          } else {
            System.out.println("Could not find CSS percentages for: " + dexStr + "-" + variantStr);
          }

          eligiblePokemon.add(new PokemonWithSpriteRef(
              Integer.parseInt(dexStr),
              nameOnly,
              variant,
              spriteBgSize,
              spritePosition,
              imageUrl
          ));
        }

        for (var mon : eligiblePokemon) {
          System.out.println("- " + mon);
        }
      }
      return new RegulationEligiblePokemon(eligiblePokemon, imageUrl);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
