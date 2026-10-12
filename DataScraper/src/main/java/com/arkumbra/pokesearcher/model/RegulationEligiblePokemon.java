package com.arkumbra.pokesearcher.model;

import java.util.List;

public record RegulationEligiblePokemon (
    List<PokemonWithSpriteRef> mons,
    String spriteSheetUrl
) { }


