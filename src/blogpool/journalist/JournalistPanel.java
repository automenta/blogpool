/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogpool.journalist;

import blogpool.source.WordpressSource;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import redstone.xmlrpc.XmlRpcFault;

/**
 *
 * @author seh
 */
public class JournalistPanel extends JPanel {
    final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static final Logger logger = Logger.getLogger(JournalistPanel.class.getSimpleName());
    private final SourcesPanel sourcesPanel;
    private final PostEditorPanel postEditor;
    private final History history;
    private final WordpressSource blog;

    public static void doubleSize(JComponent c) {
        Font f = c.getFont();
        c.setFont(f.deriveFont((float)f.getSize()*2.0f));
    }
    
    public class PostEditorPanel extends JPanel {

        List<Content> elements = new LinkedList();
        private final JPanel list;
        private final JTextField titleField;

        public PostEditorPanel(History history) {
            super(new BorderLayout());

            list = new JPanel();
            list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
            add(new JScrollPane(list), BorderLayout.CENTER);

            JPanel topPanel = new JPanel(new BorderLayout());
            {
                titleField = new JTextField("");
                doubleSize(titleField);
                topPanel.add(titleField, BorderLayout.NORTH);
            }
            add(topPanel, BorderLayout.NORTH);
            
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            {
                JButton preview = new JButton("Preview");
                bottomPanel.add(preview);

                JButton post = new JButton("Post");
                post.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            blog.newPost(getTitle(), getBody() /*, getTags()*/);
                            clear();
                        } catch (XmlRpcFault ex) {
                            logger.severe(ex.toString());
                        }
                    }                    
                });
                bottomPanel.add(post);
            }
            add(bottomPanel, BorderLayout.SOUTH);

            refresh();
        }

        public String getTitle() { return titleField.getText(); }
        public void setTitle(String t) { titleField.setText(t); }
        
        public String getBody() {
            StringBuilder sb = new StringBuilder();
            for (Content c : elements) {
                sb.append(c.toHTML());
            }
            return sb.toString();
        }
        
        protected void edit(final Content c) {
            final JDialog d = new JDialog((Frame)null, true);
            d.setTitle("Editing: " + c.toString());
            
            d.setSize(500, 400);
            
            final JTextArea srcTxt;
            
            JPanel p = new JPanel(new BorderLayout());
            {
                JPanel s = new JPanel(new BorderLayout());
                
                srcTxt = new JTextArea(c.getSource());
                srcTxt.setLineWrap(true);
                srcTxt.setWrapStyleWord(true);
                
                s.add(new JScrollPane(srcTxt), BorderLayout.CENTER);
                
                p.add(s, BorderLayout.CENTER);
            }
            {
             
                JPanel b = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                
                JButton cancelButton = new JButton("Cancel");
                JButton saveButton = new JButton("Save");
                
                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        c.setSource(srcTxt.getText());
                        d.hide();
                    }
                });
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        d.hide();
                    }
                });
                
                b.add(cancelButton);                
                b.add(saveButton);
                
                p.add(b, BorderLayout.SOUTH);
            }            
            
            d.getContentPane().add(p);
            d.setVisible(true);
        }
        
        protected JPanel newElementPanel(final Content c) {
            final JPanel p = new JPanel(new BorderLayout());
            p.setBorder(new EmptyBorder(5,5,5,5));

            JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
            {
                JButton closeButton = new JButton("X");

                closeButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        p.setVisible(false);
                        elements.remove(c);
                    }
                });

                header.add(new JLabel(c.getClass().getSimpleName()), BorderLayout.NORTH);
 
                JButton editButton = new JButton("Edit");
                editButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        edit(c);
                    }                    
                });
                
                header.add(editButton);
                
                header.add(closeButton);
                
                header.add(new JButton("(^)"));
                
                header.add(new JButton("(v)"));
                
            }
            p.add(header, BorderLayout.NORTH);

//            final JTextArea text = new JTextArea(c.getSource());
//            text.setWrapStyleWord(true);
//            text.setLineWrap(true);
//            p.add(new JScrollPane(text), BorderLayout.CENTER);
//
//

            //new JButton(c.toString()));

            return p;
        }

        protected void refresh() {
            list.removeAll();
            for (Content c : elements) {
                list.add(newElementPanel(c));
            }
            list.add(Box.createVerticalBox());
            list.updateUI();
        }

        public void clear() {
            elements.clear();
            titleField.setText("");
            refresh();
        }

        private void addContent(Content c) {
            elements.add(c);
            refresh();
        }
    }

    public String noLongerThan(String s, int numChars) {
        if (s.length() < numChars) return s;
        return s.substring(0,numChars-2) + "..";
    }
    
    protected void refresh() {
        sourcesPanel.sourceContents.empty();
        new Thread(new Journalist.RefreshSources(history, new Runnable() {

            @Override
            public void run() {
                sourcesPanel.sourceContents.refresh();
            }
        })).start();
    }

    protected void addContent(ContentSource cs, Content c) {
        logger.info("Add content " + c + " from " + cs);
        postEditor.addContent(c);
        
        if (postEditor.getTitle().length()==0) {
            postEditor.setTitle(cs.getTitle());
        }
    }

    public class SourceContentsPanel extends JPanel {

        public SourceContentsPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            empty();
        }

        public void refresh() {
            removeAll();
            //TODO sort these in different ways
            for (final ContentSource cs : history.content.values()) {

                final JPanel jp = new JPanel(new BorderLayout());
                //float h = (cs.getTitle().hashCode() % 5000.0F) / 5000.0F;
                float h = ((float)Math.sin(cs.getTitle().hashCode()/2.0f))+1.0f * 0.05f + 0.1f;
                jp.setBackground(Color.getHSBColor(h, 0.1F, 0.95F));
                jp.setBorder(new EmptyBorder(5, 5, 5, 5));

                final JPanel c = new JPanel();
                c.setVisible(false);

                final JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                topPanel.setOpaque(false);
                {
                    
                    final JButton expandButton = new JButton("->");
                    expandButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            c.setVisible(!c.isVisible());
                        }                        
                    });
                    
                    JButton allButton = new JButton(cs.getTitle());
                    allButton.setToolTipText("Add all contained content");
                    allButton.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                           int n = JOptionPane.showConfirmDialog(
                                        topPanel,
                                        "Add all " + cs.content.size() + " items?",
                                        "Add all?",
                                        JOptionPane.YES_NO_OPTION);
                           if (n == JOptionPane.YES_OPTION) {

                                for (Content x : cs.content) {
                                    addContent(cs, x);
                                }
                           }
                        }
                    });
                    
                    
                    jp.add(topPanel, BorderLayout.NORTH);

                    JButton discardButton = new JButton("(Remove)");
                    discardButton.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            logger.info("Discard " + cs);
                            //TODO animate fadeout; instantaneous removal can be disorienting
                            jp.setVisible(false);
                        }
                    });
                    topPanel.add(expandButton);
                    topPanel.add(allButton);
                    topPanel.add(discardButton, BorderLayout.SOUTH);
                }
                
                //for each piece of content...
                {
                    c.setBorder(new EmptyBorder(5, 18, 18, 5));
                    c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
                    for (final Content x : cs.content) {
                        JButton b = new JButton(x.getClass().getSimpleName());
                        b.setHorizontalTextPosition(SwingConstants.LEFT);                        
                        
                        b.setToolTipText("<html>" + x.getSource() + "</html>");
                        b.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                addContent(cs, x);
                            }
                        });
                        if (x instanceof Content.Text) {
                            String t = x.getSource();
                            b.setText("<html>Text (" + t.length() + " chars)<br/>" + noLongerThan(t, 80) + "</html>");
                        }
                        if (x instanceof Content.Image) {
                            String u = ((Content.Image)x).url;
                            
                            ImageIcon ii = history.getImage(u);
                            if (ii!=null) {
                                if ((ii.getIconWidth() < 2) || (ii.getIconHeight() < 2)) {
                                    b.setVisible(false);
                                }
                                else {
                                    b.setIcon(ii);
                                }
                            }
                        }
                        c.add(b);
                    }
                    c.setOpaque(false);
                }


                jp.add(c, BorderLayout.CENTER);

                add(jp);


            }
            add(Box.createVerticalBox());
            add(Box.createVerticalGlue());
            updateUI();
        }

        public void empty() {
            removeAll();
            add(new JLabel("Updating..."));
            updateUI();
        }
    }

    public class SourcesPanel extends JPanel {

        public final SourceContentsPanel sourceContents;

        public SourcesPanel(final History history, PostEditorPanel jse) {
            super(new BorderLayout());

            sourceContents = new SourceContentsPanel();
            add(new JScrollPane(sourceContents), BorderLayout.CENTER);
            JMenuBar jm = new JMenuBar();
            {
                final JButton ref = new JButton("Refresh");
                ref.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        refresh();
                    }
                });
                jm.add(ref);

                final JButton clr = new JButton("More");
                clr.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        
                    }
                });
                jm.add(clr);
                
                JMenu addM = new JMenu("Add...");
                {
                    JMenuItem r = new JMenuItem("RSS/Atom Feed");
                    r.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String s = (String) JOptionPane.showInputDialog(SourcesPanel.this, "Enter URL:\n", null);
                            if ((s != null) && (s.length() > 0)) {
                                history.rssFeeds.add(s);
                            }
                        }
                    });
                    addM.add(r);
                }
                jm.add(addM);
            }
            add(jm, BorderLayout.NORTH);
        }
    }

    public JournalistPanel(History history, WordpressSource blog) {
        super(new BorderLayout());

        this.blog = blog;
        this.history = history;

        Border b = new EmptyBorder(5,5,5,5);
        
        postEditor = new PostEditorPanel(history);
        postEditor.setBorder(b);
        sourcesPanel = new SourcesPanel(history, postEditor);
        sourcesPanel.setBorder(b);
        
        JSplitPane js = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        add(js, BorderLayout.CENTER);
        js.setLeftComponent(sourcesPanel);
        js.setRightComponent(postEditor);
        updateUI();
        js.setDividerLocation(0.5);
        refresh();
    }
}
