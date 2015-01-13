Introduction
-------------

The search-engine contains two modules.
indexer - For creating indexes.
s-engine - Executes a submitted query and outputs the result with their priority values.

Dependencies
-------------
java 1.7

Apache Maven 3

How to Build
--------------

Option -1

For both modules, jar files are provided with the distribution run them once and get the proper command format.

Option -2 (Needs Apache Maven 3)

issue $> mvn clean install

This would create the jar files needed.

How to Run
-----------

indexer  - java -jar <jar_name> <base_folder_path> <index_file_path>

s-engine - java -jar <jar_name> <query_file_path> <index_file_path> <answer_file_path>

