#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define ambient vec4(vec3(0.1, 0.03, 0.01) * 2.0, 0.5)
#define steprad 0.13

uniform sampler2D u_texture;

varying vec4 v_color;
varying vec2 v_texCoord;

float stepped(float inp){
    return float(int(inp / steprad)) * steprad;
}

void main(){
	vec4 color = texture2D(u_texture, v_texCoord.xy);
	float rounded = stepped(color.a);
	gl_FragColor = clamp(vec4(mix(ambient.rgb, color.rgb, rounded), ambient.a - rounded), 0.0, 1.0);
}
