package com.arkumbra.pokesearcher.scraper;


import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;

// TODO Refactor this to handle dex by generation
public class SingleFormPokedexScraper {
  private static final String NOT_PRESENT_IN_DEX = "-";
  protected final ScraperUtil scraperUtil;

  public SingleFormPokedexScraper() {
    scraperUtil = new ScraperUtil();
  }

  public SingleFormPokedexScraper(ScraperUtil scraperUtil) {
    this.scraperUtil = scraperUtil;
  }

  protected Pokemon extractPokemonInfo(Document doc, Pokemon pokemon) {
    // TDs
    // 0 - Name
    // 1 - Other Names
    // 2 - No.
    // 3 - Gender Ratio
    // 4 - Type
    Elements dexTableNameNumberDataTds = Util.selectDataRowTdsByPartOfTrHeading(doc, "Other names");

    PokemonDetails details = new PokemonDetails();

    // Name
    String englishName = dexTableNameNumberDataTds.get(0).text();
    Name name = new Name();
    name.english = englishName;
    pokemon.setName(name);

    // Pokedex number
    Map<PokedexRegion, Integer> dexToNumber = extractDexInfo(
        dexTableNameNumberDataTds.get(2)
            .select("table > tbody > tr > td")
    );

    PokedexNumber pokedexNumber = new PokedexNumber();
    pokedexNumber.dexNumbers = dexToNumber;
    pokemon.setPokedexNumber(pokedexNumber);

    // Types
    Elements typeElements = dexTableNameNumberDataTds.get(4).select(" > a");
    details.setTypes(extractTypes(typeElements));

    // Abilities
    Element abilityHeaderBlockTd = doc.select("tr:contains(Abilities:)").first(); // get header td
    details.setAbilities(parseAbilitiesFromTextString(abilityHeaderBlockTd.text()));

    // Moveset
    details.setMoveset(extractMoveset(doc));

    // Base Stats
    Element statsTable = doc.selectFirst("table.dextable:contains(Base Stats - Total)");
    details.setBaseStats(parseBaseStats(statsTable));

    pokemon.setDetails(details);

    extractExtraPokemonInfo(pokemon, doc);
    return pokemon;
  }

  protected void extractExtraPokemonInfo(Pokemon pokemon, Document doc) {
    // no op - for override
  }

  protected String parseBracketedWord(String word, int indexOfOpenBracket) {
    for (int i = indexOfOpenBracket; i < word.length(); i++) {
      if (word.charAt(i) == ')') {
        return word.substring(indexOfOpenBracket, i + 1);
      }
    }

    throw new RuntimeException("Couldn't find closing bracket of bracketed word");
  }

  protected Abilities parseAbilitiesFromTextString(String abilityHeaderBlockText) {
    // For multi-form Pokemon the page uses 'Hidden' instead of 'Hidden Ability' which is kind of annoying, but that's
    // why have the partial text comparison here
    boolean hasHiddenAbility = abilityHeaderBlockText.contains("(Hidden");

    // Example text of abilityHeaderBlockTd.text() before processing
    // Abilities: Overgrow - Grassy Surge (Hidden Ability)
    List<String> abilityNameIds = Arrays.stream(
        abilityHeaderBlockText
        .replace("(Hidden Ability)", "")
        .replace("(Hidden)", "")
        .replace("Abilities:", "")
        // NOTE!! Avoid abilities with hyphen in the name, but also split correctly on the hyphen because abilities. Sometimes they are separated by " - ", and sometimes it is "- "...
        .trim().split("- ")
    ).map(humanReadableAbility -> {
      return humanReadableAbility
          .trim()
          .toLowerCase()
          .replace(" ", ""); // remove spaces
    }).collect(Collectors.toList());

    String hiddenAbilityNameId = null;
    if (hasHiddenAbility) {
      hiddenAbilityNameId = abilityNameIds.remove(abilityNameIds.size() - 1);
    }

    return new Abilities(abilityNameIds, hiddenAbilityNameId);
  }

  protected Map<PokedexRegion, Integer> extractDexInfo(Elements tds) {
    Map<PokedexRegion, Integer> dexToNumber = new HashMap<>();

    for (int i = 0; i < tds.size() - 1; i += 2) {
      String dex = tds.get(i).text().split(":")[0];
      PokedexRegion region = PokedexRegion.valueOf(dex);
      String number = tds.get(i + 1).text().split("#")[1];

      if (number.startsWith(NOT_PRESENT_IN_DEX)) {
        dexToNumber.put(region, null);
      } else {
        dexToNumber.put(region, Integer.valueOf(number));
      }
    }

    return dexToNumber;
  }

  protected LinkedHashSet<TypeValue> extractTypes(Elements typeElements) {
    LinkedHashSet<TypeValue> types = new LinkedHashSet<>();

    for (Element typeElement : typeElements) {
      Attribute href = typeElement.attribute("href");
      String[] splits = href.getValue().split("/");
      String type = splits[splits.length - 1].replaceAll(".shtml", ""); // remove the .shtml part of the type
      TypeValue typeValue = TypeValue.valueOf(type);
      types.add(typeValue);
    }

    return types;
  }

  protected Moveset extractMoveset(Document doc) {
    Moveset moveset = new Moveset();

    scraperUtil.parseLevelUpMoveset(doc, moveset);
    scraperUtil.parseTmMoveset(doc, moveset);
    scraperUtil.parseReminderOnlyMoveset(doc, moveset);
    scraperUtil.parseSpecialMoves(doc, moveset);
    scraperUtil.parseEggMovesMoveset(doc, moveset);
    scraperUtil.parsePreEvolutionOnlyMoves(doc, moveset);

    return moveset;
  }

  protected BaseStats parseBaseStats(Element statsDexTable) {
    Elements statsTds = statsDexTable.select("tbody > tr").get(2).select("td");

    return new BaseStats(
        Integer.parseInt(statsTds.get(1).text()),
        Integer.parseInt(statsTds.get(2).text()),
        Integer.parseInt(statsTds.get(3).text()),
        Integer.parseInt(statsTds.get(4).text()),
        Integer.parseInt(statsTds.get(5).text()),
        Integer.parseInt(statsTds.get(6).text())
    );
  }

}
