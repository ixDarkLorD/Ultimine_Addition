package net.ixdarklord.ultimine_addition.client.handler;

import net.ixdarklord.ultimine_addition.client.event.KeyInputEvents;
import net.ixdarklord.ultimine_addition.client.event.ScreenEvents;

public class ClientEventHandler {
    public static void register() {
        ScreenEvents.init();
        KeyInputEvents.init();
    }
}