package GUI;

import BE.Image;
import BLL.ImageManager;
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
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class MainViewController {
    ImageManager imageManager = new ImageManager();
    @FXML
    private ImageView iVImages;
    @FXML
    private TableView<Image> tbVImageView;
    @FXML
    private TableColumn<Image, String> imageNameCol, imagePathCol;
    @FXML
    private Button btnLoadImage,btnNext,btnPrevious, btnStart, btnStop, btnCountPixels;


    @FXML
    private ObservableList<Image> imageList = FXCollections.observableArrayList();;
    private int currentIndex = -1;
    @FXML
    private Label lblRed, lblGreen, lblBlue;



    @FXML
    private void initialize(){
        String pathToImages = "resources/images";
        imageNameCol.setCellValueFactory(new PropertyValueFactory<>("imageName"));
        imagePathCol.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        imageManager.loadImagesFromDirectory(pathToImages);
        tbVImageView.setItems(imageManager.getImages());
        if (!imageList.isEmpty()){
            currentIndex = 0;
            showImage();
        }
        btnStop.setVisible(false);
    }



    private void loadImagesDetails(String details){
        File folder = new File(details);
        File[] listOfImages = folder.listFiles();
        ObservableList<Image> tempImageList = FXCollections.observableArrayList();


        if (listOfImages != null) {
            for (File file : listOfImages) {
                if (file.isFile()) {
                    String imageName = file.getName();
                    String imagePath = file.getAbsolutePath();

                    if (imageName.endsWith(".png") || imageName.endsWith(".jpeg") || imageName.endsWith(".jpg")) {
                        tempImageList.add(new Image(imageName, imagePath));
                    }
                }
            }
        }
        Platform.runLater(()-> {
            imageList.clear();
            imageList.addAll(tempImageList);
            tbVImageView.setItems(imageList);
        });
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
            Image currentImage = imageList.get(currentIndex);
            javafx.scene.image.Image image = new javafx.scene.image.Image(new File(currentImage.getImagePath()).toURI().toString());
            iVImages.setImage(image);
        }
    }

    private void presentPicture(Image image) {
        if (image != null) {
            javafx.scene.image.Image fxImage = new javafx.scene.image.Image(new File(image.getImagePath()).toURI().toString());
            iVImages.setImage(fxImage);
        }
    }



    @FXML
    private void goToPreviousPicture(ActionEvent actionEvent) {
        Image image = imageManager.goToPreviousPicture();
        if (image != null){
            presentPicture(image);
        }
    }

    @FXML
    private void goToNextPicture(ActionEvent actionEvent) {
        Image image = imageManager.goToNextPicture();
        if (image != null){
           presentPicture(image);
        }
    }

    @FXML
    private void startSlidshow(ActionEvent actionEvent){
        imageManager.startSlidshow();
        btnStop.setVisible(true);

    }

    @FXML
    private void stopSlideshow(){
        imageManager.stopSlideshow();
        btnStop.setVisible(false);
    }


    @FXML
    private void startCounting(ActionEvent actionEvent) {
        javafx.scene.image.Image currentImage = iVImages.getImage();
        if (currentImage != null) {
            imageManager.colorCounter(currentImage, (redPixels, greenPixels, bluePixels) -> {
                Platform.runLater(() -> {
                    lblRed.setText("Red pixels: " + redPixels);
                    lblBlue.setText("Blue pixels: " + bluePixels);
                    lblGreen.setText("Green pixels: " + greenPixels);
                });
            });
        }
    }
}
