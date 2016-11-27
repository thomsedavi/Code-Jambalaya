% COMP304 Assignment 5
% More Prolog
% David Thomsen, 300052209

% ROAD/3
%
% I have chosen to store my road segments in predicates called 'data'. 

edge(wellington, palmerston_north, 143).
edge(palmerston_north, wanganui, 74).
edge(palmerston_north, napier, 178).
edge(palmerston_north, taupo, 259).
edge(wanganui, taupo, 231).
edge(wanganui, new_plymouth, 163).
edge(wanganui, napier, 252).
edge(napier, taupo, 147).
edge(napier, gisborne, 215).
edge(new_plymouth, hamilton, 242).
edge(new_plymouth, taupo, 289).
edge(taupo, hamilton, 153).
edge(taupo, rotorua, 82).
edge(taupo, gisborne, 334).
edge(gisborne, rotorua, 291).
edge(rotorua, hamilton, 109).
edge(hamilton, auckland, 126).

% The reason for this is that I want my 'road' predicate to be able to go
% either way, so it can create 'road' segments by parsing the data and
% swapping the origins and destinations around.

road(From, To, Km) :- edge(From, To, Km).
road(From, To, Km) :- edge(To, From, Km).

% ROUTE/3
%
% My 'route' predicate passes the parameters to the 'route1' predicate and
% initialised a fourth parameter as a list containing the origin.
% It also checks whether the origin city is a member of the list of
% destinations, if so delete it from the list before passing it forward.

route(Start,Finish,Visits) :-
  (member(Start,Visits) ->
    delete(Visits,Start,Visits2), route1(Start,Finish,Visits2,[Start]) ;
    route1(Start,Finish,Visits,[Start])).

% If the origin and destination match and the set of towns to go through is
% empty, then we have reached our destination and have gone through all the
% towns we had planned to.

route1(Start,Start,[],_).

% My 'route1' predicate first tries to find a roads that match the origin.
% When it finds one, it checks whether the destination of that road segment
% is in the list of towns visited. If it is, we fail and try again.
% If it is, we append the new town to the list of those visited, and then
% checks whether the new town is in the list of those that we want to visit.
% If it is, we check it off the list and pass the town as the origin of the
% next search along with the modified list. If is isn't, we pass the town as
% the origin with the previous list of visited towns.

route1(Start,Finish,Visits,Visited) :-
  road(Start,Next,_), (member(Next,Visited) ->
    fail ;
    append(Visited,[Next],Visited2), (member(Next,Visits) ->
      delete(Visits,Next,Visits2), route1(Next,Finish,Visits2,Visited2) ;
      route1(Next,Finish,Visits,Visited2))), !.

% ROUTE/4
%
% This 'route' predicate passes the parameters to a new 'route1' predicate with
% a list initialised with the origin and a length initialised to zero.


route(Start,Finish,Visits,Distance) :-
  (member(Start,Visits) ->
    delete(Visits,Start,Visits2),
      route1(Start,Finish,Visits2,Distance,[Start],0) ;
    route1(Start,Finish,Visits,Distance,[Start],0)).

% If the origin matches the destination and the list of places to go is empty,
% then we match the desired length parameter with the accumulated length
% parameter.

route1(Start,Start,[],Distance,_,Distance).

% This logic is very similar to that for my 'route1' for 'ROUTE/3', but with
% the additional step that the length of the matching road is added to the
% accumulated duration and passed forward.

route1(Start,Finish,Visits,Distance,Visited,TotalDistance) :-
  road(Start,Next,NextDistance), (member(Next,Visited) ->
    fail ;
    append(Visited,[Next],Visited2),
      plus(TotalDistance,NextDistance,TotalDistance2), (member(Next,Visits) ->
      delete(Visits,Next,Visits2),
        route1(Next,Finish,Visits2,Distance,Visited2,TotalDistance2) ;
      route1(Next,Finish,Visits,Distance,Visited2,TotalDistance2))), !.

% CHOICE/3
%
% My 'choice' predicate agains passes the parameters on to the 'choice1'
% predicate. This one does not require it to visit any cities, so does not
% delete the origin from any list.

choice(Start,Finish,RoutesAndDistances) :-
  choice1(Start,Finish,RoutesAndDistances,[Start]).

% If the origin and destination match, we have reached our destinatation, and
% we match the path we have taken to the 'path' parameter.

choice1(Start,Start,RoutesAndDistances,RoutesAndDistances).

% As with previous logic, we check whether the destination of the next road
% segment is part of our visited set, and don't go there if it is. Otherwise
% we append the length of that road and the destination to the list of places
% visited and pass that list forward.

choice1(Start,Finish,RoutesAndDistances,TotalRoutesAndDistances) :-
  road(Start,Next,NextDistance), (member(Next,TotalRoutesAndDistances) ->
    fail ;
    append(TotalRoutesAndDistances,[NextDistance],TotalRoutesAndDistances2),
      append(TotalRoutesAndDistances2,[Next],TotalRoutesAndDistances3),
      choice1(Next,Finish,RoutesAndDistances,TotalRoutesAndDistances3)).

% VIA/4
%
% 'via' is a combination of previous predicates. It uses the logic of 'route' to
% make sure all required cities get visited, and the logic of 'choice' to build
% list of city and road segments.

via(Start,Finish,Via,RoutesAndDistances) :-
  (member(Start,Via) ->
    delete(Via,Start,Via2), via1(Start,Finish,Via2,RoutesAndDistances,[Start]) ;
    via1(Start,Finish,Via,RoutesAndDistances,[Start])).

via1(Start,Start,[],RoutesAndDistances,RoutesAndDistances).

via1(Start,Finish,Via,RoutesAndDistances,TotalRoutesAndDistances) :-
  road(Start,Next,NextDistance), (member(Next,TotalRoutesAndDistances) ->
    fail ;
    append(TotalRoutesAndDistances,[NextDistance],TotalRoutesAndDistances2),
      append(TotalRoutesAndDistances2,[Next],TotalRoutesAndDistances3),
      (member(Next,Via) ->
        delete(Via,Next,Via2),
          via1(Next,Finish,Via2,RoutesAndDistances,TotalRoutesAndDistances3) ;
        via1(Next,Finish,Via,RoutesAndDistances,TotalRoutesAndDistances3))).

% AVOIDING/4
%
% 'avoiding' is very similar to 'via', but with the logic reversed. If the next
% destination of a segment has been visited before OR is part of the list of
% cities to avoid, then we avoid that city.

avoiding(Start,Finish,Avoiding,RoutesAndDistances) :-
  avoiding1(Start,Finish,Avoiding,RoutesAndDistances,[Start]).

avoiding1(Start,Start,_,RoutesAndDistances,RoutesAndDistances).

avoiding1(Start,Finish,Avoiding,RoutesAndDistances,TotalRoutesAndDistances) :-
  road(Start,Next,NextDistance), (member(Next,TotalRoutesAndDistances) ->
    fail ;
    (member(Next,Avoiding) ->
      fail ;
      append(TotalRoutesAndDistances,[NextDistance],TotalRoutesAndDistances2),
        append(TotalRoutesAndDistances2,[Next],TotalRoutesAndDistances3),
        avoiding1(Next,Finish,Avoiding,RoutesAndDistances,TotalRoutesAndDistances3))).

% TESTING
%
% My testing is not completely extensive, but a lot of the logic for my
% predicates is so similar that the test for logic in one predicate will prove
% the same logic for other predicates.
% I have often used Hamilton as a good data point because it is impossible to
% get to Auckland without going through this city.

% can't go through Wanganui on the way from Hamilton to Auckland
:- (route(hamilton,auckland,[wanganui],_) -> fail ; !).

% can go from hamilton to auckland if there are not other cities required
:- (route(hamilton,auckland,[],_) -> ! ; fail).

% there is a route of length 747 from wellington to auckland
:- (route(wellington,auckland,[],747) -> ! ; fail).

% this route must go through Wanganui
:- (route(wellington,auckland,[wanganui],727) -> ! ; fail).

% the route of length 747 does not go through Wanganui
:- (route(wellington,auckland,[wanganui],747) -> fail ; !).

% no route of length 1 from Wellington to Auckland
:- (route(wellington,auckland,[],1) -> fail ; !).

% going from Hamilton to Auckland will pass through both Hamilton and Auckland
:- (route(hamilton,auckland,[hamilton,auckland],126) -> ! ; fail).

% travelling from Rotorua to Auckland will use roads of this length
:- (choice(rotorua,auckland,[rotorua,109,hamilton,126,auckland]) -> ! ; fail).

% but not roads of this length
:- (choice(rotorua,auckland,[rotorua,126,hamilton,109,auckland]) -> fail ; !).

% travelling from Rotorua to Rotorua will not go anywhere
:- (choice(rotorua,rotorua,[rotorua]) -> ! ; fail).
:- (choice(rotorua,rotorua,[rotorua,109,hamilton,109,rotorua]) -> fail ; !). 

% this path from Rotorua to Auckland will go through Taupo
:- (via(rotorua,auckland,[taupo],[rotorua,82,taupo,153,hamilton,126,auckland])
  -> ! ; fail).

% this path from Rotorua to Auckland will not go through Taupo
:- (via(rotorua,auckland,[taupo],[rotorua,109,hamilton,126,auckland])
  -> fail ; !).

% this path from Hamilton to Auckland via Hamilton to Auckland will succeed
:- (via(hamilton,auckland,[hamilton,auckland],[hamilton,126,auckland])
  -> ! ; fail).

% can't get from Rotorua to Auckland avoiding Hamilton
:- (avoiding(rotorua,auckland,[hamilton],_) -> fail ; !).

% can get from Rotorua to Auckland by avoiding Wanganui
:- (avoiding(rotorua,auckland,[wanganui],[rotorua,109,hamilton,126,auckland])
  -> ! ; fail).

% there is a path from Wellington to Auckland avoiding all of these towns
:- (avoiding(wellington,auckland,[wanganui,napier,rotorua,new_plymouth],
  [wellington,143,palmerston_north,259,taupo,153,hamilton,126,auckland])
  -> ! ; fail).

