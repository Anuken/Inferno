#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define ambient vec4(0.0, 0.0, 0.0, 0.8)
#define steprad 0.1

uniform sampler2D u_texture;

varying vec4 v_color;
varying vec2 v_texCoord;


void main(){
	vec4 color = texture2D(u_texture, v_texCoord.xy);
	float rounded = color.a;
	gl_FragColor = clamp(vec4(lerp(ambient.rgb, color.rgb, rounded), ambient.a - rounded), 0.0, 1.0);
}
