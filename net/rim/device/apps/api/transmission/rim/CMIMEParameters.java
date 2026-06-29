package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.transmission.Parameters;

public final class CMIMEParameters extends Parameters implements CMIMEConstants {
   public CMIMEParameters(int capacityInt, int incrementInt) {
      super(capacityInt, incrementInt);
   }

   public CMIMEParameters(DataBuffer aDataBuffer, int capacityInt, int incrementInt) {
      super(aDataBuffer, capacityInt, incrementInt);
   }

   public final void addCMIMEDate(byte nameByte, long millisecondsLong) {
      int value = (int)(millisecondsLong / 1000);
      this.addToIndex(nameByte, super._buffer.getPosition());
      super._buffer.writeByte(nameByte);
      super._buffer.writeCompressedInt(4);
      super._buffer.writeByte(value >> 24 & 0xFF);
      super._buffer.writeByte(value >> 16 & 0xFF);
      super._buffer.writeByte(value >> 8 & 0xFF);
      super._buffer.writeByte(value & 0xFF);
   }

   public final void addCMIMEInteger(byte nameByte, int valueInt) {
      this.writeCMIMEInteger(nameByte, valueInt, (byte)4);
   }

   public final void addCMIMEIntegerInCompactWay(byte nameByte, int valueInt) {
      if (valueInt < 0 || valueInt > 16777215) {
         this.writeCMIMEInteger(nameByte, valueInt, (byte)4);
      } else if (valueInt <= 255) {
         this.writeCMIMEInteger(nameByte, valueInt, (byte)1);
      } else if (valueInt <= 65535) {
         this.writeCMIMEInteger(nameByte, valueInt, (byte)2);
      } else {
         this.writeCMIMEInteger(nameByte, valueInt, (byte)3);
      }
   }

   public final void addCMIMEEmailAddress(byte nameByte, String addressString, String friendlyString) {
      this.addCMIMEEmailAddress(nameByte, addressString, friendlyString, false, (byte)0);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void addCMIMEEmailAddress(byte nameByte, String addressString, String friendlyString, boolean isUnicodeEnabled, byte encodingCode) {
      StringBuffer wholeAddress = (StringBuffer)(new Object());
      byte[] wholeAddressBytes = null;
      int wholeAddressLength = 0;
      if (addressString != null && addressString.length() > 0) {
         wholeAddress.append(addressString);
         if (friendlyString != null && friendlyString.length() > 0) {
            wholeAddress.append('\u0000');
            wholeAddress.append(friendlyString);
         }
      }

      String wholeAddressString = wholeAddress.toString();
      String encString = CMIMEUtilities.CMIME_DEFAULT_EMAIL_ENCODING;
      if (isUnicodeEnabled) {
         if (wholeAddressString == null || ConverterUtilities.isIntellisyncCompatible(wholeAddressString)) {
            isUnicodeEnabled = false;
         }

         if (isUnicodeEnabled) {
            nameByte = (byte)(nameByte | -128);
            encString = CMIMEUtilities.getEncoding(encodingCode);
         }
      }

      boolean var13 = false /* VF: Semaphore variable */;

      label77:
      try {
         var13 = true;
         if (wholeAddressString != null) {
            wholeAddressBytes = wholeAddressString.getBytes(encString);
            var13 = false;
         } else {
            var13 = false;
         }
      } finally {
         if (var13) {
            wholeAddressBytes = wholeAddressString.getBytes();
            break label77;
         }
      }

      if (wholeAddressBytes != null) {
         wholeAddressLength = wholeAddressBytes.length;
      }

      if (isUnicodeEnabled && wholeAddressLength > 0) {
         wholeAddressLength++;
      }

      this.addToIndex(nameByte, super._buffer.getPosition());
      super._buffer.writeByte(nameByte);
      super._buffer.writeCompressedInt(wholeAddressLength);
      if (wholeAddressLength > 0) {
         if (isUnicodeEnabled) {
            super._buffer.writeByte(encodingCode);
         }

         super._buffer.write(wholeAddressBytes);
      }
   }

   public final long getCMIMEDate(int offsetInt) {
      long value = 0;

      try {
         int position = super._buffer.getPosition();
         super._buffer.setPosition(offsetInt);
         super._buffer.readByte();
         if (super._buffer.readCompressedInt() == 4) {
            value |= super._buffer.readUnsignedByte() << 24 & -16777216;
            value |= super._buffer.readUnsignedByte() << 16 & 16711680;
            value |= super._buffer.readUnsignedByte() << 8 & 65280;
            value |= super._buffer.readUnsignedByte() & 255;
            value *= 1000;
         } else {
            value = System.currentTimeMillis();
         }

         super._buffer.setPosition(position);
         return value;
      } finally {
         return System.currentTimeMillis();
      }
   }

   public final int getCMIMEInteger(int offsetInt) {
      int value = 0;

      try {
         int position = super._buffer.getPosition();
         super._buffer.setPosition(offsetInt);
         super._buffer.readByte();
         switch (super._buffer.readCompressedInt()) {
            case 4:
            default:
               value |= super._buffer.readUnsignedByte() << 24 & 0xFF000000;
            case 3:
               value |= super._buffer.readUnsignedByte() << 16 & 0xFF0000;
            case 2:
               value |= super._buffer.readUnsignedByte() << 8 & 0xFF00;
            case 1:
               value |= super._buffer.readUnsignedByte() & 0xFF;
            case 0:
               super._buffer.setPosition(position);
               return value;
         }
      } finally {
         return 0;
      }
   }

   private final void writeCMIMEInteger(byte nameByte, int valueInt, byte numberOfInt) {
      this.addToIndex(nameByte, super._buffer.getPosition());
      super._buffer.writeByte(nameByte);
      super._buffer.writeCompressedInt(numberOfInt);
      switch (numberOfInt) {
         case 4:
         default:
            super._buffer.writeByte(valueInt >> 24 & 0xFF);
         case 3:
            super._buffer.writeByte(valueInt >> 16 & 0xFF);
         case 2:
            super._buffer.writeByte(valueInt >> 8 & 0xFF);
         case 1:
            super._buffer.writeByte(valueInt & 0xFF);
         case 0:
      }
   }
}
