package com.bschooleventmanager.eventmanager.controller.client;

import com.bschooleventmanager.eventmanager.model.Evenement;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;


public class ClientEventDetailsController {
    @FXML private Label eventNameLabel;
    @FXML private Label eventDateLabel;
    @FXML private Label eventLocationLabel;
    @FXML private Label eventTypeLabel;
    @FXML private Label eventPriceLabel; // Example
    @FXML private TextArea eventDescriptionArea;

    private Evenement currentEvent;

    /**
     * Called by ClientEventsController to inject the selected event data.
     * @param event The Evenement object to display.
     */
    public void setEventData(Evenement event) {
        this.currentEvent = event;
        // Call a method to populate the UI fields
        populateUI();
    }

    private void populateUI() {
        if (currentEvent != null) {
            eventNameLabel.setText(currentEvent.getNom());
            eventDateLabel.setText(currentEvent.getDateEvenement().toString()); // Format this properly
            eventLocationLabel.setText(currentEvent.getLieu());
            eventTypeLabel.setText(currentEvent.getTypeEvenement().name());
            eventDescriptionArea.setText(currentEvent.getDescription());
            eventPriceLabel.setText(currentEvent.getPrixStandard() + " " + currentEvent.getPrixVip() + "...");
        }
    }

    @FXML
    public void initialize() {
    }
}
