package inferno.desktop;

import arc.backend.sdl.*;
import inferno.*;

public class DesktopLauncher{
	public static void main(String[] arg){
		new SdlApplication(new Inferno(), new SdlConfig(){{
			title = "Final Fight";
			maximized = true;
			disableAudio = false;
			vSyncEnabled = true;
		}});
	}

}

