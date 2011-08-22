/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogpool;

import blogpool.source.WordpressSource;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author me
 */
public class Blogpool {

    public Blogpool() {
        super();
    }
    
    final Map<String, Content> content = new HashMap();
    
    public Content addContent(Content c) {
        content.put(c.getURL(), c);        
        return c;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Blogpool p = new Blogpool();
            
            WordpressSource wp = new WordpressSource(p, "/home/me/blogpool.cfg");
            wp.refresh();
            
            System.out.println(p.content);
            
            
        }
        catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}
