# NMCalc v0.5 dev [![Build Status](https://travis-ci.org/nnen/nmcalc.svg?branch=master)](https://travis-ci.org/nnen/nmcalc)
NMCalc is primarily a desktop calculator application.  It is very young
project, but already has useful functions.

![NMCalc Screenshot](nmcalc.png)


## Usage
The application window is divided into three parts (from top to bottom):
history window, input window and result window.  Enter an expression into
the input window (the middle one), the result is shown as you type in the
result window (the bottom one).  To commit the expression into the history
window, press `Ctrl + Enter`.


## Feedback
To report any issues, make requests or submit suggestions for improvement,
please use either [github issue tracking system](https://github.com/nnen/nmcalc/issues),
or send an email to <jan.milik@gmail.com>.  I will be glad for any feedback.


## Documentation
Documentation consists of this readme and a built-in help system, such as it
is. Type `help()` into the input field and press `Ctrl + Enter` to get more
information about the application, or type `help(help)` and press `Ctrl + Enter`
to get more information about the `help()` function itself.


## Features

### Variables

The variable assignment syntax is simply:

    x = <expression>

Variables are remembered between different expression evaluations, can hold value of any type (real, list, boolean, function, etc.).

### Built-in Functions

The built-in mathematical functions include:

  - trigonometric functions (`sin`, `asin`, `cos`, `acos`, `tan`, `atan`),
  - natural logarithm function (`ln`),
  - least common multiplier function (`lcm`),
  - power and square root function (`pow` and `sqrt` respectively),
  - absolute value, ceiling and floor functions
    (`abs`, `ceil` and `floor` respectively).

### User-defined Functions

There are some builtin functions, such as `sin(<real>)`, `apply(<value>, <arguments>)` or `eval(<expression>)`. Apart from those, users can define custom functions using the syntax:

    def myFunction(<arguments>) <expression>

For example, factorial would be defined as:

    def factorial(x) if x > 1 then x * factorial(x - 1) else 1

Function calls use a C-like syntax, for example: `factorial(5)`. Note - function definitions are themselfs expressions, which evaluate to the function itself (functions are first-class objects and as such can be assigned to variables, passed to function arguments or returned by a function).

### Pattern matching

The pattern matching syntax is:

    match <expression> {
      case <pattern 1> -> <expression>
      case <pattern 2> -> <expression>
      ...
    }

Example:

    def append(lst, value) match lst {
      case first :: rest -> first :: append(lst, value)
      case [] -> [value]
    }
    
    append([1, 2, 3], 4) => [1, 2, 3, 4]
