package Data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static Data.arrayHelper.array;

@ExtendWith(MockitoExtension.class)
public class ArrayTest {

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

    @ParameterizedTest
    @MethodSource("legalArrays")
    public void testLegalArrays(List<?> array){
        Assertions.assertNotNull(array);

    }

    @Test
    public void test1DArray(){
        List<?> array1d = array(1,2,3,4,5);
        Assertions.assertNotNull(array1d);
        Assertions.assertEquals(5,array1d.size());
        Assertions.assertEquals("[1, 2, 3, 4, 5]", Arrays.toString(array1d.toArray()));
    }

    @Test
    public void test2DArray(){
        List<?> array2d = array(
                array(1, 2, 3),
                array(4, 5, 6),
                array(7, 8, 9)
        );
        Assertions.assertNotNull(array2d);
        Assertions.assertEquals(3,array2d.size());
        Assertions.assertEquals(3,array2d.get(0));
        Assertions.assertInstanceOf(List.class, array2d.get(0));
        Assertions.assertEquals("[[1, 2, 3], [4, 5, 6], [7, 8, 9]]", Arrays.toString(array2d.toArray()));
    }

    @Test
    public void testHighDimensional(){
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

        Assertions.assertNotNull(highDimensionalArray);
        Assertions.assertEquals(3,highDimensionalArray.size());
        Assertions.assertInstanceOf(List.class, highDimensionalArray.get(0));
        Assertions.assertEquals("[[[1, 2, 3], [4, 5, 6], [7, 8, 9]], [[1, 2, 3], [4, 5, 6], [7, 8, 9]], [[1, 2, 3], [4, 5, 6], [7, 8, 9]]]", Arrays.toString(highDimensionalArray.toArray()));
    }

    @Test
    public void testUnsupportedForm(){
        Assertions.assertThrows(IllegalArgumentException.class, ()->{
            array(
                    array(array(1, 2, 3),
                            array(4, 5, 6),
                            array(7, 8, 9)),
                    array(array(1, 2, 3),
                            array(4, 5),
                            array(7, 8, 9)),
                    array(array(1, 2, 3),
                            array(4, 5, 6),
                            array(7, 8, 9))
            );
        });
    }
}
