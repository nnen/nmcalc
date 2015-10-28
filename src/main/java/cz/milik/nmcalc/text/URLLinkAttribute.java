/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.text;

import java.net.URL;

/**
 *
 * @author jan
 */
public class URLLinkAttribute {
    
    private final URL link;
    
    public URL getLink() {
        return link;
    }
    
    public URLLinkAttribute(URL link) {
        this.link = link;
    }
    
}
