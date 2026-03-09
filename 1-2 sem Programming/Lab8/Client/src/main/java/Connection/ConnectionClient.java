package Connection;


import Utility.ExecutionResponse;

import java.io.*;
import java.net.*;

public class ConnectionClient {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private int serverPort;
    private String serverHost;

    public ConnectionClient(int serverPort, String serverHost) {
        this.serverPort = serverPort;
        this.serverHost = serverHost;
    }

    public byte[] serializeObject(Object object) throws IOException {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteOut)) {
            out.writeObject(object);
            return byteOut.toByteArray();
        }
    }

    public ExecutionResponse deserializeObject(byte[] bytes) {
        if (bytes == null) return new ExecutionResponse( "Ответ от сервера не получен, выполнение отменено!",false);
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        try (ObjectInputStream in = new ObjectInputStream(is)) {
            return (ExecutionResponse) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ExecutionResponse("Error in deserialization: " + e.getMessage(), false);
        }
    }

    public void send(byte[] arr) throws IOException {
        outputStream.writeObject(arr);
        outputStream.flush();
    }

    public byte[] receive() throws IOException, ClassNotFoundException {
        return (byte[]) inputStream.readObject();
    }

    public ExecutionResponse start()  {
        try {
            socket = new Socket(serverHost, serverPort);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            return new ExecutionResponse("", true);
        } catch (IOException e) {
            return new ExecutionResponse("Server isn't available now. Reconnecting after 5 seconds.", false);
        }
    }

}
