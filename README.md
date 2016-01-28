# DynamicHashTable
This hashing function simulates a username storage system for 16 character long usernames. It uses multiplicative hashing to resolve any unwanted chaining.

The hash function is: h(k) = floor(m{kA}) where m is the size of the table, k is the username, and A is a constant (.618).

To compute k, I used a custom hashing method:
  For example we'll use the username, "misimpso"
  
  
  1)If the username isn't 16 char long it pads it with "_" until it is.
  
    ex: ________misimpso
    
  2)Each char is then converted to 6 bit binary, converting each "_" to 000000
  
    ex: 000000 | 000000 | 000000 | 000000 | 000000 | 000000 | 000000 | 000000 | 010111 | 010011 | 011101 | 010011 | 010111 | 011010 | 011101 | 011001
    
  3) This binary number is then converted to hex.
  
    ex: 0x000000005D37535DA759
    
  4) Each digit of the hex number is then added up. 5 + D(11) + 3 + 7 + ... etc
  
    ex: 81
    
  5) A salt is randonly generated between 0 and the size of the table -1 and added to this number
  
  
  Now we have k!
  
  
The magic of this hashtable is it has the ability to grow depending on the load of the table. The load is the population of the table divided by the table's size. If the load is above .75, then it grows.
  
The table grows by doubling its original size then finding the next highest prime number. Having a prime table size is needed to resolve chaining when computing the hash key.
  
  
