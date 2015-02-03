
% Author: Tiago Stegun Vaquero
% Date: 10/14/2010

%PSL AXIOMS


%Axiom 9 Everything is either an activity, activity occurrence, timepoint, or object
%Mod Axiom 9 Everything is either an activity, activity occurrence, object, state, or plan. (In our case timepoint does not exists).
%activity(X) :- not(activity_occurrence(X)), not(object(X)), not(state(X)).
%activity_occurrence(X) :- not(activity(X)), not(object(X)), not(state(X)).
%object(X) :- not(activity(X)), not(activity_occurrence(X)), not(state(X)).
%plan(X) :- not(activity(X)), not(activity_occurrence(X)), not(object(X)), not(state(X)).


%Axiom 10 Objects, activities, activity occurrences, and timepoints are all distinct kinds of things. 
%Mod Axiom 10 Objects, activities, activity occurrences, states, plans are all distinct kinds of things. 
%not(activity(X)) :- activity_occurrence(X).
%not(activity_occurrence(X)) :- activity(X).
%not(activity(X)) :- object(X).
%not(object(X)) :- activity(X).
%not(activity(X)) :- state(X).
%not(state(X)) :- activity(X).
%not(activity_occurrence(X)) :- object(X).
%not(object(X)) :- activity_occurrence(X).
%not(activity_occurrence(X)) :- state(X).
%not(state(X)) :- activity_occurrence(X).
%not(object(X)) :- state(X).
%not(state(X)) :- object(X).


%Axiom 11 The occurrence relation only holds between activities and activity occurrences. 
%not(occurrence_of(Occ,A)) :- not(activity(A)).
%activity(A) :- occurrence_of(_,A).
%not(occurrence_of(Occ,A)) :- not(activity_occurrence(Occ)).
activity_occurrence(Occ) :- occurrence_of(Occ,_).



%Axiom 12 Every activity occurrence is the occurrence of some activity. 
%not(activity_occurrence(Occ)) :- not(activity(_#1(Occ))).
%activity(_#1(Occ)) :- activity_occurrence(Occ).
%not(activity_occurrence(Occ)) :- not(occurrence_of(Occ, _#1(Occ))).
%occurrence_of(Occ, _#1(Occ)) :- activity_occurrence(Occ).


%Axiom 13 An activity occurrence is associated with a unique activity. 
%A1 == A2 :- occurrence_of(Occ, A1) and occurrence_of(Occ, A2).


%A.2 Theory of Subactivities


%Axiom 1 subactivity is a relation over activities 
%activity(X) :- !,subactivity(X,_).
%activity(X) :- subactivity(X,_).
%neg subactivity(?a1, ?a2) :- neg activity(?a1).
%activity(X) :- !,subactivity(_,X).
%activity(X) :- subactivity(_,X).
%neg subactivity(?a1, ?a2) :- neg activity(?a2).


%Axiom 2 The subactivity relation is reflexive. 
%subactivity(X,X) :- activity(X).
%neg activity(?a) :- neg subactivity(?a, ?a).


%Axiom 3 The subactivity relation is antisymmetric. 
%?a1 :=: ?a2 :- subactivity(?a1, ?a2) and subactivity ?a2 ?a1).
%neg subactivity(?a1, ?a2) :- subactivity ?a2 ?a1) and neg ?a1 :=: ?a2.
%neg subactivity ?a2 ?a1) :- subactivity(?a1, ?a2) and neg ?a1 :=: ?a2.


%Axiom 4 The subactivity relation is transitive. 
%neg subactivity(?a1, ?a2) :- subactivity(?a2, ?a3) and neg subactivity(?a1, ?a3).
%neg subactivity(?a2, ?a3) :- subactivity(?a1, ?a2) and neg subactivity(?a1, ?a3).
%subactivity(X,Z) :- subactivity(X,Y), subactivity(Y,Z).
%this one solves the recursive
%subactivity(X,Y) :- issubactivity(X,Y).
%subactivity(X,Z) :- issubactivity(X,Y), subactivity(Y,Z).



%A.3 Theory of Occurrence Trees

%occtree.th:ax14
legal(Y) :- leaf(Y,Z) ; (next_subocc(X,Y,Z), legal(X), \+(leaf(Y,Z))).


%A.4 Theory of Discrete States

%Axiom 3 The prior relation is only between states and activity occurrences. Intuitively, it means 
% that the fluent (property of the world) is true before the activity occurrence ?occ.
%state(S) :- prior(S, _). 
state(S) :- holds(S,_).


%A.6 Theory of Complex Activities

%min_precedes is transitive
%this one solves the recursive
min_precedes(X,Y,A) :- next_subocc(X,Y,A).
min_precedes(X,Z,A) :- next_subocc(X,Y,A), min_precedes(Y,Z,A).

%precedes is transitive
precedes(X,Y) :- next_subocc(X,Y,_).
precedes(X,Z) :- next_subocc(X,Y,_), precedes(Y,Z).


%complex.th:ax6
legal(X) :- root(X,_).



% Additional Aximos (Application specific)

%fluent propagation. Propagetes the fluents in every state 
fluent_of(X,S1) :- prior(S0,Occ), holds(S1,Occ), occurrence_of(Occ,Act), fluent_of(X,S0), not(negative_effect(Act,X)).
fluent_of(X,S1) :- prior(_,Occ), holds(S1,Occ), occurrence_of(Occ,Act), effect(Act,X).

%numeric fluent propagation. Propagetes the fluents in every state 
numeric_fluent_of(Function,V0,S1):- prior(S0,Occ), holds(S1,Occ), occurrence_of(Occ,A), numeric_fluent_of(Function,V0,S0), not(assign(A,Function)).




%############### POST DESIGN DATABASE ################


%Quality of plans aximos

%Hierarchy of quality metrics. 
%If the metric is applicable for a given domain it is applicable for all its problem instances 
metric_of(M,P):- project(Proj), domain(D), problem(P), metric_of(M,D), problem_of(P,Proj,D).
%If the metric is applicable for a given project it is applicable for all its problem instances 
metric_of(M,P):- project(Proj), domain(D), problem(P), metric_of(M,Proj), problem_of(P,Proj,D).


%levels of quality
excelent(Plan):- quality(Plan,X), X >= 1.
good(Plan):- quality(Plan,X), X < 1, X >= 0.7.
regular(Plan):- quality(Plan,X), X < 0.7, X >= 0.5.
bad(Plan):- quality(Plan,X), X < 0.5.
verybad(Plan):- quality(Plan,X), X =< 0. 


%Rationale aximos

%function for printing the description of a given rationale
get_rationale_description(R):-description(R,D), print(D).



