import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;


import java.awt.*;
import java.util.HashSet;

public class Input {

    private static final HashSet<KeyCode> keysDown = new HashSet<>();
    private static final HashSet<MouseButton> mouseDown = new HashSet<>();
    private static final Vector3 currMousePos = new Vector3(0, 0);
    private static final Vector3 prevMousePos = new Vector3(0, 0);


    public Input(Canvas canvas) {
        canvas.setOnMousePressed(event -> {
            canvas.requestFocus();
            mouseDown.add(event.getButton());
        });
        canvas.setOnMouseReleased(event -> mouseDown.remove(event.getButton()));
        canvas.setOnKeyPressed(key -> keysDown.add(key.getCode()));
        canvas.setOnKeyReleased(key -> keysDown.remove(key.getCode()));
    }


    public void pollInput(Canvas canvas) {
        Point mouseScreenPos = MouseInfo.getPointerInfo().getLocation();
        Point2D mouseLocalPos = canvas.screenToLocal(mouseScreenPos.getX(), mouseScreenPos.getY());

        prevMousePos.setX(currMousePos.getX());
        prevMousePos.setY(currMousePos.getY());

        currMousePos.setX(mouseLocalPos.getX());
        currMousePos.setY(mouseLocalPos.getY());
    }

    public boolean keyIsDown(KeyCode key) {
        return keysDown.contains(key);
    }

    public boolean mouseIsDown(MouseButton mouseButton) {
        return mouseDown.contains(mouseButton);
    }

    public Vector3 getMousePosition() {
        return currMousePos;
    }

    public Vector3 getMouseDelta() {
        return currMousePos.subtract(prevMousePos);
    }

}
