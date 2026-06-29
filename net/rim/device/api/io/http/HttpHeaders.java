package net.rim.device.api.io.http;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.util.CloneableVector;
import net.rim.device.api.util.ContentProtectedVector;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.http.Utilities;

public final class HttpHeaders implements HttpProtocolConstants, Persistable {
   private ContentProtectedVector _properties;
   private Vector _propertyKeys;

   public HttpHeaders() {
      this.reset();
   }

   private HttpHeaders(Vector keys, ContentProtectedVector properties) {
      this._properties = properties;
      this._propertyKeys = keys;
   }

   public HttpHeaders(Hashtable headers) {
      this.reset();
      this.fromHashtable(headers);
   }

   public HttpHeaders(DataInputStream dins) {
      this.reset();
      this.readFromStream(dins);
   }

   public final String getPropertyValue(String key) {
      int position = this.searchForPropertyIndex(0, key);
      return position >= 0 ? this.getPropertyValue(position) : null;
   }

   public final Vector getPropertyValues(String key) {
      int position = -1;
      Vector items = new Vector();

      while ((position = this.searchForPropertyIndex(position + 1, key)) != -1) {
         items.addElement(this.getPropertyValue(position));
      }

      return items.size() > 0 ? items : null;
   }

   public final String getPropertyValue(int position) {
      return position > -1 && position < this._properties.size() ? (String)this._properties.elementAt(position) : null;
   }

   public final String getPropertyKey(int position) {
      return position > -1 && position < this._propertyKeys.size() ? (String)this._propertyKeys.elementAt(position) : null;
   }

   public final int size() {
      return this._propertyKeys.size();
   }

   public final void reset() {
      this._properties = new ContentProtectedVector(false);
      this._propertyKeys = new Vector();
   }

   private final int searchForPropertyIndex(int startPosition, String key) {
      int vectorSize = this._propertyKeys.size();

      for (int position = startPosition; position < vectorSize; position++) {
         if (StringUtilities.strEqualIgnoreCase(key, (String)this._propertyKeys.elementAt(position), 1701707776)) {
            return position;
         }
      }

      return -1;
   }

   public final void setProperty(String key, String value) {
      if (key == null) {
         throw new IllegalArgumentException();
      }

      int position = this.searchForPropertyIndex(0, key);
      if (position > -1) {
         this._properties.setElementAt(value, position);
      } else {
         this._propertyKeys.addElement(key);
         this._properties.addElement(value);
      }
   }

   public final void addProperty(String key, String value) {
      if (key == null) {
         throw new IllegalArgumentException();
      }

      this._propertyKeys.addElement(key);
      this._properties.addElement(value);
   }

   public final void setProperty(int keyPosition, String value) {
      if (keyPosition > -1 && keyPosition < this._propertyKeys.size()) {
         this._properties.setElementAt(value, keyPosition);
      }
   }

   public final void removeProperty(int keyPosition) {
      if (keyPosition > -1 && keyPosition < this._propertyKeys.size()) {
         this._propertyKeys.removeElementAt(keyPosition);
         this._properties.removeElementAt(keyPosition);
      }
   }

   public final void removeProperties(String key) {
      for (int i = this._propertyKeys.size() - 1; i >= 0; i--) {
         if (StringUtilities.strEqualIgnoreCase(key, (String)this._propertyKeys.elementAt(i), 1701707776)) {
            this._propertyKeys.removeElementAt(i);
            this._properties.removeElementAt(i);
         }
      }
   }

   public final void writeToStream(DataOutputStream douts) {
      int xPropertiesSize = this._properties.size();

      for (int i = 0; i < xPropertiesSize; i++) {
         douts.write(((String)this._propertyKeys.elementAt(i)).getBytes());
         douts.write(":".getBytes());
         douts.write(" ".getBytes());
         douts.write(((String)this._properties.elementAt(i)).getBytes());
         douts.write("\r\n".getBytes());
      }

      douts.write("\r\n".getBytes());
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void readFromStream(DataInputStream aDataInputStream) throws IOException {
      String line = null;

      while ((line = Utilities.receiveLine(aDataInputStream)) != null) {
         if (line.length() == 0) {
            return;
         }

         try {
            int indexOfColon = line.indexOf(58);
            if (indexOfColon != -1) {
               this._propertyKeys.addElement(line.substring(0, indexOfColon).trim());
               this._properties.addElement(line.substring(indexOfColon + 1).trim());
            }
         } catch (Throwable var5) {
            throw new IOException(e.toString());
         }
      }
   }

   public final void setProperties(HttpHeaders otherHeaders) {
      for (int j = otherHeaders._propertyKeys.size() - 1; j >= 0; j--) {
         String key = (String)otherHeaders._propertyKeys.elementAt(j);
         int foundIndex = -1;

         while ((foundIndex = this.searchForPropertyIndex(foundIndex + 1, key)) != -1) {
            this.removeProperty(foundIndex);
         }
      }

      for (int j = otherHeaders._propertyKeys.size() - 1; j >= 0; j--) {
         this.addProperty((String)otherHeaders._propertyKeys.elementAt(j), (String)otherHeaders._properties.elementAt(j));
      }
   }

   public final Hashtable toHashtable() {
      Hashtable output = new Hashtable();
      int count = this._properties.size();

      for (int i = 0; i < count; i++) {
         output.put(StringUtilities.toLowerCase((String)this._propertyKeys.elementAt(i), 1701707776), (String)this._properties.elementAt(i));
      }

      return output;
   }

   public final void fromHashtable(Hashtable ht) {
      Enumeration keys = ht.keys();

      while (keys.hasMoreElements()) {
         Object key = keys.nextElement();
         this._propertyKeys.addElement(key);
         this._properties.addElement(ht.get(key));
      }
   }

   public final synchronized boolean isProtected() {
      return this._properties.isProtected();
   }

   public final synchronized boolean checkCrypt() {
      return this._properties.checkCrypt();
   }

   public final synchronized void reCrypt() {
      this._properties.reCrypt();
   }

   public final synchronized HttpHeaders cloneHeaders() {
      ContentProtectedVector propertiesClone = new ContentProtectedVector(this._properties.size(), 1, false);
      Enumeration elements = this._properties.elements();

      while (elements.hasMoreElements()) {
         propertiesClone.addElement(elements.nextElement());
      }

      return new HttpHeaders(CloneableVector.clone(this._propertyKeys), propertiesClone);
   }
}
