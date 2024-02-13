import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient implements Runnable {

    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter App");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(10,40);

    public ChatClient() {

        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });

    }

    private String getServerAddress() {
        return JOptionPane.showInputDialog(
                frame,
                "Enter the IP address of the server : ",
                "Welcome to Chatter App by Shaleel",
                JOptionPane.QUESTION_MESSAGE
        );
    }

    private String getName() {
        return JOptionPane.showInputDialog(
                frame,
                "Choose a screen name : ",
                "Screen name selection",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    public void run() {

        try {
            String serverAddress = getServerAddress();
            Socket s = new Socket(serverAddress, 9001);

            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);

            while (true) {
                String line = in.readLine();

                if (line.startsWith("SUBMIT NAME")) {
                    out.println(getName());
                } else if (line.startsWith("NAME ACCEPTED")) {
                    textField.setEditable(true);
                } else if (line.startsWith("MESSAGE")) {
                    messageArea.append(line.substring(8) + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {

        ChatClient chatClient = new ChatClient();
        chatClient.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatClient.frame.setVisible(true);

        Thread client = new Thread(chatClient);

        client.start();

    }

}
