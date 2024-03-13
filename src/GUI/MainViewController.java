package GUI;

import BE.Images;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

import java.io.File;

public class MainViewController {
    @FXML
    private ImageView iVImages;
    @FXML
    private TableView<Images> tbVImageView;
    @FXML
    private TableColumn<Images, String> imageNameCol, imagePathCol;


    @FXML
    private void initialize(){
        String pathToImages = "resources/images";
        imageNameCol.setCellValueFactory(new PropertyValueFactory<>("imageName"));
        imagePathCol.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        loadImagesDetails(pathToImages);

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
}
