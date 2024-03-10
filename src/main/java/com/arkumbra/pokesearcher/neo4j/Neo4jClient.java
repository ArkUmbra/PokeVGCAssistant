package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.scraper.*;
import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.ClientException;

import java.util.Map;

import static org.neo4j.driver.Values.parameters;

public class Neo4jClient implements AutoCloseable {

  private final Driver driver;

  public Neo4jClient(String uri, String user, String password) {
    driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
  }

  @Override
  public void close() throws RuntimeException {
    driver.close();
  }

  public void storePokemon(Pokemon pokemon) {
    try (var session = driver.session()) {
      String createPokemonQuery =
          "MERGE (n:Name {english:$english, japanese:$japanese}) \n" +
          "MERGE (p:Pokemon {id:$idName}) \n" +
          "MERGE (p)-[:NAMED]->(n) \n";
      var createPokemonParams = parameters(
          "english", pokemon.getName().getEnglishName(),
          "japanese", nullToBlank(pokemon.getName().getJapanese()),
          "idName", pokemon.getIdName()
      );

      PokedexNumber dex = pokemon.getPokedexNumber();
      String linkPokedexQuery =
          (dex.in(PokedexRegion.National) ? "MATCH (pNat:PokedexRegion {label:'National'})  \n" : "") +
          (dex.in(PokedexRegion.Paldea) ? "MATCH (pPal:PokedexRegion {label:'Paldea'})  \n" : "") +
          (dex.in(PokedexRegion.Kitakami) ? "MATCH (pKit:PokedexRegion {label:'Kitakami'})  \n" : "") +
          (dex.in(PokedexRegion.Blueberry) ? "MATCH (pBlu:PokedexRegion {label:'Blueberry'})  \n" : "") +
              "MERGE (p:Pokemon {id:$idName}) \n" +
          (dex.in(PokedexRegion.National) ? "MERGE (p)-[:IN_DEX {num:$natNum}]->(pNat)  \n" : "") +
          (dex.in(PokedexRegion.Paldea) ? "MERGE (p)-[:IN_DEX {num:$palNum}]->(pPal)  \n" : "") +
          (dex.in(PokedexRegion.Kitakami) ? "MERGE (p)-[:IN_DEX {num:$kitNum}]->(pKit)  \n" : "") +
          (dex.in(PokedexRegion.Blueberry) ? "MERGE (p)-[:IN_DEX {num:$bluNum}]->(pBlu)  \n" : "");


      var linkPokedexParams = parameters(
          "idName", pokemon.getIdName(),
          "natNum", nullToBlank(pokemon.getPokedexNumber().get(PokedexRegion.National)),
          "palNum", nullToBlank(pokemon.getPokedexNumber().get(PokedexRegion.Paldea)),
          "kitNum", nullToBlank(pokemon.getPokedexNumber().get(PokedexRegion.Kitakami)),
          "bluNum", nullToBlank(pokemon.getPokedexNumber().get(PokedexRegion.Blueberry))
      );

      executeQuery(session,
          createPokemonQuery, createPokemonParams,
          linkPokedexQuery, linkPokedexParams
      );

      storePokemonForme(session, pokemon.getIdName(), pokemon.getDetails());
      for (PokemonDetails alternateForm : pokemon.getAlternateForms()) {
        storePokemonForme(session, pokemon.getIdName(), alternateForm);
      }
    }
  }

  private void storePokemonForme(Session session, String idName, PokemonDetails pokemonDetails) {
    String qualifiedFormName = idName + "-" + pokemonDetails.getFormName();

    String createPokemonDetailQuery =
        """
        MATCH (p:Pokemon {id:$idName})
        MERGE (pd:PokemonDetails {id:$qualifiedFormName, hp:$hp, atk:$atk, def:$def, spAtk:$spAtk, spDef:$spDef, spe:$spe})
        MERGE (p)-[:WITH_FORM {formName: $formName}]->(pd)
        """;
    var createPokemonDetailParams = parameters(
        "idName", idName,
        "hp", pokemonDetails.getBaseStats().getHp(),
        "atk", pokemonDetails.getBaseStats().getAtk(),
        "def", pokemonDetails.getBaseStats().getDef(),
        "spAtk", pokemonDetails.getBaseStats().getAtk(),
        "spDef", pokemonDetails.getBaseStats().getSpecialAtk(),
        "spe", pokemonDetails.getBaseStats().getSpeed(),
        "formName", pokemonDetails.getFormName(),
        "qualifiedFormName", qualifiedFormName
    );


    String linkToTypesQuery =
        """
        UNWIND $typeList AS type
          MATCH (t:TypeValue {label: type})
          MATCH (pd:PokemonDetails {id:$qualifiedFormName})
          MERGE (pd)-[:OF_TYPE]->(t)
        """;
    var linkToTypesParams = parameters(
        "typeList", pokemonDetails.getTypes().stream().map(Enum::name).toList(),
        "qualifiedFormName", qualifiedFormName
    );

    String linkToStandardAbilityQuery =
        """
        UNWIND $abilityList AS ability
          MATCH (a:Ability {idString: ability})
          MATCH (pd:PokemonDetails {id:$qualifiedFormName})
          MERGE (pd)-[:WITH_ABILITY]->(a)
        """;

    var linkToStandardAbilityParams = parameters(
        "abilityList", pokemonDetails.getAbilities().getStandardAbilityNameIds(),
        "qualifiedFormName", qualifiedFormName
    );

    executeQuery(session,
        createPokemonDetailQuery, createPokemonDetailParams,
        linkToTypesQuery, linkToTypesParams,
        linkToStandardAbilityQuery, linkToStandardAbilityParams
    );

    pokemonDetails.getAbilities().getHiddenAbilityNameId().ifPresent(hiddenAbility -> {
      String linkToHiddenAbilityQuery =
          """
          MATCH (a:Ability {idString: $ability})
          MATCH (pd:PokemonDetails {id:$qualifiedFormName})
          MERGE (pd)-[:WITH_HIDDEN_ABILITY]->(a)
          """;

      var linkToHiddenAbilityParams = parameters(
          "ability", hiddenAbility,
          "qualifiedFormName", qualifiedFormName
      );

      executeQuery(session,
          linkToHiddenAbilityQuery, linkToHiddenAbilityParams
      );
    });

    Moveset moveset = pokemonDetails.getMoveset();

    String linkToEggMovesQuery =
        """
        UNWIND $moveList AS move
          MATCH (m:Move {id: move})
          MATCH (pd:PokemonDetails {id:$qualifiedFormName})
          MERGE (pd)-[:WITH_EGG_MOVE]->(m)
        """;

    var linkToEggMovesParams = parameters(
        "moveList", pokemonDetails.getMoveset().getEggMoves(),
        "qualifiedFormName", qualifiedFormName
    );

    executeQuery(session,
        linkToEggMovesQuery, linkToEggMovesParams
    );

    String linkToLevelUpMovesQuery =
        """
        UNWIND $moveList AS move
          MATCH (m:Move {id: split(move,'&')[1]})
          MATCH (pd:PokemonDetails {id:$qualifiedFormName})
          MERGE (pd)-[:WITH_LEVEL_UP_MOVE {acquire:split(move,'&')[0]}]->(m)
        """;

    var linkToLevelUpMovesParams = parameters(
        "moveList", pokemonDetails.getMoveset().getLevelUpMoves().stream()
            .map(p -> p.getLeft().toString() + "&" + p.getRight())
            .toList(),
        "qualifiedFormName", qualifiedFormName
    );

    String linkToTmMovesQuery =
        """
        UNWIND $moveList AS move
          MATCH (m:Move {id: split(move,'&')[1]})
          MATCH (pd:PokemonDetails {id:$qualifiedFormName})
          MERGE (pd)-[:WITH_TM_MOVE {acquire:split(move,'&')[0]}]->(m)
        """;

    var linkToTmMovesParams = parameters(
        "moveList", pokemonDetails.getMoveset().getTmMoves().stream()
            .map(p -> p.getLeft().toString() + "&" + p.getRight())
            .toList(),
        "qualifiedFormName", qualifiedFormName
    );

    executeQuery(session,
        linkToLevelUpMovesQuery, linkToLevelUpMovesParams,
        linkToTmMovesQuery, linkToTmMovesParams
    );

  }

  public void storeMove(Move move) {
    try (var session = driver.session()) {
      String createMoveDetails =
          "MATCH (tv:TypeValue {label:$typeValue}) \n" +
          "MATCH (ac:AttackCategory {label:$attackCategory}) \n" +
          "MATCH (pr:Priority {label:$priority}) \n" +
          "MATCH (mt:MoveTarget {label:$moveTarget}) \n" +
          "MERGE (n:Name {english:$english, japanese:$japanese}) \n" +
          "MERGE (m:Move {id:$moveId, summary:$summary, description:$description, pp:$pp, power:$power, critRatio:$critRatio, effectChance:$effectChance}) \n" +
          "MERGE (m)-[:NAMED]->(n) \n" +
          "MERGE (m)-[:TARGETS]->(mt) \n" +
          "MERGE (m)-[:WITH_PRIORITY]->(pr) \n" +
          "MERGE (m)-[:OF_CATEGORY]->(ac) \n" +
          "MERGE (m)-[:OF_TYPE]->(tv) \n";
      var createMoveDetailsParams = parameters(
          "typeValue", move.getType().name(),
          "attackCategory", move.getCategory().name(),
          "priority", move.getPriority().name(),
          "moveTarget", move.getMoveTarget().name(),
          "english", move.getName().getEnglishName(),
          "japanese", nullToBlank(move.getName().getJapanese()),
          "moveId", nullToBlank(move.getIdName()),
          "pp", move.getPp(),
          "power", move.getPower(),
          "critRatio", move.getCritRatio().toString(),
          "effectChance", move.getEffectChance().getChance(),
          "description", move.getIngameDescription(),
          "summary", move.getSummary()
      );

      if (move.getAttackMetaData().isEmpty()) {
        executeQuery(session, createMoveDetails, createMoveDetailsParams);
        return;
      }

      // Find existing metadata node and link ability to that
      String linkMoveToMetaData =
          "UNWIND $metadataList AS metadata\n" +
              "MATCH (a:Move {id: $moveId}) \n" +
              "MATCH (m:AttackMetaData {label: metadata}) \n" +
              "MERGE (a)-[:WITH_BEHAVIOUR]->(m)";
      var linkMoveToMetaDataParams = parameters(
          "moveId", move.getIdName(),
          "metadataList", move.getAttackMetaData().stream().map(Enum::name).toList()
      );

      executeQuery(session,
          createMoveDetails, createMoveDetailsParams,
          linkMoveToMetaData, linkMoveToMetaDataParams);

    }
  }

  public void storeAbility(Ability ability) {
    try (var session = driver.session()) {

      String createAbilityLinkName =
          "MERGE (n:Name {english:$english, japanese:$japanese}) \n" +
          "MERGE (a:Ability {idString: $abilityId, description: $description, summary: $description}) \n" +
          "MERGE (a)-[:NAMED]->(n)";
      var createAbilityLinkNameParams = parameters(
          "english", ability.getName().getEnglishName(),
          "japanese", nullToBlank(ability.getName().getJapanese()),
          "abilityId", ability.getIdName(),
          "description", ability.getIngameDescription()
      );


      if (ability.getMetaData().isEmpty()) {
        executeQuery(session, createAbilityLinkName, createAbilityLinkNameParams);
        return;
      }

      // Find existing metadata node and link ability to that
      String linkAbilityToMetaData =
          "UNWIND $metadataList AS metadata\n" +
              "MATCH (a:Ability {idString: $abilityId}) \n" +
              "MATCH (m:AbilityMetaData {label: metadata}) \n" +
              "MERGE (a)-[:WITH_BEHAVIOUR]->(m)";
      var linkAbilityToMetaDataParams = parameters(
          "abilityId", ability.getIdName(),
          "metadataList", ability.getMetaData().stream().map(a -> a.name()).toList()
      );


      executeQuery(session,
          createAbilityLinkName, createAbilityLinkNameParams,
          linkAbilityToMetaData, linkAbilityToMetaDataParams);
    }
  }

  public void writeJapaneseName(Map<String, String> englishNameToJapaneseName) {
    try (var session = driver.session()) {

      Object[] queryAndParams = new Object[englishNameToJapaneseName.size() * 2];

      int i = 0;
      for (Map.Entry<String, String> englishJapaneseEntry : englishNameToJapaneseName.entrySet()) {
        queryAndParams[i] =
            "MATCH (a:Name {english: $english}) \n" +
            "SET a.japanese = $japanese";

        queryAndParams[i + 1] = parameters(
            "english", englishJapaneseEntry.getKey(),
            "japanese", englishJapaneseEntry.getValue()
        );
        i += 2;
      }

      executeQuery(session, queryAndParams);
    }
  }

  private String nullToBlank(String value) {
    return value == null ? "" : value;
  }

  private Integer nullToBlank(Integer value) {
    return value == null ? -1 : value;
  }

  private void executeQuery(Session session, Object... queryStringAndParameterPairs) {
    if (queryStringAndParameterPairs.length % 2 != 0)
      throw new IllegalArgumentException("Must have proper pairings of query string and params");

    session.executeWrite(tx -> {
      for (int i = 0; i < queryStringAndParameterPairs.length; i += 2) {
        String queryString = (String)queryStringAndParameterPairs[i];
        Value parameters = (Value)queryStringAndParameterPairs[i + 1];

        try {
          var query = new Query(queryString, parameters);
          tx.run(query);
        } catch (ClientException e) {
          System.err.println("Failed to execute the following query " + queryString + ", " + parameters);
          throw e;
        }
      }
      return null;
    });
  }

  private Value toParameters(Name name) {
    return parameters(
        "english", name.getEnglishName(),
        "japanese", name.getJapanese()
    );
  }

  public int getNumberOfAbilities() {
    try (var session = driver.session()) {
      var allAbilities = session.executeWrite(tx -> {
        var query = new Query("MATCH (a:Ability) RETURN a");
        var result = tx.run(query);
        return result.list();
      });
      return allAbilities.size();
    }
  }


  /*
    TODO. Keep queries for later

  - Finds any node with a Name node label containing selected text
  MATCH (n:Name) WHERE n.english contains "eez"
  MATCH (n)<-[rn:NAMED]-(something)
  return n,something;

  - Finds any node with a Name node label containing selected text
  - Selects the type of the node if referenced directly, or via the PokemonDetails node if that is present
  MATCH (n:Name) WHERE n.english contains "eez"
  MATCH (n)<-[rn:NAMED]-(something)
  OPTIONAL MATCH (something)-[rf:WITH_FORM]->(f:PokemonDetails)
  OPTIONAL MATCH (something)-[rt:OF_TYPE]->(tMove:TypeValue)
  OPTIONAL MATCH (f)-[rtf:OF_TYPE]->(tPoke:TypeValue)
  return n.english,something, tMove.label, collect(tPoke.label), rf, f;

  - Same as above, but merge the types, and add a label in the first column to show what sort of node it is
  MATCH (n:Name) WHERE n.english contains "eez"
  MATCH (n)<-[rn:NAMED]-(something)
  OPTIONAL MATCH (something)-[rf:WITH_FORM]->(f:PokemonDetails)
  OPTIONAL MATCH (something)-[rt:OF_TYPE]->(tMove:TypeValue)
  OPTIONAL MATCH (f)-[rtf:OF_TYPE]->(tPoke:TypeValue)
  return labels(something),n.english,something, collect(tMove.label)+collect(tPoke.label), rf, f;
   */
//  public String findElementByName(String englishName) {
//    try (var session = driver.session()) {
//      var allAbilities = session.executeWrite(tx -> {
//        var query = new Query("MATCH (a:Ability) RETURN a");
//        var result = tx.run(query);
//        return result.list();
//      });
//      return allAbilities.size();
//    }
//  }

  public void initializeConstantEnumDataToDatabase() {
    storeSimpleEnum(AbilityMetaData.values(), "AbilityMetaData");
    storeSimpleEnum(TypeValue.values(), "TypeValue");
    storeSimpleEnum(AttackCategory.values(), "AttackCategory");
    storeSimpleEnum(Priority.values(), "Priority");
    storeSimpleEnum(MoveTarget.values(), "MoveTarget");
    storeSimpleEnum(AttackMetaData.values(), "AttackMetaData");
    storeSimpleEnum(PokedexRegion.values(), "PokedexRegion");
  }

  private void storeSimpleEnum(Enum<?>[] values, String graphObjectName) {
    try (var session = driver.session()) {
      for (Enum<?> value : values) {
        session.executeWrite(tx -> {
          var query = new Query("MERGE (v:" + graphObjectName + "{label:$label})",
              parameters("label", value.name()));
          tx.run(query);
          return null;
        });
      }
    }
  }

}
