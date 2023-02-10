import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    static Camera camera;
    static Stage mainWindow;
    static MeshRenderer sceneRenderer;
    static Menu menu;
    static Input scene3DInput;
    static Scene windowScene;
    static AnimationTimer timer;
    static double deltaTime;
    static double passedTime;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        mainWindow = primaryStage;
        mainWindow.setWidth(1280 + 250);
        mainWindow.setHeight(720 + 50);
        mainWindow.setTitle("JavaFX 3D Software Renderer");
        sceneRenderer = new MeshRenderer(1280, 720);
        menu = new Menu(250);
        SplitPane splitPane = new SplitPane(menu, sceneRenderer);
        splitPane.setDividerPosition(0, (250.0/(1280.0 + 250.0)));

        windowScene = new Scene(splitPane);
        mainWindow.setScene(windowScene);

        scene3DInput = new Input(sceneRenderer);

        loadScene(null, null);

        timer = new AnimationTimer() {
            private long prevTime;
            @Override
            public void handle(long currentTime) {
                deltaTime = (currentTime - prevTime) / 1e9;
                passedTime += deltaTime;
                checkInput();
                draw();
                prevTime = currentTime;
            }
        };

        timer.start();
        mainWindow.show();
    }



    public static void loadScene(File meshFile, File meshTextureFile) {
        camera = new Camera(new Vector3(0, 0, -10), new Vector3(0, 0, 1));

        if (meshFile == null) {
            return;
        }

        Mesh mesh = new Mesh(meshFile.getAbsolutePath());

        if (meshTextureFile != null)
            mesh.texture = new Texture(meshTextureFile.getAbsolutePath());

        sceneRenderer.mesh = mesh;
    }



    public static void draw() {
        sceneRenderer.nextFrame();
    }

    public static void checkInput() {

        scene3DInput.pollInput(sceneRenderer);

        if (scene3DInput.keyIsDown(KeyCode.W)) {
            camera.move(camera.getForward(), 5 * Main.deltaTime);
        }
        if (scene3DInput.keyIsDown(KeyCode.A)) {
            camera.move(camera.getLeft(), 5 * Main.deltaTime);
        }
        if (scene3DInput.keyIsDown(KeyCode.S)) {
            camera.move(camera.getBackward(), 5 * Main.deltaTime);
        }
        if (scene3DInput.keyIsDown(KeyCode.D)) {
            camera.move(camera.getRight(), 5 * Main.deltaTime);
        }


        if (scene3DInput.mouseIsDown(MouseButton.PRIMARY)) {
            camera.rotate(camera.getUp(), -scene3DInput.getMouseDelta().getX() * 0.1);
            camera.rotate(camera.getRight(), -scene3DInput.getMouseDelta().getY() * 0.1);
        }
    }

}
