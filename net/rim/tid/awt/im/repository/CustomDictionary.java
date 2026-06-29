package net.rim.tid.awt.im.repository;

import java.util.Enumeration;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableSet;
import net.rim.device.api.collection.util.CollectionListenerManager;

public class CustomDictionary implements ReadableSet, CollectionEventSource {
   protected CollectionListenerManager _listeners = new CollectionListenerManager();
   public static final byte NEW_WORDS;
   public static final byte FREQUENCY_WORDS;
   public static final byte URL_EMAIL_WORDS;
   public static final byte EXTRACTED_WORDS;
   public static final byte REJECTED_WORDS;

   public Object add(Object newEntry) {
      this._listeners.fireElementAdded(this, newEntry);
      return newEntry;
   }

   public void remove(Object removeEntry) {
      this._listeners.fireElementRemoved(this, removeEntry);
   }

   public boolean isInRepository(Object _1) {
      throw null;
   }

   public Object find(Object entry) {
      return null;
   }

   @Override
   public void addCollectionListener(Object listener) {
      this._listeners.addCollectionListener(listener);
   }

   @Override
   public void removeCollectionListener(Object listener) {
      this._listeners.addCollectionListener(listener);
   }

   @Override
   public int getElements(Object[] _1) {
      throw null;
   }

   @Override
   public Enumeration getElements() {
      throw null;
   }

   @Override
   public boolean contains(Object _1) {
      throw null;
   }

   @Override
   public int size() {
      throw null;
   }
}
