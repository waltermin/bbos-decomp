package net.rim.device.api.lbs.gps;

import net.rim.device.api.bluetooth.BluetoothSerialPort;

public class NmeaStream {
   private byte[] _buffer = new byte[4096];
   private int _posEndOfData = 0;
   private int _posStartOfData = -1;
   private int _posScanned = -1;
   int[] _field = new int[20];
   private static final long GPGGA = 39888708126529L;
   private static final int GPGGA_UTC_TIME = 1;
   private static final int GPGGA_LATITUDE = 2;
   private static final int GPGGA_NS_INDICATOR = 3;
   private static final int GPGGA_LONGITUDE = 4;
   private static final int GPGGA_EW_INDICATOR = 5;
   private static final int GPGGA_POSITION_FIX = 6;
   private static final int GPGGA_SATELLITES_USED = 7;
   private static final int GPGGA_HDOP = 8;
   private static final int GPGGA_ALTITUDE = 9;
   private static final int GPGGA_CHECKSUM = 16;
   private static final int GPGGA_REQUIRED_FIELD_COUNT = 10;
   private static final long GPRMC = 39888708848963L;
   public static final int GPRMC_UTC_TIME = 1;
   public static final int GPRMC_STATUS = 2;
   public static final int GPRMC_LATITUDE = 3;
   public static final int GPRMC_NS_INDICATOR = 4;
   public static final int GPRMC_LONGITUDE = 5;
   public static final int GPRMC_EW_INDICATOR = 6;
   public static final int GPRMC_SPEED_OVER_GROUND = 7;
   public static final int GPRMC_COURSE_OVER_GROUND = 8;
   public static final int GPRMC_UTC_DATE = 9;
   public static final int GPRMC_MAGNETIC_VARIATION_DEG = 10;
   public static final int GPRMC_MAGNETIC_VARIATION_EW = 11;
   public static final int GPRMC_CHECKSUM = 13;
   public static final int GPRMC_REQUIRED_FIELD_COUNT = 8;

   int parseFields(int markerEnd) {
      int fieldCount = 0;
      this._field[fieldCount++] = this._posStartOfData;

      for (int index = this._posStartOfData; index < markerEnd; index++) {
         if (this._buffer[index] == 44) {
            if (fieldCount >= this._field.length - 1) {
               return fieldCount;
            }

            this._field[fieldCount++] = index + 1;
         }

         if (fieldCount > 18) {
            break;
         }
      }

      this._field[fieldCount] = markerEnd;
      return fieldCount;
   }

   public boolean parseSentence(GPSLocationData location, String sentence) {
      this._buffer = sentence.getBytes();
      this._posStartOfData = 0;
      this._posEndOfData = this._buffer.length;
      this._posScanned = -1;
      long sentenceId = this.parseSentenceId(0);
      return this.parseSentence(location, sentenceId, this._buffer.length);
   }

   boolean parseSentence(GPSLocationData param1, long param2, int param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 5
      // 003: lload 2
      // 004: ldc2_w 39888708848963
      // 007: lcmp
      // 008: ifne 03f
      // 00b: aload 0
      // 00c: iload 4
      // 00e: invokevirtual net/rim/device/api/lbs/gps/NmeaStream.parseFields (I)I
      // 011: istore 6
      // 013: iload 6
      // 015: bipush 8
      // 017: if_icmpgt 01c
      // 01a: bipush 0
      // 01b: ireturn
      // 01c: aload 1
      // 01d: getfield net/rim/device/api/lbs/gps/GPSLocationData._sourceLocked Z
      // 020: ifne 026
      // 023: goto 182
      // 026: aload 1
      // 027: aload 0
      // 028: bipush 8
      // 02a: invokevirtual net/rim/device/api/lbs/gps/NmeaStream.parseDouble (I)D
      // 02d: d2f
      // 02e: putfield net/rim/device/api/lbs/gps/GPSLocationData._bearing F
      // 031: aload 1
      // 032: aload 0
      // 033: bipush 7
      // 035: invokevirtual net/rim/device/api/lbs/gps/NmeaStream.parseDouble (I)D
      // 038: d2f
      // 039: putfield net/rim/device/api/lbs/gps/GPSLocationData._speed F
      // 03c: goto 182
      // 03f: lload 2
      // 040: ldc2_w 39888708126529
      // 043: lcmp
      // 044: ifeq 04a
      // 047: goto 182
      // 04a: aload 0
      // 04b: iload 4
      // 04d: invokevirtual net/rim/device/api/lbs/gps/NmeaStream.parseFields (I)I
      // 050: istore 6
      // 052: iload 6
      // 054: bipush 10
      // 056: if_icmpgt 05b
      // 059: bipush 0
      // 05a: ireturn
      // 05b: aload 0
      // 05c: bipush 2
      // 05e: bipush 3
      // 060: invokevirtual net/rim/device/api/lbs/gps/NmeaStream.parseDegrees (II)I
      // 063: istore 7
      // 065: aload 0
      // 066: bipush 4
      // 068: bipush 5
      // 06a: invokevirtual net/rim/device/api/lbs/gps/NmeaStream.parseDegrees (II)I
      // 06d: istore 8
      // 06f: aload 0
      // 070: bipush 9
      // 072: invokevirtual net/rim/device/api/lbs/gps/NmeaStream.parseDouble (I)D
      // 075: d2f
      // 076: nop
      // 077: fstore 9
      // 079: aload 0
      // 07a: bipush 7
      // 07c: invokevirtual net/rim/device/api/lbs/gps/NmeaStream.parseInt (I)I
      // 07f: istore 10
      // 081: aload 0
      // 082: bipush 6
      // 084: invokevirtual net/rim/device/api/lbs/gps/NmeaStream.parseInt (I)I
      // 087: ifeq 093
      // 08a: iload 10
      // 08c: ifle 093
      // 08f: bipush 1
      // 090: goto 094
      // 093: bipush 0
      // 094: istore 11
      // 096: aload 1
      // 097: getfield net/rim/device/api/lbs/gps/GPSLocationData._satelliteCount I
      // 09a: iload 10
      // 09c: if_icmpne 0c5
      // 09f: aload 1
      // 0a0: getfield net/rim/device/api/lbs/gps/GPSLocationData._sourceLocked Z
      // 0a3: iload 11
      // 0a5: if_icmpne 0c5
      // 0a8: aload 1
      // 0a9: getfield net/rim/device/api/lbs/gps/GPSLocationData._latitude I
      // 0ac: iload 7
      // 0ae: if_icmpne 0c5
      // 0b1: aload 1
      // 0b2: getfield net/rim/device/api/lbs/gps/GPSLocationData._longitude I
      // 0b5: iload 8
      // 0b7: if_icmpne 0c5
      // 0ba: aload 1
      // 0bb: getfield net/rim/device/api/lbs/gps/GPSLocationData._altitude F
      // 0be: nop
      // 0bf: fload 9
      // 0c1: fcmpl
      // 0c2: ifeq 0c9
      // 0c5: bipush 1
      // 0c6: goto 0ca
      // 0c9: bipush 0
      // 0ca: istore 5
      // 0cc: aload 1
      // 0cd: iload 10
      // 0cf: putfield net/rim/device/api/lbs/gps/GPSLocationData._satelliteCount I
      // 0d2: aload 1
      // 0d3: iload 7
      // 0d5: putfield net/rim/device/api/lbs/gps/GPSLocationData._latitude I
      // 0d8: aload 1
      // 0d9: iload 8
      // 0db: putfield net/rim/device/api/lbs/gps/GPSLocationData._longitude I
      // 0de: aload 1
      // 0df: nop
      // 0e0: fload 9
      // 0e2: putfield net/rim/device/api/lbs/gps/GPSLocationData._altitude F
      // 0e5: aload 1
      // 0e6: getfield net/rim/device/api/lbs/gps/GPSLocationData._sourceLocked Z
      // 0e9: iload 11
      // 0eb: if_icmpne 0f1
      // 0ee: goto 158
      // 0f1: ldc2_w 4560142210062134028
      // 0f4: new java/lang/Object
      // 0f7: dup
      // 0f8: ldc_w "GPS lock "
      // 0fb: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0fe: iload 11
      // 100: ifeq 11a
      // 103: new java/lang/Object
      // 106: dup
      // 107: ldc_w "accquired, satellite count: "
      // 10a: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 10d: aload 1
      // 10e: getfield net/rim/device/api/lbs/gps/GPSLocationData._satelliteCount I
      // 111: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 114: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 117: goto 149
      // 11a: new java/lang/Object
      // 11d: dup
      // 11e: ldc_w "lost at lat: "
      // 121: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 124: aload 1
      // 125: getfield net/rim/device/api/lbs/gps/GPSLocationData._latitude I
      // 128: i2d
      // 129: nop
      // 12a: ldc2_w 4681608360884174848
      // 12d: ddiv
      // 12e: d2i
      // 12f: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 132: ldc_w ", long: "
      // 135: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 138: aload 1
      // 139: getfield net/rim/device/api/lbs/gps/GPSLocationData._longitude I
      // 13c: i2d
      // 13d: nop
      // 13e: ldc2_w 4681608360884174848
      // 141: ddiv
      // 142: d2i
      // 143: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 146: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 149: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 14c: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 14f: invokevirtual java/lang/String.getBytes ()[B
      // 152: bipush 5
      // 154: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 157: pop
      // 158: aload 1
      // 159: iload 11
      // 15b: putfield net/rim/device/api/lbs/gps/GPSLocationData._sourceLocked Z
      // 15e: aload 1
      // 15f: iload 11
      // 161: putfield net/rim/device/api/lbs/gps/GPSLocationData._isValid Z
      // 164: iload 11
      // 166: ifne 182
      // 169: aload 1
      // 16a: nop
      // 16b: bipush 0
      // 16d: putfield net/rim/device/api/lbs/gps/GPSLocationData._speed F
      // 170: goto 182
      // 173: astore 6
      // 175: aload 1
      // 176: invokevirtual net/rim/device/api/lbs/gps/GPSLocationData.reset ()V
      // 179: goto 182
      // 17c: astore 6
      // 17e: aload 1
      // 17f: invokevirtual net/rim/device/api/lbs/gps/GPSLocationData.reset ()V
      // 182: iload 5
      // 184: ireturn
      // try (2 -> 14): 178 null
      // try (15 -> 45): 178 null
      // try (46 -> 177): 178 null
      // try (2 -> 14): 182 null
      // try (15 -> 45): 182 null
      // try (46 -> 177): 182 null
   }

   int fieldLength(int field) {
      return this._field[field + 1] - this._field[field] - 1;
   }

   boolean isDirectionNegative(int field) {
      byte b = this._buffer[this._field[field]];
      return b == 83 || b == 87 || b == 115 || b == 119;
   }

   public int parseDegrees(int field, int dirField) {
      int offset = this._field[field];
      int length = this.fieldLength(field);
      int colon = -1;

      int decimal;
      for (decimal = offset; decimal < offset + length; decimal++) {
         int c = this._buffer[decimal];
         if (c == 58) {
            colon = decimal;
            break;
         }

         if (c == 46) {
            break;
         }
      }

      double minutes;
      double degrees;
      if (colon != -1) {
         degrees = parseInt(this._buffer, offset, colon - offset);
         minutes = parseDouble(this._buffer, colon + 1, offset + length - colon - 1);
      } else {
         degrees = parseInt(this._buffer, offset, decimal - offset - 2);
         minutes = parseDouble(this._buffer, decimal - 2, offset + length - decimal + 2);
      }

      double result = degrees + minutes / 4633641066610819072L;
      if (this.isDirectionNegative(dirField)) {
         result *= -4616189618054758400L;
      }

      return (int)(result * 4681608360884174848L);
   }

   public int parseInt(int field) {
      return parseInt(this._buffer, this._field[field], this.fieldLength(field));
   }

   public static int parseInt(byte[] buffer, int offset, int length) {
      int end = offset + length;
      int sign = 1;
      if (buffer[offset] == 45) {
         sign = -1;
         offset++;
      } else if (buffer[offset] == 43) {
         offset++;
      }

      int value = 0;

      for (int index = offset; index < end; index++) {
         int digit = buffer[index] - 48;
         if (digit < 0 || 9 < digit) {
            throw new Object();
         }

         value = 10 * value + digit;
      }

      return sign * value;
   }

   public double parseDouble(int field) {
      return parseDouble(this._buffer, this._field[field], this.fieldLength(field));
   }

   public static double parseDouble(byte[] buffer, int offset, int length) {
      int decimal = offset;

      while (decimal < offset + length && buffer[decimal] != 46) {
         decimal++;
      }

      double integer = parseInt(buffer, offset, decimal - offset);
      double fraction = (double)0L;

      label49:
      try {
         fraction = parseInt(buffer, decimal + 1, offset + length - decimal - 1);
      } finally {
         break label49;
      }

      int divisor = 1;
      decimal++;

      while (decimal < offset + length) {
         divisor *= 10;
         decimal++;
      }

      return integer + fraction / divisor;
   }

   public int length() {
      return this._posStartOfData == -1 ? this._posEndOfData : this._posEndOfData - this._posStartOfData;
   }

   void reset() {
      this._posEndOfData = 0;
      this._posStartOfData = -1;
      this._posScanned = -1;
   }

   public void appendData(String string, int offset, int length) {
      byte[] data = string.getBytes();
      int end = offset + length;
      if (end >= this._buffer.length) {
         this.reset();
      } else {
         for (int i = offset; i < end; i++) {
            this._buffer[this._posEndOfData++] = data[i];
         }
      }
   }

   public void appendData(BluetoothSerialPort port, int length) {
      synchronized (this) {
         try {
            this._posEndOfData = this._posEndOfData
               + port.read(this._buffer, this._posEndOfData, length == -1 ? this._buffer.length - this._posEndOfData : length);
         } finally {
            return;
         }
      }
   }

   long parseSentenceId(int pos) {
      long sentenceId = 0;

      for (int index = pos; index < pos + 6; index++) {
         sentenceId = sentenceId << 8 | this._buffer[index];
      }

      return sentenceId;
   }

   boolean parseBuffer(GPSLocationData location) {
      boolean sendUpdate = false;

      for (int i = this._posScanned + 1; i < this._posEndOfData; i++) {
         switch (this._buffer[i]) {
            case 10:
               if (this._posStartOfData != -1) {
                  long sentenceId = this.parseSentenceId(this._posStartOfData);
                  if (this.parseSentence(location, sentenceId, i)) {
                     sendUpdate = true;
                  }
               }
               break;
            case 36:
               this._posStartOfData = i;
         }
      }

      if (this._posStartOfData > 0) {
         this._posEndOfData = this._posEndOfData - this._posStartOfData;
         System.arraycopy(this._buffer, this._posStartOfData, this._buffer, 0, this._posEndOfData);
         this._posStartOfData = 0;
      }

      this._posScanned = this._posEndOfData - 1;
      return sendUpdate;
   }
}
