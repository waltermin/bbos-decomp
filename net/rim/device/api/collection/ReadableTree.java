package net.rim.device.api.collection;

import java.util.Enumeration;

public interface ReadableTree extends Collection {
   Enumeration children();

   ReadableTree getParent();

   boolean hasChildren();

   boolean allowsChildren();
}
