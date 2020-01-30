# ATChat
Simple chat application designed to communicate between users by sending text messages. Implemented using gRPC framework and JavaFx.

## Motivation
Project was created for practicing Java coding skills and getting know the gRPC framework features.

## Structure
ATChat project consists of three modules:

**1. Common** - app's service definition protofile, which specifies chat's models and methods for communication between client and server; once these data structures are specified, protocol buffer compiler (protoc) is used to automatically generate data access classes in Java language

**2. Server** - server application, which implements service interface and runs a gRPC server to listen for and handle client calls like 			 sending messages or getting active users;
   * ServerApplication - main server class, that builds and starts server listening on port 9000;
   * ChatStorage - interface for storing chat messages history; by default chat is saved in the file using FileChatStorage implementation; fallback is MemoryStorage;
   * ChatServerService:
     - implements all service interface methods to let gRPC infrastructure decode incoming requests, execute these methods, and encode 				  responses
     - holds map of users with their own users- and messages observers that enables to continuously update each user's chat state
     - adds user to the map once joined chat and remove from once logged out
     - additionally implements connection poller in order to update current user's list every 1 second to avoid showing on UI users who 				 lost connection to the server

**3. Client** - simple JavaFx GUI application containing three screens: login, loading placeholder and chat window. This allows users to join chat, send and receive messages and see other active users;
* EntryController and ChatboardController:
  - UI controllers, that extends BaseController class;
  - handle inputs provided by the user (like username, host address or message text) and pass them to the methods called on SceneCoordinator;
  - user can see new messages immediately and follow current users list thanks to observers held by ChatboardController, which, once notified, update UI with latest changes;
  - responsible for displaying error informations;
* SceneCoordinator:
  - coordinates switching between different scenes; 
  - holds an instance of ChatService that enables to send requests to the server using parameters provided by the user; 
  - uses Message- and UsersObservable instances, that notify UI observers about changes;
* ChatService - uses channel specified by server address and port number to create asynchronous stub that makes calls to the server;
