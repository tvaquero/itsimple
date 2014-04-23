(define (domain Blocks_Domain_v1_0)
  (:requirements :typing :negative-preconditions :quantified-preconditions)
  (:types
    Hand - object
    Block - object
    Table - object
  )
  (:predicates
    (on ?blo - Block ?blo1 - Block)
    (holding ?han - Hand ?blo - Block)
    (ontable ?blo - Block ?tab - Table)
    (handempty ?han - Hand)
    (clear ?blo - Block)
  )
  (:action pickUp
   :parameters (?hand - Hand ?x - Block ?table - Table)
   :precondition 
     (and
       (ontable ?x ?table)
       (clear ?x)
       (handempty ?hand)
     )
   :effect
     (and
       (not (clear ?x))
       (holding ?hand ?x)
       (not (handempty ?hand))
       (not (ontable ?x ?table))
     )
  )

  (:action putDown
   :parameters (?hand - Hand ?x - Block ?table - Table)
   :precondition 
     (and
       (not (clear ?x))
       (holding ?hand ?x)
       (not (handempty ?hand))
     )
   :effect
     (and
       (ontable ?x ?table)
       (clear ?x)
       (handempty ?hand)
       (not (holding ?hand ?x))
     )
  )

  (:action stack
   :parameters (?hand - Hand ?x - Block ?y - Block ?table - Table)
   :precondition 
     (and
       (not (clear ?x))
       (clear ?y)
       (holding ?hand ?x)
       (not (handempty ?hand))
     )
   :effect
     (and
       (on ?x ?y)
       (not (clear ?y))
       (clear ?x)
       (handempty ?hand)
       (not (holding ?hand ?x))
     )
  )

  (:action unstack
   :parameters (?hand - Hand ?x - Block ?y - Block ?table - Table)
   :precondition 
     (and
       (on ?x ?y)
       (clear ?x)
       (handempty ?hand)
     )
   :effect
     (and
       (not (clear ?x))
       (clear ?y)
       (holding ?hand ?x)
       (not (handempty ?hand))
       (not (on ?x ?y))
     )
  )

)