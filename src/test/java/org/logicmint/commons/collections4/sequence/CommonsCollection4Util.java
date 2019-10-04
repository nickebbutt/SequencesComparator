package org.logicmint.commons.collections4.sequence;

import org.apache.commons.collections4.sequence.SequencesComparator;

import java.util.List;

public class CommonsCollection4Util {
    
    static <E> int getDifferenceCountUsingCommons(List<E> l1, List<E> l2) {
        SequencesComparator<E> sequencesComparator = new SequencesComparator(l1, l2);
        return sequencesComparator.getScript().getModifications();
    }
}
