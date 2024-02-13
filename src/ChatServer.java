import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class ChatServer {

    private static final int PORT = 9001;
    private static HashSet<String> names = new HashSet<String>();
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    public static void main(String[] args) throws Exception {

        System.out.println("The chat server is running...");
        ServerSocket ss = new ServerSocket(PORT);

        try {
            Socket s = ss.accept();
            Thread handlerThread = new Thread(new Handler(s));
            handlerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ss.close();
        }

    }

    private static class Handler implements Runnable {

        private String name;
        private BufferedReader in;
        private PrintWriter out;
        private Socket s;

        public Handler(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {

            try {

                // obtaining input and output streams
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new PrintWriter(s.getOutputStream(), true);

                while (true) {
                    out.println("SUBMIT NAME : ");
                    name = in.readLine();

                    // check name if exist or null
                    if (name == null) {
                        return;
                    } else if (!names.contains(name)){
                        names.add(name);
                        break;
                    }
                }

                out.println("NAME ACCEPTED");
                writers.add(out);

                while (true) {

                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + name + ": " + input);
                    }

                }

            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (names != null) {
                    names.remove(name);
                }
                if (writers != null) {
                    writers.remove(out);
                }
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}


