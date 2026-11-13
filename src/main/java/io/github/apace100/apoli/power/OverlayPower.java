package io.github.apace100.apoli.power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class OverlayPower extends Power {

    private final ResourceLocation texture;
    private final float strength;
    private final float red;
    private final float green;
    private final float blue;
    private final DrawMode drawMode;
    private final DrawPhase drawPhase;
    private final boolean hideWithHud;
    private final boolean visibleInThirdPerson;

    public enum DrawMode {
        NAUSEA, TEXTURE
    }

    public enum DrawPhase {
        BELOW_HUD, ABOVE_HUD
    }

    public OverlayPower(PowerType<?> type, LivingEntity entity, ResourceLocation texture, float strength, float red, float green, float blue, DrawMode drawMode, DrawPhase drawPhase, boolean hideWithHud, boolean visibleInThirdPerson) {
        super(type, entity);
        this.texture = texture;
        this.strength = strength;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.drawMode = drawMode;
        this.drawPhase = drawPhase;
        this.hideWithHud = hideWithHud;
        this.visibleInThirdPerson = visibleInThirdPerson;
    }

    public DrawPhase getDrawPhase() {
        return drawPhase;
    }

    public boolean shouldBeVisibleInThirdPerson() {
        return visibleInThirdPerson;
    }

    public boolean doesHideWithHud() {
        return hideWithHud;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public float getStrength() {
        return strength;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public DrawMode getDrawMode() {
        return drawMode;
    }

    public static PowerFactory createFactory() {
        return new PowerFactory<>(Apoli.identifier("overlay"),
            new SerializableData()
                .add("texture", SerializableDataTypes.IDENTIFIER)
                .add("strength", SerializableDataTypes.FLOAT, 1.0F)
                .add("red", SerializableDataTypes.FLOAT, 1.0F)
                .add("green", SerializableDataTypes.FLOAT, 1.0F)
                .add("blue", SerializableDataTypes.FLOAT, 1.0F)
                .add("draw_mode", SerializableDataType.enumValue(OverlayPower.DrawMode.class))
                .add("draw_phase", SerializableDataType.enumValue(OverlayPower.DrawPhase.class))
                .add("hide_with_hud", SerializableDataTypes.BOOLEAN, true)
                .add("visible_in_third_person", SerializableDataTypes.BOOLEAN, false),
            data ->
                (type, player) -> new OverlayPower(type, player,
                    data.getId("texture"),
                    data.getFloat("strength"),
                    data.getFloat("red"),
                    data.getFloat("green"),
                    data.getFloat("blue"),
                    (OverlayPower.DrawMode) data.get("draw_mode"),
                    (OverlayPower.DrawPhase) data.get("draw_phase"),
                    data.getBoolean("hide_with_hud"),
                    data.getBoolean("visible_in_third_person")))
            .allowCondition();
    }
}