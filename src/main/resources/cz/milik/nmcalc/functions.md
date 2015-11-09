Functions
=========

The syntax for defining functions is:

    [nmcalc]
    def function_name(argument_name1, argument_name2, ...) <expression>

For example function taking two arguments and returning their sum is defined as:

    [nmcalc]
    def add_arguments(a, b) a + b

The definition does two things:

  - It is itself an expression, so it evaluates to the defined function. 
  - It defines a variable in the local scope with the same name as that of the function and assigns the function to that variable. This is responsible for that fact that after defining function `add_arguments`, you can use the symbol `add_arguments` to refer to that function in the same scope.

A little more complex example, which also shows the use of `if-then-else` construct and comparison operator, the factorial function:

    [nmcalc]
    def factorial(x) if x > 1 then x * factorial(x - 1) else 1

Functions are first-class values. This means they can be assigned to variables, passed as arguments or returned from other functions. For example:

    [nmcalc]
    def add2(a, b) a + b => def add2(...

    [nmcalc]
    def apply_binary_operation(operation, a, b) operation(a, b) => def apply_binary_operation(...
    
    apply_binary_operation(add2, 1, 2) => 3

Functions support free variables and closures. This means they can capture variables that exist outside their lexical scope and use them. For example:

    [nmcalc]
    def make_adder(x) def adder(y) x + y

Function `make_adder(x)` takes a single argument `x` and returns a new function which takes argument `y` and returns the sum of `x` and `y`. For example:

    [nmcalc]
    add5 = make_adder(5)
    
    add5(10) => 15
