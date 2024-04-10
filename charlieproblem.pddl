(define (problem textingproblem)
  (:domain shoppingdomain)
  (:init
    (hasMoney)
    (hasPhone)
    (parentsHappy)
    (onPhone)
    (dummyPredicate)
  )
  (:goal
    (and
      (messageSent)
    )
  )
)
