package net.rim.device.apps.internal.mms.model;

import net.rim.device.apps.internal.mms.api.MMSAttachment;

public final class AliasedAttachment implements MMSAttachment {
   private String _name;
   private MMSAttachment _attachment;

   public AliasedAttachment(String name, MMSAttachment attachment) {
      this._name = name;
      this._attachment = attachment;
   }

   @Override
   public final String getName() {
      return this._name;
   }

   @Override
   public final int getType() {
      return this._attachment.getType();
   }

   @Override
   public final byte[] getData() {
      return this._attachment.getData();
   }

   @Override
   public final String getCharset() {
      return this._attachment.getCharset();
   }

   @Override
   public final int getDataSize() {
      return this._attachment.getDataSize();
   }
}
