package com.allenamento.view;

import com.allenamento.model.Allenamento;
import com.allenamento.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;

/**
 * Modal dialog for creating and editing a training.
 */
public class AllenamentoDialog extends Dialog<Allenamento> {

    private TextField nomeField;
    private TextField etaField;
    private TextField tipoField;
    private TextArea obiettivoArea;
    private TextArea descrizioneArea;
    private TextArea noteArea;
    private TextField strumentoField;
    private LanguageManager languageManager;

    /**
     * Constructor of the dialog.
     *
     * @param allenamento the training to edit, null to create a new one
     * @param languageManager the manager for handling translated messages
     */
    public AllenamentoDialog(Allenamento allenamento, LanguageManager languageManager) {
        this.languageManager = languageManager;
        setTitle(allenamento == null ? languageManager.get("dialog.newTraining") : languageManager.get("dialog.editTraining"));
        setHeaderText(null);

        setResizable(true);

        DialogPane dialogPane = getDialogPane();
        dialogPane.setMinWidth(500);
        dialogPane.setMinHeight(500);
        dialogPane.setPrefWidth(520);
        dialogPane.setPrefHeight(520);
        
        // Create the dialog content in a ScrollPane
        VBox content = createContent(allenamento);
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setPrefHeight(380);
        scrollPane.setMaxHeight(380);
        dialogPane.setContent(scrollPane);

        // Add system buttons
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Modify button texts after they are created
        setOnShown(event -> {
            Button okBtn = (Button) dialogPane.lookupButton(ButtonType.OK);
            Button cancelBtn = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
            
            if (okBtn != null) {
                okBtn.setText(languageManager.get("button.save"));
            }
            if (cancelBtn != null) {
                cancelBtn.setText(languageManager.get("dialog.cancel"));
            }
        });

        // Set the result when OK is pressed
        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String nome = nomeField != null ? nomeField.getText() : "";
                String eta = etaField != null ? etaField.getText() : "";
                String tipo = tipoField != null ? tipoField.getText() : "";
                String obiettivo = obiettivoArea != null ? obiettivoArea.getText() : "";
                String descrizione = descrizioneArea != null ? descrizioneArea.getText() : "";
                String note = noteArea != null ? noteArea.getText() : "";
                String strumento = strumentoField != null ? strumentoField.getText() : "";
                
                return new Allenamento(nome, eta, tipo, obiettivo, descrizione, note, strumento);
            }
            return null;
        });
    }

    /**
     * Creates the dialog content.
     */
    private VBox createContent(Allenamento allenamento) {
        VBox content = new VBox();
        content.setSpacing(8);
        content.setPadding(new Insets(10));

        // Type field
        VBox tipoBox = createLabeledField(languageManager.get("label.trainingType"), languageManager.get("placeholder.type"));
        tipoField = (TextField) tipoBox.getChildren().get(1);
        content.getChildren().add(tipoBox);

        // Training name field
        VBox nomeBox = createLabeledField(languageManager.get("label.trainingName"), languageManager.get("placeholder.name"));
        nomeField = (TextField) nomeBox.getChildren().get(1);
        content.getChildren().add(nomeBox);

        // Objective field
        VBox obiettivoBox = new VBox();
        obiettivoBox.setSpacing(3);
        Label obiettivoLabel = new Label(languageManager.get("label.objective"));
        obiettivoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");
        obiettivoArea = new TextArea();
        obiettivoArea.setPromptText(languageManager.get("placeholder.objective"));
        obiettivoArea.setWrapText(true);
        obiettivoArea.setPrefHeight(60);
        obiettivoBox.getChildren().addAll(obiettivoLabel, obiettivoArea);
        content.getChildren().add(obiettivoBox);

        // Age field
        VBox etaBox = createLabeledField(languageManager.get("label.age"), languageManager.get("placeholder.age"));
        etaField = (TextField) etaBox.getChildren().get(1);
        content.getChildren().add(etaBox);

        // Training phase TextArea
        VBox noteBox = new VBox();
        noteBox.setSpacing(3);
        Label noteLabel = new Label(languageManager.get("label.trainingPhase"));
        noteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");
        noteArea = new TextArea();
        noteArea.setPromptText(languageManager.get("placeholder.phase"));
        noteArea.setWrapText(true);
        noteArea.setPrefHeight(50);
        noteBox.getChildren().addAll(noteLabel, noteArea);
        content.getChildren().add(noteBox);

        // Equipment field
        VBox strumentoBox = createLabeledField(languageManager.get("label.equipment"), languageManager.get("placeholder.equipment"));
        strumentoField = (TextField) strumentoBox.getChildren().get(1);
        content.getChildren().add(strumentoBox);

        // Description field
        VBox descrizioneBox = new VBox();
        descrizioneBox.setSpacing(3);
        Label descrizioneLabel = new Label(languageManager.get("label.description"));
        descrizioneLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");
        descrizioneArea = new TextArea();
        descrizioneArea.setPromptText(languageManager.get("placeholder.description"));
        descrizioneArea.setWrapText(true);
        descrizioneArea.setPrefHeight(60);
        descrizioneBox.getChildren().addAll(descrizioneLabel, descrizioneArea);
        content.getChildren().add(descrizioneBox);

        // Populate fields if editing
        if (allenamento != null) {
            nomeField.setText(allenamento.getNome());
            etaField.setText(allenamento.getEta());
            tipoField.setText(allenamento.getTipo());
            obiettivoArea.setText(allenamento.getObiettivo() != null ? allenamento.getObiettivo() : "");
            descrizioneArea.setText(allenamento.getDescrizione() != null ? allenamento.getDescrizione() : "");
            noteArea.setText(allenamento.getNote() != null ? allenamento.getNote() : "");
            strumentoField.setText(allenamento.getStrumento() != null ? allenamento.getStrumento() : "");
        }

        return content;
    }

    /**
     * Creates a TextField with label.
     */
    private VBox createLabeledField(String label, String promptText) {
        VBox box = new VBox();
        box.setSpacing(2);

        Label labelControl = new Label(label);
        labelControl.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");

        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setPrefWidth(Double.MAX_VALUE);
        field.setPrefHeight(28);

        box.getChildren().addAll(labelControl, field);
        return box;
    }
}
