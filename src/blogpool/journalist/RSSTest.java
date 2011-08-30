/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogpool.journalist;

import java.util.Vector;
import org.horrabin.horrorss.RssChannelBean;
import org.horrabin.horrorss.RssImageBean;
import org.horrabin.horrorss.RssItemBean;
import org.horrabin.horrorss.RssParser;

/**
 *
 * @author seh
 */
public class RSSTest {

    public static void main(String[] args) {
        RssParser rss = new RssParser("http://code.google.com/feeds/p/horrorss/updates/basic");
        try {
            rss.parse();
            RssChannelBean channel = rss.getChannel(); //Obtain the channel element
            RssImageBean image = rss.getImage(); //Obtain the image element
            Vector items = rss.getItems(); //Obtain a Vector of item elements (RssItemBean)

            // Iterate the list items
            for (int i = 0; i < items.size(); i++) {
                RssItemBean item = (RssItemBean) items.elementAt(i); //Cast the Object from the list to RssItemBean
                System.out.println("Title: " + item.getTitle());
                System.out.println("Link : " + item.getLink());
                System.out.println("Desc.: " + item.getDescription());
            }


        } catch (Exception e) {
            //Treat the exception as you want
        }
    }
}
