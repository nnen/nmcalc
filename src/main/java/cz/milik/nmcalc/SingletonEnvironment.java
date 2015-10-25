/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author jan
 */
public class SingletonEnvironment extends Environment {
    
    private final UUID id;

    public UUID getId() {
        return id;
    }
    
    
    private SingletonEnvironment(UUID id) {
        this.id = id;
    }

    private SingletonEnvironment(UUID id, Environment parent) {
        super(parent);
        this.id = id;
    }
    
    
    protected Object writeReplace()
        throws java.io.ObjectStreamException
    {
        return new Proxy(getId());
    }
    
    
    private static Map<UUID, SingletonEnvironment> index = new HashMap();
    
    public static SingletonEnvironment get(UUID id) {
        SingletonEnvironment env = index.get(id);
        if (env == null) {
            env = new SingletonEnvironment(id);
            index.put(id, env);
        }
        return env;
    }
    
    
    public static class Proxy implements Serializable {
        private final UUID id;
        
        public Proxy(UUID id) {
            this.id = id;
        }
        
        private Object readResolve()
            throws java.io.ObjectStreamException
        {
            return get(id);
        }
    }
    
}
