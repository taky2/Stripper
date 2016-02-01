/**
 * Created by taky2 on 2/1/16.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Stripper {

    public static void main(String[] args) {
        File file = new File("stripper.txt");
        String fileString = fileToString(file);

        System.out.println(fileString); //print final string (parsed)
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
        BODY, COMMENT, STRING, BLOCK_COMMENT, LINE_COMMENT, END_OF_COMMENT
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
        FilterState filterState = FilterState.BODY;

        StringBuilder strBldr = new StringBuilder();

        char previousChar=' ';

        for(int i = 0; i < textFile.length(); i++){
            char currentChar = textFile.charAt(i);

            switch(filterState){
                case BODY:
                    if(currentChar=='/')
                        filterState = FilterState.COMMENT;
                    else {
                        if (currentChar == '"')
                            filterState = FilterState.STRING;
                        strBldr.append(currentChar);
                    }
                    break;
                case COMMENT:
                    if(currentChar=='*'){
                        filterState = FilterState.BLOCK_COMMENT;
                    }
                    else if(currentChar=='/'){
                        filterState = FilterState.LINE_COMMENT;
                    }
                    else {
                        filterState = FilterState.BODY;
                        strBldr.append(previousChar+currentChar);
                    }
                    break;
                case LINE_COMMENT:
                    if(currentChar=='\n' || currentChar=='\r') {
                        filterState = FilterState.BODY;
                        strBldr.append(currentChar);
                    }
                    break;
                case BLOCK_COMMENT:
                    if(currentChar=='*')
                        filterState=FilterState.END_OF_COMMENT;
                    break;
                case END_OF_COMMENT:
                    if(currentChar=='/')
                        filterState = FilterState.BODY;
                    else if(currentChar!='*')
                        filterState = FilterState.BLOCK_COMMENT;
                    break;
                case STRING:
                    if(currentChar == '"' && previousChar!='\\')
                        filterState = FilterState.BODY;
                    strBldr.append(currentChar);
                    break;
                default:
                    System.out.println("unknown case");
                    return null;
            }
            previousChar = currentChar;
        }

        /***********************************************************************
         *  Remove extraneous blank lines leftover from comments               *
         ***********************************************************************/
        for(int i=0; i < strBldr.length(); i++){
            char currentC = strBldr.charAt(i);

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

}//end Stripper


