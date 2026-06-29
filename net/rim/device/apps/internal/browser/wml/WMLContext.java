package net.rim.device.apps.internal.browser.wml;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.internal.browser.core.IBrowserContext;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.BrowserIdEncryptor;

public final class WMLContext implements IBrowserContext, Persistable {
   private Hashtable _nameToValue;
   private int _timer;
   public static final String RIM_ID_NAME = "XXX_RIM_ID";
   private static final int VERSION = 1;

   public WMLContext() {
      this._nameToValue = new Hashtable();
      this._timer = -1;
   }

   private WMLContext(Hashtable nameToValue) {
      this._nameToValue = nameToValue;
      this._timer = -1;
   }

   public final String get(String name) {
      if ("XXX_RIM_ID".equals(name)) {
         return BrowserIdEncryptor.getEncryptedId();
      } else {
         return this._nameToValue.get(name) == null ? "" : PersistentContent.decodeString(this._nameToValue.get(name));
      }
   }

   public final void clear() {
      this._nameToValue.clear();
      this._timer = -1;
   }

   public final void put(String name, String value) {
      if (name != null && value != null) {
         this._nameToValue.put(name, PersistentContent.encode(value, true, true));
      }
   }

   public final void setTimerValue(int value) {
      this._timer = value;
   }

   @Override
   public final IBrowserContext clone() {
      WMLContext newContext = new WMLContext();
      Enumeration keys = this._nameToValue.keys();

      while (keys.hasMoreElements()) {
         String name = (String)keys.nextElement();
         newContext.put(name, PersistentContent.decodeString(this._nameToValue.get(name)));
      }

      newContext.setTimerValue(this._timer);
      return newContext;
   }

   @Override
   public final boolean serialize(SyncBuffer syncBuffer) {
      DataBuffer dataBuffer = new DataBuffer(false);
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

      dataBuffer.writeCompressedInt(this._timer);
      dataBuffer.writeCompressedInt(1);
      syncBuffer.addBytes(20, dataBuffer.toArray());
      return true;
   }

   @Override
   public final String toString() {
      StringBuffer buff = new StringBuffer(MessageFormat.format(BrowserResources.getString(371), new String[]{Integer.toString(this._timer)}));
      buff.append('\n');
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

   public static final WMLContext createInstance(Object data) {
      SyncBuffer syncBuffer = (SyncBuffer)data;
      DataBuffer dataBuffer = syncBuffer.getDataBuffer();
      int fieldType = syncBuffer.getFieldType();

      try {
         dataBuffer.readShort();
         dataBuffer.readByte();
      } finally {
         ;
      }

      Hashtable nameToValue = null;
      int timer = -1;

      try {
         int numContextEntries = dataBuffer.readCompressedInt();
         nameToValue = new Hashtable();

         for (int i = 0; i < numContextEntries; i++) {
            String key = dataBuffer.readUTF();
            String value = dataBuffer.readUTF();
            nameToValue.put(key, PersistentContent.encode(value, true, true));
         }

         timer = dataBuffer.readCompressedInt();
         if (fieldType == 20) {
            dataBuffer.readCompressedInt();
         }
      } finally {
         ;
      }

      WMLContext wmlContext = new WMLContext(nameToValue);
      wmlContext.setTimerValue(timer);
      return wmlContext;
   }
}
