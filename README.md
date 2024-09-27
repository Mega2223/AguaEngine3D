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

I usually to upload feature showcases in [my youtube development playlist](https://www.youtube.com/playlist?list=PL2CjNrK_Cb0qEX2Y7PIwy2z6IGoHwQ9Cr), but i also have some cool captures

<div>
    <img 
    height=325
    width=295
    src="https://github.com/Mega2223/Mega2223.github.io/blob/main/media/WorldGenDemo.gif?raw=true"
    >
    <img 
    height=325
    width=295
    src="https://github.com/Mega2223/Mega2223.github.io/blob/main/media/Screenshot_1860.png?raw=true"
    >
    <img 
    height=325
    width=295
    src="https://github.com/Mega2223/Mega2223.github.io/blob/main/media/ShadowDemo.gif?raw=true"
    >
    <img 
    height=325
    width=295
    src="https://github.com/Mega2223/Mega2223.github.io/blob/main/media/FloorDemo.gif?raw=true"
    >
    
</div>

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

If you want to import the project as a maven dependecy without having to go login via maven you can run 
```bat
mvn clean deploy -o
```
in the repo directory, this will return an error message, however the repository will be saved in your machine's internal `.m2` directory, thus making it usable as a dependecy in other projects.
