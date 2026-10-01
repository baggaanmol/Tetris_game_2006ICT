package tetris.game;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ExternalPlayer {

    private static final String HOST = "localhost";
    private static final int PORT = 3000;

    private final ObjectMapper mapper = new ObjectMapper();

    public OpMove requestMove(PureGame game) {

        // A NEW socket is created for every request
        try (Socket socket = new Socket()) {

            socket.connect(
                    new InetSocketAddress(HOST, PORT),
                    1000
            );

            PrintWriter output = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()),
                    true
            );

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            // Convert PureGame -> JSON
            String requestJson =
                    mapper.writeValueAsString(game);

            System.out.println("Sending:");
            System.out.println(requestJson);

            // Send game state
            output.println(requestJson);

            // Receive server response
            String responseJson = input.readLine();

            if (responseJson == null) {
                System.out.println(
                        "External Player: no response from server."
                );
                return null;
            }

            System.out.println("Received:");
            System.out.println(responseJson);

            // Convert JSON -> OpMove
            return mapper.readValue(
                    responseJson,
                    OpMove.class
            );

        } catch (IOException e) {

            System.out.println(
                    "External Player: TetrisServer is unavailable."
            );

            return null;
        }
    }
}