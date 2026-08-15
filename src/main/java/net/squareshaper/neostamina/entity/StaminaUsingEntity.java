package net.squareshaper.neostamina.entity;

public interface StaminaUsingEntity {
    int neostamina$getDepletedStaminaRegenerationDelayThreshold();

    int neostamina$getStaminaRegenerationDelayThreshold();

    int neostamina$getStaminaTickThreshold();

    float neostamina$getRegeneratedStamina();

    float neostamina$getStaminaRegeneration();

    float neostamina$getUnreservedStamina();

    float neostamina$getBaseStamina();

    float neostamina$getBoostedStamina();

    float neostamina$getMaxStamina();

    float neostamina$getMaxStaminaChange();

    float neostamina$getReservedStamina();

    float neostamina$getItemUseStaminaCost();

    float neostamina$getSprintingTickStaminaCost();

//    float neostamina$getSneakingTickStaminaCost(); remove sneak costs

    float neostamina$getWalkingTickStaminaCost();

    float neostamina$getSwimmingTickStaminaCost();

    float neostamina$getWalkingUnderwaterTickStaminaCost();

    float neostamina$getWalkingInWaterTickStaminaCost();

    float neostamina$getClimbingTickStaminaCost();

    float neostamina$getJumpingActionStaminaCost();

    float neostamina$getSprintJumpingActionStaminaCost();

    void neostamina$addStamina(float amount);

    float neostamina$getStamina();

    void neostamina$setStamina(float stamina);

    void neostamina$setApplyOldStamina(boolean applyOldStamina);

    void neostamina$setApplyMaxStamina(boolean applyMaxStamina);

    void neostamina$setMaxStamina(float maxStamina);

    void neostamina$addMaxStamina(float maxStamina);

    void neostamina$boostMaxStamina();
}
