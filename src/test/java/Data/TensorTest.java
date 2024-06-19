package Data;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import static Data.arrayHelper.array;

@SpringBootTest(classes = Tensor.class)
@ContextConfiguration(classes = {DataConfig.class})
@ExtendWith(MockitoExtension.class)
public class TensorTest {
    @Autowired
    private ApplicationContext context;

    private DoubleArrayList data;
    private IntArrayList shape;

    private static Stream<Arguments> legalTensors(){
        return Stream.of(
                Arguments.of(array(1,2,3),
                        DoubleArrayList.wrap(new double[]{1,2,3}),
                        IntArrayList.wrap(new int[]{3}),
                        IntArrayList.wrap(new int[]{1})),

                Arguments.of(array(
                        array(1, 2, 3),
                        array(4, 5, 6),
                        array(7, 8, 9)),
                        DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9}),
                        IntArrayList.wrap(new int[]{3,3}),
                        IntArrayList.wrap(new int[]{3,1})),

                Arguments.of(array(
                        array(array(1, 2, 3),
                                array(4, 5, 6),
                                array(7, 8, 9)),
                        array(array(1, 2, 3),
                                array(4, 5, 6),
                                array(7, 8, 9)),
                        array(array(1, 2, 3),
                                array(4, 5, 6),
                                array(7, 8, 9))),
                        DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9}),
                        IntArrayList.wrap(new int[]{3,3,3}),
                        IntArrayList.wrap(new int[]{9,3,1}))
        );
    }

    @ParameterizedTest
    @MethodSource("legalTensors")
    public void testCreatingTensorArray(List<?> array, DoubleArrayList data, IntArrayList shape, IntArrayList stride){
        Tensor tensor = (Tensor) context.getBean("tensorArray", array);
        Assertions.assertNotNull(tensor);
        Assertions.assertEquals(tensor.getData(), data);
        Assertions.assertEquals(tensor.getShape(), shape);
        Assertions.assertEquals(tensor.getStride(), stride);

    }

    @Test
    public void testCreatingTensorDir(){
        DoubleArrayList expectedData;
        IntArrayList expectedShape;
        IntArrayList expectedStride;
        expectedData = DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        expectedShape = IntArrayList.wrap(new int[]{3,3,3});
        expectedStride = IntArrayList.wrap(new int[]{9,3,1});
        // Test creating a tensor by the second constructor
        Tensor tensorByDataAndShape = (Tensor) context.getBean("tensorDir", expectedData, expectedShape);
        Assertions.assertEquals(expectedData, tensorByDataAndShape.getData());
        Assertions.assertEquals(expectedShape, tensorByDataAndShape.getShape());
        Assertions.assertEquals(expectedStride, tensorByDataAndShape.getStride());
    }

    private static Stream<Arguments> indices(){
        return Stream.of(
                Arguments.of(
                        0, "[0]", DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6}), IntArrayList.wrap(new int[]{1,2,3}),
                        0, "[0,1]", DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6}), IntArrayList.wrap(new int[]{2,2,3}),
                        1, "[0,1]", DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6}), IntArrayList.wrap(new int[]{3,2,3}),
                        2, "[0,2]", DoubleArrayList.wrap(new double[]{1, 3, 4, 6, 1, 3, 4, 6, 1, 3, 4, 6}), IntArrayList.wrap(new int[]{3,2,2})
                )
        );
    }

    @ParameterizedTest
    @MethodSource("indices")
    public void testGetHelper(int layer, String indices, DoubleArrayList expectedData, IntArrayList expectedShape) throws Exception{
        DoubleArrayList data = DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6});
        IntArrayList shape = IntArrayList.wrap(new int[]{3,2,3});
        Tensor tensor = (Tensor) context.getBean("tensorDir", data, shape);
        Method getHelper = Tensor.class.getDeclaredMethod("getHelper", Tensor.class, int.class, String.class);
        getHelper.setAccessible(true);

        Object[] args = {tensor, layer, indices};
        Tensor result = (Tensor) getHelper.invoke(tensor, args);

        Assertions.assertEquals(expectedData, result.getData());
        Assertions.assertEquals(expectedShape, result.getShape());
    }

}
