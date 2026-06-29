package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.internal.resource.crypto.CryptoIcons;
import net.rim.device.internal.ui.Image;

public class SecureEmailMissingPrivateKeyField extends StatusField implements ContextMenuDelegate {
   private SecureEmailFactory _secureEmailFactory;
   private static final long STATUS_TYPE_MISSING_PRIVATE_KEY = -4997593557371589019L;

   public SecureEmailMissingPrivateKeyField(SecureEmailFactory secureEmailFactory) {
      super(Application.getApplication());
      this._secureEmailFactory = secureEmailFactory;
      this.updateStatus();
   }

   @Override
   protected Image getImage() {
      return CryptoIcons.getImage(12);
   }

   @Override
   public String getText() {
      return MessageFormat.format(SecureEmailResources.getString(161), new String[]{this._secureEmailFactory.getEncodingString()});
   }

   @Override
   public String getShortText() {
      return SecureEmailResources.getString(160);
   }

   @Override
   public int getPriority() {
      return 2100;
   }

   @Override
   public long getStatusType() {
      return -4997593557371589019L;
   }
}
