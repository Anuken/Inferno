package inferno.teavm;

import inferno.*;
import io.anuke.arc.*;
import io.anuke.arc.backends.teavm.*;
import io.anuke.arc.backends.teavm.TeaApplication.*;
import io.anuke.arc.graphics.*;
import io.anuke.arc.graphics.VertexAttributes.*;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.graphics.glutils.*;
import org.teavm.jso.browser.*;
import org.teavm.jso.dom.html.*;
import org.teavm.jso.dom.xml.*;

public class TeaVMLauncher {
    public static void main(String[] args) {
        TeaApplicationConfig config = new TeaApplicationConfig();
        config.canvas = (HTMLCanvasElement)Window.current().getDocument().getElementById("main-canvas");
        new TeaApplication(new Inferno(), config).start();
    }
}
