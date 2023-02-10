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
    static Input scene3DInput;
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

        scene3DInput = new Input(sceneRenderer);
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
    }



    public void loadScene() {


        //Example of creating mesh manually using an array of vertices

//        Vertex[] vertices = new Vertex[]
//
//                {
//                        new Vertex(0, 0, 0), new Vertex(0, 1, 0), new Vertex(1, 1, 0),
//                        new Vertex(0, 0, 0), new Vertex(1, 1, 0), new Vertex(1, 0, 0),
//                        new Vertex(1, 0, 0), new Vertex(1, 1, 0), new Vertex(1, 1, 1),
//                        new Vertex(1, 0, 0), new Vertex(1, 1, 1), new Vertex(1, 0, 1),
//                        new Vertex(1, 0, 1), new Vertex(1, 1, 1), new Vertex(0, 1, 1),
//                        new Vertex(1, 0, 1), new Vertex(0, 1, 1), new Vertex(0, 0, 1),
//                        new Vertex(0, 0, 1), new Vertex(0, 1, 1), new Vertex(0, 1, 0),
//                        new Vertex(0, 0, 1), new Vertex(0, 1, 0), new Vertex(0, 0, 0),
//                        new Vertex(0, 1, 0), new Vertex(0, 1, 1), new Vertex(1, 1, 1),
//                        new Vertex(0, 1, 0), new Vertex(1, 1, 1), new Vertex(1, 1, 0),
//                        new Vertex(1, 0, 1), new Vertex(0, 0, 1), new Vertex(0, 0, 0),
//                        new Vertex(1, 0, 1), new Vertex(0, 0, 0), new Vertex(1, 0, 0)
//                };
//
//        Mesh mesh = new Mesh(vertices);



        //create mesh by importing from file
        //Mesh mesh = new Mesh("./res/Realistic_Body_Base_Mesh.obj");

        //import mesh with texture
        Mesh mesh = new Mesh("./res/brick_cube.obj");
        mesh.texture = new Texture("./res/brick_texture.png");

        if (!sceneRenderer.meshes.contains(mesh))
            sceneRenderer.meshes.add(mesh);

    }



    public void draw() {
        sceneRenderer.nextFrame();
    }

    public void checkInput() {

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
