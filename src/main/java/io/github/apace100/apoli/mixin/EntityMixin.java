package io.github.apace100.apoli.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.apace100.apoli.access.MovingEntity;
import io.github.apace100.apoli.access.SubmergableEntity;
import io.github.apace100.apoli.access.WaterMovingEntity;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.*;
import io.github.apace100.calio.Calio;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin implements MovingEntity, SubmergableEntity {

    @Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
    private void makeFullyFireImmune(CallbackInfoReturnable<Boolean> cir) {
        if(PowerHolderComponent.hasPower((Entity)(Object)this, FireImmunityPower.class)) {
            cir.setReturnValue(true);
        }
    }

    @Shadow
    public Level level;

    @Shadow
    public abstract double getFluidHeight(TagKey<Fluid> fluid);

    @Shadow
    public float moveDist;

    @Shadow
    protected boolean onGround;

    @Shadow @Nullable protected Set<TagKey<Fluid>> fluidOnEyes;

    @Shadow protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;

    @Shadow protected Object2DoubleMap<net.neoforged.neoforge.fluids.FluidType> forgeFluidTypeHeight;

    @Shadow private net.neoforged.neoforge.fluids.FluidType forgeFluidTypeOnEyes;

    @Shadow public abstract boolean isSwimming();
    @Shadow public abstract void resetFallDistance();
    @Shadow public abstract BlockPos blockPosition();

    @Inject(method = "updateInWaterStateAndDoWaterCurrentPushing", at = @At("HEAD"))
    private void makeEntitiesIgnoreWaterResetFall(CallbackInfo ci) {
        if (PowerHolderComponent.hasPower((Entity)(Object)this, IgnoreWaterPower.class)) {
            if (this.level.getFluidState(this.blockPosition()).is(FluidTags.WATER) || this.level.getFluidState(this.blockPosition().above()).is(FluidTags.WATER)) {
                this.resetFallDistance();
            }
        }
    }

    @Inject(method = "isInWater", at = @At("HEAD"), cancellable = true)
    private void makeEntitiesIgnoreWater(CallbackInfoReturnable<Boolean> cir) {
        if(PowerHolderComponent.hasPower((Entity)(Object)this, IgnoreWaterPower.class)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isUnderWater", at = @At("HEAD"), cancellable = true)
    private void makeEntitiesIgnoreWaterUnder(CallbackInfoReturnable<Boolean> cir) {
        if(PowerHolderComponent.hasPower((Entity)(Object)this, IgnoreWaterPower.class)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canStartSwimming", at = @At("HEAD"), cancellable = true)
    private void makeEntitiesIgnoreWaterCanStartSwimming(CallbackInfoReturnable<Boolean> cir) {
        if(PowerHolderComponent.hasPower((Entity)(Object)this, IgnoreWaterPower.class)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canSwimInFluidType", at = @At("HEAD"), cancellable = true)
    private void makeEntitiesIgnoreWaterCanSwimInFluidType(net.neoforged.neoforge.fluids.FluidType type, CallbackInfoReturnable<Boolean> cir) {
        if(PowerHolderComponent.hasPower((Entity)(Object)this, IgnoreWaterPower.class)) {
            if (type != net.neoforged.neoforge.common.NeoForgeMod.LAVA_TYPE.value()) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "isPushedByFluid", at = @At("HEAD"), cancellable = true)
    private void makeEntitiesIgnoreWaterPushedByFluid(CallbackInfoReturnable<Boolean> cir) {
        if (IgnoreWaterPower.shouldIgnoreFluidPush((Entity)(Object)this)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getFluidTypeHeight", at = @At("HEAD"), cancellable = true)
    private void makeEntitiesIgnoreWaterFluidTypeHeight(net.neoforged.neoforge.fluids.FluidType type, CallbackInfoReturnable<Double> cir) {
        if(PowerHolderComponent.hasPower((Entity)(Object)this, IgnoreWaterPower.class)) {
            if (type != net.neoforged.neoforge.common.NeoForgeMod.LAVA_TYPE.value()) {
                cir.setReturnValue(0.0D);
            }
        }
    }

    @Inject(method = "getMaxHeightFluidType", at = @At("HEAD"), cancellable = true)
    private void makeEntitiesIgnoreWaterMaxHeightFluidType(CallbackInfoReturnable<net.neoforged.neoforge.fluids.FluidType> cir) {
        if(PowerHolderComponent.hasPower((Entity)(Object)this, IgnoreWaterPower.class) && !((Entity)(Object)this).isInLava()) {
            cir.setReturnValue(net.neoforged.neoforge.common.NeoForgeMod.EMPTY_TYPE.value());
        }
    }

    @Inject(method = "getEyeInFluidType", at = @At("HEAD"), cancellable = true)
    private void makeEntitiesIgnoreWaterEyeInFluidType(CallbackInfoReturnable<net.neoforged.neoforge.fluids.FluidType> cir) {
        if(PowerHolderComponent.hasPower((Entity)(Object)this, IgnoreWaterPower.class) && !((Entity)(Object)this).isInLava()) {
            cir.setReturnValue(net.neoforged.neoforge.common.NeoForgeMod.EMPTY_TYPE.value());
        }
    }

    @Inject(method = "isInFluidType()Z", at = @At("HEAD"), cancellable = true)
    private void makeEntitiesIgnoreWaterInFluidType(CallbackInfoReturnable<Boolean> cir) {
        if (PowerHolderComponent.hasPower((Entity)(Object)this, IgnoreWaterPower.class)) {
            cir.setReturnValue(this.forgeFluidTypeHeight.object2DoubleEntrySet().stream()
                .anyMatch(IgnoreWaterPower::isNonIgnoredFluidEntry));
        }
    }

    @Inject(method = "isInFluidType(Ljava/util/function/BiPredicate;Z)Z", at = @At("HEAD"), cancellable = true)
    private void makeEntitiesIgnoreWaterInFluidTypePredicate(java.util.function.BiPredicate<net.neoforged.neoforge.fluids.FluidType, Double> predicate, boolean forAllTypes, CallbackInfoReturnable<Boolean> cir) {
        if (PowerHolderComponent.hasPower((Entity)(Object)this, IgnoreWaterPower.class)) {
            var nonIgnored = this.forgeFluidTypeHeight.object2DoubleEntrySet().stream()
                .filter(IgnoreWaterPower::isNonIgnoredFluidEntry)
                .toList();
            if (nonIgnored.isEmpty()) {
                cir.setReturnValue(false);
            } else {
                cir.setReturnValue(forAllTypes
                    ? nonIgnored.stream().allMatch(e -> predicate.test(e.getKey(), e.getDoubleValue()))
                    : nonIgnored.stream().anyMatch(e -> predicate.test(e.getKey(), e.getDoubleValue())));
            }
        }
    }

    @WrapOperation(method = "updateFluidHeightAndDoFluidPushing()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPushedByFluid(Lnet/neoforged/neoforge/fluids/FluidType;)Z"))
    private boolean apoli$ignoreWaterFluidPush(Entity instance, net.neoforged.neoforge.fluids.FluidType type, Operation<Boolean> original) {
        if (type != net.neoforged.neoforge.common.NeoForgeMod.LAVA_TYPE.value() && PowerHolderComponent.hasPower(instance, IgnoreWaterPower.class)) {
            return false;
        }
        net.neoforged.neoforge.fluids.FluidType previous = io.github.apace100.apoli.util.ApoliSharedMixinValues.CURRENT_PUSHING_FLUID.get();
        io.github.apace100.apoli.util.ApoliSharedMixinValues.CURRENT_PUSHING_FLUID.set(type);
        try {
            return original.call(instance, type);
        } finally {
            if (previous == null) {
                io.github.apace100.apoli.util.ApoliSharedMixinValues.CURRENT_PUSHING_FLUID.remove();
            } else {
                io.github.apace100.apoli.util.ApoliSharedMixinValues.CURRENT_PUSHING_FLUID.set(previous);
            }
        }
    }

    @Inject(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;fallOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;F)V"))
    private void invokeActionOnLand(CallbackInfo ci) {
        List<ActionOnLandPower> powers = PowerHolderComponent.getPowers((Entity)(Object)this, ActionOnLandPower.class);
        powers.forEach(ActionOnLandPower::executeAction);
    }

    @Inject(at = @At("HEAD"), method = "isInvulnerableToBase", cancellable = true)
    private void makeOriginInvulnerable(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if((Object)this instanceof LivingEntity) {
            PowerHolderComponent component = PowerHolderComponent.KEY.get((Entity) (Object) this);
            if(component.getPowers(InvulnerablePower.class).stream().anyMatch(inv -> inv.doesApply(damageSource))) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "clearFire", at = @At("HEAD"), cancellable = true)
    private void preventExtinguishingFromSwimming(CallbackInfo ci) {
        if(PowerHolderComponent.hasPower((Entity) (Object) this, SwimmingPower.class) && this.isSwimming() && !(getFluidHeight(FluidTags.WATER) > 0)) {
            ci.cancel();
        }
    }

    @WrapWithCondition(method = "applyEffectsFromBlocks(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setRemainingFireTicks(I)V"))
    private boolean preventExtinguishingFromSwimming(Entity instance, int remainingFireTicks) {
        return !(PowerHolderComponent.hasPower(instance, SwimmingPower.class) && instance.isSwimming() && !(getFluidHeight(FluidTags.WATER) > 0));
    }

    @Inject(at = @At("HEAD"), method = "isInvisible", cancellable = true)
    private void phantomInvisibility(CallbackInfoReturnable<Boolean> info) {
        if(PowerHolderComponent.hasPower((Entity)(Object)this, InvisibilityPower.class)) {
            info.setReturnValue(true);
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"), method = "moveTowardsClosestSpace", cancellable = true)
    protected void pushOutOfBlocks(double x, double y, double z, CallbackInfo info) {
        List<PhasingPower> powers = PowerHolderComponent.getPowers((Entity)(Object)this, PhasingPower.class);
        if(powers.size() > 0) {
            if(powers.stream().anyMatch(phasingPower -> phasingPower.doesApply(BlockPos.containing(x, y, z)))) {
                info.cancel();
            }
        }
    }

    @Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
    private void apoli$preventPhasingSuffocation(CallbackInfoReturnable<Boolean> cir) {
        List<PhasingPower> powers = PowerHolderComponent.getPowers((Entity)(Object)this, PhasingPower.class);
        if(powers.size() > 0) {
            BlockPos eyePos = BlockPos.containing(((Entity)(Object)this).getEyePosition());
            if(powers.stream().anyMatch(p -> p.doesApply(eyePos))) {
                cir.setReturnValue(false);
            }
        }
    }



    private boolean isMoving;
    private float distanceBefore;

    @Inject(method = "move", at = @At("HEAD"))
    private void saveDistanceTraveled(MoverType type, Vec3 movement, CallbackInfo ci) {
        this.isMoving = false;
        this.distanceBefore = this.moveDist;
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
    private void checkIsMoving(MoverType type, Vec3 movement, CallbackInfo ci) {
        if (this.moveDist > this.distanceBefore) {
            this.isMoving = true;
        }
    }

    @ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true)
    private Vec3 modifyMovementVelocity(Vec3 original, MoverType movementType) {
        if(movementType != MoverType.SELF) {
            return original;
        }
        Vec3 modified = new Vec3(
            PowerHolderComponent.modify((Entity)(Object)this, ModifyVelocityPower.class, original.x, p -> p.axes.contains(Direction.Axis.X), null),
            PowerHolderComponent.modify((Entity)(Object)this, ModifyVelocityPower.class, original.y, p -> p.axes.contains(Direction.Axis.Y), null),
            PowerHolderComponent.modify((Entity)(Object)this, ModifyVelocityPower.class, original.z, p -> p.axes.contains(Direction.Axis.Z), null)
        );
        return modified;
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getOnPosLegacy()Lnet/minecraft/core/BlockPos;"))
    private void forceGrounded(MoverType movementType, Vec3 movement, CallbackInfo ci) {
        if(PowerHolderComponent.hasPower((Entity)(Object)this, GroundedPower.class)) {
            this.onGround = true;
        }
    }
    
    @Inject(method = "makeStuckInBlock", at = @At("HEAD"), cancellable = true)
    private void preventCobwebSlowdown(BlockState state, Vec3 multiplier, CallbackInfo ci) {
        if (PowerHolderComponent.KEY.maybeGet((Entity)(Object)this).map(phc ->
            phc.hasPower(io.github.apace100.origins.power.OriginsPowerTypes.NO_COBWEB_SLOWDOWN)
            || phc.hasPower(io.github.apace100.origins.power.OriginsPowerTypes.MASTER_OF_WEBS_NO_SLOWDOWN)
        ).orElse(false)) {
            ci.cancel();
        }
    }

    @Override
    public boolean isSubmergedInLoosely(TagKey<Fluid> tag) {
        if(tag == null) {
            return false;
        }
        Entity entity = (Entity)(Object)this;
        if(io.github.apace100.calio.Calio.areTagsEqual(net.minecraft.core.registries.Registries.FLUID, net.minecraft.tags.FluidTags.WATER, tag) || tag.location().equals(net.minecraft.tags.FluidTags.WATER.location())) {
            return this.forgeFluidTypeOnEyes == net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE.value() || entity.isEyeInFluid(net.minecraft.tags.FluidTags.WATER);
        } else if (io.github.apace100.calio.Calio.areTagsEqual(net.minecraft.core.registries.Registries.FLUID, net.minecraft.tags.FluidTags.LAVA, tag) || tag.location().equals(net.minecraft.tags.FluidTags.LAVA.location())) {
            return this.forgeFluidTypeOnEyes == net.neoforged.neoforge.common.NeoForgeMod.LAVA_TYPE.value() || entity.isEyeInFluid(net.minecraft.tags.FluidTags.LAVA);
        }
        if (entity.isEyeInFluid(tag)) {
            return true;
        }
        return this.forgeFluidTypeOnEyes != net.neoforged.neoforge.common.NeoForgeMod.EMPTY_TYPE.value()
            && entity.level().getFluidState(BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ())).is(tag);
    }

    @Override
    public double getFluidHeightLoosely(TagKey<Fluid> tag) {
        if(tag == null) {
            return 0;
        }
        Entity entity = (Entity)(Object)this;
        if(io.github.apace100.calio.Calio.areTagsEqual(net.minecraft.core.registries.Registries.FLUID, net.minecraft.tags.FluidTags.WATER, tag) || tag.location().equals(net.minecraft.tags.FluidTags.WATER.location())) {
            return Math.max(entity.getFluidHeight(net.minecraft.tags.FluidTags.WATER), this.forgeFluidTypeHeight.getDouble(net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE.value()));
        } else if (io.github.apace100.calio.Calio.areTagsEqual(net.minecraft.core.registries.Registries.FLUID, net.minecraft.tags.FluidTags.LAVA, tag) || tag.location().equals(net.minecraft.tags.FluidTags.LAVA.location())) {
            return Math.max(entity.getFluidHeight(net.minecraft.tags.FluidTags.LAVA), this.forgeFluidTypeHeight.getDouble(net.neoforged.neoforge.common.NeoForgeMod.LAVA_TYPE.value()));
        }
        return entity.getFluidHeight(tag);
    }

    /*
    @com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockCollisions(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/lang/Iterable;"))
    private Iterable<VoxelShape> apoli$filterCollisions(Level instance, Entity entity, net.minecraft.world.phys.AABB aabb, com.llamalad7.mixinextras.injector.wrapoperation.Operation<Iterable<VoxelShape>> original) {
        if (!(entity instanceof LivingEntity living)) return original.call(instance, entity, aabb);
        List<PhasingPower> powers = PowerHolderComponent.getPowers(living, PhasingPower.class);
        if (powers.isEmpty()) return original.call(instance, entity, aabb);
        
        boolean isShifting = powers.stream().anyMatch(p -> p.shouldPhaseDown(living));
        boolean isInsideBlock = !instance.noCollision(living, living.getBoundingBox().deflate(0.001));
        
        if (isShifting || isInsideBlock) {
             return java.util.Collections.emptyList();
        }
        
        return original.call(instance, entity, aabb);
    }
    */

    @Override
    public boolean isMoving() {
        return isMoving;
    }
}

