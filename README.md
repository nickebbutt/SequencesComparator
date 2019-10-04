## SequencesComparator

This code is a small enhancement on top of Apache Commons Collections 4 SequencesComparator, and is released under the same license terms.
Ideally it should be incorporated into that project in a future version.

SequenceComparator allows two sequences (Lists) to be differenced, producing an EditScript which describes the insert/delete operations required to transform
one sequence into the other

The modifications made in this version allow the user to set a maximum to the number of differences to be returned in the output EditScript

If during the calculation the maximum is exceeded (because there are too many differences between the two sequences) then the calculation of the EditScript will be 
terminated prematurely and a MaxDifferenceException will be thrown

This may be useful in some cases - although the Myers diff algorithm is efficient in a lot of practical applications, in the worst case (where two sequences are completely different)
the complexity of the algorithm will still result in an expensive / impractical computation

The ability to set an upper bound on the difference detection allows an approach which searches for differences but aborts early if the calculation
proves to be too complex / expensive.

I need this to test differences between two long sequences which will usually be very similar, but occasionally entirely different.
In the case they are very dissimilar, I need to abort the diff without spending too much time






