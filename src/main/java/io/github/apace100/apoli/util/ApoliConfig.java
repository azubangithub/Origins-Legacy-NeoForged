package io.github.apace100.apoli.util;

import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionSet;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ApoliConfig {

    public static final ModConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON;

    static {
        final Pair<CommonConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    // Legacy compatibility fields — read from NeoForge config
    public ExecuteCommand executeCommand = new ExecuteCommand();

    public static class ExecuteCommand {
        public int permissionLevel = 2;
        public boolean showOutput = false;

        public PermissionSet getPermissionHandler() {
            return (permission) -> switch (this.permissionLevel) {
                case 0 -> LevelBasedPermissionSet.NO_PERMISSIONS.hasPermission(permission);
                case 1 -> LevelBasedPermissionSet.MODERATOR.hasPermission(permission);
                case 2 -> LevelBasedPermissionSet.GAMEMASTER.hasPermission(permission);
                case 3 -> LevelBasedPermissionSet.ADMIN.hasPermission(permission);
                case 4 -> LevelBasedPermissionSet.OWNER.hasPermission(permission);
                default -> false;
            };
        }
    }

    public static class CommonConfig {
        public final ModConfigSpec.IntValue executeCommandPermissionLevel;
        public final ModConfigSpec.BooleanValue executeCommandShowOutput;

        public CommonConfig(ModConfigSpec.Builder builder) {
            builder.push("executeCommand");
            executeCommandPermissionLevel = builder
                .comment("Permission level required to use /execute in origins powers")
                .defineInRange("permissionLevel", 2, 0, 4);
            executeCommandShowOutput = builder
                .comment("Whether execute command output is shown")
                .define("showOutput", false);
            builder.pop();
        }
    }

    /** Refresh POJO fields from the live config spec values */
    public void syncFromSpec() {
        if (COMMON_SPEC != null && COMMON_SPEC.isLoaded()) {
            executeCommand.permissionLevel = COMMON.executeCommandPermissionLevel.get();
            executeCommand.showOutput = COMMON.executeCommandShowOutput.get();
        }
    }
}
