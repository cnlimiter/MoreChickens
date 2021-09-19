package cn.evolvefield.mods.morechickens.common.util.math;

import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Uniform-select random pools
 * @param <T>
 */
public class RandomPool<T> {

    private final List<Tuple<Float, T>> pulls;
    private final T fallback;

    public RandomPool(T fb){
        pulls = new ArrayList<>();
        fallback = fb;
    }

    public RandomPool(Tuple<Float, T> ... options){
        pulls = new ArrayList<>();
        pulls.addAll(Arrays.asList(options).subList(1, options.length));
        fallback = options[0].getB();
    }

    public RandomPool add(T opt, float choice){
        pulls.add(new Tuple<>(choice, opt));
        return this;
    }

    public T get(float pull){
        for (Tuple<Float, T> floatTTuple : pulls) {
            pull -= floatTTuple.getA();
            if (pull <= 0)
                return floatTTuple.getB();
        }
        return fallback;
    }

}
