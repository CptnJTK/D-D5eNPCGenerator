package NPCGenerator;

import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class Main extends JFrame implements KeyListener{
	final MainView view;

	public Main(String windowTitle) {
		this.setTitle(windowTitle);
		this.view = new MainView();
		addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
	}
	@Override
	public void keyReleased(KeyEvent e) {
		if (this.view.savedList.getSelectedValue() != null) {
			if (e.getKeyCode() == KeyEvent.VK_DELETE) {
				Object response = JOptionPane.showOptionDialog(null,
	                    "The selected NPC will be deleted, are you sure you want to continue?",
	                    "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
	                    null, null);
	            if (response.equals(1)) {
	                return;
	            }
	            MainView.savedNPCs.getElementAt(this.view.savedList.getSelectedIndex()).delete();
	            MainView.savedNPCs.removeElementAt(this.view.savedList.getSelectedIndex());
	            this.view.savedList.setSelectedIndex((this.view.savedList.getSelectedIndex()-1==-1?null:this.view.savedList.getSelectedIndex()-1));
			}
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			if (this.view.savedList.getSelectedValue() != null) {
				this.view.savedList.setSelectedIndex(this.view.savedList.getSelectedIndex()-1);
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			if (this.view.savedList.getSelectedValue() != null) {
				this.view.savedList.setSelectedIndex(this.view.savedList.getSelectedIndex()+1);
			}
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			if (this.view.savedList.getSelectedValue() != null) {
                if (!this.view.currentNPC.isSaved()) {
                    Object response = JOptionPane.showOptionDialog(null,
                            "The current NPC is not saved, are you sure you want to continue?",
                            "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, null, null);
                    if (response.equals(1)) {
                        return;
                    }
                }
                this.view.currentNPC.load(this.view.savedList.getSelectedValue());
            }
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (this.view.currentNPC.isSaved()) {
                Object response = JOptionPane.showOptionDialog(null,
                        "The current NPC is already saved, do you wish to save a copy?",
                        "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                        null, null);
                if (response.equals(1)) {
                    return;
                }
            }
            MainView.savedNPCs.addElement(this.view.currentNPC.copy());
            this.view.currentNPC.save();
            this.view.savedList.setSelectedIndex(this.view.savedList.getFirstVisibleIndex());
		} else if (e.getKeyCode() == KeyEvent.VK_N) {
			if (!this.view.currentNPC.isSaved()) {
                Object response = JOptionPane.showOptionDialog(null,
                        "The current NPC is not saved, are you sure you want to continue?",
                        "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                        null, null);
                if (response.equals(1))
                    return;
            }
            this.view.currentNPC.regenerate();
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        // Instantiate input reader
    	FileInputStream inFile = null;
    	ObjectInputStream in = null;
        // Attempt to populate MainView with saved data
        try {
        	inFile = new FileInputStream("SaveData.sav");
        	in = new ObjectInputStream(inFile);
            MainView.load(in);
            in.close();
        } catch(FileNotFoundException e) {
        	FileOutputStream outFile = new FileOutputStream("SaveData.sav");
        	ObjectOutputStream out = new ObjectOutputStream(outFile);
        	out.writeInt(0);
        	out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Instantiate window
        final Main f = new Main("5e NPC Generator");
        f.setContentPane(f.view);
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
                        FileOutputStream out = null;
                        out = new FileOutputStream("SaveData.sav");
                        MainView.save(new ObjectOutputStream(out));
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
