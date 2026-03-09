package Connection;


import Utility.ExecutionResponse;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ConnectionClient {
    private DatagramChannel dc;
    private SocketAddress serverAddr;
    private SocketAddress responseAddr;

    public ConnectionClient(int ServerPort, String ServerHost) {
        this.serverAddr = new InetSocketAddress(ServerHost, ServerPort);
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
        if (dc == null || !dc.isOpen()) {
            throw new IOException("Channel is not initialized");
        }
        ByteBuffer buf = ByteBuffer.wrap(arr);
        dc.send(buf, serverAddr);
        buf.clear();
    }

    public byte[] receive() throws IOException {
        if (dc == null || !dc.isOpen()) {
            throw new IOException("Channel is not initialized");
        }
        ByteBuffer buffer = ByteBuffer.allocate(65535);
        responseAddr = dc.receive(buffer);
        if (responseAddr != null) {
            byte[] data = new byte[buffer.position()];
            buffer.flip();
            buffer.get(data);
            return data;
        }
        return null;
    }

    public boolean start() {
        try {
            dc = DatagramChannel.open();
            dc.bind(null);
            dc.connect(serverAddr);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to start connection: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (dc != null) {
                dc.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing channel: " + e.getMessage());
        }
    }
}
