#include<stdio.h>
#include<stdlib.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include<string.h>
#include <arpa/inet.h>
#include <fcntl.h> // for open
#include <unistd.h> // for close
#include<pthread.h>
#include<time.h>

#define GETLOCALTIME "GETLOCALTIME"

char client_message[112];
// char buffer[1024];
pthread_mutex_t lock = PTHREAD_MUTEX_INITIALIZER;

void * socketThread(void *arg)
{


  char buffer[112];
  
  int newSocket = *((int *)arg);
  
  // received command message
  recv(newSocket , client_message , 112 , 0);

  // Send message to the client socket 
  pthread_mutex_lock(&lock);
  char *message = malloc(sizeof(client_message));
  

  strcpy(message,client_message);
  // for get local time
  char *getLocaltime = GETLOCALTIME;
  int equal = 1;
  int i = 0;
  while(message[i] != '\0' && getLocaltime[i]!='\0'){
      if (message[i] != getLocaltime[i]){
        equal = 0;
      }
      i ++;
  }


  if (equal){
    printf("found == %s",message);
    // time_t current_time;
    // time(&current_time);
    // printf("\n%ld\n", current_time);

    // char timebuf[200];
    // sprintf(timebuf, "%ld", current_time);
    // printf("%s%ld\n",timebuf,sizeof(timebuf));
    // send(newSocket,timebuf,200,0);

    time_t curTime;
    struct tm*time_info;
    char timeString[9];
    

    time(&curTime);
    time_info=localtime(&curTime);
    strftime(timeString,sizeof(timeString),"%H:%M",time_info);
    printf("time:");
    puts(timeString);
    // send(newSocket,timeString,9,0);



    char sendMessage[121];

    int j = 0 ;

    for(j = 0;j < 112 && message[j] != '\0';j ++){
      sendMessage[j] = message[j];
      // copy main message and command
    }
    int k = 0;
    for(j = 112;j < 121;j ++){
      sendMessage[j] = timeString[k++];
      // copy main message and command
    }




    send(newSocket,sendMessage,121,0);

  }else{
    printf("not equal");

  }


  // to do for local os ---------------------->


  // strcpy(message,"Hello Client : ");
  // strcat(message,client_message);
  // strcat(message,"\n");
  // strcpy(buffer,message);



  free(message);
  pthread_mutex_unlock(&lock);
  sleep(1);
  // send(newSocket,buffer,20,0);
  printf("Exit socketThread \n %s",buffer);
  close(newSocket);
  pthread_exit(NULL);
}




int main(){
  int serverSocket, newSocket;
  struct sockaddr_in serverAddr;
  struct sockaddr_storage serverStorage;
  socklen_t addr_size;

  //Create the socket. 
  serverSocket = socket(PF_INET, SOCK_STREAM, 0);

  // Configure settings of the server address struct
  // Address family = Internet 
  serverAddr.sin_family = AF_INET;

  //Set port number, using htons function to use proper byte order 
  serverAddr.sin_port = htons(8080);

  //Set IP address to localhost 
  serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");


  //Set all bits of the padding field to 0 
  memset(serverAddr.sin_zero, '\0', sizeof serverAddr.sin_zero);

  //Bind the address struct to the socket 
  bind(serverSocket, (struct sockaddr *) &serverAddr, sizeof(serverAddr));

  //Listen on the socket, with 40 max connection requests queued 
  if(listen(serverSocket,50)==0)
    printf("Listening\n");
  else
    printf("Error\n");
    pthread_t tid[60];
    int i = 0;
    while(1)
    {
        //Accept call creates a new socket for the incoming connection
        addr_size = sizeof serverStorage;
        newSocket = accept(serverSocket, (struct sockaddr *) &serverStorage, &addr_size);

        //for each client request creates a thread and assign the client request to it to process
       //so the main thread can entertain next request
        if( pthread_create(&tid[i++], NULL, socketThread, &newSocket) != 0 )
           printf("Failed to create thread\n");

        if( i >= 50)
        {
          i = 0;
          while(i < 50)
          {
            pthread_join(tid[i++],NULL);
          }
          i = 0;
        }
    }
  return 0;
}
