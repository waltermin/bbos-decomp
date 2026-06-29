package net.rim.device.apps.internal.browser.html;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.browser.core.IBrowserContext;

public final class HTMLContext implements IBrowserContext, Persistable {
   private Hashtable _nameToValue;
   private static final int VERSION = 1;

   HTMLContext() {
      this._nameToValue = new Hashtable();
   }

   private HTMLContext(Hashtable ht) {
      this._nameToValue = ht;
   }

   final void put(String name, String value) {
      if (name != null && value != null) {
         this._nameToValue.put(name, PersistentContent.encode(value, true, true));
      }
   }

   final String get(String name) {
      return this._nameToValue.get(name) == null ? "" : PersistentContent.decodeString(this._nameToValue.get(name));
   }

   @Override
   public final String toString() {
      StringBuffer buff = new StringBuffer();
      Enumeration e = this._nameToValue.keys();

      while (e.hasMoreElements()) {
         String key = (String)e.nextElement();
         buff.append('\n');
         buff.append(key);
         buff.append(' ');
         buff.append('=');
         buff.append(' ');
         buff.append(PersistentContent.decodeString(this._nameToValue.get(key)));
      }

      return buff.toString();
   }

   @Override
   public final IBrowserContext clone() {
      HTMLContext newContext = new HTMLContext();
      Enumeration keys = this._nameToValue.keys();

      while (keys.hasMoreElements()) {
         String name = (String)keys.nextElement();
         newContext.put(name, PersistentContent.decodeString(this._nameToValue.get(name)));
      }

      return newContext;
   }

   @Override
   public final boolean serialize(SyncBuffer syncBuffer) {
      DataBuffer dataBuffer = new DataBuffer(false);
      dataBuffer.writeCompressedInt(1);
      dataBuffer.writeCompressedInt(this._nameToValue.size());
      Enumeration e = this._nameToValue.keys();

      try {
         while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            dataBuffer.writeUTF(key);
            dataBuffer.writeUTF(PersistentContent.decodeString(this._nameToValue.get(key)));
         }
      } finally {
         ;
      }

      syncBuffer.addBytes(24, dataBuffer.toArray());
      return true;
   }

   public static final HTMLContext createInstance(Object data) {
      SyncBuffer syncBuffer = (SyncBuffer)data;
      DataBuffer dataBuffer = syncBuffer.getDataBuffer();
      int fieldType = syncBuffer.getFieldType();

      try {
         dataBuffer.readShort();
         dataBuffer.readByte();
         if (fieldType == 24) {
            dataBuffer.readCompressedInt();
         }
      } finally {
         ;
      }

      Hashtable nameToValue = null;

      try {
         int numContextEntries = dataBuffer.readCompressedInt();
         nameToValue = new Hashtable();

         for (int i = 0; i < numContextEntries; i++) {
            String key = dataBuffer.readUTF();
            String value = dataBuffer.readUTF();
            nameToValue.put(key, PersistentContent.encode(value, true, true));
         }
      } finally {
         ;
      }

      return new HTMLContext(nameToValue);
   }
}
