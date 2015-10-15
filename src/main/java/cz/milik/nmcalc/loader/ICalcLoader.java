/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.loader;

import java.io.InputStream;

/**
 *
 * @author jan
 */
public interface ICalcLoader {
    public InputStream getStream(String name);
}
