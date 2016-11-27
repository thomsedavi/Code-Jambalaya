# David Thomsen, 300052209

import time
import string
import random

repeat = raw_input("Repeat how many times: ")
try:
    int(repeat)
except ValueError:
    print "'" + repeat + "' is not an integer :("
    exit()
input = raw_input("Generate sequences (g) or enter sequences (e)?: ")

if input == 'g':
    length1 = raw_input("Enter length of first string: ")
    try:
        int(length1)
    except ValueError:
        print "'" + length1 + "' is not an integer :("
        exit()
    length2 = raw_input("Enter length of second string: ")
    try:
        int(length2)
    except ValueError:
        print "'" + length2 + "' is not an integer :("
        exit()
elif input == 'e':
    sequence1 = raw_input("Enter 1st sequence: ")
    sequence2 = raw_input("Enter 2nd sequence: ")
else:
    sequence1 = "THAT WAS NOT AN OPTION"
    sequence2 = "XYZTHATXWASYNOTZANXOPTIONYZX"

class SortedCostList:

    def __init__(self):
        self.list = []

    def push(self, alignCost):
        matrixLocation = matrix[len(alignCost.remainSeq1)][len(alignCost.remainSeq2)]
        if matrixLocation == False:
            matrix[len(alignCost.remainSeq1)][len(alignCost.remainSeq2)] = alignCost
            self.list.append(alignCost)
            self.list = sorted(self.list, key=lambda alignCost: alignCost.cost)
        elif alignCost.cost < matrixLocation.cost:
            matrix[len(alignCost.remainSeq1)][len(alignCost.remainSeq2)] = alignCost
            self.list.append(alignCost)
            self.list = sorted(self.list, key=lambda alignCost: alignCost.cost)

            # If we get here we should know the matrixLocation is in the list,
            # but for some reason can't always find it!
            if matrixLocation in self.list:
                self.list.remove(matrixLocation)
            
    #actually list already probably has this, but I wrote it, so here it is
    def pop(self):
        result = self.list[0]
        self.list = self.list[1:]
        return result

class AlignCosts:
    def __init__(self, finalSeq1, finalSeq2, remainSeq1, remainSeq2, cost):
        self.finalSeq1 = finalSeq1
        self.finalSeq2 = finalSeq2
        self.remainSeq1 = remainSeq1
        self.remainSeq2 = remainSeq2
        self.cost = cost

    def noGap(seq):
        # Add or subract from cost depending on whether first characters match
        if seq.remainSeq1[:1] == seq.remainSeq2[:1]:
            return AlignCosts(seq.finalSeq1 + seq.remainSeq1[:1], seq.finalSeq2 + seq.remainSeq2[:1], seq.remainSeq1[1:], seq.remainSeq2[1:], seq.cost - 1)
        else:
            return AlignCosts(seq.finalSeq1 + seq.remainSeq1[:1], seq.finalSeq2 + seq.remainSeq2[:1], seq.remainSeq1[1:], seq.remainSeq2[1:], seq.cost + 1)

    def gap1st(seq):
        return AlignCosts(seq.finalSeq1 + " ", seq.finalSeq2 + seq.remainSeq2[:1], seq.remainSeq1, seq.remainSeq2[1:], seq.cost + 2)

    def gap2nd(seq):
        return AlignCosts(seq.finalSeq1 + seq.remainSeq1[:1], seq.finalSeq2 + " ", seq.remainSeq1[1:], seq.remainSeq2, seq.cost + 2)

    # For printing out the minimum cost alignment in a neat fashion
    def evaluate(finish, seq1, seq2, tally, cost):
        if len(seq1) == 0:
            print finish.finalSeq1 + "\n" + finish.finalSeq2 + "\n" + tally + " (" + str(cost) + ")"
        elif seq1[:1] == " " or seq2[:1] == " ":
            AlignCosts.evaluate(finish, seq1[1:], seq2[1:], tally + "*", cost - 2)
        elif seq1[:1] == seq2[:1]:
            AlignCosts.evaluate(finish, seq1[1:], seq2[1:], tally + "+", cost + 1)
        else:
            AlignCosts.evaluate(finish, seq1[1:], seq2[1:], tally + "-", cost - 1)

totals = []

for x in range(int(repeat)):
    if input == 'g':
        sequence1 = ''.join(random.choice("ACGT") for _ in range(int(length1)))
        sequence2 = ''.join(random.choice("ACGT") for _ in range(int(length2)))

    start_time = time.time()
    list = SortedCostList()
    alignment = AlignCosts("", "", sequence1, sequence2, 0)
    unfound = True
    matrix = [[False for x in range(len(sequence2) + 1)] for y in range(len(sequence1) + 1)] 

    list.push(alignment)

    while(unfound):
        if len(alignment.remainSeq1) > 0 and len(alignment.remainSeq2) > 0:
            list.push(AlignCosts.noGap(alignment))
        if len(alignment.remainSeq2) > 0:
            list.push(AlignCosts.gap1st(alignment))
        if len(alignment.remainSeq1) > 0:
            list.push(AlignCosts.gap2nd(alignment))
    
        alignment = list.pop()
        if len(alignment.remainSeq1) == 0 and len(alignment.remainSeq2) == 0:
            unfound = False

    finish_time = time.time()
    totals.append(finish_time - start_time)

    AlignCosts.evaluate(alignment, alignment.finalSeq1, alignment.finalSeq2, "", 0)

totals.sort()

print "Median time taken: " + str(totals[(int(repeat)/2)]) + " seconds"
