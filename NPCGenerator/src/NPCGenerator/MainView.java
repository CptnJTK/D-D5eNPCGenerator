package NPCGenerator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class MainView extends JPanel{
    // Current model data
    NPC currentNPC = new NPC();

    // Model data storage
    public static DefaultListModel<NPC> savedNPCs = new DefaultListModel<NPC>();
    final JList<NPC> savedList = new JList<NPC>(savedNPCs);

    // Buttons
    private final JButton newButton = new JButton("New NPC");
    private final JButton saveButton = new JButton("Save NPC");
    private final JButton loadButton = new JButton("Load NPC");
    private final JButton deleteButton = new JButton("Delete NPC");

    // Labels
    Font normal = new Font("Arial", Font.PLAIN, 16);
    // Font bold = new Font("Arial", Font.BOLD, 16); UNUSED_FONT_BOLD
    // private final JLabel nameLabel = new JLabel(currentNPC.getName()); UNUSED_LABEL_NAME
    JTextArea ta = new JTextArea();

    // Constructor
    public MainView() {
        this.layoutView();
        this.registerListeners();
    }

    // Build layout
    private void layoutView() {
        this.setLayout(new BorderLayout());
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 4));
        buttons.add(newButton);
        buttons.add(saveButton);
        buttons.add(deleteButton);
        buttons.add(loadButton);
        this.add(buttons, BorderLayout.NORTH);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setOpaque(false);
        ta.setWrapStyleWord(true);
        ta.setFont(normal);
        ta.setText(currentNPC.getRawInfo());
        this.add(ta, BorderLayout.CENTER);
        savedList.setCellRenderer(new ListCellRenderer());
        savedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.add(new JScrollPane(savedList), BorderLayout.EAST);
    }

    // Instantiate listeners
    private void registerListeners() {
        // Button listeners
        this.newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                if (!currentNPC.isSaved()) {
                    Object response = JOptionPane.showOptionDialog(null,
                            "The current NPC is not saved, are you sure you want to continue?",
                            "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                            null, null);
                    if (response.equals(1))
                        return;
                }
                currentNPC.regenerate();
            }
        });
        this.deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                if (MainView.this.savedList.getSelectedValue() != null) {
                    Object response = JOptionPane.showOptionDialog(null,
                            "The selected NPC will be deleted, are you sure you want to continue?",
                            "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                            null, null);
                    if (response.equals(1)) {
                        return;
                    }
                    savedNPCs.getElementAt(savedList.getSelectedIndex()).delete();
                    savedNPCs.removeElementAt(savedList.getSelectedIndex());
                }
            }
        });
        this.loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                if (MainView.this.savedList.getSelectedValue() != null) {
                    if (!currentNPC.isSaved()) {
                        Object response = JOptionPane.showOptionDialog(null,
                                "The current NPC is not saved, are you sure you want to continue?",
                                "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                                null, null, null);
                        if (response.equals(1)) {
                            return;
                        }
                    }
                    currentNPC.load(MainView.this.savedList.getSelectedValue());
                }
            }
        });
        this.saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                if (currentNPC.isSaved()) {
                    Object response = JOptionPane.showOptionDialog(null,
                            "The current NPC is already saved, do you wish to save a copy?",
                            "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                            null, null);
                    if (response.equals(1)) {
                        return;
                    }
                }
                savedNPCs.addElement(currentNPC.copy());
                currentNPC.save();
                MainView.this.savedList.setSelectedIndex(MainView.this.savedList.getFirstVisibleIndex());
            }
        });

        // Property change listeners
        this.currentNPC.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                // MainView.this.nameLabel.setText(currentNPC.getName()); UNUSED_LABEL_NAME
                MainView.this.ta.setText(currentNPC.getInfo());
            }
        });

        // List listeners
        this.savedList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

            }

        });
    }
	
    // Sets list cell label to string representation of object
    public class ListCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);
            label.setText(value.toString());
            return label;
        }
    }

    // Saves all cached objects to file
    // Run automatically upon close
    public static void save(ObjectOutputStream out) throws IOException {
    	out.writeInt(savedNPCs.getSize());
        for (int i = 0; i < savedNPCs.getSize(); i++) {
            out.writeObject(savedNPCs.elementAt(i).getName());
            out.writeObject(savedNPCs.elementAt(i).getRawInfo());
        }

    }

    // Loads all saved objects from file
    // Run automatically upon start
    public static void load(ObjectInputStream in) throws IOException, ClassNotFoundException {
    	try {
	        int size = in.readInt();
	        for(int i = 0; i < size; i++) {
	            savedNPCs.addElement(new NPC((String) in.readObject(), (String) in.readObject()));
	        }
    	} catch (EOFException e) {
    		FileOutputStream outFile = new FileOutputStream("SaveData.sav");
        	ObjectOutputStream out = new ObjectOutputStream(outFile);
        	out.writeInt(0);
        	out.close();
    	}
    }
}
