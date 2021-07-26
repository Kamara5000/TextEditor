import javax.swing.*;
//import javax.swing.event.ListSelectionEvent;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.print.PrinterJob;
import java.net.*;
import java.text.SimpleDateFormat;
//import javax.swing.event.CaretListener;
//import javax.swing.event.CaretEvent;

public class TextEditor implements ActionListener, ListSelectionListener, CaretListener  {
    JTextArea area, page;
    //JTextPane page;
    int pageNumber=1;
    String path;
    JFrame frame;
    JTextField findText, replaceText, val1;
    JList myList;
    JLabel statusBar;
    int linenum = 1, word=1, columnnum=1, linenumber, columnnumber;
    String text="", words[];
    JPanel statusPanel;


    public TextEditor(){
        frame = new JFrame();
        frame.setSize(new Dimension(1200,600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(new Point(100,50));
        frame.setTitle("Simple Text Editor");
        frame.setLayout(new BorderLayout());

        

        //create menu bar to palce all menu
         JMenuBar menuContainer = new JMenuBar();

         //create a menu called file
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
         //create menu item in a file called new
         JMenuItem newPage = new JMenuItem("New", 'N');
         //add a control from the keyboard
         newPage.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
         //add actionListener to the item
        //  newPage.addActionListener(this);
        newPage.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                int option = JOptionPane.showConfirmDialog(null, "Do your wish to save?", "warning", JOptionPane.YES_NO_CANCEL_OPTION);
                //option returns an integer, 0 is yes, 1 is no 
                if (option == 0) {
                        if (path == null) {
                            saveAsFile();
                        }else{
                            saveFile();
                        }
                     
                    //area.setText("");
                     //saveAsFile();
                    page.setText("");
                    frame.setTitle("untitled "+ pageNumber+ "- Text Frame");
                    pageNumber ++;
                 }else if (option == 1) {
                     page.setText("");
                     frame.setTitle("untitled "+ pageNumber+ "- Text Frame");
                     pageNumber ++;
                 }else{

                 }
                
            }
        });
         //add the item to a menu
         fileMenu.add(newPage);

         JMenuItem open = new JMenuItem("Open...", 'O');
         //open.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
         open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
         open.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                JFileChooser fopen =  new JFileChooser();
                int option = fopen.showOpenDialog(frame);
                int fileToOpen = option;
                JFileChooser fileOpen =  fopen;
                if (fileToOpen == JFileChooser.APPROVE_OPTION) {
                    page.setText("");
                    try {
                        Scanner scan = new Scanner(new FileReader(fileOpen.getSelectedFile().getPath()));
                        while(scan.hasNext()){
                            page.append(scan.nextLine()+"\n");
                        }
                        
                        frame.setTitle(fileOpen.getSelectedFile().getPath() + "- Editor");
                        path = fileOpen.getSelectedFile().getPath();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                }
                
            }
        });
         fileMenu.add(open);

         JMenuItem save = new JMenuItem("Save", 'S');
         save.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
         save.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                saveFile();
            }        
        });
         fileMenu.add(save);

         JMenuItem saveAs = new JMenuItem("Save as...");
         saveAs.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
               
                     saveAsFile();    
            }
        });;
         fileMenu.add(saveAs);
         //to add a line
         fileMenu.addSeparator();

         JMenuItem pageSetUp = new JMenuItem("Page Set Up...");
         pageSetUp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0 ));
         pageSetUp.addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent ae){
                    PrinterJob pj = PrinterJob.getPrinterJob();
                    pj.pageDialog(pj.defaultPage());
             }
         });
         fileMenu.add(pageSetUp);
        

         JMenuItem print = new JMenuItem("print...", 'P');
         //print.setAccelerator(KeyStroke.getKeyStroke('P', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
         print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.SHIFT_MASK | ActionEvent.ALT_MASK ));
         print.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                   PrinterJob pj = PrinterJob.getPrinterJob();
                  if (pj.printDialog()) {
                      try {
                          pj.print();
                      } catch (Exception e) {
                         System.out.println(e);
                      }
                  }
            }
        });
         fileMenu.add(print);
         fileMenu.addSeparator();

         JMenuItem NewWindow = new JMenuItem("New Window");
         NewWindow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.SHIFT_MASK | ActionEvent.CTRL_MASK));
        NewWindow.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                   new TextArea();
            }
        });
         fileMenu.add(NewWindow);

         JMenuItem exit = new JMenuItem("Exit");
         exit.addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent ae){
                int option = JOptionPane.showConfirmDialog(null, "Do you wish to save", "warning", JOptionPane.YES_NO_CANCEL_OPTION);
                if (option == 0) {
                    if (path == null) {
                        saveAsFile();
                    }else{
                        saveFile();
                    }
                    System.exit(0);
                }else if(option == 1){
                    System.exit(0);                }
             }
         });
         fileMenu.add(exit);

         menuContainer.add(fileMenu);



        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem undo = new JMenuItem("Undo");
        //undo.setAccelerator(KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        undo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                  
            }
        });
        editMenu.add(undo);

        editMenu.addSeparator();

        JMenuItem cut = new JMenuItem("Cut");
        //cut.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        cut.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                   page.cut();
            }
        });
        editMenu.add(cut);

        JMenuItem copy = new JMenuItem("Copy", 'C');
        //copy.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        copy.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                   page.copy();
            }
        });
        editMenu.add(copy);

        JMenuItem paste = new JMenuItem("Paste", 'V');
        // paste.setAccelerator(KeyStroke.getKeyStroke('V', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
        paste.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
               page.paste();
               
            }
        });
        editMenu.add(paste);

        JMenuItem del = new JMenuItem("Delete");
        del.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK));
        del.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
               try {
                String search = page.getSelectedText();
                int n = page.getText().indexOf(search);
                
                page.replaceRange("", n, n+search.length());
               } catch (Exception e) {
                   JOptionPane.showMessageDialog(null,"No text is selected");
               }

            
               
            }
        });
        editMenu.add(del);
        editMenu.addSeparator();
        
        JMenuItem find = new JMenuItem("Find", 'F');
        //find.setAccelerator(KeyStroke.getKeyStroke('F', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        find.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                findText();
            }
        });
        editMenu.add(find);

        JMenuItem findNext = new JMenuItem("Find Next");
        findNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        findNext.addActionListener(this);
        editMenu.add(findNext);

        JMenuItem findWithG = new JMenuItem("Find with Google");
        findWithG.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        findWithG.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
               try {
                  if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(URI.create("www.google.com"));
                  }else{
                    JOptionPane.showMessageDialog(null, "System not supported");
                  }
               } catch (Exception e) {
                  JOptionPane.showMessageDialog(null, "not able to open url");
               }
            }
        });
        editMenu.add(findWithG);

        JMenuItem repl = new JMenuItem("Replace...");
        //repl.setAccelerator(KeyStroke.getKeyStroke('H', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        repl.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
        repl.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                findAndReplace();
            }
        });
        editMenu.add(repl);

        JMenuItem goTo = new JMenuItem("Go To...");
        //goTo.setAccelerator(KeyStroke.getKeyStroke('G', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        goTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));
        goTo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
               try {
                  goToLine();
               } catch (Exception e) {
                  JOptionPane.showMessageDialog(null, "not able to find line");
               }
            }
        });
        editMenu.add(goTo);

        editMenu.addSeparator();

        JMenuItem selAll = new JMenuItem("Select All", 'A');
        //selAll.setAccelerator(KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        selAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        selAll.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                page.selectAll();
            }
        });
        editMenu.add(selAll);

        JMenuItem tD = new JMenuItem("Time/Date");
        tD.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        tD.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                // String date = new Date().toString();
                String date = new SimpleDateFormat("hh:mm:ss yyy/MM/dd ").format(new Date());
                page.setText(date); 
            }
        });
        editMenu.add(tD);

        menuContainer.add(editMenu);

        JMenu formatMenu = new JMenu("Format");
        formatMenu.setMnemonic(KeyEvent.VK_F1);
        
        final JCheckBoxMenuItem wordWrap = new JCheckBoxMenuItem("Word Wrap");
        wordWrap.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                boolean checkState = wordWrap.isSelected();
                if (checkState == true) {
                    page.setLineWrap(true);
                    page.setWrapStyleWord(true);
                    
                } else {
                    page.setLineWrap(false);
                    page.setWrapStyleWord(false);
                }
            }
        });
        formatMenu.add(wordWrap);

        JMenuItem font = new JMenuItem("Font...");
        font.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                //font();

            }
        });
        formatMenu.add(font);

        menuContainer.add(formatMenu);

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        final JCheckBoxMenuItem status = new JCheckBoxMenuItem("Status Bar");
        status.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                boolean checkState = status.isSelected();
                if (checkState) {
                    //give the status a value
                    updateStatus(linenumber, columnnumber, word, text);
                    statusPanel.add(statusBar);
                    statusPanel.repaint();
                } else {
                    statusPanel.remove(statusBar);
                    statusPanel.repaint();
                }
            }
        });
        viewMenu.add(status);

        JMenu zoom = new JMenu("Zoom");
        JMenuItem zoomIn = new JMenuItem("Zoom In");
        //zoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_+, ActionEvent.CTRL_MASK));
        zoomIn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                Font font = page.getFont();

                if (font.getSize() <  200) {
                    page.setFont(new Font(null,0,font.getSize()+10));
                    frame.validate();
                } else {
                    JOptionPane.showMessageDialog(null, "zoom cannot go above maximum zoom level");
                }
            }
        });

        zoom.add(zoomIn);

        JMenuItem zoomOut = new JMenuItem("Zoom out");
        //zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_+, ActionEvent.CTRL_MASK));
        zoomOut.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                Font font = page.getFont();

                if (font.getSize() >= 20) {
                    page.setFont(new Font(null,0,font.getSize()-10));
                    //frame.validate();
                } else {
                    JOptionPane.showMessageDialog(null, "zoom cannot go below minimum zoom level");
                }
            }
        }); 
        zoom.add(zoomOut);

        JMenuItem defaultZoom = new JMenuItem("Restore default zoom");
        //zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_+, ActionEvent.CTRL_MASK));
        defaultZoom.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                page.setFont(new Font(null,0,20));
                frame.validate();
            }
        });

        zoom.add(defaultZoom);
        
        viewMenu.add(status);
        viewMenu.add(zoom);

        menuContainer.add(viewMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem viewHelp = new JMenuItem("View Help");
        viewHelp.addActionListener(this);
        helpMenu.add(viewHelp);
        
        helpMenu.addSeparator();

        JMenuItem about = new JMenuItem("About Editor");
        about.addActionListener(this);
        helpMenu.add(about);

        menuContainer.add(helpMenu);
        
         frame.add(menuContainer, BorderLayout.NORTH);


        //page = new JTextPane();

        page = new JTextArea();
        page.setSize(1000,500);
        page.setFont(new Font("serrif",  Font.PLAIN, 20));
        frame.add(page);
        frame.add(new JScrollPane(page));

        statusBar = new JLabel("Line: "+linenumber+ " Column: " + columnnumber+" Characters: "+text.length()+ " Words: "+word+ "     ");
        statusPanel = new JPanel(new FlowLayout());
        frame.add(statusPanel, BorderLayout.SOUTH);
        page.addCaretListener(new CaretListener(){
            @Override
            public void caretUpdate(CaretEvent ce){
                JTextArea editArea = (JTextArea)ce.getSource();
                try {
                    text = page.getText();
                    words = text.split(" ");
                    word = words.length;
                    int caretpos= editArea.getCaretPosition();
                    linenum = editArea.getLineOfOffset(caretpos);
                    columnnum = caretpos - editArea.getLineStartOffset(linenum);
                    linenum += 1;

                    //this will set the line number
                } catch (Exception e) {
                
                }

                updateStatus(linenum,columnnum,word,text);

            }
        });
        //frame.pack(); - this will  size the frame base on the component on it
        frame.setVisible(true);

        

        // String[] menus = {"File", "Edit", "Format", "View", "Help"};
        // String[][] menuItems = {{"New", "Open", "Save", "Save as", "Page Setup","Print", "Exist"},
        //                         {"Undo","Copy", "Paste", "Delete", "Find","Find Next","Replace", "Go to", "Select All", "Time/Date"},
        //                         {"Word Wrap", "Font"}, {"view"}, {"view help", "About Notepad"}};


        // for (int i = 0; i< menus.length; i++) {
        //     //create a menu called file
        // JMenu fileMenu = new JMenu(menus[i]);
        // //create menu item called new
        // for (int j=0; j<menuItems[i].length; j++ ) {            
        //     JMenuItem newPage = new JMenuItem(menuItems[i][j], 'N');
        //     newPage.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        //     newPage.addActionListener(this);
        //     fileMenu.add(newPage);
        //     menuContainer.add(fileMenu);

        // }

        //}

           




        // //create a menu called file
        // JMenu fileMenu = new JMenu("File");
        //  //create menu item called new
        //  JMenuItem newPage = new JMenuItem("New", 'N');
        //  newPage.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
        //  newPage.addActionListener(this);
        //  fileMenu.add(newPage);
        //  menuContainer.add(fileMenu);
        
    }

    public void actionPerformed(ActionEvent ae){

    }

    public void saveAsFile(){
        JFileChooser fsave = new JFileChooser();
        int option = fsave.showSaveDialog(fsave);
         if (option == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(fsave.getSelectedFile().getPath()));
                out.write(page.getText());
                frame.setTitle(fsave.getSelectedFile().getPath() + "- Editor");
                path = fsave.getSelectedFile().getPath();
                out.close();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
         }

    }

    public void saveFile(){
        //JFileChooser fsave = new JFileChooser();
        //String myPath = fsave.getSelectedFile().getPath();

        File myFile = new File(path);

        if (myFile.exists()) {
            
             try {
                BufferedWriter out = new BufferedWriter(new FileWriter(myFile.getPath()));
                out.write(page.getText());
                out.close();
            } catch (Exception ex) {
                //TODO: handle exception\
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
        
    }

    public void findText(){
        JDialog findR = new JDialog();
        findR.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        findR.setLayout(new GridLayout(2,1));
        findR.setSize(400, 150);
        findR.setLocation(500,300);
        findText =  new JTextField("Enter the text to find",20);
        findR.setTitle("Find");
        final JButton find = new JButton("Find");
        find.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                try {
                    String search = findText.getText();
                    int n = page.getText().indexOf(search);
                    page.select(n, n+search.length());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"No text is selected");
                }
            }
        });

        

        JPanel panel = new JPanel();
       
        panel.add(find);
        findR.add(findText);
        findR.add(panel);
        findR.setVisible(true);


    } 

    public void findAndReplace(){
        JDialog findR = new JDialog();
        findR.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        findR.setLayout(new GridLayout(3,1));
        findR.setSize(400, 150);
        findR.setLocation(500,300);
        replaceText = new JTextField("Enter  the text to replace",20);
        findText =  new JTextField("Enter the text to find",20);
        findR.setTitle("Find and Replace");
        final JButton find = new JButton("Find");
        find.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                try {
                    String search = findText.getText();
                    int n = page.getText().indexOf(search);
                    page.select(n, n+search.length());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"No text is selected");
                }
            }
        });

        final JButton replace = new JButton("Replace");
        replace.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                try {
                    String search = findText.getText();
                    int n = page.getText().indexOf(search);
                    String replacing = replaceText.getText();
                    page.replaceRange(replacing, n, n+search.length());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"No text is selected");
                }
            }
        });

        JPanel panel = new JPanel();
        findR.add(findText);
        findR.add(replaceText);
        panel.add(find);
        panel.add(replace);
        findR.add(panel);
        findR.setVisible(true);


    }

    public void goToLine(){
        final JDialog go2 = new JDialog(frame);
        go2.setSize(200,100);
        go2.setLocation(200,300);
        JPanel pa = new JPanel(new BorderLayout());
        JLabel lb  = new JLabel("Enter line number");
        pa.add(lb, BorderLayout.NORTH);
        final JTextField tf = new JTextField(10);
        pa.add(tf, BorderLayout.CENTER);
        JButton bn = new JButton("Go to line");
        bn.setSize(5,5);
        bn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                try {
                    String line = tf.getText();
                    page.setCaretPosition(page.getDocument().getDefaultRootElement().getElement(Integer.parseInt(line)-1).getStartOffset());
                    page.requestFocusInWindow();
                    go2.dispose();
                } catch (Exception e) {
                   JOptionPane.showMessageDialog(null,"cannot find line");
                }
            }
        });
        pa.add(bn , BorderLayout.SOUTH);
        go2.add(pa);
        go2.setVisible(true);
    }

    // public void font(){
    //     JDialog fnt = new JDialog();
    //             fnt.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    //             fnt.setLayout(new GridLayout(2,1));
    //             fnt.setSize(400, 150);
    //             fnt.setLocation(500,300);
    //             String listItems[]= {"name1", "name2", "name3","name4","name5"};
    //             myList = new JList(listItems);
    //             myList.setSelectedIndex(2);
    //             myList.addListSelectionListener(this);
    //             JScrollPane scroll = new JScrollPane(myList);
                
    //             JPanel panel = new JPanel();
    //             JTextField val1 = new JTextField();
    //             panel.add(val1);
    //             fnt.add(panel);
    //             fnt.add(scroll);
    //             ///fnt.add(panel);
    //             fnt.setVisible(true);

    //             // JPanel panel = new JPanel(new BorderLayout());
    //             // List<String> labels= new ArrayList<>(25);
    //             // for (int index =0; index < 100; index++){
    //             //     labels.add("Item " + index);
    //             // }

    //             // final JList<String> listArea = new JList<>(labels.toArray(new String));
    //             // listArea.setSelectionModel(ListSelectionModel.SINGLE_SELECTION);
    //             // listArea.setFont(new Font )
    // }

    
    public void valueChanged(ListSelectionEvent lsv){
        JOptionPane.showMessageDialog(null, myList.getSelectedValue());
        val1.setText(myList.getSelectedValue().toString());
        
    }

    @Override
    public void caretUpdate(CaretEvent e){
        throw new UnsupportedOperationException("Not supported yet");
    }
    
    private void updateStatus(int linenumber, int columnnumber, int word, String text){
        //create and add a status bar here
        statusBar.setText("Line: "+ linenumber+ " Colomn: "+ columnnumber + " Characters: "+text.length() + " Words: "+word);
    }
    
    public static void main(String[] args) {
       TextEditor tE = new TextEditor(); 
    }
}
