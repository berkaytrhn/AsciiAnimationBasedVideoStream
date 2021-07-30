char* readFile(char* path, int *totalLength){
	FILE *fp;
	int counter;

	// Opeing file according to given parameter
	fp = fopen(path,"r");
	if(!fp){
		printf("Error on openning file!");
	}

	for(counter = 0; fgetc(fp) != EOF; counter++){
		
	}
	// Counting total length to allocate buffer
	(*totalLength) = counter+ (*totalLength);

	fseek(fp, 0, SEEK_SET);
	char* content = malloc(sizeof(char)*(counter + 1) );
	content[counter] = '\0';
	for (int i=0; i < counter; i++){
		content[i] = fgetc(fp);
	}
	// Closing opened file
	fclose(fp);
	return content;
}

