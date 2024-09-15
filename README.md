<div class="title" style="align-items: center ;">
    <h1><img 
    height=100
    width=100
    alt="Yes i made this in paint."
    src="https://github.com/user-attachments/assets/fb61b1df-ed46-4abd-acbb-398be4da802a"
    align = left> 
    <div class="titleText";>
        AguaEngine3D
    </div>
    </h1>
    <strong>OpenGL based rendering engine made as a personal hobby-project :)  </strong>
</div>

## Showcase:

I usually to upload feature showcases in [my youtube development playlist](https://www.youtube.com/playlist?list=PL2CjNrK_Cb0qEX2Y7PIwy2z6IGoHwQ9Cr), but i also have some cool screenshots for you.

![image](https://github.com/user-attachments/assets/973aacf5-7c37-41ab-abab-8b3197042bee)
![image](https://github.com/user-attachments/assets/586dcaed-b2d2-4295-9c50-25e5ad37398e)
![image](https://github.com/user-attachments/assets/59bc9947-8ff7-4196-a4eb-04ed2c8bb851)

<img 
    height=200
    width=350
    src="https://github.com/user-attachments/assets/89bf2bdb-ed00-48dd-909a-59c336e98950"
    >
<img 
    height=200
    width=350
    src="https://github.com/user-attachments/assets/90b5ec0f-40e1-4f5e-893e-40fe4802ce68"
    >

## Features:  

The engine has a variety of utilities and abstractions for Graphics and general application management, plus an unifinished physics module. It currently contains:
- OpenGL handling.
- GLFW abstraction.
- Ready-to-use shader classes.
- OpenGL abstraction (models and other renderable types).
- Dynamic lighting.
- An pseudo-wave-function-collapse algorithm.
- A procedual noise framework that can generate custom models.
- General vector and matrix mathematics.
- Other math tools (interpolators, colisions etc.).
- Shadow mapping (unstable for now).
- A physics module (unfinished).
- Rendering miscs. (Skyboxes, interface components)
- Text & font loading/rendering.

## Applications:

This repository contais a variety of sub-applications which use the engine as it's framework in order to serve as some complementary tool or as a showcase of the engine's features. Those are:

### Procedural Building Generator
  Generates a set of tiled buildings using a pseudo wfc algorithm. Made to showcase the building generator sub-module.
### Procedural Terrain Generator   
Procedurally generates a small island surrounded with ocean. Made to showcase the procedural noise framework.

### World Generator
Uses multi-threadening to procedurally generate an infinite\* and ever-growing world.

## Installation:

The project uses Maven as it's dependency manager.  
In order to use the engine in your project as a Maven depedency you must declare the following variables in your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/Mega2223/AguaEngine3D/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

```xml
<dependencies>
    <dependency>
        <groupId>net.mega2223</groupId>
        <artifactId>aguaengine3d</artifactId>
        <version>BETA-0.4-U</version>
    </dependency>
</dependencies>
```

You also need to have [github authentication](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry) in your maven configuration for some reason.

Or, you can check for the releases for an usable jar. Alternatively you may clone the repository and build the `.jar` file from your machine (also requires Maven):

```bat
git clone https://github.com/Mega2223/AguaEngine3D.git
cd AguaEngine3D
mvn clean validate compile test package assembly:single verify install
```

the resulting `.jar` file will be at the `target` directory.
