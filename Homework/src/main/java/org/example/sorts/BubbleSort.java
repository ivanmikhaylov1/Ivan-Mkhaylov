package org.example.sorts;

import java.util.List;

public class BubbleSort {
  private final int maxElements;

  public BubbleSort(int maxElements) {
    this.maxElements = maxElements;
  }

  public List<Integer> sort(List<Integer> list) throws IllegalAccessException {
    if (list.size() > maxElements) {
      throw new IllegalAccessException("Превышено максимальное количество элементов (50)");
    }
    for (int i = 0; i < list.size(); i++) {
      for (int j = i + 1; j < list.size(); j++) {
        if (list.get(i) > list.get(j)) {
          int temp = list.get(i);
          list.set(i, list.get(j));
          list.set(j, temp);
        }
      }
    }
    return list;
  }
}
