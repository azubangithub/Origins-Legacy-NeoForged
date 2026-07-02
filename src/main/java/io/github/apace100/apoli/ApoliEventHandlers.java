package io.github.apace100.apoli;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ActionOnWakeUp;
import io.github.apace100.apoli.power.ModifyBreakSpeedPower;
import io.github.apace100.apoli.power.ModifyHarvestPower;
import io.github.apace100.apoli.power.PreventSleepPower;
import io.github.apace100.apoli.util.SavedBlockPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;

@EventBusSubscriber(modid = io.github.apace100.origins.Origins.MODID)
public class ApoliEventHandlers {

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        float speed = event.getNewSpeed();
        java.util.Optional<BlockPos> posOpt = event.getPosition();
        if (posOpt.isPresent()) {
            BlockPos pos = posOpt.get();
            speed = PowerHolderComponent.modify(player, ModifyBreakSpeedPower.class, speed, p -> p.doesApply(player.level(), pos));
            event.setNewSpeed(speed);
        }
    }

    @SubscribeEvent
    public static void onLivingBreathe(LivingBreatheEvent event) {
        LivingEntity entity = event.getEntity();
        PowerHolderComponent.KEY.maybeGet(entity).ifPresent(phc -> {
            if (phc.hasPower(io.github.apace100.origins.power.OriginsPowerTypes.WATER_BREATHING)) {
                boolean isInWater = entity.isEyeInFluid(net.minecraft.tags.FluidTags.WATER);
                event.setCanBreathe(isInWater);
                if (isInWater) {
                    // Refill air when in water
                    event.setRefillAirAmount(4);
                } else {
                    // Drain air on land
                    event.setConsumeAirAmount(1);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        Player player = event.getEntity();
        net.minecraft.core.BlockPos pos = event.getPos();
        net.minecraft.world.level.block.state.BlockState state = event.getTargetBlock();
        
        for (ModifyHarvestPower mhp : PowerHolderComponent.getPowers(player, ModifyHarvestPower.class)) {
            net.minecraft.world.level.block.state.pattern.BlockInWorld biw = new net.minecraft.world.level.block.state.pattern.BlockInWorld(player.level(), pos, false);
            if (mhp.doesApply(biw)) {
                event.setCanHarvest(mhp.isHarvestAllowed());
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onSleepInBed(net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent event) {
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        
        List<PreventSleepPower> preventSleepPowers = PowerHolderComponent.getPowers(player, PreventSleepPower.class).stream()
                .filter(p -> {
                    boolean doesPrev = p.doesPrevent(player.level(), pos);
                    io.github.apace100.origins.Origins.LOGGER.info("CanPlayerSleepEvent: power " + p.getType().getIdentifier() + " doesPrevent=" + doesPrev + " at height " + pos.getY());
                    return doesPrev;
                })
                .toList();

        if(!preventSleepPowers.isEmpty()) {
            event.setProblem(Player.BedSleepingProblem.OTHER_PROBLEM);
            player.displayClientMessage(Component.translatable(preventSleepPowers.get(0).getMessage()), true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        net.minecraft.world.level.block.state.BlockState state = event.getLevel().getBlockState(pos);
        if (state.getBlock() instanceof net.minecraft.world.level.block.BedBlock) {
            List<PreventSleepPower> preventSleepPowers = PowerHolderComponent.getPowers(player, PreventSleepPower.class).stream()
                    .filter(p -> {
                        boolean doesPrev = p.doesPrevent(player.level(), pos);
                        io.github.apace100.origins.Origins.LOGGER.info("RightClickBlock: power " + p.getType().getIdentifier() + " doesPrevent=" + doesPrev + " at height " + pos.getY() + " sideClient=" + player.level().isClientSide);
                        return doesPrev;
                    })
                    .toList();

            if(!preventSleepPowers.isEmpty()) {
                event.setCanceled(true);
                event.setCancellationResult(net.minecraft.world.InteractionResult.FAIL);
                player.displayClientMessage(Component.translatable(preventSleepPowers.get(0).getMessage()), true);
            }
        }
    }

    @SubscribeEvent
    public static void onWakeUp(net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        if (event.updateLevel() || event.wakeImmediately()) return;

        player.getSleepingPos().ifPresent(pos -> {
            PowerHolderComponent.getPowers(player, ActionOnWakeUp.class).stream()
                    .filter(p -> p.doesApply(pos))
                    .forEach(p -> p.executeActions(pos, null));
        });
    }

    @SubscribeEvent
    public static void onSetSpawn(net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent event) {
        if (event.isForced()) return;

        Player player = event.getEntity();
        BlockPos pos = event.getNewSpawn();
        if (pos != null) {
            if (PowerHolderComponent.hasPower(player, PreventSleepPower.class, p -> p.doesPrevent(player.level(), pos))) {
                event.setCanceled(true);
            }
        }
    }
}
