import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.util.HashSet;

public class Input {

    private static final HashSet<KeyCode> keysDown = new HashSet<>();
    private static final HashSet<MouseButton> mouseDown = new HashSet<>();
    private static final Vector3 currMousePos = new Vector3(0, 0);
    private static final Vector3 prevMousePos = new Vector3(0, 0);


    public static void initializeInput(SceneRenderer3D sceneRenderer) {
        Main.windowScene.setOnKeyPressed(null);
        Main.windowScene.setOnKeyReleased(null);

        Main.windowScene.setOnKeyPressed(key -> {
            if(key.getCode() == KeyCode.W
                    || key.getCode() == KeyCode.A
                    || key.getCode() == KeyCode.S
                    || key.getCode() == KeyCode.D) {
                keysDown.add(key.getCode());
            }
        });

        Main.windowScene.setOnKeyReleased(key -> {
            if(key.getCode() == KeyCode.W
                    || key.getCode() == KeyCode.A
                    || key.getCode() == KeyCode.S
                    || key.getCode() == KeyCode.D) {
                keysDown.remove(key.getCode());
            }
        });

        sceneRenderer.setOnMousePressed(mouse -> {
            mouseDown.add(mouse.getButton());
        });

        sceneRenderer.setOnMouseReleased(mouse -> {
            mouseDown.remove(mouse.getButton());
        });
    }


    public static void pollInput(SceneRenderer3D sceneRenderer) {
        Point mouseScreenPos = MouseInfo.getPointerInfo().getLocation();
        Point2D mouseLocalPos = sceneRenderer.screenToLocal(mouseScreenPos.getX(), mouseScreenPos.getY());

        prevMousePos.setX(currMousePos.getX());
        prevMousePos.setY(currMousePos.getY());

        currMousePos.setX(mouseLocalPos.getX());
        currMousePos.setY(mouseLocalPos.getY());
    }

    public static boolean keyIsDown(KeyCode key) {
        return keysDown.contains(key);
    }

    public static boolean mouseIsDown(MouseButton mouseButton) {
        return mouseDown.contains(mouseButton);
    }

    public static Vector3 getMousePosition() {
        return currMousePos;
    }

    public static Vector3 getMouseDelta() {
        return currMousePos.subtract(prevMousePos);
    }

}
