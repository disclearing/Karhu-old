package me.liwk.karhu.event;

import lombok.Getter;
import org.bukkit.entity.Entity;

@Getter
public class AttackEvent extends Event {

    private final int entityId;
    private final Entity entity;

    public AttackEvent(int entityId, Entity entity) {
        this.entityId = entityId;
        this.entity = entity;
    }

    public int getEntityId() {
        return entityId;
    }

    public Entity getEntity() {
        return entity;
    }

}
