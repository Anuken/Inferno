package inferno;

import io.anuke.arc.KeyBinds.*;
import io.anuke.arc.input.InputDevice.DeviceType;
import io.anuke.arc.input.KeyCode;

public enum Binding implements KeyBind{
    move_x(new Axis(KeyCode.A, KeyCode.D)),
    move_y(new Axis(KeyCode.S, KeyCode.W));

    private final KeybindValue code;

    Binding(KeybindValue code){
        this.code = code;
    }

    @Override
    public KeybindValue defaultValue(DeviceType type){
        return code;
    }
}
