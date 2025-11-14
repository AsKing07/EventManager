package com.bschooleventmanager.eventmanager.controller.events;
import com.bschooleventmanager.eventmanager.dao.EvenementDAO;
import com.bschooleventmanager.eventmanager.exception.BusinessException;
import com.bschooleventmanager.eventmanager.model.enums.*;
import com.bschooleventmanager.eventmanager.service.EvenementService;
import com.bschooleventmanager.eventmanager.util.NotificationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.bschooleventmanager.eventmanager.model.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Event {
    @FXML
    public Label lbtfAge,lbtfArtits,lbtyConcert,lbTySpectacle,lbNvExpert,lbDomaine,lbIntervenant,lblErrDate,lblErrTitre,lbErrLieu,lblErrTyEvent,lblErrTypeConcert,lblErrTypeSpectacle,lblErrNvExpert,lblErrDomaine,lblErrAge,lblErrArtiste;
    @FXML
    public TextField tfNom, tfLieu, prixPlaceStandard,nbPlacesVip,prixPlaceVip,nbPlacesPremium,prixPlacePremium, nbPlacesStandard,tfArtits,Domaine,Intervenant;
    @FXML
    public TextArea Description;
    @FXML
    public DatePicker dpDate;
    @FXML
    public ComboBox<String> evType,nvExpert,tyConcert,tySpectacle;
    public ComboBox<Integer>tfAge;
    @FXML
    public Label lblError;
    @FXML
    public ImageView imgPreview;
    private static final Logger logger = LoggerFactory.getLogger(EvenementDAO.class);

    public Event(ComboBox<Integer> tfAge) {
        this.tfAge = tfAge;
    }
    public Event() {
        // Obligatoire pour JavaFX
    }

    @FXML
    private void importImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imgPreview.setImage(image);

            // Tu peux stocker le chemin si tu en as besoin
            // this.imagePath = selectedFile.getAbsolutePath();
        }
    }

    @FXML
    private void typeEventChange(){
        //LocalDate date = LocalDate.now();
        //NotificationUtils.showError(date.toString());
        var typeEvent = evType.getValue();
        if(typeEvent.equals(TypeEvenement.CONCERT.getLabel())){
            NotificationUtils.showError("je suis concert");
            //Visibilité TRUE de l'attribut age minmum
            tfAge.setVisible(true); tfAge.setManaged(true); lbtfAge.setVisible(true);lbtfAge.setManaged(true);
            //vISU TRUE Artiste/groupe
            tfArtits.setVisible(true); tfArtits.setManaged(true); lbtfArtits.setVisible(true);lbtfArtits.setManaged(true);
            //vISU TRUE typeConcert
            tyConcert.setVisible(true); tyConcert.setManaged(true); lbtyConcert.setVisible(true);lbtyConcert.setManaged(true);

            //visu False typeSpectacle
            tySpectacle.setVisible(false); tySpectacle.setManaged(false); lbTySpectacle.setVisible(false);lbTySpectacle.setManaged(false);
            //Visibilité False Niveau experience
            nvExpert.setVisible(false); nvExpert.setManaged(false); lbNvExpert.setVisible(false);lbNvExpert.setManaged(false);
            //vISU False Domaine
            Domaine.setVisible(false); Domaine.setManaged(false); lbDomaine.setVisible(false);lbDomaine.setManaged(false);
            //vISU False Intervenants
            Intervenant.setVisible(false); Intervenant.setManaged(false); lbIntervenant.setVisible(false);lbIntervenant.setManaged(false);

        }else if(typeEvent.equals(TypeEvenement.SPECTACLE.getLabel())){
            NotificationUtils.showError("je suis SPECTACLE");
            //Visibilité TRUE de l'attribut age minmum
            tfAge.setVisible(true); tfAge.setManaged(true); lbtfAge.setVisible(true);lbtfAge.setManaged(true);
            //Visibilité TRUE de Artiste
            tfArtits.setVisible(true); tfArtits.setManaged(true); lbtfArtits.setVisible(true);lbtfArtits.setManaged(true);
            //Visibilité TRUE TpeSpectacle
            tySpectacle.setVisible(true); tySpectacle.setManaged(true); lbTySpectacle.setVisible(true);lbTySpectacle.setManaged(true);

            //Visibilité False Niveau experience
            nvExpert.setVisible(false); nvExpert.setManaged(false); lbNvExpert.setVisible(false);lbNvExpert.setManaged(false);
            //vISU False Domaine
            Domaine.setVisible(false); Domaine.setManaged(false); lbDomaine.setVisible(false);lbDomaine.setManaged(false);
            //vISU False Intervenants
            Intervenant.setVisible(false); Intervenant.setManaged(false); lbIntervenant.setVisible(false);lbIntervenant.setManaged(false);
            //vISU false typeConcert
            tyConcert.setVisible(false); tyConcert.setManaged(false); lbtyConcert.setVisible(false);lbtyConcert.setManaged(false);
        }else{
            NotificationUtils.showError("je suis Conference");
            //Visibilité FALSE de l'attribut age minmum
            tfAge.setVisible(false);tfAge.setManaged(false);lbtfAge.setVisible(false);lbtfAge.setManaged(false);
            //vISU false Artiste/groupe
            tfArtits.setVisible(false); tfArtits.setManaged(false); lbtfArtits.setVisible(false);lbtfArtits.setManaged(false);
            //vISU false typeConcert
            tyConcert.setVisible(false); tyConcert.setManaged(false); lbtyConcert.setVisible(false);lbtyConcert.setManaged(false);
            //visu False typeSpectacle
            tySpectacle.setVisible(false); tySpectacle.setManaged(false); lbTySpectacle.setVisible(false);lbTySpectacle.setManaged(false);

            //Visibilité TRUE Niveau experience
            nvExpert.setVisible(true); nvExpert.setManaged(true); lbNvExpert.setVisible(true);lbNvExpert.setManaged(true);
            //vISU TRUE Domaine
            Domaine.setVisible(true); Domaine.setManaged(true); lbDomaine.setVisible(true);lbDomaine.setManaged(true);
            //vISU TRUE Intervenants
            Intervenant.setVisible(true); Intervenant.setManaged(true); lbIntervenant.setVisible(true);lbIntervenant.setManaged(true);
        }
    }
    /*@FXML
    private void ChangeDate(){
        LocalDate date = dpDate.getValue();
        LocalDateTime dT = LocalDateTime.now();
        System.out.println("la date "+dT);
        NotificationUtils.showError(dT.toString());
        if(date.equals(LocalDate.now())) NotificationUtils.showError("bonjour");
    }*/

    @FXML
    private void createEvent() throws BusinessException {
        lblError.setText("");
        lblErrDate.setText("");
        lblErrTitre.setText("");
        lbErrLieu.setText("");
        lblErrTyEvent.setText("");
        lblErrTypeConcert.setText("");
        lblErrTypeSpectacle.setText("");
        lblErrNvExpert.setText("");
        lblErrDomaine.setText("");
        lblErrAge.setText("");
        lblErrArtiste.setText("");

        String titre,artiste_groupe,typeEvent ,ValDescription, Intervenants, domaine= ""; LocalDate dateEvent = null; String Lieu="";
        TypeSpectacle typeSpect;
        NiveauExpertise nivExpert = null;
        Integer NbrePlaceStandard,NbrePlaceVip,NbrePlacesPremium ,prixStand,prixVip,prixPremium,ageMin=0; TypeConcert typeConcert=null;

        // Validation
        if (tfNom.getText().isEmpty() || (dpDate.getValue() == null) ||
                tfLieu.getText().isEmpty() || evType.getValue() == null) {
            if(dpDate.getValue() == null){
                lblErrDate.setManaged(true);
                lblErrDate.setText("La date de l'évènement ne peut pas être vide !");
            }else if(dpDate.getValue().isBefore(LocalDate.now())){
                lblErrDate.setManaged(true);
                lblErrDate.setText("La date de l'évènement ne peut pas être avant la date du jour !");
            }
            if (tfNom.getText().isEmpty()) {
                lblErrTitre.setManaged(true);
                lblErrTitre.setText("Le tirtre titre ne peut pas être vide");
            }
            if (tfLieu.getText().isEmpty()) {
                lbErrLieu.setManaged(true);
                lbErrLieu.setText("Le lieu de l'évènement ne peut pas être vide");
            }
            if (evType.getValue() == null) {
                lblErrTyEvent.setManaged(true);
                lblErrTyEvent.setText("Le type d'évènement est obligatoire");
            }
            lblError.setText("⚠️ Veuillez remplir tous les champs obligatoires (*) !");
            return;
        }else{
            if(dpDate.getValue().isBefore(LocalDate.now())){
                lblErrDate.setManaged(true);
                lblErrDate.setText("La date de l'évènement ne peut pas être avant la date du jour !");
            }else {
                titre = tfNom.getText();
                ValDescription = Description.getText();
                dateEvent = dpDate.getValue();
                Lieu = tfLieu.getText();
                typeEvent = evType.getValue().toString();


                NbrePlaceStandard = Integer.parseInt(nbPlacesStandard.getText());
                NbrePlaceVip = Integer.parseInt(nbPlacesVip.getText());
                NbrePlacesPremium = Integer.parseInt(nbPlacesPremium.getText());
                prixVip = Integer.parseInt(prixPlaceVip.getText());
                prixStand = Integer.parseInt(prixPlaceStandard.getText());
                prixPremium = Integer.parseInt(prixPlacePremium.getText());

                if (typeEvent.equals(TypeEvenement.CONCERT.getLabel())) {
                    if (tyConcert.getValue() == null) {
                        lblErrTypeConcert.setManaged(true);
                        lblErrTypeConcert.setText("Le type de concert est obligatoire ");
                    } else if (tfAge.getValue() == null) {
                        lblErrAge.setManaged(true);
                        lblErrAge.setText("Veuillez choisir un âge minimum !");
                    } else if (tfArtits.getText().isEmpty()) {
                        lblErrArtiste.setManaged(true);
                        lblErrArtiste.setText("Veuillez renseigner l'artiste(s) / groupe en spéctacle");
                    } else {

                        // try{
                        if (tyConcert.getValue().equals(TypeConcert.LIVE)) {
                            typeConcert = TypeConcert.LIVE;
                        } else {
                            typeConcert = TypeConcert.ACOUSTIQUE;
                        }
                        ageMin = Integer.parseInt(String.valueOf(tfAge.getValue()));
                        artiste_groupe = tfArtits.getText();
                        //CREATION D'UN CONCERT
                        Concert concert = new Concert(titre, dateEvent, Lieu, ValDescription, NbrePlaceStandard, NbrePlaceVip, NbrePlacesPremium, BigDecimal.valueOf(prixStand), BigDecimal.valueOf(prixVip), BigDecimal.valueOf(prixPremium), LocalDateTime.now(), artiste_groupe, typeConcert, ageMin);
                        EvenementService.creerConcert(concert);
                        NotificationUtils.showSuccess("Creation CONCERT");
                        //  }
                        //  catch (Exception e)
                        //  {
//logger.error("erreur");
                        //  logger.error(e.toString());
                        //  }

                    }
                } else if (typeEvent.equals(TypeEvenement.SPECTACLE.getLabel())) {
                    if (tySpectacle.getValue() == null) {
                        lblErrTypeSpectacle.setManaged(true);
                        lblErrTypeSpectacle.setText("Le type du spectacle est obligatoire");
                    } else if (tfAge.getValue() == null) {
                        lblErrAge.setManaged(true);
                        lblErrAge.setText("Veuillez choisir un âge minimum !");
                    } else if (tfArtits.getText().isEmpty()) {
                        lblErrArtiste.setManaged(true);
                        lblErrArtiste.setText("Veuillez renseigner l'artiste(s) / groupe en spéctacle");
                    } else {
                        if (tySpectacle.getValue().equals(TypeSpectacle.CIRQUE.getLabel())) {
                            typeSpect = TypeSpectacle.CIRQUE;
                        } else if (tySpectacle.getValue().equals(TypeSpectacle.HUMOUR.getLabel())) {
                            typeSpect = TypeSpectacle.HUMOUR;
                        } else {
                            typeSpect = TypeSpectacle.THEATRE;
                        }
                        ageMin = Integer.parseInt(String.valueOf(tfAge.getValue()));
                        artiste_groupe = tfArtits.getText();
                        Spectacle spectacle = new Spectacle(titre, dateEvent, Lieu, ValDescription, NbrePlaceStandard, NbrePlaceVip, NbrePlacesPremium, BigDecimal.valueOf(prixStand), BigDecimal.valueOf(prixVip), BigDecimal.valueOf(prixPremium), LocalDateTime.now(), typeSpect, artiste_groupe, ageMin);
                        EvenementService.creerSpectacle(spectacle);
                        //CREATION D'UN SPECTACLE
                        NotificationUtils.showError("Creation SPECTACLE");
                    }
                } else {
                    if (nvExpert.getValue() == null) {
                        lblErrNvExpert.setManaged(true);
                        lblErrNvExpert.setText("Le Niveau d'expertise est obligatoire");
                    } else if (Domaine.getText().isEmpty()) {
                        lblErrDomaine.setManaged(true);
                        lblErrDomaine.setText("Le domaine est obligatoire");
                    } else {
                        domaine = Domaine.getText();
                        Intervenants = Intervenant.getText();
                        if(nvExpert.getValue().equals(NiveauExpertise.DEBUTANT.getLabel())){
                            nivExpert = NiveauExpertise.DEBUTANT;
                        }
                        if(nvExpert.getValue().equals(NiveauExpertise.INTERMEDIAIRE.getLabel())){
                            nivExpert = NiveauExpertise.INTERMEDIAIRE;
                        }
                        if(nvExpert.getValue().equals(NiveauExpertise.PROFESSIONNEL.getLabel())){
                            nivExpert = NiveauExpertise.PROFESSIONNEL;
                        }

                        artiste_groupe = tfArtits.getText();
                        Conference conference = new Conference(titre,dateEvent,Lieu,TypeEvenement.CONFERENCE,ValDescription,NbrePlaceStandard,NbrePlaceVip,NbrePlacesPremium,BigDecimal.valueOf(prixStand),BigDecimal.valueOf(prixVip),BigDecimal.valueOf(prixPremium),LocalDateTime.now(),Intervenants,domaine,nivExpert);
                        //Conference conference = new Conference(titre, dateEvent, Lieu, TypeEvenement.CONFERENCE,ValDescription, NbrePlaceStandard, NbrePlaceVip, NbrePlacesPremium, BigDecimal.valueOf(prixStand), BigDecimal.valueOf(prixVip), BigDecimal.valueOf(prixPremium), LocalDateTime.now(), domaine, Intervenants,nivExpert);
                        EvenementService.creerConference(conference);
                        //CREATION D'une CONFERENCE
                        NotificationUtils.showError("Creation CONFERENCE");
                    }
                }
            }
        }

        /*try {
            //Date dateEvent = Date.valueOf(dpDate.getValue());
            LocalDate dateEvent = dpDate.getValue();
            double prix = Double.parseDouble(tfPrix.getText());
            int nbPlaces = Integer.parseInt(tfNbPlaces.getText());
            String typeEvent = evType.getValue();

            if(typeEvent == TypeEvenement.CONCERT.getLabel()){
                Concert concert = new Concert();
            } else if (typeEvent == TypeEvenement.CONFERENCE.getLabel()) {
                Conference conference = new Conference();
            } else if (typeEvent == TypeEvenement.SPECTACLE.getLabel()) {
                Spectacle spectacle = new Spectacle();
            }


            lblError.setStyle("-fx-text-fill: green;");
            lblError.setText("✔️ Événement créé avec succès !");

            // Exemple d'utilisation : impression console
            System.out.println("\nNouvel événement créé : ");
            //System.out.println(event);

        } catch (NumberFormatException e) {
            lblError.setText("⚠️ Les champs prix et nombre de places doivent être numériques !");
        }*/
    }
}
