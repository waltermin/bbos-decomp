package net.rim.device.apps.internal.mms.model;

import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.vm.Persistable;

public final class ExtraWSPHeaderAttachment extends PersistedAttachmentImpl implements Persistable {
   private byte[] _extraWSPHeaderData;

   public ExtraWSPHeaderAttachment(MMSAttachment attachment, byte[] wspHeaderData) {
      super(attachment);
      this._extraWSPHeaderData = wspHeaderData;
   }

   public final byte[] getExtraWSPHeaderData() {
      return this._extraWSPHeaderData;
   }
}
