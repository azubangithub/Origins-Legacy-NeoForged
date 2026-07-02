package io.github.apace100.apoli.registry;

import io.github.apace100.apoli.Apoli;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(modid = io.github.apace100.origins.Origins.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ApoliAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, Apoli.MODID);

    public static final net.neoforged.neoforge.registries.DeferredHolder<Attribute, Attribute> LAVA_SPEED = ATTRIBUTES.register("lava_speed", () -> new RangedAttribute("attribute.name.generic.apoli.lava_speed", 0.0D, 0.0D, 1024.0D).setSyncable(true));
    public static final net.neoforged.neoforge.registries.DeferredHolder<Attribute, Attribute> LAVA_VISIBILITY = ATTRIBUTES.register("lava_visibility", () -> new RangedAttribute("attribute.name.generic.apoli.lava_visibility", 0.0D, 0.0D, 1024.0D).setSyncable(true));
    public static final net.neoforged.neoforge.registries.DeferredHolder<Attribute, Attribute> WATER_SPEED = ATTRIBUTES.register("water_speed", () -> new RangedAttribute("attribute.name.generic.apoli.water_speed", 0.0D, 0.0D, 1024.0D).setSyncable(true));
    public static final net.neoforged.neoforge.registries.DeferredHolder<Attribute, Attribute> CLIMB_SPEED = ATTRIBUTES.register("climb_speed", () -> new RangedAttribute("attribute.name.generic.apoli.climb_speed", 0.0D, 0.0D, 1024.0D).setSyncable(true));

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent event) {
        for (EntityType<? extends net.minecraft.world.entity.LivingEntity> entityType : event.getTypes()) {
            if (event.has(entityType, net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)) {
                event.add(entityType, LAVA_SPEED);
                event.add(entityType, LAVA_VISIBILITY);
                event.add(entityType, WATER_SPEED);
                event.add(entityType, CLIMB_SPEED);
            }
        }
    }
}

