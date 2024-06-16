package Data;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static Data.arrayHelper.array;

@SpringBootTest(classes = Tensor.class)
@ContextConfiguration(classes = {TensorConfig.class})
public class TensorTest {
    @Autowired
    //private ApplicationContext context;
    private ApplicationContext context = new AnnotationConfigApplicationContext(TensorConfig.class);


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
        Tensor ordinaryTensor = (Tensor) context.getBean("tensorFromArray", ordinaryArray);
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
        Tensor highDimensionalTensor = (Tensor) context.getBean("tensorFromArray", highDimensionalArray);
        expectedData = DoubleArrayList.wrap(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        expectedShape = IntArrayList.wrap(new int[]{3,3,3});
        expectedStride = IntArrayList.wrap(new int[]{9,3,1});
        Assertions.assertNotNull(highDimensionalTensor);
        Assertions.assertEquals(expectedData, highDimensionalTensor.getData());
        Assertions.assertEquals(expectedShape, highDimensionalTensor.getShape());
        Assertions.assertEquals(expectedStride, highDimensionalTensor.getStride());

        // Test creating a tensor by the second constructor
        Tensor tensorByDataAndShape = (Tensor) context.getBean("tensorFromDataShape", expectedData, expectedShape);
        Assertions.assertEquals(expectedData, tensorByDataAndShape.getData());
        Assertions.assertEquals(expectedShape, tensorByDataAndShape.getShape());
        Assertions.assertEquals(expectedStride, tensorByDataAndShape.getStride());
    }
}
