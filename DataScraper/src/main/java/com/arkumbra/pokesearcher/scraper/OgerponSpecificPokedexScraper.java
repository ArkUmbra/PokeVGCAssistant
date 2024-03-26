package com.arkumbra.pokesearcher.scraper;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OgerponSpecificPokedexScraper extends MultiFormPokedexScraper implements PokemonSpecificScraper {
  private static final String URL = "https://www.serebii.net/pokedex-sv/ogerpon/";

  private static final List<String> pokemonFormNames = List.of(
      "Teal Mask",
      "Hearthflame Mask",
      "Wellspring Mask",
      "Cornerstone Mask"
  );

  private static final List<String> specialAbilityNames = List.of(
      "embodyaspect-tealmask",
      "embodyaspect-hearthflamemask",
      "embodyaspect-wellspringmask",
      "embodyaspect-cornerstonemask"
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
      String ability = abilityTextSplitByForm.get(formOrder);
      Abilities abilities = new Abilities(List.of(ability), null, specialAbilityNames.get(formOrder));
      pokemonDetails.setAbilities(abilities);
      pokemonDetails.setTypes(extractTypes(subTypeTd.select("a")));
      pokemonDetails.setMoveset(extractMovesetForMultiForm(doc, pokemonFormName));


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

}
