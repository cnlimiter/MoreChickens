package cn.evolvefield.mods.morechickens.common.util.math;

/**
 * Unordered pair of parameterized type for order-agnostic comparison
 * @param <T>
 */
public class UnorderedPair<T> {

    public T first, second;

    public UnorderedPair(T a, T b){
        first = a;
        second = b;
    }

    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UnorderedPair){
            try{
                UnorderedPair<T> other = (UnorderedPair<T>)obj;
                if(first.equals(other.first) && second.equals(other.second))
                    return true;
                if(first.equals(other.second) && second.equals(other.first))
                    return true;
            } catch (Exception e){
                return false;
            }
        }
        return false;
    }
}
