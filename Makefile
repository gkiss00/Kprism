
all :				
					javac Krpsim.java

run :				
					javac Krpsim.java
					java Krpsim files/test.txt 10

clean :				
					rm -rf *.class
					rm -rf */*.class


.PHONY:				all clean