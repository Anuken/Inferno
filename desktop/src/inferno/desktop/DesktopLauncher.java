package inferno.desktop;

import inferno.Inferno;
import io.anuke.arc.backends.sdl.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		SdlConfig config = new SdlConfig();
		config.title = "Inferno";
		config.maximized = true;
		new SdlApplication(new Inferno(), config);
	}
}
