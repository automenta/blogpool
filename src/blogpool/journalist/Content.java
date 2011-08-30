/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blogpool.journalist;

import java.io.Serializable;

/**
 *
 * @author seh
 */
public abstract class Content implements Serializable {
    
    abstract public String toHTML();
    abstract public String getSource();
    abstract public boolean setSource(String s);
    
    public static class Text extends Content {
        private String text;
        private String wrapper;
        
        public Text(String text) {
            this(text, null);            
        }
            
        /**
         * @param wrapper HTML tag to wrap the text in, ex: H1
         */
        public Text(String text, String wrapper) {
            this.text = text;
            this.wrapper = wrapper;
        }

        public String getText() {
            return text;
        }

        public String getWrapper() {
            return wrapper;
        }

        @Override
        public String getSource() {
            return text;
        }

        @Override
        public boolean setSource(String s) {
            this.text = s;
            return true;
        }
        
        public void setText(String text) {
            this.text = text;
        }

        public void setWrapper(String wrapper) {
            this.wrapper = wrapper;
        }                

        @Override
        public String toHTML() {
            return (wrapper == null) ? text : "<" + wrapper + ">" + text + "</" + wrapper + ">";
        }
        
        
    }
    
    public static class Image extends Content {
    
        public String url;

        public Image(String url) {
            this.url = url;
        }

        @Override
        public String toHTML() {
            return "<img src='" + url + "'/><br/>";
        }        
        
        @Override
        public String getSource() {
            return url;
        }

        @Override
        public boolean setSource(String s) {
            this.url = s;
            return true;
        }
        
    }
    
    public static class YoutubeVideo extends Content {
        public String video;

        public YoutubeVideo(String video) {
            this.video = video;
        }

        @Override
        public String toHTML() {
            //TODO use embed code
            return "<a href='" + video + "'>" + video + "</a>";
        }
        
        @Override
        public String getSource() {
            return video;
        }

        @Override
        public boolean setSource(String s) {
            this.video = s;
            return true;
        }
        
        
    }
    
}
