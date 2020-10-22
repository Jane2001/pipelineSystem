Water Pipeline System

  What is it?
  -----------
  Water Pipeline System is a program that determines whether a path 
  exists from one point in the pipeline system to another and, if 
  the path exists, calculates its minimum length. 
  The pipeline system coordinates and the points between which the
  path needs to be calculated are read from the csv file. Also coordinate 
  systems are loaded into the H2 database. Next, a csv file that contains 
  information about whether the path exists and the minimum length is generated .

  
  Launching and working with the program
  --------------------------------------
  1)Input coordinates and length between them in pipeline_system.csv file that is
  in pipelineSystemProject directory
  2)Input points between which will be calculated minimum length if path exists into
  points.csv file which is in pipelineSystemProject directory
  3)To launch the program open the CMD and input the path to 
  the pipelineSystemProject-1.0-SNAPSHOT.jar file that is in pipelineSystemProject directory.
  4)Input command java -jar pipelineSystemProject-1.0-SNAPSHOT.jar <options>
  	-r is required option
	-ds display the pipeline system coordinates and length between them
	-dp display points between which will be found minimum length if path exists
	-g <filename.csv> generates csv file 
	-s  display the content of generated file
  5) In case wrong options' input will be displayed table of commands