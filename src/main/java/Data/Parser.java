package Data;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    /*
    Following methods are essentially indexing and slicing arrays.
    The goal is to fully imitate slicing operations in Python.
    Because Java does not support advanced indexing as in Numpy, the idea is to treat the input as a string,
    using parsers to interpret it.
     */

    /**
     * The method parses parentheses.
     * The general form of indexing can be treated as [Coordinates][Coordinates]...
     * The first step is to separate coordinates.
     * E.g. input = "[0:3][2:][:1][0][0,1][[0,1]]"
     * The output should be {"0:3", "2:", ":1", "0", "0,1","[0,1]"}
     * The mechanism follows as such:
     * initialise brackets as 0
     * Traverse the whole string:
     * If the character c is '[' and brackets == 0 -> we are going to store a new coordinate, do nothing and brackets++
     *                               brackets > 0 -> we meet a bracket inside a bracket, so we need to store the bracket too
     * Similar action on ']', but here we do brackets-- first and check the value of it.
     * E.g.
     * Given input = "[[0,1]]"
     * c = '[', bracket = 0 -> sb = "", bracket = 1
     * c = '[', bracket = 1 -> sb = "[", bracket = 2
     * ...
     * (sb = "[0,1"
     * c = ']', bracket = 2 -> bracket = 1, sb = "[0,1]"
     * c = ']', bracket = 1 -> bracket = 0, sb = "[0,1]", add sb to result, sb = ""
     * @param input the input string to be parsed
     * @return a hashset that stores all coordinates
     */
    protected static List<String> parseParentheses(String input){
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int brackets = 0;
        for (char c : input.toCharArray()) {
            // If it is a bracket inside a bracket, append it, otherwise remove it
            if (c == '['){
                if (brackets > 0){
                    sb.append(c);
                }
                brackets++;
            } else if (c == ']'){
                brackets--;
                // If it is a bracket inside a bracket, append it, otherwise a string is built,
                // add it to the hashset and re-initialise the StringBuilder
                if (brackets > 0){
                    sb.append(c);
                } else{
                    result.add(sb.toString());
                    sb = new StringBuilder();
                }
            } else{
                sb.append(c);
            }
        }
        return result;
    }

    /**
     * The second part parses commas.
     * E.g.
     * 0 -> {0}
     * 0,1 -> {0, 1}
     * [0,1], 1 -> {[0,1], 1}
     * It's worth nothing that commas in brackets are ignored
     * @param input the input string to be parsed
     * @return list of strings
     */
    protected static List<String> parseComma(String input){
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int brackets = 0;
        for (char c : input.toCharArray()) {
            if (c == '[')
                brackets++;
            else if (c == ']')
                brackets--;
            // If meet a comma, add the string to result, initialise sb
            if (c == ',' && brackets == 0){
                result.add(sb.toString());
                sb = new StringBuilder();
                continue;
            }
            sb.append(c);
        }
        result.add(sb.toString());
        return result;
    }

}
