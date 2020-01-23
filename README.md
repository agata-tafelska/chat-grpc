# ATChat
Simple chat application designed to communicate between users by sending text messages. Implemented using gRPC framework and JavaFx.

#### Motivation
Project was created for practicing Java coding skills and getting know the gRPC framework features.

#### Structure
ATChat project consists of three modules:
- common - app's service definition protofile, which specifies chat's models and methods for communication between client and server; once these data structures are specified, protocol buffer compiler (protoc) is used to automatically generate data access classes in Java language
- server - server application, which implements service interface and runs a gRPC server to listen for and handle client calls like sending messages or getting active users
- client - simple JavaFx GUI application containing three screens: login, loading placeholder and chat window. This allows users to login, send and receive messages and see other active users.
