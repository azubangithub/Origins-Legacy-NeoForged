package io.github.apace100.apoli.power;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class PreventGameEventPower extends Power {

    private final TagKey<GameEvent> tag;
    private final HolderSet<GameEvent> list;
    private final Consumer<Entity> entityAction;

    public PreventGameEventPower(PowerType<?> type, LivingEntity entity, TagKey<GameEvent> tag, HolderSet<GameEvent> list, Consumer<Entity> entityAction) {
        super(type, entity);
        this.tag = tag;
        this.list = list;
        this.entityAction = entityAction;
    }

    public void executeAction(Entity entity) {
        if(entityAction != null) {
            entityAction.accept(entity);
        }
    }

    public boolean doesPrevent(Holder<GameEvent> event) {
        if(tag != null && event.is(tag)) {
            return true;
        }
        if(list != null) {
            if (list.contains(event)) {
                return true;
            }
            for (Holder<GameEvent> holder : list) {
                if (holder.value() == event.value()) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static PowerFactory createFactory() {
        return new PowerFactory<>(Apoli.identifier("prevent_game_event"),
            new SerializableData()
                .add("event", SerializableDataTypes.GAME_EVENT, null)
                .add("events", SerializableDataTypes.GAME_EVENTS, null)
                .add("tag", SerializableDataTypes.GAME_EVENT_TAG, null)
                .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null),
            data ->
                (type, player) -> {
                    List<Holder<GameEvent>> eventList = new LinkedList<>();
                    if(data.isPresent("events")) {
                        Object rawEvents = data.get("events");
                        if(rawEvents instanceof List<?> l) {
                            for(Object elem : l) {
                                if(elem instanceof Holder<?> h) {
                                    eventList.add((Holder<GameEvent>) h);
                                } else if(elem instanceof GameEvent ge) {
                                    eventList.add(BuiltInRegistries.GAME_EVENT.wrapAsHolder(ge));
                                }
                            }
                        }
                    }

                    if(data.isPresent("event")) {
                        Object rawEvent = data.get("event");
                        if(rawEvent instanceof Holder<?> h) {
                            eventList.add((Holder<GameEvent>) h);
                        } else if(rawEvent instanceof GameEvent ge) {
                            eventList.add(BuiltInRegistries.GAME_EVENT.wrapAsHolder(ge));
                        }
                    }

                    HolderSet<GameEvent> holderSet = eventList.isEmpty() ? null : HolderSet.direct(eventList);
                    return new PreventGameEventPower(type, player,
                        data.get("tag"), holderSet,
                        data.get("entity_action"));
                })
            .allowCondition();
    }
}
