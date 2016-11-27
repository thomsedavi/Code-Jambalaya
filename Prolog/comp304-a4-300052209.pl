% COMP 304 Assignment 4
% Basic Prolog
% David Thomsen

% My 'printSentence' predicate goes through each word in a list and prints it
% out, replacing 'fs' with a full stop and 'qm' with a question mark. Because
% I needed to insert a space between each word, 'printSentence' simply prints
% the first word and then passed the remainder to 'printSentence2', which
% inserts a space before each word.

printSentence([X|Rest]) :- write(X), printSentence2(Rest).

printSentence2([fs]) :- write(".").
printSentence2([qm]) :- write("?").
printSentence2([X|Rest]) :- write(" "), write(X), printSentence2(Rest).

% my 'printReply' predicate takes an input and then modifies it to turn a
% statement into a question, then transforms it to change every 'i' into a
% 'you', and so on, and then prints out the result.

printReply(X) :- modify(X,Y), transform(Y,Z), printSentence(Z).

% My 'transform' predicate will go through each word in the sentence to match
% it against a set of words that need to be transformed. First it checks if
% the sentence is empty, in which case it returns a question mark. Then it
% exchanges 'i' for 'you' and so on, and passes the remainder of the list
% forward to be transformed. If the word doesn't match any in the set, it
% simply leaves the word as it is and passes the remainder of the list on.

% The first solution will be the correct one, however if further solutions are
% looked for then it will fall through and leave words unchanged. This created
% a problem with testing, as different solutions make the tests pass than the
% one that is intended, so I have done manual testing as well.

transform([],[qm]).

transform([i|XRest],[you|YRest]) :- transform(XRest,YRest).
transform([me|XRest],[you|YRest]) :- transform(XRest,YRest).
transform([am|XRest],[are|YRest]) :- transform(XRest,YRest).
transform([my|XRest],[your|YRest]) :- transform(XRest,YRest).
transform([you|XRest],[me|YRest]) :- transform(XRest,YRest).
transform([your|XRest],[my|YRest]) :- transform(XRest,YRest).

transform([X|XRest],[X|YRest]) :- transform(XRest,YRest).

% These are the sentence modifications that cover the examples given in the
% assignment handout. They look at the first two words in the sentence and then
% replace them with a sentence beginning that modifies the input into a
% question. The last one contains an additional step where an extra word is
% added to the end of the sentence, using the predefined 'append' predicate.

modify([i,know|B],[are,i,sure,i,know|B]).
modify([i,feel|B],[what,makes,i,feel|B]).
modify([i,like|B],[why,do,i,like|B]).
modify([i,fantasise|B],C) :- append([have,i,ever,fantasised|B],[before],C).

% these are my additional responses. I have one normal response where I only
% change the beginning, an extended response where something is appended to the
% end of the sentence, and a couple of catch all predicates that cover inputs
% that have not otherwise been covered.

modify([i,worry|B],[when,did,i,start,worrying|B]).
modify([i,think|B],C) :- append([do,i,think|B],[all,the,time],C).
modify([i,am|B],[why,are,i|B]).
modify(Y,[why,do|Y]).

% my 'answer' predicate goes through the same steps as the 'printReply'
% predicate, first modifying the input and then transforming it. The
% transformed list will then be compared to the desired list using the
% 'compare' predicate.

answer(X,A) :- modify(X,Y), transform(Y,Z), compare(A,Z).

% The 'compare' predicate matches lists only if they are empty, or if the first
% items in the list match, at which point it will compare the remainder of the
% list. If the first items do not match, then a matching predicate will not be
% found and the code will fail.

compare([],[]).
compare([X|Alist],[X|Blist]) :- compare(Alist,Blist).

% This is an example of how to use the 'printReply' predicate:

:- printReply([i,think,the,illuminati,is,watching,me,through,my,webcam]).

% TESTS
%
% I have written two tests for every kind of input that Dr. Roberts is able to
% respond to. I have mixed in usage of words like 'my' and 'your' and 'me' to
% demonstrate the natural language my code is capable of producing.
% Unfortunately the code would produce illegible sentences if given the wrong
% input, for example 'i think about my mother all the time' would produce the
% reply 'do you think about your mother all the time all the time?' which makes
% little sense. I have excluded examples of this inflexibility in my testing.

% because the 'answer' predicate uses all of my predicates except the 'print'
% ones, I have not tested other individual predicates as I believe the code
% coverage is complete. I used manual testing to test the 'print' predicates.

:- answer([i,feel,bad,about,my,brother],
  [what,makes,you,feel,bad,about,your,brother,qm]).
:- answer([i,feel,concerned,about,donald,trump],
  [what,makes,you,feel,concerned,about,donald,trump,qm]).

:- answer([i,fantasise,about,fast,cars],
  [have,you,ever,fantasised,about,fast,cars,before,qm]).
: - answer([i,fantasise,about,cyndi,lauper],
  [have,you,ever,fantasised,about,cyndi,lauper,before,qm]).

:- answer([i,like,my,mother],
  [why,do,you,like,your,mother,qm]).
:- answer([i,like,to,play,with,my,mothers,hair],
  [why,do,you,like,to,play,with,your,mothers,hair,qm]).

:- answer([i,know,i,am,insecure],
  [are,you,sure,you,know,you,are,insecure,qm]).
:- answer([i,know,all,about,game,of,thrones],
  [are,you,sure,you,know,all,about,game,of,thrones,qm]).

:- answer([i,worry,about,global,warming],
  [when,did,you,start,worrying,about,global,warming,qm]).
:- answer([i,worry,that,i,am,going,bald],
  [when,did,you,start,worrying,that,you,are,going,bald,qm]).

:- answer([i,am,happy,to,see,you],
  [why,are,you,happy,to,see,me,qm]).
:- answer([i,am,curious,about,your,programming],
  [why,are,you,curious,about,my,programming,qm]).

:- answer([i,think,about,my,mother],
  [do,you,think,about,your,mother,all,the,time,qm]).
:- answer([i,think,about,gretchen,ross],
  [do,you,think,about,gretchen,ross,all,the,time,qm]).

:- answer([i,hate,everything,about,me],
  [why,do,you,hate,everything,about,you,qm]).
:- answer([i,train,pokemon,to,fight,each,other],
  [why,do,you,train,pokemon,to,fight,each,other,qm]).

% can my 'answer' predicate produce an input list, provided that it is given
% the answer list? No, because it ends up in a perpetual loop of appending 'i'
% to the input sentence and 'you' to the output sentence.
% It would be possible to fix this by doing a breadth first search rather than
% a depth first search. This might not find the correct solution straight away
% but at least it would return some solutions, rather than none at all.

