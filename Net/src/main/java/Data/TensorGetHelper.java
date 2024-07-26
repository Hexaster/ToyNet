package Data;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.List;

/*
This class offers a bunch of helpers for the get method in Tensor.
More details can be found at get() and getHelper() in the Tensor class.
 */

public class TensorGetHelper {

    /**
     * Given a string list of indices with brackets, returns pairs of indices.
     *
     * @param indicesList the list of indices in string
     * @return an array list that contains pairs of (startIndex, endIndex)
     */
    protected static IntArrayList getIndicesFromSbsWithBrackets(Tensor tensor, List<String> indicesList, int layer, int lastStride) {
        IntArrayList startAndEndIndices = new IntArrayList();

        // For indicesList {idx1, idx2, ...}, add data to newData
        for (int block = 0; block < tensor.getBlocks().getInt(layer); block++) {
            for (String index : indicesList) {
                int indexInt = Integer.parseInt(index);
                if (indexInt < 0)
                    indexInt += tensor.getShape().getInt(layer);
                assert indexInt < tensor.getShape().getInt(layer) : "index out of bounds";
                int startIdx = indexInt * tensor.getStride().getInt(layer) + block * lastStride;
                int endIdx = (indexInt + 1) * tensor.getStride().getInt(layer) + block * lastStride;
                startAndEndIndices.add(startIdx);
                startAndEndIndices.add(endIdx);
            }
        }
        return startAndEndIndices;
    }

    /**
     * Given an sb, returns offsets
     */
    protected static IntArrayList getOffsetFromSbsWithoutBrackets(Tensor tensor, String sb, int layer) {
        IntArrayList offsets = new IntArrayList();
        int startOffset;
        int endOffset;

        // Without colons
        if (!sb.contains(":")){
            startOffset = Integer.parseInt(sb);
            if (startOffset < 0)
                startOffset += tensor.getShape().getInt(layer);
            endOffset = startOffset+1;
        }
        // With colons
        else{
            String[] indices = sb.split(":");
            // :
            if (indices.length == 0){
                startOffset = 0;
                endOffset = tensor.getShape().getInt(layer);
            }
            // num:
            else if (indices.length == 1){
                startOffset = Integer.parseInt(indices[0])<0?Integer.parseInt(indices[0])+tensor.getShape().getInt(layer):Integer.parseInt(indices[0]);
                endOffset = tensor.getShape().getInt(layer);
            }
            else if (indices.length == 2){
                // :num
                if (indices[0].isEmpty()){
                    startOffset = 0;
                }
                // num:num
                else{
                    startOffset = Integer.parseInt(indices[0])<0?Integer.parseInt(indices[0])+tensor.getShape().getInt(layer):Integer.parseInt(indices[0]);
                }
                endOffset = Integer.parseInt(indices[1])<0?Integer.parseInt(indices[1])+tensor.getShape().getInt(layer):Integer.parseInt(indices[1]);
            } else{
                throw new IllegalArgumentException("invalid syntax");
            }
        }

        assert startOffset <= endOffset && startOffset < tensor.getShape().getInt(layer) && endOffset <= tensor.getShape().getInt(layer): "index out of bounds";
        offsets.add(startOffset);
        offsets.add(endOffset);
        return offsets;
    }

    /**
     * Transform offsets to indices. The formula is:
     * index = offset*current_stride+block*last_stride
     * @param tensor The given tensor
     * @param layer The layer we are operating
     * @param lastStride Last layer's stride
     * @param startOffset The start offset
     * @param endOffset THe end offset
     * @return An int array list that stores indices with the form {startIdx, endIdx, startIdx, endIdx,...}
     */
    protected static IntArrayList offset2Indices(Tensor tensor, int layer, int lastStride, int startOffset, int endOffset){
        IntArrayList startAndEndIndices = new IntArrayList();
        for (int block = 0; block < tensor.getBlocks().getInt(layer); block++) {
            int startIdx = startOffset*tensor.getStride().getInt(layer)+block*lastStride;
            int endIdx = endOffset*tensor.getStride().getInt(layer)+block*lastStride;
            startAndEndIndices.add(startIdx);
            startAndEndIndices.add(endIdx);
        }
        return startAndEndIndices;
    }

}
