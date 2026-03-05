package de.davarava.extrahammers;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue IS_BLOCK_OUTLINE_BLACK = BUILDER
            .comment("Whether the outline color on a block is black when holding a hammer. If not than the color is white.")
            .define("hammer_block_outline_black", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
