# Lucene indexer

## How it works
Program initially checks for existence of the index (`/index` directory). If it does not exist, it loads all data crawled in first excercise from the Storage (`/storage` directory) and performs indexing, creating the `/index` directory. Documents are indexed into four fields:
- **title** - A title of the article.
- **date** - A date on which the article was published.
- **author** - The author of the article.
- **content** - The content of the article itself.

After that, provided query is executed over the created index. All hits are printed into the console split into pages. By default, there are three documents per page. This value may be changed in the source code (`src/main/java/cz/zcu/kiv/nlp/ir/Main.java`).

## Compile from sources
To compile an executable JAR file from sources, change your working directory to the root of the project and run
```sh
mvn clean package
```
Executable JAR file called `Lucene-<version>-jar-with-dependencies.jar` will be created in `/target` directory. You can move and rename this executable however you like. However, keep in mind that it expects the Storage to be in your working directory.

## Usage
Program expects only argument - a Lucene query to apply to index. E.g.:
```sh
java -jar lucene.jar 'content:extraliga'
```
If there are more hits than may fit into single page, press `Enter` to print the next page.

Document hits are printed in the following format:
```
<AUTHOR> (DATE) \t <TITLE> 
```
