package com.arkumbra.pokesearcher.scraper;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

// TODO Refactor this to handle dex by generation
public class MultiFormPokedexScraper extends SingleFormPokedexScraper {
  private static final String NOT_PRESENT_IN_DEX = "-";

  protected Elements getAlternateFormDexTable(Document doc) {
    return doc.select("table.dextable:contains(Alternate Forms)");
  }

  @Override
  protected Pokemon extractPokemonInfo(Document doc, Pokemon pokemon) {
    Elements formImages = getAlternateFormDexTable(doc).select("img");
    List<String> pokemonFormNames = parseFormNamesFromFormDexTable(pokemon.getIdName(), formImages);

    // First form can also be considered 'Normal', so we can use that when searching for matching data
    Element abilityHeaderBlockTd = doc.select("tr:contains(Abilities:)").first(); // get header td
    List<String> abilityTextSplitByForm = splitUpAbilitiesHeaderStringForMultiFormPokemon(abilityHeaderBlockTd.text());


    boolean baseStatsForAlternateFormsAreGroupedTogether = doc.select("table.dextable:contains(Base Stats - Total)").size() == 1 ||
        !doc.select("table.dextable:contains(Stats - Alternate Forms)").isEmpty();

    // TDs
    // 0 - Name
    // 1 - Other Names
    // 2 - No.
    // 3 - Gender Ratio
    // 4 - Type (for each form)
    Elements dexTableNameNumberDataTds = Util.selectDataRowTdsByPartOfTrHeading(doc, "Other names");

    LinkedList<PokemonDetails> forms = new LinkedList<>();

    // Name
    String englishName = dexTableNameNumberDataTds.get(0).text();
    Name name = new Name();
    name.english = englishName;
    pokemon.setName(name);

    for (int formOrder = 0; formOrder < pokemonFormNames.size(); formOrder++) {
      String pokemonFormName = pokemonFormNames.get(formOrder);
      String escapedFormName = pokemonFormName.replace("'", "\\'");

      PokemonDetails pokemonDetails = new PokemonDetails();
      // Some pages just have the form name listed as the Pokemon's name... change it here to remove it
      pokemonDetails.setFormName(pokemonFormName.equals(name.english) ? "" : pokemonFormName);

      // a. Try searching by form name (just use the first few characters)
      Element typeTd = dexTableNameNumberDataTds.get(4);
      Element subTypeTd = typeTd;
      if (! typeTd.select("tbody").isEmpty()) {
        //  if there are subtables in this tr, it means there are type differences between forms, so we need to dig out
        // only the relevant elements

        // There should be a tr for each Pokemon form, and these seem to be in the same order they are listed in the
        // "Alternate Forms" section, so we should just be able to grab the TR by its ID/order
        // However, in certain cases like with Rotom, the ordering is different so we can try to find based on form  name first

        subTypeTd = typeTd.select("tr:contains(" + escapedFormName + ")").first();
        if (subTypeTd == null) {
          subTypeTd = typeTd.select("tr").get(formOrder);
        }
      }

      // Abilities
      // Some multi form Pokemon just share one set of abilities, so  make sure we don't go out of bounds by trying
      // to select an ability string that doesn't exist.
      String abilityTextString = abilityTextSplitByForm.get(Math.min(formOrder, abilityTextSplitByForm.size() - 1));
      pokemonDetails.setAbilities(parseAbilitiesFromTextString(abilityTextString));

      pokemonDetails.setTypes(extractTypes(subTypeTd.select("a")));
      pokemonDetails.setMoveset(extractMovesetForMultiForm(doc, escapedFormName, formOrder));

      // Base Stats
      BaseStats baseStats = null;
      Elements allStatsTable = doc.select("table.dextable:contains(Base Stats - Total)");
      if (baseStatsForAlternateFormsAreGroupedTogether && formOrder > 0) {
        baseStats = parseBaseStats(allStatsTable.get(allStatsTable.size() - 1));
      } else {
        baseStats = parseBaseStats(allStatsTable.get(formOrder));
      }

      pokemonDetails.setBaseStats(baseStats);
      forms.add(pokemonDetails);
    }

    PokemonDetails details = new PokemonDetails();

    // Pokedex number
    Map<PokedexRegion, Integer> dexToNumber = extractDexInfo(
        dexTableNameNumberDataTds.get(2)
            .select("table > tbody > tr > td")
    );

    PokedexNumber pokedexNumber = new PokedexNumber();
    pokedexNumber.dexNumbers = dexToNumber;
    pokemon.setPokedexNumber(pokedexNumber);
    pokemon.setDetails(forms.removeFirst());
    pokemon.setAlternateForms(forms);

    return pokemon;
  }

  protected List<String> parseFormNamesFromFormDexTable(String pokemonId, Elements formImages) {
    return formImages.stream()
        // Sometimes there are other images like for items, etc, so try to only pick out the Pokemon
        .filter(formImg -> formImg.attribute("src").getValue().contains("/pokemon/"))
        // Note: Sometimes the spelling form or forme is used, so this should handle both
        // alt value should be something like: "Kantonian Form", "Incarnate Forme", etc
        .map(formImg -> formImg.attr("alt"))
        // Some entries, (i.e. Basculegion) aren't consistent between the form name and alt text
        // In this case, we reduce 'Male Sprite' to 'Male'
        .map(formName -> formName.replace(" Sprite", ""))
        .map(formName -> formName.toLowerCase().equals(pokemonId) ? "Default Form" : formName)
        .toList();
  }

  protected List<String> splitUpAbilitiesHeaderStringForMultiFormPokemon(String text) {
    // Example abilities header text for multi form Pokemon before process
    // Abilities: Levitate - Neutralizing Gas - Stench (Hidden) (Normal Forme) - Levitate- Neutralizing Gas - Misty Surge (Hidden) (Galarian Form)
    // Abilities: Defiant (Teal Mask) - Water Absorb (Wellspring Mask) - Mold Breaker (Hearthflame Mask) - Sturdy (Cornerstone Mask)

    // Because matching the form names is unreliable due to seemingly random use of form vs forme, we will just detect
    // brackets and split there. Except in the case of '(Hidden)', which we need to include in the output

    // Remove the leading 'Abilities:  ' string
    text = text.replace("Abilities: ", "");

    List<String> results = new ArrayList<>();
    StringBuilder currentAbilityString = new StringBuilder();

    String hiddenTag = "(Hidden)";
    String hiddenTagAlternate = "(Hidden Ability)";

    for (int i = 0; i < text.length(); i++) {

      if (text.charAt(i) == '(') {
        // Start of either (Hidden) or (form name)
        String bracketedWord = parseBracketedWord(text, i);

        // Fast-forward the index to the end of this bracketed word we've found
        i += bracketedWord.length();

        if (bracketedWord.equals(hiddenTag) || bracketedWord.equals(hiddenTagAlternate)) {
          // We found the hidden ability, so let's just add it to the current word and
          currentAbilityString.append(bracketedWord);

        } else {
          // Seems like we found the form name, so lets store the current word and reset
          results.add(currentAbilityString.toString().trim());
          currentAbilityString = new StringBuilder();

          // Sometimes there's some formatting junk after the form name, so fast forward until we get to the next
          // actual word
          while (i < text.length() - 1 && ! Character.isAlphabetic(text.charAt(i + 1)))
            i++;
        }


      } else {
        // Append to the end of our current ability string
        currentAbilityString.append(text.charAt(i));
      }
    }

    if (! currentAbilityString.isEmpty())
      results.add(currentAbilityString.toString());

    return results;
  }

  protected String parseBracketedWord(String word, int indexOfOpenBracket) {
    for (int i = indexOfOpenBracket; i < word.length(); i++) {
      if (word.charAt(i) == ')') {
        return word.substring(indexOfOpenBracket, i + 1);
      }
    }

    throw new RuntimeException("Couldn't find closing bracket of bracketed word");
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


  protected Moveset extractMovesetForMultiForm(Document doc, String formName) {
    return extractMovesetForMultiForm(doc, formName, -1);
  }

  protected Moveset extractMovesetForMultiForm(Document doc, String formName, int formOrder) {
    Moveset moveset = new Moveset();

    super.scraperUtil.parseLevelUpMoveset(doc, moveset, formName);
    super.scraperUtil.parseTmMoveset(doc, moveset, formName);
    super.scraperUtil.parseReminderOnlyMoveset(doc, moveset);
    super.scraperUtil.parseSpecialMoves(doc, moveset);
    super.scraperUtil.parseEggMovesMoveset(doc, moveset);
    super.scraperUtil.parsePreEvolutionOnlyMoves(doc, moveset, formName, this::getPreEvolutionNames);

    // Due to a quirk with how the alt-text for pre-evo moves is displayed on Serebii, we rerun this if its the
    // first or default form. Usually the alt text would be "Learn as Alola form Sandshrew" or something, but for the
    // default form is it (always??) listed as "Learn as Sandshrew" with no reference to Kantonian form
    if (formOrder == 0) {
      super.scraperUtil.parsePreEvolutionOnlyMoves(doc, moveset, null, this::getPreEvolutionNames);
    }

    return moveset;
  }

  protected Set<String> getPreEvolutionNames() {
    // Only needs to be implemented in this case of a Pokemon with Pre-Evo only moves, and also is multi form
    throw new RuntimeException("This method should be implemented in the relevant scraper subclass!");
  }

}
