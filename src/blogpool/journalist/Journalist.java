/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogpool.journalist;

import blogpool.Blogpool;
import blogpool.source.WordpressSource;
import flexjson.JSONDeserializer;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author seh
 */
public class Journalist {

    public static class RefreshSources implements Runnable {

        private final Runnable whenFinished;
        private final History history;

        public RefreshSources(History h) {
            this(h, null);
        }

        public RefreshSources(History h, Runnable whenFinished) {
            this.whenFinished = whenFinished;
            this.history = h;
        }

        @Override
        public void run() {
            history.refresh();

            if (whenFinished != null) {
                whenFinished.run();
            }
        }
    }

    final static String historyFile = "/home/me/history.json";
    
    public static void main(String[] args) {
        try {
            History history;
            
            WordpressSource wp = new WordpressSource(new Blogpool(), "/home/me/blogpool.cfg");            

            try {
                history = new JSONDeserializer<History>().deserialize(new FileReader(new File(historyFile)), History.class);
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Creating new history at " + historyFile);
                history = new History();
            }
            

            JournalistPanel jp = new JournalistPanel(history, wp);

            JFrame jf = new JFrame("News Bot");
            jf.setSize(800, 800);
            jf.getContentPane().add(jp);
            jf.setVisible(true);

            jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            final History h = history;
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        h.save(historyFile);
                    } catch (IOException ex) {
                        Logger.getLogger(Journalist.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
            }));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
