// Agent bob in project t1

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

/* Bob is an example of Action Predicate Failure
For textfriend to be executed successfully,
Bob must have the beliefs that he:
1) Owns a phone
2) Is on his phone

If he does not have these he must buy a phone and earn money then use his phone
*/


+!start : true <- textfriend.
