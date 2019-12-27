package inferno;

import arc.KeyBinds.*;
import arc.input.InputDevice.DeviceType;
import arc.input.KeyCode;

public enum Binding implements KeyBind{
    move_x(new Axis(KeyCode.A, KeyCode.D)),
    move_y(new Axis(KeyCode.S, KeyCode.W)),
    shoot(KeyCode.MOUSE_LEFT),
    alt(KeyCode.MOUSE_RIGHT);

    private final KeybindValue code;

    Binding(KeybindValue code){
        this.code = code;
    }

    @Override
    public KeybindValue defaultValue(DeviceType type){
        return code;
    }
}
