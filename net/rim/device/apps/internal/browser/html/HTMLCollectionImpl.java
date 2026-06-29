package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLOptionsCollection;

public final class HTMLCollectionImpl implements HTMLCollection, NodeList, HTMLOptionsCollection {
   private HTMLElement[] _elements;
   private String[] _names;
   private boolean _multipleNames;
   private int _lookupCache = Integer.MAX_VALUE;

   public final void replace(int index, String name, HTMLElement newElement) {
      if (index >= 0 && index < this._elements.length) {
         this.checkDuplicateNames(name);
         this._elements[index] = newElement;
         this._names[index] = name;
      }
   }

   public final void remove(int index) {
      if (index >= 0 && index < this._elements.length) {
         Arrays.removeAt(this._elements, index);
         Arrays.removeAt(this._names, index);
      }
   }

   public final void addItem(int index, String name, HTMLElement element) {
      if (index >= 0 && index < this._elements.length) {
         this.checkDuplicateNames(name);
         Arrays.insertAt(this._elements, element, index);
         Arrays.insertAt(this._names, name, index);
      }
   }

   public final void addItem(String name, HTMLElement element) {
      this.checkDuplicateNames(name);
      Arrays.add(this._elements, element);
      Arrays.add(this._names, name);
   }

   public final Object namedItems(String name) {
      if (name == null) {
         return null;
      }

      if (!this._multipleNames) {
         return this.namedItem(name);
      }

      HTMLCollectionImpl list = null;

      for (int i = 0; i < this._elements.length; i++) {
         if (name.equals(this._names[i])) {
            if (list == null) {
               list = new HTMLCollectionImpl();
            }

            list.addItem(name, this._elements[i]);
         }
      }

      return list;
   }

   @Override
   public final Node namedItem(String name) {
      if (name == null) {
         return null;
      }

      if (this._lookupCache < this._elements.length && name.equals(this._elements[this._lookupCache])) {
         return this._elements[this._lookupCache];
      }

      for (int i = 0; i < this._elements.length; i++) {
         if (name.equals(this._names[i])) {
            this._lookupCache = i;
            return this._elements[i];
         }
      }

      return null;
   }

   @Override
   public final Node item(int index) {
      return index >= 0 && index < this._elements.length ? this._elements[index] : null;
   }

   @Override
   public final void setLength(int newLength) {
      if (newLength < 0) {
         newLength = 0;
      }

      Array.resize(this._elements, newLength);
      Array.resize(this._names, newLength);
   }

   @Override
   public final int getLength() {
      return this._elements.length;
   }

   public HTMLCollectionImpl() {
      this._elements = new HTMLElement[0];
      this._names = new String[0];
   }

   private final void checkDuplicateNames(String name) {
      if (name != null && !this._multipleNames) {
         for (int i = 0; i < this._names.length; i++) {
            if (name.equals(this._names[i])) {
               this._multipleNames = true;
               return;
            }
         }
      }
   }
}
