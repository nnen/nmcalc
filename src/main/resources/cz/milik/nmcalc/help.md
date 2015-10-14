NMCalc
======

Expression evaluator.


Arithmetic Expressions
----------------------

Arithmetic expressions can be written normally with the usual operator precedence. Supported arithmetic operators are: `+ - / *`.  Subexpressions can be put into parentheses, for example: `4 * (12 * 3)`.


Working With Hexadecimal And Octal Numbers
------------------------------------------

The syntax for hexadecimal and octal numbers is the same as in many C-like languages. Hexadecimal numbers are prefixed with `0x` (e.g. 0xcafebabe), while octal numbers are prefixed with just extra `0`. (e.g. 0777). Decimal, hexadecimal and octal numbers can be freely mixed in expressions:

    123 + 0xbabe + 0777 => 48440

Results are, by default, printed out in decimal base. To force a value to be printed out in hexadecimal or octal format, use built-in functions `hex` and `oct` respectively. For example:

    hex(255) => 0xff
    oct(16) => 020

Note these functions do not return string, they still return the same value and thus can be used in expressions such as `hex(10) + 10 => 20`, they just internally annotate the value in way that makes the GUI to print it out in the desired format.