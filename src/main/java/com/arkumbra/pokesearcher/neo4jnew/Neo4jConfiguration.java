package com.arkumbra.pokesearcher.neo4jnew;

import com.arkumbra.pokesearcher.neo4j.Neo4jClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jConfiguration {

  @Bean
  public NewNeo4jClient newNeo4jClient() {
    // TODO get params from config, etc
    return new NewNeo4jClient("bolt://localhost:7687", "neo4j", "password");
  }

  @Bean
  public Neo4jClient neo4jClient() {
    return new Neo4jClient("bolt://localhost:7687", "neo4j", "password");
  }
}
