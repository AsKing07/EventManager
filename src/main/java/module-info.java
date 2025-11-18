module com.bschooleventmanager.eventmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires mysql.connector.j;


    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires org.slf4j;
    requires jbcrypt;
    requires java.desktop;
    requires stripe.java;

    // Exports pour permettre l'accès aux packages
    exports com.bschooleventmanager.eventmanager;
    exports com.bschooleventmanager.eventmanager.controller.auth;
    exports com.bschooleventmanager.eventmanager.controller.organisateur;
    exports com.bschooleventmanager.eventmanager.controller.client;
    exports com.bschooleventmanager.eventmanager.controller.shared;
    exports com.bschooleventmanager.eventmanager.model;
    exports com.bschooleventmanager.eventmanager.service;
    exports com.bschooleventmanager.eventmanager.exception;
    exports com.bschooleventmanager.eventmanager.config;

    // Opens pour permettre la réflexion JavaFX
    opens com.bschooleventmanager.eventmanager.model to javafx.base;
    opens com.bschooleventmanager.eventmanager to javafx.fxml;
    opens com.bschooleventmanager.eventmanager.controller.auth to javafx.fxml;
    opens com.bschooleventmanager.eventmanager.controller.organisateur to javafx.fxml;
    opens com.bschooleventmanager.eventmanager.controller.client to javafx.fxml;
    opens com.bschooleventmanager.eventmanager.controller.shared to javafx.fxml;
    opens com.bschooleventmanager.eventmanager.controller.events to javafx.fxml;


}