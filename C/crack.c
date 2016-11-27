#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#define TEXT_SIZE 200000  // Note, the longer the text the more likely you will get a good 'decode' from the start.
#define ALEN 26         // Number of chars in ENGLISH alphabet
#define CHFREQ "ETAONRISHDLFCMUGYPWBVKJXQZ" // Characters in order of appearance in English documents.
#define ALPHABET "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

  // takes any character and if it is lower case, converts it to the upper case before returning it

char upcase(char ch){
  if(islower(ch))
    ch -= 'a' - 'A';
  return ch;
}

  // takes and a blank frequency table and an array of characters where the alphabetical characters have all been converted
  // to upper case. For each character of the alphabet in the array, the corresponding number in the frequency table gets
  // incremented.

void frequency(int i, int j, char* text, int* freq) {
  int k;
  for (k = j; k < strlen(text); k = k + i) {
    if (isalpha(text[k]))
      freq[text[k] - 'A']++;
  }
}

  // Takes the frequency table and uses the CHFREQ array to map the highest frequency onto the 'map' char array starting
  // with the beginning of the CHFREQ

void mapfrequency(int* freq, char* map) {
  int j;
  int i = 0;
  int l = 0;

  // 'i' becomes the highest frequency of any letter

  for (j = 0; j < 26; j++) {
    if (i < freq[j])
      i = freq[j];
  }

  // loops through the alphabet repetitively, matching the CHFREQ character onto the character with the highest
  // frequency in turn, until all letters of the alphabet have been mapped.

  do {
    for (j = 0; j < 26; j++) {
      if (freq[j] == i) {
        map[j] = CHFREQ[l++];
      }
    }
    i--;
  } while (l < 26);
}

 // takes the text to be translated and pushes it through the map onto the resulting output

void maptext(int i, int j, char* text, char* map, char* output) {
  int k;

  for (k = j; k < strlen(text); k = k + i) {
    if(isalpha(text[k]))
      output[k] = map[text[k] -'A'];
    else 
      output[k] = text[k];
  }
}


int main(int argc, char **argv){

  // first allocate some space for our input text (we will read from stdin).

  char* text = (char*)malloc(sizeof(char)*TEXT_SIZE+1);
  char* output;
  char* map;
  char ch;
  int n, i, j;
  
  int* freq; //array of frequency of characters

  if(argc > 1 && (n = atoi(argv[1])) > 0); else{ fprintf(stderr,"Malformed argument, use: crack [n], n > 0\n"); exit(-1);} // get the command line argument n
  
  // Now read TEXT_SIZE or feof worth of characters (whichever is smaller) and convert to uppercase as we do it.
  // Added: changed to count frequencies as we read it in

  for(i = 0, ch = fgetc(stdin); i < TEXT_SIZE && !feof(stdin); i++, ch = fgetc(stdin)){
    text[i] = (ch = (isalpha(ch)?upcase(ch):ch));
  }
  text[i] = '\0'; // terminate the string properly.

  /* At this point we have two things,
   *   1. The input cyphertext in "text"
   *   2. The maximum number of keys to try (n) - we'll be trying 1..n keys.
   *
   * What you need to do is as follows:
   *   1. create a for-loop that will check key lengths from 1..n
   *   2. for each i <= n, spit the cypher text into i sub-texts.  For i = 1, 1 subtext, for i = 2, 2 subtexts, of alternating characters etc.
   *   3. for each subtext: 
   *          a. count the occurance of each letter 
   *          b. then map this onto the CHFREQ, to create a map between the sub-text and english
   *          c. apply the new map to the subtext 
   *   4. merge the subtexts
   *   5. output the 'possibly' partially decoded text to stdout.  This will only look OK if i was the correct number of keys
   *
   * what you need to output (sample will be provided) - exactly:
   * i maps -> stderr
   * i 'possible' translations
   *
   * You would be wise to make seperate functions that perform various sub-tasks, and test them incrementally.  Any other approach will likely
   * make your brain revolt.  This isn't a long program, mine is 160 lines, with comments (and written in a very verbose style) - if yours is
   * getting too long, double check you're on the right track.
   *
   */
  
  for (i = 1; i <= n; i++) {
    output = (char*)calloc(sizeof(char), (sizeof(char)*TEXT_SIZE+1));
    for (j = 0; j < i; j++) {
      freq = (int*)calloc(sizeof(int), sizeof(int)*26);
      map = (char*)calloc(sizeof(char), (sizeof(char)*26));
      frequency(i, j, text, freq);
      mapfrequency(freq, map);
      maptext(i, j, text, map, output);
    }
    int k;
    for (k = 0; k < strlen(output); k++) {
      printf("%c", output[k]);
    }
    printf("\n");
  }

  free(text);
  free(output);
  free(map);
  free(freq);

  return 0;
}