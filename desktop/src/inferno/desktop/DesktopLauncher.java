package inferno.desktop;

import inferno.*;
import io.anuke.arc.backends.sdl.*;

public class DesktopLauncher{
	public static void main(String[] arg){
		new SdlApplication(new Inferno(), new SdlConfig(){{
			title = "Inferno";
			maximized = true;
		}});
	}
}
