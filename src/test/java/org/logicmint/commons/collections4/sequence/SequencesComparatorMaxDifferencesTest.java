/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.logicmint.commons.collections4.sequence;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SequencesComparatorMaxDifferencesTest {

    @Test
    public void testRandomisedSequencesWithNoMaxDifference() {
        //Testing that this implementation returns identical results to commons4 where there is no max difference set
        for (int i = 0; i < 10000; i++) {
            String s1 = UUID.randomUUID().toString();
            String s2 = UUID.randomUUID().toString();

            List<Character> l1 = toCharacterList(s1);
            List<Character> l2 = toCharacterList(s2);

            SequencesComparator<Character> s = new SequencesComparator<>(l1, l2);
            EditScript<Character> script = s.getScript();
            
            int diffs = script.getModifications();
            assertEquals(diffs, CommonsCollection4Util.getDifferenceCountUsingCommons(l1, l2));

            testTransformation(l1, l2, script);
        }
    }

    <T> EditScript<T> getEditScript(List<T> l1, List<T> l2) throws MaxDifferencesExceeded {

        SequencesComparator<T> s = new SequencesComparator<>(l1, l2);

        //this original unbounded search may take a long time if there are many differences:
        //EditScript script = s.getScript();
        
        //this new version limits the differences and throws MaxDifferencesException if they are exceeded
        int maxDifferences = 20; 
        return s.getScript(maxDifferences);
    }
    
    @Test
    public void testWhenThereAreMoreThanMaxDifferencesProcessingIsAborted() {
        int exceedCount = 0;
        for (int i = 0; i < 10000; i++) {
            String s1 = UUID.randomUUID().toString();
            String s2 = UUID.randomUUID().toString();

            List<Character> l1 = toCharacterList(s1);
            List<Character> l2 = toCharacterList(s2);

            SequencesComparator<Character> s = new SequencesComparator<>(l1, l2);
            int max = (int)(Math.random() * s1.length());

            try {
                int diffs = s.getScript(max).getModifications();
                assertEquals(diffs, CommonsCollection4Util.getDifferenceCountUsingCommons(l1, l2));
            } catch (MaxDifferencesExceeded maxDifferencesExceeded) {
                exceedCount++;
                assertTrue(CommonsCollection4Util.getDifferenceCountUsingCommons(l1, l2) > max);
            }
        }
        System.out.println("Exceeded " + exceedCount + " times");
    }
    
    
    @Test
    public void testComparingVeryLongSequencesIsQuickProvidedAMaxDifferenceLimitIsSet() {
        //this tests the worst case where the long strings are random and so contain a huge number of differences
        //without the max limit this will take a LONG time
        StringBuilder sb1 = populateLongString();
        StringBuilder sb2 = populateLongString();
        
        assertEquals(360000, sb1.length());
        
        SequencesComparator<Character> s = new SequencesComparator<>(toCharacterList(sb1.toString()), toCharacterList(sb2.toString()));
        long start = System.currentTimeMillis();
        try {
            s.getScript(20);
            fail("should not succeed!");
        } catch (MaxDifferencesExceeded maxDifferencesExceeded) {
            long timeTaken = System.currentTimeMillis() - start;
            System.out.println("Time taken " + timeTaken);
            assertTrue(timeTaken < 10); //shoudl be a lot less!
        }
    }

    private StringBuilder populateLongString() {
        StringBuilder sb1 = new StringBuilder();
        IntStream.range(0,10000).forEach(i -> sb1.append(UUID.randomUUID().toString()));
        return sb1;
    }

    private void testTransformation(List<Character> l1, List<Character> l2, EditScript<Character> script) {
        List<Character> l3 = new LinkedList<>(l1);
        CommandVisitor<Character> v = new CommandVisitor<>() {
            int index = 0;

            @Override
            public void visitInsertCommand(Character object) {
                l3.add(index++, object);
            }

            @Override
            public void visitKeepCommand(Character object) {
                index++;
            }

            @Override
            public void visitDeleteCommand(Character object) {
                l3.remove(index);
            }
        };
        script.visit(v);
        assertEquals(l3, l2);
    }

    @Test
    public void testMaxDifference() throws MaxDifferencesExceeded {
        String a = "abcdefghijklm";
        String b = "bcefgijklmn";
        
        List<Character> al = toCharacterList(a);
        List<Character> bl = toCharacterList(b);
        
        SequencesComparator<Character> s = new SequencesComparator<>(al, bl);
        assertEquals(4, s.getScript().getModifications());
        assertEquals(4, s.getScript(4).getModifications());
    }
    
    
    @Test(expected = MaxDifferencesExceeded.class)
    public void testMaxDifferenceLimit() throws MaxDifferencesExceeded {
        String a = "abcdefghijklm";
        String b = "bcefgijklmn";

        List<Character> al = toCharacterList(a);
        List<Character> bl = toCharacterList(b);

        SequencesComparator<Character> s = new SequencesComparator<>(al, bl);
        s.getScript(3);
    }

    private List<Character> toCharacterList(String s) {
        return s.chars().mapToObj(e->(char)e).collect(Collectors.toList());
    }

}
