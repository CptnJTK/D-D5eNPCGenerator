package NPCGenerator;

import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.awt.event.WindowAdapter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] args) {
        // Instantiate input reader
        BufferedReader in = null;
        final MainView view = new MainView();
        Charset charset = Charset.forName("US-ASCII");
        File saveFile = new File("save_data.txt");

        // Attempt to populate MainView with saved data
        try {
            in = Files.newBufferedReader(saveFile.toPath(), charset);
            MainView.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Instantiate window
        final JFrame f = new JFrame("5e NPC Generator");
        f.setContentPane(view);
        f.setSize(900, 400);
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        f.setLocationRelativeTo(null);

        // Window close safety check
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                JFrame frame = (JFrame) we.getSource();

                int result = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to exit the application? Your NPCs will be saved.",
                        "Exit Application",

                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) { // Save and exit program
                    try {
                        BufferedWriter out = null;
                        out = Files.newBufferedWriter(saveFile.toPath(), charset);
                        MainView.save(out);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }
        });
        f.setVisible(true);
    }
}
