package com.allenamento.view;

import com.allenamento.controller.AllenamentoController;
import com.allenamento.model.Allenamento;
import com.allenamento.model.Immagine;
import com.allenamento.util.ImageManager;
import com.allenamento.util.LanguageManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Main window of the application.
 * Shows the list of trainings with search and management options.
 */
public class MainWindow extends Application {
    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

    private AllenamentoController controller;
    private LanguageManager languageManager;
    private Stage primaryStage;
    private TableView<Allenamento> allenameentiTable;
    private TextField searchNomeField;
    private TextField searchEtaField;
    private TextField searchTipoField;
    private TextField searchFaseField;
    private TextField searchStrumentoField;
    private Label detailNomeValue;
    private Label detailTipoValue;
    private Label detailEtaValue;
    private Label detailFaseValue;
    private Label detailStrumentoValue;
    private TextArea detailObiettivoArea;
    private TextArea detailDescrizioneArea;
    private FlowPane detailImagesPane;
    private Allenamento currentDetailAllenamento;

    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            languageManager = LanguageManager.getInstance();
            logger.info("Application initialization...");
            
            controller = new AllenamentoController();

            buildUI();
            
            logger.info("Showing main window");
            primaryStage.show();
            
            // On macOS, ensure the window is in foreground
            primaryStage.requestFocus();
            primaryStage.toFront();
            
            // Load initial data in application thread
            Platform.runLater(() -> {
                logger.info("Loading data...");
                refreshAllenamenti();
                logger.info("Application started successfully");
            });

        } catch (Exception e) {
            logger.error("Error starting application", e);
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Errore nell'avvio dell'applicazione");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Builds/rebuilds the complete user interface.
     */
    private void buildUI() {
        // Create language menu before other elements
        MenuBar menuBar = createLanguageMenu();

        primaryStage.setTitle(languageManager.get("app.title"));
        primaryStage.setWidth(1200);
        primaryStage.setHeight(700);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Segoe UI', 'Helvetica'; -fx-font-size: 11;");

        // VBox containing menu + header
        VBox topContainer = new VBox();
        topContainer.getChildren().addAll(menuBar, createHeader());
        root.setTop(topContainer);

        // Center: training table + details on right
        allenameentiTable = createAllenameentiTable();
        root.setCenter(createCenterContent());

        // Bottom: search bar
        VBox searchBar = createSearchBar();
        root.setBottom(searchBar);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    /**
     * Creates the language change menu.
     */
    private MenuBar createLanguageMenu() {
        MenuBar menuBar = new MenuBar();
        
        Menu languageMenu = new Menu(languageManager.get("menu.language"));
        
        MenuItem italianItem = new MenuItem(languageManager.get("menu.italian"));
        italianItem.setOnAction(e -> {
            languageManager.setLanguage("it");
            refreshUIAfterLanguageChange();
        });
        
        MenuItem englishItem = new MenuItem(languageManager.get("menu.english"));
        englishItem.setOnAction(e -> {
            languageManager.setLanguage("en");
            refreshUIAfterLanguageChange();
        });
        
        languageMenu.getItems().addAll(italianItem, englishItem);
        menuBar.getMenus().add(languageMenu);
        
        return menuBar;
    }

    /**
     * Refreshes the UI after a language change.
     */
    private void refreshUIAfterLanguageChange() {
        // Completely rebuilds the UI with new translated texts
        buildUI();
        // Reload data to populate the table
        refreshAllenamenti();
    }

    /**
     * Creates the header with title and main buttons.
     */
    private VBox createHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 15;");
        header.setSpacing(10);

        Label title = new Label(languageManager.get("app.title"));
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);

        Button newButton = new Button(languageManager.get("button.new"));
        newButton.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        newButton.setPrefWidth(150);
        newButton.setOnAction(e -> openNewAllenamentoDialog());

        Button editButton = new Button(languageManager.get("button.edit"));
        editButton.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        editButton.setPrefWidth(100);
        editButton.setOnAction(e -> editSelectedAllenamento());

        Button deleteButton = new Button(languageManager.get("button.delete"));
        deleteButton.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        deleteButton.setPrefWidth(100);
        deleteButton.setStyle("-fx-font-size: 12; -fx-padding: 8; -fx-text-fill: white; -fx-background-color: #dc3545;");
        deleteButton.setOnAction(e -> deleteSelectedAllenamento());

        Button refreshButton = new Button(languageManager.get("button.refresh"));
        refreshButton.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        refreshButton.setPrefWidth(100);
        refreshButton.setOnAction(e -> refreshAllenamenti());

        buttonBox.getChildren().addAll(newButton, editButton, deleteButton, refreshButton);

        header.getChildren().addAll(title, buttonBox);
        return header;
    }

    /**
     * Creates the training table.
     */
    private TableView<Allenamento> createAllenameentiTable() {
        TableView<Allenamento> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                showAllenamentoDetail(newValue);
            }
        });
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Allenamento selected = table.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showAllenamentoDetail(selected);
                }
            }
        });

        // Type column
        TableColumn<Allenamento, String> tipoCol = new TableColumn<>(languageManager.get("label.typeHeader"));
        tipoCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipo()));
        tipoCol.setPrefWidth(120);

        // Training Name column
        TableColumn<Allenamento, String> nomeCol = new TableColumn<>(languageManager.get("label.nameHeader"));
        nomeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNome()));
        nomeCol.setPrefWidth(150);

        // Age column
        TableColumn<Allenamento, String> etaCol = new TableColumn<>(languageManager.get("label.ageHeader"));
        etaCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEta()));
        etaCol.setPrefWidth(100);

        // Training Phase column (notes)
        TableColumn<Allenamento, String> noteCol = new TableColumn<>(languageManager.get("label.phaseHeader"));
        noteCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNote()));
        noteCol.setPrefWidth(180);

        // Equipment column
        TableColumn<Allenamento, String> strumentoCol = new TableColumn<>(languageManager.get("label.equipmentHeader"));
        strumentoCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStrumento()));
        strumentoCol.setPrefWidth(140);

        table.getColumns().addAll(
            tipoCol, nomeCol, etaCol, noteCol, strumentoCol
        );

        return table;
    }

    /**
     * Creates the central content with list on left and details on right.
     */
    private SplitPane createCenterContent() {
        SplitPane splitPane = new SplitPane();

        ScrollPane tableScroll = new ScrollPane(allenameentiTable);
        tableScroll.setFitToWidth(true);
        tableScroll.setFitToHeight(true);

        ScrollPane detailScroll = new ScrollPane(createDetailPanel());
        detailScroll.setFitToWidth(true);
        detailScroll.setFitToHeight(true);

        splitPane.getItems().addAll(tableScroll, detailScroll);
        splitPane.setDividerPositions(0.62);
        return splitPane;
    }

    /**
     * Creates the detail panel shown on the right of the list.
     */
    private VBox createDetailPanel() {
        VBox panel = new VBox();
        panel.setSpacing(8);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f7f7f7;");

        ScrollPane mainScroll = new ScrollPane();
        mainScroll.setFitToWidth(true);

        VBox scrollContent = new VBox();
        scrollContent.setSpacing(8);
        scrollContent.setPadding(new Insets(5));

        // Title
        Label title = new Label(languageManager.get("label.trainingDetails"));
        title.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        // Base information grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(6);

        detailTipoValue = new Label("-");
        detailNomeValue = new Label(languageManager.get("label.selectTraining"));
        detailEtaValue = new Label("-");
        detailFaseValue = new Label("-");
        detailStrumentoValue = new Label("-");

        grid.add(new Label(languageManager.get("label.type")), 0, 0);
        grid.add(detailTipoValue, 1, 0);
        grid.add(new Label(languageManager.get("label.name")), 0, 1);
        grid.add(detailNomeValue, 1, 1);
        grid.add(new Label(languageManager.get("label.age")), 0, 2);
        grid.add(detailEtaValue, 1, 2);
        grid.add(new Label(languageManager.get("label.phase")), 0, 3);
        grid.add(detailFaseValue, 1, 3);
        grid.add(new Label(languageManager.get("label.equipment")), 0, 4);
        grid.add(detailStrumentoValue, 1, 4);

        // Objective
        Label obiettivoLabel = new Label(languageManager.get("label.objective"));
        obiettivoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");
        detailObiettivoArea = new TextArea();
        detailObiettivoArea.setEditable(false);
        detailObiettivoArea.setWrapText(true);
        detailObiettivoArea.setPrefHeight(60);

        // Description
        Label descrizioneLabel = new Label(languageManager.get("label.description"));
        descrizioneLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");
        detailDescrizioneArea = new TextArea();
        detailDescrizioneArea.setEditable(false);
        detailDescrizioneArea.setWrapText(true);
        detailDescrizioneArea.setPrefHeight(60);

        // Images
        Label imagesTitle = new Label(languageManager.get("label.images"));
        imagesTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");

        HBox imageButtonBox = new HBox(8);
        imageButtonBox.setPadding(new Insets(5, 0, 0, 0));

        Button addImageBtn = new Button(languageManager.get("button.addImage"));
        addImageBtn.setStyle("-fx-font-size: 10; -fx-padding: 5;");
        addImageBtn.setOnAction(e -> addImageToCurrentAllenamento());

        Button deleteImageBtn = new Button(languageManager.get("button.removeImage"));
        deleteImageBtn.setStyle("-fx-font-size: 10; -fx-padding: 5; -fx-text-fill: white; -fx-background-color: #dc3545;");
        deleteImageBtn.setOnAction(e -> deleteSelectedImage());

        imageButtonBox.getChildren().addAll(addImageBtn, deleteImageBtn);

        detailImagesPane = new FlowPane();
        detailImagesPane.setHgap(8);
        detailImagesPane.setVgap(8);
        detailImagesPane.setPrefWrapLength(300);
        detailImagesPane.setStyle("-fx-border: 1px dashed #ccc; -fx-padding: 8; -fx-background-color: #fafafa;");
        detailImagesPane.setPrefHeight(150);

        scrollContent.getChildren().addAll(
            title, grid,
            new Separator(),
            obiettivoLabel, detailObiettivoArea,
            descrizioneLabel, detailDescrizioneArea,
            new Separator(),
            imagesTitle, imageButtonBox, detailImagesPane
        );

        mainScroll.setContent(scrollContent);
        panel.getChildren().add(mainScroll);
        VBox.setVgrow(mainScroll, Priority.ALWAYS);
        return panel;
    }

    /**
     * Adds an image to the currently displayed training.
     */
    private void addImageToCurrentAllenamento() {
        if (currentDetailAllenamento == null) {
            showAlert(Alert.AlertType.WARNING, languageManager.get("alert.warning"), languageManager.get("message.selectTraining"), "");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(languageManager.get("message.imageViewer"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Immagini", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Tutti i file", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            if (controller.addImage(currentDetailAllenamento.getId(), selectedFile)) {
                Allenamento updated = controller.getAllenamento(currentDetailAllenamento.getId());
                currentDetailAllenamento.setImmagini(updated.getImmagini());
                refreshDetailImages();
                showAlert(Alert.AlertType.INFORMATION, languageManager.get("alert.warning"), languageManager.get("alert.imageAdded"), "");
            } else {
                showAlert(Alert.AlertType.ERROR, languageManager.get("alert.error"), languageManager.get("alert.error"), "");
            }
        }
    }

    /**
     * Refreshes the image display in the detail panel.
     */
    private void refreshDetailImages() {
        detailImagesPane.getChildren().clear();

        if (currentDetailAllenamento == null) {
            return;
        }

        for (Immagine immagine : currentDetailAllenamento.getImmagini()) {
            VBox imageBox = createDetailImageBox(immagine);
            detailImagesPane.getChildren().add(imageBox);
        }

        if (currentDetailAllenamento.getImmagini().isEmpty()) {
            Label noImagesLabel = new Label(languageManager.get("message.noImages"));
            noImagesLabel.setStyle("-fx-text-fill: #999; -fx-font-style: italic; -fx-font-size: 10;");
            detailImagesPane.getChildren().add(noImagesLabel);
        }
    }

    /**
     * Creates the box for a single image in the panel.
     */
    private VBox createDetailImageBox(Immagine immagine) {
        VBox imageBox = new VBox();
        imageBox.setSpacing(3);
        imageBox.setStyle("-fx-border: 1px solid #ddd; -fx-padding: 6; -fx-background-color: white;");
        imageBox.setPrefWidth(90);
        imageBox.setAlignment(Pos.CENTER);

        try {
            File imageFile = ImageManager.resolveImageFile(immagine.getPath());
            if (imageFile != null && imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
                imageView.setCursor(Cursor.HAND);

                // Click to open fullscreen
                int imageIndex = currentDetailAllenamento.getImmagini().indexOf(immagine);
                imageView.setOnMouseClicked(e -> openFullScreenImageViewer(imageIndex));

                imageBox.getChildren().add(imageView);
            } else {
                Label errorLabel = new Label("X");
                errorLabel.setStyle("-fx-font-size: 24; -fx-text-fill: #cc0000;");
                imageBox.getChildren().add(errorLabel);
            }
        } catch (Exception e) {
            Label errorLabel = new Label("?");
            errorLabel.setStyle("-fx-font-size: 24; -fx-text-fill: #cc0000;");
            imageBox.getChildren().add(errorLabel);
        }

        Button removeBtn = new Button("X");
        removeBtn.setStyle("-fx-font-size: 9; -fx-padding: 1 4;");
        removeBtn.setOnAction(e -> {
            if (controller.deleteImage(immagine.getId()) && currentDetailAllenamento != null) {
                currentDetailAllenamento.removeImmagine(immagine);
                refreshDetailImages();
            }
        });

        imageBox.getChildren().add(removeBtn);
        return imageBox;
    }

    /**
     * Deletes image (placeholder).
     */
    private void deleteSelectedImage() {
        // Implementation in next step if multiple selection is needed
    }

    /**
     * Creates the search bar.
     */
    private VBox createSearchBar() {
        VBox searchBar = new VBox();
        searchBar.setStyle("-fx-background-color: #f9f9f9; -fx-border-top: 1px solid #ddd; -fx-padding: 10;");
        searchBar.setSpacing(8);

        Label searchLabel = new Label(languageManager.get("label.searchFilters"));
        searchLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        HBox filterBox = new HBox();
        filterBox.setSpacing(10);
        filterBox.setPadding(new Insets(5, 0, 0, 0));
        filterBox.setStyle("-fx-alignment: center-left;");

        Label tipoLabel = new Label(languageManager.get("label.type"));
        searchTipoField = new TextField();
        searchTipoField.setPromptText(languageManager.get("placeholder.searchType"));
        searchTipoField.setPrefWidth(100);

        Label nomeLabel = new Label(languageManager.get("label.trainingName"));
        searchNomeField = new TextField();
        searchNomeField.setPromptText(languageManager.get("placeholder.searchName"));
        searchNomeField.setPrefWidth(120);

        Label etaLabel = new Label(languageManager.get("label.age"));
        searchEtaField = new TextField();
        searchEtaField.setPromptText(languageManager.get("placeholder.searchAge"));
        searchEtaField.setPrefWidth(80);

        Label faseLabel = new Label(languageManager.get("label.trainingPhase"));
        searchFaseField = new TextField();
        searchFaseField.setPromptText(languageManager.get("placeholder.searchPhase"));
        searchFaseField.setPrefWidth(120);

        Label strumentoLabel = new Label(languageManager.get("label.equipment"));
        searchStrumentoField = new TextField();
        searchStrumentoField.setPromptText(languageManager.get("placeholder.searchEquipment"));
        searchStrumentoField.setPrefWidth(120);

        Button searchButton = new Button(languageManager.get("button.search"));
        searchButton.setStyle("-fx-font-size: 11; -fx-padding: 5;");
        searchButton.setPrefWidth(70);
        searchButton.setOnAction(e -> performSearch());

        Button clearButton = new Button(languageManager.get("button.clearFilters"));
        clearButton.setStyle("-fx-font-size: 11; -fx-padding: 5;");
        clearButton.setPrefWidth(110);
        clearButton.setOnAction(e -> clearFilters());

        filterBox.getChildren().addAll(
            tipoLabel, searchTipoField,
            nomeLabel, searchNomeField,
            etaLabel, searchEtaField,
            faseLabel, searchFaseField,
            strumentoLabel, searchStrumentoField,
            searchButton, clearButton
        );

        searchBar.getChildren().addAll(searchLabel, filterBox);
        return searchBar;
    }

    /**
     * Refreshes the table with all trainings.
     */
    private void refreshAllenamenti() {
        List<Allenamento> allenamenti = controller.getAllAllenamenti();
        allenameentiTable.getItems().clear();
        allenameentiTable.getItems().addAll(allenamenti);

        if (!allenamenti.isEmpty()) {
            allenameentiTable.getSelectionModel().selectFirst();
            showAllenamentoDetail(allenamenti.get(0));
        } else {
            clearDetailPanel();
        }
    }

    /**
     * Opens a dialog for creating a new training.
     */
    private void openNewAllenamentoDialog() {
        try {
            AllenamentoDialog dialog = new AllenamentoDialog(null, languageManager);
            java.util.Optional<Allenamento> result = dialog.showAndWait();
            if (result.isPresent()) {
                Allenamento allenamento = result.get();
                logger.info("Attempting to create training: " + allenamento.getNome());
                int id = controller.createAllenamento(
                        allenamento.getNome(),
                        allenamento.getEta(),
                        allenamento.getTipo(),
                        allenamento.getObiettivo(),
                        allenamento.getDescrizione(),
                        allenamento.getNote(),
                        allenamento.getStrumento()
                );
                if (id > 0) {
                    showAlert(Alert.AlertType.INFORMATION, languageManager.get("alert.warning"), languageManager.get("alert.createSuccess"), "");
                    refreshAllenamenti();
                } else {
                    showAlert(Alert.AlertType.ERROR, languageManager.get("alert.error"), languageManager.get("alert.error"), "");
                }
            }
        } catch (Exception e) {
            logger.error("Error creating training", e);
            showAlert(Alert.AlertType.ERROR, languageManager.get("alert.error"), languageManager.get("alert.error"), e.toString());
        }
    }

    /**
     * Edits the selected training.
     */
    private void editSelectedAllenamento() {
        Allenamento selected = allenameentiTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, languageManager.get("alert.warning"), languageManager.get("message.selectTraining"), "");
            return;
        }

        Allenamento loaded = controller.getAllenamento(selected.getId());
        AllenamentoDialog dialog = new AllenamentoDialog(loaded, languageManager);
        java.util.Optional<Allenamento> result = dialog.showAndWait();
        if (result.isPresent()) {
            Allenamento allenamento = result.get();
            allenamento.setId(loaded.getId());
            if (controller.updateAllenamento(allenamento)) {
                showAlert(Alert.AlertType.INFORMATION, languageManager.get("alert.warning"), languageManager.get("alert.updateSuccess"), "");
                refreshAllenamenti();
            } else {
                showAlert(Alert.AlertType.ERROR, languageManager.get("alert.error"), languageManager.get("alert.error"), "");
            }
        }
    }

    /**
     * Deletes the selected training.
     */
    private void deleteSelectedAllenamento() {
        Allenamento selected = allenameentiTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, languageManager.get("alert.warning"), languageManager.get("message.selectTraining"), "");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle(languageManager.get("alert.warning"));
        confirmation.setHeaderText(languageManager.get("alert.deleteConfirm"));
        confirmation.setContentText(languageManager.get("alert.deleteConfirm") + " " + selected.getNome() + "?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (controller.deleteAllenamento(selected.getId())) {
                showAlert(Alert.AlertType.INFORMATION, languageManager.get("alert.warning"), languageManager.get("alert.deleteSuccess"), "");
                refreshAllenamenti();
            } else {
                showAlert(Alert.AlertType.ERROR, languageManager.get("alert.error"), languageManager.get("alert.error"), "");
            }
        }
    }

    /**
     * Shows the details of a training.
     */
    private void showAllenamentoDetail(Allenamento allenamento) {
        Allenamento loaded = controller.getAllenamento(allenamento.getId());
        if (loaded == null) {
            clearDetailPanel();
            return;
        }

        currentDetailAllenamento = loaded;
        detailTipoValue.setText(safeValue(loaded.getTipo()));
        detailNomeValue.setText(safeValue(loaded.getNome()));
        detailEtaValue.setText(safeValue(loaded.getEta()));
        detailFaseValue.setText(safeValue(loaded.getNote()));
        detailStrumentoValue.setText(safeValue(loaded.getStrumento()));
        detailObiettivoArea.setText(safeValue(loaded.getObiettivo()));
        detailDescrizioneArea.setText(safeValue(loaded.getDescrizione()));
        refreshDetailImages();
    }

    private void clearDetailPanel() {
        currentDetailAllenamento = null;
        detailTipoValue.setText("-");
        detailNomeValue.setText(languageManager.get("label.selectTraining"));
        detailEtaValue.setText("-");
        detailFaseValue.setText("-");
        detailStrumentoValue.setText("-");
        detailObiettivoArea.clear();
        detailDescrizioneArea.clear();
        detailImagesPane.getChildren().clear();
    }

    private String safeValue(String value) {
        return value == null ? "" : value;
    }

    /**
     * Performs the search with specified filters.
     */
    private void performSearch() {
        String tipo = searchTipoField.getText().isEmpty() ? null : searchTipoField.getText();
        String nome = searchNomeField.getText().isEmpty() ? null : searchNomeField.getText();
        String eta = searchEtaField.getText().isEmpty() ? null : searchEtaField.getText();
        String fase = searchFaseField.getText().isEmpty() ? null : searchFaseField.getText();
        String strumento = searchStrumentoField.getText().isEmpty() ? null : searchStrumentoField.getText();

        List<Allenamento> results = controller.searchAllenamenti(tipo, nome, null, eta, fase, strumento, null);
        allenameentiTable.getItems().clear();
        allenameentiTable.getItems().addAll(results);
    }

    /**
     * Clears all search filters.
     */
    private void clearFilters() {
        searchTipoField.clear();
        searchNomeField.clear();
        searchEtaField.clear();
        searchFaseField.clear();
        searchStrumentoField.clear();
        refreshAllenamenti();
    }

    /**
     * Shows an alert to the user.
     */
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Opens the fullscreen image viewer.
     */
    private void openFullScreenImageViewer(int startIndex) {
        if (currentDetailAllenamento == null || currentDetailAllenamento.getImmagini().isEmpty()) {
            return;
        }

        java.util.List<Immagine> immagini = currentDetailAllenamento.getImmagini();
        if (startIndex < 0 || startIndex >= immagini.size()) {
            startIndex = 0;
        }

        Stage fullScreenStage = new Stage();
        fullScreenStage.setTitle(languageManager.get("message.imageViewer"));
        fullScreenStage.setFullScreen(true);
        fullScreenStage.setFullScreenExitHint(languageManager.get("message.pressEscToExit"));

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #000000;");

        ImageView fullImageView = new ImageView();
        fullImageView.setPreserveRatio(true);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        fullImageView.setFitWidth(screenBounds.getWidth() * 0.9);
        fullImageView.setFitHeight(screenBounds.getHeight() * 0.85);

        final int[] currentIndex = {startIndex};

        Label counterLabel = new Label();
        counterLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-background-color: rgba(0,0,0,0.6); -fx-padding: 6 12; -fx-background-radius: 4;");

        Label fileNameLabel = new Label();
        fileNameLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12; -fx-background-color: rgba(0,0,0,0.6); -fx-padding: 4 10; -fx-background-radius: 4;");

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

        loadCurrentImage.run();

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

        if (immagini.size() <= 1) {
            prevButton.setVisible(false);
            nextButton.setVisible(false);
        }

        Button closeButton = new Button("\u2715");
        closeButton.setStyle("-fx-font-size: 20; -fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-padding: 8 14; -fx-cursor: hand; -fx-background-radius: 20;");
        closeButton.setOnAction(e -> fullScreenStage.close());

        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: transparent;");

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(15));
        Region spacerLeft = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        Region spacerRight = new Region();
        HBox.setHgrow(spacerRight, Priority.ALWAYS);
        topBar.getChildren().addAll(spacerLeft, counterLabel, spacerRight, closeButton);

        StackPane centerPane = new StackPane(fullImageView);
        centerPane.setAlignment(Pos.CENTER);

        StackPane leftPane = new StackPane(prevButton);
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(0, 0, 0, 20));

        StackPane rightPane = new StackPane(nextButton);
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPadding(new Insets(0, 20, 0, 0));

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

    public static void main(String[] args) {
        launch(args);
    }
}
