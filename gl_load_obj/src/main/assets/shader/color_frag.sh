precision mediump float;
//接收从顶点着色器过来的参数
varying vec4 ambient;
varying vec4 diffuse;
varying vec4 specular;

// 直接传入片元着色器的顶点颜色数据
uniform vec3 uColor;
// alpha
uniform float uOpacity;

void main()
{
    vec4 finalColor=vec4(uColor,1.0);
    finalColor.a *= uOpacity;
    //给此片元颜色值
    gl_FragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;
}   