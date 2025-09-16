import java.io.*;
import java.net.*;
import java.util.Random;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private ClientHandler opponent;
    private Fighter fighter;
    private volatile boolean connected = true; // indica si el jugador sigue en juego

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.fighter = new Fighter("Anon", 100);
    }

    public void setOpponent(ClientHandler opp) {
        this.opponent = opp;
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public boolean isConnected() {
        return connected && !socket.isClosed();
    }

    public Fighter getFighter() {
        return fighter;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Recibido: " + line);
                ClientHandler opponent1 = opponent; // copia local para evitar race conditions

                if (line.startsWith("NAME:")) {
                    String playerName = line.substring(5);
                    fighter = new Fighter(playerName, 100);
                    sendMessage("WELCOME " + fighter.getName());

                } else if (line.equalsIgnoreCase("ATTACK")) {
                    if (opponent1 != null && opponent1.isConnected()) {
                        synchronized (opponent1) {
                            int damage = 10 + new Random().nextInt(11); // daño entre 10 y 20
                            opponent1.fighter.takeDamage(damage, fighter.getName());

                            opponent1.sendMessage("DAMAGE:" + damage + " de " + fighter.getName());
                            sendMessage("ATACASTE a " + opponent1.fighter.getName() + " por " + damage);

                            if (!opponent1.fighter.isAlive()) {
                                sendMessage("YOU_WIN");
                                opponent1.sendMessage("YOU_LOSE");
                            }
                        }
                    } else {
                        sendMessage("No tienes un oponente disponible.");
                    }

                } else if (line.equalsIgnoreCase("STATUS")) {
                    sendMessage("HP:" + fighter.getHp());

                } else if (line.equalsIgnoreCase("EXIT")) {
                    sendMessage("Saliendo del juego...");
                    connected = false;
                    if (opponent1 != null && opponent1.isConnected()) {
                        opponent1.sendMessage("Tu oponente se ha desconectado. Ganaste por abandono.");
                    }
                    break;

                } else {
                    sendMessage("UNKNOWN_CMD");
                }
            }
        } catch (IOException e) {
            System.out.println("Error en handler: " + e.getMessage());
        } finally {
            connected = false;
            try { socket.close(); } catch (IOException ignored) {}
            System.out.println("Jugador desconectado: " + fighter.getName());
        }
    }
}
