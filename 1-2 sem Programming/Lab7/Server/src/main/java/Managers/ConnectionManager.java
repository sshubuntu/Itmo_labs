package Managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;

/**
 * Manages TCP connections for client-server communication, handling serialization,
 * deserialization, sending, and receiving of objects over the network.
 *
 * @author sh_ub
 */
public class ConnectionManager {
    public static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    /**
     * Creates a server socket on the specified port.
     *
     * @param port the port number to bind the server socket to
     * @return a {@link ServerSocket} instance if successful, or {@code null} if an error occurs
     * @author sh_ub
     */
    public static ServerSocket createConnection(int port) {
        try {
            return new ServerSocket(port);
        } catch (IOException e) {
            logger.error("Failed to create server socket on port {}: {}", port, e.getMessage());
            return null;
        }
    }

    /**
     * Initializes the connection with a client socket, setting up input and output streams.
     *
     * @param clientSocket the client socket to establish the connection with
     * @author sh_ub
     */
    public synchronized void start(Socket clientSocket) {
        try {
            logger.info("New client connected from: {}", clientSocket.getRemoteSocketAddress());
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            logger.error("Failed to initialize connection with client {}: {}",
                    clientSocket.getRemoteSocketAddress(), e.getMessage());
        }
    }

    /**
     * Serializes an object into a byte array.
     *
     * @param object the object to serialize
     * @return a byte array containing the serialized object, or {@code null} if serialization fails
     * @author sh_ub
     */
    public byte[] serializeObject(Object object) {
        logger.debug("Serializing object of type: {}", object.getClass().getName());
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteOut)) {
            out.writeObject(object);
            byte[] result = byteOut.toByteArray();
            logger.debug("Successfully serialized object, size: {} bytes", result.length);
            return result;
        } catch (IOException e) {
            logger.error("Failed to serialize object of type {}: {}", object.getClass().getName(), e.getMessage());
            return null;
        }
    }

    /**
     * Deserializes a byte array into an object of the specified class.
     *
     * @param bytes    the byte array containing the serialized object
     * @param classOfT the class to deserialize the object into
     * @param <T>      the type of the object to deserialize
     * @return the deserialized object cast to type {@code T}, or {@code null} if deserialization fails
     * @author sh_ub
     */
    public <T> T deserialize(byte[] bytes, Class<T> classOfT) {
        if (bytes == null || bytes.length == 0) {
            logger.error("Cannot deserialize: byte array is null or empty");
            return null;
        }
        if (classOfT == null) {
            logger.error("Cannot deserialize: target class is null");
            return null;
        }

        logger.debug("Deserializing {} bytes into class {}", bytes.length, classOfT.getName());
        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(is)) {
            Object obj = in.readObject();
            if (classOfT.isInstance(obj)) {
                logger.debug("Successfully deserialized object: {}", obj);
                return classOfT.cast(obj);
            } else {
                logger.error("Deserialized object type mismatch. Expected: {}, Got: {}",
                        classOfT.getName(), obj.getClass().getName());
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Failed to deserialize into class {}: {}", classOfT.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * Sends a byte array to the connected client.
     *
     * @param arr the byte array to send
     * @author sh_ub
     */
    public void send(byte[] arr) {
        if (arr == null) {
            logger.error("Cannot send: byte array is null");
            return;
        }
        try {
            outputStream.writeObject(arr);
            outputStream.flush();
            logger.debug("Successfully sent {} bytes", arr.length);
        } catch (IOException e) {
            logger.error("Failed to send data: {}", e.getMessage());
        }
    }

    /**
     * Receives a byte array from the connected client.
     *
     * @return the received byte array, or {@code null} if an error occurs
     * @author sh_ub
     */
    public byte[] receive() {
        try {
            byte[] command = (byte[]) inputStream.readObject();
            logger.debug("Successfully received {} bytes", command.length);
            return command;
        } catch (IOException e) {
            logger.error("Failed to receive data: {}", e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            logger.error("Failed to deserialize received data: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Closes the input and output streams associated with the connection.
     *
     * @author sh_ub
     */
    public void close() {
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            logger.info("Successfully closed connection streams");
        } catch (IOException e) {
            logger.error("Failed to close connection streams: {}", e.getMessage());
        }
    }
}