package com.andoverrobotics.toolbox;

import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.*;

public class SelectorTest {
  private Selector selector;

  @Test
  public void initialSelectedFirst() {
    givenChoices("1", "2", "3");
    assertEquals("1", selector.selected());
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalArgumentWithNoChoices() {
    givenChoices();
  }

  @Test
  public void selectNext() {
    givenChoices("2", "4", "6");
    selector.selectNext();
    assertEquals("4", selector.selected());
  }

  @Test
  public void selectNextWhenLastSelected() {
    givenChoices("A", "B", "C", "D");
    for (int i = 0; i < 3; i++) selector.selectNext();

    selector.selectNext();

    assertEquals("A", selector.selected());
  }

  @Test
  public void allChoices() {
    String[] choices = {"Hello", "World"};
    givenChoices(choices);
    assertArrayEquals(choices, selector.allChoices().toArray(String[]::new));
  }

  private void givenChoices(String... choices) {
    selector = new Selector(Stream.of(choices));
  }
}