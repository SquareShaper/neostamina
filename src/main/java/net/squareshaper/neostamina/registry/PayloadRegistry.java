package net.squareshaper.neostamina.registry;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.squareshaper.neostamina.Neostamina;
import net.squareshaper.neostamina.networking.UseStaminaPayload;

public class PayloadRegistry {
    public static final Identifier USE_STAMINA_PAYLOAD_IDENTIFIER = Neostamina.id("use_stamina_payload");
    public static final CustomPayload.Type<? super RegistryByteBuf, UseStaminaPayload> USE_STAMINA_PAYLOAD_TYPE = registerServerBoundPayload(UseStaminaPayload.ID, UseStaminaPayload.CODEC);


    public static void init() {

    }

    public static <T extends CustomPayload> CustomPayload.Type<? super RegistryByteBuf, T> registerServerBoundPayload(CustomPayload.Id<T> id, PacketCodec<? super RegistryByteBuf, T> codec) {
        return PayloadTypeRegistry.playC2S().register(id, codec);
    }
}
