package com.bschooleventmanager.eventmanager.controller.organisateur;

import com.bschooleventmanager.eventmanager.model.Evenement;
import com.bschooleventmanager.eventmanager.model.enums.StatutEvenement;
import com.bschooleventmanager.eventmanager.service.EvenementService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class UpdateEventController {

    @FXML
    private TextField nomField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField heureField;

    @FXML
    private ComboBox<StatutEvenement> statutCombo;  // IMPORTANT for enums

    @FXML
    private Button saveButton;

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private Evenement evenement;
    private Runnable refreshCallback;

    private final EvenementService evenementService = new EvenementService();

    public void setEvent(Evenement evt, Runnable callback) {
        this.evenement = evt;
        this.refreshCallback = callback;

        statutCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(StatutEvenement item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getLabel());
            }
        });

        statutCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(StatutEvenement item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getLabel());
            }
        });

        statutCombo.getItems().setAll(StatutEvenement.values());

        // Fill fields with current event values
        nomField.setText(evt.getNom());
        LocalDateTime dateTime = evt.getDateEvenement();
        datePicker.setValue(dateTime.toLocalDate());
        heureField.setText(dateTime.toLocalTime().toString());
        statutCombo.setValue(evt.getStatut());
    }


    @FXML
    private void saveChanges() {
        try {
            String nom = nomField.getText();
            LocalDate date = datePicker.getValue();
            LocalTime heure = LocalTime.parse(heureField.getText());
            StatutEvenement statut = statutCombo.getValue();

            if (nom.isEmpty() || date == null || statut == null) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants",
                        "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            evenement.setNom(nom);
            evenement.setDateEvenement(LocalDateTime.of(date, heure));
            evenement.setStatut(statut);

            evenementService.updateEvent(evenement);

            showAlert(Alert.AlertType.INFORMATION,
                    "Succès",
                    "L'événement a bien été mis à jour !");

            if (refreshCallback != null) {
                refreshCallback.run();
            }

            closeWindow();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible de sauvegarder les modifications.");
            e.printStackTrace();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}