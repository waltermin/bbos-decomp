package net.rim.device.apps.internal.addressbook;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.DataBuffer;

final class AddressBookOptionsImpl$AddressBookOptionsSyncItem extends OTASyncCapableSyncItem {
   private static final int MIN_DATA_LENGTH = 6;
   private static final int DATA_LENGTH = 11;
   private static final int DB_VERSION = 0;

   @Override
   public final String getSyncName() {
      return "Address Book Options";
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
      AddressBookOptionsImpl options = AddressBookOptionsImpl.getOptions();

      label73:
      try {
         int length = buffer.readShort();
         buffer.readByte();
         if (length < 6) {
            return false;
         }

         options.setDuplicateNamesAllowed(buffer.readByte() != 0);
         options.setConfirmDelete(buffer.readByte() != 0);
         long sortOrder = 1232448844688687736L;
         switch (buffer.readInt()) {
            case 0:
            default:
               sortOrder = 1232448844688687736L;
               break;
            case 1:
               sortOrder = -227891759293611117L;
               break;
            case 2:
               sortOrder = -4388042602796535003L;
         }

         options.setSortOrder(sortOrder);
         buffer.skipBytes(2);
         options.allowWirelessSync(buffer.readByte() != 0);
         byte preference = 0;
         switch (buffer.readByte()) {
            case 0:
            default:
               preference = 0;
               break;
            case 1:
               preference = 1;
               break;
            case 2:
               preference = 2;
         }

         options.setComposePreference(preference);
         switch (buffer.readByte()) {
            case -1:
               break;
            case 0:
            default:
               options.setListSeparatorAppearance((byte)0);
               break;
            case 1:
               options.setListSeparatorAppearance((byte)1);
               break;
            case 2:
               options.setListSeparatorAppearance((byte)2);
         }
      } finally {
         break label73;
      }

      options.commit();
      RIMGlobalMessagePoster.postGlobalEvent(-3950819934062689467L);
      return true;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      AddressBookOptionsImpl options = AddressBookOptionsImpl.getOptions();
      buffer.writeShort(11);
      buffer.writeByte(0);
      buffer.writeByte(options.getDuplicateNamesAllowed() ? 1 : 0);
      buffer.writeByte(options.getConfirmDelete() ? 1 : 0);
      long sortOrder = options.getSortOrder();
      int order = 0;
      if (sortOrder == 1232448844688687736L) {
         order = 0;
      } else if (sortOrder == -227891759293611117L) {
         order = 1;
      } else if (sortOrder == -4388042602796535003L) {
         order = 2;
      }

      buffer.writeInt(order);
      buffer.writeByte(0);
      buffer.writeByte(0);
      buffer.writeByte(options.isWirelessSyncAllowed() ? 1 : 0);
      int preferenceCode = 0;
      byte var8;
      switch (options.getComposePreference()) {
         case 0:
         default:
            var8 = 0;
            break;
         case 1:
            var8 = 1;
            break;
         case 2:
            var8 = 2;
      }

      buffer.writeByte(var8);
      buffer.writeByte(options.getListSeparatorAppearance());
      return true;
   }
}
