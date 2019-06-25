package bloom.bloomshaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public final class BloomShaderLoader {

	static final public ShaderProgram createShader(String vertexName,
			String fragmentName) {

		String vertexShader = Gdx.files.classpath(
				"bloom/bloomshaders/" + vertexName
						+ ".vertex.glsl").readString();
		String fragmentShader = Gdx.files.classpath(
				"bloom/bloomshaders/" + fragmentName
						+ ".fragment.glsl").readString();
		ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if (!shader.isCompiled()) {
			System.out.println(shader.getLog());
			Gdx.app.exit();
		} else
			Gdx.app.log("shader compiled", shader.getLog());
		return shader;
	}
}
