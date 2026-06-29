package net.rim.device.cldc.impl.softtoken;

import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.enterpriseconfig.EnterpriseConfig;
import net.rim.device.api.enterpriseconfig.EnterpriseConfigRecord;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.cldc.impl.api.SoftToken;
import net.rim.device.cldc.impl.api.SoftTokenDialog;
import net.rim.device.cldc.impl.api.SoftTokenListener;
import net.rim.device.cldc.impl.api.SoftTokenManager;

public final class SoftTokenManagerImpl extends SoftTokenManager implements PersistentContentListener, CollectionListener, RealtimeClockListener {
   private PersistentObject _persistentObject;
   private boolean _softTokensLoaded;
   private Vector _softTokens;
   private Object[] _listeners;
   EnterpriseConfig _enterpriseConfig;
   private static ResourceBundle _rb = ResourceBundle.getBundle(390461180289309471L, "net.rim.device.internal.resource.SoftTokenResources");
   private static final long SOFT_TOKEN_STORE_ID = 5330586314080255177L;
   private static final int ERROR_CONTROLLED_ACCESS_EXCEPTION = 1162035525;
   private static final byte VERSION = 0;
   private static final byte TOKEN_TAG = 1;
   private static final byte SERIAL_NUM_TAG = 2;
   private static final byte PASSPHRASE_TAG = 3;
   private static final byte PIN_TAG = 4;
   private static final byte PIN_CACHE_TIMEOUT_TAG = 5;
   private static final byte PIN_CACHE_MILLIS_TAG = 6;
   private static final byte READ_ONLY_TAG = 7;
   private static final byte ENTERPRISE_CONFIG_ID_TAG = 8;

   public final boolean saveSecrets() {
      DataBuffer buffer = new DataBuffer();
      buffer.writeByte(0);

      for (int i = 0; i < this._softTokens.size(); i++) {
         buffer.write(1);
         SoftTokenImpl token = (SoftTokenImpl)this._softTokens.elementAt(i);
         if (token._serialNum != null) {
            buffer.writeByte(2);
            buffer.writeByteArray(token._serialNum.getBytes());
         }

         if (token._passphrase != null) {
            buffer.writeByte(3);
            buffer.writeByteArray(token._passphrase);
         }

         if (token.getPIN().length() > 0) {
            buffer.writeByte(4);
            buffer.writeByteArray(token._pin);
         }

         buffer.writeByte(5);
         buffer.writeCompressedInt(4);
         buffer.writeInt(token._pinCacheTimeout);
         buffer.writeByte(6);
         buffer.writeCompressedInt(8);
         buffer.writeLong(token._pinCacheMillis);
         buffer.writeByte(7);
         buffer.writeCompressedInt(1);
         buffer.writeByte(token._readOnly ? 1 : 0);
         if (token._enterpriseConfigID != null) {
            buffer.writeByte(8);
            buffer.writeByteArray(token._enterpriseConfigID.getBytes());
         }
      }

      Object encoding = PersistentContent.encode(buffer.toArray(), true, true);

      try {
         this._persistentObject.setContents(encoding, 51);
      } finally {
         ;
      }

      this._persistentObject.commit();
      return true;
   }

   @Override
   public final void clockUpdated() {
      long currentTime = System.currentTimeMillis();
      boolean changesMade = false;

      for (int i = this._softTokens.size() - 1; i >= 0; i--) {
         SoftTokenImpl softToken = (SoftTokenImpl)this._softTokens.elementAt(i);
         if (softToken.getPIN().length() > 0 && softToken._pinCacheTimeout > 0 && softToken._pinCacheMillis >= 0) {
            long expiryTime = 60000 * softToken._pinCacheTimeout + softToken._pinCacheMillis;
            if (currentTime - expiryTime >= 0) {
               softToken.setPIN(null);
               changesMade = true;
            }
         }
      }

      if (changesMade) {
         this.saveSecrets();
      }
   }

   @Override
   public final void persistentContentStateChanged(int state) {
      if (state == 1 && !this._softTokensLoaded) {
         this.load();
         this.notifyListeners();
      }
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
   }

   @Override
   public final void reset(Collection collection) {
      this.load();
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      SoftTokenImpl token = this.readRecord((EnterpriseConfigRecord)element);
      if (token != null) {
         this.save(token.getSeed(), token.getSeedPassword(), token._pinCacheTimeout, true, token.getSerialNum());
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      SoftTokenImpl oldToken = this.readRecord((EnterpriseConfigRecord)oldElement);
      if (oldToken != null) {
         this.delete(oldToken._serialNum, false);
      }

      SoftTokenImpl newToken = this.readRecord((EnterpriseConfigRecord)newElement);
      if (newToken != null) {
         this.save(newToken.getSeed(), newToken.getSeedPassword(), newToken._pinCacheTimeout, true, newToken.getSerialNum());
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      SoftTokenImpl token = this.readRecord((EnterpriseConfigRecord)element);
      if (token != null) {
         this.delete(token._serialNum, false);
      }
   }

   private final Vector load() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: new java/util/Vector
      // 003: dup
      // 004: invokespecial java/util/Vector.<init> ()V
      // 007: astore 1
      // 008: new net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib
      // 00b: dup
      // 00c: invokespecial net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.<init> ()V
      // 00f: astore 2
      // 010: goto 031
      // 013: astore 3
      // 014: ldc_w 1162035525
      // 017: aload 3
      // 018: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 01b: bipush 2
      // 01d: invokestatic net/rim/device/cldc/impl/api/SoftTokenManager.logEvent (ILjava/lang/String;I)V
      // 020: aload 1
      // 021: areturn
      // 022: astore 3
      // 023: ldc_w 1162035525
      // 026: aload 3
      // 027: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 02a: bipush 2
      // 02c: invokestatic net/rim/device/cldc/impl/api/SoftTokenManager.logEvent (ILjava/lang/String;I)V
      // 02f: aload 1
      // 030: areturn
      // 031: aload 0
      // 032: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 035: invokevirtual java/util/Vector.removeAllElements ()V
      // 038: bipush 0
      // 039: istore 3
      // 03a: aload 0
      // 03b: ldc2_w 5330586314080255177
      // 03e: invokestatic net/rim/device/api/system/RIMPersistentStore.getPersistentObject (J)Lnet/rim/device/api/system/PersistentObject;
      // 041: putfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._persistentObject Lnet/rim/device/api/system/PersistentObject;
      // 044: aload 0
      // 045: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._persistentObject Lnet/rim/device/api/system/PersistentObject;
      // 048: invokevirtual net/rim/device/api/system/PersistentObject.getContents ()Ljava/lang/Object;
      // 04b: invokestatic net/rim/device/api/system/PersistentContent.decodeByteArray (Ljava/lang/Object;)[B
      // 04e: checkcast [B
      // 051: astore 4
      // 053: goto 05d
      // 056: astore 5
      // 058: aload 0
      // 059: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 05c: areturn
      // 05d: bipush 0
      // 05e: istore 5
      // 060: iload 5
      // 062: aload 2
      // 063: pop
      // 064: bipush 10
      // 066: if_icmpge 0ab
      // 069: aload 2
      // 06a: iload 5
      // 06c: invokevirtual net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.getTokenName (I)Ljava/lang/String;
      // 06f: astore 6
      // 071: aload 6
      // 073: ifnull 0a5
      // 076: aload 6
      // 078: ldc_w "demo"
      // 07b: invokevirtual java/lang/String.equalsIgnoreCase (Ljava/lang/String;)Z
      // 07e: ifne 0a5
      // 081: aload 0
      // 082: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 085: new net/rim/device/cldc/impl/softtoken/SoftTokenImpl
      // 088: dup
      // 089: iload 5
      // 08b: aload 6
      // 08d: aload 2
      // 08e: iload 5
      // 090: invokevirtual net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.getTokenSerialNo (I)Ljava/lang/String;
      // 093: ldc_w ""
      // 096: bipush -1
      // 098: bipush 0
      // 099: aconst_null
      // 09a: invokespecial net/rim/device/cldc/impl/softtoken/SoftTokenImpl.<init> (ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;IZLjava/lang/String;)V
      // 09d: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 0a0: goto 0a5
      // 0a3: astore 6
      // 0a5: iinc 5 1
      // 0a8: goto 060
      // 0ab: aload 4
      // 0ad: ifnonnull 0b5
      // 0b0: bipush 0
      // 0b1: newarray 8
      // 0b3: astore 4
      // 0b5: new net/rim/device/api/util/DataBuffer
      // 0b8: dup
      // 0b9: aload 4
      // 0bb: bipush 0
      // 0bc: aload 4
      // 0be: arraylength
      // 0bf: bipush 1
      // 0c0: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 0c3: astore 5
      // 0c5: aload 5
      // 0c7: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 0ca: istore 6
      // 0cc: goto 0d6
      // 0cf: astore 7
      // 0d1: aload 0
      // 0d2: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 0d5: areturn
      // 0d6: iload 6
      // 0d8: sipush 240
      // 0db: iand
      // 0dc: ifeq 0e2
      // 0df: goto 2a7
      // 0e2: aconst_null
      // 0e3: astore 7
      // 0e5: aload 5
      // 0e7: invokevirtual net/rim/device/api/util/DataBuffer.eof ()Z
      // 0ea: ifeq 0f0
      // 0ed: goto 29c
      // 0f0: aload 5
      // 0f2: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 0f5: istore 8
      // 0f7: iload 8
      // 0f9: bipush 1
      // 0fa: if_icmpne 11e
      // 0fd: aload 7
      // 0ff: ifnull 108
      // 102: aload 1
      // 103: aload 7
      // 105: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 108: new net/rim/device/cldc/impl/softtoken/SoftTokenImpl
      // 10b: dup
      // 10c: bipush 0
      // 10d: aconst_null
      // 10e: aconst_null
      // 10f: ldc_w ""
      // 112: bipush -1
      // 114: bipush 0
      // 115: aconst_null
      // 116: invokespecial net/rim/device/cldc/impl/softtoken/SoftTokenImpl.<init> (ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;IZLjava/lang/String;)V
      // 119: astore 7
      // 11b: goto 0e5
      // 11e: aload 5
      // 120: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 123: istore 9
      // 125: iload 9
      // 127: ifgt 12d
      // 12a: goto 0e5
      // 12d: iload 9
      // 12f: aload 5
      // 131: invokevirtual net/rim/device/api/util/DataBuffer.getLength ()I
      // 134: aload 5
      // 136: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 139: isub
      // 13a: if_icmple 142
      // 13d: bipush 1
      // 13e: istore 3
      // 13f: goto 29c
      // 142: iload 9
      // 144: newarray 8
      // 146: astore 10
      // 148: aload 5
      // 14a: aload 10
      // 14c: bipush 0
      // 14d: aload 10
      // 14f: arraylength
      // 150: invokevirtual net/rim/device/api/util/DataBuffer.read ([BII)I
      // 153: aload 10
      // 155: arraylength
      // 156: if_icmpeq 15e
      // 159: bipush 1
      // 15a: istore 3
      // 15b: goto 29c
      // 15e: iload 8
      // 160: tableswitch 48 1 8 -123 48 65 82 99 153 275 295
      // 190: aload 7
      // 192: new java/lang/String
      // 195: dup
      // 196: aload 10
      // 198: invokespecial java/lang/String.<init> ([B)V
      // 19b: putfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._serialNum Ljava/lang/String;
      // 19e: goto 0e5
      // 1a1: aload 7
      // 1a3: new java/lang/String
      // 1a6: dup
      // 1a7: aload 10
      // 1a9: invokespecial java/lang/String.<init> ([B)V
      // 1ac: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.setPassphrase (Ljava/lang/String;)V
      // 1af: goto 0e5
      // 1b2: aload 7
      // 1b4: new java/lang/String
      // 1b7: dup
      // 1b8: aload 10
      // 1ba: invokespecial java/lang/String.<init> ([B)V
      // 1bd: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.setPIN (Ljava/lang/String;)V
      // 1c0: goto 0e5
      // 1c3: aload 7
      // 1c5: aload 10
      // 1c7: bipush 0
      // 1c8: baload
      // 1c9: sipush 255
      // 1cc: iand
      // 1cd: bipush 24
      // 1cf: ishl
      // 1d0: aload 10
      // 1d2: bipush 1
      // 1d3: baload
      // 1d4: sipush 255
      // 1d7: iand
      // 1d8: bipush 16
      // 1da: ishl
      // 1db: ior
      // 1dc: aload 10
      // 1de: bipush 2
      // 1e0: baload
      // 1e1: sipush 255
      // 1e4: iand
      // 1e5: bipush 8
      // 1e7: ishl
      // 1e8: ior
      // 1e9: aload 10
      // 1eb: bipush 3
      // 1ed: baload
      // 1ee: sipush 255
      // 1f1: iand
      // 1f2: ior
      // 1f3: putfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._pinCacheTimeout I
      // 1f6: goto 0e5
      // 1f9: aload 7
      // 1fb: aload 10
      // 1fd: bipush 0
      // 1fe: baload
      // 1ff: i2l
      // 200: sipush 255
      // 203: i2l
      // 204: land
      // 205: bipush 56
      // 207: lshl
      // 208: aload 10
      // 20a: bipush 1
      // 20b: baload
      // 20c: i2l
      // 20d: sipush 255
      // 210: i2l
      // 211: land
      // 212: bipush 48
      // 214: lshl
      // 215: lor
      // 216: aload 10
      // 218: bipush 2
      // 21a: baload
      // 21b: i2l
      // 21c: sipush 255
      // 21f: i2l
      // 220: land
      // 221: bipush 40
      // 223: lshl
      // 224: lor
      // 225: aload 10
      // 227: bipush 3
      // 229: baload
      // 22a: i2l
      // 22b: sipush 255
      // 22e: i2l
      // 22f: land
      // 230: bipush 32
      // 232: lshl
      // 233: lor
      // 234: aload 10
      // 236: bipush 4
      // 238: baload
      // 239: i2l
      // 23a: sipush 255
      // 23d: i2l
      // 23e: land
      // 23f: bipush 24
      // 241: lshl
      // 242: lor
      // 243: aload 10
      // 245: bipush 5
      // 247: baload
      // 248: i2l
      // 249: sipush 255
      // 24c: i2l
      // 24d: land
      // 24e: bipush 16
      // 250: lshl
      // 251: lor
      // 252: aload 10
      // 254: bipush 6
      // 256: baload
      // 257: i2l
      // 258: sipush 255
      // 25b: i2l
      // 25c: land
      // 25d: bipush 8
      // 25f: lshl
      // 260: lor
      // 261: aload 10
      // 263: bipush 7
      // 265: baload
      // 266: i2l
      // 267: sipush 255
      // 26a: i2l
      // 26b: land
      // 26c: lor
      // 26d: putfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._pinCacheMillis J
      // 270: goto 0e5
      // 273: aload 7
      // 275: aload 10
      // 277: bipush 0
      // 278: baload
      // 279: ifeq 280
      // 27c: bipush 1
      // 27d: goto 281
      // 280: bipush 0
      // 281: putfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._readOnly Z
      // 284: goto 0e5
      // 287: aload 7
      // 289: new java/lang/String
      // 28c: dup
      // 28d: aload 10
      // 28f: invokespecial java/lang/String.<init> ([B)V
      // 292: putfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._enterpriseConfigID Ljava/lang/String;
      // 295: goto 0e5
      // 298: astore 8
      // 29a: bipush 1
      // 29b: istore 3
      // 29c: aload 7
      // 29e: ifnull 2a7
      // 2a1: aload 1
      // 2a2: aload 7
      // 2a4: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 2a7: iload 3
      // 2a8: ifeq 2ae
      // 2ab: goto 356
      // 2ae: bipush 0
      // 2af: istore 7
      // 2b1: iload 7
      // 2b3: aload 0
      // 2b4: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 2b7: invokevirtual java/util/Vector.size ()I
      // 2ba: if_icmplt 2c0
      // 2bd: goto 362
      // 2c0: bipush 0
      // 2c1: istore 8
      // 2c3: iload 8
      // 2c5: aload 1
      // 2c6: invokevirtual java/util/Vector.size ()I
      // 2c9: if_icmpge 350
      // 2cc: aload 0
      // 2cd: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 2d0: iload 7
      // 2d2: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 2d5: checkcast net/rim/device/cldc/impl/softtoken/SoftTokenImpl
      // 2d8: astore 9
      // 2da: aload 1
      // 2db: iload 8
      // 2dd: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 2e0: checkcast net/rim/device/cldc/impl/softtoken/SoftTokenImpl
      // 2e3: astore 10
      // 2e5: aload 9
      // 2e7: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._serialNum Ljava/lang/String;
      // 2ea: ifnull 34a
      // 2ed: aload 10
      // 2ef: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._serialNum Ljava/lang/String;
      // 2f2: ifnull 34a
      // 2f5: aload 9
      // 2f7: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._serialNum Ljava/lang/String;
      // 2fa: aload 10
      // 2fc: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._serialNum Ljava/lang/String;
      // 2ff: invokevirtual java/lang/String.equalsIgnoreCase (Ljava/lang/String;)Z
      // 302: ifeq 34a
      // 305: aload 9
      // 307: aload 10
      // 309: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.getPassphrase ()Ljava/lang/String;
      // 30c: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.setPassphrase (Ljava/lang/String;)V
      // 30f: aload 9
      // 311: aload 10
      // 313: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.getPIN ()Ljava/lang/String;
      // 316: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.setPIN (Ljava/lang/String;)V
      // 319: aload 9
      // 31b: aload 10
      // 31d: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._pinCacheTimeout I
      // 320: putfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._pinCacheTimeout I
      // 323: aload 9
      // 325: aload 10
      // 327: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._pinCacheMillis J
      // 32a: putfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._pinCacheMillis J
      // 32d: aload 9
      // 32f: aload 10
      // 331: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._readOnly Z
      // 334: putfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._readOnly Z
      // 337: aload 9
      // 339: aload 10
      // 33b: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._enterpriseConfigID Ljava/lang/String;
      // 33e: putfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._enterpriseConfigID Ljava/lang/String;
      // 341: aload 1
      // 342: iload 8
      // 344: invokevirtual java/util/Vector.removeElementAt (I)V
      // 347: goto 350
      // 34a: iinc 8 1
      // 34d: goto 2c3
      // 350: iinc 7 1
      // 353: goto 2b1
      // 356: ldc2_w -334688660027749397
      // 359: ldc_w 1162892116
      // 35c: bipush 2
      // 35e: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 361: pop
      // 362: aload 0
      // 363: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._enterpriseConfig Lnet/rim/device/api/enterpriseconfig/EnterpriseConfig;
      // 366: bipush 1
      // 367: invokeinterface net/rim/device/api/enterpriseconfig/EnterpriseConfig.getRecordsByTableId (B)[Lnet/rim/device/api/enterpriseconfig/EnterpriseConfigRecord; 2
      // 36c: astore 7
      // 36e: aload 1
      // 36f: invokevirtual java/util/Vector.removeAllElements ()V
      // 372: bipush 0
      // 373: istore 8
      // 375: iload 8
      // 377: aload 7
      // 379: arraylength
      // 37a: if_icmpge 3fe
      // 37d: aload 0
      // 37e: aload 7
      // 380: iload 8
      // 382: aaload
      // 383: invokespecial net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl.readRecord (Lnet/rim/device/api/enterpriseconfig/EnterpriseConfigRecord;)Lnet/rim/device/cldc/impl/softtoken/SoftTokenImpl;
      // 386: astore 9
      // 388: aload 9
      // 38a: ifnull 3f8
      // 38d: bipush 0
      // 38e: istore 10
      // 390: bipush 0
      // 391: istore 11
      // 393: iload 11
      // 395: aload 0
      // 396: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 399: invokevirtual java/util/Vector.size ()I
      // 39c: if_icmpge 3d9
      // 39f: aload 0
      // 3a0: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 3a3: iload 11
      // 3a5: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 3a8: checkcast net/rim/device/cldc/impl/softtoken/SoftTokenImpl
      // 3ab: astore 12
      // 3ad: aload 12
      // 3af: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._serialNum Ljava/lang/String;
      // 3b2: ifnull 3d3
      // 3b5: aload 9
      // 3b7: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._serialNum Ljava/lang/String;
      // 3ba: ifnull 3d3
      // 3bd: aload 12
      // 3bf: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._serialNum Ljava/lang/String;
      // 3c2: aload 9
      // 3c4: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._serialNum Ljava/lang/String;
      // 3c7: invokevirtual java/lang/String.equalsIgnoreCase (Ljava/lang/String;)Z
      // 3ca: ifeq 3d3
      // 3cd: bipush 1
      // 3ce: istore 10
      // 3d0: goto 3d9
      // 3d3: iinc 11 1
      // 3d6: goto 393
      // 3d9: iload 10
      // 3db: ifne 3f8
      // 3de: aload 0
      // 3df: aload 9
      // 3e1: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.getSeed ()Ljava/lang/String;
      // 3e4: aload 9
      // 3e6: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.getSeedPassword ()Ljava/lang/String;
      // 3e9: aload 9
      // 3eb: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._pinCacheTimeout I
      // 3ee: bipush 1
      // 3ef: aload 9
      // 3f1: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.getSerialNum ()Ljava/lang/String;
      // 3f4: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl.save (Ljava/lang/String;Ljava/lang/String;IZLjava/lang/String;)I
      // 3f7: pop
      // 3f8: iinc 8 1
      // 3fb: goto 375
      // 3fe: aload 0
      // 3ff: bipush 1
      // 400: putfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokensLoaded Z
      // 403: aload 0
      // 404: invokespecial net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl.notifyListeners ()V
      // 407: aload 0
      // 408: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 40b: areturn
      // try (4 -> 8): 9 null
      // try (4 -> 8): 17 null
      // try (34 -> 40): 41 null
      // try (52 -> 77): 78 null
      // try (95 -> 98): 99 null
      // try (114 -> 142): 320 null
      // try (143 -> 152): 320 null
      // try (153 -> 167): 320 null
      // try (168 -> 319): 320 null
   }

   @Override
   public final Vector getSoftTokens() {
      return this._softTokens;
   }

   @Override
   public final String[] getSoftTokenSerialNums() {
      if (this._softTokens.size() <= 0) {
         return null;
      }

      String[] tokenSerialNums = new String[this._softTokens.size()];

      for (int i = 0; i < this._softTokens.size(); i++) {
         tokenSerialNums[i] = ((SoftTokenImpl)this._softTokens.elementAt(i))._serialNum;
      }

      return tokenSerialNums;
   }

   @Override
   public final SoftToken getSoftToken(String serialNum) {
      synchronized (this._softTokens) {
         if (serialNum != null) {
            for (int i = this._softTokens.size() - 1; i >= 0; i--) {
               SoftTokenImpl softToken = (SoftTokenImpl)this._softTokens.elementAt(i);
               if (softToken._serialNum != null && softToken._serialNum.equalsIgnoreCase(serialNum)
                  || softToken._enterpriseConfigID != null && softToken._enterpriseConfigID.equalsIgnoreCase(serialNum)) {
                  return softToken;
               }
            }
         }

         return null;
      }
   }

   @Override
   public final boolean delete(String param1, boolean param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: new net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib
      // 03: dup
      // 04: invokespecial net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.<init> ()V
      // 07: astore 3
      // 08: goto 2d
      // 0b: astore 4
      // 0d: ldc_w 1162035525
      // 10: aload 4
      // 12: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 15: bipush 2
      // 17: invokestatic net/rim/device/cldc/impl/api/SoftTokenManager.logEvent (ILjava/lang/String;I)V
      // 1a: bipush 0
      // 1b: ireturn
      // 1c: astore 4
      // 1e: ldc_w 1162035525
      // 21: aload 4
      // 23: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 26: bipush 2
      // 28: invokestatic net/rim/device/cldc/impl/api/SoftTokenManager.logEvent (ILjava/lang/String;I)V
      // 2b: bipush 0
      // 2c: ireturn
      // 2d: aload 0
      // 2e: aload 1
      // 2f: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl.getSoftToken (Ljava/lang/String;)Lnet/rim/device/cldc/impl/api/SoftToken;
      // 32: checkcast net/rim/device/cldc/impl/softtoken/SoftTokenImpl
      // 35: astore 4
      // 37: aload 4
      // 39: ifnonnull 3e
      // 3c: bipush 0
      // 3d: ireturn
      // 3e: iload 2
      // 3f: ifeq 4a
      // 42: aload 4
      // 44: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._readOnly Z
      // 47: ifne 5b
      // 4a: aload 3
      // 4b: aload 4
      // 4d: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._tag I
      // 50: aload 4
      // 52: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.getSerialNum ()Ljava/lang/String;
      // 55: invokevirtual net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.deleteToken (ILjava/lang/String;)V
      // 58: goto 5d
      // 5b: bipush 0
      // 5c: ireturn
      // 5d: aload 0
      // 5e: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 61: dup
      // 62: astore 5
      // 64: monitorenter
      // 65: aload 0
      // 66: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 69: invokevirtual java/util/Vector.size ()I
      // 6c: bipush 1
      // 6d: isub
      // 6e: istore 6
      // 70: iload 6
      // 72: iflt b4
      // 75: aload 0
      // 76: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 79: iload 6
      // 7b: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 7e: checkcast net/rim/device/cldc/impl/softtoken/SoftTokenImpl
      // 81: astore 7
      // 83: aload 7
      // 85: aload 4
      // 87: if_acmpne 96
      // 8a: aload 0
      // 8b: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 8e: iload 6
      // 90: invokevirtual java/util/Vector.removeElementAt (I)V
      // 93: goto ae
      // 96: aload 7
      // 98: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._tag I
      // 9b: aload 4
      // 9d: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._tag I
      // a0: if_icmple ae
      // a3: aload 7
      // a5: dup
      // a6: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._tag I
      // a9: bipush 1
      // aa: isub
      // ab: putfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._tag I
      // ae: iinc 6 -1
      // b1: goto 70
      // b4: aload 0
      // b5: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl.saveSecrets ()Z
      // b8: pop
      // b9: ldc_w 1397970005
      // bc: bipush 0
      // bd: invokestatic net/rim/device/cldc/impl/api/SoftTokenManager.logEvent (II)V
      // c0: aload 0
      // c1: invokespecial net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl.notifyListeners ()V
      // c4: aload 5
      // c6: monitorexit
      // c7: goto d2
      // ca: astore 8
      // cc: aload 5
      // ce: monitorexit
      // cf: aload 8
      // d1: athrow
      // d2: bipush 1
      // d3: ireturn
      // d4: astore 5
      // d6: ldc_w 1163220308
      // d9: bipush 2
      // db: invokestatic net/rim/device/cldc/impl/api/SoftTokenManager.logEvent (II)V
      // de: bipush 0
      // df: ireturn
      // try (0 -> 4): 5 null
      // try (0 -> 4): 13 null
      // try (49 -> 94): 95 null
      // try (95 -> 98): 95 null
      // try (30 -> 43): 102 null
      // try (44 -> 101): 102 null
   }

   @Override
   public final String showDialog(String tokenSerialNum, String dialogTitle, boolean showPromptResponseField, boolean showUserNameField, String username) {
      SoftTokenDialog softTokenDialog = new SoftTokenDialogImpl(tokenSerialNum, dialogTitle, showPromptResponseField, showUserNameField, username);
      synchronized (Application.getEventLock()) {
         softTokenDialog.show();
      }

      return softTokenDialog.getChallengeResponse();
   }

   @Override
   public final SoftTokenDialog getDialog(
      String tokenSerialNum, String dialogTitle, boolean showPromptResponseField, boolean showUserNameField, String username
   ) {
      SoftTokenDialogImpl dialog = new SoftTokenDialogImpl(tokenSerialNum, dialogTitle, showPromptResponseField, showUserNameField, username);
      return dialog.getSoftToken() != null ? dialog : null;
   }

   private final SoftTokenImpl readRecord(EnterpriseConfigRecord param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
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
      // 00: aload 1
      // 01: invokeinterface net/rim/device/api/enterpriseconfig/EnterpriseConfigRecord.getTableId ()B 1
      // 06: bipush 1
      // 07: if_icmpeq 0c
      // 0a: aconst_null
      // 0b: areturn
      // 0c: aload 1
      // 0d: invokeinterface net/rim/device/api/enterpriseconfig/EnterpriseConfigRecord.getData ()Lnet/rim/device/api/util/DataBuffer; 1
      // 12: astore 2
      // 13: aload 2
      // 14: ifnull 1e
      // 17: aload 2
      // 18: invokevirtual net/rim/device/api/util/DataBuffer.getLength ()I
      // 1b: ifgt 20
      // 1e: aconst_null
      // 1f: areturn
      // 20: new net/rim/device/cldc/impl/softtoken/SoftTokenImpl
      // 23: dup
      // 24: bipush 0
      // 25: aconst_null
      // 26: aconst_null
      // 27: ldc_w ""
      // 2a: bipush -1
      // 2c: bipush 0
      // 2d: aconst_null
      // 2e: invokespecial net/rim/device/cldc/impl/softtoken/SoftTokenImpl.<init> (ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;IZLjava/lang/String;)V
      // 31: astore 3
      // 32: aload 2
      // 33: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 36: istore 4
      // 38: aload 2
      // 39: invokevirtual net/rim/device/api/util/DataBuffer.rewind ()V
      // 3c: aload 2
      // 3d: invokevirtual net/rim/device/api/util/DataBuffer.eof ()Z
      // 40: ifne 9f
      // 43: aload 2
      // 44: invokestatic net/rim/device/api/synchronization/ConverterUtilities.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 47: istore 5
      // 49: iload 5
      // 4b: tableswitch 33 0 4 77 33 44 55 66
      // 6c: aload 3
      // 6d: aload 2
      // 6e: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 71: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.setSeed ([B)V
      // 74: goto 3c
      // 77: aload 3
      // 78: aload 2
      // 79: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readString (Lnet/rim/device/api/util/DataBuffer;)Ljava/lang/String;
      // 7c: putfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._serialNum Ljava/lang/String;
      // 7f: goto 3c
      // 82: aload 3
      // 83: aload 2
      // 84: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readString (Lnet/rim/device/api/util/DataBuffer;)Ljava/lang/String;
      // 87: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.setSeedPassword (Ljava/lang/String;)V
      // 8a: goto 3c
      // 8d: aload 3
      // 8e: aload 2
      // 8f: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 92: putfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._pinCacheTimeout I
      // 95: goto 3c
      // 98: aload 2
      // 99: invokestatic net/rim/device/api/synchronization/ConverterUtilities.skipField (Lnet/rim/device/api/util/DataBuffer;)V
      // 9c: goto 3c
      // 9f: aload 2
      // a0: iload 4
      // a2: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // a5: aload 3
      // a6: areturn
      // a7: astore 5
      // a9: aload 2
      // aa: iload 4
      // ac: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // af: aload 3
      // b0: areturn
      // b1: astore 6
      // b3: aload 2
      // b4: iload 4
      // b6: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // b9: aload 6
      // bb: athrow
      // try (32 -> 63): 68 null
      // try (32 -> 63): 74 null
      // try (68 -> 69): 74 null
      // try (74 -> 75): 74 null
   }

   @Override
   public final void addListener(SoftTokenListener listener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, listener);
   }

   @Override
   public final void removeListener(SoftTokenListener listener) {
      this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
   }

   private final void notifyListeners() {
      if (this._listeners != null) {
         for (int i = 0; i < this._listeners.length; i++) {
            ((SoftTokenListener)this._listeners[i]).softTokensUpdated();
         }
      }
   }

   private SoftTokenManagerImpl() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial net/rim/device/cldc/impl/api/SoftTokenManager.<init> ()V
      // 04: ldc2_w -334688660027749397
      // 07: ldc_w "net.rim.softtokens"
      // 0a: bipush 2
      // 0c: invokestatic net/rim/device/api/system/EventLogger.register (JLjava/lang/String;I)Z
      // 0f: pop
      // 10: aload 0
      // 11: new java/util/Vector
      // 14: dup
      // 15: invokespecial java/util/Vector.<init> ()V
      // 18: putfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 1b: invokestatic net/rim/device/api/system/ApplicationRegistry.getApplicationRegistry ()Lnet/rim/device/api/system/ApplicationRegistry;
      // 1e: astore 1
      // 1f: aload 0
      // 20: aload 1
      // 21: ldc2_w 5083252457608156518
      // 24: invokevirtual net/rim/device/api/system/ApplicationRegistry.waitFor (J)Ljava/lang/Object;
      // 27: checkcast net/rim/device/api/enterpriseconfig/EnterpriseConfig
      // 2a: putfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._enterpriseConfig Lnet/rim/device/api/enterpriseconfig/EnterpriseConfig;
      // 2d: aload 0
      // 2e: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._enterpriseConfig Lnet/rim/device/api/enterpriseconfig/EnterpriseConfig;
      // 31: aload 0
      // 32: invokeinterface net/rim/device/api/collection/CollectionEventSource.addCollectionListener (Ljava/lang/Object;)V 2
      // 37: aload 0
      // 38: invokestatic net/rim/device/api/system/PersistentContent.addListener (Lnet/rim/device/api/system/PersistentContentListener;)V
      // 3b: invokestatic net/rim/device/internal/proxy/Proxy.getInstance ()Lnet/rim/device/internal/proxy/Proxy;
      // 3e: aload 0
      // 3f: invokevirtual net/rim/device/api/system/Application.addRealtimeClockListener (Lnet/rim/device/api/system/RealtimeClockListener;)V
      // 42: aload 0
      // 43: invokespecial net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl.load ()Ljava/util/Vector;
      // 46: pop
      // 47: aload 1
      // 48: ldc2_w 8929046088285360721
      // 4b: aload 0
      // 4c: invokevirtual net/rim/device/api/system/ApplicationRegistry.put (JLjava/lang/Object;)V
      // 4f: return
      // 50: astore 2
      // 51: ldc_w 1162035525
      // 54: aload 2
      // 55: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 58: bipush 2
      // 5a: invokestatic net/rim/device/cldc/impl/api/SoftTokenManager.logEvent (ILjava/lang/String;I)V
      // 5d: return
      // 5e: astore 2
      // 5f: ldc_w 1162035525
      // 62: aload 2
      // 63: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 66: bipush 2
      // 68: invokestatic net/rim/device/cldc/impl/api/SoftTokenManager.logEvent (ILjava/lang/String;I)V
      // 6b: return
      // try (29 -> 36): 37 null
      // try (29 -> 36): 44 null
   }

   @Override
   public final int save(String param1, String param2, int param3, boolean param4, String param5) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: new net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib
      // 003: dup
      // 004: invokespecial net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.<init> ()V
      // 007: astore 6
      // 009: goto 030
      // 00c: astore 7
      // 00e: ldc_w 1162035525
      // 011: aload 7
      // 013: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 016: bipush 2
      // 018: invokestatic net/rim/device/cldc/impl/api/SoftTokenManager.logEvent (ILjava/lang/String;I)V
      // 01b: bipush -1
      // 01d: ireturn
      // 01e: astore 7
      // 020: ldc_w 1162035525
      // 023: aload 7
      // 025: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 028: bipush 2
      // 02a: invokestatic net/rim/device/cldc/impl/api/SoftTokenManager.logEvent (ILjava/lang/String;I)V
      // 02d: bipush -1
      // 02f: ireturn
      // 030: aload 1
      // 031: ifnull 03b
      // 034: aload 1
      // 035: invokevirtual java/lang/String.length ()I
      // 038: ifne 03e
      // 03b: bipush -1
      // 03d: ireturn
      // 03e: bipush 0
      // 03f: istore 7
      // 041: iload 7
      // 043: aload 6
      // 045: pop
      // 046: bipush 10
      // 048: if_icmpge 066
      // 04b: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 04e: aload 6
      // 050: iload 7
      // 052: invokevirtual net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.getTokenName (I)Ljava/lang/String;
      // 055: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 058: goto 060
      // 05b: astore 8
      // 05d: goto 066
      // 060: iinc 7 1
      // 063: goto 041
      // 066: bipush 0
      // 067: istore 8
      // 069: aload 6
      // 06b: aload 1
      // 06c: aload 2
      // 06d: invokevirtual net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.importToken (Ljava/lang/String;Ljava/lang/String;)V
      // 070: goto 0b8
      // 073: astore 9
      // 075: bipush -4
      // 077: istore 8
      // 079: goto 0b8
      // 07c: astore 9
      // 07e: bipush -6
      // 080: istore 8
      // 082: goto 0b8
      // 085: astore 9
      // 087: bipush -2
      // 089: istore 8
      // 08b: goto 0b8
      // 08e: astore 9
      // 090: bipush -3
      // 092: istore 8
      // 094: goto 0b8
      // 097: astore 9
      // 099: bipush -5
      // 09b: istore 8
      // 09d: goto 0b8
      // 0a0: astore 9
      // 0a2: bipush -7
      // 0a4: istore 8
      // 0a6: goto 0b8
      // 0a9: astore 9
      // 0ab: bipush -8
      // 0ad: istore 8
      // 0af: goto 0b8
      // 0b2: astore 9
      // 0b4: bipush -1
      // 0b6: istore 8
      // 0b8: iload 8
      // 0ba: ifeq 0c0
      // 0bd: goto 180
      // 0c0: aload 6
      // 0c2: iload 7
      // 0c4: new java/lang/StringBuffer
      // 0c7: dup
      // 0c8: invokespecial java/lang/StringBuffer.<init> ()V
      // 0cb: getstatic net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 0ce: bipush 19
      // 0d0: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0d3: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0d6: ldc_w " "
      // 0d9: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0dc: iload 7
      // 0de: bipush 1
      // 0df: iadd
      // 0e0: invokestatic java/lang/Integer.toString (I)Ljava/lang/String;
      // 0e3: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0e6: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0e9: invokevirtual net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.setTokenName (ILjava/lang/String;)V
      // 0ec: goto 0f1
      // 0ef: astore 9
      // 0f1: aload 0
      // 0f2: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 0f5: dup
      // 0f6: astore 9
      // 0f8: monitorenter
      // 0f9: new net/rim/device/cldc/impl/softtoken/SoftTokenImpl
      // 0fc: dup
      // 0fd: iload 7
      // 0ff: aload 6
      // 101: iload 7
      // 103: invokevirtual net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.getTokenName (I)Ljava/lang/String;
      // 106: aload 6
      // 108: iload 7
      // 10a: invokevirtual net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.getTokenSerialNo (I)Ljava/lang/String;
      // 10d: aload 2
      // 10e: iload 3
      // 10f: iload 4
      // 111: aload 5
      // 113: invokespecial net/rim/device/cldc/impl/softtoken/SoftTokenImpl.<init> (ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;IZLjava/lang/String;)V
      // 116: astore 10
      // 118: aload 10
      // 11a: aload 1
      // 11b: invokevirtual java/lang/String.getBytes ()[B
      // 11e: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.setSeed ([B)V
      // 121: aload 0
      // 122: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 125: aload 10
      // 127: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 12a: aload 0
      // 12b: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl.saveSecrets ()Z
      // 12e: ifne 145
      // 131: bipush -1
      // 133: istore 8
      // 135: aload 0
      // 136: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 139: aload 0
      // 13a: getfield net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl._softTokens Ljava/util/Vector;
      // 13d: invokevirtual java/util/Vector.size ()I
      // 140: bipush 1
      // 141: isub
      // 142: invokevirtual java/util/Vector.removeElementAt (I)V
      // 145: aload 10
      // 147: aconst_null
      // 148: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.setSeed ([B)V
      // 14b: aload 9
      // 14d: monitorexit
      // 14e: goto 16e
      // 151: astore 11
      // 153: aload 9
      // 155: monitorexit
      // 156: aload 11
      // 158: athrow
      // 159: astore 9
      // 15b: aload 9
      // 15d: instanceof net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimDecryptFailException
      // 160: ifeq 16a
      // 163: bipush -6
      // 165: istore 8
      // 167: goto 16e
      // 16a: bipush -1
      // 16c: istore 8
      // 16e: iload 8
      // 170: ifeq 180
      // 173: aload 6
      // 175: iload 7
      // 177: aconst_null
      // 178: invokevirtual net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.deleteToken (ILjava/lang/String;)V
      // 17b: goto 180
      // 17e: astore 9
      // 180: iload 8
      // 182: ifne 193
      // 185: aload 0
      // 186: invokespecial net/rim/device/cldc/impl/softtoken/SoftTokenManagerImpl.notifyListeners ()V
      // 189: ldc_w 1397969993
      // 18c: bipush 0
      // 18d: invokestatic net/rim/device/cldc/impl/api/SoftTokenManager.logEvent (II)V
      // 190: goto 1a0
      // 193: ldc_w 1163088969
      // 196: iload 8
      // 198: invokestatic java/lang/String.valueOf (I)Ljava/lang/String;
      // 19b: bipush 2
      // 19d: invokestatic net/rim/device/cldc/impl/api/SoftTokenManager.logEvent (ILjava/lang/String;I)V
      // 1a0: iload 8
      // 1a2: ireturn
      // try (0 -> 4): 5 null
      // try (0 -> 4): 13 null
      // try (35 -> 40): 41 null
      // try (47 -> 51): 52 null
      // try (47 -> 51): 56 null
      // try (47 -> 51): 60 null
      // try (47 -> 51): 64 null
      // try (47 -> 51): 68 null
      // try (47 -> 51): 72 null
      // try (47 -> 51): 76 null
      // try (47 -> 51): 80 null
      // try (86 -> 104): 105 null
      // try (111 -> 152): 153 null
      // try (153 -> 156): 153 null
      // try (106 -> 158): 158 null
      // try (169 -> 173): 174 null
   }

   public static final void libMain(String[] args) {
      new SoftTokenManagerImpl();
   }
}
