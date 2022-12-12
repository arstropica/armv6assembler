# Simple ARMv6 (Dis)Assembler

Written for a Computer Organization class.

## Supported Commands

 - ADD 
 - SUB 
 - AND 
 - ASR 
 - MOV 
 - EOR 
 - LDR
 - LSL 
 - LSR 
 - MUL 
 - MVN 
 - RSB
 - STR 
 - ORR 
 - ROR 
 - RSB 
 - RRX

Includes register, immediate, S and conditional operations.

## Dependencies

`org.sqlite.JDBC` driver

## Testing
`assembler.tests.Tester` class validates commands from the tab separated `io/tests.csv` file. Add new commands for further testing.

## Implementation

 1. Download driver from xerial's sqlite [repository release page](https://github.com/xerial/sqlite-jdbc/releases). 
 2. Include driver in classpath and export project as a jar.
 3. Program entry points are `assembler.Assembler`, `assembler.Disassembler`, and `assembler.tests.Tester`.
 4. Run with: `java -jar <jar_file>`.
