Support SELECT, GROUP BY, SUM, COUNT, MAX, MIN keywords


Support GROUP BY with multiple conditions (only on String column)


Support at most one aggregation operator

Notice: To make it simple, I assume the model of database is immutable so the input file should maintain
a fixed order of column and type. But there is no limitation on the order of sql statement.

That's say, they are same if you input 
"select name, spent" & "select spent, name"

I use Maven to build the whole project, the build command is:

mvn clean compile assembly:single

Then execute:

cat test.csv | java -jar ./target/Database-1.0-jar-with-dependecies.jar "SELECT name, transaction_id, MAX(spent) GROUP BY name, transaction_id"

