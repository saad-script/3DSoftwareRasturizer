import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.io.FileInputStream;

public class Texture {

    private Image image;

    public Texture(String imageFile) {
        try {
            this.image = new Image(new FileInputStream(imageFile));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Color sampleColor(double i, double j) throws IllegalArgumentException {
        if (i > 1 || i < 0 || j > 1 || j < 0)
            throw new IllegalArgumentException( i + " and " + j + " must be between 0 and 1 (inclusive)");

        int scaledI = (int) Math.floor(i * (image.getWidth() - 1));
        int scaledJ = (int) Math.floor(j * (image.getHeight() - 1));
        return image.getPixelReader().getColor(scaledI, scaledJ);
    }

}
