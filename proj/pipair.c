#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include<sys/types.h>
#include<sys/wait.h>
#include<errno.h>
#include<string.h>

/*
 * ECE 453/653 SE 465 CS 447/647
 * Demo for Tutorial 2
 *
 * This demo is intended to show the output format that veriy.sh expects
 * 
 * Instructions:
 * 1. Compile this file, name the binary "pipair"
 * 2. Put the compiled binary into the directory where "verify.sh" is
 * 3. Run verify.sh
 * 4. Observe one test cases pass.
 *
 * */

int main(int argc, char *argv[]) {
	int support=3;
	int confidence=60;
	char cmd[50]="opt -print-callgraph ";
	char filepath[10];

	
	if(argc>=2){
		printf("filename:%s\n",argv[1]);
		strcat(cmd,argv[1]);
		if(argc>=3){
			printf("%s\n",argv[2]);
			support = atoi(argv[2]);
			printf("support:%d\n",support);
			if(argc>=4){
				printf("%s\n",argv[3]);
				confidence = atoi(argv[3]);
				printf("confidence:%d\n",confidence);
			}
		}
	}


	FILE *fp;
	char path[5000];
	fp = popen(cmd, "r");
	if (fp == NULL) {
		printf("Failed to run command\n" );
		exit(1);
	}
	fgets(path, sizeof(path), fp);
	pclose(fp);
	printf("%s\n", path);
	

	/*
	char s[5000];
	int pfd[2];
	pipe(pfd);

	pid_t opt =fork();
   	if(opt == 0){
		close(pfd[0]);
		dup2(pfd[1],STDOUT_FILENO);
		char * opt_argv[] = {"opt", "-print-callgraph", "test1/hello.bc"};
		execvp("opt", argv);
		printf("opt\n");
	}else{
		close(pfd[1]);
		dup2(pfd[0],STDIN_FILENO);
		while(fgets(s,5000,stdin)){
			printf("%s\n",s);
		}
		printf("pi\n");
	}
	*/

	
	printf("bug: A in scope2, pair: (A, B), support: 3, confidence: 75.00%%\n");
	printf("bug: A in scope3, pair: (A, D), support: 3, confidence: 75.00%%\n");
	printf("bug: B in scope3, pair: (B, D), support: 4, confidence: 80.00%%\n");
	printf("bug: D in scope2, pair: (B, D), support: 4, confidence: 80.00%%\n");
	
	return 0;
}
