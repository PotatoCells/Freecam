package com.example.freecam.mixin;

import com.example.freecam.client.FreecamClient;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Inject(method = "update", at = @At("TAIL"))
    private void simplefreecam$moveCamera(CallbackInfo ci) {
        if (!FreecamClient.isActive()) return;
        Camera camera = (Camera) (Object) this;
        camera.setPos(FreecamClient.getCamX(), FreecamClient.getCamY(), FreecamClient.getCamZ());
        camera.setRotation(FreecamClient.getCamYaw(), FreecamClient.getCamPitch());
    }
}
