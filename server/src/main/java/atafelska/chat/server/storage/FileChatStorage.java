package atafelska.chat.server.storage;

import atafelska.chat.Message;
import atafelska.chat.User;
import atafelska.chat.server.Logger;
import org.apache.commons.csv.*;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileChatStorage implements ChatStorage {
    private static final String STORAGE_DIRECTORY_PATH = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "ATChat";
    private static final String CHAT_HISTORY_FILENAME = "atchat_history.csv";
    private static final String CHAT_HISTORY_FILENAME_PATH = STORAGE_DIRECTORY_PATH + File.separator + CHAT_HISTORY_FILENAME;
    private static final String USERS_STORAGE_FILENAME = "users.csv";
    private static final String USERS_STORAGE_FILENAME_PATH = STORAGE_DIRECTORY_PATH + File.separator + USERS_STORAGE_FILENAME;

    private List<Message> messages;
    private File chatHistoryFile;

    private List<User> users;
    private File usersStorageFile;

    public FileChatStorage() throws Exception {
        initializeDirectories();
        initializeFiles();
        messages = getMessagesFromFile();
        users = getUsersFromFile();
    }

    private void initializeDirectories() throws IOException {
        Logger.print("Initializing storage directories");
        File fileStorageDir = new File(STORAGE_DIRECTORY_PATH);
        if (!fileStorageDir.exists()) {
            boolean dirsCreated = fileStorageDir.mkdirs();
            Logger.print("Chat history storage directory not found. Directories created: " + dirsCreated);

            if (!dirsCreated) {
                throw new IOException("Unable to create storage directories");
            }
        }
    }

    private void initializeFiles() throws IOException {
        Logger.print("Initializing chat history file");
        chatHistoryFile = new File(CHAT_HISTORY_FILENAME_PATH);
        if (!chatHistoryFile.exists()) {
            boolean fileCreated = chatHistoryFile.createNewFile();
            Logger.print("Chat history file not found. File created: " + fileCreated);

            if (!fileCreated) {
                throw new IOException("Unable to create chat history file.");
            }
        }
        Logger.print("Initializing users storage file");
        usersStorageFile = new File(USERS_STORAGE_FILENAME_PATH);
        if (!usersStorageFile.exists()) {
            boolean fileCreated = usersStorageFile.createNewFile();
            Logger.print("Users storage file not found. File created: " + fileCreated);

            if (!fileCreated) {
                throw new IOException("Unable to create users storage file.");
            }
        }
    }

    private List<Message> getMessagesFromFile() throws IOException {
        Logger.print("Loading chat messages from file");
        Reader reader = new FileReader(chatHistoryFile);
        CSVParser csvParser = CSVFormat.DEFAULT.parse(reader);
        List<CSVRecord> records = csvParser.getRecords();
        List<Message> messages = new ArrayList<>();
        for (CSVRecord record : records) {
            Message message = Message.newBuilder()
                    .setUser(User.newBuilder().setId(record.get(0)).setName(record.get(1)).setIsGuest(record.get(2).equals("true")).build())
                    .setText(record.get(3))
                    .setTimestamp(Long.parseLong(record.get(4)))
                    .build();
            messages.add(message);
        }
        return messages;
    }

    @Override
    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public void addMessage(Message message) {
        Logger.print("Add message called");
        saveMessageToFile(message);
        messages.add(message);
    }

    @Override
    public void clearChat() {
        Logger.print("Clearing chat history");
    }

    private void saveMessageToFile(Message message) {
        Logger.print("Saving message to file");
        try {
            FileWriter writer = new FileWriter(chatHistoryFile, true);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL));
            csvPrinter.printRecord(
                    message.getUser().getId(),
                    message.getUser().getName(),
                    message.getUser().getIsGuest(),
                    message.getText(),
                    message.getTimestamp()
            );
            writer.close();
        } catch (IOException exception) {
            Logger.print("Unable to save message to file.");
            exception.printStackTrace();
        }
    }

    private List<User> getUsersFromFile() throws IOException {
        Logger.print("Loading users from file");
        Reader reader = new FileReader(usersStorageFile);
        CSVParser csvParser = CSVFormat.DEFAULT.parse(reader);
        List<CSVRecord> records = csvParser.getRecords();
        List<User> users = new ArrayList<>();
        for (CSVRecord record : records) {
            User user = User.newBuilder()
                    .setId(record.get(0))
                    .setName(record.get(1))
                    .setPassword(record.get(2))
                    .build();
            users.add(user);
        }
        return users;
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public void addUser(User user) {
        Logger.print("Add message called");
        saveUserToFile(user);
        users.add(user);
    }

    private void saveUserToFile(User user) {
        try {
            FileWriter writer = new FileWriter(usersStorageFile, true);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL));
            csvPrinter.printRecord(
                    user.getId(),
                    user.getName(),
                    user.getPassword()
            );
            writer.close();
        } catch (IOException exception) {
            Logger.print("Unable to save user to file.");
            exception.printStackTrace();
        }
    }
}
