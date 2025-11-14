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

    // Exports pour permettre l'accès aux packages
    exports com.bschooleventmanager.eventmanager;
    exports com.bschooleventmanager.eventmanager.controller.auth;

    // Opens pour permettre la réflexion JavaFX
    opens com.bschooleventmanager.eventmanager to javafx.fxml;
    opens com.bschooleventmanager.eventmanager.controller.auth to javafx.fxml;
}