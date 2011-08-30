/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogpool.journalist;

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

    public static void main(String[] args) {
        JournalistPanel jp = new JournalistPanel(new History());

        JFrame jf = new JFrame("News Bot");
        jf.setSize(800, 800);
        jf.getContentPane().add(jp);
        jf.setVisible(true);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
