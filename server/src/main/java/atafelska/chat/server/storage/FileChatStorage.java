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


    private List<Message> messages;
    private File chatHistoryFile;

    public FileChatStorage() throws Exception {
        initializeDirectories();
        initializeFile();
        messages = getMessagesFromFile();
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

    private void initializeFile() throws IOException {
        Logger.print("Initializing chat history file");
        chatHistoryFile = new File(CHAT_HISTORY_FILENAME_PATH);
        if (!chatHistoryFile.exists()) {
            boolean fileCreated = chatHistoryFile.createNewFile();
            Logger.print("Chat history file not found. File created: " + fileCreated);

            if (!fileCreated) {
                throw new IOException("Unable to create chat history file.");
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
                    .setUser(User.newBuilder().setName(record.get(0)).build())
                    .setText(record.get(1))
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
            csvPrinter.printRecord(message.getUser().getName(), message.getText());
            writer.close();
        } catch (IOException exception) {
            Logger.print("Unable to save message to file.");
            exception.printStackTrace();
        }
    }
}
