package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

  @Test
  void testBubbleSort() {
    String input = "11\n13 2 7 8 1 10 6 11 114 4 9\n1\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
    Main.main(new String[0]);
    List<Integer> expected = Arrays.asList(1, 2, 4, 6, 7, 8, 9, 10, 11, 13, 114);
    String output = outputStream.toString().trim();
    String cleanedOutput = output.substring(output.lastIndexOf('['));
    assertEquals(expected.toString(), cleanedOutput);
  }

  @Test
  void testMergeSort() {
    String input = "5\n13 2 7 8 1\n2\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
    Main.main(new String[0]);
    List<Integer> expected = Arrays.asList(1, 2, 7, 8, 13);
    String output = outputStream.toString().trim();
    String cleanedOutput = output.substring(output.lastIndexOf('['));
    assertEquals(expected.toString(), cleanedOutput);
  }

  @Test
  void testEmptyMergeSort() {
    String input = "0\n\n2\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
    Main.main(new String[0]);
    List<Integer> expected = List.of();
    String output = outputStream.toString().trim();
    String cleanedOutput = output.substring(output.lastIndexOf('['));
    assertEquals(expected.toString(), cleanedOutput);
  }

  @Test
  void testInvalidChoice() {
    String input = "5\n13 2 7 8 1\n3\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
    Main.main(new String[0]);
    String expectedOutput = "Неверный ввод";
    String output = outputStream.toString().trim();
    String cleanedOutput = output.substring(output.lastIndexOf("Неверный ввод"));
    assertEquals(expectedOutput, cleanedOutput);
  }
}
