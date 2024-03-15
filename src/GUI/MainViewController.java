package GUI;

import BE.Images;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

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
    private Button btnLoadImage,btnNext,btnPrevious;

    @FXML
    private ObservableList<Images> imageList = FXCollections.observableArrayList();;
    private int currentIndex = -1;


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
        ObservableList<Images> images = FXCollections.observableArrayList();

        if (listOfImages != null) {
            for (File file : listOfImages) {
                if (file.isFile()) {
                    String imageName = file.getName();
                    String imagePath = file.getAbsolutePath();

                    if (imageName.endsWith(".png") || imageName.endsWith(".jpeg") || imageName.endsWith(".jpg")) {
                        images.add(new Images(imageName, imagePath));
                    }
                }
            }
        }
        tbVImageView.setItems(images);
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
        if (currentIndex >0) {
            currentIndex--;
            showImage();
        }

    }

    @FXML
    private void goToNextPicture(ActionEvent actionEvent) {
        if (currentIndex < imageList.size() -1) {
            currentIndex++;
            showImage();
        }

    }
}
