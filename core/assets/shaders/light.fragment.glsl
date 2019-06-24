#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define ambient vec3(0.5)

uniform sampler2D u_texture;

varying vec4 v_color;
varying vec2 v_texCoord;


void main(){
	vec4 color = texture2D(u_texture, v_texCoord.xy);
	gl_FragColor = clamp(ambient + color, 0.0, 1.0);
}
