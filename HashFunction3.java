import java.util.*;
import java.io.*;

public class HashFunction3 {
  
  SLItemList[] theArray; //Creates the array of SLItemLists
  int arraySize;         
  int customerNum;
  int salt;
  
  public double load() {                          //Load is the population of the table divided by the table size
    if(customerNum == 0) return 0;                //If the load is greater than .75 than the table size gets increased.
    double loadNum = (customerNum/arraySize);     //to the next prime number after doubling the initial size.
    //System.out.println("load = " + loadNum);
    return loadNum;
  }
  
  public void print(){
    for(int locate = 0; locate<arraySize; locate++){
      System.out.format("%d:", locate);
      SItem traverser = theArray[locate].firstWord;
      while(traverser != null){
        System.out.format("%s [%d],", traverser.userID, traverser.customerID);
        traverser = traverser.next;
      }
      System.out.format("%n");
    }  
  }
  //*Part of the Hashing Function*
  public int charToNum(char c) {                       //Coverts the character to its ASCII value
    int asciiVal = (int) c;
    if(asciiVal == 95) {
      return (asciiVal - 95);
    }else if (asciiVal > 47 && asciiVal < 58) {
      return (asciiVal - 47);
    } else if(asciiVal > 64 && asciiVal < 91) {
      return (asciiVal - 28);
    } else if(asciiVal > 96 && asciiVal < 123){
      return (asciiVal - 86);
    }
    return -1;
  }
  
  public int stringToBitseq(String wordToHash) {   //If the word isn't 16 characters long, pad it with "_" until it is
    String word = wordToHash;
    int wthBounds = 16 - (wordToHash.length());
    for(int s = 0;s < wthBounds; s++){
      word = "_" + word;
    } 
    
    List<Integer> bitSeqAL = new ArrayList<Integer>(); //Create an ArrayList of Integers for the easy of adding onto a 
    for (int i = 0; i < word.length(); i++) {          //dynamic data-structure
      int charCode = charToNum(word.charAt(i));        //Get the ASCII value of the character
      Integer x = new Integer(charCode);               //Convert it from int to Integer
      String binary = Integer.toBinaryString(x);       //Convert Integer to a string of binary
      int bounds = 6 - (binary.length());              //If the binary string isnt 6 char long, then pad it with 0s
      for(int d = 0; d < bounds; d++){
        binary = "0" + binary;
      }
      for(int f = 0; f < binary.length(); f++){        //Add it to the ArrayList
        String c = binary.charAt(f) + "";
        bitSeqAL.add(Integer.parseInt(c));
      }
    }
    Integer[] bitSeqArray = bitSeqAL.toArray(new Integer[bitSeqAL.size()]);  //Convert ArrayList to Array
    
    /*for (int u = 0; u < bitSeqArray.length; u++){        //For fancy printing
      if(u % 6 == 0 && u != 0) System.out.print(" | ");
      System.out.print(bitSeqArray[u]);
    }
    System.out.println();*/
    
    int hexNum = 0;
    
    for(int r = 0; r < bitSeqArray.length; r+=4) { //Takes the four lowest bits converts them to hex, then adds it
      String num = "";                             //to hexNum. Then moves to the next four bits.
      for(int index = 0; index < 4; index++) {
        num = bitSeqArray[index+r] + num + "";
      }
      int decimal = Integer.parseInt(num, 2);
      String hexStr = Integer.toString(decimal, 16);
      int hexInt = Integer.valueOf(hexStr, 16);
      hexNum += hexInt;
    }
    hexNum += salt;                                //Adds the added hex values to a randomly generated number
    double a = 0.618;                              //A real number used for multiplicative hashing
    
    double fractional = hexNum * a;                //Some fraction
    double floorFrac = Math.floor(fractional);     //Either 1 or 0
    fractional = fractional - floorFrac;           //Removes the 1 in 1.XXX if it is there
    
    int hashKey = (int) Math.floor(arraySize*fractional);  //the key needed for the hash function
    //System.out.println("hexNum = " + hexNum + ", hashKey = " + hashKey);
    
    return hashKey;
    
  }
  //*End of the Hashing Function*
  
  
  HashFunction3(int size) {
    
    arraySize = size;
    Random randomGenerator = new Random();
    salt = randomGenerator.nextInt(size-1);
    theArray = new SLItemList[size];
    customerNum = 0;
    
    // Fill the array with SLItemLists
    
    for (int i = 0; i < arraySize; i++) {
      
      theArray[i] = new SLItemList();
      
    }
    
  }
  public boolean isPrime(int number) {    //Checks if number is prime
    if(number % 2 == 0) return false;
    for(int i = 3; i*i <= number; i += 2){
      if(number % i == 0) return false;
    }
    return true;
  }
  
  public int getNextPrime(int minNumToCheck) {    //Finds the next highest prime number.
    for(int i = minNumToCheck; true; i++) {
      if(isPrime(i)) return i;
    }
  }
  
  public HashFunction3 reallocateArray(int minArraySize) {  //Calls moveOldHashTable to reallocate the hash table based 
    int newArraySize = getNextPrime(minArraySize);          //on the new prime size.
    
    return moveOldHashTable(newArraySize);
  }
  
  public HashFunction3 moveOldHashTable(int newArraySize){  //Moves the old values of the hash table into their new
    HashFunction3 temp = new HashFunction3(newArraySize);   //positions.
    //System.out.println("I made it here!");
    //System.out.println("Old Array size = " + arraySize);
    //System.out.println("New Array Size = " + newArraySize);
    
    for(int i = 0; i < arraySize; i++) {
      SItem traverser = theArray[i].firstWord;
      while(traverser != null) {
        String[][] elementToAdd = {{traverser.userID, traverser.customerID + ""}};
        temp.addTheArray(elementToAdd);
        traverser = traverser.next;
      }
    }
    arraySize = newArraySize;
    return temp;
  }
  
  public void insert(SItem newWord) {
    
    String wordToHash = newWord.userID + "";
    
    // Calculate the hashkey for the Word
    
    int hashKey = stringToBitseq(wordToHash);
    
    // Add the new word to the array and set
    // the key for the word
    
    theArray[hashKey].insert(newWord, hashKey);
    
  }
  
  public boolean find(String wordToFind) {
    
    // Calculate the hash key for the word
    
    int hashKey = stringToBitseq(wordToFind);
    
    boolean theWord = theArray[hashKey].find(hashKey, wordToFind);
    
    return theWord;
    
  }
  
  public void addTheArray(String[][] elementsToAdd) {
    for (int i = 0; i < elementsToAdd.length; i++) {
      
      String userID = elementsToAdd[i][0];
      int customerID = Integer.parseInt(elementsToAdd[i][1]);
      
      // Create the userID with the word name and
      // customerID
      
      SItem newWord = new SItem(userID, customerID);
      
      // Add the Word to theArray
      
      insert(newWord);
      
    }
    customerNum++;
    
  }
  
  public void displayTheArray() {
    
    for (int i = 0; i < arraySize; i++) {
      
      System.out.println("theArray Index " + i);
      
      theArray[i].displayWordList();
      
    }
    
  }
  
  public static boolean checkString(String word) {     //Checks to see if the word contains any special chars.
    int specials = 0, digits = 0, letters = 0, spaces = 0;
    for (int i = 0; i < word.length(); ++i) {
      char ch = word.charAt(i);
      if (!Character.isDigit(ch) && !Character.isLetter(ch) && !Character.isWhitespace(ch)) {
        ++specials;
      } else if (Character.isDigit(ch)) {
        ++digits;
      } else if (Character.isWhitespace(ch)) {
        ++spaces;
      } else {
        ++letters;
      }
    }
    //System.out.println("specials = " + specials + ", digits = " + digits + ", letters = " + letters + ", spaces = " + spaces);
    if(specials > 0 && spaces > 0) {
      System.out.println("No spaces and no special characers!");
      return true;
    } else if(specials > 0) {
      System.out.println("No special characters!");
      return true;
    } else if(spaces > 0){
      System.out.println("No spaces");
      return true;
    } else {
      return false;
    }
  }
  
  public static void main(String[] args) {
    if(args.length == 0) { //Take user input
      Scanner input = new Scanner(System.in);
      
      int arraySize = 2;
      
      HashFunction3 wordHashTable = new HashFunction3(arraySize);  //Create the hash table
      
      int customerID = 1;  //Initialize the customer ID
      
      String word = "";
      
      while (!word.equalsIgnoreCase("exit")) {
        
        System.out.println("Enter a Username (1-16 long, no special char)");
        System.out.print("> ");
        word = input.nextLine();
        
        if (checkString(word)) {
        } else {
          if(wordHashTable.load() >= 0.75){   //Check load to make sure we arent near capacity
            //System.out.println("making it bigger!");
            arraySize *= 2;                   //Double Arraysize
            wordHashTable = wordHashTable.reallocateArray(arraySize);  //Reallocate the array into the new one.
          }
          if(wordHashTable.find(word)) {      //If it's already there dont add it
            System.out.println("The username is already taken. Please Enter Another.");
          } else if(word.equalsIgnoreCase("exit")){
            System.out.println("Exiting");
          } else {
           
            String[][] elementToAdd = {{word, customerID + ""}}; //Create 2D array with word and cusomterID
            
            wordHashTable.addTheArray(elementToAdd);
            
            customerID++;
            //System.out.println(wordHashTable.find(wordLookUp));
          }
        }
      }
      input.close();
      wordHashTable.print();
    } else {    //Accept files of usernames instead of user input
      for(String a : args) {   
        File file = new File(a);
        try {
          Scanner scan = new Scanner(file);
          
          int arraySize = 2;
          HashFunction3 wordHashTable = new HashFunction3(arraySize);
          int customerID = 1;
          String word = "";
          while(scan.hasNextLine()) {
            word = scan.nextLine();
            if (checkString(word)) {
            } else {
              if(wordHashTable.load() >= 0.75){
                //System.out.println("making it bigger!");
                arraySize *= 2;
                wordHashTable = wordHashTable.reallocateArray(arraySize);
              }
              if(wordHashTable.find(word)) {
                System.out.println("The username is already taken. Please Enter Another.");
              } else if(word.equalsIgnoreCase("exit")){
                System.out.println("Exiting");
              } else {
                
                String[][] elementToAdd = {{word, customerID + ""}};
                
                wordHashTable.addTheArray(elementToAdd);
                
                customerID++;
                //System.out.println(wordHashTable.find(wordLookUp));
              }
            }
            //System.out.println(i);
            
          }
          scan.close();
          wordHashTable.print();
        } catch(FileNotFoundException e) {
          e.printStackTrace();
        }
      }
    }
  }
}

class SItem {
  
  public String userID;
  public int customerID;
  
  public int key;
  
  // Reference to next Item in ItemList
  
  public SItem next;
  
  public SItem(String userID, int customerID) {
    
    this.userID = userID;
    this.customerID = customerID;
    
  }
  
  public String toString() {
    
    return "userID: " + userID + " , customerID" + customerID;
    
  }
  
}

class SLItemList {
  
  // Reference to first Link in list
  // The last Link added to the LinkedList
  
  public SItem firstWord = null;
  
  public void insert(SItem newWord, int hashKey) {
    
    SItem previous = null;
    SItem current = firstWord;
    
    newWord.key = hashKey;
    
    while (current != null && newWord.key > current.key) {
      
      previous = current;
      current = current.next;
      
    }
    
    if (previous == null)
      firstWord = newWord;
    
    else
      previous.next = newWord;
    
    newWord.next = current;
    
  }
  
  public void displayWordList() {
    
    SItem current = firstWord;
    
    if(current == null) System.out.println("Current is NULL");
    while (current != null) {
      
      System.out.println(current);
      current = current.next;
      
    }
    
  }
  
  public boolean find(int hashKey, String wordToFind) {
    
    SItem current = firstWord;
    
    // Search for key, but stop searching if
    // the hashKey < what we are searching for
    // Because the list is sorted this allows
    // us to avoid searching the whole list
    
    while (current != null) {//&& current.key <= hashKey) {
      
      // NEW
      
      if (current.userID.equals(wordToFind)){
        return true;
      }
      current = current.next;
      
    }
    return false;
    
  }
}
