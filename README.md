# covid-corelation

This application calculates the COVID-19 correlation coefficient between 

-> the percentage of people that died 
-> the percentage of people that got vaccinated  

given:

--> a continent or 
--> all available countries

This application uses https://github.com/M-Media-Group/Covid-19-API API as a data scource.

To run this application:

java -jar demo-0.0.1-SNAPSHOT.jar

To get correlation coefficient for the particular continent run:

http://localhost:8080/correlation?continent=Europe

Possible parameter values are: Africa, Antarctica, Asia, Oceania, Europe, North America, South America

To get correlation coefficient for countries all over the world run:

http://localhost:8080/correlation

