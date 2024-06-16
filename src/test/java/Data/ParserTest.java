package Data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import java.util.List;

public class ParserTest {
    @Test
    public void testParseParentheses() {
        String input = "[0:3][2:][:1][0][0,1][[0,1]]";
        List<String> expected = new ArrayList<>();
        expected.add("0:3");
        expected.add("2:");
        expected.add(":1");
        expected.add("0");
        expected.add("0,1");
        expected.add("[0,1]");
        Assertions.assertEquals(expected, Parser.parseParentheses(input));
    }

    @Test
    public void testParseComma(){
        String input = "0,[0,1],:,1:2,:1,-1:";
        List<String> expected = new ArrayList<>();
        expected.add("0");
        expected.add("[0,1]");
        expected.add(":");
        expected.add("1:2");
        expected.add(":1");
        expected.add("-1:");
        Assertions.assertEquals(expected, Parser.parseComma(input));
    }
}
