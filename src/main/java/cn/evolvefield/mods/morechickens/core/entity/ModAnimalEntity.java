package cn.evolvefield.mods.morechickens.core.entity;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

public abstract class ModAnimalEntity extends Animal {

    protected ModAnimalEntity(EntityType<? extends Animal> type, Level worldIn) {
        super(type, worldIn);
    }

    protected abstract int getBreedingTimeout();

    @Override
    public void aiStep() {
        super.aiStep();
        int timeout = getBreedingTimeout();
        if(getAge() > timeout)
            setAge(timeout);
        if(timeout > 6000){
            if(getAge() == 5999)
                setAge(timeout);
            else if(getAge() == 6000)
                setAge(5999);
        }
    }

}
