package net.rim.device.api.xml;

import java.util.Vector;
import net.rim.device.api.util.IntHashtable;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

class XMLHashtable$XMLToHashtableHandler extends DefaultHandler {
   private XMLHashtable _hashtable;
   private StringBuffer _path = new StringBuffer(64);
   private boolean _includeNamespaces;
   private boolean _includeExplicitPath;
   private Vector _values = new Vector(20);
   private int _top = -1;
   private String _prefix;
   private int _prefixLength;
   private static IntHashtable _integers = new IntHashtable();

   private XMLHashtable$XMLToHashtableHandler(XMLHashtable hashtable, boolean includeNamespaces, boolean includeExplicitPath, String prefix) {
      this._hashtable = hashtable;
      this._includeNamespaces = includeNamespaces;
      this._includeExplicitPath = includeExplicitPath;
      this._prefix = prefix;
      this._prefixLength = prefix.length();
   }

   public void addString(String key, String value) {
      Object current = this._hashtable.get(key);
      if (current == null) {
         this._hashtable.put(key, value);
      } else if (current instanceof String) {
         String[] newStrings = new String[]{(String)current, value};
         this._hashtable.put(key, newStrings);
      } else {
         String[] oldStrings = (String[])current;
         int oldStringsLength = oldStrings.length;
         String[] newStrings = new String[oldStringsLength + 1];
         System.arraycopy(oldStrings, 0, newStrings, 0, oldStringsLength);
         newStrings[oldStringsLength] = value;
         this._hashtable.put(key, newStrings);
      }
   }

   private static Integer getInteger(int n) {
      Integer i = (Integer)_integers.get(n);
      if (i != null) {
         return i;
      }

      i = new Integer(n);
      _integers.put(n, i);
      return i;
   }

   private void extendPath(String uri, String localName, boolean attribute) {
      this._path.append('/');
      if (attribute) {
         this._path.append('@');
      }

      if (this._includeNamespaces) {
         this._path.append(uri);
         this._path.append(':');
      }

      this._path.append(localName);
      if (this._includeExplicitPath) {
         String path = this._path.toString();
         int n = 0;
         Integer i = (Integer)this._hashtable.get(path);
         if (i != null) {
            n = i;
         }

         this._hashtable.put(path, getInteger(n + 1));
         this._path.append(':').append(n);
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) {
      this.extendPath(uri, localName, false);
      int pathLength = this._path.length();
      if (pathLength > this._prefixLength) {
         for (int i = this._prefixLength - 1; i >= 0; i--) {
            if (this._prefix.charAt(i) != this._path.charAt(i)) {
               return;
            }
         }

         if (++this._top >= this._values.size()) {
            this._values.addElement(new StringBuffer(10));
         }

         int numAttributes = attributes.getLength();
         if (numAttributes != 0) {
            for (int i = attributes.getLength() - 1; i >= 0; i--) {
               this.extendPath(attributes.getURI(i), attributes.getLocalName(i), true);
               this.addString(this._path.toString().substring(this._prefixLength), attributes.getValue(i));
               this._path.setLength(pathLength);
            }
         }
      }
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      ((StringBuffer)this._values.elementAt(this._top)).append(ch, start, length);
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (this._top >= 0) {
         StringBuffer value = (StringBuffer)this._values.elementAt(this._top--);
         String path = this._path.toString();
         this.addString(path.substring(this._prefixLength), value.length() > 0 ? value.toString() : XMLHashtable._emptyString);
         value.setLength(0);
         this._path.setLength(path.lastIndexOf(47));
      }
   }

   XMLHashtable$XMLToHashtableHandler(XMLHashtable x0, boolean x1, boolean x2, String x3, XMLHashtable$1 x4) {
      this(x0, x1, x2, x3);
   }
}
