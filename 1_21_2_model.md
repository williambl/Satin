# Minecraft's new Shader system

## Shader Loading

`ShaderLoader` loads every file in the `shaders` directory
ending in `.json`, `.fsh`, `.vsh`, or `.glsl`

- `.glsl` extensions are exclusively used for common shader includes
- `.fsh` and `.vsh` files are loaded as strings and compiled later
- `.json` files are loaded as `ShaderProgramDefinition`

```mermaid
---
title: Shader data classes
---
classDiagram
    ShaderLoader --> Definitions
    Definitions "1" *-- "*" ShaderProgramDefinition
    Definitions "1" *-- "*" PostEffectPipeline
    Definitions : +string[id] shaderSources
    ShaderProgramDefinition : +id vertex
    ShaderProgramDefinition : +id fragment
    ShaderProgramDefinition "1" *-- "1" Defines
    ShaderProgramDefinition "1" *-- "1..*" Sampler
    ShaderProgramDefinition "1" *-- "*" Uniform
    Defines : +values
    Defines : +flags
    Sampler : +string name
    Uniform : +string name
    Uniform : +string type
    Uniform : +int count
    Uniform : +float[] values
    PostEffectPipeline "1" *-- "1..*" Target
    PostEffectPipeline "1" *-- "1..*" Pass
    Target <|-- CustomSized
    Target <|-- ScreenSized
    Target : ~id
    Pass : ~id
    Pass : +id program
    Pass : +id output
    Pass "1" *-- "1..*" Input
    Pass "1" *-- "1..*" UniformValues
    Input : +string samplerName
    Input <|-- TargetSampler
    Input <|-- TextureSampler
    TargetSampler : +bool useDepthBuffer
    TargetSampler : +bool bilinear
    TextureSampler : +id location
    TextureSampler : +int width
    TextureSampler : +int height
    TextureSampler : +bool bilinear
    UniformValues : +string name
    UniformValues : +float[] values
    TargetSampler ..> Target : "targetId"
```

Core shaders are referenced in `ShaderProgramKeys.ALL`. These shaders are all
preloaded in the `ShaderLoader`'s `apply` stage, at which point any failure causes a crash.

Post-process effects (also called "post chains") are loaded lazily.

```mermaid
---
title: Shader Live Objects
---
classDiagram
    Cache o-- CompiledShader
    Cache o-- ShaderProgram
    Cache o-- PostEffectProcessor
    ShaderProgram ..> CompiledShader : "created using"
    PostEffectProcessor *-- PostEffectPass
    PostEffectProcessor "1" *-- "1..*" Target
    Target : ~id
    Target <|-- CustomSized
    Target <|-- ScreenSized
    PostEffectPass : +id id
    PostEffectPass --> ShaderProgram
    PostEffectPass ..> Target : "outputTargetId"
    PostEffectPass "1" *-- "1..*" Sampler
    Sampler: +string samplerName
    Sampler <|-- TextureSampler
    Sampler <|-- TargetSampler
    TargetSampler : +bool depthBuffer
    TargetSampler : +bool bilinear
    TargetSampler ..> Target : "targetId"
    TextureSampler : +texture
    TextureSampler : +int width
    TextureSampler : +int height
```

## Post-process effect rendering

Post effect rendering is now divided into two steps:
1. building a reusable frame graph
2. rendering the frame graph

### Building the frame graph

```mermaid
---
title: Frame Graph structure
---
classDiagram
    Node <|-- ObjectNode
    Node <|-- ResourceNode
    ResourceNode "1" --* "*" FrameGraphBuilder
    ObjectNode "1" --* "*" FrameGraphBuilder
    FramePass "1" --* "*" FrameGraphBuilder
```
