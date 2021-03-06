(define (problem ThreeBlocks)
  (:domain Blocks_Domain_v1_0)
  (:objects
    A - Block
    B - Block
    C - Block
    H1 - Hand
    table1 - Table
  )
  (:init
    (ontable C table1)
    (on B C)
    (on A B)
    (clear A)
    (handempty H1)
  )
  (:goal
    (and
      (on C B)
      (on B A)
      (ontable A table1)
      (not (clear A))
      (not (clear B))
      (clear C)
      (handempty H1)
    )
  )
)