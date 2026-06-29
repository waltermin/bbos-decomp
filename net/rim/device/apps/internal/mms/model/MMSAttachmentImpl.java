package net.rim.device.apps.internal.mms.model;

import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.mms.api.MMSAttachment;

public final class MMSAttachmentImpl implements MMSAttachment {
   private String _name;
   private int _type;
   private byte[] _data;
   private String _charset;

   public MMSAttachmentImpl(MMSAttachment attachment) {
      this(attachment.getName(), attachment.getType(), attachment.getData(), attachment.getCharset());
   }

   public MMSAttachmentImpl(String name, int type, byte[] data, String charset) {
      this._name = name;
      this._type = type;
      this._data = data;
      this._charset = charset;
      if (data == null) {
         throw new RuntimeException("Missing attachment data.");
      }
   }

   public static final MMSAttachment copy(MMSAttachment attachment) {
      String name = attachment.getName();
      if (name != null) {
         name = name;
      }

      String charset = attachment.getCharset();
      if (charset != null) {
         charset = charset;
      }

      byte[] data = attachment.getData();
      if (data != null) {
         data = Arrays.copy(data);
      }

      return new MMSAttachmentImpl(name, attachment.getType(), data, charset);
   }

   @Override
   public final String getName() {
      return this._name;
   }

   @Override
   public final int getType() {
      return this._type;
   }

   @Override
   public final byte[] getData() {
      return this._data;
   }

   @Override
   public final String getCharset() {
      return this._charset;
   }

   @Override
   public final int getDataSize() {
      return this._data.length;
   }
}
