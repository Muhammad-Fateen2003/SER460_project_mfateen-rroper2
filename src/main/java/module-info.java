module org.example.ser460_project {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens org.example.ser460_project to javafx.fxml;
    exports org.example.ser460_project;
}