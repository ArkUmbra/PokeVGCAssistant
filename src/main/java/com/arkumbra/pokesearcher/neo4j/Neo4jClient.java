package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.controller.PokemonDetail;
import com.arkumbra.pokesearcher.neo4j.search.*;
import com.arkumbra.pokesearcher.rest.AbilityDetail;
import com.arkumbra.pokesearcher.rest.OtherFormsSummary;
import com.arkumbra.pokesearcher.rest.PokemonSummary;
import com.arkumbra.pokesearcher.scraper.*;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.ClientException;
import org.neo4j.driver.internal.value.ListValue;
import org.neo4j.driver.internal.value.StringValue;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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

  public SearchByNameResponse searchAllElementsByName(SearchByNameRequest searchByNameRequest) {
    /*
    MATCH (n:Name) WHERE n.english contains "al"
    MATCH (n)<-[rn:NAMED]-(something)
    OPTIONAL MATCH (something)-[rf:WITH_FORM]->(f:PokemonDetails)
    OPTIONAL MATCH (something)-[rt:OF_TYPE]->(tMove:TypeValue)
    OPTIONAL MATCH (f)-[rtf:OF_TYPE]->(tPoke:TypeValue)
    return labels(something),something.id, rf.formName, n.english, collect(tMove.label)+collect(tPoke.label), something.description
     */

    try (var session = driver.session()) {
      String searchByNameQuery =
          """
          MATCH (n:Name) 
            WHERE n.englishLower contains $name 
            OR n.japanese contains $name
          MATCH (n)<-[rn:NAMED]-(something)
          OPTIONAL MATCH (something)-[rf:WITH_FORM]->(f:PokemonDetails)
          OPTIONAL MATCH (something)-[rt:OF_TYPE]->(tMove:TypeValue)
          OPTIONAL MATCH (f)-[rtf:OF_TYPE]->(tPoke:TypeValue)
          return labels(something), something.id, rf.formName, n.english, n.japanese,
          collect(tMove.label)+collect(tPoke.label), collect(rtf.index), something.description
          """;

      var searchByNameParmas = parameters(
        "name", searchByNameRequest.nameContains().toLowerCase()
      );

      var responseRecords = session.executeWrite(tx -> {
        return tx.run(searchByNameQuery, searchByNameParmas)
            .stream().map((record) -> {
//              String resultType = record.get(0).asString();
              String resultType = record.get(0).asList(r -> r.asString()).get(0);
              String id = record.get(1).asString();
              String formName = record.get(2).asString();
              Name name = new Name(record.get(3).asString(), record.get(4).asString());
//              int typeIndex = record.get(6).asInt(-1); // Only present for Pokemon, not moves
//              List<TypeValue> types = (typeIndex > 0) ?
              List<TypeValue> types =
                  readTypeListInCorrectOrder(record.get(5), record.get(6));
//                  record.get(5).asList(type -> TypeValue.valueOf(type.asString()));
              String description = record.get(7).asString();

              switch(resultType) {
                case "Pokemon": return new SearchByNameResponsePokemonRecord(id, name, formName, types);
                case "Move": return new SearchByNameResponseMoveRecord(id, name, types.get(0), description);
                case "Ability": return new SearchByNameResponseAbilityRecord(id, name, description);
                default: throw new RuntimeException("Did not understand result type " + resultType);
              }

            }).collect(Collectors.toList());
      });

      return new SearchByNameResponse(responseRecords);
    }

  }

  public void storePokemon(Pokemon pokemon) {
    try (var session = driver.session()) {
      String createPokemonQuery =
          "MERGE (n:Name {english:$english, englishLower:$englishLower, japanese:$japanese}) \n" +
          "MERGE (p:Pokemon {id:$idName}) \n" +
          "MERGE (p)-[:NAMED]->(n) \n";
      var createPokemonParams = parameters(
          "english", pokemon.getName().getEnglish(),
          "englishLower", pokemon.getName().getEnglish().toLowerCase(),
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
    String qualifiedFormName = buildQualifiedPokemonName(idName, pokemonDetails.getFormName());

    String createPokemonDetailQuery =
        """
        MATCH (p:Pokemon {id:$idName})
        MERGE (pd:PokemonDetails {id:$qualifiedFormName, hp:$hp, atk:$atk, def:$def, spAtk:$spAtk, spDef:$spDef, spe:$spe})
        MERGE (p)-[:WITH_FORM{formName:$formName}]->(pd)
        """;
    var createPokemonDetailParams = parameters(
        "idName", idName,
        "hp", pokemonDetails.getBaseStats().getHp(),
        "atk", pokemonDetails.getBaseStats().getAtk(),
        "def", pokemonDetails.getBaseStats().getDef(),
        "spAtk", pokemonDetails.getBaseStats().getSpecialAtk(),
        "spDef", pokemonDetails.getBaseStats().getSpecialDef(),
        "spe", pokemonDetails.getBaseStats().getSpeed(),
        "formName", pokemonDetails.getFormName(),
        "qualifiedFormName", qualifiedFormName
    );


    String linkToTypesQuery =
        """
        UNWIND range(0, size($typeList)-1) as i
          MATCH (t:TypeValue {label: $typeList[i]})
          MATCH (pd:PokemonDetails {id:$qualifiedFormName})
          MERGE (pd)-[:OF_TYPE{index: i}]->(t)
        """;
    var linkToTypesParams = parameters(
        "typeList", pokemonDetails.getTypes().stream().map(Enum::name).toList(),
        "qualifiedFormName", qualifiedFormName
    );

    String linkToStandardAbilityQuery =
        """
        UNWIND $abilityList AS ability
          MATCH (a:Ability {id: ability})
          MATCH (pd:PokemonDetails {id:$qualifiedFormName})
          MERGE (pd)-[:WITH_ABILITY {category: 'regular'}]->(a)
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
          MATCH (a:Ability {id: $ability})
          MATCH (pd:PokemonDetails {id:$qualifiedFormName})
          MERGE (pd)-[:WITH_ABILITY {category: 'hidden'}]->(a)
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

  private String buildQualifiedPokemonName(String idName, String formName) {
    return idName +
        (formName == null || formName.equals("") ? "" :  "-" + formName);
  }

  public void storeMove(Move move) {
    try (var session = driver.session()) {
      String createMoveDetails =
          "MATCH (tv:TypeValue {label:$typeValue}) \n" +
          "MATCH (ac:AttackCategory {label:$attackCategory}) \n" +
          "MATCH (pr:Priority {label:$priority}) \n" +
          "MATCH (mt:MoveTarget {label:$moveTarget}) \n" +
          "MERGE (n:Name {english:$english, englishLower:$englishLower, japanese:$japanese}) \n" +
          "MERGE (m:Move {id:$moveId, summary:$summary, accuracy:$accuracy, description:$description, pp:$pp, power:$power, critRatio:$critRatio, effectChance:$effectChance}) \n" +
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
          "english", move.getName().getEnglish(),
          "englishLower", move.getName().getEnglish().toLowerCase(),
          "japanese", nullToBlank(move.getName().getJapanese()),
          "moveId", nullToBlank(move.getIdName()),
          "accuracy", move.getAccuracy(),
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
              "MERGE (a)-[:WITH_ATTACK_BEHAVIOUR]->(m)";
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
          "MERGE (n:Name {english:$english, englishLower:$englishLower, japanese:$japanese}) \n" +
          "MERGE (a:Ability {id: $abilityId, description: $description, summary: $description}) \n" +
          "MERGE (a)-[:NAMED]->(n)";
      var createAbilityLinkNameParams = parameters(
          "english", ability.getName().getEnglish(),
          "englishLower", ability.getName().getEnglish().toLowerCase(),
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
              "MATCH (a:Ability {id: $abilityId}) \n" +
              "MATCH (m:AbilityMetaData {label: metadata}) \n" +
              "MERGE (a)-[:WITH_ABILITY_BEHAVIOUR]->(m)";
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

  public List<AbilitySummary> getAllAbilities() {
    try (var session = driver.session()) {
      List<Record> allAbilities = session.executeWrite(tx -> {
        var query = new Query(
            """
                MATCH (a:Ability)
                MATCH (a)-[:NAMED]->(n:Name)
                OPTIONAL MATCH (a)-[:WITH_ABILITY_BEHAVIOUR]->(m:AbilityMetaData)
                OPTIONAL MATCH (a)<-[]-(pokemon:PokemonDetails)
                RETURN a.id, n.english, n.japanese, a.description, collect(m.label), count(pokemon)
                """
        );
        var result = tx.run(query);
        return result.list();
      });

      List<AbilitySummary> abilities = allAbilities.stream().map(abilityRecord -> {
        String id = abilityRecord.values().get(0).asString();
        String englishName = abilityRecord.values().get(1).asString();
        String japaneseName = abilityRecord.values().get(2).asString();
        String description = abilityRecord.values().get(3).asString();
        List<AbilityMetaData> metaData = abilityRecord.values().get(4).asList(raw -> {
          return AbilityMetaData.valueOf(raw.asString());
        });
        Ability ability = new Ability(id, new Name(englishName, japaneseName), description, new HashSet<>(metaData));
        return new AbilitySummary(ability, abilityRecord.get(5).asInt());
      }).collect(Collectors.toList());

      return abilities;
    }
  }

  public AbilityDetail getAbilityDetail(String abilityId) {
    try (var session = driver.session()) {

      return session.executeWrite(tx -> {
        // Get ability
        String abilityQuery =
            """
            MATCH (a:Ability {id: $abilityId})
            MATCH (a)-[]->(n:Name)
            OPTIONAL MATCH (a)-[]->(m:AbilityMetaData)
            RETURN a.id, n.english, n.japanese, a.description, collect(m.label)
            """;
        var abilityParameters = parameters("abilityId", abilityId);

        var abilityResult = tx.run(new Query(abilityQuery, abilityParameters)).single();
        Ability ability = new Ability(
            abilityResult.get(0).asString(),
            new Name(
                abilityResult.get(1).asString(),
                abilityResult.get(2).asString()
            ),
            abilityResult.get(3).asString(),
            new HashSet<>(abilityResult.get(4).asList(a -> AbilityMetaData.valueOf(a.asString())))
        );


        // Get Pokemon with this ability
        var pokemonWithAbilityQuery = new Query(
            """
                 MATCH (a:Ability {id: $abilityId})
                 MATCH (details:PokemonDetails)-[rAbility:WITH_ABILITY]->(a)
                 MATCH (pokemon:Pokemon)-[form:WITH_FORM]->(details)
                 MATCH (pokemon)-[rDex:IN_DEX]->(:PokedexRegion{label:'National'})
                 MATCH (pokemon)-[:NAMED]->(name:Name)
                 MATCH (details)-[:OF_TYPE]->(type:TypeValue)
                 RETURN distinct(details.id), rAbility.category, name.english, name.japanese,form.formName, 
                        collect(type.label), details.hp, details.atk, details.def, details.spAtk, details.spDef, 
                        details.spe, pokemon.id, rDex.num
                """,
            abilityParameters
        );
        Result matchingPokemon = tx.run(pokemonWithAbilityQuery);
        List<PokemonSummary> pokemonWithRegularAbility = new ArrayList<>();
        List<PokemonSummary> pokemonWithHiddenAbility = new ArrayList<>();
        matchingPokemon.forEachRemaining(pokemonResult -> {
          boolean isHiddenAbility = pokemonResult.get(1).asString().equals("hidden");
          Name name = new Name(
              pokemonResult.get(2).asString(),
              pokemonResult.get(3).asString()
          );
          String formName = pokemonResult.get(4).asString();
          List<TypeValue> types = pokemonResult.get(5).asList(a -> TypeValue.valueOf(a.asString()));
          BaseStats baseStats = new BaseStats(
              pokemonResult.get(6).asInt(),
              pokemonResult.get(7).asInt(),
              pokemonResult.get(8).asInt(),
              pokemonResult.get(9).asInt(),
              pokemonResult.get(10).asInt(),
              pokemonResult.get(11).asInt()
          );

          PokemonSummary pokemonSummary = new PokemonSummary(
              pokemonResult.get(12).asString(),
              name,
              formName,
              types,
              baseStats,
              pokemonResult.get(13).asInt()
          );

          if (isHiddenAbility)
            pokemonWithHiddenAbility.add(pokemonSummary);
          else
            pokemonWithRegularAbility.add(pokemonSummary);
        });

        return new AbilityDetail(ability, pokemonWithRegularAbility, pokemonWithHiddenAbility);
      });
    }
  }

  public List<PokemonSummary> getAllPokemon() {
    try (var session = driver.session()) {
      List<Record> allPokemon = session.executeWrite(tx -> {
        var query = new Query(
            """
                MATCH (details:PokemonDetails)
                MATCH (pokemon:Pokemon)-[form:WITH_FORM]->(details)
                MATCH (pokemon)-[rDex:IN_DEX]->(dex:PokedexRegion{label:'National'})
                MATCH (pokemon)-[:NAMED]->(name:Name)
                MATCH (details)-[t:OF_TYPE]->(type:TypeValue)
                RETURN distinct(details.id), pokemon.id, rDex.num, name.english, name.japanese, form.formName, 
                collect(type.label), collect(t.index), 
                details.hp, details.atk, details.def, details.spAtk, details.spDef, details.spe
                ORDER BY rDex.num
                """
        );
        var result = tx.run(query);
        return result.list();
      });

      return allPokemon.stream().map(summaryRecord -> {
        // index 0 is fully qualified name - don't need it in the result, just using it for distinct()
        String pokemonId = summaryRecord.get(1).asString();
        Integer nationalDexNum = summaryRecord.get(2).asInt();
        String englishName = summaryRecord.get(3).asString();
        String japaneseName = summaryRecord.get(4).asString();
        String formName = summaryRecord.get(5).asString();
        List<TypeValue> types = readTypeListInCorrectOrder(summaryRecord.get(6), summaryRecord.get(7));

        BaseStats baseStats = new BaseStats(
            summaryRecord.get(8).asInt(),
            summaryRecord.get(9).asInt(),
            summaryRecord.get(10).asInt(),
            summaryRecord.get(11).asInt(),
            summaryRecord.get(12).asInt(),
            summaryRecord.get(13).asInt()
        );

        return new PokemonSummary(pokemonId, new Name(englishName, japaneseName), formName, types, baseStats, nationalDexNum);
      }).collect(Collectors.toList());
    }
  }

  private List<TypeValue> readTypeListInCorrectOrder(Value typeLabels, Value typeIndices) {
    List<TypeValue> unordered = typeLabels.asList(rawType -> {
      return TypeValue.valueOf(rawType.asString());
    });

    if (typeIndices.isEmpty()) {
      return unordered;
    }

    List<Integer> indices = typeIndices.asList(Value::asInt);

    List<TypeValue> ordered = new ArrayList<>(unordered.size());
    unordered.forEach((unused) -> ordered.add(null));

    for (int i = 0; i < typeIndices.size(); i++) {
      int index = indices.get(i);
      TypeValue value = unordered.get(i);
      ordered.set(index, value);
    }
    return ordered;
  }

  public PokemonDetail getPokemonDetail(String pokemonId, String formName) {
    try (var session = driver.session()) {
      String qualifiedPokemonId = buildQualifiedPokemonName(pokemonId, formName);

      PokedexNumber pokedexNumber = getPokedexNumber(session, pokemonId);
      OtherFormsSummary otherFormsSummary = getOtherFormsSummary(session, pokemonId, qualifiedPokemonId);
      AbilitiesDetail abilitiesDetail = getPokemonAbilities(session, qualifiedPokemonId);
      MovesetDetail movesetDetail = getPokemonMoveset(session, qualifiedPokemonId);

      return session.executeWrite(tx -> {
        var query = new Query(
            """
              MATCH (details:PokemonDetails {id:$qualifiedPokemonId})
              MATCH (pokemon:Pokemon)-[form:WITH_FORM]->(details)
              MATCH (pokemon)-[:NAMED]->(name:Name)
              MATCH (details)-[t:OF_TYPE]->(type:TypeValue)
              RETURN distinct(details.id), pokemon.id, name.english, name.japanese, 
              collect(type.label), collect(t.index),
              details.hp, details.atk, details.def, details.spAtk, details.spDef, details.spe
              """,
            parameters("qualifiedPokemonId", qualifiedPokemonId)
        );

        Record baseDetailResult = tx.run(query).single();

        // index 0 is fully qualified name - don't need it in the result, just using it for distinct()
//        String pokemonIdString = baseDetailResult.get(1).asString();
        String englishName = baseDetailResult.get(2).asString();
        String japaneseName = baseDetailResult.get(3).asString();
        List<TypeValue> types = readTypeListInCorrectOrder(baseDetailResult.get(4), baseDetailResult.get(5));

        BaseStats baseStats = new BaseStats(
            baseDetailResult.get(6).asInt(),
            baseDetailResult.get(7).asInt(),
            baseDetailResult.get(8).asInt(),
            baseDetailResult.get(9).asInt(),
            baseDetailResult.get(10).asInt(),
            baseDetailResult.get(11).asInt()
        );

        return new PokemonDetail(
            pokemonId,
            new Name(englishName, japaneseName),
            types,
            baseStats,
            formName,
            otherFormsSummary,
            abilitiesDetail,
            movesetDetail,
            pokedexNumber
        );
      });

    }
  }

  private OtherFormsSummary getOtherFormsSummary(Session session, String pokemonIdToMatch, String qualifiedPokemonIdToNotMatch) {
    String queryString = """
        MATCH (pokemon:Pokemon {id:$pokemonId})
        MATCH (pokemon:Pokemon)-[form:WITH_FORM]->(details:PokemonDetails)
        WHERE details.id <> $qualifiedPokemonIdThisForm
        RETURN details.id,form.formName
        """;
    var queryParams = parameters(
        "pokemonId", pokemonIdToMatch,
          "qualifiedPokemonIdThisForm", qualifiedPokemonIdToNotMatch
        );

    return session.executeWrite(tx -> {
      List<Pair<String, String>> formNamesToFormId = tx.run(queryString, queryParams).stream()
          .map(record -> {
            String qualifiedPokemonId = record.get(0).asString();
            String formName = record.get(1).asString();

            return new Pair<>(formName, qualifiedPokemonId);
          }).toList();

      return new OtherFormsSummary(formNamesToFormId);
    });
  }

  private PokedexNumber getPokedexNumber(Session session, String pokemonId) {
    String queryString = """
        MATCH (pokemon:Pokemon {id:$pokemonId})
        MATCH (pokemon)-[rd:IN_DEX]->(d)
        RETURN rd.num, d.label
        """;
    var queryParams = parameters("pokemonId", pokemonId);

    return session.executeWrite(tx -> {
      PokedexNumber pokedexNumber = new PokedexNumber();

      tx.run(queryString, queryParams).stream().forEach(record -> {
        PokedexRegion region = PokedexRegion.valueOf(record.get(1).asString());
        int num = record.get(0).asInt();
        pokedexNumber.put(region, num);
      });

      return pokedexNumber;
    });
  }

  private AbilitiesDetail getPokemonAbilities(Session session, String qualifiedPokemonId) {
    String queryString = """
        MATCH (details:PokemonDetails {id:$qualifiedPokemonId})
        MATCH (details)-[r]->(a:Ability)
        MATCH (a)-[]->(n:Name)
        OPTIONAL MATCH (a)-[]->(m:AbilityMetaData)
        return r.category, n.english, n.japanese, a.id, a.description, collect(m.label);
        """;
    var queryParams = parameters("qualifiedPokemonId", qualifiedPokemonId);

    return session.executeWrite(tx -> {
      List<Record> result = tx.run(queryString, queryParams).list();

      List<Ability> standardAbilities = new ArrayList<>();
      Ability hiddenAbility = null;

      for (Record record : result) {
        boolean isHiddenAbility = record.get(0).asString().equals("hidden");
        String englishName = record.get(1).asString();
        String japaneseName = record.get(2).asString();
        String abilityId = record.get(3).asString();
        String description = record.get(4).asString();
        Set<AbilityMetaData> metaData = new HashSet<>(record.get(5).asList(r -> AbilityMetaData.valueOf(r.asString())));

        var ability = new Ability(abilityId, new Name(englishName, japaneseName), description, metaData);
        if (isHiddenAbility) hiddenAbility = ability;
        else standardAbilities.add(ability);
      }

      return new AbilitiesDetail(standardAbilities, hiddenAbility);
    });
  }

  private MovesetDetail getPokemonMoveset(Session session, String qualifiedPokemonId) {
    String queryString =
        """
            MATCH (details:PokemonDetails {id:$qualifiedPokemonId})
            MATCH (details)-[r]->(m:Move)
            MATCH (m)-[]->(n:Name)
            MATCH (m)-[]->(type:TypeValue)
            MATCH (m)-[]->(t:MoveTarget)
            MATCH (m)-[]->(p:Priority)
            MATCH (m)-[]->(c:AttackCategory)
            OPTIONAL MATCH (m)-[]->(meta:AttackMetaData)
            return type(r), r.acquire, n.english, n.japanese, m.description, m.power, m.pp, m.accuracy, 
            m.effectChance, m.critRatio, t.label, p.label, c.label, m.id, type.label, collect(meta.label)
            """;
    var queryParams = parameters("qualifiedPokemonId", qualifiedPokemonId);

    return session.executeWrite(tx -> {
      List<Record> result = tx.run(queryString, queryParams).list();

      List<Pair<MoveAcquire, Move>> levelUpMoves = new ArrayList<>();
      List<Pair<MoveAcquire, Move>> tmMoves = new ArrayList<>();
      List<Move> eggMoves = new ArrayList<>();

      for (Record record : result) {
        String movelistType = record.get(0).asString();
        String acquireType = record.get(1).asString();
        String englishName = record.get(2).asString();
        String japaneseName = record.get(3).asString();
        String description = record.get(4).asString();
        int power = record.get(5).asInt(-1);
        int pp = record.get(6).asInt(-1);
        int accuracy = record.get(7).asInt(-1);
        EffectChance effectChance = new EffectChance(record.get(8).asInt());
        BigDecimal critRatio = new BigDecimal(record.get(9).asString());
        MoveTarget target = MoveTarget.valueOf(record.get(10).asString());
        Priority priority = Priority.valueOf(record.get(11).asString());
        AttackCategory attackCategory = AttackCategory.valueOf(record.get(12).asString());
        String moveId = record.get(13).asString();
        TypeValue typeValue = TypeValue.valueOf(record.get(14).asString());
        Set<AttackMetaData> metaData = new HashSet<>(record.get(15).asList(r -> AttackMetaData.valueOf(r.asString())));

        Move move = new Move(moveId, new Name(englishName, japaneseName), typeValue, attackCategory, pp, power,
            accuracy, description, critRatio, priority, effectChance, target, metaData);

        if (movelistType.equals("WITH_EGG_MOVE")) {
          eggMoves.add(move);
        } else if (movelistType.equals("WITH_LEVEL_UP_MOVE")) {
          levelUpMoves.add(new Pair<>(MoveAcquire.of(acquireType), move));
        } else if (movelistType.equals("WITH_TM_MOVE")) {
          tmMoves.add(new Pair<>(MoveAcquire.of(acquireType), move));
        } else {
          throw new IllegalArgumentException("Unexpected value for movelist type " + movelistType);
        }
      }

      try {
        Collections.sort(levelUpMoves);
        Collections.sort(tmMoves);
        Collections.sort(eggMoves);
      } catch (Exception e) {
        e.printStackTrace();
      }

      return new MovesetDetail(levelUpMoves, tmMoves, eggMoves);
    });

  }

  public MoveDetail getMoveDetail(String moveId) {
    String queryString =
        """
            MATCH (details)-[r]->(m:Move {id:$moveId})
            MATCH (m)-[]->(n:Name)
            MATCH (m)-[]->(type:TypeValue)
            MATCH (m)-[]->(t:MoveTarget)
            MATCH (m)-[]->(p:Priority)
            MATCH (m)-[]->(c:AttackCategory)
            OPTIONAL MATCH (m)-[]->(meta:AttackMetaData)
            return type(r), r.acquire, n.english, n.japanese, m.description, m.power, m.pp, m.accuracy, 
            m.effectChance, m.critRatio, t.label, p.label, c.label, m.id, type.label, collect(meta.label)
            """;
    var queryParams = parameters("moveId", moveId);

    try (var session = driver.session()) {
      return session.executeWrite(tx -> {
        Record record = tx.run(queryString, queryParams).single();

        String englishName = record.get(2).asString();
        String japaneseName = record.get(3).asString();
        String description = record.get(4).asString();
        int power = record.get(5).asInt(-1);
        int pp = record.get(6).asInt(-1);
        int accuracy = record.get(7).asInt(-1);
        EffectChance effectChance = new EffectChance(record.get(8).asInt());
        BigDecimal critRatio = new BigDecimal(record.get(9).asString());
        MoveTarget target = MoveTarget.valueOf(record.get(10).asString());
        Priority priority = Priority.valueOf(record.get(11).asString());
        AttackCategory attackCategory = AttackCategory.valueOf(record.get(12).asString());
        TypeValue typeValue = TypeValue.valueOf(record.get(14).asString());
        Set<AttackMetaData> metaData = new HashSet<>(record.get(15).asList(r -> AttackMetaData.valueOf(r.asString())));

        Move move = new Move(moveId, new Name(englishName, japaneseName), typeValue, attackCategory, pp, power,
            accuracy, description, critRatio, priority, effectChance, target, metaData);

        return new MoveDetail(move);
      });
    }
  }

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
