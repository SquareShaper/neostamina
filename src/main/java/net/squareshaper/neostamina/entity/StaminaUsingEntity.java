package net.squareshaper.neostamina.entity;

public interface StaminaUsingEntity {
    int neostamina$getDepletedStaminaRegenerationDelayThreshold();

    int neostamina$getStaminaRegenerationDelayThreshold();

    int neostamina$getStaminaTickThreshold();

    float neostamina$getRegeneratedStamina();

    float neostamina$getStaminaRegeneration();

    float neostamina$getUnreservedStamina();

    float neostamina$getMaxStamina();

    float neostamina$getMaxStaminaAttribute();

    float neostamina$getReservedStamina();

    float neostamina$getItemSingleUseStaminaCost();

    float neostamina$getItemContinuousUseStaminaCost();

    float neostamina$getSprintingTickStaminaCost();

    float neostamina$getWalkingTickStaminaCost();

    float neostamina$getSwimmingTickStaminaCost();

    float neostamina$getWalkingUnderwaterTickStaminaCost();

    float neostamina$getWalkingInWaterTickStaminaCost();

    float neostamina$getClimbingTickStaminaCost();

    float neostamina$getMiningTickStaminaCost();

    float neostamina$getJumpingActionStaminaCost();

    float neostamina$getSprintJumpingActionStaminaCost();

    float neostamina$getAttackActionStaminaCost();

    float neostamina$getInteractionActionStaminaCost();

    float neostamina$getShieldBlockActionStaminaCost();

    void neostamina$addStamina(float amount, boolean shouldPauseRegeneration);

    float neostamina$getStamina();

    void neostamina$setStamina(float stamina);

    void neostamina$setApplyOldStamina(boolean applyOldStamina);

    void neostamina$setApplyMaxStamina(boolean applyMaxStamina);

    void neostamina$setMaxStamina(float maxStamina);

    float neostamina$getMinMaxStamina();

    float neostamina$getHealthToStamina();

    void neostamina$syncStaminaToHealth();

    float neostamina$getCrawlingTickStaminaCost();
}
