/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogpool.journalist;

import blogpool.journalist.Content.Image;
import blogpool.journalist.Content.Text;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author seh
 */
public class History implements Serializable {

    private static class RSSContentSource extends ContentSource {
        private final String title;

        int timeoutMS = 1500;
        
        public RSSContentSource(String contentURL, RssItemBean item) {
            super(contentURL, new Date());
            content.add(new Text(item.getDescription()));
            
            this.title = item.getTitle();
                        
            try {
                Document doc = Jsoup.parse(new URL(item.getLink()), timeoutMS);
                
                System.out.println(doc.text());
                content.add(new Text(doc.text()));               
                
                Elements images = doc.select("img");
                for (int i = 0; i < images.size(); i++) {
                    Element img = images.get(i);
                    content.add(new Image(img.attr("src")));
                }
            } catch (Exception ex) {
                Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, ex);
            }
//                System.out.println("Title: " + item.getTitle());
//                System.out.println("Link : " + item.getLink());
//                System.out.println("Desc.: " + item.getDescription());
        }

        @Override
        public String getTitle() {
            return title;
        }
        
    }

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
            
    
    
    
}
