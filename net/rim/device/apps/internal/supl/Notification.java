package net.rim.device.apps.internal.supl;

final class Notification {
   NotificationType notificationType;
   private EncodingType encodingType;
   private byte[] requestorId = new byte[50];
   private FormatIndicator requestorIdType;
   private byte[] clientName = new byte[50];
   private FormatIndicator clientNameType;
   private byte optionals;
   static final byte NOTIFICATION_MIN_CLIENT_LENGTH = 1;
   static final byte NOTIFICATION_MAX_CLIENT_LENGTH = 50;
   static final byte NOTIFICATION_CLIENT_LENGTH_BIT_SIZE = 6;
   static final byte NOTIFICATION_MIN_REQ_LENGTH = 1;
   static final byte NOTIFICATION_MAX_REQ_LENGTH = 50;
   static final byte NOTIFICATION_REQ_LENGTH_BIT_SIZE = 6;
   static final byte NOTIFICATION_MASK_ENCODING_TYPE = 16;
   static final byte NOTIFICATION_MASK_REQUESTOR_ID = 8;
   static final byte NOTIFICATION_MASK_REQUESTOR_ID_TYPE = 4;
   static final byte NOTIFICATION_MASK_CLIENT_NAME = 2;
   static final byte NOTIFICATION_MASK_CLIENT_NAME_TYPE = 1;
   static final byte NOTIFICATION_OPTIONALS_BIT_SIZE = 5;

   final void decode(Nibbler nib) {
      nib.getBit();
      this.optionals = (byte)nib.getBitsLarge(5);
      this.notificationType = new NotificationType();
      this.notificationType.decode(nib);
      if (Nibbler.IsBitSet(this.optionals, (byte)16)) {
         this.encodingType = new EncodingType();
         this.encodingType.decode(nib);
      }

      if (Nibbler.IsBitSet(this.optionals, (byte)8)) {
         int length = (byte)nib.getBitsLarge(6) + 1;

         for (int i = 0; i < length; i++) {
            this.requestorId[i] = nib.getByte();
         }
      }

      if (Nibbler.IsBitSet(this.optionals, (byte)4)) {
         this.requestorIdType = new FormatIndicator();
         this.requestorIdType.decode(nib);
      }

      if (Nibbler.IsBitSet(this.optionals, (byte)2)) {
         int length = nib.getBitsLarge(6) + 1;

         for (int i = 0; i < length; i++) {
            this.clientName[i] = nib.getByte();
         }
      }

      if (Nibbler.IsBitSet(this.optionals, (byte)1)) {
         this.clientNameType = new FormatIndicator();
         this.clientNameType.decode(nib);
      }
   }

   final void print() {
      System.out.println("Notification: ");
      System.out.println(((StringBuffer)(new Object("Optionals: "))).append(this.optionals).toString());
      this.notificationType.print();
      if ((this.optionals & 16) == 16) {
         this.encodingType.print();
      }

      if ((this.optionals & 8) == 8) {
         System.out.println("Requestor Id: ");

         for (int i = 0; i < 50; i++) {
            System.out.print(((StringBuffer)(new Object())).append(Integer.toHexString(255 & this.requestorId[i])).append(" ").toString());
         }

         System.out.print("\n");
      }

      if ((this.optionals & 4) == 4) {
         System.out.println("Requestor Id Type: ");
         this.requestorIdType.print();
      }

      if ((this.optionals & 2) == 2) {
         System.out.println("Client Name: ");

         for (int i = 0; i < 50; i++) {
            System.out.print(((StringBuffer)(new Object())).append(Integer.toHexString(255 & this.clientName[i])).append(" ").toString());
         }

         System.out.print("\n");
      }

      if ((this.optionals & 1) == 1) {
         System.out.println("Client Name Type: ");
         this.clientNameType.print();
      }
   }
}
