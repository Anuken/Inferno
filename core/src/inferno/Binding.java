package inferno;

import arc.KeyBinds.*;
import arc.input.InputDevice.DeviceType;
import arc.input.KeyCode;

public enum Binding implements KeyBind{
    move_x(new Axis(KeyCode.a, KeyCode.d)),
    move_y(new Axis(KeyCode.s, KeyCode.w)),
    shoot(KeyCode.mouseLeft),
    alt(KeyCode.mouseRight);

    private final KeybindValue code;

    Binding(KeybindValue code){
        this.code = code;
    }

    @Override
    public KeybindValue defaultValue(DeviceType type){
        return code;
    }
}
