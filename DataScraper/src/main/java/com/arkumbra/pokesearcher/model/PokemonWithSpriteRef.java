package com.arkumbra.pokesearcher.model;

public record PokemonWithSpriteRef(
    int dexNumber,
    String name,
    String variant,
    String spriteBgSize,
    String spritePosition,
    String imageUrl,
    String pokemonId,
    boolean restricted
) {
  public PokemonWithSpriteRef(
      int dexNumber,
      String name,
      String variant,
      String spriteBgSize,
      String spritePosition,
      String imageUrl
  ) {
    this(dexNumber, name, variant, spriteBgSize, spritePosition, imageUrl, null, false);
  }

  public boolean isRestricted() {
    return restricted;
  }

  public String formattedDexNumber() {
    if (dexNumber <= 0) return "";
    return String.format("#%04d", dexNumber);
  }

  public String displayName() {
    if (variant != null && !variant.isBlank()) {
      return name + " (" + variant + ")";
    }
    return name;
  }
}
