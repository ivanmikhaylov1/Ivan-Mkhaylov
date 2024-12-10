package org.example;

import org.example.sorts.BubbleSort;
import org.example.sorts.MergeSort;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    System.out.print("Введите количество элементов: ");
    int n = input.nextInt();
    List<Integer> numbers = new ArrayList<>();
    System.out.print("Введите элементы: ");
    for (int i = 0; i < n; i++) {
      numbers.add(input.nextInt());
    }
    System.out.print("Выберите алгоритм сортировки: 1 - сортировка слиянием; 2 - сортировка пузырьком ");
    int choice = input.nextInt();
    switch (choice) {
      case 1:
        try {
          BubbleSort bubbleSort = new BubbleSort(50);
          List<Integer> sortedList = bubbleSort.sort(numbers);
          System.out.println(sortedList);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            System.out.println("Ошибка " + e.getMessage());
        }
        break;
      case 2:
        MergeSort mergeSort = new MergeSort();
        List<Integer> sortedList = mergeSort.sort(numbers);
        System.out.println(sortedList);
        break;
      default:
        System.out.println("Неверный ввод");
        break;
    }
  }
}
