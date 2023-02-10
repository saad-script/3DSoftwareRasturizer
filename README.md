# 3DSoftwareRasturizer

This is a 3D software rasturizer I created so that I can understand how the math and algorithms behind 3D computer graphics work.
This project uses JavaFX's Canvas class to draw pixels to the screen. All 3D math and algorithms (such as drawing a triangle on screen) are done manually.
This rasturizer also allows you to import basic OBJ files and view them in a 3D port.

### How to use

- download and run jar executable (check releases)
- import `.obj` mesh file by clicking the `Selected Mesh: None` button.
  - The mesh MUST be triangulated (contain only triangles) and also should contain uv coordinates. If you are using a 3d software like blender make sure you export as a .obj triangulated mesh that contains uv texture coordinates.
- import the texture file (as a `.png` or `.jpg`) be clicking on the `Selected Texture: None` button.
  - if no texture is specified, the mesh will be given a base color specified by the menu option `Mesh Base Color`.
- click the `Reload Scene` button to load the scene.
- move camera around the scene using WASD keys. Left click and drag to rotate camera.
