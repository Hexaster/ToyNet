package Data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An array is a list of either lists or numbers.
 * Used to create tensor objects.
 * Form: List<?> matrix = array(
 *                 array(1, 2, 3),
 *                 array(4, 5, 6),
 *                 array(7, 8, 9)
 *                 );
 * The shape of each element must be consistent.
 * E.g.
 * array(
 *  array(1, 2, 3),
 *  array(4, 5)) is illegal.
 */
public class arrayHelper {

    @SafeVarargs
    public static <T> @NotNull List<List<T>> array(List<T>... elements) {
        // If the shape is inconsistent, throw an exception
        int expectedSize = elements[0].size();
        List<List<T>> result = new ArrayList<>(elements.length);
        for (List<T> list : elements) {
            if (list.size() != expectedSize) {
                throw new IllegalArgumentException("element size non-uniform");
            }
            result.add(list);
        }

        return result;
    }

    @Contract("_ -> new")
    public static @NotNull List<Number> array(Number... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

}
