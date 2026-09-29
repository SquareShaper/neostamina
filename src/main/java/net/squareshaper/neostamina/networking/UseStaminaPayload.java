package net.squareshaper.neostamina.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import net.squareshaper.neostamina.registry.PayloadRegistry;

import java.util.UUID;

public record UseStaminaPayload(float amount, boolean blockStaminaRegen) implements CustomPayload {
    public static final CustomPayload.Id<UseStaminaPayload> ID = new CustomPayload.Id<>(PayloadRegistry.USE_STAMINA_PAYLOAD_IDENTIFIER);
    public static final PacketCodec<RegistryByteBuf, UseStaminaPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, UseStaminaPayload::amount,
            PacketCodecs.BOOL, UseStaminaPayload::blockStaminaRegen,
            UseStaminaPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
