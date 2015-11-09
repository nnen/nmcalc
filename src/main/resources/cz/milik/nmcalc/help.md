NMCalc
======

Expression evaluator.


Arithmetic Expressions
----------------------

Arithmetic expressions can be written normally with the usual operator
precedence. Supported arithmetic operators are: `+ - / *`.  Subexpressions can
be put into parentheses, for example: `4 * (12 + 3)`.

### Arithmetic Operators

  - **`+`**, **`-`**, **`\*`**, **`/`** - addition, subtraction, multiplication and division.
  - **`\*\*`** - power, for example: `[2 ** 2, 2 ** 3, 3 ** 2, 3 ** 3] => [4, 8, 9, 27]`
  - `<<` and `>>` - left and right bit shift: `1 << 1 => 2`, `1 << 2 => 4`


Working With Hexadecimal, Octal And Binary Numbers
--------------------------------------------------

The syntax for hexadecimal and octal numbers is the same as in many C-like
languages. Hexadecimal numbers are prefixed with `0x` (e.g. 0xcafebabe), while
octal numbers are prefixed with just extra `0`. (e.g. 0777). Decimal,
hexadecimal and octal numbers can be freely mixed in expressions:

    [nmcalc]
    123 + 0xbabe + 0777 => 48440

Results are, by default, printed out in decimal base. To force a value to be
printed out in hexadecimal or octal format, use built-in functions `hex` and
`oct` respectively. For example:
    
    [nmcalc]
    hex(255) => 0xff
    oct(16) => 020
    bin(128) => 10000000b

Note these functions do not return string, they still return the same value and
thus can be used in expressions such as `hex(10) + 10 => 20`, they just
internally annotate the value in way that makes the GUI to print it out in the
desired format.


Further Topics
--------------

### Functions

For more info on functions, see [`help("functions.md")`](help:/functions.md).
