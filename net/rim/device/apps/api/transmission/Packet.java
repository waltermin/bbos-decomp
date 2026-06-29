package net.rim.device.apps.api.transmission;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ContextObject;

public class Packet {
   private byte[] _bytes;
   private int _offset;
   private int _length;
   private Object _listener;
   private int _tag;
   private Object _context;

   public Packet() {
   }

   public Packet(byte[] byteArray, Object listenerObject, int tagInt, Object contextObject) {
      this.setPayload(byteArray);
      this._listener = listenerObject;
      this._tag = tagInt;
      this._context = copyContextObject(contextObject);
   }

   public Packet(DataBuffer buffer, Object listenerObject, int tagInt, Object contextObject) {
      this.setPayload(buffer);
      this._listener = listenerObject;
      this._tag = tagInt;
      this._context = copyContextObject(contextObject);
   }

   private static void copyItem(ContextObject oldContext, ContextObject newContext, long item) {
      Object o = oldContext.get(item);
      if (o != null) {
         newContext.put(item, o);
      }
   }

   private static void copyFlag(ContextObject oldContext, ContextObject newContext, int flag) {
      if (oldContext.getFlag(flag)) {
         newContext.setFlag(flag);
      }
   }

   private static Object copyContextObject(Object context) {
      if (!(context instanceof ContextObject)) {
         return context;
      }

      ContextObject oldContext = (ContextObject)context;
      ContextObject newContext = new ContextObject();
      copyItem(oldContext, newContext, -5971550291443523639L);
      copyItem(oldContext, newContext, -7981905408958106750L);
      copyItem(oldContext, newContext, -6095803566992128485L);
      copyItem(oldContext, newContext, -7050660451800027507L);
      copyFlag(oldContext, newContext, 75);
      return newContext;
   }

   public void setPayload(byte[] byteArray) {
      this.setPayload(byteArray, 0, byteArray != null ? byteArray.length : 0);
   }

   public void setPayload(byte[] byteArray, int offset, int length) {
      this._bytes = byteArray;
      this._offset = offset;
      this._length = length;
   }

   public void setPayload(DataBuffer buffer) {
      this._bytes = buffer.getArray();
      this._offset = buffer.getArrayStart();
      this._length = buffer.getLength();
   }

   public byte[] getPayload() {
      return this._bytes;
   }

   public int getPayloadOffset() {
      return this._offset;
   }

   public int getPayloadLength() {
      return this._length;
   }

   public void setListener(Object listenerObject) {
      this._listener = listenerObject;
   }

   public Object getListener() {
      return this._listener;
   }

   public void setTag(int tagInt) {
      this._tag = tagInt;
   }

   public int getTag() {
      return this._tag;
   }

   public void setContextObject(Object contextObject) {
      this._context = copyContextObject(contextObject);
   }

   public Object getContextObject() {
      return this._context;
   }
}
