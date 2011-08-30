/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogpool.journalist;

import flexjson.JSONSerializer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.horrabin.horrorss.RssChannelBean;
import org.horrabin.horrorss.RssImageBean;
import org.horrabin.horrorss.RssItemBean;
import org.horrabin.horrorss.RssParser;

/**
 *
 * @author seh
 */
public class History implements Serializable {

    public final Set<String> rssFeeds = new HashSet();
    public final Map<String, ContentSource> content = new HashMap();
    transient public final Map<String, ImageIcon> imageCache = new ConcurrentHashMap();
        
    public History() {
        rssFeeds.add("http://finance.yahoo.com/rss/topstories");
    }

    void refresh() {
        for (String s : rssFeeds) {
            refreshRSS(s);
        }
    }

    private void refreshRSS(String s) {
        RssParser rss = new RssParser(s);
        try {
            rss.parse();
            RssChannelBean channel = rss.getChannel(); //Obtain the channel element
            RssImageBean image = rss.getImage(); //Obtain the image element
            
            Vector items = rss.getItems(); //Obtain a Vector of item elements (RssItemBean)
            for (int i = 0; i < items.size(); i++) {
                RssItemBean item = (RssItemBean) items.elementAt(i);
                
                String contentURL = item.getLink();
                if (!content.containsKey(contentURL))
                    content.put(contentURL, new RSSContentSource(contentURL, item));               
            }

        } catch (Exception e) {
            //Treat the exception as you want
        }
    }
    
    public ImageIcon getImage(String url) {
        ImageIcon i = imageCache.get(url);
        if (i != null)
            return i;
        ImageIcon ii;
        try {
            ii = new ImageIcon(new URL(url));
            imageCache.put(url, ii);
            return ii;
        } catch (MalformedURLException ex) {
            Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    void save(String historyFile) throws IOException {
        String s = new JSONSerializer().deepSerialize(this);
        
        FileWriter fw = new FileWriter(new File(historyFile));
        fw.append(s);
        fw.close();

    }
            
    
    
    
}
