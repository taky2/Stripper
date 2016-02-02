/**
 * Created by taky2 on 2/1/16.
 */

import java.io.*;

public class Stripper {

    public static void main(String[] args) {

        String fileName = "striptest-";
        int fileNumber = 1;
        String fileType = ".txt";

        String currentFileName = "";
        File file;
        String fileString;

        for (int i = 1; i <= 13; i++) {

            if ( (i > 9) && (i <= 13) ) {

                currentFileName = fileName+fileNumber+"a"+fileType;
                file = new File(currentFileName);
                fileString = fileToString(file, currentFileName);
                currentFileName = fileName + fileNumber + "a" + "-processed" + fileType;
                stringToFile(fileString, currentFileName);

                currentFileName = fileName+fileNumber+"b"+fileType;
                file = new File(currentFileName);
                fileString = fileToString(file, currentFileName);
                currentFileName = fileName + fileNumber + "b" + "-processed" + fileType;
                stringToFile(fileString, currentFileName);

                currentFileName = fileName+fileNumber+"c"+fileType;
                file = new File(currentFileName);
                fileString = fileToString(file, currentFileName);
                currentFileName = fileName + fileNumber + "c" + "-processed" + fileType;
                stringToFile(fileString, currentFileName);
                /**
                currentFileName = fileName+fileNumber+"d"+fileType;
                file = new File(currentFileName);
                fileString = fileToString(file, currentFileName);
                currentFileName = fileName + fileNumber + "d" + "-processed" + fileType;
                stringToFile(fileString, currentFileName);
                 **/
            } else {
                currentFileName = fileName + fileNumber + fileType;
                file = new File(currentFileName);
                fileString = fileToString(file, currentFileName);
                //System.out.println(fileString); //print final string (parsed)
                currentFileName = fileName + fileNumber + "-processed" + fileType;
                stringToFile(fileString, currentFileName);
            }

            fileNumber++;
        }

        System.out.println("\nFiles have been processed.");

    }

    /***********************************************************************************
     *  Read text from current file into a StringBuilder then send string to           *
     *  removeAllComments method to prune comments of all type.                        *
     ***********************************************************************************/
    private static String fileToString(File file, String currentFileName) {
        String textFile = "";

        //TRY-WITH-RESOURCES: closes resource after program finishes using it
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            StringBuilder stringBuilder = new StringBuilder();
            String lineFromFile = bufferedReader.readLine();

                while(lineFromFile != null) {

                    if ( !lineFromFile.isEmpty()){
                        stringBuilder.append(lineFromFile);
                        stringBuilder.append(System.lineSeparator());

                    }
                    lineFromFile = bufferedReader.readLine();
                }
                textFile = stringBuilder.toString();
                //bufferedReader.close(); //not necessary because try-with-resources statement was used

        } catch (Exception e) {
            e.printStackTrace();
        }

        return removeAllComments(textFile);
    }//end fileToString

    /***********************************************************************
     *  Create enumeration of all possible states while parsing comments   *
     ***********************************************************************/
    enum FilterState
    {
        TEXT_BODY, STRING, COMMENT_START, BLOCK_COMMENT, LINE_COMMENT, COMMENT_END
    }

    /***********************************************************************
     *  Method for removing all types of comments:                         *
     *                                                                     *
     *  Loops through all characters of provided string. The switch        *
     *  statement will branch to a specific FilterState case which         *
     *  corresponds to the current character being assessed within         *
     *  the provided string.                                               *
     ***********************************************************************/
    public static String removeAllComments(String textFile)  {
        FilterState filterState = FilterState.TEXT_BODY;

        StringBuilder strBldr = new StringBuilder();

        char previousChar=' ';

        for(int i = 0; i < textFile.length(); i++){
            char currentChar = textFile.charAt(i);

            switch(filterState){
                case TEXT_BODY:
                    if(currentChar=='/')
                        filterState = FilterState.COMMENT_START;
                    else {
                        if (currentChar == '"')
                            filterState = FilterState.STRING;
                        strBldr.append(currentChar);
                    }
                    break;
                case STRING:
                    if(currentChar == '"' && previousChar!='\\')
                        filterState = FilterState.TEXT_BODY;
                    strBldr.append(currentChar);
                    break;
                case COMMENT_START:
                    if(currentChar=='*'){
                        filterState = FilterState.BLOCK_COMMENT;
                    }
                    else if(currentChar=='/'){
                        filterState = FilterState.LINE_COMMENT;
                    }
                    else {
                        filterState = FilterState.TEXT_BODY;
                        strBldr.append(previousChar+currentChar);
                    }
                    break;
                case BLOCK_COMMENT:
                    if(currentChar=='*')
                        filterState=FilterState.COMMENT_END;
                    break;
                case LINE_COMMENT:
                    if(currentChar=='\n' || currentChar=='\r') {
                        filterState = FilterState.TEXT_BODY;
                        strBldr.append(currentChar);
                    }
                    break;
                case COMMENT_END:
                    if(currentChar=='/')
                        filterState = FilterState.TEXT_BODY;
                    else if(currentChar!='*')
                        filterState = FilterState.BLOCK_COMMENT;
                    break;
                default:
                    System.out.println("Error: unknown case");
                    return null;
            }//end switch statement
            previousChar = currentChar;
        }//end for loop

        String fullString = strBldr.toString();

        /***********************************************************************
         *  Remove extraneous blank lines leftover from comments               *
         ***********************************************************************/
        String finalText = "";

        //TRY-WITH-RESOURCES: closes resource after program finishes using it
        try(BufferedReader reader = new BufferedReader(new StringReader(fullString))) {
            StringBuilder finalBuilder = new StringBuilder();
            String temp = reader.readLine();

            while(temp != null) {

                //Check for line with only tabs and spaces
                if (temp.trim().length() == 0) {
                    temp = temp.trim();
                    //stringBuilder.append(lineFromFile);
                }

                if ( !temp.isEmpty()){
                    finalBuilder.append(temp);
                    finalBuilder.append(System.lineSeparator());

                }
                temp = reader.readLine();
            }
            finalText = finalBuilder.toString();
            //bufferedReader.close(); //not necessary because try-with-resources statement was used

        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalText;

    }//end removeAllComments

     private static void stringToFile(String text, String currentFileName) {

        try(PrintWriter out = new PrintWriter(currentFileName)) {
            out.print(text);

        }
        catch (IOException ex) {

        }
    }

}//end Stripper


