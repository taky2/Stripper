/**
 * Created by taky2 on 2/1/16.
 */

import java.io.*;

public class Stripper {

    public static void main(String[] args) {

        String fileName = "striptest-";
        int fileNumber = 1;
        String fileType = ".txt";

        //TODO: LOOP THROUGH FILES
        String currentFileName = "";

        for (int i = 1; i <= 3; i++) {

            currentFileName = fileName + fileNumber + fileType;

            File file = new File(currentFileName);
            String fileString = fileToString(file);

            //System.out.println(fileString); //print final string (parsed)
            currentFileName = fileName + fileNumber + "-processed-" + fileType;
            stringToFile(fileString, currentFileName);

            fileNumber++;
        }

        System.out.println("File(s) have been processed.");

/**
        try(PrintWriter out = new PrintWriter(fileName+"_stripped_"+fileType)) {
            out.print(fileString);
            System.out.println("File(s) processed.");

        }
        catch (IOException ex) {

        }
**/
    }

    /***********************************************************************************
     *  Read text from current file, excluding empty lines, into a StringBuilder then  *
     *  send string to removeAllComments method to remove all types of comments        *
     ***********************************************************************************/
    private static String fileToString(File file) {
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

        /***********************************************************************
         *  Remove extraneous blank lines leftover from comments               *
         ***********************************************************************/
        for(int i=0; i < strBldr.length(); i++){
            char currentC = strBldr.charAt(i);

            /** Check first line **/
            if ( (i == 0 ) && (currentC=='\n' || currentC=='\r') ) {
                strBldr.deleteCharAt(i);
            }
            /** Check last line **/
            if ( ( i == strBldr.length()-1 ) && (currentC=='\n' || currentC=='\r') ) {
                strBldr.deleteCharAt(i);
            }
            /** Final check for empty blank lines **/
            if ( i > 0 ) {

                char previousC = strBldr.charAt(i-1);

                if ( (currentC=='\n' || currentC=='\r') && (previousC=='\n' || previousC=='\r') ) {
                    strBldr.deleteCharAt(i);
                }
            }
        }

        String fullString = strBldr.toString();
        return fullString;

    }//end removeAllComments

     private static void stringToFile(String text, String currentFileName) {

        try(PrintWriter out = new PrintWriter(currentFileName)) {
            out.print(text);

        }
        catch (IOException ex) {

        }

        /**
        try(PrintStream out = new PrintStream(fileName+fileType)) {
            out.print(text);
        } catch (IOException e) {

        }
         **/

    }

}//end Stripper


