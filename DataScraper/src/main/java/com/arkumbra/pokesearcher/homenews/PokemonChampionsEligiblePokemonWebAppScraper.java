package com.arkumbra.pokesearcher.homenews;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
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

//  public List<String> scrape(String targetUrl) {
//    // Initialize Playwright context
//    try (Playwright playwright = Playwright.create()) {
//      Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
//      Page page = browser.newPage();
//;
//      page.navigate(targetUrl);
//
//      // Wait until network traffic settles, ensuring JS-rendered elements are present
//      page.waitForLoadState(LoadState.NETWORKIDLE);
//
//      // Extract elements.
//      // NOTE: You will need to inspect the page via browser DevTools to find the exact CSS selector
//      // replacing '.pokemon-item' (e.g., '.pokemon-card span', 'ul.list li', etc.)
//      List<String> eligiblePokemon = (List<String>) page.evalOnSelectorAll(
//          ".pokemon-item",
//          "elements => elements.map(el => el.innerText.trim())"
//      );
//
//      // Print results
//      System.out.println("Total eligible Pokémon found: " + eligiblePokemon.size());
//      for (String pokemon : eligiblePokemon) {
//        System.out.println(pokemon);
//      }
//
//      browser.close();
//
//      return eligiblePokemon;
//    } catch (Exception e) {
//      throw new RuntimeException(e);
//    }
//  }

  public EligiblePokemonResponse scrapeMonsAndSpriteWithJsoup(String htmlUrl) {
//    public List<Pokemon> scrapeWithJsoup(String htmlUrl) {
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

      List<PokemonWithSprite> eligiblePokemon = new ArrayList<>();
      Matcher arrayMatcher = Pattern.compile("const pokemons = (\\[.*?\\]);").matcher(html);

      if (arrayMatcher.find()) {
        String rawArray = arrayMatcher.group(1);
        Matcher matcher = Pattern.compile("\\[\"((\\d+)-(\\d+))\",\\d+,\"(.*?)\"\\]").matcher(rawArray);

        while (matcher.find()) {
          String dexStr = matcher.group(2);
//          String variantStr = "00";//matcher.group(3);
          String variantStr = matcher.group(3).substring(1); // matcher seems to get 3 digits, but we need 2
          String nameRaw = matcher.group(4);

          String nameOnly = nameRaw.contains("(") ? nameRaw.substring(0, nameRaw.indexOf("(")) : nameRaw;
          String variant = nameRaw.contains("(") ? nameRaw.substring(nameOnly.length()).replace("(", "").replace(")", "") : "";

          // Updated Regex: Looks inside the class block [^}]*? for size and position
          String cssRegex = "\\.sprite-poke-ui_PokeIcon_\\d+_" + dexStr + "_" + variantStr + "_\\d+[^}]*?background-size:\\s*([^;]+);[^}]*?background-position:\\s*([^;]+);";
          Matcher cssMatcher = Pattern.compile(cssRegex).matcher(css);

          // TODO - Variants (e.g. Tauros) end up with the same image currently

          String spriteBgSize = "";
          String spritePosition = "";

          if (cssMatcher.find()) {
            spriteBgSize = cssMatcher.group(1).trim();     // e.g. "1623.438% 1521.875%"
            spritePosition = cssMatcher.group(2).trim(); // e.g. "0.000% 0.000%"
          } else {
            System.out.println("Could not find CSS percentages for: " + dexStr + "-" + variantStr);
          }

          eligiblePokemon.add(new PokemonWithSprite(
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
      return new EligiblePokemonResponse(eligiblePokemon, imageUrl);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  record EligiblePokemonResponse (
      List<PokemonWithSprite> mons,
      String spriteSheetUrl
  ) { }

// Updated Record includes the dynamically discovered image URL
  record PokemonWithSprite(
      int dexNumber,
      String name,
      String variant,
      String spriteBgSize,
      String spritePosition,
      String imageUrl
  ) { }

//  public List<PokemonWithSprite> scrapeMonsAndSpriteWithJsoup(String url) {
//    try {
//      Document doc = Jsoup.connect(url).get();
//      String html = doc.html();
//      List<PokemonWithSprite> eligiblePokemon = new ArrayList<>();
//
//      Matcher arrayMatcher = Pattern.compile("const pokemons = (\\[.*?\\]);").matcher(html);
//
//      if (arrayMatcher.find()) {
//        String rawArray = arrayMatcher.group(1);
//
//        // UPDATED REGEX: Added a capture group for the middle integer (\d+)
//        // Group 1: Dex Number (e.g., "0003")
//        // Group 2: Sprite Index (e.g., "0")
//        // Group 3: Raw Name (e.g., "Venusaur(Mega)")
//        Matcher matcher = Pattern.compile("\\[\"(\\d+)-\\d+\",(\\d+),\"(.*?)\"\\]").matcher(rawArray);
//
//        // Sprite sheet properties (You will need to adjust these based on the actual image)
//        int spriteWidth = 64;
//        int spriteHeight = 64;
//        int columnsInSheet = 10;
//
//        while (matcher.find()) {
//          String dexNumber = matcher.group(1);
//          int spriteIndex = Integer.parseInt(matcher.group(2));
//          String nameRaw = matcher.group(3);
//
//          String nameOnly = nameRaw.contains("(") ? nameRaw.substring(0, nameRaw.indexOf("(")) : nameRaw;
//          String variant = nameRaw.contains("(") ? nameRaw.substring(nameOnly.length()).replace("(", "").replace(")", "") : "";
//
//          // Calculate the X and Y coordinates based on the index
//          int spriteX = (spriteIndex % columnsInSheet) * spriteWidth;
//          int spriteY = (spriteIndex / columnsInSheet) * spriteHeight;
//
//          eligiblePokemon.add(new PokemonWithSprite(
//              Integer.parseInt(dexNumber),
//              nameOnly,
//              variant,
//              spriteX,
//              spriteY
//          ));
//        }
//
//        for (var mon : eligiblePokemon) {
//          System.out.println("- " + mon);
//        }
//      }
//      return eligiblePokemon;
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }

  // Updated Record
//  record PokemonWithSprite(
//      int dexNumber,
//      String name,
//      String variant,
//      int spriteX,
//      int spriteY
//  ) { }

  public List<Pokemon> scrapeWithJsoup(String url) {

      try {
        // Fetch the raw HTML using Jsoup
        Document doc = Jsoup.connect(url).get();
        String html = doc.html();

        List<Pokemon> eligiblePokemon = new ArrayList<>();

        // 1. Locate the JavaScript array: const pokemons = [...]
        // We use a regex to capture everything between the brackets
        Matcher arrayMatcher = Pattern.compile("const pokemons = (\\[.*?\\]);").matcher(html);

        if (arrayMatcher.find()) {
          String rawArray = arrayMatcher.group(1);

          // Updated Regex:
          // (\\d+) captures the digits before the hyphen (e.g., "0003")
          // (.*?) captures the name string
          Matcher matcher = Pattern.compile("\\[\"(\\d+)-\\d+\",\\d+,\"(.*?)\"\\]").matcher(rawArray);

          while (matcher.find()) {
            String dexNumber = matcher.group(1);
            String nameRaw = matcher.group(2);

            String nameOnly = nameRaw.contains("(") ? nameRaw.substring(0, nameRaw.indexOf("(")) : nameRaw;
            String variant =  nameRaw.substring(nameOnly.length()).replace("(", "").replace(")", "");

            eligiblePokemon.add(new Pokemon(Integer.parseInt(dexNumber), nameOnly, variant));
          }
        }

        System.out.println("Total eligible Pokémon found: " + eligiblePokemon.size());
        for (var mon : eligiblePokemon) {
          System.out.println("- " + mon);
        }

        return eligiblePokemon;

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
  }

  record Pokemon(
    int dexNumber,
    String name,
    String variant
  ) { }
}
