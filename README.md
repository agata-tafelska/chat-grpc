# ATChat
Simple chat application designed to communicate between users by sending text messages. Implemented using gRPC framework and JavaFx.

#### Motivation
Project was created for practicing java coding skills and gettig know the gRPC framework features.

#### Structure
ATChat project includes three modules:
- common - app's service definition protofile, which specifies chat's models and methods that will be called remote lately; once those data structures are specified, protocol buffer compiler (protoc) is used to automatically generate data access classes in java language
- server - server application, which implements service interface and runs a gRPC server to listen for and handle client calls like sending messages or getting active users
- client - client application which logic is responsible for calling the same service methods on the server; this module provides also simple GUI written using JavaFx to interact with user in easy and nice way
