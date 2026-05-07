import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.util.concurrent.CompletableFuture;

public class Main extends Application {

    private Chatbot bot;
    private VBox messageContainer;
    private ScrollPane scrollPane;
    private TextField inputField;
    private Button sendButton;
    private Button resetButton;
    private Label statusLabel;

    // Colors
    private static final String PRIMARY_BLUE   = "#c01515";
    private static final String LIGHT_BLUE     = "#E3F2FD";
    private static final String BOT_BUBBLE     = "#F1F3F4";
    private static final String USER_BUBBLE    = "#1565C0";
    private static final String BG_COLOR       = "#FAFAFA";
    private static final String HEADER_COLOR   = "#c01515";

    @Override
    public void start(Stage stage) {
        // Add this after: public void start(Stage stage) {
             Image icon = new Image("file:images/QCU-logo.png");
            stage.getIcons().add(icon);
        try {
            bot = new Chatbot();
        } catch (Exception e) {
            showErrorAlert("Startup Error", e.getMessage());
            Platform.exit();
            return;
        }

        // ── Header ──────────────────────────────────────────
        // Logo image
        ImageView logo = new ImageView(new Image("file:images/QCU-logo.png"));
        logo.setFitWidth(40);
        logo.setFitHeight(40);

Label schoolName = new Label("Quezon City University");
        schoolName.setFont(Font.font("System", FontWeight.BOLD, 16));
        schoolName.setTextFill(Color.WHITE);

        Label subtitle = new Label("School Information Assistant");
        subtitle.setFont(Font.font("System", 12));
        subtitle.setTextFill(Color.web("#ffffff"));

        VBox headerText = new VBox(2, schoolName, subtitle);
        headerText.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label("● Online");
        statusLabel.setFont(Font.font("System", 11));
        statusLabel.setTextFill(Color.web("#A5D6A7"));

        resetButton = new Button("↺ Reset");
        resetButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-border-color: white;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-font-size: 11px;" +
            "-fx-cursor: hand;"
        );
        resetButton.setOnAction(e -> resetChat());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(12, logo, headerText, spacer, statusLabel, resetButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 20, 14, 20));
        header.setStyle("-fx-background-color: " + HEADER_COLOR + ";");

        // ── Chat Area ────────────────────────────────────────
        messageContainer = new VBox(10);
        messageContainer.setPadding(new Insets(16));
        messageContainer.setStyle("-fx-background-color: " + BG_COLOR + ";");

        scrollPane = new ScrollPane(messageContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background: " + BG_COLOR + ";" +
            "-fx-background-color: " + BG_COLOR + ";" +
            "-fx-border-color: transparent;"
        );
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Welcome message
        addBotMessage("Hello! 👋 Welcome to Quezon City University. I can help you with:\n• School hours & schedule\n• Enrollment & admissions\n• Programs & facilities\n• Staff information\n\nHow can I help you today?");

        // ── Quick Chips ──────────────────────────────────────
        HBox chips = new HBox(8,
            makeChip("School Hours"),
            makeChip("Enrollment"),
            makeChip("Programs"),
            makeChip("Tuition"),
            makeChip("Staff")
        );
        chips.setPadding(new Insets(8, 16, 8, 16));
        chips.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // ── Input Row ────────────────────────────────────────
        inputField = new TextField();
        inputField.setPromptText("Ask about the school...");
        inputField.setFont(Font.font("System", 13));
        inputField.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #DADCE0;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 16 8 16;"
        );
        inputField.setOnAction(e -> sendMessage());
        HBox.setHgrow(inputField, Priority.ALWAYS);

        sendButton = new Button("➤");
        sendButton.setFont(Font.font("System", FontWeight.BOLD, 14));
        sendButton.setStyle(
            "-fx-background-color: " + PRIMARY_BLUE + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 50%;" +
            "-fx-min-width: 38px;" +
            "-fx-min-height: 38px;" +
            "-fx-max-width: 38px;" +
            "-fx-max-height: 38px;" +
            "-fx-cursor: hand;"
        );
        sendButton.setOnAction(e -> sendMessage());

        HBox inputRow = new HBox(10, inputField, sendButton);
        inputRow.setAlignment(Pos.CENTER);
        inputRow.setPadding(new Insets(10, 16, 14, 16));
        inputRow.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #E0E0E0;" +
            "-fx-border-width: 1 0 0 0;"
        );

        // ── Root Layout ──────────────────────────────────────
        VBox root = new VBox(header, scrollPane, chips, inputRow);
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        Scene scene = new Scene(root, 480, 640);
        stage.setScene(scene);
        stage.setTitle("Quezon City University Chatbot");
        stage.setMinWidth(400);
        stage.setMinHeight(500);
        stage.show();

        inputField.requestFocus();
    }

    // ── Send Message ─────────────────────────────────────────
    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isBlank() || !sendButton.isDisable() == false) return;

        inputField.clear();
        addUserMessage(text);
        setLoading(true);

        CompletableFuture.supplyAsync(() -> {
            try {
                return bot.chat(text);
            } catch (Exception e) {
                return "ERROR: " + e.getMessage();
            }
        }).thenAccept(reply -> Platform.runLater(() -> {
            setLoading(false);
            if (reply.startsWith("ERROR:")) {
                addBotMessage("⚠️ " + reply.substring(6));
            } else {
                addBotMessage(reply);
            }
        }));
    }

    // ── Add User Bubble ──────────────────────────────────────
    private void addUserMessage(String text) {
        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.setMaxWidth(300);
        bubble.setFont(Font.font("System", 13));
        bubble.setTextFill(Color.WHITE);
        bubble.setStyle(
            "-fx-background-color: " + USER_BUBBLE + ";" +
            "-fx-background-radius: 16 16 4 16;" +
            "-fx-padding: 10 14 10 14;"
        );

        HBox row = new HBox(bubble);
        row.setAlignment(Pos.CENTER_RIGHT);
        row.setPadding(new Insets(2, 4, 2, 60));
        messageContainer.getChildren().add(row);
        scrollToBottom();
    }

    // ── Add Bot Bubble ───────────────────────────────────────
    private void addBotMessage(String text) {
        ImageView icon = new ImageView(new Image("file:images/QCU-logo.png"));
        icon.setFitWidth(32);
        icon.setFitHeight(32);

// Make it circular
        Circle clip = new Circle(16, 16, 16);
        icon.setClip(clip);

        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.setMaxWidth(300);
        bubble.setFont(Font.font("System", 13));
        bubble.setTextFill(Color.web("#202124"));
        bubble.setStyle(
            "-fx-background-color: " + BOT_BUBBLE + ";" +
            "-fx-background-radius: 16 16 16 4;" +
            "-fx-padding: 10 14 10 14;" +
            "-fx-border-color: #E0E0E0;" +
            "-fx-border-radius: 16 16 16 4;" +
            "-fx-border-width: 1;"
        );

        HBox row = new HBox(8, icon, bubble);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(2, 60, 2, 4));
        messageContainer.getChildren().add(row);
        scrollToBottom();
    }

    // ── Typing Indicator ─────────────────────────────────────
    private HBox typingIndicator;

    private void setLoading(boolean loading) {
        sendButton.setDisable(loading);
        inputField.setDisable(loading);

        if (loading) {
            ImageView icon = new ImageView(new Image("file:images/QCU-logo.png"));
            icon.setFitWidth(32);
            icon.setFitHeight(32);

            // Make it circular
            Circle clip = new Circle(16, 16, 16);
            icon.setClip(clip);

            Label dots = new Label("● ● ●");
            dots.setFont(Font.font("System", 13));
            dots.setTextFill(Color.web("#9AA0A6"));
            dots.setStyle(
                "-fx-background-color: " + BOT_BUBBLE + ";" +
                "-fx-background-radius: 16;" +
                "-fx-padding: 10 14 10 14;" +
                "-fx-border-color: #E0E0E0;" +
                "-fx-border-radius: 16;" +
                "-fx-border-width: 1;"
            );

            typingIndicator = new HBox(8, icon, dots);
            typingIndicator.setAlignment(Pos.CENTER_LEFT);
            typingIndicator.setPadding(new Insets(2, 60, 2, 4));
            messageContainer.getChildren().add(typingIndicator);
            scrollToBottom();
        } else {
            if (typingIndicator != null) {
                messageContainer.getChildren().remove(typingIndicator);
                typingIndicator = null;
            }
        }
    }

    // ── Quick Chip Button ────────────────────────────────────
    private Button makeChip(String label) {
        Button chip = new Button(label);
        chip.setFont(Font.font("System", 11));
        chip.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: #1565C0;" +
            "-fx-border-color: #BBDEFB;" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-padding: 4 12 4 12;" +
            "-fx-cursor: hand;"
        );
        chip.setOnMouseEntered(e -> chip.setStyle(
            "-fx-background-color: #E3F2FD;" +
            "-fx-text-fill: #1565C0;" +
            "-fx-border-color: #1565C0;" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-padding: 4 12 4 12;" +
            "-fx-cursor: hand;"
        ));
        chip.setOnMouseExited(e -> chip.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: #1565C0;" +
            "-fx-border-color: #BBDEFB;" +
            "-fx-border-radius: 16;" +
            "-fx-background-radius: 16;" +
            "-fx-padding: 4 12 4 12;" +
            "-fx-cursor: hand;"
        ));
        chip.setOnAction(e -> {
            inputField.setText(label);
            sendMessage();
        });
        return chip;
    }

    // ── Reset Chat ───────────────────────────────────────────
    private void resetChat() {
        bot.resetHistory();
        messageContainer.getChildren().clear();
        addBotMessage("Chat reset! How can I help you? 😊");
    }

    // ── Scroll to Bottom ─────────────────────────────────────
    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    // ── Error Alert ──────────────────────────────────────────
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
