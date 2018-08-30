

// Initial Imports
import java.io.*;
import java.util.*;

/**
 * @author Justin Kramer, jpk91@pitt.edu
 * Assig3
 * This class will find words in a crossword puzzle when they
 * are entered by the user. It can find words vertically, horizontally,
 * or diagonally. Once a word begins in a direction it can not change
 * direction. If looking for multiple words, the next word must be found
 * around the last letter of the previous word. If not all the words are found
 * the program backtracks and tries a new path with the previous words
 * for all possible manners. The program prints its path for the user.
 */

public class Assig3
{	
    // Instance variables to be used throughout program
    public String[] holdWords = new String[50];
    public int numWord = 0;
    public int[] xs = new int[50];
    public int[] ys = new int[50];
    public int numFound = 0;
    public String wordOut = "";
    public StringBuilder helper;
    public int[] endxs = new int[50];
    public int[] endys = new int[50];
    public int tracker = 0;
    public int rowCounter = 0;
    public int colCounter = 0;
    public int rowCounter2;
    public int colCounter2;
    public boolean backNext;
    public boolean newStart;
    public boolean beginCycle = true;
    public int tooMuch = 0;
    public boolean firstCycle = true;
    public int tryPath = 1;
    public static void main(String [] args)
    {
        // Create a new instance of this class to start instance variables
        new Assig3();
    }

    // Constructor to set things up and make the initial search call.
    public Assig3()
    {
        Scanner inScan = new Scanner(System.in);
                Scanner fReader;
                File fName;
        String fString = "", word = "";

        // Make sure the file name is valid
        while (true)
        {
           try
           {
               System.out.println("Please enter grid filename:");
               fString = inScan.nextLine();
               fName = new File(fString);
               fReader = new Scanner(fName);

               break;
           }
           catch (IOException e)
           {
               System.out.println("Problem " + e);
           }
        }

        // Parse input file to create 2-d grid of characters
        String [] dims = (fReader.nextLine()).split(" ");
        int rows = Integer.parseInt(dims[0]);
        int cols = Integer.parseInt(dims[1]);

        char [][] theBoard = new char[rows][cols];

        for (int i = 0; i < rows; i++)
        {
            String rowString = fReader.nextLine();
            for (int j = 0; j < rowString.length(); j++)
            {
                    theBoard[i][j] = Character.toLowerCase(rowString.charAt(j));
            }
        }

        // Show user the grid
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                    System.out.print(theBoard[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
        // Begin to ask which phrase the user wants to find
        System.out.println("Please enter phrase (sep. by single spaces):");
        String words = inScan.nextLine();
        holdWords = words.split(" ");
        for(int i = 0; i < holdWords.length; i++){
            holdWords[i] = holdWords[i].toLowerCase();
            wordOut += holdWords[i] + " ";
            numWord++;
        }
        // Loop until the user enters nothing for their phrase
        while (!(holdWords[0].equals("")))
        {
            System.out.println("Looking for: " + wordOut);
            System.out.println("containing " + numWord + " words");
            boolean found = false;
            rowCounter = 0;
            while((rowCounter < rows && !found))
            {
                colCounter = 0;
                while((colCounter < cols && !found))
                {
                    tracker = 0;
                    // Start search for each position at index 0 of the word
                    found = findWord(rowCounter, colCounter, holdWords[0], 0, theBoard);
                    while(true){
                        if (found)
                        {
                            tryPath++;
                            xs[0] = rowCounter;  // store starting indices of solution
                            ys[0] = colCounter;
                            numFound++;
                            rowCounter = endxs[tracker];
                            colCounter = endys[tracker];
                            endxs[0] = rowCounter;
                            endys[0] = colCounter;
                            // If there is more than one word to find
                            if(numFound != numWord){
                                try{
                                    // Recursive function to get the rest of the phrase if it exists
                                    newStart = getWords(rows, cols, theBoard, 1, 1);
                                }
                                    catch(ArrayIndexOutOfBoundsException e){   
                                }
                                // If recursive call fails, need to move to next instance of first word
                                if(!newStart){
                                    tooMuch = 0;
                                    numFound = 0;
                                    found = !found;
                                    rowCounter = xs[0];
                                    colCounter = ys[0];
                                    // Reset the board
                                    for (int i = 0; i < rows; i++)
                                    {
                                        for (int j = 0; j < cols; j++)
                                        {
                                            theBoard[i][j] = Character.toLowerCase(theBoard[i][j]);
                                        }
                                    }
                                    // Each possible path including diagonal attempts
                                    while(tryPath < 9){
                                        found = findWord(rowCounter, colCounter, holdWords[0], 0, theBoard, tryPath);
                                        if(found){
                                            break;
                                        }
                                        tryPath++;
                                    }
                                    if(found){
                                        continue;
                                    }
                                }
                            }
                            // Leave loop if failure or complete success
                            break;
                        }
                        else{
                            break;
                        }
                    }
                    // Move to next column or row to search
                    tryPath = 1;
                    colCounter++;
                }
                rowCounter++;
            }
            // Once the answer is definitely correct or wrong
            if (numFound == numWord)
            {
                // Print out results
                System.out.println("The phrase: " + wordOut);
                System.out.println("was found:");
                for(int i = 0; i < numFound; i++){
                    System.out.print(holdWords[i] + ": ");
                    System.out.println("(" + xs[i] + "," + ys[i] + ")" + " to " + "(" + endxs[i] + "," + endys[i] + ")");
                }
                for (int i = 0; i < rows; i++)
                {
                    for (int j = 0; j < cols; j++)
                    {
                            System.out.print(theBoard[i][j] + " ");
                            theBoard[i][j] = Character.toLowerCase(theBoard[i][j]);
                    }
                    System.out.println();
                }
                System.out.println();
            }
            else
            {
                // What to do with failure to find phrase
                System.out.println("The phrase: " + wordOut);
                System.out.println("was not found");
                for (int i = 0; i < rows; i++)
                {
                    for (int j = 0; j < cols; j++)
                    {
                            theBoard[i][j] = Character.toLowerCase(theBoard[i][j]);
                    }
                }
                System.out.println();
            }
            // Reset all variables for loop and ask new phrase
            numWord = 0;
            numFound = 0;
            wordOut = "";
            beginCycle = true;
            tooMuch = 0;
            firstCycle = true;
            System.out.println("Please enter phrase (sep. by single spaces):");
            words = inScan.nextLine();
            holdWords = words.split(" ");
            for(int i = 0; i < holdWords.length; i++){
                holdWords[i] = holdWords[i].toLowerCase();
                wordOut += holdWords[i] + " ";
                numWord++;
            }
        }
    }

    /**
     * Recursive method to find first word searching all directions for each point on board
     * @param r of current row
     * @param c of current column
     * @param word of current word
     * @param loc of location in current word
     * @param bo the board being used
     * @return true if word found, false if otherwise
     */
    public boolean findWord(int r, int c, String word, int loc, char [][] bo) throws ArrayIndexOutOfBoundsException
    {
        //System.out.println("findWord: " + r + ":" + c + " " + word + ": " + loc); // trace code
        // Check boundary conditions
        if (r >= bo.length || r < 0 || c >= bo[0].length || c < 0){
            endxs[tracker] = 90;
            endys[tracker] = 90;
            return false;
        }
        else if (bo[r][c] != word.charAt(loc)){
            endxs[tracker] = 90;
            endys[tracker] = 90;
            return false;
        }
        else  	// current character matches
        {
            bo[r][c] = Character.toUpperCase(bo[r][c]);  // Change it to
                    // upper case.  This serves two purposes:
                    // 1) It will no longer match a lower case char, so it will
                    //    prevent the same letter from being used twice
                    // 2) It will show the word on the board when displayed

            boolean answer;
            if (loc == word.length()-1){
                // Set ending coordinates once found
                endxs[tracker] = r;
                endys[tracker] = c;
                answer = true;
            }
            else
            {		// Try all eight directions if necessary (but only if necessary)
                answer = findWord(r, c+1, word, loc+1, bo, 1);  // Right
                if (!answer)
                    answer = findWord(r+1, c, word, loc+1, bo, 2);  // Down
                if (!answer)
                        answer = findWord(r, c-1, word, loc+1, bo, 3);  // Left
                if (!answer)
                        answer = findWord(r-1, c, word, loc+1, bo, 4);  // Up
                if (!answer)
                        answer = findWord(r-1, c+1, word, loc+1, bo, 5);  // Up right
                if (!answer)
                        answer = findWord(r+1, c+1, word, loc+1, bo, 6);  // Down right
                if (!answer)
                        answer = findWord(r+1, c-1, word, loc+1, bo, 7);  // Down left
                if (!answer)
                        answer = findWord(r-1, c-1, word, loc+1, bo, 8);  // Up left

                // If answer was not found, backtrack.  Note that in order to
                // backtrack for this algorithm, we need to move back in the
                // board (r and c) and in the word index (loc) -- these are both 
                // handled via the activation records, since after the current AR 
                // is popped, we revert to the previous values of these variables.
                // However, we also need to explicitly change the character back
                // to lower case before backtracking.
                if (!answer)
                        bo[r][c] = Character.toLowerCase(bo[r][c]);
            }
            return answer;
        }
    }	
    /**
     * Recursive method to find first word searching all directions for each point on board, keeping the same direction in searching
     * @param r of current row
     * @param c of current column
     * @param word of current word
     * @param loc of location in current word
     * @param bo the board being used
     * @param path the direction to search in
     * @return true if word found, false if otherwise
     */
    public boolean findWord(int r, int c, String word, int loc, char [][] bo, int path) throws ArrayIndexOutOfBoundsException
    {
        //System.out.println("findWord: " + r + ":" + c + " " + word + ": " + loc); // trace code

        // Check boundary conditions
        if (r >= bo.length || r < 0 || c >= bo[0].length || c < 0){
            endxs[tracker] = 90;
            endys[tracker] = 90;
            return false;
        }
        else if (bo[r][c] != word.charAt(loc)){
            endxs[tracker] = 90;
            endys[tracker] = 90;
            return false;
        }
        else  	// current character matches
        {
            if(tooMuch > 200){
                // If loop goes for too long without finding anything end it, safety measure
                return false;
            }
            bo[r][c] = Character.toUpperCase(bo[r][c]);  // Change it to
                    // upper case.  This serves two purposes:
                    // 1) It will no longer match a lower case char, so it will
                    //    prevent the same letter from being used twice
                    // 2) It will show the word on the board when displayed

            boolean answer;
            if (loc == word.length()-1){
                // Set ending coordinates once found
                endxs[tracker] = r;
                endys[tracker] = c;
                answer = true;
            }
            else
            {		// Try all eight directions if necessary (but only if necessary) in the set path
                if(path == 1){
                    answer = findWord(r, c+1, word, loc+1, bo, 1);
                }
                else if(path == 2){
                    answer = findWord(r+1, c, word, loc+1, bo, 2);
                }
                else if(path == 3){
                    answer = findWord(r, c-1, word, loc+1, bo, 3);
                }
                else if(path == 4){
                    answer = findWord(r-1, c, word, loc+1, bo, 4);
                }
                else if(path == 5){
                    answer = findWord(r-1, c+1, word, loc+1, bo, 5);
                }
                else if(path == 6){
                    answer = findWord(r+1, c+1, word, loc+1, bo, 6);
                }
                else if(path == 7){
                    answer = findWord(r+1, c-1, word, loc+1, bo, 7);
                }
                else if(path == 8){
                    answer = findWord(r-1, c-1, word, loc+1, bo, 8);
                }
                else{
                    answer = false;
                }
                // If answer was not found, backtrack.  Note that in order to
                // backtrack for this algorithm, we need to move back in the
                // board (r and c) and in the word index (loc) -- these are both 
                // handled via the activation records, since after the current AR 
                // is popped, we revert to the previous values of these variables.
                // However, we also need to explicitly change the character back
                // to lower case before backtracking.
                if (!answer)
                        bo[r][c] = Character.toLowerCase(bo[r][c]);
            }
            return answer;
        }
    }	
    /**
     * Recursive method to find all other words besides first word, starting at end of first word
     * @param rows of current row
     * @param cols of current column
     * @param word of current word
     * @param theBoard the board being used
     * @param start where in the holdWords array to begin search, which part of phrase to start at
     * @param setPos the direction to search in
     * @return true if phrase found, false if otherwise
     */
    public boolean getWords(int rows, int cols, char[][] theBoard, int start, int setPos) throws ArrayIndexOutOfBoundsException{
        // Initially false answer, increment tooMuch for too much looping condition
        boolean answer = false;
        tooMuch++;
        if(tooMuch > 200){
            return false;
        }
        // Set the tracker to before start for ending coordinates of last word
        tracker = start-1;
        numFound = start;
        // Loop through each word from the starting word in the phrase
        for(int q = start; q < numWord; q++){
            tracker++;
            // Each possible way to search from ending coordinates, cancel out one possible direction each time until only one last way to search
            if(setPos == 1){
                xs[q] = rowCounter;
                ys[q] = colCounter+1;
                // Recursive call with the set path to take and the location of this relative to your rowCounter you're currently at
                answer = findWord(rowCounter, colCounter+1, holdWords[q], 0 , theBoard,1);
                if(answer){
                    numFound++;
                }
                if(!answer){
                    xs[q] = rowCounter+1;
                    ys[q] = colCounter;
                    answer = findWord(rowCounter+1, colCounter, holdWords[q], 0, theBoard, 2);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter;
                    ys[q] = colCounter-1;
                    answer = findWord(rowCounter, colCounter-1, holdWords[q], 0, theBoard, 3);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter-1;
                    ys[q] = colCounter;
                    answer = findWord(rowCounter-1, colCounter, holdWords[q], 0, theBoard, 4);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter-1;
                    ys[q] = colCounter+1;
                    answer = findWord(rowCounter-1, colCounter+1, holdWords[q], 0, theBoard, 5);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter+1;
                    ys[q] = colCounter+1;
                    answer = findWord(rowCounter+1, colCounter+1, holdWords[q], 0, theBoard, 6);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter+1;
                    ys[q] = colCounter-1;
                    answer = findWord(rowCounter+1, colCounter-1, holdWords[q], 0, theBoard, 7);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter-1;
                    ys[q] = colCounter-1;
                    answer = findWord(rowCounter-1, colCounter-1, holdWords[q], 0, theBoard, 8);
                    if(answer){
                        numFound++;
                    }
                }
            }
            if(setPos == 2){
                xs[q] = rowCounter+1;
                ys[q] = colCounter;
                answer = findWord(rowCounter+1, colCounter, holdWords[q], 0, theBoard, 2);
                if(answer){
                    // Change the setPos back to default in this case, return to normal search
                    setPos = 1;
                    numFound++;
                }
                if(!answer){
                    xs[q] = rowCounter;
                    ys[q] = colCounter-1;
                    answer = findWord(rowCounter, colCounter-1, holdWords[q], 0, theBoard, 3);
                    if(answer){
                        setPos = 1;
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter-1;
                    ys[q] = colCounter;
                    answer = findWord(rowCounter-1, colCounter, holdWords[q], 0, theBoard, 4);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter-1;
                    ys[q] = colCounter+1;
                    answer = findWord(rowCounter-1, colCounter+1, holdWords[q], 0, theBoard, 5);
                    if(answer){
                        setPos = 1;
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter+1;
                    ys[q] = colCounter+1;
                    answer = findWord(rowCounter+1, colCounter+1, holdWords[q], 0, theBoard, 6);
                    if(answer){
                        setPos = 1;
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter+1;
                    ys[q] = colCounter-1;
                    answer = findWord(rowCounter+1, colCounter-1, holdWords[q], 0, theBoard, 7);
                    if(answer){
                        setPos = 1;
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter-1;
                    ys[q] = colCounter-1;
                    answer = findWord(rowCounter-1, colCounter-1, holdWords[q], 0, theBoard, 8);
                    if(answer){
                        setPos = 1;
                        numFound++;
                    }
                }
            }
            if(setPos == 3){
                xs[q] = rowCounter;
                ys[q] = colCounter-1;
                answer = findWord(rowCounter, colCounter-1, holdWords[q], 0, theBoard, 3);
                if(answer){
                    numFound++;
                }
                if(!answer){
                    xs[q] = rowCounter-1;
                    ys[q] = colCounter;
                    answer = findWord(rowCounter-1, colCounter, holdWords[q], 0, theBoard, 4);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter-1;
                    ys[q] = colCounter+1;
                    answer = findWord(rowCounter-1, colCounter+1, holdWords[q], 0, theBoard, 5);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter+1;
                    ys[q] = colCounter+1;
                    answer = findWord(rowCounter+1, colCounter+1, holdWords[q], 0, theBoard, 6);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter+1;
                    ys[q] = colCounter-1;
                    answer = findWord(rowCounter+1, colCounter-1, holdWords[q], 0, theBoard, 7);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter-1;
                    ys[q] = colCounter-1;
                    answer = findWord(rowCounter-1, colCounter-1, holdWords[q], 0, theBoard, 8);
                    if(answer){
                        numFound++;
                    }
                }
            }
            if(setPos == 4){
                xs[q] = rowCounter-1;
                ys[q] = colCounter;
                answer = findWord(rowCounter-1, colCounter, holdWords[q], 0, theBoard, 4);
                if(answer){
                    numFound++;
                }
                if(!answer){
                    xs[q] = rowCounter-1;
                    ys[q] = colCounter+1;
                    answer = findWord(rowCounter-1, colCounter+1, holdWords[q], 0, theBoard, 5);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter+1;
                    ys[q] = colCounter+1;
                    answer = findWord(rowCounter+1, colCounter+1, holdWords[q], 0, theBoard, 6);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter+1;
                    ys[q] = colCounter-1;
                    answer = findWord(rowCounter+1, colCounter-1, holdWords[q], 0, theBoard, 7);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter-1;
                    ys[q] = colCounter-1;
                    answer = findWord(rowCounter-1, colCounter-1, holdWords[q], 0, theBoard, 8);
                    if(answer){
                        numFound++;
                    }
                }
            }
            if(setPos == 5){
                xs[q] = rowCounter-1;
                ys[q] = colCounter+1;
                answer = findWord(rowCounter-1, colCounter+1, holdWords[q], 0, theBoard, 5);
                if(answer){
                    numFound++;
                }
                if(!answer){
                    xs[q] = rowCounter+1;
                    ys[q] = colCounter+1;
                    answer = findWord(rowCounter+1, colCounter+1, holdWords[q], 0, theBoard, 6);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter+1;
                    ys[q] = colCounter-1;
                    answer = findWord(rowCounter+1, colCounter-1, holdWords[q], 0, theBoard, 7);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter-1;
                    ys[q] = colCounter-1;
                    answer = findWord(rowCounter-1, colCounter-1, holdWords[q], 0, theBoard, 8);
                    if(answer){
                        numFound++;
                    }
                }
            }
            if(setPos == 6){
                xs[q] = rowCounter+1;
                ys[q] = colCounter+1;
                answer = findWord(rowCounter+1, colCounter+1, holdWords[q], 0, theBoard, 6);
                if(answer){
                    numFound++;
                }
                if(!answer){
                    xs[q] = rowCounter+1;
                    ys[q] = colCounter-1;
                    answer = findWord(rowCounter+1, colCounter-1, holdWords[q], 0, theBoard, 7);
                    if(answer){
                        numFound++;
                    }
                }
                if(!answer){
                    xs[q] = rowCounter-1;
                    ys[q] = colCounter-1;
                    answer = findWord(rowCounter-1, colCounter-1, holdWords[q], 0, theBoard, 8);
                    if(answer){
                        numFound++;
                    }
                }
            }
            if(setPos == 7){
                xs[q] = rowCounter+1;
                ys[q] = colCounter-1;
                answer = findWord(rowCounter+1, colCounter-1, holdWords[q], 0, theBoard, 7);
                if(answer){
                    numFound++;
                }
                if(!answer){
                    xs[q] = rowCounter-1;
                    ys[q] = colCounter-1;
                    answer = findWord(rowCounter-1, colCounter-1, holdWords[q], 0, theBoard, 8);
                    if(answer){
                        numFound++;
                    }
                }
            }
            if(setPos == 8){
                xs[q] = rowCounter-1;
                ys[q] = colCounter-1;
                answer = findWord(rowCounter-1, colCounter-1, holdWords[q], 0, theBoard, 8);
                if(answer){
                    numFound++;
                }
                // If at very end, reset back to normal searching
                setPos = 1;
            }
            if(!answer){
                // If this didn't work and on first word, it can't be found here
                if(q == 1){
                    answer = false;
                    start = numWord;
                }
                else{
                    // Move back to second word one time always
                    if(firstCycle){
                        q = 2;
                        firstCycle = false;
                    }
                    if(beginCycle){
                        // Clear the board
                        for (int i = 0; i < rows; i++)
                        {
                            for (int j = 0; j < cols; j++)
                            {
                                theBoard[i][j] = Character.toLowerCase(theBoard[i][j]);
                            }
                        }
                        // Reset the words you got rid of but need to come back on board
                        for(int a = 0; a <= q-2; a++){
                            findWord(xs[a],ys[a], holdWords[a], 0, theBoard);
                        }
                        rowCounter = endxs[q-2];
                        colCounter = endys[q-2];
                        start = q-1;
                        beginCycle = false;
                    }
                    if(setPos == 9){
                        // Once you tried all setPositions, reset and try starting from next word
                        setPos = 1;
                        if(start+1 < numWord){
                            answer = getWords(rows, cols, theBoard, start+1, setPos);
                            if(answer){
                                start = numWord;
                            }
                        }
                        else{
                            answer = false;
                        }
                    }
                    else{
                        // Try the next way of searching
                        beginCycle = true;
                        if(tooMuch > 200){
                            return false;
                        }
                        answer = getWords(rows, cols, theBoard, start, setPos+1);
                        if(answer){
                            start = numWord;
                        }
                    }
                }
            }
            // After the loops, check if you found the entire rest of the phrase
            rowCounter = endxs[tracker];
            colCounter = endys[tracker];  
            if(numWord == numFound){
                return true;
            }
            if(rowCounter == 90){
                return false;
            }
            if(colCounter == 90){
                return false;
            }
        }
        // Double check after loop that you found everything
        if(numWord == numFound){
            answer = true;
        }
        // Send back your answer
        return answer;
    }
}
