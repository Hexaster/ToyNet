package Data;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static Data.arrayHelper.array;

/*
A tensor has a hierarchical structure. Given data = {d1, d2, ..., dN},
shape = {s1, s2, ..., sn}, stride = {st1, st2, ..., st(n-1),1},
We have:
sti = si*s(i+1)*...*sn; N = s1*st1.
The apex layer (layer 0) is the whole array, then:
The k-th layer has big_pi(i from 1 to k)si blocks, each block has sti elements, i.e.
(d1, d2, ..., d(sti)), (d(st(i+1)), ..., d(2*sti)), ..., big_pi(i from 1 to k)si blocks in total.
In the last layer each element in data forms a block.
Here a block is informally be defined as different parts of a layer. When doing slicing for example, we usually need to
do the same operation on all blocks of a layer.

For example, given a matrix [[1,2,3],[4,5,6],[7,8,9]], in a tensor it will be represented as:
data = [1,2,3,4,5,6,7,8,9], shape = [3,3], stride = [3,1]
The apex layer is the data itself (1,2,3,4,5,6,7,8,9).
The first layer has 3 (s1) blocks, each block has 3 (st1) elements, so the layer is:
(1,2,3),(4,5,6),(7,8,9)
The second layer has 9 (s1*s2) blocks, each block has 1 (st2) elements:
(1),(2),(3),(4),(5),(6),(7),(8),(9)
Such a structure is crucial when manipulating the tensor.
 */

@Component
@Scope(value = "prototype")
public class Tensor {
    private DoubleArrayList data;
    private IntArrayList shape;
    private IntArrayList stride;
    private IntArrayList blocks;
    private final ApplicationContext context = new AnnotationConfigApplicationContext(DataConfig.class);

    @Autowired
    public Tensor(List<?> inputArray) {
        this.data = new DoubleArrayList();
        this.shape = new IntArrayList();
        setData(inputArray);
        setShape(inputArray);
        this.stride = setStride(this.shape);
        this.blocks = setBlocks(this.shape);
    }

    public Tensor(DoubleArrayList data, IntArrayList shape){
        this.data = data;
        this.shape = shape;
        this.stride = setStride(this.shape);
        this.blocks = setBlocks(this.shape);
    }

    //Setters
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
     * @param array a whether-nested-or-not list
     */
    private void setShape(List<?> array){
        this.shape.add(array.size());
        // If not at the innermost layer, call this function recursively
        if ((array.get(0) instanceof List))
            setShape((List<?>) array.get(0));
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

    /**
     * Set the number of blocks of the tensor.
     * Given the shape, the number of blocks of the i-th layer is the multiplication of first i-1 dimensions
     * @param shape the shape list
     * @return the list of blocks
     */
    private IntArrayList setBlocks(IntArrayList shape){
        blocks = new IntArrayList();
        blocks.add(1); // The apex layer always has one block
        for (int d = 0; d < shape.size()-1; d++) {
            blocks.add(blocks.getInt(blocks.size()-1)*shape.getInt(d));
        }
        return blocks;
    }

    /**
     * This method essentially imitates the way of indexing and slicing tensors in Python.
     * To solve such a complicated problem, several parsers are used, please refer to the Parser class.
     * The most complete schema is like this:
     * [[Number, Number, ...], [Number, Number, ...], ...]*n
     * To simplify the representing way, define [Number, Number, ...] as a small bracket (sb),
     * [[Number, Number, ...], [Number, Number, ...], ...] as a large bracket (lb),
     * so a lb can be represented as: [sb1, sb2, ...]
     * Overall mechanism:
     * 1. Parse brackets, from a whole string with n lb's to a list of lb's. We will deal with them
     *    one by one. One lb will only be processed when former ones are fully completed.
     * 2. Parse commas, given one lb, parse it into a series of sb's:
     *    [sb1, sb2, ...] -> {sb1, sb2, ...}
     * 3. There are several cases of sb:
     *    a. [num1, num2, ...], this case indicates we need specific rows num1, num2, ...
     *    b. num, a single number indicates that we only need one row from the array
     *    c. numbers with a colon:
     *       - num: means we need all rows after array[num]
     *       - :num means we need all rows before array[num]
     *       - num1:num2 means we need all rows between num1 and num2 (exclusive)
     *    It's worth nothing that only the first case actually have parentheses and is discrete, maybe we can treat it differently
     * 4. Go back to the set {sb1, sb2, ...}, from sb's we can already get a sub-array deprive of the first big bracket:
     *    As expressed at first, a tensor can be treated as a hierarchical structure, in this set, each sb works on a layer,
     *    sb1 works on the first layer (not the apex layer), sb2 works on the second layer, and so forth. An n-dimensional
     *    array supports n sb's at most.
     *    E.g.
     *    Given an array [[1,2,3], [4,5,6], [7,8,9]] with layers:
     *    (1,2,3),(4,5,6),(7,8,9) and (1),(2),(3),(4),(5),(6),(7),(8),(9)
     *    Say sb1 = 0, so we are doing array[0], the new data should be the first block in the first layer, (1,2,3).
     *    Say sb1 = sb2 = 0, so we are doing array[0,0]. The new data should be the first block of the first block
     *    in the first layer, i.e. the first block of (1,2,3), which is (1).
     *    Say sb1 = :, sb2 = 0, we should get the first block of all blocks from the first layer, (1), (4), (7).
     *    About shape:
     *    sbi affects shape[i], if it is a single number, remove one dimension.
     *    After doing so we already get a new tensor.
     * 5. Step 4 deals with one lb, then call this method recursively for all lb's.
     * @param lbs the string contains multiple lbs
     * @return a new tensor that aligns the rule
     */
    public Tensor get(String lbs){
        // 1. parse lbs
        List<String> listLbs = Parser.parseParentheses(lbs);

        // We process lbs one by one
        for (String sbs : listLbs){
            // 2. parse commas to get a list of sbs
            List<String> listSbs = Parser.parseComma(sbs);

//            for (String sb : listSbs){
//
//            }
        }
        return new Tensor(array(0));
    }

    /**
     * This is a helper method for get() method for continuous sb cases, b and c.
     * Given a sb, first detect whether the string contains a colon.
     * For a specific index, the formula of adding data is:
     * index*stride[layer]+block*stride[layer-1] : (index+1)*stride[layer]+block*stride[layer-1]
     * For a particular layer, DO FOLLOWING ON ALL BLOCKS:
     * If without colon:
     *   The sb should be a single number, suppose the number is i.
     *   The start index should be i*stride[layer]
     *   The length of data should be stride[layer]
     *   Delete shape[layer]
     * If with colon:
     *   If num1:num2:
     *     start index = num1*stride[layer]
     *     end index = num2*stride[layer]
     *   if num1:
     *     end index = shape[layer]*stride[layer]
     *   if :num2:
     *     start index = 0
     * If with brackets:
     *   Should not have any colons
     *   Get data one by one
     *   Can reuse parseParentheses and parseComma
     *   set shape[layer] as the length if the bracket.
     * @param tensor the tensor to be sliced
     * @param layer the layer for operation
     * @param sb the sb with indices
     * @return the tensor after being sliced
     */
    private Tensor getHelper(Tensor tensor, int layer, String sb){
        DoubleArrayList data = tensor.getData();
        IntArrayList shape = tensor.getShape();
        IntArrayList stride = tensor.getStride();
        IntArrayList blocks = tensor.getBlocks();
        DoubleArrayList newData = new DoubleArrayList();

        // If it is the first layer, the stride[layer-1] is always 1
        int lastStride = layer==0?1:stride.getInt(layer-1);

        // With brackets
        if (sb.contains("[")){
            // There should not have any colons in brackets
            assert !sb.contains(":"):"invalid syntax";

            // The form of sb should like [num1, num2, ...], we can parse the brackets
            // Since there's only one set of brackets, the size of the output from parseParentheses is always 1.
            // Then parse commas to get indices.
            String indices = Parser.parseParentheses(sb).get(0);
            List<String> indicesList = Parser.parseComma(indices);

            // For indicesList {idx1, idx2, ...}, add data to newData
            for (int block = 0; block < blocks.getInt(layer); block++){
                for (String index : indicesList){
                    int indexInt = Integer.parseInt(index);
                    if (indexInt < 0)
                        indexInt += shape.getInt(layer);
                    assert indexInt < shape.getInt(layer) : "index out of bounds";
                    int startIdx = indexInt*stride.getInt(layer)+block*lastStride;
                    int endIdx = (indexInt+1)*stride.getInt(layer)+block*lastStride;
                    newData.addAll(data.subList(startIdx, endIdx));
                }
            }

            // Update new shape
            shape.set(layer, indicesList.size());
        }

        // Without brackets
        else{
            String[] indices = sb.split(":");
            int startOffset;
            int endOffset;
            // Without colon
            if (indices.length == 1){
                startOffset = Integer.parseInt(indices[0]);
                if (startOffset < 0)
                    startOffset += shape.getInt(layer);
                endOffset = startOffset+1;
                assert startOffset <= endOffset && startOffset < shape.getInt(layer) && endOffset <= shape.getInt(layer): "index out of bounds";

                shape.removeInt(layer);
            }
            // With a colon
            else if (indices.length == 2){
                startOffset = indices[0].isEmpty()?0:Integer.parseInt(indices[0]);
                endOffset = indices[1].isEmpty()?shape.getInt(layer):Integer.parseInt(indices[1]);
                if (startOffset < 0)
                    startOffset += shape.getInt(layer);
                if (endOffset < 0)
                    endOffset += shape.getInt(layer);
                assert startOffset <= endOffset && startOffset < shape.getInt(layer) && endOffset <= shape.getInt(layer): "index out of bounds";
                shape.set(layer, endOffset - startOffset);
            }
            // With more than one colon, which is illegal.
            else{
                throw new IllegalArgumentException("invalid syntax");
            }

            // For each block, add data to newData
            for (int block = 0; block < blocks.getInt(layer); block++){
                int startIdx = startOffset*stride.getInt(layer)+block*lastStride;
                int endIdx = endOffset*stride.getInt(layer)+block*lastStride;
                newData.addAll(data.subList(startIdx, endIdx));
            }
        }
        return (Tensor) this.context.getBean("tensorDir", newData, shape);
    }


    // Getters
    public IntArrayList getShape() {
        return shape;
    }

    protected DoubleArrayList getData() {
        return data;
    }

    protected IntArrayList getStride() {
        return stride;
    }
    protected IntArrayList getBlocks() {
        return blocks;
    }

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
            if (i > 0)
                sb.append(", ");
            toStringHelper(sb, dim + 1, index + i * this.stride.getInt(dim));
        }
        sb.append("]");
    }
}
