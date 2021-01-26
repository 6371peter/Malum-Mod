package com.sammy.malum.common.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class Entangled extends Effect
{
    public Entangled()
    {
        super(EffectType.NEUTRAL, 5926017);
    }
    
    @Override
    public void performEffect(LivingEntity entityLivingBaseIn, int amplifier)
    {
    }
    
    @Override
    public boolean isReady(int duration, int amplifier)
    {
        return false;
    }
}