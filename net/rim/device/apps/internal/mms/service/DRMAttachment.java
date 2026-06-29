package net.rim.device.apps.internal.mms.service;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSAttachment;

final class DRMAttachment implements MMSAttachment {
   private MMSAttachment _inner;
   private byte[] _head;
   private byte[] _tail;

   public DRMAttachment(MMSAttachment attachment) {
      this._inner = attachment;
      String EOL = "\r\n";
      StringBuffer buf = new StringBuffer();
      buf.append("--boundary-1");
      buf.append(EOL);
      buf.append("Content-type: ");
      buf.append(MMSUtilities.getMIMETypeString(attachment.getType()));
      buf.append(EOL);
      buf.append("Content-Transfer-Encoding: binary");
      buf.append(EOL);
      buf.append(EOL);
      this._head = buf.toString().getBytes();
      buf.setLength(0);
      buf.append(EOL);
      buf.append("--boundary-1--");
      buf.append(EOL);
      this._tail = buf.toString().getBytes();
   }

   @Override
   public final String getName() {
      return this._inner.getName();
   }

   @Override
   public final int getType() {
      return 72;
   }

   @Override
   public final byte[] getData() {
      DataBuffer buf = new DataBuffer();
      buf.write(this._head);
      buf.write(this._inner.getData());
      buf.write(this._tail);
      return buf.toArray();
   }

   @Override
   public final String getCharset() {
      return null;
   }

   @Override
   public final int getDataSize() {
      return this._head.length + this._inner.getDataSize() + this._tail.length;
   }
}
