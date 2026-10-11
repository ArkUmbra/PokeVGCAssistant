package regulation;

import com.arkumbra.pokesearcher.regulation.RegulationParser;
import org.junit.jupiter.api.Test;

public class RegulationParserTest {

  @Test
  public void testRegulationMA() {
    var res = new RegulationParser()
        .parseValidPokemon("https://web-view.app.pokemonchampions.jp/battle/pages/events/rs177501629259kmzbny/ja/pokemon.html");

    System.out.println(res.getToIntegerList());
  }
}
