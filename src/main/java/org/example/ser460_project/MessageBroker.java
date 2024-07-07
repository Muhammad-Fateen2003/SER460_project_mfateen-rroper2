package org.example.ser460_project;
import javafx.scene.control.TextArea;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class MessageBroker {
    private static final MessageBroker instance = new MessageBroker();
    private final Object mutex = new Object();
    private final Map<String, Set<Subscriber>> subscribersMap = new HashMap<>();
    private TextArea logTextArea;

    private MessageBroker() {}

    public static MessageBroker getInstance() {
        return instance;
    }

    public void setLogTextArea(TextArea logTextArea) {
        this.logTextArea = logTextArea;
    }

    public boolean register(String topic, Subscriber subscriber) {
        synchronized (mutex) {
            return subscribersMap.computeIfAbsent(topic, k -> new HashSet<>()).add(subscriber);
        }
    }

    public boolean deregister(String topic, Subscriber subscriber) {
        synchronized (mutex) {
            Set<Subscriber> subscribers = subscribersMap.get(topic);
            if (subscribers != null) {
                boolean removed = subscribers.remove(subscriber);
//                if (subscribers.isEmpty()) {
                    subscribersMap.remove(topic);
//                }
                return removed;
            }
            return false;
        }
    }

    public void sendMessage(String mealPlanType, String cuisineType, String message) {
        synchronized (mutex) {
            String topic = mealPlanType + ":" + cuisineType;
            Set<Subscriber> subscribers = subscribersMap.get(topic);
            if (subscribers != null) {
                subscribers.forEach(subscriber -> subscriber.update(message));
            }
        }
    }

    public void log(String message) {
        if (logTextArea != null) {
            logTextArea.appendText(message + "\n");
        }
    }
}