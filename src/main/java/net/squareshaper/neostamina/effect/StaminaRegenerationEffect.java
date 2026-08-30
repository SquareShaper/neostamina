package net.squareshaper.neostamina.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.squareshaper.neostamina.Neostamina;
import net.squareshaper.neostamina.entity.StaminaUsingEntity;

public class StaminaRegenerationEffect extends StatusEffect {
    public StaminaRegenerationEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof StaminaUsingEntity staminaUsingEntity) {
            staminaUsingEntity.neostamina$addStamina(Neostamina.SERVER_CONFIG.stamina_regeneration_effect * (amplifier + 1), false);
        }
        return super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
