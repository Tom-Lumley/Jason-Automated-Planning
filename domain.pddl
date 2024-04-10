(define (domain shoppingdomain)
    (:requirements :strips)
    (:predicates
        (hasMoney)
        (hasPhone)
        (onPhone)
        (messageSent)
        (randomBelief1)
        (randomBelief2)
        (parentsHappy)
        (atGym)
        (motivated)
        (hungry)
        (happy)
        (inCar)
        (hasCar)
        (tired)
        (atWork)
        (bossHappy)
        (atHome)
        (dummyPredicate)
    )

    (:action buyphone
        :parameters ()
        :precondition (hasMoney)
        :effect 
            (and 
                (not (hasMoney))
                (hasPhone)
            )
    )

    (:action dochores
        :parameters ()
        :precondition (dummyPredicate)
        :effect 
            (hasMoney)
    )

    (:action earnsalary
        :parameters ()
        :precondition (dummyPredicate)
        :effect 
            (hasMoney)
    )

    (:action textfriend
        :parameters ()
        :precondition 
                    (and 
                        (onPhone)
                        (hasPhone)
                    )
        :effect 
            (messageSent)
    )

    (:action usephone
        :parameters ()
        :precondition (hasPhone)
        :effect 
            (onPhone)
    )

    (:action goOffPhone
        :parameters ()
        :precondition 
            (and 
                (onPhone)
                (hasPhone)
            )
        :effect 
            (not (onPhone))
    )

    (:action goToGym
        :parameters ()
        :precondition 
            (and 
                (motivated)
                (inCar)
            )
        :effect 
            (and 
                (atGym)
                (hungry)
                (happy)
            )
    )
    (:action getInCar
        :parameters ()
        :precondition (hasCar)
        :effect 
            (and 
                (inCar)
                (not (atHome))
            )
    )
    (:action goToWork
        :parameters ()
        :precondition (inCar)
        :effect 
            (and 
                (atWork)
                (tired)
                (bossHappy)
            )
    )
)
