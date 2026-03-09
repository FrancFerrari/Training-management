package com.allenamento.view;

import com.allenamento.controller.AllenamentoController;
import com.allenamento.model.Allenamento;
import com.allenamento.model.Immagine;
import com.allenamento.util.ImageManager;
import com.allenamento.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

/**
 * Detail window for viewing and modifying complete training details.
 */
public class AllenamentoDetailWindow {
    private final Stage stage;
    private final AllenamentoController controller;
    private final Allenamento allenamento;
    private final LanguageManager languageManager;
    private FlowPane imagesPane;

    /**
     * Constructor for the detail window.
     *
     * @param controller the controller for operations
     * @param allenamento the training to display
     * @param languageManager the manager for handling translated messages
     */
    public AllenamentoDetailWindow(AllenamentoController controller, Allenamento allenamento, LanguageManager languageManager) {
        this.controller = controller;
        this.allenamento = allenamento;
        this.languageManager = languageManager;
        this.stage = new Stage();

        initializeWindow();
    }

    /**
     * Initializes the window.
     */
    private void initializeWindow() {
        stage.setTitle(languageManager.get("label.trainingDetails") + ": " + allenamento.getNome());
        stage.setWidth(900);
        stage.setHeight(700);
        stage.setMinWidth(700);
        stage.setMinHeight(500);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-padding: 0;");

        VBox mainContent = createMainContent();
        scrollPane.setContent(mainContent);

        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
    }

    /**
     * Creates the main content of the window.
     */
    private VBox createMainContent() {
        VBox mainContent = new VBox();
        mainContent.setSpacing(15);
        mainContent.setPadding(new Insets(15));
        mainContent.setStyle("-fx-background-color: #f5f5f5;");

        // General information section
        VBox infoSection = createInfoSection();
        mainContent.getChildren().add(infoSection);

        // Separator
        Separator separator = new Separator();
        mainContent.getChildren().add(separator);

        // Images section
        VBox imagesSection = createImagesSection();
        mainContent.getChildren().add(imagesSection);

        return mainContent;
    }

    /**
     * Creates the general information section.
     */
    private VBox createInfoSection() {
        VBox section = new VBox();
        section.setSpacing(10);
        section.setStyle("-fx-border: 1px solid #ddd; -fx-padding: 12; -fx-background-color: white;");

        Label titleLabel = new Label(languageManager.get("label.trainingInfo"));
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(10, 0, 0, 0));

        // Type
        grid.add(new Label(languageManager.get("label.type")), 0, 0);
        Label tipoValue = new Label(allenamento.getTipo());
        tipoValue.setStyle("-fx-text-fill: #0066cc;");
        grid.add(tipoValue, 1, 0);

        // Training name
        grid.add(new Label(languageManager.get("label.trainingName")), 0, 1);
        Label nomeValue = new Label(allenamento.getNome());
        nomeValue.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        grid.add(nomeValue, 1, 1);

        // Age
        grid.add(new Label(languageManager.get("label.age")), 0, 2);
        grid.add(new Label(allenamento.getEta()), 1, 2);

        // Equipment
        grid.add(new Label(languageManager.get("label.equipment")), 0, 3);
        grid.add(new Label(allenamento.getStrumento() != null ? allenamento.getStrumento() : ""), 1, 3);

        // Creation Date
        grid.add(new Label(languageManager.get("label.createDate")), 0, 4);
        grid.add(new Label(allenamento.getDataCreazione() != null ? allenamento.getDataCreazione().toString() : "N/A"), 1, 4);

        // Modification Date
        grid.add(new Label(languageManager.get("label.modifyDate")), 0, 5);
        grid.add(new Label(allenamento.getDataModifica() != null ? allenamento.getDataModifica().toString() : "N/A"), 1, 5);

        section.getChildren().addAll(titleLabel, grid);

        // Objective
        if (allenamento.getObiettivo() != null && !allenamento.getObiettivo().isEmpty()) {
            Separator sep = new Separator();
            section.getChildren().add(sep);

            Label descLabel = new Label(languageManager.get("label.objective"));
            descLabel.setStyle("-fx-font-weight: bold;");
            TextArea descArea = new TextArea(allenamento.getObiettivo());
            descArea.setEditable(false);
            descArea.setWrapText(true);
            descArea.setPrefHeight(80);
            descArea.setStyle("-fx-control-inner-background: #efefef;");

            section.getChildren().addAll(descLabel, descArea);
        }

        // Description
        if (allenamento.getDescrizione() != null && !allenamento.getDescrizione().isEmpty()) {
            Separator sep = new Separator();
            section.getChildren().add(sep);

            Label descrizioneLabel = new Label(languageManager.get("label.description"));
            descrizioneLabel.setStyle("-fx-font-weight: bold;");
            TextArea descrizioneArea = new TextArea(allenamento.getDescrizione());
            descrizioneArea.setEditable(false);
            descrizioneArea.setWrapText(true);
            descrizioneArea.setPrefHeight(70);
            descrizioneArea.setStyle("-fx-control-inner-background: #efefef;");

            section.getChildren().addAll(descrizioneLabel, descrizioneArea);
        }

        // Training phase (notes)
        if (allenamento.getNote() != null && !allenamento.getNote().isEmpty()) {
            Separator sep = new Separator();
            section.getChildren().add(sep);

            Label noteLabel = new Label(languageManager.get("label.trainingPhase"));
            noteLabel.setStyle("-fx-font-weight: bold;");
            TextArea noteArea = new TextArea(allenamento.getNote());
            noteArea.setEditable(false);
            noteArea.setWrapText(true);
            noteArea.setPrefHeight(60);
            noteArea.setStyle("-fx-control-inner-background: #efefef;");

            section.getChildren().addAll(noteLabel, noteArea);
        }

        return section;
    }

    /**
     * Creates the images section.
     */
    private VBox createImagesSection() {
        VBox section = new VBox();
        section.setSpacing(10);
        section.setStyle("-fx-border: 1px solid #ddd; -fx-padding: 12; -fx-background-color: white;");

        Label titleLabel = new Label(languageManager.get("label.attachedImages"));
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        // Pulsanti azioni
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);

        Button addImageButton = new Button(languageManager.get("button.addImageLabel"));
        addImageButton.setStyle("-fx-padding: 8; -fx-font-size: 11;");
        addImageButton.setOnAction(e -> addImage());

        Button deleteImageButton = new Button(languageManager.get("button.deleteImageLabel"));
        deleteImageButton.setStyle("-fx-padding: 8; -fx-font-size: 11; -fx-text-fill: white; -fx-background-color: #dc3545;");
        deleteImageButton.setOnAction(e -> deleteSelectedImage());

        buttonBox.getChildren().addAll(addImageButton, deleteImageButton);

        // Flow pane for images
        imagesPane = new FlowPane();
        imagesPane.setHgap(10);
        imagesPane.setVgap(10);
        imagesPane.setPrefWrapLength(800);
        imagesPane.setStyle("-fx-border: 1px dashed #ccc; -fx-padding: 10; -fx-background-color: #fafafa;");

        refreshImages();

        ScrollPane scrollPane = new ScrollPane(imagesPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        section.getChildren().addAll(titleLabel, buttonBox, scrollPane);

        return section;
    }

    /**
     * Refreshes the image display.
     */
    private void refreshImages() {
        imagesPane.getChildren().clear();

        for (Immagine immagine : allenamento.getImmagini()) {
            VBox imageBox = createImageBox(immagine);
            imagesPane.getChildren().add(imageBox);
        }

        if (allenamento.getImmagini().isEmpty()) {
            Label noImagesLabel = new Label(languageManager.get("message.noImagesLoaded"));
            noImagesLabel.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
            imagesPane.getChildren().add(noImagesLabel);
        }
    }

    /**
     * Creates the box for a single image.
     */
    private VBox createImageBox(Immagine immagine) {
        VBox imageBox = new VBox();
        imageBox.setSpacing(5);
        imageBox.setStyle("-fx-border: 1px solid #ddd; -fx-padding: 8; -fx-background-color: white;");
        imageBox.setPrefWidth(150);
        imageBox.setAlignment(Pos.CENTER);

        try {
            File imageFile = ImageManager.resolveImageFile(immagine.getPath());
            if (imageFile != null && imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(130);
                imageView.setFitHeight(130);
                imageView.setPreserveRatio(true);
                imageView.setCursor(Cursor.HAND);

                // Click to open fullscreen
                int imageIndex = allenamento.getImmagini().indexOf(immagine);
                imageView.setOnMouseClicked(e -> openFullScreenViewer(imageIndex));

                Tooltip.install(imageView, new Tooltip(languageManager.get("tooltip.clickToEnlarge")));
                imageBox.getChildren().add(imageView);
            } else {
                Label errorLabel = new Label(languageManager.get("message.imageNotFound"));
                errorLabel.setStyle("-fx-text-alignment: center; -fx-text-fill: #cc0000;");
                imageBox.getChildren().add(errorLabel);
            }
        } catch (Exception e) {
            Label errorLabel = new Label(languageManager.get("message.cannotLoad"));
            errorLabel.setStyle("-fx-text-alignment: center;");
            imageBox.getChildren().add(errorLabel);
        }

        Label filenameLabel = new Label(immagine.getFilename());
        filenameLabel.setStyle("-fx-font-size: 10; -fx-text-alignment: center;");
        filenameLabel.setWrapText(true);
        imageBox.getChildren().add(filenameLabel);

        // Action buttons
        HBox buttonsBox = new HBox(5);
        buttonsBox.setAlignment(Pos.CENTER);

        Button fullScreenButton = new Button("\u26F6");
        fullScreenButton.setTooltip(new Tooltip(languageManager.get("tooltip.fullScreen")));
        fullScreenButton.setStyle("-fx-padding: 3 6; -fx-font-size: 10;");
        int idx = allenamento.getImmagini().indexOf(immagine);
        fullScreenButton.setOnAction(e -> openFullScreenViewer(idx));

        Button removeButton = new Button(languageManager.get("button.removeImage"));
        removeButton.setStyle("-fx-padding: 3 6; -fx-font-size: 10;");
        removeButton.setOnAction(e -> {
            if (controller.deleteImage(immagine.getId())) {
                allenamento.removeImmagine(immagine);
                refreshImages();
            }
        });

        buttonsBox.getChildren().addAll(fullScreenButton, removeButton);
        imageBox.getChildren().add(buttonsBox);

        return imageBox;
    }

    /**
     * Opens the fullscreen image viewer.
     *
     * @param startIndex index of the image to start with
     */
    private void openFullScreenViewer(int startIndex) {
        java.util.List<Immagine> immagini = allenamento.getImmagini();
        if (immagini.isEmpty() || startIndex < 0 || startIndex >= immagini.size()) {
            return;
        }

        Stage fullScreenStage = new Stage();
        fullScreenStage.initStyle(StageStyle.UNDECORATED);
        fullScreenStage.setFullScreen(true);
        fullScreenStage.setFullScreenExitHint(languageManager.get("message.pressEscToExit"));

        // Main container with black background
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #000000;");

        // Central ImageView
        ImageView fullImageView = new ImageView();
        fullImageView.setPreserveRatio(true);

        // Adapt to screen dimensions
        javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        fullImageView.setFitWidth(screenBounds.getWidth() * 0.9);
        fullImageView.setFitHeight(screenBounds.getHeight() * 0.85);

        // Current index (use array to be able to mutate in lambda)
        final int[] currentIndex = {startIndex};

        // Image counter label
        Label counterLabel = new Label();
        counterLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-background-color: rgba(0,0,0,0.6); -fx-padding: 6 12; -fx-background-radius: 4;");

        // File name label
        Label fileNameLabel = new Label();
        fileNameLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12; -fx-background-color: rgba(0,0,0,0.6); -fx-padding: 4 10; -fx-background-radius: 4;");

        // Function to load the current image
        Runnable loadCurrentImage = () -> {
            Immagine img = immagini.get(currentIndex[0]);
            File imgFile = ImageManager.resolveImageFile(img.getPath());
            if (imgFile != null && imgFile.exists()) {
                fullImageView.setImage(new Image(imgFile.toURI().toString()));
            } else {
                fullImageView.setImage(null);
            }
            counterLabel.setText((currentIndex[0] + 1) + " / " + immagini.size());
            fileNameLabel.setText(img.getFilename());
        };

        // Load the first image
        loadCurrentImage.run();

        // Navigation buttons
        Button prevButton = new Button("\u25C0");
        prevButton.setStyle("-fx-font-size: 28; -fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-padding: 15 18; -fx-cursor: hand; -fx-background-radius: 30;");
        prevButton.setOnAction(e -> {
            currentIndex[0] = (currentIndex[0] - 1 + immagini.size()) % immagini.size();
            loadCurrentImage.run();
        });

        Button nextButton = new Button("\u25B6");
        nextButton.setStyle("-fx-font-size: 28; -fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-padding: 15 18; -fx-cursor: hand; -fx-background-radius: 30;");
        nextButton.setOnAction(e -> {
            currentIndex[0] = (currentIndex[0] + 1) % immagini.size();
            loadCurrentImage.run();
        });

        // Hide arrows if there's only one image
        if (immagini.size() <= 1) {
            prevButton.setVisible(false);
            nextButton.setVisible(false);
        }

        // Close button
        Button closeButton = new Button("\u2715");
        closeButton.setStyle("-fx-font-size: 20; -fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-padding: 8 14; -fx-cursor: hand; -fx-background-radius: 20;");
        closeButton.setOnAction(e -> fullScreenStage.close());

        // Layout
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: transparent;");

        // Top: close + counter
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(15));
        Region spacerLeft = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        Region spacerRight = new Region();
        HBox.setHgrow(spacerRight, Priority.ALWAYS);
        topBar.getChildren().addAll(spacerLeft, counterLabel, spacerRight, closeButton);

        // Center: image
        StackPane centerPane = new StackPane(fullImageView);
        centerPane.setAlignment(Pos.CENTER);

        // Left/Right: navigation arrows
        StackPane leftPane = new StackPane(prevButton);
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(0, 0, 0, 20));

        StackPane rightPane = new StackPane(nextButton);
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPadding(new Insets(0, 20, 0, 0));

        // Bottom: file name
        HBox bottomBar = new HBox();
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(10));
        bottomBar.getChildren().add(fileNameLabel);

        layout.setTop(topBar);
        layout.setCenter(centerPane);
        layout.setLeft(leftPane);
        layout.setRight(rightPane);
        layout.setBottom(bottomBar);

        root.getChildren().add(layout);

        Scene fullScene = new Scene(root);
        fullScene.setFill(javafx.scene.paint.Color.BLACK);

        // Keyboard navigation
        fullScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                fullScreenStage.close();
            } else if (event.getCode() == KeyCode.LEFT) {
                currentIndex[0] = (currentIndex[0] - 1 + immagini.size()) % immagini.size();
                loadCurrentImage.run();
            } else if (event.getCode() == KeyCode.RIGHT) {
                currentIndex[0] = (currentIndex[0] + 1) % immagini.size();
                loadCurrentImage.run();
            }
        });

        fullScreenStage.setScene(fullScene);
        fullScreenStage.showAndWait();
    }

    /**
     * Adds a new image to the training.
     */
    private void addImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(languageManager.get("message.imageViewer"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Immagini", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Tutti i file", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            if (controller.addImage(allenamento.getId(), selectedFile)) {
                // Reload images
                Allenamento updated = controller.getAllenamento(allenamento.getId());
                allenamento.setImmagini(updated.getImmagini());
                refreshImages();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(languageManager.get("alert.warning"));
                alert.setHeaderText(languageManager.get("alert.imageAdded"));
                alert.setContentText(languageManager.get("alert.imageAdded"));
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(languageManager.get("alert.error"));
                alert.setHeaderText(languageManager.get("alert.error"));
                alert.setContentText(languageManager.get("alert.error"));
                alert.showAndWait();
            }
        }
    }

    /**
     * Deletes the selected image.
     */
    private void deleteSelectedImage() {
        // Simplified implementation - complete version would have multiple selection
    }

    /**
     * Shows the window.
     */
    public void showAndWait() {
        stage.showAndWait();
    }
}
