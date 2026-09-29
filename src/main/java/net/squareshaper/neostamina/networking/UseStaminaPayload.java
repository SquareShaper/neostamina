package net.squareshaper.neostamina.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.squareshaper.neostamina.registry.PayloadRegistry;

public record UseStaminaPayload(float amount, boolean blockStaminaRegen) implements CustomPayload {
    public static final CustomPayload.Id<UseStaminaPayload> ID = new CustomPayload.Id<>(PayloadRegistry.USE_STAMINA_PAYLOAD_IDENTIFIER);
    public static final PacketCodec<PacketByteBuf, UseStaminaPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, UseStaminaPayload::amount,
            PacketCodecs.BOOL, UseStaminaPayload::blockStaminaRegen,
            UseStaminaPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
