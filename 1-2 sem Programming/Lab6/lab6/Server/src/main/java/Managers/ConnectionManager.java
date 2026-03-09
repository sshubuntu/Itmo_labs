package Managers;

import Command.CommandWithArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;

public class ConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    private DatagramSocket ds;
    private InetAddress lastClientAddress;
    private int lastClientPort;

    public ConnectionManager(int port) throws SocketException {
        this.ds = new DatagramSocket(port);
        logger.info("ConnectionManager initialized on port {}", port);
    }

    public byte[] serializeObject(Object object) throws IOException {
        logger.debug("Serializing object of type: {}", object.getClass().getName());
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteOut)) {
            out.writeObject(object);
            return byteOut.toByteArray();
        }
    }

    public CommandWithArgs deserialize(byte[] bytes) {
        logger.debug("Deserializing command from byte array (length: {})", bytes.length);
        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(is)) {
            CommandWithArgs command = (CommandWithArgs) in.readObject();
            logger.debug("Deserialized command: {}", command);
            return command;
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Deserialization error: {}", e.getMessage(), e);
            return null;
        }
    }

    public void send(byte[] arr) throws IOException {
        if (ds == null || ds.isClosed()) {
            String errorMsg = "Socket is not initialized";
            logger.error(errorMsg);
            throw new IOException(errorMsg);
        }
        if (lastClientAddress == null) {
            String errorMsg = "No client address available";
            logger.error(errorMsg);
            throw new IOException(errorMsg);
        }

        logger.debug("Sending response to {}:{} ({} bytes)", lastClientAddress, lastClientPort, arr.length);

        DatagramPacket packet = new DatagramPacket(arr, arr.length, lastClientAddress, lastClientPort);
        ds.send(packet);

        logger.debug("Response sent successfully");
    }

    public byte[] receive() throws IOException {
        byte[] arr = new byte[65535];
        DatagramPacket packet = new DatagramPacket(arr, arr.length);

        logger.debug("Waiting for incoming datagram...");
        ds.receive(packet);
        logger.info("Received datagram from {}:{} ({} bytes)",
                packet.getAddress(), packet.getPort(), packet.getLength());

        this.lastClientAddress = packet.getAddress();
        this.lastClientPort = packet.getPort();

        return arr;
    }

    public void close(){
        ds.close();
    }
}