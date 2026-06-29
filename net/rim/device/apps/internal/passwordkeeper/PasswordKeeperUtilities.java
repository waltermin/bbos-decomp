package net.rim.device.apps.internal.passwordkeeper;

import java.util.Vector;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.ObjectGroup;

public final class PasswordKeeperUtilities {
   private PasswordKeeperUtilities() {
   }

   public static final boolean checkPasswordValidity(byte[] currentPassword) {
      PasswordKeeper passwordKeeper = PasswordKeeper.getInstance();
      PasswordKeeperList source = passwordKeeper.getCollection().getSource();
      int size = source.size();

      for (int i = 0; i < size; i++) {
         PasswordKeeperElement element = (PasswordKeeperElement)source.getAt(i);
         if (!element.checkPassword(currentPassword)) {
            return false;
         }
      }

      return true;
   }

   public static final void convertElementsToCurrentPassword(byte[] currentPassword) {
      PasswordKeeper passwordKeeper = PasswordKeeper.getInstance();
      PasswordKeeperList source = passwordKeeper.getCollection().getSource();
      Vector passwordContainer = (Vector)(new Object());
      passwordContainer.addElement(currentPassword);
      int size = source.size();

      for (int i = size - 1; i >= 0; i--) {
         PasswordKeeperElement element = (PasswordKeeperElement)source.getAt(i);
         byte[] oldPassword = checkPasswords(element, passwordContainer);
         if (oldPassword == null) {
            try {
               oldPassword = getOldPassword(element);
               passwordContainer.addElement(oldPassword);
            } catch (DeletionException e) {
               source.removeAt(i);
               continue;
            }
         }

         try {
            PasswordKeeperElement conversionElement = element;
            if (ObjectGroup.isInGroup(element)) {
               conversionElement = (PasswordKeeperElement)ObjectGroup.expandGroup(element);
            }

            conversionElement.changePassword(oldPassword, currentPassword);
            ObjectGroup.createGroup(conversionElement);
            source.insertAt(i, conversionElement);
            source.remove(element);
         } catch (DecryptionException e) {
            throw new Object();
         } catch (PasswordKeeperLockedException e) {
            throw new Object();
         }
      }
   }

   private static final byte[] checkPasswords(PasswordKeeperElement element, Vector passwordContainer) {
      int numberOfPasswords = passwordContainer.size();

      for (int i = 0; i < numberOfPasswords; i++) {
         byte[] password = (byte[])passwordContainer.elementAt(i);
         if (element.checkPassword(password)) {
            return password;
         }
      }

      return null;
   }

   public static final byte[] getOldPassword(PasswordKeeperElement element) throws CancelException, DeletionException {
      boolean firstAttempt = true;

      while (true) {
         PasswordKeeperPasswordDialog dialog;
         if (firstAttempt) {
            DateFormat dateFormat = DateFormat.getInstance(56);
            String date;
            if (element.getCreationTime() == 0) {
               date = PasswordKeeper.getString(3037);
            } else {
               date = dateFormat.formatLocal(element.getCreationTime());
            }

            String message = MessageFormat.format(PasswordKeeper.getString(3035), new Object[]{date});
            dialog = new PasswordKeeperPasswordDialog(false, message, true);
         } else {
            dialog = new PasswordKeeperPasswordDialog(false, PasswordKeeper.getString(3036), true);
         }

         dialog.show();
         if (dialog.getCloseReason() == -1) {
            throw new CancelException();
         }

         if (dialog.getCloseReason() == 2) {
            throw new DeletionException();
         }

         byte[] oldPassword = dialog.getText().getBytes();
         if (oldPassword.length != 0) {
            if (element.checkPassword(oldPassword)) {
               return oldPassword;
            }

            firstAttempt = false;
         }
      }
   }

   public static final boolean isAvailable(String string) {
      return string != null && string.length() != 0;
   }
}
