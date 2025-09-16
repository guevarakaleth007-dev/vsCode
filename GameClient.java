import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GameClient {
    public static void main(String[] args) {
        String host = "localhost"; // cambiar si el server está en otra máquina
        int port = 5000;

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            // Hilo lector de mensajes del servidor
            Thread reader = new Thread(() -> {
                try {
                    String s;
                    while ((s = in.readLine()) != null) {
                        System.out.println("[SERVER] " + s);
                    }
                } catch (IOException e) {
                    System.out.println("Conexión cerrada por el servidor.");
                }
            });
            reader.start();

            // Nombre del jugador
            System.out.print("Tu nombre: ");
            String name = sc.nextLine();
            out.println("NAME:" + name);

            // Bucle principal de comandos
            while (true) {
                System.out.print("Comando (ATTACK/STATUS/EXIT): ");
                String cmd = sc.nextLine().trim();

                if (cmd.equalsIgnoreCase("EXIT")) {
                    out.println("EXIT");  // 🔑 AVISAMOS AL SERVIDOR
                    break;                // Luego cerramos el cliente
                }

                out.println(cmd);
            }

            System.out.println("Saliendo del juego...");

        } catch (IOException e) {
            System.out.println("Error en cliente: " + e.getMessage());
        }
    }
}
 

