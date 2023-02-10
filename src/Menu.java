import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;


public class Menu extends VBox {

    File meshFile;
    File meshTextureFile;
    FileChooser meshFileChooser;
    FileChooser meshTextureFileChooser;
    Button meshFileChooserButton;
    Button meshTextureFileChooserButton;
    Button reloadSceneButton;
    CheckBox wireframeCheckBox;
    CheckBox cullNonVisibleFacesCheckBox;
    CheckBox fillCheckBox;
    CheckBox showStatsCheckBox;

    CheckBox diffuseLightingCheckBox;
    ColorPicker wireFrameColorPicker;
    ColorPicker backgroundColorPicker;
    ColorPicker meshBaseColorPicker;

    ChoiceBox<MeshRenderer.DEPTH_SORTING> depthSortingDropDown;

    public Menu(int width) {
        setWidth(width);
        setPadding(new Insets(10, 10, 10, 10));
        setSpacing(10);

        meshFileChooser = new FileChooser();
        meshFileChooser.setInitialDirectory(new File("./"));
        meshFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OBJ Files", "*.obj"));
        meshFileChooser.setTitle("Select .obj file for the mesh");
        meshFileChooserButton = new Button("Selected Mesh: None");
        meshFileChooserButton.setOnAction(event -> {
            meshFile = meshFileChooser.showOpenDialog(Main.mainWindow);
            if (meshFile != null) {
                meshFileChooserButton.setText("Selected Mesh: " + meshFile.getName());
            } else {
                meshFileChooserButton.setText("Selected Mesh: None");
            }
        });

        meshTextureFileChooser = new FileChooser();
        meshTextureFileChooser.setInitialDirectory(new File("./"));
        meshTextureFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        meshTextureFileChooser.setTitle("Select image file for the mesh");
        meshTextureFileChooserButton = new Button("Selected Texture: None");
        Tooltip meshTextureToolTip = new Tooltip("Will override mesh base color with UV texture sampling");
        meshTextureToolTip.setShowDelay(Duration.seconds(0.5));
        meshTextureFileChooserButton.setTooltip(meshTextureToolTip);
        meshTextureFileChooserButton.setOnAction(event -> {
            meshTextureFile = meshTextureFileChooser.showOpenDialog(Main.mainWindow);
            if (meshTextureFile != null) {
                meshTextureFileChooserButton.setText("Selected Texture: " + meshTextureFile.getName());
            } else {
                meshTextureFileChooserButton.setText("Selected Texture: None");
            }
        });

        reloadSceneButton = new Button("Reload Scene");
        reloadSceneButton.setOnAction(event -> Main.loadScene(meshFile, meshTextureFile));

        VBox meshLoadingControlsVBox = new VBox(meshFileChooserButton, meshTextureFileChooserButton, reloadSceneButton);
        meshLoadingControlsVBox.setSpacing(10);
        meshLoadingControlsVBox.setAlignment(Pos.CENTER);

        Separator separator = new Separator();

        wireframeCheckBox = new CheckBox("Wireframe");
        wireframeCheckBox.setSelected(Main.sceneRenderer.wireframe);
        wireframeCheckBox.setOnAction(event -> Main.sceneRenderer.wireframe = wireframeCheckBox.isSelected());

        cullNonVisibleFacesCheckBox = new CheckBox("Cull Non-Visible Faces");
        cullNonVisibleFacesCheckBox.setSelected(Main.sceneRenderer.cullNonVisibleFaces);
        cullNonVisibleFacesCheckBox.setOnAction(event -> Main.sceneRenderer.cullNonVisibleFaces = cullNonVisibleFacesCheckBox.isSelected());

        fillCheckBox = new CheckBox("Draw Scan Lines");
        fillCheckBox.setSelected(Main.sceneRenderer.fill);
        fillCheckBox.setOnAction(event -> Main.sceneRenderer.fill = fillCheckBox.isSelected());

        diffuseLightingCheckBox = new CheckBox("Diffuse Lighting");
        diffuseLightingCheckBox.setSelected(Main.sceneRenderer.diffuseLighting);
        diffuseLightingCheckBox.setOnAction(event -> Main.sceneRenderer.diffuseLighting = diffuseLightingCheckBox.isSelected());

        showStatsCheckBox = new CheckBox("Show Stats");
        showStatsCheckBox.setSelected(Main.sceneRenderer.showStats);
        showStatsCheckBox.setOnAction(event -> Main.sceneRenderer.showStats = showStatsCheckBox.isSelected());

        Label depthSortingLabel = new Label("Depth Resolution:");
        depthSortingDropDown = new ChoiceBox<>(FXCollections.observableArrayList(MeshRenderer.DEPTH_SORTING.values()));
        depthSortingDropDown.setValue(MeshRenderer.DEPTH_SORTING.DEPTH_BUFFER);
        depthSortingDropDown.setOnAction(event -> Main.sceneRenderer.depthSorting = depthSortingDropDown.getValue());
        HBox depthHBox = new HBox(depthSortingLabel, depthSortingDropDown);
        depthHBox.setSpacing(5);
        depthHBox.setAlignment(Pos.CENTER_LEFT);

        Label wireFrameColorPickerLabel = new Label("Wireframe Color:");
        wireFrameColorPicker = new ColorPicker(Main.sceneRenderer.wireframeColor);
        wireFrameColorPicker.setOnAction(event -> Main.sceneRenderer.wireframeColor = wireFrameColorPicker.getValue());
        HBox wireFrameColorPickerHBox = new HBox(wireFrameColorPickerLabel, wireFrameColorPicker);
        wireFrameColorPickerHBox.setSpacing(5);
        wireFrameColorPickerHBox.setAlignment(Pos.CENTER_LEFT);

        Label backgroundColorPickerLabel = new Label("Background Color:");
        backgroundColorPicker = new ColorPicker(Main.sceneRenderer.backgroundColor);
        backgroundColorPicker.setOnAction(event -> Main.sceneRenderer.backgroundColor = backgroundColorPicker.getValue());
        HBox backgroundColorPickerHBox = new HBox(backgroundColorPickerLabel, backgroundColorPicker);
        backgroundColorPickerHBox.setSpacing(5);
        backgroundColorPickerHBox.setAlignment(Pos.CENTER_LEFT);

        Label meshBaseColorPickerLabel = new Label("Mesh Base Color:");
        meshBaseColorPicker = new ColorPicker(Color.RED);
        meshBaseColorPicker.setOnAction(event -> Main.sceneRenderer.mesh.baseColor = meshBaseColorPicker.getValue());
        HBox mashBaseColorPickerHBox = new HBox(meshBaseColorPickerLabel, meshBaseColorPicker);
        mashBaseColorPickerHBox.setSpacing(5);
        mashBaseColorPickerHBox.setAlignment(Pos.CENTER_LEFT);
        Tooltip meshColorPickerToolTip = new Tooltip("Only has an effect if no texture is specified for the mesh");
        meshColorPickerToolTip.setShowDelay(Duration.seconds(0.5));
        meshBaseColorPickerLabel.setTooltip(meshColorPickerToolTip);
        meshBaseColorPicker.setTooltip(meshColorPickerToolTip);


        this.getChildren().addAll(
                meshLoadingControlsVBox,
                separator,
                wireframeCheckBox,
                cullNonVisibleFacesCheckBox,
                fillCheckBox,
                diffuseLightingCheckBox,
                showStatsCheckBox,
                depthHBox,
                wireFrameColorPickerHBox,
                backgroundColorPickerHBox,
                mashBaseColorPickerHBox);
    }

}
