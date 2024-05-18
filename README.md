# Parsers in Java

## Input Information
- The input grammar should be given in the grammar.txt file present inside the 'resources' folder.
- The first line should contain a delimiter. This delimiter can be a single character or a string of characters, but it should neither be a terminal nor a non-terminal of the grammar.
- The second line should contain the terminals separated by the delimiter. Only the delimiter provided in the first line should be used and no extra white spaces or any other characters should be present.
- The second line should contain the non-terminals separated by the same delimiter. Only the delimiter provided in the first line should be used and no extra white spaces or any other characters should be present.
- The next lines should contain the production rules, one in each line. If a non-terminal has multiple expansions, all of them can be entered in the same line as one production rule, with the expansions separated by the pipe '|' symbol.
- Null String productions should be entered using the ε symbol (use Alt + 238 for epsilon symbol).
- The last line should contain the start symbol non-terminal.
- A sample input grammar format is given below: <br />
,                                               <br />
+,id                                            <br />
E,T                                             <br />
E->E+T|T                                        <br />
T->id                                           <br />
E

## Output Information