# VFont

## Purpose
VFont aims to enhance the appearance and performance of font rendering using OpenGL in Java applications.
The project focuses on creating smooth bindings between the two environments, resulting in an efficient and visually appealing text rendering experience.

## Motivations
The primary motivation behind VFont was the observation that existing techniques for rendering fonts in Java applications often yielded sub-optimal results.
The two most common methods, Font Atlas and Signed Distance Fields (SDF), have their limitations.
Font Atlas tends to become blurry when letters are scaled too large or too small, while SDF-based implementations suffer from various artifacts.

## VFont also draws inspiration from [Will Dobbie's Blog Post](https://wdobbie.com/post/gpu-text-rendering-with-vector-textures/),
where glyphs are stored as textures and rasterized on-the-fly, leading to improved rendering quality.

## Implementation
VFont's current implementation utilizes Java's built-in APIs to load fonts and extract font glyphs as a set of Bezier curves.
In addition, a GLSL shader is used to achieve results similar to Will Dobbie's work on GPU font rendering.

## Future Improvements 
- Refining the GLSL shader to optimize performance and visual quality
- Implementing a more ser-friendly API for loading and rendering fonts
- Exploring additional font formats and advanced features, such as kerning and ligatures
- Investigating other GPU-based rendering techniques to identify potential improvements
