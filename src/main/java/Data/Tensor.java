package Data;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope(value = "prototype")
public class Tensor {
    private DoubleArrayList data;
    private IntArrayList shape;
    private IntArrayList stride;

    public Tensor(List<?> inputArray) {
        this.data = new DoubleArrayList();
        this.shape = new IntArrayList();
        setData(inputArray);
        setShape(inputArray);
        this.stride = setStride(this.shape);
    }

    @Autowired
    public Tensor(DoubleArrayList data, IntArrayList shape){
        this.data = data;
        this.shape = shape;
        this.stride = setStride(this.shape);
    }


    //Following methods are called in the constructor to initalise a tensor.

    /**
     * private void initialise(List<?> array)
     * Getting the shape of the array, recursively.
     * Given an array, do following steps:
     * 1. Add the length of the list to this.shape.
     * 2. Check the instance of items. If this is a nested list, call the method itself again.
     * * E.g.
     * Given an array [[1,2,3], [4,5,6], [7,8,9]], this.shape = []
     * 1. add the length of array to this.shape, so this.shape = [3]
     * 2. call getShape(array[0])
     * 3. array[0] is not a nested list, so add the length to this.shape
     * 4. this.shape = [3,3]
     * * @param array a whether-nested-or-not list
     */
    private void setShape(List<?> array){
        this.shape.add(array.size());
        // If not at the innermost layer, call this function recursively
        if ((array.get(0) instanceof List)){
            setShape((List<?>) array.get(0));
        }
    }


    /**
     * Given the input array, store elements to this.data
     * The mechanism is similar to setShape().
     * E.g.
     * [[1,2,3], [4,5,6], [7,8,9]]
     * this.data = [1, 2, 3, 4, 5, 6, 7, 8, 9]
     * For convenience, all numbers are stored in double
     * @param array the input array
     */
    private void setData(List<?> array){
        for (Object element : array){
            if (element instanceof List){
                setData((List<?>)element);
            } else if (element instanceof Number) {
                this.data.add(((Number) element).doubleValue());
            }
        }
    }

    /**
     * Given a list of shape, return the list of stride.
     * First initialise stride as [1], then from the last item of shape,
     * for each item until the second one, do multiplication on the first item of stride,
     * and add it to the first place.
     * <p>
     * E.g.
     * shape = [3, 3, 3]
     * stride = [9, 3, 1]
     * If shape is one-dimensional, the stride is always [1].
     * @param shape An IntArrayList of shape
     * @return An IntArrayList of stride
     */
    private IntArrayList setStride(IntArrayList shape){
        stride = new IntArrayList();
        stride.add(1);
        for (int d = shape.size()-1; d > 0; d--) {
            stride.add(0, stride.getInt(0)*shape.getInt(d));
        }
        return stride;
    }

    // Following methods get sth from a tensor.
    public IntArrayList getShape() {
        return shape;
    }


    public DoubleArrayList getData() {
        return data;
    }

    public IntArrayList getStride() {
        return stride;
    }


    /**
     * A method for printing the tensor with its original shape.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringHelper(sb, 0, 0);
        return "Tensor{" + sb + '}';
    }
    private void toStringHelper(StringBuilder sb, int dim, int index){
        if (dim == this.shape.size()) {
            sb.append(this.data.getDouble(index));
            return;
        }
        sb.append("[");
        for (int i = 0; i < this.shape.getInt(dim); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            toStringHelper(sb, dim + 1, index + i * this.stride.getInt(dim));
        }
        sb.append("]");
    }

}
