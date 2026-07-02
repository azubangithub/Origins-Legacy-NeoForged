package io.github.apace100.origins.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.origins.power.OriginsPowerTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class ScareCreepersMixin extends Monster {

    protected ScareCreepersMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void origins$scareCreepers(CallbackInfo ci) {
        // Use priority 1 and a slightly larger distance than cats to be safe (8.0F)
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, 8.0F, 1.0D, 1.2D, (player) -> {
            return player != null && PowerHolderComponent.KEY.get(player).hasPower(OriginsPowerTypes.SCARE_CREEPERS);
        }));
    }

    // Wrap the construction of the player target goal to add a predicate that excludes Felines
    @WrapOperation(method = "registerGoals", at = @At(value = "NEW", target = "(Lnet/minecraft/world/entity/Mob;Ljava/lang/Class;Z)Lnet/minecraft/world/entity/ai/goal/target/NearestAttackableTargetGoal;"))
    private NearestAttackableTargetGoal<?> origins$modifyTargetGoal(Mob mob, Class<?> targetClass, boolean mustSee, Operation<NearestAttackableTargetGoal<?>> original) {
        if (targetClass == Player.class) {
            return new NearestAttackableTargetGoal<>(mob, Player.class, 10, mustSee, false, (player) -> {
                return !PowerHolderComponent.KEY.get(player).hasPower(OriginsPowerTypes.SCARE_CREEPERS);
            });
        }
        return original.call(mob, targetClass, mustSee);
    }
}
