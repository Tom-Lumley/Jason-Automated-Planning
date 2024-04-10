(define (problem textingproblem)
  (:domain shoppingdomain)
  (:init
    (hasPhone)
    (parentsHappy)
    (dummyPredicate)
  )
  (:goal
    (and
      (onPhone)
    )
  )
)
