#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>

char upcase(char ch){
  if(islower(ch))
    ch -= 'a' - 'A';
  return ch;
}

char* fixkey(char* s){
  int i, j;
  char plain[26]; // assume key < length of alphabet, local array on stack, will go away!

  for(i = 0, j = 0; i < strlen(s); i++){
    if(isalpha(s[i])){
      plain[j++] = upcase(s[i]);
    }
  }
  plain[j] = '\0'; 
  return strcpy(s, plain);
}

void buildtable (char* key, char* encode){

  // This function needs to build an array of mappings in the 'encode' array from plaintext characters
  // to encypered characters.  The encode array will be indexed by the plaintext char.  To 
  // make this a useful 0-26 index for the array, 'A' will be stubtracted from it (yes you
  // can do this in C).  You can see this in the main(){} below.  The values in the array 
  // will be the cipher value, in the example at the top A -> H, B -> J, etc.

  // You are implementing a Caesar 1 & 2 combo Cypher as given in handout.
  // Your code here:

  // probably need to declare some stuff here!
  
  int a, b, x, y;
  int offset = strlen(key) - 1;
  int q = offset;

  char* alpha = (char*)malloc(sizeof(char)*26);

  for (x = 0, y = 26; x < y; x++) {
    alpha[x] = x + 'A';
  }
  
  fixkey(key); // fix the key, i.e., uppercase and remove whitespace and punctuation

  for (x = 0, y = 26; x < y; x++) {
    encode[x] = ' ';
  }

  for (x = 0, y = offset; x < y; x++) {
    for (a = 0, b = 26; a < b; a++ ) {
      if (key[x] == alpha[a]) {
        alpha[a] = ' ';
        encode[q] = key[x];
        q++;
        q = q % 26;
      }
    }
  }

  a = encode[q-1] - 'A';

  for (x = 0, y = 26; x < y; x++) {
    b = x + a;
    b = b % 26;
    if (alpha[b] != ' ') {
      encode[q] = alpha[b];
      q++;
      q = q % 26;
    }
  }
  
}

int main(int argc, char **argv){
  // format will be: 'program' key {encode|decode}
  // We'll be using stdin and stdout for files to encode and decode.

  // first allocate some space for our translation table.

  char* encode = (char*)malloc(sizeof(char)*26);
  char ch;

  if(argc != 2){
    printf("format is: '%s' key", argv[0]);
    exit(1);
  }

  // Build translation tables, and ensure key is upcased and alpha chars only.

  buildtable(argv[1], encode);

  // write the key to stderr (so it doesn't break our pipes)

  fprintf(stderr,"key: %s - %d\n", encode, strlen(encode));

  // the following code does the translations.  Characters are read 
  // one-by-one from stdin, translated and written to stdout.

  ch = fgetc(stdin);
  while (!feof(stdin)) {
    if(isalpha(ch)){        // only encrypt alpha chars
      ch = upcase(ch);      // make it uppercase
      fputc(encode[ch-'A'], stdout);
    }else 
      fputc(ch, stdout);
    ch = fgetc(stdin);      // get next char from stdin
  }
}
  
