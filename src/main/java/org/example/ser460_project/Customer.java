package org.example.ser460_project;

import java.util.Map;

public class Customer implements Subscriber {
    private String subscriberName;
    private String cuisineTypeInterest;
    private MessageBroker broker;

    public Customer(String subscriberName, String cuisineTypeInterest, MessageBroker broker) {
        this.subscriberName = subscriberName;
        this.cuisineTypeInterest = cuisineTypeInterest;
        this.broker = broker;
    }

    public void subscribe(String mealPlanType) {
        broker.register(mealPlanType + ":" + cuisineTypeInterest, this);
        broker.log("subscribe, " + subscriberName + ", " + mealPlanType);
    }

    public void unsubscribe(String mealPlanType) {
        broker.deregister(mealPlanType + ":" + cuisineTypeInterest, this);
        broker.log("unsubscribe, " + subscriberName + ", " + mealPlanType);
    }

    @Override
    public void update(String message) {
        broker.log("Customer " + subscriberName + " received notification: " + message);
    }
}
