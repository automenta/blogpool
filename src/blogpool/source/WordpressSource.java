/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogpool.source;

import blogpool.Blogpool;
import blogpool.Content;
import blogpool.Source;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.logging.Logger;
import net.bican.wordpress.Comment;
import net.bican.wordpress.Page;
import net.bican.wordpress.Wordpress;
import redstone.xmlrpc.XmlRpcFault;

/**
 *
 * @author seh
 */
public class WordpressSource extends Source {
    //http://wordpress-java.googlecode.com/svn-history/r70/trunk/doc/index.html
    
    private static final Logger logger = Logger.getLogger(WordpressSource.class.toString());
    private final Wordpress wordpress;

    public WordpressSource(Blogpool pool, String cfgPath) throws IOException, MalformedURLException, XmlRpcFault {
        super(pool);
        
        FileInputStream fstream = new FileInputStream(cfgPath);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String host = br.readLine().trim();
        String user = br.readLine().trim();
        String pw = br.readLine().trim();
        br.close();
        this.wordpress = new Wordpress(user, pw, host);
        //System.out.println("Page List:");
        //System.out.println(wordpress.getPageList());
        //System.out.println(wordpress.getRecentPosts(10));
        //            for (Page p : wordpress.getRecentPosts(10)) {
        //                System.out.println(p);
        //                int id = p.getPostid();
        //                String content = "Noticed by " + getClass().getSimpleName();
        //                wordpress.newComment(id, -1, content, null, null, null);
        //            }
    }

    public void refresh() {
        final int maxPosts = 128;
        try {
            for (final Page p : wordpress.getRecentPosts(maxPosts)) {
                final Content postContent = pool.addContent(new Content() {

                    @Override
                    public String getURL() {
                        return p.getLink();
                    }

                    @Override
                    public String getTitle() {
                        return p.getTitle();
                    }

                    @Override
                    public String getContent() {
                        return p.getDescription();
                    }

                    @Override
                    public Content getParent() {
                        return null;
                    }

                    @Override
                    public void comment(String content) {
                        try {
                            newComment(p.getPostid(), content);
                        } catch (XmlRpcFault ex) {
                            logger.severe("Unable to comment on " + p + ": " + ex.toString());
                        }
                    }
                });
                int numComments = wordpress.getCommentsCount(p.getPostid()).getApproved();
                for (final Comment c : wordpress.getComments("approve", p.getPostid(), numComments, 0)) {
                    pool.addContent(new Content() {

                        @Override
                        public String getURL() {
                            return c.getLink();
                        }

                        @Override
                        public String getTitle() {
                            return null;
                        }

                        @Override
                        public String getContent() {
                            return c.getContent();
                        }

                        @Override
                        public Content getParent() {
                            return postContent;
                        }

                        @Override
                        public void comment(String content) {
                            try {
                                newComment(p.getPostid(), content, c.getComment_id());
                            } catch (XmlRpcFault ex) {
                                logger.severe("Unable to comment on " + p + "#" + c + ": " + ex.toString());
                            }
                        }
                    });
                }
            }
        } catch (XmlRpcFault f) {
            logger.severe(f.toString());
        }
    }

    public void newComment(int postID, String content) throws XmlRpcFault {
        wordpress.newComment(postID, -1, content, null, null, null);
    }

    public void newComment(int postID, String content, int commentID) throws XmlRpcFault {
        wordpress.newComment(postID, commentID, content, null, null, null);
    }
    
}
