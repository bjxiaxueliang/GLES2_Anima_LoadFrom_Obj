precision mediump float;
uniform sampler2D sTexture;//纹理内容数据
//接收从顶点着色器过来的参数
varying vec4 ambient;
varying vec4 diffuse;
varying vec4 specular;
varying vec2 vTextureCoord;

// 直接传入片元着色器的顶点颜色数据
uniform vec3 uColor;
// 绘制纹理or绘制颜色
uniform int uRenderType;
// alpha值
uniform float uOpacity;

void main()
{
   //将计算出的颜色给此片元
   if (uRenderType == 0) {
      vec4 finalColor=texture2D(sTexture, vTextureCoord);
      finalColor.a *= uOpacity;
      //给此片元颜色值
      gl_FragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;
   }else{
      vec4 finalColor=vec4(uColor,1.0);
      finalColor.a *= uOpacity;
      //给此片元颜色值
      gl_FragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;
   }
}   