package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.system.Application;
import net.rim.device.internal.resource.crypto.CryptoIcons;
import net.rim.device.internal.ui.Image;

public class ProcessingField extends StatusField {
   private String _processingText;
   private static final long STATUS_TYPE_PROCESSING = -3092861192590837945L;

   ProcessingField(String processingText) {
      super(Application.getApplication());
      this._processingText = processingText;
      this.updateStatus();
   }

   @Override
   protected Image getImage() {
      return CryptoIcons.getImage(13);
   }

   @Override
   protected String getText() {
      return this._processingText;
   }

   @Override
   public String getShortText() {
      return this.getText();
   }

   @Override
   public int getPriority() {
      return 9000;
   }

   @Override
   public long getStatusType() {
      return -3092861192590837945L;
   }
}
