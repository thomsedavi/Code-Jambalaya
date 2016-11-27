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


int in(char c, char* s, int pos){
  // Assume everything is already in the same case
  int i;

  for(i = 0; i < pos; i++)
    if(c == s[i]) return 1;
    
  return 0;
} 


void buildtable (char* key, char* decode){ // this changed from encode
  
  // This function needs to build an array of mappings in 'encode' from plaintext characters
  // to encihered characters.

  // You are implementing a Caesar 1 & 2 combo Cypher as given in the lab handout.
  // Your code here:

  // probably need to declare some stuff here!
  
  int a, b, x, y;
  int offset = strlen(key) - 1;
  int q = offset;

  char* alpha = (char*)malloc(sizeof(char)*26);
  char* encode = (char*)malloc(sizeof(char)*26);

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

  for (x = 0, y = 26; x < y; x++) {
    decode[encode[x] - 'A'] = x + 'A';
  }

}

int main(int argc, char **argv){
  // format will be: 'program' key {encode|decode}
  // We'll be using stdin and stdout for files to encode and decode.

  // first allocate some space for our translation table.

  char* decode = (char*)malloc(sizeof(char)*26); // this changed from encode
  char ch;

  if(argc != 2){
    fprintf(stderr,"format is: '%s' key", argv[0]);
    exit(1);
  }

  // Build translation tables, and ensure key is upcased and alpha chars only.

  buildtable(argv[1], decode); // this changed from encode

  // write the key to stderr (so it doesn't break our pipes)

  fprintf(stderr,"key: %s - %d\n", decode, strlen(decode));


  // the following code does the translations.  Characters are read 
  // one-by-one from stdin, translated and written to stdout.

    ch = fgetc(stdin);
    while (!feof(stdin)) {
      if(isalpha(ch))          // only decrypt alpha chars
	fputc(decode[ch-'A'], stdout);
     else 
	fputc(ch, stdout);
      ch = fgetc(stdin);      // get next char from stdin
    }
}

