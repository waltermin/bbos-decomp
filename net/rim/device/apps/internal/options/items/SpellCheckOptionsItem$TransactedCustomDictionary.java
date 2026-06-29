package net.rim.device.apps.internal.options.items;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableSet;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.awt.im.repository.CustomDictionary;

final class SpellCheckOptionsItem$TransactedCustomDictionary implements ReadableSet, WritableSet, CollectionEventSource {
   private CollectionListenerManager _listeners = new CollectionListenerManager();
   private Hashtable _deleted = new Hashtable();
   private Hashtable _added = new Hashtable();
   private boolean _dirty;
   private boolean _cleared;
   private CustomDictionary _customDic;
   private SpellCheckOptionsItem$TransactedCustomDictionary$TransactedCustomDictionaryEnumeration _enumer;

   SpellCheckOptionsItem$TransactedCustomDictionary(CustomDictionary customDic) {
      this._customDic = customDic;
   }

   public final boolean isDirty() {
      return this._dirty;
   }

   public final void commit() {
      if (this._cleared) {
         SpellCheckOptionsItem.clearCustomDictionary();
         this._cleared = false;
      } else {
         Enumeration enumer = this._deleted.keys();

         while (enumer.hasMoreElements()) {
            Object entry = enumer.nextElement();
            this._customDic.remove(entry);
         }

         this._deleted.clear();
      }

      Enumeration enumer = this._added.keys();
      CustomDictionary tempCustomDic = InputContext.getInstance().getCustomDictionary(1);

      while (enumer.hasMoreElements()) {
         Object entry = enumer.nextElement();
         this._customDic.add(entry);
         if (tempCustomDic != null) {
            tempCustomDic.add(entry);
         }
      }

      this._added.clear();
      this._dirty = false;
   }

   public final void rollback() {
      this._deleted.clear();
      this._added.clear();
      this._listeners.fireReset(this);
      this._dirty = false;
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._listeners.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._listeners.addCollectionListener(listener);
   }

   @Override
   public final void add(Object newEntry) {
      if (this._deleted.containsKey(newEntry)) {
         this._deleted.remove(newEntry);
         this._listeners.fireElementAdded(this, newEntry);
      }

      if (this._cleared || !this._customDic.contains(newEntry)) {
         this._added.put(newEntry, newEntry);
         this._dirty = true;
         this._listeners.fireElementAdded(this, newEntry);
      }
   }

   @Override
   public final boolean contains(Object element) {
      return this._added.containsKey(element) ? true : !this._cleared && this._customDic.contains(element) && !this._deleted.containsKey(element);
   }

   @Override
   public final Enumeration getElements() {
      return this.getEnumerator();
   }

   @Override
   public final int getElements(Object[] elements) {
      Enumeration enumer = this.getElements();

      int i;
      for (i = 0; enumer.hasMoreElements(); i++) {
         elements[i] = enumer.nextElement();
      }

      return i;
   }

   @Override
   public final int size() {
      return this._cleared ? this._added.size() : this._customDic.size() + this._added.size() - this._deleted.size();
   }

   @Override
   public final void remove(Object removeEntry) {
      if (this._added.containsKey(removeEntry)) {
         this._added.remove(removeEntry);
      } else {
         this._deleted.put(removeEntry, removeEntry);
      }

      this._dirty = true;
      this._listeners.fireElementRemoved(this, removeEntry);
   }

   @Override
   public final void removeAll() {
      this._added.clear();
      this._deleted.clear();
      this._cleared = true;
      this._dirty = true;
      this._listeners.fireReset(this);
   }

   private final SpellCheckOptionsItem$TransactedCustomDictionary$TransactedCustomDictionaryEnumeration getEnumerator() {
      if (this._enumer == null) {
         this._enumer = new SpellCheckOptionsItem$TransactedCustomDictionary$TransactedCustomDictionaryEnumeration(this);
      }

      this._enumer.reset();
      return this._enumer;
   }
}
