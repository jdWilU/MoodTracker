module org.example.moodtracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens org.example.moodtracker to javafx.fxml;
    exports org.example.moodtracker;
    exports org.example.moodtracker.model;
    opens org.example.moodtracker.model to javafx.fxml;
    exports org.example.moodtracker.controller;
    opens org.example.moodtracker.controller to javafx.fxml;
}