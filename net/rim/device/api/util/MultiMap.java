package net.rim.device.api.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class MultiMap implements Persistable {
   private Hashtable _hashtable;
   private int _initialVectorCapacity;

   public MultiMap(int initialHashtableCapacity, int initialVectorCapacity) {
      if (initialVectorCapacity < 0) {
         throw new IllegalArgumentException();
      }

      this._hashtable = new Hashtable(initialHashtableCapacity);
      this._initialVectorCapacity = initialVectorCapacity;
   }

   public MultiMap() {
      this(16, 10);
   }

   public boolean isEmpty() {
      return this._hashtable.isEmpty();
   }

   public void clear() {
      this._hashtable.clear();
   }

   public boolean add(Object key, Object value) {
      Vector vector = (Vector)this._hashtable.get(key);
      if (vector == null) {
         vector = new Vector(this._initialVectorCapacity);
         this._hashtable.put(key, vector);
      }

      if (vector.contains(value)) {
         return false;
      }

      vector.addElement(value);
      return true;
   }

   public boolean removeKey(Object key) {
      return this._hashtable.remove(key) != null;
   }

   public boolean removeValue(Object key, Object value) {
      Vector vector = (Vector)this._hashtable.get(key);
      if (vector == null) {
         return false;
      }

      if (!vector.removeElement(value)) {
         return false;
      }

      if (vector.size() == 0) {
         this._hashtable.remove(key);
      }

      return true;
   }

   public boolean removeValue(Object value) {
      boolean result = false;
      Enumeration keys = this._hashtable.keys();

      while (keys.hasMoreElements()) {
         Object key = keys.nextElement();
         if (this.removeValue(key, value)) {
            result = true;
         }
      }

      return result;
   }

   public boolean containsKey(Object key) {
      return this._hashtable.containsKey(key);
   }

   public boolean containsValue(Object key, Object value) {
      Vector vector = (Vector)this._hashtable.get(key);
      return vector == null ? false : vector.contains(value);
   }

   public Enumeration keys() {
      return this._hashtable.keys();
   }

   public Enumeration elements(Object key) {
      Vector vector = (Vector)this._hashtable.get(key);
      return vector == null ? new EmptyEnumeration() : vector.elements();
   }

   public Enumeration elements() {
      Vector vector = new Vector();
      Enumeration keys = this._hashtable.keys();

      while (keys.hasMoreElements()) {
         Enumeration elements = this.elements(keys.nextElement());

         while (elements.hasMoreElements()) {
            Object element = elements.nextElement();
            vector.addElement(element);
         }
      }

      return vector.elements();
   }

   public int size() {
      int size = 0;
      Enumeration keys = this._hashtable.keys();

      while (keys.hasMoreElements()) {
         Vector vector = (Vector)this._hashtable.get(keys.nextElement());
         if (vector != null) {
            size += vector.size();
         }
      }

      return size;
   }

   public int size(Object key) {
      Vector vector = (Vector)this._hashtable.get(key);
      return vector == null ? 0 : vector.size();
   }

   @Override
   public String toString() {
      return this._hashtable.toString();
   }
}
