###
# This Makefile can be used to make a scanner for the bach language
# (Yylex.class) and to make a program that tests the scanner (P2.class).
#
# The default makes both the scanner and the test program.
#
# make clean removes all generated files.
#
# Note: P2.java will not compile unless Yylex.class exists.
#
###

# define the java compiler to be used and the flags
JC = javac
FLAGS = -g -cp $(CP)
CP = ./deps:.

P2.class: P2.java Yylex.class sym.class
	$(JC) $(FLAGS) P2.java

Yylex.class: bach.jlex.java ErrMsg.class sym.class
	$(JC) $(FLAGS) bach.jlex.java

bach.jlex.java: bach.jlex sym.class
	java -cp $(CP) JLex.Main bach.jlex

sym.class: sym.java
	$(JC) $(FLAGS) sym.java

ErrMsg.class: ErrMsg.java
	$(JC) $(FLAGS) ErrMsg.java

	
###
# testing - MY TESTERS ARE SELF OUTPUTTING AND OUTPUT ERRORS SO I DID NOT ADD ANY DIFFS
# PLEASE NOTE DIFFS WERE REMOVED BECAUSE THEY CLUTTERED THE TERMINAL AND
# TOOK FOCUS AWAY FROM THE SELF DESCRIBING OUTPUT - Abhinav Nandwani
###
test:
	java -cp $(CP) P2 
###
# clean up generated files
###
clean:
	rm -f *~ *.class bach.jlex.java

cleantest:
	rm -f allTokens.out stringTokens.out integerTokens.out charCountTokens.out illegalCharacter.out eof.out