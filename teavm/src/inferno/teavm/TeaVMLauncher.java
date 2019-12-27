package inferno.teavm;

import arc.backend.teavm.*;
import arc.backend.teavm.TeaApplication.*;
import inferno.*;
import org.teavm.jso.browser.*;
import org.teavm.jso.dom.html.*;

public class TeaVMLauncher {
    public static void main(String[] args) {
        TeaApplicationConfig config = new TeaApplicationConfig();
        config.canvas = (HTMLCanvasElement)Window.current().getDocument().getElementById("main-canvas");
        new TeaApplication(new Inferno(), config).start();
    }
}
