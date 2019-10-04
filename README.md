## SequencesComparator

This project is a small enhancement on top of Apache Commons Collections 4 SequencesComparator
It is released under the same license terms and ideally it should be incorporated into the Apache project in a future version.

SequenceComparator allows two sequences (Lists) to be differenced, producing an EditScript which describes the insert/delete operations required to transform
one sequence into the other

The algorithm used is the [Myers diff algorithm](http://www.xmailserver.org/diff2.pdf) which is efficient in most common cases.
The modifications made in this version allow the user to set a maximum to the number of differences to be returned in the output EditScript

If during the calculation the maximum is exceeded (because there are too many differences between the two sequences) then the calculation of the EditScript will be 
terminated prematurely and a MaxDifferenceException will be thrown

This may be useful in some cases - although the Myers Diff algorithm is efficient in a lot of practical applications, in the worst case (where two sequences are completely different)
the complexity of the algorithm will still result in an expensive / impractical computation

The ability to set an upper bound on the difference detection allows an approach which searches for differences but aborts early if the calculation
proves to be too complex / expensive.

I needed this feature to test for differences between two long sequences which will usually be very similar, but occasionally entirely different.
In the case they are very dissimilar, I need to abort the diff without spending too much time processing.



A sample application is:

    <T> EditScript<T> getEditScript(List<T> l1, List<T> l2) throws MaxDifferencesExceeded {

        SequencesComparator<T> s = new SequencesComparator<>(l1, l2);

        //this original unbounded search may take a long time if there are many differences:
        //EditScript script = s.getScript();
        
        //this new version limits the differences and throws MaxDifferencesException if they are exceeded
        int maxDifferences = 20; 
        return s.getScript(maxDifferences);
    }



