package com.example.freecam.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public final class FreecamClient implements ClientModInitializer {
    private static KeyBinding toggleKey;
    private static KeyBinding forwardKey;
    private static KeyBinding backKey;
    private static KeyBinding leftKey;
    private static KeyBinding rightKey;
    private static KeyBinding upKey;
    private static KeyBinding downKey;
    private static KeyBinding fasterKey;

    private static boolean active = false;
    private static double camX, camY, camZ;
    private static float camYaw, camPitch;
    private static final double BASE_SPEED = 0.35;

    @Override
    public void onInitializeClient() {
        toggleKey = register("key.simplefreecam.toggle", GLFW.GLFW_KEY_G);
        forwardKey = register("key.simplefreecam.forward", GLFW.GLFW_KEY_W);
        backKey = register("key.simplefreecam.back", GLFW.GLFW_KEY_S);
        leftKey = register("key.simplefreecam.left", GLFW.GLFW_KEY_A);
        rightKey = register("key.simplefreecam.right", GLFW.GLFW_KEY_D);
        upKey = register("key.simplefreecam.up", GLFW.GLFW_KEY_SPACE);
        downKey = register("key.simplefreecam.down", GLFW.GLFW_KEY_LEFT_SHIFT);
        fasterKey = register("key.simplefreecam.faster", GLFW.GLFW_KEY_LEFT_CONTROL);

        ClientTickEvents.END_CLIENT_TICK.register(client -> tick(client));
    }

    private static KeyBinding register(String translationKey, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                translationKey, InputUtil.Type.KEYSYM, key, KeyBinding.Category.MISC));
    }

    private static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            active = false;
            return;
        }

        while (toggleKey.wasPressed()) {
            if (!active) {
                camX = client.player.getX();
                camY = client.player.getEyeY();
                camZ = client.player.getZ();
                camYaw = client.player.getYaw();
                camPitch = client.player.getPitch();
                active = true;
                client.player.sendMessage(Text.literal("Freecam enabled (G to return)"), true);
            } else {
                active = false;
                client.player.sendMessage(Text.literal("Freecam disabled"), true);
            }
        }

        if (!active) return;

        // Keep freecam orientation independent from the player's body rotation.
        camYaw += client.mouse.getX() * 0.15f;
        camPitch += client.mouse.getY() * 0.15f;
        camPitch = MathHelper.clamp(camPitch, -90.0f, 90.0f);

        double speed = fasterKey.isPressed() ? BASE_SPEED * 3.0 : BASE_SPEED;
        double yawRadians = Math.toRadians(camYaw);
        double forwardX = -Math.sin(yawRadians);
        double forwardZ = Math.cos(yawRadians);
        double rightX = Math.cos(yawRadians);
        double rightZ = Math.sin(yawRadians);

        if (forwardKey.isPressed()) { camX += forwardX * speed; camZ += forwardZ * speed; }
        if (backKey.isPressed())    { camX -= forwardX * speed; camZ -= forwardZ * speed; }
        if (rightKey.isPressed())   { camX += rightX * speed; camZ += rightZ * speed; }
        if (leftKey.isPressed())    { camX -= rightX * speed; camZ -= rightZ * speed; }
        if (upKey.isPressed())      { camY += speed; }
        if (downKey.isPressed())    { camY -= speed; }
    }

    public static boolean isActive() { return active; }
    public static double getCamX() { return camX; }
    public static double getCamY() { return camY; }
    public static double getCamZ() { return camZ; }
    public static float getCamYaw() { return camYaw; }
    public static float getCamPitch() { return camPitch; }
}
