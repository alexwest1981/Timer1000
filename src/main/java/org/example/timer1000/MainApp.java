package org.example.timer1000;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {

    private ObservableList<Member> memberList = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        memberList.addAll(JsonManager.loadMembers());

        VBox memberForm = createMemberForm();
        VBox stopwatch = createStopwatch();
        VBox logTable = createTimeLogTable();

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        root.getChildren().addAll(memberForm, new Separator(), stopwatch, new Separator(), logTable);

        Scene scene = new Scene(root, 400, 750);
        primaryStage.setTitle("Timer1000 (Medlemsregistrering & Tidtagning)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Valideringsfel");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private VBox createMemberForm() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label firstNameLabel = new Label("Förnamn:");
        TextField firstNameInput = new TextField();
        Label lastNameLabel = new Label("Efternamn:");
        TextField lastNameInput = new TextField();
        Label phoneLabel = new Label("Telefonnummer:");
        TextField phoneInput = new TextField();
        Label addressLabel = new Label("Adress:");
        TextField addressInput = new TextField();

        Button saveButton = new Button("Spara medlem");
        Label resultLabel = new Label(" ");

        GridPane.setConstraints(firstNameLabel, 0, 0);
        GridPane.setConstraints(firstNameInput, 1, 0);
        GridPane.setConstraints(lastNameLabel, 0, 1);
        GridPane.setConstraints(lastNameInput, 1, 1);
        GridPane.setConstraints(phoneLabel, 0, 2);
        GridPane.setConstraints(phoneInput, 1, 2);
        GridPane.setConstraints(addressLabel, 0, 3);
        GridPane.setConstraints(addressInput, 1, 3);
        GridPane.setConstraints(saveButton, 1, 4);
        GridPane.setConstraints(resultLabel, 0, 5, 2, 1);

        grid.getChildren().addAll(
                firstNameLabel, firstNameInput,
                lastNameLabel, lastNameInput,
                phoneLabel, phoneInput,
                addressLabel, addressInput,
                saveButton,
                resultLabel
        );

        saveButton.setOnAction(e -> {
            String fName = firstNameInput.getText().trim();
            String lName = lastNameInput.getText().trim();
            String phone = phoneInput.getText().trim();
            String address = addressInput.getText().trim();

            if (fName.isEmpty() || lName.isEmpty()) {
                showAlert("Obligatoriska fält saknas", "Förnamn och Efternamn måste fyllas i.");
                return;
            }

            if (address.isEmpty()) {
                showAlert("Obligatoriskt fält saknas", "Adress måste fyllas i.");
                return;
            }

            // OBS: Om du vill att telefonnummer ska vara obligatoriskt, använd: if (phone.isEmpty()) { ... }
            if (!phone.isEmpty() && !phone.matches("\\d+")) {
                showAlert("Felaktigt format", "Telefonnummer får endast innehålla siffror (0-9).");
                return;
            }

            Member newMember = new Member(0, fName, lName, address, phone);

            JsonManager.saveMember(newMember);

            // Uppdatera UI: Ladda om och uppdatera listan för ComboBoxen
            memberList.clear();
            memberList.addAll(JsonManager.loadMembers());

            String data = String.format("Sparad: %s %s, %s, %s", fName, lName, phone, address);
            resultLabel.setText(data);

            // Rensa fält
            firstNameInput.clear();
            lastNameInput.clear();
            phoneInput.clear();
            addressInput.clear();
        });

        VBox formContainer = new VBox(10, grid);
        return formContainer;
    }

    private VBox createStopwatch() {
        ComboBox<Member> memberSelector = new ComboBox<>();
        memberSelector.setPromptText("Välj medlem att logga tid för");
        memberSelector.setItems(memberList);

        Button reloadListButton = new Button("Ladda om medlemmar");
        HBox selectorContainer = new HBox(10, memberSelector, reloadListButton);

        Label timeLabel = new Label("00:00:00.00");
        timeLabel.setStyle("-fx-font-size: 24;");

        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");
        Button resetButton = new Button("Nollställ");

        HBox buttons = new HBox(10, startButton, stopButton, resetButton);
        final long[] totalMillis = {0};
        Label logStatusLabel = new Label("");

        reloadListButton.setOnAction(e -> {
            // Tömmer och laddar om listan från JSON-filen
            memberList.clear();
            memberList.addAll(JsonManager.loadMembers());
            logStatusLabel.setText("Medlemslista uppdaterad från JSON.");
        });

        Timeline timeline = new Timeline(
                // Ändrad från Duration.seconds(1) till 10 millisekunder
                new KeyFrame(Duration.millis(10), e -> {
                    totalMillis[0] += 10; // Lägg till 10 ms
                    timeLabel.setText(formatTime(totalMillis[0]));
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);

        startButton.setOnAction(e -> {
            if (memberSelector.getValue() == null) {
                logStatusLabel.setText("OBS: Välj en medlem innan du startar!");
            } else {
                totalMillis[0] = 0; // Återställ räknaren i millisekunder
                timeline.play();
                logStatusLabel.setText("Räknar tid för " + memberSelector.getValue().toString() + "...");
            }
        });

        stopButton.setOnAction(e -> {
            timeline.stop();
            Member selectedMember = memberSelector.getSelectionModel().getSelectedItem();
            long finalDurationMillis = totalMillis[0]; // Hämta tiden i millisekunder

            // Konvertera millisekunder till hela sekunder för sparande i JSON
            long finalDurationSeconds = finalDurationMillis / 1000;

            if (selectedMember != null) {
                JsonManager.saveTimeLog(selectedMember.getId(), finalDurationSeconds); // Spara i sekunder
                logStatusLabel.setText(String.format("Tid loggad: %s (%d sekunder) för %s.",
                        formatTime(finalDurationMillis), finalDurationSeconds, selectedMember.toString())); // Visa tiden i det nya formatet
            } else {
                logStatusLabel.setText("Tid stoppad, ingen tidslogg sparades (ingen medlem vald).");
            }
        });

        resetButton.setOnAction(e -> {
            timeline.stop();
            totalMillis[0] = 0;
            timeLabel.setText(formatTime(totalMillis[0])); // Nollställ displayen
            logStatusLabel.setText("Timer nollställd.");
        });

        VBox stopwatchContainer = new VBox(10, selectorContainer, timeLabel, buttons, logStatusLabel);
        return stopwatchContainer;
    }

    // NY METOD: Skapar tabellen för tidsloggar
    private VBox createTimeLogTable() {
        Label header = new Label("Sparade Tidsloggar:");
        header.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 5 0;");

        TableView<TimeLogEntry> table = new TableView<>();
        table.setPrefHeight(200);

        TableColumn<TimeLogEntry, Integer> memberIdCol = new TableColumn<>("Medlem ID");
        // Egenskapsnamnet måste matcha getters i TimeLogEntry klassen (getMemberId)
        memberIdCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("memberId"));
        memberIdCol.setPrefWidth(100);

        TableColumn<TimeLogEntry, Long> durationCol = new TableColumn<>("Tid (sekunder)");
        // Egenskapsnamnet måste matcha getters i TimeLogEntry klassen (getDurationSeconds)
        durationCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("durationSeconds"));
        durationCol.setPrefWidth(150);

        table.getColumns().addAll(memberIdCol, durationCol);
        table.getItems().addAll(JsonManager.loadTimeLogs());

        Button reloadButton = new Button("Ladda om loggar");
        reloadButton.setOnAction(e -> {
            table.getItems().clear();
            table.getItems().addAll(JsonManager.loadTimeLogs());
        });

        HBox controls = new HBox(10, reloadButton);
        controls.setPadding(new Insets(10, 0, 0, 0));

        VBox container = new VBox(5, header, table, controls);
        return container;
    }


    private String formatTime(long totalMillis) {
        long hours = totalMillis / 3_600_000;
        long minutes = (totalMillis % 3_600_000) / 60_000;
        long seconds = (totalMillis % 60_000) / 1_000;
        long centiseconds = (totalMillis % 1_000) / 10; // Hundradelar

        return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, centiseconds);
    }

    public static void main(String[] args) {
        launch(args);
    }

}