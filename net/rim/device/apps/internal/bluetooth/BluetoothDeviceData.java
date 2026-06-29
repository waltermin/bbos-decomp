package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.vm.Persistable;

final class BluetoothDeviceData implements Persistable {
   byte[] _address;
   String _name;
   String _friendlyName;
   int _deviceClass;
   int _authorized;
   boolean _encrypt = true;
   RemoteServiceRecord[] _serviceRecords;
   int _supportedProfiles;
   byte[] _linkKey;
   int _linkKeyType;
   int _nrecMode;
   boolean _sniffModeEnabled = true;
   private static final int TYPE_ADDRESS;
   private static final int TYPE_NAME;
   private static final int TYPE_FRIENDLY_NAME;
   private static final int TYPE_DEVICE_CLASS;
   private static final int TYPE_NEXT_UUID;
   private static final int TYPE_SUPPORTED_PROFILES;
   private static final int TYPE_FLAGS;
   private static final int TYPE_SERVICE_RECORD;
   private static final int TYPE_LINK_KEY;
   private static final int TYPE_LINK_KEY_TYPE;
   private static final int TYPE_AUTHORIZED;
   private static final int TYPE_NREC_MODE;
   private static final int FLAG_AUTHORIZED;
   private static final int FLAG_ENCRYPT;
   private static final int FLAG_SNIFF_MODE_ENABLED;

   BluetoothDeviceData() {
      this._serviceRecords = new RemoteServiceRecord[0];
   }

   final void addServiceRecord(RemoteServiceRecord record) {
      int handle = record.getHandle();
      synchronized (this._serviceRecords) {
         int i;
         for (i = 0; i < this._serviceRecords.length; i++) {
            if (this._serviceRecords[i].getHandle() == handle) {
               this._serviceRecords[i] = record;
               break;
            }
         }

         if (i == this._serviceRecords.length) {
            Arrays.add(this._serviceRecords, record);
         }
      }
   }

   static final BluetoothDeviceData deserialize(byte[] param0, boolean param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 2
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: new net/rim/device/apps/internal/bluetooth/BluetoothDeviceData
      // 003: dup
      // 004: invokespecial net/rim/device/apps/internal/bluetooth/BluetoothDeviceData.<init> ()V
      // 007: astore 2
      // 008: new java/lang/Object
      // 00b: dup
      // 00c: aload 0
      // 00d: bipush 0
      // 00e: aload 0
      // 00f: arraylength
      // 010: iload 1
      // 011: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 014: astore 3
      // 015: aload 3
      // 016: invokestatic net/rim/device/api/synchronization/ConverterUtilities.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 019: istore 4
      // 01b: goto 023
      // 01e: astore 6
      // 020: goto 132
      // 023: iload 4
      // 025: tableswitch 67 -1 11 252 67 78 89 100 252 197 111 170 208 219 230 241
      // 068: aload 2
      // 069: aload 3
      // 06a: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 06d: putfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._address [B
      // 070: goto 015
      // 073: aload 2
      // 074: aload 3
      // 075: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readString (Lnet/rim/device/api/util/DataBuffer;)Ljava/lang/String;
      // 078: putfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._name Ljava/lang/String;
      // 07b: goto 015
      // 07e: aload 2
      // 07f: aload 3
      // 080: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readString (Lnet/rim/device/api/util/DataBuffer;)Ljava/lang/String;
      // 083: putfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._friendlyName Ljava/lang/String;
      // 086: goto 015
      // 089: aload 2
      // 08a: aload 3
      // 08b: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 08e: putfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._deviceClass I
      // 091: goto 015
      // 094: aload 3
      // 095: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 098: istore 5
      // 09a: aload 2
      // 09b: iload 5
      // 09d: bipush 1
      // 09e: iand
      // 09f: ifeq 0a6
      // 0a2: bipush 1
      // 0a3: goto 0a7
      // 0a6: bipush 0
      // 0a7: putfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._authorized I
      // 0aa: aload 2
      // 0ab: iload 5
      // 0ad: bipush 2
      // 0af: iand
      // 0b0: ifeq 0b7
      // 0b3: bipush 1
      // 0b4: goto 0b8
      // 0b7: bipush 0
      // 0b8: putfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._encrypt Z
      // 0bb: aload 2
      // 0bc: iload 5
      // 0be: bipush 4
      // 0c0: iand
      // 0c1: ifeq 0c8
      // 0c4: bipush 1
      // 0c5: goto 0c9
      // 0c8: bipush 0
      // 0c9: putfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._sniffModeEnabled Z
      // 0cc: goto 015
      // 0cf: aload 2
      // 0d0: new net/rim/device/apps/internal/bluetooth/RemoteServiceRecord
      // 0d3: dup
      // 0d4: aload 2
      // 0d5: getfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._address [B
      // 0d8: aload 3
      // 0d9: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 0dc: invokespecial net/rim/device/apps/internal/bluetooth/RemoteServiceRecord.<init> ([B[B)V
      // 0df: invokevirtual net/rim/device/apps/internal/bluetooth/BluetoothDeviceData.addServiceRecord (Lnet/rim/device/apps/internal/bluetooth/RemoteServiceRecord;)V
      // 0e2: goto 015
      // 0e5: astore 6
      // 0e7: goto 015
      // 0ea: aload 2
      // 0eb: aload 3
      // 0ec: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 0ef: putfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._supportedProfiles I
      // 0f2: goto 015
      // 0f5: aload 2
      // 0f6: aload 3
      // 0f7: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 0fa: putfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._linkKey [B
      // 0fd: goto 015
      // 100: aload 2
      // 101: aload 3
      // 102: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 105: putfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._linkKeyType I
      // 108: goto 015
      // 10b: aload 2
      // 10c: aload 3
      // 10d: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 110: putfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._authorized I
      // 113: goto 015
      // 116: aload 2
      // 117: aload 3
      // 118: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 11b: putfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._nrecMode I
      // 11e: goto 015
      // 121: aload 3
      // 122: invokestatic net/rim/device/api/synchronization/ConverterUtilities.skipField (Lnet/rim/device/api/util/DataBuffer;)V
      // 125: goto 015
      // 128: astore 4
      // 12a: ldc_w 1381192774
      // 12d: invokestatic net/rim/device/internal/bluetooth/BluetoothEvents.log (I)V
      // 130: aconst_null
      // 131: areturn
      // 132: aload 2
      // 133: getfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._address [B
      // 136: ifnull 147
      // 139: aload 2
      // 13a: getfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._name Ljava/lang/String;
      // 13d: ifnull 147
      // 140: aload 2
      // 141: getfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._linkKey [B
      // 144: ifnonnull 14f
      // 147: ldc_w 1381192774
      // 14a: invokestatic net/rim/device/internal/bluetooth/BluetoothEvents.log (I)V
      // 14d: aconst_null
      // 14e: areturn
      // 14f: aload 2
      // 150: areturn
      // try (13 -> 16): 17 null
      // try (72 -> 81): 82 null
      // try (13 -> 112): 112 null
   }

   final byte[] serialize(boolean bigEndian) {
      DataBuffer buffer = (DataBuffer)(new Object(bigEndian));
      ConverterUtilities.writeByteArray(buffer, 0, this._address);
      if (this._name != null) {
         ConverterUtilities.writeString(buffer, 1, this._name);
      }

      if (this._friendlyName != null) {
         ConverterUtilities.writeString(buffer, 2, this._friendlyName);
      }

      ConverterUtilities.writeInt(buffer, 3, this._deviceClass);
      int flags = 0;
      if (this._authorized == 1) {
         flags |= 1;
      }

      if (this._encrypt) {
         flags |= 2;
      }

      if (this._sniffModeEnabled) {
         flags |= 4;
      }

      ConverterUtilities.writeInt(buffer, 6, flags);
      synchronized (this._serviceRecords) {
         for (int i = 0; i < this._serviceRecords.length; i++) {
            ConverterUtilities.writeByteArray(buffer, 7, this._serviceRecords[i].serialize());
         }
      }

      ConverterUtilities.writeInt(buffer, 5, this._supportedProfiles);
      ConverterUtilities.writeByteArray(buffer, 8, this._linkKey);
      ConverterUtilities.writeInt(buffer, 9, this._linkKeyType);
      ConverterUtilities.writeInt(buffer, 10, this._authorized);
      ConverterUtilities.writeInt(buffer, 11, this._nrecMode);
      return buffer.toArray();
   }
}
