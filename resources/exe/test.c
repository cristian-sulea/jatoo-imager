
#include <Windows.h>
#include <string.h>
#include <stdio.h>

int main ()
{
char buffer[MAX_PATH];//always use MAX_PATH for filepaths
GetModuleFileName(NULL,buffer,sizeof(buffer));
//printf("buffer: %s",buffer);

  char * ptr;
  int    ch = '\\';

  ptr = strrchr( buffer, ch );
  for(int i = 0, n = strlen(ptr); i < n; i++) buffer[strlen(buffer)-1] = 0;
  printf( "The last occurrence of %c in '%s' is '%s'\n", ch, buffer, ptr );
}