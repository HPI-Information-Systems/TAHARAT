# TAHARAT: Cleaning ill-formed Records in CSV Files
Code repository of the TAHARAT project, developed at the Information Systems Group of the Hasso Plattner Institute.

TAHARAT takes a raw CSV file containing ill-formed records as input and outputs a cleaned version of the file by correcting the structure of the incorrectly formatted records.

## Setup

1. The code is written using Java 8.
2. Download the available code as a zip file or cloned SURAGH repository.
3. Include the following external libraries: 
	- univocity-parsers-2.9.1 or its latest version
	- commons-lang3-3.11
	- guava-31.0.1-jre

   Note: The aforementioned libraries are a part of the project and are available in the same repository. Please update the path for the referenced libraries.
   
 4. Input/Output: It can be executed directly from the command line
	-  Open a command prompt window and go to the directory where you saved the TAHARAT project code, use `javac` and `java` commands to compile and run the program. Note -- entry point is `Main_Class.java`.  
	
	 `OR`
	     
	-  For the IDE, import TAHARAT as a project, and you can set the arguments in the `Run Configuration`. 
	
       
The system takes a CSV file as input and outputs a CSV file.
