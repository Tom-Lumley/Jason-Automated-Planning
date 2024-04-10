
/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

/* 
Charlie is an example of no applicable plan failure
The context is only supported currently if it is simple:

eg hasMoney & hasPhone etc simple bob has this belief and this one....
This can be adapted though in parsing operations

Because we by default select plan(0) to be the plan to fix 
It contains the actual plan the others are just kind of there
Cannot be achieved as no way (not developed yet) to be happy or motivated etc
*/

+!start : hasPhone & messageSent & hasMoney <- .print("i texted my friend!").
+!start : motivated <- usephone.
+!start : happy <- usephone.
+!start : false <- usephone.
