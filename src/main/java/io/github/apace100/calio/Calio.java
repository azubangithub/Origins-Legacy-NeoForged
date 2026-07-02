package io.github.apace100.calio;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

/**
 * Calio library - ported to NeoForge.
 * No longer a separate mod - inlined into Origins.
 * Registration is done via DeferredRegister from Origins' main class.
 */
public class Calio {
	public static final String MODID = "calio";

	// These will be registered via DeferredRegister in the Origins mod class
	// For now, keep the static references that other code uses
	public static DataComponentType<Boolean> NON_ITALIC_NAME;
	public static DataComponentType<Boolean> HAS_ADDITIONAL_ATTRIBUTES;

	/**
	 * Called from Origins mod constructor to initialize Calio subsystem.
	 */
	public static void init() {
		// Registration is handled by DeferredRegister in CalioRegistries
	}

	public static boolean hasNonItalicName(ItemStack stack) {
		return NON_ITALIC_NAME != null && stack.getOrDefault(NON_ITALIC_NAME, false);
	}

	public static void setNameNonItalic(ItemStack stack) {
		if(stack != null && NON_ITALIC_NAME != null)
			stack.set(NON_ITALIC_NAME, true);
	}

	public static boolean areEntityAttributesAdditional(ItemStack stack) {
		return HAS_ADDITIONAL_ATTRIBUTES != null && stack.getOrDefault(HAS_ADDITIONAL_ATTRIBUTES, false);
	}

	/**
	 * Sets whether the item stack counts the entity attribute modifiers specified in its tag as additional,
	 * meaning they won't overwrite the equipment's inherent modifiers.
	 * @param stack
	 * @param additional
	 */
	public static void setEntityAttributesAdditional(ItemStack stack, boolean additional) {
		if(stack != null && HAS_ADDITIONAL_ATTRIBUTES != null) {
			if(additional) {
				stack.set(HAS_ADDITIONAL_ATTRIBUTES, true);
			} else {
				stack.remove(HAS_ADDITIONAL_ATTRIBUTES);
			}
		}
	}

	public static <T> boolean areTagsEqual(ResourceKey<? extends Registry<T>> registryKey, TagKey<T> tag1, TagKey<T> tag2) {
		return areTagsEqual(tag1, tag2);
	}

	public static <T> boolean areTagsEqual(TagKey<T> tag1, TagKey<T> tag2) {
		if(tag1 == tag2) {
			return true;
		}
		if(tag1 == null || tag2 == null) {
			return false;
		}
		if(!tag1.registry().equals(tag2.registry())) {
			return false;
		}
		if(!tag1.location().equals(tag2.location())) {
			return false;
		}
		return true;
	}
}
