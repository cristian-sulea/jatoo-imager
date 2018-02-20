 
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <process.h> 
 
int main(int argc, char *argv[]) {
/*
char * cp = getenv("JATOO_IMAGER_PATH");
strcat(cp, "\\lib\\*");
printf("The argument supplied is %s\n", cp);
spawnlp( P_WAIT, "javaw.exe", "javaw.exe", "-cp", cp, "jatoo.imager.JaTooImagerLauncher", NULL );
*/

	if( argc == 2 ) {
		
		char param[255];
		strcpy(param, "\"");
		strcat(param, argv[1]);
		strcat(param, "\"");
		
		spawnlp( P_WAIT, "launcher.exe", "launcher.exe", param, NULL );
		
		char * userFolder = getenv("UserProfile");
		char * fileFolder1 = "\\.jatoo";
		char * fileFolder2 = "\\imager";
		char * fileFolder3 = "\\args";
		
		char file[255];
		strcpy(file, userFolder);
		
		strcat(file, fileFolder1);
		mkdir(file);
		strcat(file, fileFolder2);
		mkdir(file);
		strcat(file, fileFolder3);
		mkdir(file);
		
		char fileName[255];
		tmpnam(fileName);
		
		strcat(file, fileName);
		
		FILE * f = fopen(file, "w");
		fprintf(f, argv[1]);
		fclose(f);	
	}
	
	else {
		spawnlp( P_WAIT, "launcher.exe", "launcher.exe", NULL, NULL );
	}
	
	return 0;
}