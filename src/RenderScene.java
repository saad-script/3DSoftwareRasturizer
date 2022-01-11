import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class RenderScene {

    public ArrayList<Mesh> meshes = new ArrayList<>();


    private double temp;


    public void draw() {
        for (Mesh mesh : meshes)
            mesh.draw(Color.RED, false, true);
    }



}
