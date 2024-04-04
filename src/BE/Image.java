package BE;

public class Image {
    private final String imageName;
    private final String imagePath;

    public Image(String imageName, String imagePath) {
        this.imageName = imageName;
        this.imagePath = imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImagePath() {
        return imagePath;
    }
}
