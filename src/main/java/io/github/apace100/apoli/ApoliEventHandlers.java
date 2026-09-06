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
        if (PowerHolderComponent.hasPower(entity, io.github.apace100.apoli.power.IgnoreWaterPower.class)) {
            event.setCanBreathe(true);
            event.setRefillAirAmount(4);
            return;
        }
        if (io.github.apace100.origins.power.OriginsPowerTypes.WATER_BREATHING.isActive(entity)) {
            boolean isInWater = entity.isEyeInFluid(net.minecraft.tags.FluidTags.WATER);
            boolean hasWaterBreathingEffect = entity.hasEffect(net.minecraft.world.effect.MobEffects.WATER_BREATHING)
                    || entity.hasEffect(net.minecraft.world.effect.MobEffects.CONDUIT_POWER);
            boolean isInRain = ((io.github.apace100.apoli.mixin.EntityAccessor) entity).callIsInRain();

            if (isInWater || hasWaterBreathingEffect) {
                event.setCanBreathe(true);
                event.setRefillAirAmount(4);
            } else if (isInRain) {
                // In rain, Merlings do not suffocate, but do not refill air
                event.setCanBreathe(true);
                event.setRefillAirAmount(0);
            } else {
                // On land: cannot breathe, consume 1 air per tick
                event.setCanBreathe(false);
                event.setConsumeAirAmount(1);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDrown(net.neoforged.neoforge.event.entity.living.LivingDrownEvent event) {
        LivingEntity entity = event.getEntity();
        if (PowerHolderComponent.hasPower(entity, io.github.apace100.apoli.power.IgnoreWaterPower.class)) {
            event.setCanceled(true);
            event.setDrowning(false);
            return;
        }
        if (io.github.apace100.origins.power.OriginsPowerTypes.WATER_BREATHING.isActive(entity) && !entity.isEyeInFluid(net.minecraft.tags.FluidTags.WATER)) {
            event.setCanceled(true);
            if (event.isDrowning()) {
                entity.setAirSupply(0);

                net.minecraft.world.phys.Vec3 vec3 = entity.getDeltaMovement();
                for (int i = 0; i < event.getBubbleCount(); ++i) {
                    double d2 = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                    double d3 = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                    double d4 = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                    entity.level().addParticle(net.minecraft.core.particles.ParticleTypes.BUBBLE, entity.getX() + d2, entity.getY() + d3, entity.getZ() + d4, vec3.x, vec3.y, vec3.z);
                }

                if (event.getDamageAmount() > 0 && !entity.level().isClientSide) {
                    entity.hurt(io.github.apace100.origins.registry.ModDamageSources.getSource(entity.level().registryAccess(), io.github.apace100.origins.registry.ModDamageSources.NO_WATER_FOR_GILLS), event.getDamageAmount());
                }
            }
        }
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
