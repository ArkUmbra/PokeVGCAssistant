package com.arkumbra.pokesearcher.model;

public record PokemonWithSpriteRef(
    int dexNumber,
    String name,
    String variant,
    String spriteBgSize,
    String spritePosition,
    String imageUrl
) { }
