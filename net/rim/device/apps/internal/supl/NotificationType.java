package net.rim.device.apps.internal.supl;

final class NotificationType {
   byte type;
   static final byte NO_NOTIFICATION_NO_VERIFICATION = 0;
   static final byte NOTIFICATION_ONLY = 1;
   static final byte NOTIFICATION_AND_VERIFICATION_ALLOWED_NA = 2;
   static final byte NOTIFICATION_AND_VERIFICATION_DENIED_NA = 3;
   static final byte PRIVACY_OVERRIDE = 4;
   static final byte NOTIFICATION_TYPE_BIT_SIZE = 3;

   final void decode(Nibbler nib) {
      nib.getBit();
      this.type = (byte)nib.getBitsLarge(3);
   }

   final void print() {
      System.out.println("Notification Type: " + this.type);
   }
}
