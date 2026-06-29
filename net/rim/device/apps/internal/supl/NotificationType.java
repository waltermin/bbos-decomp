package net.rim.device.apps.internal.supl;

final class NotificationType {
   byte type;
   static final byte NO_NOTIFICATION_NO_VERIFICATION;
   static final byte NOTIFICATION_ONLY;
   static final byte NOTIFICATION_AND_VERIFICATION_ALLOWED_NA;
   static final byte NOTIFICATION_AND_VERIFICATION_DENIED_NA;
   static final byte PRIVACY_OVERRIDE;
   static final byte NOTIFICATION_TYPE_BIT_SIZE;

   final void decode(Nibbler nib) {
      nib.getBit();
      this.type = (byte)nib.getBitsLarge(3);
   }

   final void print() {
      System.out.println(((StringBuffer)(new Object("Notification Type: "))).append(this.type).toString());
   }
}
