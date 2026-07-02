package io.github.apace100.apoli.registry;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.component.PowerHolderComponentImpl;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.modifier.IModifierOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.nbt.CompoundTag;
import java.util.function.Supplier;

@EventBusSubscriber(modid = io.github.apace100.origins.Origins.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ApoliRegistries {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Apoli.MODID);
    public static final DeferredRegister<net.minecraft.commands.synchronization.ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(net.minecraft.core.registries.Registries.COMMAND_ARGUMENT_TYPE, Apoli.MODID);
    public static final DeferredRegister<net.minecraft.world.item.crafting.RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(net.minecraft.core.registries.Registries.RECIPE_SERIALIZER, Apoli.MODID);

    public static final Supplier<net.minecraft.world.item.crafting.RecipeSerializer<?>> MODIFIED_CRAFTING_SERIALIZER = RECIPE_SERIALIZERS.register("modified",
        () -> io.github.apace100.apoli.util.ModifiedCraftingRecipe.SERIALIZER);
    public static final Supplier<net.minecraft.world.item.crafting.RecipeSerializer<?>> POWER_RESTRICTED_CRAFTING_SERIALIZER = RECIPE_SERIALIZERS.register("power_restricted",
        () -> io.github.apace100.apoli.util.PowerRestrictedCraftingRecipe.SERIALIZER);

    public static final Supplier<net.minecraft.commands.synchronization.ArgumentTypeInfo<?, ?>> POWER_ARG = ARGUMENT_TYPES.register("power", 
        () -> net.minecraft.commands.synchronization.ArgumentTypeInfos.registerByClass(io.github.apace100.apoli.command.PowerTypeArgumentType.class, 
            net.minecraft.commands.synchronization.SingletonArgumentInfo.contextFree(io.github.apace100.apoli.command.PowerTypeArgumentType::power)));
    public static final Supplier<net.minecraft.commands.synchronization.ArgumentTypeInfo<?, ?>> POWER_OPERATION_ARG = ARGUMENT_TYPES.register("power_operation", 
        () -> net.minecraft.commands.synchronization.ArgumentTypeInfos.registerByClass(io.github.apace100.apoli.command.PowerOperation.class,
            net.minecraft.commands.synchronization.SingletonArgumentInfo.contextFree(io.github.apace100.apoli.command.PowerOperation::operation)));

    public static final Supplier<AttachmentType<PowerHolderComponentImpl>> POWER_HOLDER = ATTACHMENT_TYPES.register("powers", () -> 
        AttachmentType.builder((holder) -> new PowerHolderComponentImpl((LivingEntity) holder))
            .serialize(new net.neoforged.neoforge.attachment.IAttachmentSerializer<net.minecraft.nbt.CompoundTag, PowerHolderComponentImpl>() {
                @Override
                public PowerHolderComponentImpl read(net.neoforged.neoforge.attachment.IAttachmentHolder holder, net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
                    PowerHolderComponentImpl component = new PowerHolderComponentImpl((LivingEntity) holder);
                    component.deserializeNBT(provider, tag);
                    return component;
                }
                @Override
                public net.minecraft.nbt.CompoundTag write(PowerHolderComponentImpl attachment, net.minecraft.core.HolderLookup.Provider provider) {
                    return attachment.serializeNBT(provider);
                }
            })
            .copyOnDeath()
            .copyHandler((attachment, holder, provider) -> {
                LivingEntity newEntity = (LivingEntity) holder;
                PowerHolderComponentImpl newComponent = new PowerHolderComponentImpl(newEntity);
                CompoundTag tag = attachment.serializeNBT(provider);
                newComponent.deserializeNBT(provider, tag);
                return newComponent;
            })
            .build()
    );

    public static final ResourceKey<Registry<PowerFactory>> POWER_FACTORY_KEY = ResourceKey.createRegistryKey(Apoli.identifier("power_factory"));
    public static final ResourceKey<Registry<ConditionFactory<Entity>>> ENTITY_CONDITION_KEY = ResourceKey.createRegistryKey(Apoli.identifier("entity_condition"));
    public static final ResourceKey<Registry<ConditionFactory<Tuple<Entity, Entity>>>> BIENTITY_CONDITION_KEY = ResourceKey.createRegistryKey(Apoli.identifier("bientity_condition"));
    public static final ResourceKey<Registry<ConditionFactory<ItemStack>>> ITEM_CONDITION_KEY = ResourceKey.createRegistryKey(Apoli.identifier("item_condition"));
    public static final ResourceKey<Registry<ConditionFactory<BlockInWorld>>> BLOCK_CONDITION_KEY = ResourceKey.createRegistryKey(Apoli.identifier("block_condition"));
    public static final ResourceKey<Registry<ConditionFactory<Tuple<DamageSource, Float>>>> DAMAGE_CONDITION_KEY = ResourceKey.createRegistryKey(Apoli.identifier("damage_condition"));
    public static final ResourceKey<Registry<ConditionFactory<FluidState>>> FLUID_CONDITION_KEY = ResourceKey.createRegistryKey(Apoli.identifier("fluid_condition"));
    public static final ResourceKey<Registry<ConditionFactory<Holder<Biome>>>> BIOME_CONDITION_KEY = ResourceKey.createRegistryKey(Apoli.identifier("biome_condition"));
    public static final ResourceKey<Registry<ActionFactory<Entity>>> ENTITY_ACTION_KEY = ResourceKey.createRegistryKey(Apoli.identifier("entity_action"));
    public static final ResourceKey<Registry<ActionFactory<Tuple<Level, ItemStack>>>> ITEM_ACTION_KEY = ResourceKey.createRegistryKey(Apoli.identifier("item_action"));
    public static final ResourceKey<Registry<ActionFactory<Triple<Level, BlockPos, Direction>>>> BLOCK_ACTION_KEY = ResourceKey.createRegistryKey(Apoli.identifier("block_action"));
    public static final ResourceKey<Registry<ActionFactory<Tuple<Entity, Entity>>>> BIENTITY_ACTION_KEY = ResourceKey.createRegistryKey(Apoli.identifier("bientity_action"));
    public static final ResourceKey<Registry<IModifierOperation>> MODIFIER_OPERATION_KEY = ResourceKey.createRegistryKey(Apoli.identifier("modifier_operation"));

    public static final Registry<PowerFactory> POWER_FACTORY = new net.minecraft.core.MappedRegistry<>(POWER_FACTORY_KEY, com.mojang.serialization.Lifecycle.stable(), false);
    public static final Registry<ConditionFactory<Entity>> ENTITY_CONDITION = new net.minecraft.core.MappedRegistry<>(ENTITY_CONDITION_KEY, com.mojang.serialization.Lifecycle.stable(), false);
    public static final Registry<ConditionFactory<Tuple<Entity, Entity>>> BIENTITY_CONDITION = new net.minecraft.core.MappedRegistry<>(BIENTITY_CONDITION_KEY, com.mojang.serialization.Lifecycle.stable(), false);
    public static final Registry<ConditionFactory<ItemStack>> ITEM_CONDITION = new net.minecraft.core.MappedRegistry<>(ITEM_CONDITION_KEY, com.mojang.serialization.Lifecycle.stable(), false);
    public static final Registry<ConditionFactory<BlockInWorld>> BLOCK_CONDITION = new net.minecraft.core.MappedRegistry<>(BLOCK_CONDITION_KEY, com.mojang.serialization.Lifecycle.stable(), false);
    public static final Registry<ConditionFactory<Tuple<DamageSource, Float>>> DAMAGE_CONDITION = new net.minecraft.core.MappedRegistry<>(DAMAGE_CONDITION_KEY, com.mojang.serialization.Lifecycle.stable(), false);
    public static final Registry<ConditionFactory<FluidState>> FLUID_CONDITION = new net.minecraft.core.MappedRegistry<>(FLUID_CONDITION_KEY, com.mojang.serialization.Lifecycle.stable(), false);
    public static final Registry<ConditionFactory<Holder<Biome>>> BIOME_CONDITION = new net.minecraft.core.MappedRegistry<>(BIOME_CONDITION_KEY, com.mojang.serialization.Lifecycle.stable(), false);
    public static final Registry<ActionFactory<Entity>> ENTITY_ACTION = new net.minecraft.core.MappedRegistry<>(ENTITY_ACTION_KEY, com.mojang.serialization.Lifecycle.stable(), false);
    public static final Registry<ActionFactory<Tuple<Level, ItemStack>>> ITEM_ACTION = new net.minecraft.core.MappedRegistry<>(ITEM_ACTION_KEY, com.mojang.serialization.Lifecycle.stable(), false);
    public static final Registry<ActionFactory<Triple<Level, BlockPos, Direction>>> BLOCK_ACTION = new net.minecraft.core.MappedRegistry<>(BLOCK_ACTION_KEY, com.mojang.serialization.Lifecycle.stable(), false);
    public static final Registry<ActionFactory<Tuple<Entity, Entity>>> BIENTITY_ACTION = new net.minecraft.core.MappedRegistry<>(BIENTITY_ACTION_KEY, com.mojang.serialization.Lifecycle.stable(), false);
    public static final Registry<IModifierOperation> MODIFIER_OPERATION = new net.minecraft.core.MappedRegistry<>(MODIFIER_OPERATION_KEY, com.mojang.serialization.Lifecycle.stable(), false);

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
event.register(        POWER_FACTORY);
event.register(        ENTITY_CONDITION);
event.register(        BIENTITY_CONDITION);
event.register(        ITEM_CONDITION);
event.register(        BLOCK_CONDITION);
event.register(        DAMAGE_CONDITION);
event.register(        FLUID_CONDITION);
event.register(        BIOME_CONDITION);
event.register(        ENTITY_ACTION);
event.register(        ITEM_ACTION);
event.register(        BLOCK_ACTION);
event.register(        BIENTITY_ACTION);
event.register(        MODIFIER_OPERATION);
    }

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
        ARGUMENT_TYPES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
        ApoliAttributes.ATTRIBUTES.register(modBus);
        PowerHolderComponent.KEY.setAttachmentSupplier(POWER_HOLDER);
    }
}


