package com.arkumbra.pokesearcher.scraper;


import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// While each of these Pokemon have forms from 2 or more regions, the Serebii page
// for them is characterized by listing the first form name as 'Normal' in certain places (such as the alt-text in
// the TM Moveset list). This means in certain places we need to replace the
public class RegionalFormSpecificPokedexScraper extends MultiFormPokedexScraper implements PokemonSpecificScraper {
  private static final Set<String> URLS = Set.of(
      "https://www.serebii.net/pokedex-sv/raichu/",
      "https://www.serebii.net/pokedex-sv/sandshrew/",
      "https://www.serebii.net/pokedex-sv/sandslash/",
      "https://www.serebii.net/pokedex-sv/vulpix/",
      "https://www.serebii.net/pokedex-sv/ninetales/",
      "https://www.serebii.net/pokedex-sv/diglett/",
      "https://www.serebii.net/pokedex-sv/dugtrio/",
      "https://www.serebii.net/pokedex-sv/meowth/",
      "https://www.serebii.net/pokedex-sv/persian/",
      "https://www.serebii.net/pokedex-sv/geodude/",
      "https://www.serebii.net/pokedex-sv/graveler/",
      "https://www.serebii.net/pokedex-sv/golem/",
      "https://www.serebii.net/pokedex-sv/grimer/",
      "https://www.serebii.net/pokedex-sv/muk/",
      "https://www.serebii.net/pokedex-sv/exeggutor/",
      "https://www.serebii.net/pokedex-sv/ponyta/",
      "https://www.serebii.net/pokedex-sv/slowpoke/",
      "https://www.serebii.net/pokedex-sv/slowbro/",
      "https://www.serebii.net/pokedex-sv/slowking/",
      "https://www.serebii.net/pokedex-sv/weezing/",
      "https://www.serebii.net/pokedex-sv/articuno/",
      "https://www.serebii.net/pokedex-sv/zapdos/",
      "https://www.serebii.net/pokedex-sv/moltres/",
      "https://www.serebii.net/pokedex-sv/growlithe/",
      "https://www.serebii.net/pokedex-sv/arcanine/",
      "https://www.serebii.net/pokedex-sv/voltorb/",
      "https://www.serebii.net/pokedex-sv/typhlosion/",
      "https://www.serebii.net/pokedex-sv/qwilfish/",
      "https://www.serebii.net/pokedex-sv/sneasel/",
      "https://www.serebii.net/pokedex-sv/samurott/",
      "https://www.serebii.net/pokedex-sv/lilligant/",
      "https://www.serebii.net/pokedex-sv/zorua/",
      "https://www.serebii.net/pokedex-sv/zoroark/",
      "https://www.serebii.net/pokedex-sv/braviary/",
      "https://www.serebii.net/pokedex-sv/sliggoo/",
      "https://www.serebii.net/pokedex-sv/goodra/",
      "https://www.serebii.net/pokedex-sv/avalugg/",
      "https://www.serebii.net/pokedex-sv/decidueye/",
      "https://www.serebii.net/pokedex-sv/wooper/"
  );

  private static final Map<String, Set<String>> pokemonIdToPreEvoNames = new HashMap<>();

  static {
    pokemonIdToPreEvoNames.put("sandslash", Set.of("Sandshrew"));
    pokemonIdToPreEvoNames.put("raichu", Set.of("Pikachu", "Pichu"));
    pokemonIdToPreEvoNames.put("ninetales", Set.of("Vulpix"));
    pokemonIdToPreEvoNames.put("golem", Set.of("Graveler", "Geodude"));
    pokemonIdToPreEvoNames.put("slowking", Set.of("Slowpoke"));
    pokemonIdToPreEvoNames.put("slowbro", Set.of("Slowpoke"));
    pokemonIdToPreEvoNames.put("samurott", Set.of("Dewott, Oshwawott"));
    pokemonIdToPreEvoNames.put("lilligant", Set.of("Petilil"));
    pokemonIdToPreEvoNames.put("zoroark", Set.of("Zorua"));
    pokemonIdToPreEvoNames.put("braviary", Set.of("Rufflet"));
    pokemonIdToPreEvoNames.put("sliggoo", Set.of("Goomy"));
    pokemonIdToPreEvoNames.put("goodra", Set.of("Sliggoo", "Goomy"));
    pokemonIdToPreEvoNames.put("decidueye", Set.of("Rowlett", "Dartrix"));
    pokemonIdToPreEvoNames.put("zoroark", Set.of("zorua"));
  }

  private final ThreadLocal<List<String>> formNames = new ThreadLocal<>();
  private final ThreadLocal<String> thisPokemonNameId = new ThreadLocal<>();

  @Override
  public boolean validForUrl(String url) {
    return URLS.contains(url);
  }

  @Override
  public Pokemon extractPokemonInfo(Document doc, Pokemon pokemon) {
    thisPokemonNameId.set(pokemon.getIdName());
    return super.extractPokemonInfo(doc, pokemon);
  }

  @Override
  public List<String> parseFormNamesFromFormDexTable(Elements formImages) {
    // Hook in here so we can keep track of the form names to use later
    List<String> formNames = super.parseFormNamesFromFormDexTable(formImages);
    this.formNames.set(formNames);
    return formNames;
  }

  @Override
  protected Moveset extractMovesetForMultiForm(Document doc, String formName) {
    String formNameToUse = formName.equals(this.formNames.get().get(0)) ? "Normal" : formName;
    return super.extractMovesetForMultiForm(doc, formNameToUse);
  }

  @Override
  protected Set<String> getPreEvolutionNames() {
    Set<String> preEvoNames = pokemonIdToPreEvoNames.get(thisPokemonNameId.get());
    if (preEvoNames == null) {
      throw new RuntimeException("Unable to find pre-evo name for " + thisPokemonNameId.get());
    }
    return preEvoNames;
  }
}
