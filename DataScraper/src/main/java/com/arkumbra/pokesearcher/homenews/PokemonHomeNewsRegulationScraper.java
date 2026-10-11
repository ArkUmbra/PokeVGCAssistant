package com.arkumbra.pokesearcher.homenews;

import com.arkumbra.pokesearcher.scraper.BattleTimers;
import com.arkumbra.pokesearcher.scraper.RegulationRules;
import com.arkumbra.pokesearcher.scraper.Schedule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PokemonHomeNewsRegulationScraper {
  // Define the exact pattern matching the string.
  // Locale.ENGLISH ensures it correctly reads "Wednesday" and "September" globally.
  private static final DateTimeFormatter englishFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy, 'at' HH:mm z", Locale.ENGLISH);


  public RegulationRules scrape(String url) {

    try {
      Document doc = Jsoup.connect(url)
          .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
          .timeout(10000)
          .get();

      // Extract all text content from the body, stripping HTML tags
      String fullText = doc.body().text();

      // === Data Variables ===
      String regulationName = null;
      String scheduleStart = null;
      String scheduleEnd = null;
      boolean duplicateItemsAllowed = true; // Assume true unless stated otherwise
      String totalTime = null;
      String playerTime = null;
      String turnTime = null;
      String previewTime = null;
      List<String> newMegaEvolutions = new ArrayList<>();

      // 1. Extract Regulation Name (e.g., "Regulation Set M-C")
      // The title usually looks like "Regulation Set M-C (Updated on September 9)"
      String rawTitle = doc.title();
      Matcher titleMatcher = Pattern.compile("(Regulation Set [A-Z\\-]+)").matcher(rawTitle);
      if (titleMatcher.find()) {
        regulationName = titleMatcher.group(1);
      }

      // 2. Extract Duration/Schedule
      // Expected format: "Wednesday, September 9, 2026, at 02:00 UTC to Wednesday, December 2, 2026, at 01:59 UTC"
      Matcher durationMatcher = Pattern.compile("Duration (.*?) to (.*? UTC)").matcher(fullText);
      if (durationMatcher.find()) {
        scheduleStart = durationMatcher.group(1).trim();
        scheduleEnd = durationMatcher.group(2).trim();
      }

      // 3. Extract Duplicate Items Allowed
      if (fullText.contains("Duplicate held items are not allowed")) {
        duplicateItemsAllowed = false;
      }

      // 4. Extract Timers
      Matcher totalTimeMatcher = Pattern.compile("Total Time: (\\d+ minutes)").matcher(fullText);
      if (totalTimeMatcher.find()) totalTime = totalTimeMatcher.group(1);

      Matcher playerTimeMatcher = Pattern.compile("Player Time: (\\d+ minutes)").matcher(fullText);
      if (playerTimeMatcher.find()) playerTime = playerTimeMatcher.group(1);

      Matcher turnTimeMatcher = Pattern.compile("Turn Time: (\\d+ seconds)").matcher(fullText);
      if (turnTimeMatcher.find()) turnTime = turnTimeMatcher.group(1);

      Matcher previewTimeMatcher = Pattern.compile("Preview Time: (\\d+ seconds)").matcher(fullText);
      if (previewTimeMatcher.find()) previewTime = previewTimeMatcher.group(1);

      // 5. Extract "Big Picture" / New Mechanics (Mega Evolutions)
      // Looks for bullet points like "• Mega Absol Z"
      Matcher megaMatcher = Pattern.compile("• (Mega [A-Za-z ]+)").matcher(fullText);
      while (megaMatcher.find()) {
        newMegaEvolutions.add(megaMatcher.group(1).trim());
      }

      // 6. Extract the Eligible Pokémon Link
      String eligiblePokemonUrl = null;

      // Find the first <a> tag containing the word "Eligible"
      Element eligibleLink = doc.selectFirst("a:contains(Eligible)");

      if (eligibleLink != null) {
        // "abs:href" resolves the full URL based on the page's base URI
        eligiblePokemonUrl = eligibleLink.attr("abs:href");
      }

      System.out.println("Eligible Pokémon URL: " + eligiblePokemonUrl);

      // === Output Results ===
      System.out.println("=== Parsed Regulation Data ===");
      System.out.println("Regulation Name: " + regulationName);
      System.out.println("Schedule Start:  " + scheduleStart);
      System.out.println("Schedule End:    " + scheduleEnd);
      System.out.println("Duplicate Items allowed? " + duplicateItemsAllowed);
      System.out.println("\n--- Timers ---");
      System.out.println("Total Time:   " + totalTime);
      System.out.println("Player Time:  " + playerTime);
      System.out.println("Turn Time:    " + turnTime);
      System.out.println("Preview Time: " + previewTime);

      System.out.println("\n--- Newly Allowed Mega Evolutions ---");
      for (String mega : newMegaEvolutions) {
        System.out.println("- " + mega);
      }

      return new RegulationRules(
          regulationName + "-" + new Random().nextInt(99999),
          regulationName,
          eligiblePokemonUrl,
          new Schedule(
            parseDateTime(scheduleStart),
            parseDateTime(scheduleEnd)
          ),
          null, // valid pokemon
          null, // restricted pokemon
          0, // number of restricted
          50, // set level
          true, // items allowed
          duplicateItemsAllowed,
          new BattleTimers(
              parseTimer(totalTime),
              parseTimer(playerTime),
              parseTimer(previewTime),
              parseTimer(turnTime)
          )
      );

    } catch (IOException e) {
      System.err.println("Error fetching or parsing the page: " + e.getMessage());
      throw new RuntimeException("Failed to pull regulation docs", e);
    }
  }

  private ZonedDateTime parseDateTime(String datetime) {
    return ZonedDateTime.parse(datetime, englishFormatter);
  }

  private Duration parseTimer(String timerText) {
    if (timerText == null || timerText.trim().isEmpty()) {
      return Duration.ZERO;
    }

    // Split the string by spaces to separate the number from the unit
    String[] parts = timerText.trim().split("\\s+");
    long amount = Long.parseLong(parts[0]);
    String unit = parts[1].toLowerCase();

    if (unit.startsWith("minute")) {
      return Duration.ofMinutes(amount);
    } else if (unit.startsWith("second")) {
      return Duration.ofSeconds(amount);
    } else if (unit.startsWith("hour")) {
      return Duration.ofHours(amount);
    } else {
      throw new IllegalArgumentException("Unknown time unit: " + unit);
    }
  }
}
