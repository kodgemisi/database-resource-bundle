package com.kodgemisi.summer.databaseresourcbundle;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Copied from {@link sun.util.ResourceBundleEnumeration}
 */
class ResourceBundleEnumeration implements Enumeration<String> {
    Set<String> set;
    Iterator<String> iterator;
    Enumeration<String> enumeration;
    String next = null;

    public ResourceBundleEnumeration(Set<String> set, Enumeration<String> enumeration) {
        this.set = set;
        this.iterator = set.iterator();
        this.enumeration = enumeration;
    }

    public boolean hasMoreElements() {
        if (this.next == null) {
            if (this.iterator.hasNext()) {
                this.next = (String)this.iterator.next();
            } else if (this.enumeration != null) {
                while(this.next == null && this.enumeration.hasMoreElements()) {
                    this.next = (String)this.enumeration.nextElement();
                    if (this.set.contains(this.next)) {
                        this.next = null;
                    }
                }
            }
        }

        return this.next != null;
    }

    public String nextElement() {
        if (this.hasMoreElements()) {
            String result = this.next;
            this.next = null;
            return result;
        } else {
            throw new NoSuchElementException();
        }
    }
}
