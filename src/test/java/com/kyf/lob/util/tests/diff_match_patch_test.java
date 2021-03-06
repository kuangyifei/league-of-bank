/*
 * Test harness for diff_match_patch.java
 *
 * Copyright 2006 Google Inc.
 * http://code.google.com/p/google-diff-match-patch/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kyf.lob.util.tests;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.kyf.common.util.textSync.diff_match_patch.Diff;
import com.kyf.common.util.textSync.diff_match_patch;
import com.kyf.common.util.textSync.diff_match_patch.Patch;
import org.junit.Assert;
import org.junit.Test;

public class diff_match_patch_test {

  private diff_match_patch dmp;
  private diff_match_patch.Operation DELETE = diff_match_patch.Operation.DELETE;
  private diff_match_patch.Operation EQUAL = diff_match_patch.Operation.EQUAL;
  private diff_match_patch.Operation INSERT = diff_match_patch.Operation.INSERT;

  public diff_match_patch_test() {
    // Create an instance of the diff_match_patch object.
    dmp = new diff_match_patch();
  }


  //  DIFF TEST FUNCTIONS


  @Test
  public void testDiffCommonPrefix() {
    // Detect any common prefix.
    Assert.assertEquals("diff_commonPrefix: Null case.", 0, dmp.diff_commonPrefix("abc", "xyz"));

    Assert.assertEquals("diff_commonPrefix: Non-null case.", 4, dmp.diff_commonPrefix("1234abcdef", "1234xyz"));

    Assert.assertEquals("diff_commonPrefix: Whole case.", 4, dmp.diff_commonPrefix("1234", "1234xyz"));
  }

  @Test
  public void testDiffCommonSuffix() {
    // Detect any common suffix.
    Assert.assertEquals("diff_commonSuffix: Null case.", 0, dmp.diff_commonSuffix("abc", "xyz"));

    Assert.assertEquals("diff_commonSuffix: Non-null case.", 4, dmp.diff_commonSuffix("abcdef1234", "xyz1234"));

    Assert.assertEquals("diff_commonSuffix: Whole case.", 4, dmp.diff_commonSuffix("1234", "xyz1234"));
  }

  @Test
  public void testDiffCleanupMerge() {
    // Cleanup a messy diff.
    LinkedList<Diff> diffs = diffList();
    dmp.diff_cleanupMerge(diffs);
    Assert.assertEquals("diff_cleanupMerge: Null case.", diffList(), diffs);

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "b"), new Diff(INSERT, "c"));
    dmp.diff_cleanupMerge(diffs);
    Assert.assertEquals("diff_cleanupMerge: No change case.", diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "b"), new Diff(INSERT, "c")), diffs);

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(EQUAL, "b"), new Diff(EQUAL, "c"));
    dmp.diff_cleanupMerge(diffs);
    Assert.assertEquals("diff_cleanupMerge: Merge equalities.", diffList(new Diff(EQUAL, "abc")), diffs);

    diffs = diffList(new Diff(DELETE, "a"), new Diff(DELETE, "b"), new Diff(DELETE, "c"));
    dmp.diff_cleanupMerge(diffs);
    Assert.assertEquals("diff_cleanupMerge: Merge deletions.", diffList(new Diff(DELETE, "abc")), diffs);

    diffs = diffList(new Diff(INSERT, "a"), new Diff(INSERT, "b"), new Diff(INSERT, "c"));
    dmp.diff_cleanupMerge(diffs);
    Assert.assertEquals("diff_cleanupMerge: Merge insertions.", diffList(new Diff(INSERT, "abc")), diffs);

    diffs = diffList(new Diff(DELETE, "a"), new Diff(INSERT, "b"), new Diff(DELETE, "c"), new Diff(INSERT, "d"), new Diff(EQUAL, "e"), new Diff(EQUAL, "f"));
    dmp.diff_cleanupMerge(diffs);
    Assert.assertEquals("diff_cleanupMerge: Merge interweave.", diffList(new Diff(DELETE, "ac"), new Diff(INSERT, "bd"), new Diff(EQUAL, "ef")), diffs);

    diffs = diffList(new Diff(DELETE, "a"), new Diff(INSERT, "abc"), new Diff(DELETE, "dc"));
    dmp.diff_cleanupMerge(diffs);
    Assert.assertEquals("diff_cleanupMerge: Prefix and suffix detection.", diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "d"), new Diff(INSERT, "b"), new Diff(EQUAL, "c")), diffs);

    diffs = diffList(new Diff(EQUAL, "x"), new Diff(DELETE, "a"), new Diff(INSERT, "abc"), new Diff(DELETE, "dc"), new Diff(EQUAL, "y"));
    dmp.diff_cleanupMerge(diffs);
    Assert.assertEquals("diff_cleanupMerge: Prefix and suffix detection with equalities.", diffList(new Diff(EQUAL, "xa"), new Diff(DELETE, "d"), new Diff(INSERT, "b"), new Diff(EQUAL, "cy")), diffs);

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(INSERT, "ba"), new Diff(EQUAL, "c"));
    dmp.diff_cleanupMerge(diffs);
    Assert.assertEquals("diff_cleanupMerge: Slide edit left.", diffList(new Diff(INSERT, "ab"), new Diff(EQUAL, "ac")), diffs);

    diffs = diffList(new Diff(EQUAL, "c"), new Diff(INSERT, "ab"), new Diff(EQUAL, "a"));
    dmp.diff_cleanupMerge(diffs);
    Assert.assertEquals("diff_cleanupMerge: Slide edit right.", diffList(new Diff(EQUAL, "ca"), new Diff(INSERT, "ba")), diffs);

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "b"), new Diff(EQUAL, "c"), new Diff(DELETE, "ac"), new Diff(EQUAL, "x"));
    dmp.diff_cleanupMerge(diffs);
    Assert.assertEquals("diff_cleanupMerge: Slide edit left recursive.", diffList(new Diff(DELETE, "abc"), new Diff(EQUAL, "acx")), diffs);

    diffs = diffList(new Diff(EQUAL, "x"), new Diff(DELETE, "ca"), new Diff(EQUAL, "c"), new Diff(DELETE, "b"), new Diff(EQUAL, "a"));
    dmp.diff_cleanupMerge(diffs);
    Assert.assertEquals("diff_cleanupMerge: Slide edit right recursive.", diffList(new Diff(EQUAL, "xca"), new Diff(DELETE, "cba")), diffs);
  }

  @Test
  public void testDiffCleanupSemanticLossless() {
    // Slide diffs to match logical boundaries.
    LinkedList<Diff> diffs = diffList();
    dmp.diff_cleanupSemanticLossless(diffs);
    Assert.assertEquals("diff_cleanupSemanticLossless: Null case.", diffList(), diffs);

    diffs = diffList(new Diff(EQUAL, "AAA\r\n\r\nBBB"), new Diff(INSERT, "\r\nDDD\r\n\r\nBBB"), new Diff(EQUAL, "\r\nEEE"));
    dmp.diff_cleanupSemanticLossless(diffs);
    Assert.assertEquals("diff_cleanupSemanticLossless: Blank lines.", diffList(new Diff(EQUAL, "AAA\r\n\r\n"), new Diff(INSERT, "BBB\r\nDDD\r\n\r\n"), new Diff(EQUAL, "BBB\r\nEEE")), diffs);

    diffs = diffList(new Diff(EQUAL, "AAA\r\nBBB"), new Diff(INSERT, " DDD\r\nBBB"), new Diff(EQUAL, " EEE"));
    dmp.diff_cleanupSemanticLossless(diffs);
    Assert.assertEquals("diff_cleanupSemanticLossless: Line boundaries.", diffList(new Diff(EQUAL, "AAA\r\n"), new Diff(INSERT, "BBB DDD\r\n"), new Diff(EQUAL, "BBB EEE")), diffs);

    diffs = diffList(new Diff(EQUAL, "The c"), new Diff(INSERT, "ow and the c"), new Diff(EQUAL, "at."));
    dmp.diff_cleanupSemanticLossless(diffs);
    Assert.assertEquals("diff_cleanupSemanticLossless: Word boundaries.", diffList(new Diff(EQUAL, "The "), new Diff(INSERT, "cow and the "), new Diff(EQUAL, "cat.")), diffs);

    diffs = diffList(new Diff(EQUAL, "The-c"), new Diff(INSERT, "ow-and-the-c"), new Diff(EQUAL, "at."));
    dmp.diff_cleanupSemanticLossless(diffs);
    Assert.assertEquals("diff_cleanupSemanticLossless: Alphanumeric boundaries.", diffList(new Diff(EQUAL, "The-"), new Diff(INSERT, "cow-and-the-"), new Diff(EQUAL, "cat.")), diffs);

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "a"), new Diff(EQUAL, "ax"));
    dmp.diff_cleanupSemanticLossless(diffs);
    Assert.assertEquals("diff_cleanupSemanticLossless: Hitting the start.", diffList(new Diff(DELETE, "a"), new Diff(EQUAL, "aax")), diffs);

    diffs = diffList(new Diff(EQUAL, "xa"), new Diff(DELETE, "a"), new Diff(EQUAL, "a"));
    dmp.diff_cleanupSemanticLossless(diffs);
    Assert.assertEquals("diff_cleanupSemanticLossless: Hitting the end.", diffList(new Diff(EQUAL, "xaa"), new Diff(DELETE, "a")), diffs);

    diffs = diffList(new Diff(EQUAL, "The xxx. The "), new Diff(INSERT, "zzz. The "), new Diff(EQUAL, "yyy."));
    dmp.diff_cleanupSemanticLossless(diffs);
    Assert.assertEquals("diff_cleanupSemanticLossless: Sentence boundaries.", diffList(new Diff(EQUAL, "The xxx."), new Diff(INSERT, " The zzz."), new Diff(EQUAL, " The yyy.")), diffs);
  }

  @Test
  public void testDiffCleanupSemantic() {
    // Cleanup semantically trivial equalities.
    LinkedList<Diff> diffs = diffList();
    dmp.diff_cleanupSemantic(diffs);
    Assert.assertEquals("diff_cleanupSemantic: Null case.", diffList(), diffs);

    diffs = diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "cd"), new Diff(EQUAL, "12"), new Diff(DELETE, "e"));
    dmp.diff_cleanupSemantic(diffs);
    Assert.assertEquals("diff_cleanupSemantic: No elimination #1.", diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "cd"), new Diff(EQUAL, "12"), new Diff(DELETE, "e")), diffs);

    diffs = diffList(new Diff(DELETE, "abc"), new Diff(INSERT, "ABC"), new Diff(EQUAL, "1234"), new Diff(DELETE, "wxyz"));
    dmp.diff_cleanupSemantic(diffs);
    Assert.assertEquals("diff_cleanupSemantic: No elimination #2.", diffList(new Diff(DELETE, "abc"), new Diff(INSERT, "ABC"), new Diff(EQUAL, "1234"), new Diff(DELETE, "wxyz")), diffs);

    diffs = diffList(new Diff(DELETE, "a"), new Diff(EQUAL, "b"), new Diff(DELETE, "c"));
    dmp.diff_cleanupSemantic(diffs);
    Assert.assertEquals("diff_cleanupSemantic: Simple elimination.", diffList(new Diff(DELETE, "abc"), new Diff(INSERT, "b")), diffs);

    diffs = diffList(new Diff(DELETE, "ab"), new Diff(EQUAL, "cd"), new Diff(DELETE, "e"), new Diff(EQUAL, "f"), new Diff(INSERT, "g"));
    dmp.diff_cleanupSemantic(diffs);
    Assert.assertEquals("diff_cleanupSemantic: Backpass elimination.", diffList(new Diff(DELETE, "abcdef"), new Diff(INSERT, "cdfg")), diffs);

    diffs = diffList(new Diff(INSERT, "1"), new Diff(EQUAL, "A"), new Diff(DELETE, "B"), new Diff(INSERT, "2"), new Diff(EQUAL, "_"), new Diff(INSERT, "1"), new Diff(EQUAL, "A"), new Diff(DELETE, "B"), new Diff(INSERT, "2"));
    dmp.diff_cleanupSemantic(diffs);
    Assert.assertEquals("diff_cleanupSemantic: Multiple elimination.", diffList(new Diff(DELETE, "AB_AB"), new Diff(INSERT, "1A2_1A2")), diffs);

    diffs = diffList(new Diff(EQUAL, "The c"), new Diff(DELETE, "ow and the c"), new Diff(EQUAL, "at."));
    dmp.diff_cleanupSemantic(diffs);
    Assert.assertEquals("diff_cleanupSemantic: Word boundaries.", diffList(new Diff(EQUAL, "The "), new Diff(DELETE, "cow and the "), new Diff(EQUAL, "cat.")), diffs);

    diffs = diffList(new Diff(DELETE, "abcxx"), new Diff(INSERT, "xxdef"));
    dmp.diff_cleanupSemantic(diffs);
    Assert.assertEquals("diff_cleanupSemantic: No overlap elimination.", diffList(new Diff(DELETE, "abcxx"), new Diff(INSERT, "xxdef")), diffs);

    diffs = diffList(new Diff(DELETE, "abcxxx"), new Diff(INSERT, "xxxdef"));
    dmp.diff_cleanupSemantic(diffs);
    Assert.assertEquals("diff_cleanupSemantic: Overlap elimination.", diffList(new Diff(DELETE, "abc"), new Diff(EQUAL, "xxx"), new Diff(INSERT, "def")), diffs);

    diffs = diffList(new Diff(DELETE, "xxxabc"), new Diff(INSERT, "defxxx"));
    dmp.diff_cleanupSemantic(diffs);
    Assert.assertEquals("diff_cleanupSemantic: Reverse overlap elimination.", diffList(new Diff(INSERT, "def"), new Diff(EQUAL, "xxx"), new Diff(DELETE, "abc")), diffs);

    diffs = diffList(new Diff(DELETE, "abcd1212"), new Diff(INSERT, "1212efghi"), new Diff(EQUAL, "----"), new Diff(DELETE, "A3"), new Diff(INSERT, "3BC"));
    dmp.diff_cleanupSemantic(diffs);
    Assert.assertEquals("diff_cleanupSemantic: Two overlap eliminations.", diffList(new Diff(DELETE, "abcd"), new Diff(EQUAL, "1212"), new Diff(INSERT, "efghi"), new Diff(EQUAL, "----"), new Diff(DELETE, "A"), new Diff(EQUAL, "3"), new Diff(INSERT, "BC")), diffs);
  }

  @Test
  public void testDiffCleanupEfficiency() {
    // Cleanup operationally trivial equalities.
    dmp.Diff_EditCost = 4;
    LinkedList<Diff> diffs = diffList();
    dmp.diff_cleanupEfficiency(diffs);
    Assert.assertEquals("diff_cleanupEfficiency: Null case.", diffList(), diffs);

    diffs = diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "12"), new Diff(EQUAL, "wxyz"), new Diff(DELETE, "cd"), new Diff(INSERT, "34"));
    dmp.diff_cleanupEfficiency(diffs);
    Assert.assertEquals("diff_cleanupEfficiency: No elimination.", diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "12"), new Diff(EQUAL, "wxyz"), new Diff(DELETE, "cd"), new Diff(INSERT, "34")), diffs);

    diffs = diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "12"), new Diff(EQUAL, "xyz"), new Diff(DELETE, "cd"), new Diff(INSERT, "34"));
    dmp.diff_cleanupEfficiency(diffs);
    Assert.assertEquals("diff_cleanupEfficiency: Four-edit elimination.", diffList(new Diff(DELETE, "abxyzcd"), new Diff(INSERT, "12xyz34")), diffs);

    diffs = diffList(new Diff(INSERT, "12"), new Diff(EQUAL, "x"), new Diff(DELETE, "cd"), new Diff(INSERT, "34"));
    dmp.diff_cleanupEfficiency(diffs);
    Assert.assertEquals("diff_cleanupEfficiency: Three-edit elimination.", diffList(new Diff(DELETE, "xcd"), new Diff(INSERT, "12x34")), diffs);

    diffs = diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "12"), new Diff(EQUAL, "xy"), new Diff(INSERT, "34"), new Diff(EQUAL, "z"), new Diff(DELETE, "cd"), new Diff(INSERT, "56"));
    dmp.diff_cleanupEfficiency(diffs);
    Assert.assertEquals("diff_cleanupEfficiency: Backpass elimination.", diffList(new Diff(DELETE, "abxyzcd"), new Diff(INSERT, "12xy34z56")), diffs);

    dmp.Diff_EditCost = 5;
    diffs = diffList(new Diff(DELETE, "ab"), new Diff(INSERT, "12"), new Diff(EQUAL, "wxyz"), new Diff(DELETE, "cd"), new Diff(INSERT, "34"));
    dmp.diff_cleanupEfficiency(diffs);
    Assert.assertEquals("diff_cleanupEfficiency: High cost elimination.", diffList(new Diff(DELETE, "abwxyzcd"), new Diff(INSERT, "12wxyz34")), diffs);
    dmp.Diff_EditCost = 4;
  }

  @Test
  public void testDiffPrettyHtml() {
    // Pretty print.
    LinkedList<Diff> diffs = diffList(new Diff(EQUAL, "a\n"), new Diff(DELETE, "<B>b</B>"), new Diff(INSERT, "c&d"));
    Assert.assertEquals("diff_prettyHtml:", "<span>a&para;<br></span><del style=\"background:#ffe6e6;\">&lt;B&gt;b&lt;/B&gt;</del><ins style=\"background:#e6ffe6;\">c&amp;d</ins>", dmp.diff_prettyHtml(diffs));
  }

  @Test
  public void testDiffText() {
    // Compute the source and destination texts.
    LinkedList<Diff> diffs = diffList(new Diff(EQUAL, "jump"), new Diff(DELETE, "s"), new Diff(INSERT, "ed"), new Diff(EQUAL, " over "), new Diff(DELETE, "the"), new Diff(INSERT, "a"), new Diff(EQUAL, " lazy"));
    Assert.assertEquals("diff_text1:", "jumps over the lazy", dmp.diff_text1(diffs));
    Assert.assertEquals("diff_text2:", "jumped over a lazy", dmp.diff_text2(diffs));
  }

  @Test
  public void testDiffDelta() {
    // Convert a diff into delta string.
    LinkedList<Diff> diffs = diffList(new Diff(EQUAL, "jump"), new Diff(DELETE, "s"), new Diff(INSERT, "ed"), new Diff(EQUAL, " over "), new Diff(DELETE, "the"), new Diff(INSERT, "a"), new Diff(EQUAL, " lazy"), new Diff(INSERT, "old dog"));
    String text1 = dmp.diff_text1(diffs);
    Assert.assertEquals("diff_text1: Base text.", "jumps over the lazy", text1);

    String delta = dmp.diff_toDelta(diffs);
    Assert.assertEquals("diff_toDelta:", "=4\t-1\t+ed\t=6\t-3\t+a\t=5\t+old dog", delta);

    // Convert delta string into a diff.
    Assert.assertEquals("diff_fromDelta: Normal.", diffs, dmp.diff_fromDelta(text1, delta));

    // Generates error (19 < 20).
    try {
      dmp.diff_fromDelta(text1 + "x", delta);
      Assert.fail("diff_fromDelta: Too long.");
    } catch (IllegalArgumentException ex) {
      // Exception expected.
    }

    // Generates error (19 > 18).
    try {
      dmp.diff_fromDelta(text1.substring(1), delta);
      Assert.fail("diff_fromDelta: Too short.");
    } catch (IllegalArgumentException ex) {
      // Exception expected.
    }

    // Generates error (%c3%xy invalid Unicode).
    try {
      dmp.diff_fromDelta("", "+%c3%xy");
      Assert.fail("diff_fromDelta: Invalid character.");
    } catch (IllegalArgumentException ex) {
      // Exception expected.
    }

    // Test deltas with special characters.
    diffs = diffList(new Diff(EQUAL, "\u0680 \000 \t %"), new Diff(DELETE, "\u0681 \001 \n ^"), new Diff(INSERT, "\u0682 \002 \\ |"));
    text1 = dmp.diff_text1(diffs);
    Assert.assertEquals("diff_text1: Unicode text.", "\u0680 \000 \t %\u0681 \001 \n ^", text1);

    delta = dmp.diff_toDelta(diffs);
    Assert.assertEquals("diff_toDelta: Unicode.", "=7\t-7\t+%DA%82 %02 %5C %7C", delta);

    Assert.assertEquals("diff_fromDelta: Unicode.", diffs, dmp.diff_fromDelta(text1, delta));

    // Verify pool of unchanged characters.
    diffs = diffList(new Diff(INSERT, "A-Z a-z 0-9 - _ . ! ~ * ' ( ) ; / ? : @ & = + $ , # "));
    String text2 = dmp.diff_text2(diffs);
    Assert.assertEquals("diff_text2: Unchanged characters.", "A-Z a-z 0-9 - _ . ! ~ * \' ( ) ; / ? : @ & = + $ , # ", text2);

    delta = dmp.diff_toDelta(diffs);
    Assert.assertEquals("diff_toDelta: Unchanged characters.", "+A-Z a-z 0-9 - _ . ! ~ * \' ( ) ; / ? : @ & = + $ , # ", delta);

    // Convert delta string into a diff.
    Assert.assertEquals("diff_fromDelta: Unchanged characters.", diffs, dmp.diff_fromDelta("", delta));
  }

  @Test
  public void testDiffXIndex() {
    // Translate a location in text1 to text2.
    LinkedList<Diff> diffs = diffList(new Diff(DELETE, "a"), new Diff(INSERT, "1234"), new Diff(EQUAL, "xyz"));
    Assert.assertEquals("diff_xIndex: Translation on equality.", 5, dmp.diff_xIndex(diffs, 2));

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "1234"), new Diff(EQUAL, "xyz"));
    Assert.assertEquals("diff_xIndex: Translation on deletion.", 1, dmp.diff_xIndex(diffs, 3));
  }

  @Test
  public void testDiffLevenshtein() {
    LinkedList<Diff> diffs = diffList(new Diff(DELETE, "abc"), new Diff(INSERT, "1234"), new Diff(EQUAL, "xyz"));
    Assert.assertEquals("Levenshtein with trailing equality.", 4, dmp.diff_levenshtein(diffs));

    diffs = diffList(new Diff(EQUAL, "xyz"), new Diff(DELETE, "abc"), new Diff(INSERT, "1234"));
    Assert.assertEquals("Levenshtein with leading equality.", 4, dmp.diff_levenshtein(diffs));

    diffs = diffList(new Diff(DELETE, "abc"), new Diff(EQUAL, "xyz"), new Diff(INSERT, "1234"));
    Assert.assertEquals("Levenshtein with middle equality.", 7, dmp.diff_levenshtein(diffs));
  }

  @Test
  public void testDiffMain() {
    // Perform a trivial diff.
    LinkedList<Diff> diffs = diffList();
    Assert.assertEquals("diff_main: Null case.", diffs, dmp.diff_main("", "", false));

    diffs = diffList(new Diff(EQUAL, "abc"));
    Assert.assertEquals("diff_main: Equality.", diffs, dmp.diff_main("abc", "abc", false));

    diffs = diffList(new Diff(EQUAL, "ab"), new Diff(INSERT, "123"), new Diff(EQUAL, "c"));
    Assert.assertEquals("diff_main: Simple insertion.", diffs, dmp.diff_main("abc", "ab123c", false));

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "123"), new Diff(EQUAL, "bc"));
    Assert.assertEquals("diff_main: Simple deletion.", diffs, dmp.diff_main("a123bc", "abc", false));

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(INSERT, "123"), new Diff(EQUAL, "b"), new Diff(INSERT, "456"), new Diff(EQUAL, "c"));
    Assert.assertEquals("diff_main: Two insertions.", diffs, dmp.diff_main("abc", "a123b456c", false));

    diffs = diffList(new Diff(EQUAL, "a"), new Diff(DELETE, "123"), new Diff(EQUAL, "b"), new Diff(DELETE, "456"), new Diff(EQUAL, "c"));
    Assert.assertEquals("diff_main: Two deletions.", diffs, dmp.diff_main("a123b456c", "abc", false));

    // Perform a real diff.
    // Switch off the timeout.
    dmp.Diff_Timeout = 0;
    diffs = diffList(new Diff(DELETE, "a"), new Diff(INSERT, "b"));
    Assert.assertEquals("diff_main: Simple case #1.", diffs, dmp.diff_main("a", "b", false));

    diffs = diffList(new Diff(DELETE, "Apple"), new Diff(INSERT, "Banana"), new Diff(EQUAL, "s are a"), new Diff(INSERT, "lso"), new Diff(EQUAL, " fruit."));
    Assert.assertEquals("diff_main: Simple case #2.", diffs, dmp.diff_main("Apples are a fruit.", "Bananas are also fruit.", false));

    diffs = diffList(new Diff(DELETE, "a"), new Diff(INSERT, "\u0680"), new Diff(EQUAL, "x"), new Diff(DELETE, "\t"), new Diff(INSERT, "\000"));
    Assert.assertEquals("diff_main: Simple case #3.", diffs, dmp.diff_main("ax\t", "\u0680x\000", false));

    diffs = diffList(new Diff(DELETE, "1"), new Diff(EQUAL, "a"), new Diff(DELETE, "y"), new Diff(EQUAL, "b"), new Diff(DELETE, "2"), new Diff(INSERT, "xab"));
    Assert.assertEquals("diff_main: Overlap #1.", diffs, dmp.diff_main("1ayb2", "abxab", false));

    diffs = diffList(new Diff(INSERT, "xaxcx"), new Diff(EQUAL, "abc"), new Diff(DELETE, "y"));
    Assert.assertEquals("diff_main: Overlap #2.", diffs, dmp.diff_main("abcy", "xaxcxabc", false));

    diffs = diffList(new Diff(DELETE, "ABCD"), new Diff(EQUAL, "a"), new Diff(DELETE, "="), new Diff(INSERT, "-"), new Diff(EQUAL, "bcd"), new Diff(DELETE, "="), new Diff(INSERT, "-"), new Diff(EQUAL, "efghijklmnopqrs"), new Diff(DELETE, "EFGHIJKLMNOefg"));
    Assert.assertEquals("diff_main: Overlap #3.", diffs, dmp.diff_main("ABCDa=bcd=efghijklmnopqrsEFGHIJKLMNOefg", "a-bcd-efghijklmnopqrs", false));

    diffs = diffList(new Diff(INSERT, " "), new Diff(EQUAL, "a"), new Diff(INSERT, "nd"), new Diff(EQUAL, " [[Pennsylvania]]"), new Diff(DELETE, " and [[New"));
    Assert.assertEquals("diff_main: Large equality.", diffs, dmp.diff_main("a [[Pennsylvania]] and [[New", " and [[Pennsylvania]]", false));

    dmp.Diff_Timeout = 0.1f;  // 100ms
    String a = "`Twas brillig, and the slithy toves\nDid gyre and gimble in the wabe:\nAll mimsy were the borogoves,\nAnd the mome raths outgrabe.\n";
    String b = "I am the very model of a modern major general,\nI've information vegetable, animal, and mineral,\nI know the kings of England, and I quote the fights historical,\nFrom Marathon to Waterloo, in order categorical.\n";
    // Increase the text lengths by 1024 times to ensure a timeout.
    for (int x = 0; x < 10; x++) {
      a = a + a;
      b = b + b;
    }
    long startTime = System.currentTimeMillis();
    dmp.diff_main(a, b);
    long endTime = System.currentTimeMillis();
    // Test that we took at least the timeout period.
    Assert.assertTrue("diff_main: Timeout min.", dmp.Diff_Timeout * 1000 <= endTime - startTime);
    // Test that we didn't take forever (be forgiving).
    // Theoretically this test could fail very occasionally if the
    // OS task swaps or locks up for a second at the wrong moment.
    Assert.assertTrue("diff_main: Timeout max.", dmp.Diff_Timeout * 1000 * 2 > endTime - startTime);
    dmp.Diff_Timeout = 0;

    // Test the linemode speedup.
    // Must be long to pass the 100 char cutoff.
    a = "1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n";
    b = "abcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\n";
    Assert.assertEquals("diff_main: Simple line-mode.", dmp.diff_main(a, b, true), dmp.diff_main(a, b, false));

    a = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
    b = "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghijabcdefghij";
    Assert.assertEquals("diff_main: Single line-mode.", dmp.diff_main(a, b, true), dmp.diff_main(a, b, false));

    a = "1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n";
    b = "abcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n";
    String[] texts_linemode = diff_rebuildtexts(dmp.diff_main(a, b, true));
    String[] texts_textmode = diff_rebuildtexts(dmp.diff_main(a, b, false));
    assertArrayEquals("diff_main: Overlap line-mode.", texts_textmode, texts_linemode);

    // Test null inputs.
    try {
      dmp.diff_main(null, null);
      Assert.fail("diff_main: Null inputs.");
    } catch (IllegalArgumentException ex) {
      // Error expected.
    }
  }


  //  MATCH TEST FUNCTIONS

  @Test
  public void testMatchMain() {
    // Full match.
    Assert.assertEquals("match_main: Equality.", 0, dmp.match_main("abcdef", "abcdef", 1000));

    Assert.assertEquals("match_main: Null text.", -1, dmp.match_main("", "abcdef", 1));

    Assert.assertEquals("match_main: Null pattern.", 3, dmp.match_main("abcdef", "", 3));

    Assert.assertEquals("match_main: Exact match.", 3, dmp.match_main("abcdef", "de", 3));

    Assert.assertEquals("match_main: Beyond end match.", 3, dmp.match_main("abcdef", "defy", 4));

    Assert.assertEquals("match_main: Oversized pattern.", 0, dmp.match_main("abcdef", "abcdefy", 0));

    dmp.Match_Threshold = 0.7f;
    Assert.assertEquals("match_main: Complex match.", 4, dmp.match_main("I am the very model of a modern major general.", " that berry ", 5));
    dmp.Match_Threshold = 0.5f;

    // Test null inputs.
    try {
      dmp.match_main(null, null, 0);
      Assert.fail("match_main: Null inputs.");
    } catch (IllegalArgumentException ex) {
      // Error expected.
    }
  }


  //  PATCH TEST FUNCTIONS

  @Test
  public void testPatchObj() {
    // Patch Object.
    Patch p = new Patch();
    p.start1 = 20;
    p.start2 = 21;
    p.length1 = 18;
    p.length2 = 17;
    p.diffs = diffList(new Diff(EQUAL, "jump"), new Diff(DELETE, "s"), new Diff(INSERT, "ed"), new Diff(EQUAL, " over "), new Diff(DELETE, "the"), new Diff(INSERT, "a"), new Diff(EQUAL, "\nlaz"));
    String strp = "@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n %0Alaz\n";
    Assert.assertEquals("Patch: toString.", strp, p.toString());
  }

  @Test
  public void testPatchFromText() {
    Assert.assertTrue("patch_fromText: #0.", dmp.patch_fromText("").isEmpty());

    String strp = "@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n %0Alaz\n";
    Assert.assertEquals("patch_fromText: #1.", strp, dmp.patch_fromText(strp).get(0).toString());

    Assert.assertEquals("patch_fromText: #2.", "@@ -1 +1 @@\n-a\n+b\n", dmp.patch_fromText("@@ -1 +1 @@\n-a\n+b\n").get(0).toString());

    Assert.assertEquals("patch_fromText: #3.", "@@ -1,3 +0,0 @@\n-abc\n", dmp.patch_fromText("@@ -1,3 +0,0 @@\n-abc\n").get(0).toString());

    Assert.assertEquals("patch_fromText: #4.", "@@ -0,0 +1,3 @@\n+abc\n", dmp.patch_fromText("@@ -0,0 +1,3 @@\n+abc\n").get(0).toString());

    // Generates error.
    try {
      dmp.patch_fromText("Bad\nPatch\n");
      Assert.fail("patch_fromText: #5.");
    } catch (IllegalArgumentException ex) {
      // Exception expected.
    }
  }

  public void testPatchToText() {
    String strp = "@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n  laz\n";
    List<Patch> patches;
    patches = dmp.patch_fromText(strp);
    Assert.assertEquals("patch_toText: Single.", strp, dmp.patch_toText(patches));

    strp = "@@ -1,9 +1,9 @@\n-f\n+F\n oo+fooba\n@@ -7,9 +7,9 @@\n obar\n-,\n+.\n  tes\n";
    patches = dmp.patch_fromText(strp);
    Assert.assertEquals("patch_toText: Dual.", strp, dmp.patch_toText(patches));
  }

  @Test
  @SuppressWarnings("deprecation")
  public void testPatchMake() {
    LinkedList<Patch> patches;
    patches = dmp.patch_make("", "");
    Assert.assertEquals("patch_make: Null case.", "", dmp.patch_toText(patches));

    String text1 = "The quick brown fox jumps over the lazy dog.";
    String text2 = "That quick brown fox jumped over a lazy dog.";
    String expectedPatch = "@@ -1,8 +1,7 @@\n Th\n-at\n+e\n  qui\n@@ -21,17 +21,18 @@\n jump\n-ed\n+s\n  over \n-a\n+the\n  laz\n";
    // The second patch must be "-21,17 +21,18", not "-22,17 +21,18" due to rolling context.
    patches = dmp.patch_make(text2, text1);
    Assert.assertEquals("patch_make: Text2+Text1 inputs.", expectedPatch, dmp.patch_toText(patches));

    expectedPatch = "@@ -1,11 +1,12 @@\n Th\n-e\n+at\n  quick b\n@@ -22,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n  laz\n";
    patches = dmp.patch_make(text1, text2);
    Assert.assertEquals("patch_make: Text1+Text2 inputs.", expectedPatch, dmp.patch_toText(patches));

    LinkedList<Diff> diffs = dmp.diff_main(text1, text2, false);
    patches = dmp.patch_make(diffs);
    Assert.assertEquals("patch_make: Diff input.", expectedPatch, dmp.patch_toText(patches));

    patches = dmp.patch_make(text1, diffs);
    Assert.assertEquals("patch_make: Text1+Diff inputs.", expectedPatch, dmp.patch_toText(patches));

    patches = dmp.patch_make(text1, text2, diffs);
    Assert.assertEquals("patch_make: Text1+Text2+Diff inputs (deprecated).", expectedPatch, dmp.patch_toText(patches));

    patches = dmp.patch_make("`1234567890-=[]\\;',./", "~!@#$%^&*()_+{}|:\"<>?");
    Assert.assertEquals("patch_toText: Character encoding.", "@@ -1,21 +1,21 @@\n-%601234567890-=%5B%5D%5C;',./\n+~!@#$%25%5E&*()_+%7B%7D%7C:%22%3C%3E?\n", dmp.patch_toText(patches));

    diffs = diffList(new Diff(DELETE, "`1234567890-=[]\\;',./"), new Diff(INSERT, "~!@#$%^&*()_+{}|:\"<>?"));
    Assert.assertEquals("patch_fromText: Character decoding.", diffs, dmp.patch_fromText("@@ -1,21 +1,21 @@\n-%601234567890-=%5B%5D%5C;',./\n+~!@#$%25%5E&*()_+%7B%7D%7C:%22%3C%3E?\n").get(0).diffs);

    text1 = "";
    for (int x = 0; x < 100; x++) {
      text1 += "abcdef";
    }
    text2 = text1 + "123";
    expectedPatch = "@@ -573,28 +573,31 @@\n cdefabcdefabcdefabcdefabcdef\n+123\n";
    patches = dmp.patch_make(text1, text2);
    Assert.assertEquals("patch_make: Long string with repeats.", expectedPatch, dmp.patch_toText(patches));

    // Test null inputs.
    try {
      dmp.patch_make(null);
      Assert.fail("patch_make: Null inputs.");
    } catch (IllegalArgumentException ex) {
      // Error expected.
    }
  }

  @Test
  public void testPatchSplitMax() {
    // Assumes that Match_MaxBits is 32.
    LinkedList<Patch> patches;
    patches = dmp.patch_make("abcdefghijklmnopqrstuvwxyz01234567890", "XabXcdXefXghXijXklXmnXopXqrXstXuvXwxXyzX01X23X45X67X89X0");
    dmp.patch_splitMax(patches);
    Assert.assertEquals("patch_splitMax: #1.", "@@ -1,32 +1,46 @@\n+X\n ab\n+X\n cd\n+X\n ef\n+X\n gh\n+X\n ij\n+X\n kl\n+X\n mn\n+X\n op\n+X\n qr\n+X\n st\n+X\n uv\n+X\n wx\n+X\n yz\n+X\n 012345\n@@ -25,13 +39,18 @@\n zX01\n+X\n 23\n+X\n 45\n+X\n 67\n+X\n 89\n+X\n 0\n", dmp.patch_toText(patches));

    patches = dmp.patch_make("abcdef1234567890123456789012345678901234567890123456789012345678901234567890uvwxyz", "abcdefuvwxyz");
    String oldToText = dmp.patch_toText(patches);
    dmp.patch_splitMax(patches);
    Assert.assertEquals("patch_splitMax: #2.", oldToText, dmp.patch_toText(patches));

    patches = dmp.patch_make("1234567890123456789012345678901234567890123456789012345678901234567890", "abc");
    dmp.patch_splitMax(patches);
    Assert.assertEquals("patch_splitMax: #3.", "@@ -1,32 +1,4 @@\n-1234567890123456789012345678\n 9012\n@@ -29,32 +1,4 @@\n-9012345678901234567890123456\n 7890\n@@ -57,14 +1,3 @@\n-78901234567890\n+abc\n", dmp.patch_toText(patches));

    patches = dmp.patch_make("abcdefghij , h : 0 , t : 1 abcdefghij , h : 0 , t : 1 abcdefghij , h : 0 , t : 1", "abcdefghij , h : 1 , t : 1 abcdefghij , h : 1 , t : 1 abcdefghij , h : 0 , t : 1");
    dmp.patch_splitMax(patches);
    Assert.assertEquals("patch_splitMax: #4.", "@@ -2,32 +2,32 @@\n bcdefghij , h : \n-0\n+1\n  , t : 1 abcdef\n@@ -29,32 +29,32 @@\n bcdefghij , h : \n-0\n+1\n  , t : 1 abcdef\n", dmp.patch_toText(patches));
  }

  @Test
  public void testPatchAddPadding() {
    LinkedList<Patch> patches;
    patches = dmp.patch_make("", "test");
    Assert.assertEquals("patch_addPadding: Both edges full.", "@@ -0,0 +1,4 @@\n+test\n", dmp.patch_toText(patches));
    dmp.patch_addPadding(patches);
    Assert.assertEquals("patch_addPadding: Both edges full.", "@@ -1,8 +1,12 @@\n %01%02%03%04\n+test\n %01%02%03%04\n", dmp.patch_toText(patches));

    patches = dmp.patch_make("XY", "XtestY");
    Assert.assertEquals("patch_addPadding: Both edges partial.", "@@ -1,2 +1,6 @@\n X\n+test\n Y\n", dmp.patch_toText(patches));
    dmp.patch_addPadding(patches);
    Assert.assertEquals("patch_addPadding: Both edges partial.", "@@ -2,8 +2,12 @@\n %02%03%04X\n+test\n Y%01%02%03\n", dmp.patch_toText(patches));

    patches = dmp.patch_make("XXXXYYYY", "XXXXtestYYYY");
    Assert.assertEquals("patch_addPadding: Both edges none.", "@@ -1,8 +1,12 @@\n XXXX\n+test\n YYYY\n", dmp.patch_toText(patches));
    dmp.patch_addPadding(patches);
    Assert.assertEquals("patch_addPadding: Both edges none.", "@@ -5,8 +5,12 @@\n XXXX\n+test\n YYYY\n", dmp.patch_toText(patches));
  }

  @Test
  public void testPatchApply() {
    dmp.Match_Distance = 1000;
    dmp.Match_Threshold = 0.5f;
    dmp.Patch_DeleteThreshold = 0.5f;
    LinkedList<Patch> patches;
    patches = dmp.patch_make("", "");
    Object[] results = dmp.patch_apply(patches, "Hello world.");
    boolean[] boolArray = (boolean[]) results[1];
    String resultStr = results[0] + "\t" + boolArray.length;
    Assert.assertEquals("patch_apply: Null case.", "Hello world.\t0", resultStr);

    patches = dmp.patch_make("The quick brown fox jumps over the lazy dog.", "That quick brown fox jumped over a lazy dog.");
    results = dmp.patch_apply(patches, "The quick brown fox jumps over the lazy dog.");
    boolArray = (boolean[]) results[1];
    resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1];
    Assert.assertEquals("patch_apply: Exact match.", "That quick brown fox jumped over a lazy dog.\ttrue\ttrue", resultStr);

    results = dmp.patch_apply(patches, "The quick red rabbit jumps over the tired tiger.");
    boolArray = (boolean[]) results[1];
    resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1];
    Assert.assertEquals("patch_apply: Partial match.", "That quick red rabbit jumped over a tired tiger.\ttrue\ttrue", resultStr);

    results = dmp.patch_apply(patches, "I am the very model of a modern major general.");
    boolArray = (boolean[]) results[1];
    resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1];
    Assert.assertEquals("patch_apply: Failed match.", "I am the very model of a modern major general.\tfalse\tfalse", resultStr);

    patches = dmp.patch_make("x1234567890123456789012345678901234567890123456789012345678901234567890y", "xabcy");
    results = dmp.patch_apply(patches, "x123456789012345678901234567890-----++++++++++-----123456789012345678901234567890y");
    boolArray = (boolean[]) results[1];
    resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1];
    Assert.assertEquals("patch_apply: Big delete, small change.", "xabcy\ttrue\ttrue", resultStr);

    patches = dmp.patch_make("x1234567890123456789012345678901234567890123456789012345678901234567890y", "xabcy");
    results = dmp.patch_apply(patches, "x12345678901234567890---------------++++++++++---------------12345678901234567890y");
    boolArray = (boolean[]) results[1];
    resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1];
    Assert.assertEquals("patch_apply: Big delete, big change 1.", "xabc12345678901234567890---------------++++++++++---------------12345678901234567890y\tfalse\ttrue", resultStr);

    dmp.Patch_DeleteThreshold = 0.6f;
    patches = dmp.patch_make("x1234567890123456789012345678901234567890123456789012345678901234567890y", "xabcy");
    results = dmp.patch_apply(patches, "x12345678901234567890---------------++++++++++---------------12345678901234567890y");
    boolArray = (boolean[]) results[1];
    resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1];
    Assert.assertEquals("patch_apply: Big delete, big change 2.", "xabcy\ttrue\ttrue", resultStr);
    dmp.Patch_DeleteThreshold = 0.5f;

    // Compensate for failed patch.
    dmp.Match_Threshold = 0.0f;
    dmp.Match_Distance = 0;
    patches = dmp.patch_make("abcdefghijklmnopqrstuvwxyz--------------------1234567890", "abcXXXXXXXXXXdefghijklmnopqrstuvwxyz--------------------1234567YYYYYYYYYY890");
    results = dmp.patch_apply(patches, "ABCDEFGHIJKLMNOPQRSTUVWXYZ--------------------1234567890");
    boolArray = (boolean[]) results[1];
    resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1];
    Assert.assertEquals("patch_apply: Compensate for failed patch.", "ABCDEFGHIJKLMNOPQRSTUVWXYZ--------------------1234567YYYYYYYYYY890\tfalse\ttrue", resultStr);
    dmp.Match_Threshold = 0.5f;
    dmp.Match_Distance = 1000;

    patches = dmp.patch_make("", "test");
    String patchStr = dmp.patch_toText(patches);
    dmp.patch_apply(patches, "");
    Assert.assertEquals("patch_apply: No side effects.", patchStr, dmp.patch_toText(patches));

    patches = dmp.patch_make("The quick brown fox jumps over the lazy dog.", "Woof");
    patchStr = dmp.patch_toText(patches);
    dmp.patch_apply(patches, "The quick brown fox jumps over the lazy dog.");
    Assert.assertEquals("patch_apply: No side effects with major delete.", patchStr, dmp.patch_toText(patches));

    patches = dmp.patch_make("", "test");
    results = dmp.patch_apply(patches, "");
    boolArray = (boolean[]) results[1];
    resultStr = results[0] + "\t" + boolArray[0];
    Assert.assertEquals("patch_apply: Edge exact match.", "test\ttrue", resultStr);

    patches = dmp.patch_make("XY", "XtestY");
    results = dmp.patch_apply(patches, "XY");
    boolArray = (boolean[]) results[1];
    resultStr = results[0] + "\t" + boolArray[0];
    Assert.assertEquals("patch_apply: Near edge exact match.", "XtestY\ttrue", resultStr);

    patches = dmp.patch_make("y", "y123");
    results = dmp.patch_apply(patches, "x");
    boolArray = (boolean[]) results[1];
    resultStr = results[0] + "\t" + boolArray[0];
    Assert.assertEquals("patch_apply: Edge partial match.", "x123\ttrue", resultStr);
  }

  private void assertArrayEquals(String error_msg, Object[] a, Object[] b) {
    List<Object> list_a = Arrays.asList(a);
    List<Object> list_b = Arrays.asList(b);
    Assert.assertEquals(error_msg, list_a, list_b);
  }

  // Construct the two texts which made up the diff originally.
  private static String[] diff_rebuildtexts(LinkedList<Diff> diffs) {
    String[] text = {"", ""};
    for (Diff myDiff : diffs) {
      if (myDiff.operation != diff_match_patch.Operation.INSERT) {
        text[0] += myDiff.text;
      }
      if (myDiff.operation != diff_match_patch.Operation.DELETE) {
        text[1] += myDiff.text;
      }
    }
    return text;
  }

  // Private function for quickly building lists of diffs.
  private static LinkedList<Diff> diffList(Diff... diffs) {
    LinkedList<Diff> myDiffList = new LinkedList<Diff>();
    for (Diff myDiff : diffs) {
      myDiffList.add(myDiff);
    }
    return myDiffList;
  }
}
