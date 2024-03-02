package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.scraper.Ability;
import com.arkumbra.pokesearcher.scraper.AbilityMetaData;
import com.arkumbra.pokesearcher.scraper.Name;
import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.ClientException;

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

  public void storeAbility(Ability ability) {
    try (var session = driver.session()) {
//      var abilityName = session.executeWrite(tx -> {
//        var query = new Query(
//            "CREATE (a:Name) SET a.english = $englishName, a.japanese = $japaneseName RETURN a.englishName, a.japanese",
//            toParameters(ability.getName()));
//        tx.run(query);
//      });

      var greeting = session.executeWrite(tx -> {
//        var query = new Query(
//            "CREATE (n:Name) SET n.english = $english, n.japanese = $japanese",
//            toParameters(ability.getName()));
//        tx.run(query);
//
//        query = new Query(
//            "CREATE (a:Ability) SET a.idString = $abilityId, a.description = $description RETURN a.idString",
//            parameters("abilityId", ability.getIdName(), "description", ability.getIngameDescription()));
//        var result = tx.run(query);
//        String returnVal = result.single().get(0).asString();
//
//        query = new Query(
//            "CREATE (a)-[:NAMED]->(n)",
//            toParameters(ability.getName()));
//        tx.run(query);

//        var query = new Query(
//            (
//                "CREATE (n:Name) SET n.english = $english, n.japanese = $japanese \n" +
//                    "CREATE ({{abilityKey}}:Ability) SET {{abilityKey}}.idString = $abilityId, {{abilityKey}}.description = $description \n" +
//                    "CREATE ({{abilityKey}})-[:NAMED]->(n) RETURN {{abilityKey}}.idString;"
//            ).replace("{{abilityKey}}", ability.getIdName()),
//            parameters(
//                "english", ability.getName().getEnglishName(),
//                "japanese", ability.getName().getJapanese(),
//                "abilityId", ability.getIdName(),
//                "description", ability.getIngameDescription())
//        );

        try {
          var query = new Query(
//                "MATCH (meta:AbilityMetaData) WHERE meta.label IN $metaLabel \n" +
//                "MATCH (meta:AbilityMetaData {label: $metaLabel}) \n" +
              "CREATE (n:Name {english:$english, japanese:$japanese}) \n" +
                  "CREATE (a:Ability {idString: $abilityId, description: $description}) \n" +
                  "CREATE (a)-[:NAMED]->(n) \n",//+
//                "CREATE (a)-[:WITH_BEHAVIOUR]->(meta)",
              parameters(
                  "english", ability.getName().getEnglishName(),
                  "japanese", ability.getName().getJapanese(),
                  "abilityId", ability.getIdName(),
                  "description", ability.getIngameDescription()
//                  "metaLabel", ability.getMetaData().stream().map(a -> ability.getName()).toList()
              )
          );
          tx.run(query);
        } catch (ClientException e) {
          System.out.println(ability.toString());
          throw e;
        }
        return null;
      });
    }
  }

  private Value toParameters(Name name) {
    return parameters(
        "english", name.getEnglishName(),
        "japanese", name.getJapanese()
    );
  }

  public void printGreeting(final String message) {
    try (var session = driver.session()) {
      var greeting = session.executeWrite(tx -> {
        var query = new Query("CREATE (a:Greeting) SET a.message = $message RETURN a.message + ', from node ' + id(a)", parameters("message", message));
        var result = tx.run(query);
        return result.single().get(0).asString();
      });
      System.out.println(greeting);
    }
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

  public void initializeAbilityMetadata() {
    try (var session = driver.session()) {
      for (AbilityMetaData abilityMetaData : AbilityMetaData.values()) {
        session.executeWrite(tx -> {
          var query = new Query("MERGE (a:AbilityMetaData{label:$label})",
              parameters("label", abilityMetaData.name()));
          tx.run(query);
          return null;
        });
      }
    }
  }

}
