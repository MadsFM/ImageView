package BLL;

import BE.Image;
import GUI.MainViewController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ImageManager {
    private List<Image> images = new ArrayList<>();


    private int currentIndex = -1;
    private Thread slideshow;
    private Consumer<Image> imageDisplay;


    String pathToImages = "/resources/images";

    @FXML
    private void initialize(){
        loadImagesFromDirectory(pathToImages);
    }

    public ImageManager(Consumer<Image> imageDisplay) {
        this.imageDisplay = imageDisplay;
    }

    public ImageManager() {
    }

    public void loadImagesFromDirectory(String directoryPath) {
        File folder = new File(directoryPath);
        File[] listOfImages = folder.listFiles();
        images.clear();

        if (listOfImages != null) {
            for (File file : listOfImages) {
                if (file.isFile() && (file.getName().endsWith(".png") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".jpg"))) {
                    images.add(new Image(file.getName(), file.getAbsolutePath()));
                }
            }
        }
    }

    public void loadImagesDetails(String details){
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
            images.clear();
            images.addAll(tempImageList);
        });
    }

    public ObservableList<Image> getImages(){
        return FXCollections.observableArrayList(images);
    }
    public Image goToPreviousPicture() {
        if (currentIndex > 0) {
            currentIndex--;
        } else if (!images.isEmpty()) {
            currentIndex = images.size() - 1;
        }
        return getCurrentImage();

    }

    public Image goToNextPicture() {
        if (currentIndex < images.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0; // Reset
        }
        return getCurrentImage();
    }

    private Image getCurrentImage(){
        if (!images.isEmpty() && currentIndex >= 0 && currentIndex < images.size()){
            return images.get(currentIndex);
        }
        return null;
    }


    public void startSlidshow(){
        // initiere vores tråd.
        if (slideshow == null || slideshow.isAlive()){
            Runnable slideshowTask = () -> {
                try {
                    //så længe der er billeder der kan vises kører den
                    while (!Thread.currentThread().isInterrupted()) {
                        Image nextImage = getNextImage();
                        Platform.runLater(() -> imageDisplay.accept(nextImage));
                        //1.5 sek forsinkelse
                        Thread.sleep(1500);
                    }
                } catch (InterruptedException ie){
                    //hvis den ikke kan, så bliver den stoppet, så while loopet kan brydes.
                    Thread.currentThread().interrupt();
                }
            };
            slideshow = new Thread(slideshowTask);
            slideshow.start();
        }
    }

    private Image getNextImage() {
        currentIndex = (currentIndex + 1) % images.size();
        return images.get(currentIndex);
    }

    public void stopSlideshow(){
        if (slideshow != null){
            slideshow.interrupt();
        }
    }


    public void colorCounter(javafx.scene.image.Image image, Callback callback) {
        new Thread(() -> {
            // lavet tre variabler til hver farve
            int redPixels = 0, greenPixels = 0, bluePixels = 0;
            PixelReader pReader = image.getPixelReader();
            if (pReader != null) {
                //kører gennem hvert billede i højden og bredden og tæller pixels
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        Color color = pReader.getColor(x, y);
                        //for hvert pixel der er talt, får vi den respektive farve
                        double red = color.getRed();
                        double green = color.getGreen();
                        double blue = color.getBlue();
                        //kontrol for hvilken variable der skal ++
                        //gøres for hver farve
                        if (red > green && red > blue){
                            redPixels++;
                        } else if (blue > green && blue > red) {
                            bluePixels++;
                        } else if (green > red && green > blue) {
                            greenPixels++;
                        }
                    }
                }
            }
            // sætter dem som final da @finalRedPixels, finalGreenPixels, finalBluePixels
            // ikke skal kunne ændres fra andre metoder eller klasser.
            final int finalRedPixels = redPixels;
            final int finalGreenPixels = greenPixels;
            final int finalBluePixels = bluePixels;
            callback.onComplete(finalRedPixels, finalBluePixels, finalGreenPixels);
        }).start();
    }

    public interface Callback{
        void onComplete(int redPixel, int bluePixel, int greenPixel);
    }
}
