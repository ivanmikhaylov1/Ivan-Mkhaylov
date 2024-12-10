package org.example.sorts;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MergeSortTest {
  @Test
  public void testMergeSort() {
    MergeSort mergeSort = new MergeSort();
    List<Integer> expected = Arrays.asList(9, 22, 23, 35, 36, 43, 46, 51, 56, 56, 64, 71, 86, 90, 91);
    List<Integer> actual = Arrays.asList(91, 43, 9, 36, 90, 56, 23, 51, 46, 71, 22, 64, 86, 35, 56);
    assertEquals(expected, mergeSort.sort(actual));
  }

  @Test
  public void testMergeSortEmptyList() {
    MergeSort mergeSort = new MergeSort();
    List<Integer> expected = new ArrayList<>();
    List<Integer> actual = new ArrayList<>();
    assertEquals(expected, mergeSort.sort(actual));
  }

  @Test
  public void testMergeSortSingleElement() {
    MergeSort mergeSort = new MergeSort();
    List<Integer> expected = Arrays.asList(5);
    List<Integer> actual = Arrays.asList(5);
    assertEquals(expected, mergeSort.sort(actual));
  }

  @Test
  public void testMergeSortDuplicateElements() {
    MergeSort mergeSort = new MergeSort();
    List<Integer> expected = Arrays.asList(1, 1, 2, 3, 4, 5);
    List<Integer> actual = Arrays.asList(2, 1, 3, 1, 4, 5);
    assertEquals(expected, mergeSort.sort(actual));
  }

  @Test
  public void testMergeSortNegativeElements() {
    MergeSort mergeSort = new MergeSort();
    List<Integer> expected = Arrays.asList(-5, -3, -2, 0, 1, 2);
    List<Integer> actual = Arrays.asList(1, -3, 2, 0, -5, -2);
    assertEquals(expected, mergeSort.sort(actual));
  }

  @Test
  public void testMergeSortAlreadySorted() {
    MergeSort mergeSort = new MergeSort();
    List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);
    List<Integer> actual = Arrays.asList(1, 2, 3, 4, 5);
    assertEquals(expected, mergeSort.sort(actual));
  }
}