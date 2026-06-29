package net.rim.blackberry.api.pdap;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.util.Persistable;

final class ToDoFieldsModel implements Persistable {
   private Hashtable _customFieldTable;
   private int _customCount;
   private Vector _categories;
   private long _revision;
   private long _completed;

   public ToDoFieldsModel() {
   }

   public final void setRevision(long revision) {
      this._revision = revision;
   }

   public final long getCompleted() {
      return this._completed;
   }

   public final void setCompleted(long completed) {
      this._completed = completed;
   }

   public final long getRevision() {
      return this._revision;
   }

   public final boolean isEmpty() {
      return this._customCount == 0 && this._revision == 0 && (this._categories == null || this._categories.size() == 0);
   }

   public final Object get(String key) {
      return this._customFieldTable == null ? null : this._customFieldTable.get(key);
   }

   public final void put(String key, Object value) {
      if (this._customFieldTable == null) {
         this._customFieldTable = new Hashtable();
      }

      if (this._customFieldTable.put(key, value) == null) {
         this._customCount++;
      }
   }

   public final String[] getCustomLabels() {
      if (this._customFieldTable != null && this._customCount != 0) {
         Enumeration keys = this._customFieldTable.keys();
         String[] labels = new String[this._customCount];

         for (int i = 0; keys.hasMoreElements(); i++) {
            labels[i] = (String)keys.nextElement();
         }

         return labels;
      } else {
         return null;
      }
   }

   public final void addCategory(String category) {
      if (category == null) {
         throw new NullPointerException();
      }

      if (this._categories == null) {
         this._categories = new Vector();
      }

      if (!this._categories.contains(category)) {
         this._categories.addElement(category);
      }
   }

   public final boolean removeCategory(String category) {
      if (category == null) {
         throw new NullPointerException();
      } else {
         return this._categories == null ? false : this._categories.removeElement(category);
      }
   }

   public final String[] getCategories() {
      if (this._categories == null) {
         return null;
      }

      int size = this._categories.size();
      if (size == 0) {
         return null;
      }

      String[] categories = new String[size];
      this._categories.copyInto(categories);
      return categories;
   }
}
