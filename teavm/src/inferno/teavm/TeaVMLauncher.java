package inferno.teavm;

import inferno.*;
import io.anuke.arc.backends.teavm.*;
import org.teavm.jso.browser.*;
import org.teavm.jso.dom.html.*;
import org.teavm.jso.dom.xml.*;

public class TeaVMLauncher {
    public static void main(String[] args) {
        Window window = Window.current();
        Document document = window.getDocument();
        TeaVMApplicationConfig config = new TeaVMApplicationConfig();
        config.canvas = (HTMLCanvasElement)document.getElementById("main-canvas");
        new TeaVMApplication(new Inferno(), config).start();
    }
}
