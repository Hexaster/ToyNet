package Data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static Data.arrayHelper.array;

@ExtendWith(MockitoExtension.class)
public class ArrayTest {

    private static Stream<Arguments> legalArrays(){
        return Stream.of(
                Arguments.of(array(1,2,3),3,"[1, 2, 3]"),
                Arguments.of(array(
                        array(1, 2, 3),
                        array(4, 5, 6),
                        array(7, 8, 9)
                ), 3, "[[1, 2, 3], [4, 5, 6], [7, 8, 9]]"),
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
                ), 3, "[[[1, 2, 3], [4, 5, 6], [7, 8, 9]], [[1, 2, 3], [4, 5, 6], [7, 8, 9]], [[1, 2, 3], [4, 5, 6], [7, 8, 9]]]")
        );
    }

    private static Stream<Arguments> illegalArrays(){
        return Stream.of(
                Arguments.of(IllegalArgumentException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("legalArrays")
    public void testLegalArrays(List<?> array, int size, String expected){
        Assertions.assertNotNull(array);
        Assertions.assertEquals(size, array.size());
        Assertions.assertEquals(expected, array.toString());
    }

    @ParameterizedTest
    @MethodSource("illegalArrays")
    public void testUnsupportedForm(Class<? extends Exception> expectedException){
        Assertions.assertThrows(expectedException, ()->{
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
