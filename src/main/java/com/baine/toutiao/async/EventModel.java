package com.baine.toutiao.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel {
    private EventType eventType;    // 事件类型
    private int actorID;            // 事件触发者
    private int entityType;         // 触发对象的描述
    private int entityId;
    private int entityOwnerId;      // 触发对象的拥有者
    private Map<String, String> exts = new HashMap<>(); // 触发现场信息

    public EventModel() {
    }

    public EventModel(EventType type) {
        this.eventType = type;
    }

    public String getExt(String key) {
        return exts.get(key);
    }

    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventModel setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public int getActorID() {
        return actorID;
    }

    public EventModel setActorID(int actorID) {
        this.actorID = actorID;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
