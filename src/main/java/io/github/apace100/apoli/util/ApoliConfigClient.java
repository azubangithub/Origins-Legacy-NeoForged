package io.github.apace100.apoli.util;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ApoliConfigClient extends ApoliConfig {

    public static final ModConfigSpec CLIENT_SPEC;
    public static final ClientConfig CLIENT;

    static {
        final Pair<ClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    // Legacy compatibility fields
    public ResourcesAndCooldowns resourcesAndCooldowns = new ResourcesAndCooldowns();
    public Tooltips tooltips = new Tooltips();

    public static class ResourcesAndCooldowns {
        public int hudOffsetX = 0;
        public int hudOffsetY = 0;
    }

    public static class Tooltips {
        public boolean showUsabilityHints = true;
        public boolean compactUsabilityHints = false;
    }

    public static class ClientConfig {
        public final ModConfigSpec.IntValue hudOffsetX;
        public final ModConfigSpec.IntValue hudOffsetY;
        public final ModConfigSpec.BooleanValue showUsabilityHints;
        public final ModConfigSpec.BooleanValue compactUsabilityHints;

        public ClientConfig(ModConfigSpec.Builder builder) {
            builder.push("resourcesAndCooldowns");
            hudOffsetX = builder
                .comment("Horizontal offset for the power HUD resource bars")
                .defineInRange("hudOffsetX", 0, -1000, 1000);
            hudOffsetY = builder
                .comment("Vertical offset for the power HUD resource bars")
                .defineInRange("hudOffsetY", 0, -1000, 1000);
            builder.pop();

            builder.push("tooltips");
            showUsabilityHints = builder
                .comment("Show usability hints on item tooltips")
                .define("showUsabilityHints", true);
            compactUsabilityHints = builder
                .comment("Use compact format for usability hints")
                .define("compactUsabilityHints", false);
            builder.pop();
        }
    }

    /** Refresh POJO fields from the live config spec values */
    public void syncClientFromSpec() {
        syncFromSpec();
        resourcesAndCooldowns.hudOffsetX = CLIENT.hudOffsetX.get();
        resourcesAndCooldowns.hudOffsetY = CLIENT.hudOffsetY.get();
        tooltips.showUsabilityHints = CLIENT.showUsabilityHints.get();
        tooltips.compactUsabilityHints = CLIENT.compactUsabilityHints.get();
    }
}
