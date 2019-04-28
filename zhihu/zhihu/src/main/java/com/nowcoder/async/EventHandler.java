package com.nowcoder.async;

import java.util.List;

public interface EventHandler {
    void doHandler(EventModel eventModel);
    List<EventType> getSupportEventType();
}
