package Data;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    private static Stream<Arguments> legalTensors(){
        return Stream.of(
                Arguments.of(array(1),
                        DoubleArrayList.wrap(new double[]{1}),
                        IntArrayList.wrap(new int[]{1}),
                        IntArrayList.wrap(new int[]{1}),
                        IntArrayList.wrap(new int[]{1})),
                Arguments.of(array(1,2,3),
                        DoubleArrayList.wrap(new double[]{1,2,3}),
                        IntArrayList.wrap(new int[]{3}),
                        IntArrayList.wrap(new int[]{1}),
                        IntArrayList.wrap(new int[]{1})),

                Arguments.of(array(
                        array(1, 2, 3),
                        array(4, 5, 6),
                        array(7, 8, 9)),
                        DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9}),
                        IntArrayList.wrap(new int[]{3,3}),
                        IntArrayList.wrap(new int[]{3,1}),
                        IntArrayList.wrap(new int[]{1,3})),

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
                        IntArrayList.wrap(new int[]{9,3,1}),
                        IntArrayList.wrap(new int[]{1,3,9}))
        );
    }

    @ParameterizedTest
    @MethodSource("legalTensors")
    public void testCreatingTensorArray(List<?> array, DoubleArrayList data, IntArrayList shape, IntArrayList stride, IntArrayList blocks){
        Tensor tensor = (Tensor) context.getBean("tensorArray", array);
        Assertions.assertNotNull(tensor);
        Assertions.assertEquals(tensor.getData(), data);
        Assertions.assertEquals(tensor.getShape(), shape);
        Assertions.assertEquals(tensor.getStride(), stride);
        Assertions.assertEquals(tensor.getBlocks(), blocks);
    }

    @Test
    public void testCreatingTensorDir(){
        DoubleArrayList expectedData;
        IntArrayList expectedShape;
        IntArrayList expectedStride;
        IntArrayList expectedBlocks;
        expectedData = DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        expectedShape = IntArrayList.wrap(new int[]{3,3,3});
        expectedStride = IntArrayList.wrap(new int[]{9,3,1});
        expectedBlocks = IntArrayList.wrap(new int[]{1,3,9});

        // Test creating a tensor by the second constructor
        Tensor tensorByDataAndShape = (Tensor) context.getBean("tensorDir", expectedData, expectedShape);
        Assertions.assertEquals(expectedData, tensorByDataAndShape.getData());
        Assertions.assertEquals(expectedShape, tensorByDataAndShape.getShape());
        Assertions.assertEquals(expectedStride, tensorByDataAndShape.getStride());
        Assertions.assertEquals(expectedBlocks, tensorByDataAndShape.getBlocks());
    }

    // The test tensor being used for following tests
    private Tensor testTensor;
    @BeforeEach
    public void setUpTensor(){
        DoubleArrayList data = DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 2, 2, 3, 4, 5, 5, 7, 8, 1, 3, 3, 4, 5, 6, 6, 8});
        IntArrayList shape = IntArrayList.wrap(new int[]{3,2,4});
        testTensor = (Tensor) context.getBean("tensorDir", data, shape);
    }

    // The getHelper test
    private static Stream<Arguments> getHelperTestCase(){
        return Stream.of(
                //Test discrete
                Arguments.of(0, "[0]", DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8}), IntArrayList.wrap(new int[]{1,2,4})),
                Arguments.of(0, "[0,1]", DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 2, 2, 3, 4, 5, 5, 7, 8}), IntArrayList.wrap(new int[]{2,2,4})),
                Arguments.of(1, "[0,1]", DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 2, 2, 3, 4, 5, 5, 7, 8, 1, 3, 3, 4, 5, 6, 6, 8}), IntArrayList.wrap(new int[]{3,2,4})),
                Arguments.of(2, "[0,2]", DoubleArrayList.wrap(new double[]{1, 3, 5, 7, 2, 3, 5, 7, 1, 3, 5, 6}), IntArrayList.wrap(new int[]{3,2,2})),

                // Test single
                Arguments.of(0, "0", DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8}), IntArrayList.wrap(new int[]{1,2,4})),
                Arguments.of(1, "1", DoubleArrayList.wrap(new double[]{5, 6, 7, 8, 5, 5, 7, 8, 5, 6, 6, 8}), IntArrayList.wrap(new int[]{3,1,4})),
                Arguments.of(2, "2", DoubleArrayList.wrap(new double[]{3, 7, 3, 7, 3, 6}), IntArrayList.wrap(new int[]{3,2,1})),

                // Test continuous
                Arguments.of(0, "0:1", DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8}), IntArrayList.wrap(new int[]{1,2,4})),
                Arguments.of(0, "0:2", DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 2, 2, 3, 4, 5, 5, 7, 8}), IntArrayList.wrap(new int[]{2,2,4})),
                Arguments.of(1, "0:2", DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 2, 2, 3, 4, 5, 5, 7, 8, 1, 3, 3, 4, 5, 6, 6, 8}), IntArrayList.wrap(new int[]{3,2,4})),
                Arguments.of(2, "0:2", DoubleArrayList.wrap(new double[]{1, 2, 5, 6, 2, 2, 5, 5, 1, 3, 5, 6}), IntArrayList.wrap(new int[]{3,2,2})),
                Arguments.of(2, "2:3", DoubleArrayList.wrap(new double[]{3, 7, 3, 7, 3, 6}), IntArrayList.wrap(new int[]{3,2,1}))
                );
    }
    @ParameterizedTest
    @MethodSource("getHelperTestCase")
    public void testGetHelper(int layer, String indices, DoubleArrayList expectedData, IntArrayList expectedShape) throws Exception{
        Method getHelper = Tensor.class.getDeclaredMethod("getHelper", Tensor.class, int.class, String.class);
        getHelper.setAccessible(true);
        Object[] args = {testTensor, layer, indices};
        Tensor result = (Tensor) getHelper.invoke(testTensor, args);

        Assertions.assertEquals(expectedData, result.getData());
        Assertions.assertEquals(expectedShape, result.getShape());
    }

    // The get test
    private static Stream<Arguments> getTestCase(){
        return Stream.of(
                Arguments.of("[0]", DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8}), IntArrayList.wrap(new int[]{2,4})),
                Arguments.of("[:,1]", DoubleArrayList.wrap(new double[]{5, 6, 7, 8, 5, 5, 7, 8, 5, 6, 6, 8}), IntArrayList.wrap(new int[]{3,4})),
                Arguments.of("[[0],1]", DoubleArrayList.wrap(new double[]{5, 6, 7, 8}), IntArrayList.wrap(new int[]{1,4})),
                Arguments.of("[0,1]", DoubleArrayList.wrap(new double[]{5, 6, 7, 8}), IntArrayList.wrap(new int[]{4})),
                Arguments.of("[0][1]", DoubleArrayList.wrap(new double[]{5, 6, 7, 8}), IntArrayList.wrap(new int[]{4})),
                Arguments.of("[[0,2],1]", DoubleArrayList.wrap(new double[]{5, 6, 7, 8, 5, 6, 6, 8}), IntArrayList.wrap(new int[]{2,4})),
                Arguments.of("[:,:,3:]", DoubleArrayList.wrap(new double[]{4, 8, 4, 8, 4, 8}), IntArrayList.wrap(new int[]{3,2,1}))
        );
    }
    @ParameterizedTest
    @MethodSource("getTestCase")
    public void testGet(String lbs, DoubleArrayList expectedData, IntArrayList expectedShape){
        Tensor result = testTensor.get(lbs);
        Assertions.assertEquals(expectedData, result.getData());
        Assertions.assertEquals(expectedShape, result.getShape());
    }

    // Test broadcast
    @Test
    public void testBroadCast() throws Exception {
        Method broadCast = Tensor.class.getDeclaredMethod("broadCast", Tensor.class, Tensor.class);
        broadCast.setAccessible(true);

        Tensor shortTensor = (Tensor) context.getBean("tensorDir", DoubleArrayList.wrap(new double[]{1, 2, 3}), IntArrayList.wrap(new int[]{3}));
        Tensor longTensor = (Tensor) context.getBean("tensorDir", DoubleArrayList.wrap(new double[]{4, 5, 6}), IntArrayList.wrap(new int[]{3}));

        Object[] args = {shortTensor, longTensor};
        Tensor result = (Tensor) broadCast.invoke(shortTensor, args);
        Assertions.assertEquals(shortTensor, result);
    }

    // Test broadcast
    private static Stream<Arguments> broadcastTestCase(){
        return Stream.of(
                Arguments.of(DoubleArrayList.wrap(new double[]{1, 2, 3}), IntArrayList.wrap(new int[]{3}),
                             DoubleArrayList.wrap(new double[]{1, 2, 3, 1, 2, 3}), IntArrayList.wrap(new int[]{2, 3}),
                             DoubleArrayList.wrap(new double[]{1, 2, 3, 1, 2, 3}), IntArrayList.wrap(new int[]{2, 3})),
                Arguments.of(DoubleArrayList.wrap(new double[]{1, 2, 3}), IntArrayList.wrap(new int[]{3}),
                                DoubleArrayList.wrap(new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3}), IntArrayList.wrap(new int[]{3, 1, 3}),
                                DoubleArrayList.wrap(new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3}), IntArrayList.wrap(new int[]{3, 1, 3})),
                Arguments.of(DoubleArrayList.wrap(new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3}), IntArrayList.wrap(new int[]{3, 1, 3}),
                        DoubleArrayList.wrap(new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3}), IntArrayList.wrap(new int[]{3, 2, 3}),
                        DoubleArrayList.wrap(new double[]{1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3}), IntArrayList.wrap(new int[]{3, 2, 3}))
        );
    }
    @ParameterizedTest
    @MethodSource("broadcastTestCase")
    public void testBroadcast(DoubleArrayList shortTensorData, IntArrayList shortTensorShape, DoubleArrayList longTensorData, IntArrayList longTensorShape, DoubleArrayList expectedData, IntArrayList expectedShape) throws Exception {
        Method broadCast = Tensor.class.getDeclaredMethod("broadCast", Tensor.class, Tensor.class);
        broadCast.setAccessible(true);
        Tensor shortTensor = (Tensor) context.getBean("tensorDir", shortTensorData, shortTensorShape);
        Tensor longTensor = (Tensor) context.getBean("tensorDir", longTensorData, longTensorShape);
        Object[] args = {shortTensor, longTensor};
        Tensor result = (Tensor) broadCast.invoke(shortTensor, args);
        Assertions.assertEquals(expectedData, result.getData());
        Assertions.assertEquals(expectedShape, result.getShape());
    }
}
