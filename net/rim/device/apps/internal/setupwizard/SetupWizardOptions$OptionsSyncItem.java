package net.rim.device.apps.internal.setupwizard;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;

final class SetupWizardOptions$OptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int DB_VERSION = 0;
   private static final int DATA_LENGTH = 2;

   @Override
   public final String getSyncName() {
      return "Setup Wizard Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final boolean setSyncData(DataBuffer buffer, int version) {
      SetupWizardOptions options = SetupWizardOptions.getOptions();

      label44:
      try {
         buffer.readShort();
         buffer.readByte();
         boolean val = buffer.readByte() != 0;
         if (!val) {
            options.setSetupWizardDesired(false);
         }

         val = buffer.readByte() != 0;
         if (val) {
            options.setWizardCompleted(true);
         }
      } finally {
         break label44;
      }

      options.commit();
      return true;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      SetupWizardOptions options = SetupWizardOptions.getOptions();
      buffer.writeShort(2);
      buffer.writeByte(0);
      buffer.writeByte(options.getSetupWizardDesired() ? 1 : 0);
      buffer.writeByte(options.getWizardCompleted() ? 1 : 0);
      return true;
   }
}
