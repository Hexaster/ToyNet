package Data;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.stream.Stream;

import static Data.arrayHelper.array;

@SpringBootTest(classes = Tensor.class)
@ContextConfiguration(classes = {DataConfig.class})
@ExtendWith(MockitoExtension.class)
public class TensorTest {
    @Mock
    private ApplicationContext context;

    @Mock
    private Tensor tensor;

    private DoubleArrayList data;
    private IntArrayList shape;

    private static Stream<Arguments> legalArrays(){
        return Stream.of(
                Arguments.of(array(1,2,3)),
                Arguments.of(array(
                        array(1, 2, 3),
                        array(4, 5, 6),
                        array(7, 8, 9)
                )),
                Arguments.of(array(
                        array(array(1, 2, 3),
                                array(4, 5, 6),
                                array(7, 8, 9)),
                        array(array(1, 2, 3),
                                array(4, 5, 6),
                                array(7, 8, 9)),
                        array(array(1, 2, 3),
                                array(4, 5, 6),
                                array(7, 8, 9))
                ))
        );
    }

    @Test
    public void testCreatingTensor(){
        DoubleArrayList expectedData;
        IntArrayList expectedShape;
        IntArrayList expectedStride;

        // Test ordinary array
        List<?> ordinaryArray = array(
                array(1, 2, 3),
                array(4, 5, 6),
                array(7, 8, 9)
        );
        Tensor ordinaryTensor = (Tensor) context.getBean("tensorArray", ordinaryArray);
        expectedData = DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        expectedShape = IntArrayList.wrap(new int[]{3,3});
        expectedStride = IntArrayList.wrap(new int[]{3,1});
        Assertions.assertNotNull(ordinaryTensor);
        Assertions.assertEquals(expectedData, ordinaryTensor.getData());
        Assertions.assertEquals(expectedShape, ordinaryTensor.getShape());
        Assertions.assertEquals(expectedStride, ordinaryTensor.getStride());

        // Higher dimensional test
        List<?> highDimensionalArray = array(
                array(array(1, 2, 3),
                        array(4, 5, 6),
                        array(7, 8, 9)),
                array(array(1, 2, 3),
                        array(4, 5, 6),
                        array(7, 8, 9)),
                array(array(1, 2, 3),
                        array(4, 5, 6),
                        array(7, 8, 9))
        );
        Tensor highDimensionalTensor = (Tensor) context.getBean("tensorArray", highDimensionalArray);
        expectedData = DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        expectedShape = IntArrayList.wrap(new int[]{3,3,3});
        expectedStride = IntArrayList.wrap(new int[]{9,3,1});
        Assertions.assertNotNull(highDimensionalTensor);
        Assertions.assertEquals(expectedData, highDimensionalTensor.getData());
        Assertions.assertEquals(expectedShape, highDimensionalTensor.getShape());
        Assertions.assertEquals(expectedStride, highDimensionalTensor.getStride());

        // Test creating a tensor by the second constructor
        Tensor tensorByDataAndShape = (Tensor) context.getBean("tensorDir", expectedData, expectedShape);
        Assertions.assertEquals(expectedData, tensorByDataAndShape.getData());
        Assertions.assertEquals(expectedShape, tensorByDataAndShape.getShape());
        Assertions.assertEquals(expectedStride, tensorByDataAndShape.getStride());
    }

    @Test
    public void testGetHelper(){
        DoubleArrayList data = DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6});
        IntArrayList shape = IntArrayList.wrap(new int[]{3,2,3});
        Tensor tensor = (Tensor) context.getBean("tensorDir", data,shape);

    }
}
