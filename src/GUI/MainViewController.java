package GUI;

import BE.Images;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class MainViewController {
    @FXML
    private ImageView iVImages;
    @FXML
    private TableView<Images> tbVImageView;
    @FXML
    private TableColumn<Images, String> imageNameCol, imagePathCol;
    @FXML
    private Button btnLoadImage,btnNext,btnPrevious, btnStart, btnStop, btnCountPixels;
    private Thread showThread;

    @FXML
    private ObservableList<Images> imageList = FXCollections.observableArrayList();;
    private int currentIndex = -1;
    @FXML
    private Label lblRed, lblGreen, lblBlue;



    @FXML
    private void initialize(){
        String pathToImages = "resources/images";
        imageNameCol.setCellValueFactory(new PropertyValueFactory<>("imageName"));
        imagePathCol.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        loadImagesDetails(pathToImages);
        if (!imageList.isEmpty()) {
            currentIndex = 0;
            showImage();
        }
    }



    private void loadImagesDetails(String details){
        File folder = new File(details);
        File[] listOfImages = folder.listFiles();
        imageList.clear();

        if (listOfImages != null) {
            for (File file : listOfImages) {
                if (file.isFile()) {
                    String imageName = file.getName();
                    String imagePath = file.getAbsolutePath();

                    if (imageName.endsWith(".png") || imageName.endsWith(".jpeg") || imageName.endsWith(".jpg")) {
                        imageList.add(new Images(imageName, imagePath));
                    }
                }
            }
        }
        tbVImageView.setItems(imageList);
    }

    @FXML
    private void loadImageToData(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            String fileName = selectedFile.getName();
            File targetFolder = new File("resources/images");
            if (!targetFolder.exists()) {
                boolean dirsMade = targetFolder.mkdirs();
                if (!dirsMade) {
                    System.err.println("Failed to create target directory: images");
                    return;
                }
            }
            File targetFile = new File(targetFolder, fileName);
            try {
                Files.copy(selectedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                loadImagesDetails("resources/images");
            } catch (Exception e) {
                e.printStackTrace(); // In real scenario, use a logger or a dedicated exception handler
            }
        }
    }


    private void showImage(){
        if (currentIndex >= 0 && currentIndex < imageList.size()) {
            Images currentImage = imageList.get(currentIndex);
            Image image = new Image(new File(currentImage.getImagePath()).toURI().toString());
            iVImages.setImage(image);
        }
    }


    @FXML
    private void goToPreviousPicture(ActionEvent actionEvent) {
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = imageList.size() - 1; // Reset
        }
        showImage();
    }

    @FXML
    private void goToNextPicture(ActionEvent actionEvent) {
        if (currentIndex < imageList.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0; // Reset
        }
        showImage();
    }

    @FXML
    private void startSlidshow(ActionEvent actionEvent){
        // initiaere vores tråd.
        if (showThread == null || showThread.isAlive()){
            Runnable slideshow = () -> {
                try {
                    //så længe der er billeder der kan vises kører den
                    while (!Thread.currentThread().isInterrupted()) {
                        Platform.runLater(() -> goToNextPicture(null));
                        //1.5 sek forsinkelse
                        Thread.sleep(1500);
                    }
                } catch (InterruptedException ie){
                    //hvis den ikke kan, så bliver den stoppet, så while loopet kan brydes.
                    Thread.currentThread().interrupt();
                }
            };
            showThread = new Thread(slideshow);
            showThread.start();
        }
    }

    @FXML
    private void stopSlideshow(){
        if (showThread != null){
            showThread.interrupt();
        }
    }

    private void colorCounter(Image image){
        new Thread(() -> {
           int redPixels = 0, greenPixels = 0, bluePixels = 0;
            PixelReader pReader = image.getPixelReader();
            if (pReader != null) {
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        Color color = pReader.getColor(x, y);
                        double red = color.getRed();
                        double green = color.getGreen();
                        double blue = color.getBlue();
                        if (red > green && red > blue){
                            redPixels++;
                        } else if (blue > green && blue > red) {
                            bluePixels++;
                        } else if (green > red && green > blue) {
                            bluePixels++;
                        }
                    }
                }
        }
            final int finalRedPixels = redPixels;
            final int finalGreenPixels = greenPixels;
            final int finalBluePixels = bluePixels;

            Platform.runLater(() ->{
                lblRed.setText("Red pixels: " + finalRedPixels);
                lblBlue.setText("Blue pixels: " + finalBluePixels);
                lblGreen.setText("Green pixels: " + finalGreenPixels);
            });
        }).start();
    }

    @FXML
    private void startCounting(ActionEvent actionEvent) {
        Image currentImage = iVImages.getImage();
        if (currentImage != null){
            colorCounter(currentImage);
        }
    }
}
