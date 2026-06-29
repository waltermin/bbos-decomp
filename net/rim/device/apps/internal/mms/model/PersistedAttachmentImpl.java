package net.rim.device.apps.internal.mms.model;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.vm.Persistable;

public class PersistedAttachmentImpl implements MMSAttachment, Persistable, EncryptableProvider {
   private String _name;
   private int _type;
   private Object _dataEncoding;
   private String _charset;
   private int _dataSize;

   public PersistedAttachmentImpl(MMSAttachment attachment) {
      this(attachment.getName(), attachment.getType(), attachment.getData(), attachment.getCharset());
   }

   public PersistedAttachmentImpl(String name, int type, byte[] data, String charset) {
      this._name = name;
      this._type = type;
      this._dataEncoding = PersistentContent.encode(data);
      this._charset = charset;
      this._dataSize = data.length;
   }

   @Override
   public String getName() {
      return this._name;
   }

   @Override
   public int getType() {
      return this._type;
   }

   @Override
   public byte[] getData() {
      return PersistentContent.decodeByteArray(this._dataEncoding);
   }

   @Override
   public String getCharset() {
      return this._charset;
   }

   @Override
   public int getDataSize() {
      return this._dataSize;
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._dataEncoding, compress, encrypt);
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this._dataEncoding = PersistentContent.reEncode(this._dataEncoding, compress, encrypt);
      return null;
   }
}
