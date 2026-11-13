package io.github.apace100.apoli.power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class PlayerAbilityPower extends Power {

    public PlayerAbilityPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
        if(entity instanceof Player) {
            this.setTicking(true);
        }
    }

    @Override
    public void tick() {
        if(entity instanceof Player player) {
            boolean isActive = isActive();
            if(isActive && !player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
        }
    }

    @Override
    public void onLost() {
        if(entity instanceof Player player) {
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }
    }

    public static PowerFactory createAbilityFactory(ResourceLocation identifier) {
        return new PowerFactory<>(identifier,
            new SerializableData(),
            data ->
                (type, player) -> new PlayerAbilityPower(type, player))
            .allowCondition();
    }
}
