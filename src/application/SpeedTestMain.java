package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SpeedTestMain extends Application {

    private Label resultLabel;
    private ProgressIndicator progressIndicator;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Network Speed Test");

        Button startButton = new Button("Bắt đầu đo");
        startButton.getStyleClass().add("button-style");
        startButton.setOnAction(e -> startSpeedTest());

        resultLabel = new Label();
        resultLabel.getStyleClass().add("result-label");

        progressIndicator = new ProgressIndicator();
        progressIndicator.getStyleClass().add("progress-indicator");
        progressIndicator.setVisible(false); // Không hiển thị lúc đầu

        VBox root = new VBox(10);
        root.getStyleClass().add("main-container");
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(resultLabel, startButton, progressIndicator);

        Scene scene = new Scene(root, 500, 500);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> Platform.exit());

        primaryStage.show();
    }

    private void startSpeedTest() {
        resultLabel.setText("Đang đo tốc độ mạng...");

        Task<Void> speedTestTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    // Hiển thị ProgressIndicator khi bắt đầu công việc
                    Platform.runLater(() -> progressIndicator.setVisible(true));

                    // Thực hiện lệnh đo tốc độ mạng
                    Process process = Runtime.getRuntime().exec("speedtest-cli");

                    // Đọc kết quả từ quá trình đo tốc độ mạng
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    StringBuilder output = new StringBuilder();
                    String line;

                    // Giảm độ dài của thông tin để hiển thị một số thông tin cơ bản
                    int lineCount = 0;
                    while ((line = reader.readLine()) != null && lineCount < 10) {
                        output.append(line).append("\n");
                        lineCount++;
                    }

                    // Hiển thị kết quả lên giao diện người dùng
                    Platform.runLater(() -> {
                        resultLabel.setText(output.toString());

                        // Ẩn ProgressIndicator khi công việc hoàn thành
                        progressIndicator.setVisible(false);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> resultLabel.setText("Có lỗi xảy ra khi đo tốc độ mạng."));
                }
                return null;
            }
        };

        // Thực hiện công việc khi kết thúc
        speedTestTask.setOnSucceeded(event -> showCompletionAlert());

        // Khởi động Thread cho Task
        Thread thread = new Thread(speedTestTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void showCompletionAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hoàn thành");
        alert.setHeaderText("Quá trình đo tốc độ mạng đã hoàn thành!");
        alert.setContentText("Thông báo chi tiết về tốc độ mạng có thể hiển thị ở đây.");
        alert.showAndWait();
    }
}
