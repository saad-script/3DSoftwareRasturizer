import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.beans.EventHandler;

public class Main extends Application {

    static int width = 1280;
    static int height = 720;
    static double[][] depthBuffer = new double[width][height];;
    static Camera camera = new Camera();

    static Stage mainWindow;
    static RenderScene renderScene;
    static Scene windowScene;
    static Canvas renderCanvas;
    static GraphicsContext renderer;

    static AnimationTimer timer;
    static double deltaTime;
    static double passedTime;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        mainWindow = primaryStage;
        mainWindow.setWidth(width);
        mainWindow.setHeight(height);
        renderScene = new RenderScene();
        renderCanvas = new Canvas(width, height);
        renderer = renderCanvas.getGraphicsContext2D();
        Group group = new Group(renderCanvas);
        windowScene = new Scene(group, width, height);
        mainWindow.setScene(windowScene);
        mainWindow.show();

        Input.initializeInput();
        Main.camera.move(Vector3.BACKWARD, 10);

        loadScene();

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


    }



    public void loadScene() {

        /*Vertex[] vertices = new Vertex[]
                {
                        new Vertex(0, 0, 0), new Vertex(0, 1, 0), new Vertex(1, 1, 0),
                        new Vertex(0, 0, 0), new Vertex(1, 1, 0), new Vertex(1, 0, 0),
                        new Vertex(1, 0, 0), new Vertex(1, 1, 0), new Vertex(1, 1, 1),
                        new Vertex(1, 0, 0), new Vertex(1, 1, 1), new Vertex(1, 0, 1),
                        new Vertex(1, 0, 1), new Vertex(1, 1, 1), new Vertex(0, 1, 1),
                        new Vertex(1, 0, 1), new Vertex(0, 1, 1), new Vertex(0, 0, 1),
                        new Vertex(0, 0, 1), new Vertex(0, 1, 1), new Vertex(0, 1, 0),
                        new Vertex(0, 0, 1), new Vertex(0, 1, 0), new Vertex(0, 0, 0),
                        new Vertex(0, 1, 0), new Vertex(0, 1, 1), new Vertex(1, 1, 1),
                        new Vertex(0, 1, 0), new Vertex(1, 1, 1), new Vertex(1, 1, 0),
                        new Vertex(1, 0, 1), new Vertex(0, 0, 1), new Vertex(0, 0, 0),
                        new Vertex(1, 0, 1), new Vertex(0, 0, 0), new Vertex(1, 0, 0)
                };

        Mesh mesh = new Mesh(vertices);
         */

        Mesh mesh = new Mesh("./res/textured_cube.obj");



        if (!renderScene.meshes.contains(mesh))
            renderScene.meshes.add(mesh);

    }



    public void draw() {
        // clear screen
        renderer.setFill(Color.BLACK);
        renderer.fillRect(0, 0, width, height);
        // draw
        renderScene.draw();
    }

    public void checkInput() {

        Input.pollInput();

        if (Input.keyIsDown(KeyCode.W)) {
            camera.move(camera.getForward(), 20 * Main.deltaTime);
        }
        if (Input.keyIsDown(KeyCode.A)) {
            camera.move(camera.getLeft(), 20 * Main.deltaTime);
        }
        if (Input.keyIsDown(KeyCode.S)) {
            camera.move(camera.getBackward(), 20 * Main.deltaTime);
        }
        if (Input.keyIsDown(KeyCode.D)) {
            camera.move(camera.getRight(), 20 * Main.deltaTime);
        }



        if (Input.mouseIsDown(MouseButton.MIDDLE)) {
            camera.rotate(Input.getMouseDelta().getY(), Input.getMouseDelta().getX(), 0);
            System.out.println(camera.getForward());
        }



    }

}
