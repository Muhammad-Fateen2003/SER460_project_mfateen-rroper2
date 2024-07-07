package org.example.ser460_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashSet;
import java.util.Set;

public class Controller {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField cuisineTypeField;
    @FXML
    private TextField mealNameField;
    @FXML
    private ComboBox<String> timeOfMealField;
    @FXML
    private ComboBox<String> dayOfMealField;
    @FXML
    private Spinner<Integer> cookTimeSpinner;
    @FXML
    private TextField subscriberUsernameField;
    @FXML
    private TextField cuisineTypeInterestField;
    @FXML
    private CheckBox dailyField;
    @FXML
    private CheckBox weeklyField;
    @FXML
    private Button subscribeButton;
    @FXML
    private TextArea logTextArea;

    private Set<String> publisherUsernames = new HashSet<>();
    private Set<String> subscriberUsernames = new HashSet<>();
    private MessageBroker broker = MessageBroker.getInstance();

    @FXML
    protected void initialize() {
        timeOfMealField.getItems().addAll("breakfast", "lunch", "dinner");
        dayOfMealField.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1440, 0);
        cookTimeSpinner.setValueFactory(valueFactory);
        cookTimeSpinner.setEditable(true);
        broker.setLogTextArea(logTextArea);  // Set the TextArea for logging in the broker
    }

    @FXML
    protected void publishButtonAction(ActionEvent e) {
        String publisherName = usernameField.getText().trim();
        String cuisineType = cuisineTypeField.getText().trim();
        String mealName = mealNameField.getText().trim();
        String timeOfMeal = timeOfMealField.getValue();
        String dayOfWeek = dayOfMealField.getValue();
        Integer cookTime = cookTimeSpinner.getValue();

        if (publisherName.isEmpty() || cuisineType.isEmpty() || mealName.isEmpty() || cookTime == null || cookTime <= 0) {
            showAlert("Username, Cuisine Type, Meal Name, and Cook Time must be filled out");
            return;
        }

        Nutritionist nutritionist;
        if (publisherUsernames.contains(publisherName)) {
            nutritionist = Nutritionist.getByUsername(publisherName);
            if (nutritionist.hasPublished(cuisineType, mealName, timeOfMeal, dayOfWeek)) {
                showAlert("This meal plan has already been published by the same username");
                return;
            }
        } else {
            nutritionist = new Nutritionist(publisherName, broker);
            publisherUsernames.add(publisherName);
        }

        String mealPlanType = "mealIdea";
        if (timeOfMeal != null && !timeOfMeal.isEmpty()) {
            mealPlanType = "dailyMeal";
        }
        if (dayOfWeek != null && !dayOfWeek.isEmpty()) {
            mealPlanType = "weeklyMeal";
        }

        nutritionist.publish(mealPlanType, cuisineType, mealName, cookTime, timeOfMeal, dayOfWeek);

        // Clear fields
        usernameField.clear();
        cuisineTypeField.clear();
        mealNameField.clear();
        timeOfMealField.getSelectionModel().clearSelection();
        dayOfMealField.getSelectionModel().clearSelection();
        cookTimeSpinner.getValueFactory().setValue(0);
    }

    @FXML
    protected void subscribeButtonAction(ActionEvent e) {
        String subscriberName = subscriberUsernameField.getText().trim();
        String cuisineTypeInterest = cuisineTypeInterestField.getText().trim();
        boolean daily = dailyField.isSelected();
        boolean weekly = weeklyField.isSelected();

        if (subscriberName.isEmpty() || cuisineTypeInterest.isEmpty()) {
            showAlert("Username and Cuisine Type of Interest must be filled out");
            return;
        }

        Customer customer = new Customer(subscriberName, cuisineTypeInterest, broker);

        if (subscriberUsernames.contains(subscriberName)) {
            if (daily) {
                customer.unsubscribe("dailyMeal");
            }
            if (weekly) {
                customer.unsubscribe("weeklyMeal");
            }
            customer.unsubscribe("mealIdea");

            // Only remove from subscriberUsernames if no other subscriptions remain
//            if (!daily && !weekly) {
                subscriberUsernames.remove(subscriberName);
//            }
        } else {
            subscriberUsernames.add(subscriberName);
            if (daily) {
                customer.subscribe("dailyMeal");
            }
            if (weekly) {
                customer.subscribe("weeklyMeal");
            }
            customer.subscribe("mealIdea");
        }

        // Clear fields
        subscriberUsernameField.clear();
        cuisineTypeInterestField.clear();
        dailyField.setSelected(false);
        weeklyField.setSelected(false);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}