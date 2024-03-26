package com.arkumbra.pokesearcher.scraper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BasculinSpecificPokedexScraper extends MultiFormPokedexScraper implements PokemonSpecificScraper {
  private static final String URL = "https://www.serebii.net/pokedex-sv/basculin/";

  private static final List<String> pokemonFormNames = List.of(
      "Red-Striped",
      "Blue-Striped",
      "White-Striped"
  );

  private static final List<String> pokemonFormNamesForLevelUpLookup = List.of(
      "Red/Blue-Striped Form",
      "White-Striped Form"
  );

  private static final List<String> pokemonFormNamesForTmEggMoveLookup = List.of(
      "Red Striped Form",
      "Blue Striped Form",
      "White Striped Form"
  );

  @Override
  public boolean validForUrl(String url) {
    return url.equals(URL);
  }

  @Override
  public Pokemon extractPokemonInfo(Document doc, Pokemon pokemon) {

    Element abilityHeaderBlockTd = doc.select("tr:contains(Abilities:)").first(); // get header td
    List<String> abilityTextSplitByForm = splitUpAbilitiesHeaderStringForMultiFormPokemon(abilityHeaderBlockTd.text());

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

      PokemonDetails pokemonDetails = new PokemonDetails();
      pokemonDetails.setFormName(pokemonFormName);

      Element typeTd = dexTableNameNumberDataTds.get(4);
      Element subTypeTd = typeTd;
      if (! typeTd.select("tbody").isEmpty()) {
        subTypeTd = typeTd.select("tr").get(formOrder);
      }

      // Abilities
      // For Basculin, the first 3 abilities listed are form-dependent, the 4th is for all forms, and the last is hidden ability
      // At this stage, 4th and 5th ability are still contained in the last string in abilityTextSplitByForm
      String abilityTextString = abilityTextSplitByForm.get(formOrder) + " - " + abilityTextSplitByForm.get(abilityTextSplitByForm.size() - 1);
      pokemonDetails.setAbilities(parseAbilitiesFromTextString(abilityTextString));
      pokemonDetails.setTypes(extractTypes(subTypeTd.select("a")));
      pokemonDetails.setMoveset(extractMovesetForMultiForm(doc, formOrder));

      // Base Stats
      BaseStats baseStats = parseBaseStats(doc.select("table.dextable:contains(Base Stats - Total)").first());
      pokemonDetails.setBaseStats(baseStats);
      forms.add(pokemonDetails);
    }

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

  protected Moveset extractMovesetForMultiForm(Document doc, int formOrder) {
    Moveset moveset = new Moveset();

    String formName = pokemonFormNamesForLevelUpLookup.get(formOrder <= 1 ? 0 : 1);
    super.scraperUtil.parseLevelUpMoveset(doc, moveset, formName);

    formName = pokemonFormNamesForTmEggMoveLookup.get(formOrder);
    super.scraperUtil.parseTmMoveset(doc, moveset, formName);
    super.scraperUtil.parseReminderOnlyMoveset(doc, moveset);
    super.scraperUtil.parseSpecialMoves(doc, moveset);
    super.scraperUtil.parseEggMovesMoveset(doc, moveset, formName);

    return moveset;
  }

}
