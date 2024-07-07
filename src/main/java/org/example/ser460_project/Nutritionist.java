package org.example.ser460_project;

import java.util.HashSet;
import java.util.Set;

public class Nutritionist {
    private String publisherName;
    private MessageBroker broker;
    private static Set<Nutritionist> allNutritionists = new HashSet<>();
    private Set<Meal> publishedMeals = new HashSet<>();

    public Nutritionist(String publisherName, MessageBroker broker) {
        this.publisherName = publisherName;
        this.broker = broker;
        allNutritionists.add(this);
    }

    public static Nutritionist getByUsername(String publisherName) {
        return allNutritionists.stream()
                .filter(n -> n.publisherName.equals(publisherName))
                .findFirst()
                .orElse(null);
    }

    public void publish(String mealPlanType, String cuisineType, String mealName, int cookTime, String timeOfMeal, String dayOfWeek) {
        Meal meal = new Meal(cuisineType, mealName, timeOfMeal, dayOfWeek);
        publishedMeals.add(meal);

        StringBuilder publishMessage = new StringBuilder("publish, " + publisherName + ", " + mealPlanType + ", " + mealName + ", " + cuisineType + ", " + cookTime + "minutes");
        if (timeOfMeal != null && !timeOfMeal.isEmpty()) {
            publishMessage.append(", ").append(timeOfMeal);
        }
        if (dayOfWeek != null && !dayOfWeek.isEmpty()) {
            publishMessage.append(", ").append(dayOfWeek);
        }
        System.out.println(publishMessage.toString());
        broker.log(publishMessage.toString());

        broker.sendMessage(mealPlanType, cuisineType, publishMessage.toString());
    }

    public boolean hasPublished(String cuisineType, String mealName, String timeOfMeal, String dayOfWeek) {
        Meal meal = new Meal(cuisineType, mealName, timeOfMeal, dayOfWeek);
        return publishedMeals.contains(meal);
    }

    private static class Meal {
        private String cuisineType;
        private String mealName;
        private String timeOfMeal;
        private String dayOfWeek;

        public Meal(String cuisineType, String mealName, String timeOfMeal, String dayOfWeek) {
            this.cuisineType = cuisineType;
            this.mealName = mealName;
            this.timeOfMeal = timeOfMeal;
            this.dayOfWeek = dayOfWeek;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Meal meal = (Meal) o;

            if (!cuisineType.equals(meal.cuisineType)) return false;
            if (!mealName.equals(meal.mealName)) return false;
            if (timeOfMeal != null ? !timeOfMeal.equals(meal.timeOfMeal) : meal.timeOfMeal != null) return false;
            return dayOfWeek != null ? dayOfWeek.equals(meal.dayOfWeek) : meal.dayOfWeek == null;
        }

        @Override
        public int hashCode() {
            int result = cuisineType.hashCode();
            result = 31 * result + mealName.hashCode();
            result = timeOfMeal != null ? 31 * result + timeOfMeal.hashCode() : 0;
            result = dayOfWeek != null ? 31 * result + dayOfWeek.hashCode() : 0;
            return result;
        }
    }
}