import java.util.*;
import java.io.*;

public class HashFunction3 {
  
  SLItemList[] theArray;
  int arraySize;
  int customerNum;
  int salt;
  
  public double load() {
    if(customerNum == 0) return 0;
    double loadNum = (customerNum/arraySize);
    System.out.println("load = " + loadNum);
    return loadNum;
  }
  
  public void print(){
    for(int locate = 0; locate<arraySize; locate++){
      System.out.format("%d:", locate);
      SItem traverser = theArray[locate].firstWord;
      while(traverser != null){
        System.out.format("%s,", traverser.userID);
        traverser = traverser.next;
      }
      System.out.format("%n");
    }  
  }
  
  public int charToNum(char c) {
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
  
  public int stringToBitseq(String wordToHash) {
    String word = wordToHash;
    int wthBounds = 16 - (wordToHash.length());
    for(int s = 0;s < wthBounds; s++){
      word = "_" + word;
    } 
    
    List<Integer> bitSeqAL = new ArrayList<Integer>(); 
    for (int i = 0; i < word.length(); i++) {
      int charCode = charToNum(word.charAt(i));
      Integer x = new Integer(charCode);
      String binary = Integer.toBinaryString(x);
      int bounds = 6 - (binary.length());
      for(int d = 0; d < bounds; d++){
        binary = "0" + binary;
      }
      for(int f = 0; f < binary.length(); f++){
        String c = binary.charAt(f) + "";
        bitSeqAL.add(Integer.parseInt(c));
      }
    }
    Integer[] bitSeqArray = bitSeqAL.toArray(new Integer[bitSeqAL.size()]);
    
    for (int u = 0; u < bitSeqArray.length; u++){
      if(u % 6 == 0 && u != 0) System.out.print(" | ");
      System.out.print(bitSeqArray[u]);
    }
    System.out.println();
    
    int hexNum = 0;
    
    for(int r = 0; r < bitSeqArray.length; r+=4) {
      String num = "";
      for(int index = 0; index < 4; index++) {
        num = bitSeqArray[index+r] + num + "";
      }
      int decimal = Integer.parseInt(num, 2);
      String hexStr = Integer.toString(decimal, 16);
      int hexInt = Integer.valueOf(hexStr, 16);
      hexNum += hexInt;
    }
    hexNum += salt;
    double a = 0.618;
    
    double fractional = hexNum * a;
    double floorFrac = Math.floor(fractional);
    fractional = fractional - floorFrac;
    
    int hashKey = (int) Math.floor(arraySize*fractional);
    System.out.println("hexNum = " + hexNum + ", hashKey = " + hashKey);
    
    return hashKey;
    
  }
  
  
  
  HashFunction3(int size) {
    
    arraySize = size;
    Random randomGenerator = new Random();
    salt = randomGenerator.nextInt(size-1);
    theArray = new SLItemList[size];
    customerNum = 0;
    
    // Fill the array with WordLists
    
    for (int i = 0; i < arraySize; i++) {
      
      theArray[i] = new SLItemList();
      
    }
    
  }
  public boolean isPrime(int number) {
    if(number % 2 == 0) return false;
    for(int i = 3; i*i <= number; i += 2){
      if(number % i == 0) return false;
    }
    return true;
  }
  
  public int getNextPrime(int minNumToCheck) {
    for(int i = minNumToCheck; true; i++) {
      if(isPrime(i)) return i;
    }
  }
  
  public HashFunction3 reallocateArray(int minArraySize) {
    int newArraySize = getNextPrime(minArraySize);
    
    return moveOldHashTable(newArraySize);
  }
  
  public HashFunction3 moveOldHashTable(int newArraySize){
    HashFunction3 temp = new HashFunction3(newArraySize);
    System.out.println("I made it here!");
    System.out.println("Array size = " + arraySize);
    System.out.println("New Array Size = " + newArraySize);
    
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
    
    // NEW
    
    boolean theWord = theArray[hashKey].find(hashKey, wordToFind);
    
    return theWord;
    
  }
  
  public void addTheArray(String[][] elementsToAdd) {
    for (int i = 0; i < elementsToAdd.length; i++) {
      
      String userID = elementsToAdd[i][0];
      int customerID = Integer.parseInt(elementsToAdd[i][1]);
      
      // Create the Word with the word name and
      // definition
      
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
  
  public static boolean checkString(String word) {
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
    System.out.println("specials = " + specials + ", digits = " + digits + ", letters = " + letters + ", spaces = " + spaces);
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
    if(args.length == 0) {
      Scanner input = new Scanner(System.in);
      
      // Make a 11 item array that will hold words
      // and definitions
      int arraySize = 2;
      
      HashFunction3 wordHashTable = new HashFunction3(arraySize);
      
      int customerID = 1;
      
      String word = "";
      
      // Keep retrieve requests until x is entered
      
      while (!word.equalsIgnoreCase("exit")) {
        
        System.out.println("Enter a Username (1-16 long, no special char)");
        System.out.print("> ");
        word = input.nextLine();
        
        if (checkString(word)) {
        } else {
          if(wordHashTable.load() >= 0.75){
            System.out.println("making it bigger!");
            arraySize *= 2;
            wordHashTable = wordHashTable.reallocateArray(arraySize);
          }
          if(wordHashTable.find(word)) {
            System.out.println("The username is already taken. Please Enter Another.");
          } else {
            // Look for the word requested and print
            // it out to screen
            
            String[][] elementToAdd = {{word, customerID + ""}};
            
            wordHashTable.addTheArray(elementToAdd);
            wordHashTable.print();
            customerID++;
            //System.out.println(wordHashTable.find(wordLookUp));
          }
        }
      }
      input.close();
    } else {
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
                System.out.println("making it bigger!");
                arraySize *= 2;
                wordHashTable = wordHashTable.reallocateArray(arraySize);
              }
              if(wordHashTable.find(word)) {
                System.out.println("The username is already taken. Please Enter Another.");
              } else {
                // Look for the word requested and print
                // it out to screen
                
                String[][] elementToAdd = {{word, customerID + ""}};
                
                wordHashTable.addTheArray(elementToAdd);
                wordHashTable.print();
                customerID++;
                //System.out.println(wordHashTable.find(wordLookUp));
              }
            }
            //System.out.println(i);
            
          }
          scan.close();
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
  
  // Reference to next Word made in the WordList
  
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
