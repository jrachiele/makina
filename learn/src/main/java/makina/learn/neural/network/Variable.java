package makina.learn.neural.network;

import com.google.common.base.Objects;
import makina.math.matrix.Vector;

/**
 * @author Emmanouil Antonios Platanios
 */
abstract class Variable {
    protected final int id;
    protected final String name;
    protected final int size;

    Variable(int id, int size) {
        this(id, String.valueOf(id), size);
    }

    Variable(int id, String name, int size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public int size() {
        return size;
    }

    abstract Vector value(NetworkState state);

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;

        Variable that = (Variable) other;

        return Objects.equal(id, that.id)
                && Objects.equal(name, that.name)
                && Objects.equal(size, that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, size);
    }
}
