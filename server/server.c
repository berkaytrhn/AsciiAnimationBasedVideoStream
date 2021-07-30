#include<stdio.h>
#include<stdlib.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include<string.h>
#include<arpa/inet.h>
#include<fcntl.h>
#include<unistd.h>
#include<pthread.h>

// Header file for file reading
#include "fileReader.h" 

// mutex for accessing buffer
pthread_mutex_t lock = PTHREAD_MUTEX_INITIALIZER;

// mutex for reading files
pthread_mutex_t fileReaderMutex = PTHREAD_MUTEX_INITIALIZER;


int totalBufferLength = 0;
int numberOfVideos = 0;
int PORT = 0;

//buffer two dimensional pointer
char** buffer;


//producer thread parameter struct
typedef struct{
	int* producerId;
	char* filePath;
} Producer;

void* producer(void* argument){
    // reads file contents

	int id = *(((Producer*)argument)->producerId);
	char* path = ((Producer*)argument)->filePath;

    //reading file, locking mutex
	pthread_mutex_lock(&fileReaderMutex);
	char* content = readFile(path, &totalBufferLength);
	// read operation over, unlock mutex
    pthread_mutex_unlock(&fileReaderMutex);
	
	return (void *)content;
}

void * socketThread(void *arg)
{
    // receive initial channel number value from client
    char client_message[15];
    int newSocket = *((int *)arg);
    read(newSocket , client_message , 15);

    // Convert received channel number to buffer index
    int channelIndex = atoi(client_message)-1;

    // Reading channel content from buffer, locking mutex
    pthread_mutex_lock(&lock);
    char* content = *(buffer+channelIndex);
    
    // Reading operation over, unlocking mutex
    pthread_mutex_unlock(&lock);

    while(1){
        // Send channel video content
        send(newSocket, content , strlen(content), 0);
        
        int recvresult = read(newSocket, client_message, 15);
        
        // if received message is 0, that means client closed connection,
        // return from thread(end)
        if(recvresult==0){
            printf("Connection closed.\n");
            return NULL;
        }
    }
}

int main(int argc, char* argv[]){
    
    // Parsing video file names dynamically

    // This dynamic operation causes program to
    // be able to stream even more than 3 channels
    
    // Buffer allocation is done by using this dynamic context
    for(int i=0;i<argc;i++){
        if(strcmp(argv[i], "-s") == 0){
            numberOfVideos = atoi(argv[i+1]);
        }
        if(strcmp(argv[i], "-p") == 0){
            PORT = atoi(argv[i+1]);
        }
    }
    int fileNameLengths[numberOfVideos];
    int fileCounter=0;
    for(int i=0;i<argc;i++){
        if(strstr(argv[i], "-ch")){
            fileNameLengths[fileCounter++] = strlen(argv[i+1]);
        }
    }
    int totalLength = 0;
    for(int i=0;i<numberOfVideos;i++){
        totalLength += fileNameLengths[i];
    }
    char** files = malloc(sizeof(char)*(totalLength)*(numberOfVideos));
    fileCounter=0;
    for(int i=0;i<argc;i++){
        if(strstr(argv[i], "-ch")){
            files[fileCounter++] = argv[i+1];
        }
    }  
    // Creating producer threads and read files
    pthread_t producerThreads[numberOfVideos];
    pthread_mutex_init(&fileReaderMutex, NULL);

    for(int i=0;i<numberOfVideos;i++){
        // Packaging needed information for produces threads inside struct
        Producer *producer1 = (Producer *) malloc(sizeof(Producer));
        int* id= malloc(sizeof(int));
        *id=i;
        producer1->producerId= id;
        producer1->filePath = *(files + i);

        // Creatingn producer threads
        pthread_create(producerThreads + i, NULL, &producer, (void *)producer1);
    }
    char** contents = malloc(sizeof(char)*1);

    for(int i=0;i<numberOfVideos;i++){
        char* content;

        // Joining producer threads
        pthread_join(*(producerThreads + i), (void**)&content);
        contents = (char **) realloc(contents, numberOfVideos * totalBufferLength);
        contents[i] = content;
    }
    

    // Buffer allocation
    buffer = malloc(sizeof(char) * (totalBufferLength * numberOfVideos + 3 * strlen("break\n")));
    
    // Write contents to buffer
    for(int i=0;i<numberOfVideos;i++){
        buffer[i] = contents[i];
        strcat(buffer[i], "\nbreak\n");
    }

    // Socket Programming Part
    int serverSocket, newSocket;
    struct sockaddr_in serverAddr;
    struct sockaddr_storage serverStorage;
    socklen_t addr_size;

    // Creating the socket 
    serverSocket = socket(PF_INET, SOCK_STREAM, 0);

    // Configuring the settings of server address 
    serverAddr.sin_family = AF_INET;
    // Setting port number
    serverAddr.sin_port = htons(PORT);
    // Setting ip address
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");

    memset(serverAddr.sin_zero, '\0', sizeof serverAddr.sin_zero);

    // Binding the address struct to the socket 
    bind(serverSocket, (struct sockaddr *) &serverAddr, sizeof(serverAddr));

    // Listen on the socket
    if(listen(serverSocket,50)==0){
        printf("Listening\n");
        
        // Maximum number of threads
        pthread_t tid[60];

        // Detaching threads
        pthread_attr_t detachedThread;
        pthread_attr_init(&detachedThread);
        pthread_attr_setdetachstate(&detachedThread, PTHREAD_CREATE_DETACHED);
        

        int i = 0;
        while(1)
        {
        // Accept requset and create a new socket for the client
        addr_size = sizeof serverStorage;
        newSocket = accept(serverSocket, (struct sockaddr *) &serverStorage, &addr_size);

        // For each client request creates a thread and assign the client request to it to process
        // So the main thread can can handle next request
        if( pthread_create(&tid[i++], NULL, &socketThread, &newSocket) != 0 )
            printf("Failed to create thread\n");
        }

        // Destroy detach settings
        pthread_attr_destroy(&detachedThread);

    }else{
        printf("Error\n");
    }
    return 0;
}