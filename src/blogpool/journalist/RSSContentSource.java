/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogpool.journalist;

import blogpool.journalist.Content.Image;
import blogpool.journalist.Content.Text;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.horrabin.horrorss.RssItemBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author seh
 */
public class RSSContentSource extends ContentSource {
    private String title;
    int timeoutMS = 3500;
    
    public RSSContentSource() {
        super();
    }

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
