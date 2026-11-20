#ifndef IS_GUI
#include <minecraft:fog.glsl>
#endif
#include <minecraft:dynamictransforms.glsl>
#include <minecraft:oit.glsl>

#ifndef IS_GUI
layout(location = 0) in float sphericalVertexDistance;
layout(location = 1) in float cylindricalVertexDistance;
#endif
layout(location = 2) in vec4 vertexColor;
layout(location = 3) in vec2 texCoord0;

#ifndef OIT_ALPHA_ONLY
layout(location = 0) out vec4 fragColor;
#endif

void writeModernTextColor(vec4 color) {
#ifdef OIT_ALPHA_ONLY
    executeAlphaOnlyPhase(gl_FragCoord.z, color.a);
#else
    #ifdef OIT_ACCUMULATE
    color = sampleColorForAccumulation(color);
    #endif

    #ifndef IS_GUI
        #ifdef OIT_ACCUMULATE
        vec4 fogColor = vec4(FogColor.rgb * color.a, FogColor.a);
        #else
        vec4 fogColor = FogColor;
        #endif
        color = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance,
                FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, fogColor);
    #endif
    fragColor = color;
#endif
}
