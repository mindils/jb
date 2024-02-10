package ru.mindils.jb.service;

import ru.mindils.jb.common.UtilityClass;

public class MainService {

  public static void main(String[] args) {
    String original = "Hello, World!";
    String transformed = UtilityClass.transformString(original);
    System.out.println(transformed);
  }
}