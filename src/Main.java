import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    static int width = 1280;
    static int height = 720;
    static Camera camera = new Camera();

    static Stage mainWindow;
    static SceneRenderer3D sceneRenderer;
    static Scene windowScene;
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
        sceneRenderer = new SceneRenderer3D(width, height);

        Group group = new Group(sceneRenderer);
        windowScene = new Scene(group, width, height);
        mainWindow.setScene(windowScene);
        mainWindow.show();

        Input.initializeInput(sceneRenderer);
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
                System.out.println("Frame Rate: " + 1 / deltaTime);
            }
        };


        timer.start();

        //double distance = Triangle.pointDistanceToPlane(new Vector3(0, 0, 1), new Vector3(0, 0, 0), new Vector3(0, 0, -1));



    }



    public void loadScene() {

        Vertex[] vertices = new Vertex[]
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

        //Mesh mesh = new Mesh(vertices);


        Mesh mesh = new Mesh("./res/captain_model.obj");


        if (!sceneRenderer.meshes.contains(mesh))
            sceneRenderer.meshes.add(mesh);

    }



    public void draw() {
        // draw
        sceneRenderer.clearCanvas();
        sceneRenderer.render();
    }

    public void checkInput() {

        Input.pollInput(sceneRenderer);

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


        if (Input.mouseIsDown(MouseButton.PRIMARY)) {
            camera.rotate(-Input.getMouseDelta().getY(), Input.getMouseDelta().getX(), 0);
        }



    }

}
