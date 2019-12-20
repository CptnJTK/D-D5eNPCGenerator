package NPCGenerator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class MainView extends JPanel {
    public static DefaultListModel<NPC> savedNPCs = new DefaultListModel<NPC>();
    
    private final JButton newButton = new JButton("New NPC");
    private final JButton saveButton = new JButton("Save NPC");
    private final JButton loadButton = new JButton("Load NPC");
    private final JButton deleteButton = new JButton("Delete NPC");
    
    private NPC currentNPC = new NPC();
    
    Font bold = new Font("Arial", Font.BOLD, 16);
    Font normal = new Font("Arial", Font.PLAIN, 14);
    private final JLabel nameLabel = new JLabel(currentNPC.getName());
    private final JLabel infoLabel = new JLabel(currentNPC.getInfo());
    
    private final JList<NPC> savedList = new JList<NPC>(savedNPCs);
    
    public MainView() {
        this.layoutView();
        this.registerListeners();
    }

    private void layoutView() {
        this.setLayout(new BorderLayout());
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,4));
        buttons.add(newButton);
        buttons.add(saveButton);
        buttons.add(deleteButton);
        buttons.add(loadButton);
        infoLabel.setFont(normal);
        this.add(buttons, BorderLayout.NORTH);
        this.add(infoLabel, BorderLayout.CENTER);
        savedList.setCellRenderer(new ListCellRenderer());
        savedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.add(new JScrollPane(savedList), BorderLayout.EAST);
    }
    
    private void registerListeners() {
        this.newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                if (!currentNPC.isSaved()) {
                    Object response = JOptionPane.showOptionDialog(null,
                            "The current NPC is not saved, are you sure you want to continue?",
                            "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
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
                            "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
                    if (response.equals(1)) {
                        return;
                    }
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
                                "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
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
                if(currentNPC.isSaved()) {
                    Object response = JOptionPane.showOptionDialog(null,
                            "The current NPC is already saved, do you wish to save a copy?",
                            "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
                    if (response.equals(1)) {
                        return;
                    }
                }
                savedNPCs.addElement(currentNPC.copy());
                currentNPC.save();
            }
        });
        this.currentNPC.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                MainView.this.nameLabel.setText(currentNPC.getName());
                MainView.this.infoLabel.setText(currentNPC.getInfo());
            }
        });
        this.savedList.addListSelectionListener( new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent e) {
                
            }
            
        });
    }
    public class ListCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel)super.getListCellRendererComponent(list,  value,  index,  isSelected,  cellHasFocus);
            label.setText(value.toString());
            return label;
        }
    }
    public static void save(BufferedWriter out) throws IOException {
        if (savedNPCs.getSize() > 0) {
            String outLine = "";
            for(int i = 0; i < savedNPCs.getSize(); i++) {
                outLine += savedNPCs.getElementAt(i).getName()+'|'+savedNPCs.getElementAt(0).getRawInfo()+'|';
                out.write(outLine);
                if (i < savedNPCs.getSize()-1) {
                    out.write(System.lineSeparator());
                }
            }
            
        }
    }
    public static void load(BufferedReader in) throws IOException {
        String currentLine;
        String totalLine = "";
        while((currentLine = in.readLine())!=null) {
            totalLine += currentLine;
        }
        StringTokenizer st = new StringTokenizer(totalLine, "|");
        while (st.hasMoreElements()) {
            savedNPCs.addElement(new NPC((String)st.nextElement(), (String)st.nextElement()));
        }
    }
}
