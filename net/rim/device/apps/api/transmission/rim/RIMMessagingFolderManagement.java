package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfiguration;

public class RIMMessagingFolderManagement extends RIMMessagingTransmission implements Persistable {
   private DataBuffer _buffer = (DataBuffer)(new Object(true));
   private static final byte FM_SUBCOMMAND_TERMINATOR;
   private static final byte FM_MOVE_MESSAGE;
   private static final byte FM_DELETE_MESSAGE;
   private static final byte FM_PURGE_MESSAGES;
   private static final byte FM_MESSAGE_STATUS;
   private static final byte FM_OTAFM_CONFIG;
   private static final byte FM_MESSAGE_LIST;
   private static final byte FM_FOLDER_LIST;
   private static final byte FM_CREATE_FOLDER;
   private static final byte FM_DELETE_FOLDER;
   private static final byte FM_MODIFY_FOLDER;
   private static final byte FM_MESSAGE_LIST_REQUEST;
   private static final byte FM_FOLDER_LIST_REQUEST;
   private static final byte FM_OTAFM_CONFIG_REQUEST;
   private static final byte FM_RESTORE_MESSAGE_LIST;
   private static final byte FM_OTAFM_CONFIG_ACK;
   private static final byte FM_MESSAGE_LIST_ACK;
   private static final byte FM_SEND_CHANGES_REQUEST;
   private static final byte FM_SEND_PURGED_MESSAGE_LIST;
   private static final byte FM_RIM_MESSAGE_ID;
   private static final byte FM_RIM_CURRENT_FOLDER_ID;
   private static final byte FM_RIM_PREVIOUS_FOLDER_ID;
   private static final byte FM_RIM_MESSAGE_STATUS_ID;
   private static final byte FM_RIM_PARENT_FOLDER_ID;
   private static final byte FM_RIM_FOLDER_NAME;
   private static final byte FM_RIM_FOLDER_TYPE;
   private static final byte FM_RIM_FOLDER_ATTR;
   private static final byte FM_RIM_DELETE_BITMASK;
   private static final byte FM_RIM_MESSAGE_LIST_ID;
   private static final byte FM_RIM_FOLDER_NAME_2;
   public static final byte FM_DELETE_MESSAGES;
   public static final byte FM_DELETE_FOLDERS;
   private static final byte FM_RIM_SERVER_SUPPORT;
   private static final byte FM_RIM_USER_CONFIG;
   private static final byte FM_RIM_CONFLICT;
   private static final byte FM_MESSAGE_LIST_NUM_MESSAGE_STATUS;
   private static final int OTAFM_CONFIG_STATUS_MASK;
   private static final int OTAFM_CONFIG_DELETE_MASK;
   private static final int OTAFM_CONFIG_FILING_MASK;
   private static final int OTAFM_CONFIG_PURGE_MASK;
   private static final int OTAFM_CONFIG_USER_ENABLED;
   private static final int OTAFM_CONFIG_DEVICE_INIT;
   private static final int OTAFM_CONFIG_PURGED_MESSAGE_LIST_MASK;
   private static final int OTAFM_CONFIG_USER_MASK;
   private static final int OTAFM_CONFIG_HANDHELD_WINS;
   private static final int OTAFM_CONFIG_DESKTOP_WINS;
   public static final int FM_MSG_READ;
   public static final int FM_MSG_DELETED_ON_DEVICE;
   public static final int FM_MSG_REPLYTO;
   public static final int FM_MSG_FORWARD;
   public static final int FM_MSG_DELETED;
   public static final int FM_FOLDER_SUBTREE;
   public static final int FM_FOLDER_DELETED_ITEMS;
   public static final int FM_FOLDER_INBOX;
   public static final int FM_FOLDER_OUTBOX;
   public static final int FM_FOLDER_SENT_ITEMS;
   public static final int FM_FOLDER_OTHER;
   public static final int FM_FOLDER_ATTR_LOCAL_FOLDER;
   public static final int FM_FOLDER_ATTR_NO_MESSAGES;
   public static final int FM_FOLDER_ATTR_REDIRECTION_SUPPORTED;
   public static final int FM_FOLDER_ATTR_REDIRECTION_STATUS;

   public RIMMessagingFolderManagement() {
      this(16);
   }

   public RIMMessagingFolderManagement(int size) {
      if (size >= 16) {
         this._buffer.ensureCapacity(size);
      }
   }

   @Override
   public DataBuffer write() {
      DataBuffer result = (DataBuffer)(new Object(true));
      result.writeByte(7);
      synchronized (this._buffer) {
         this._buffer.trim();
         byte[] data = this._buffer.getArray();
         result.write(data, 0, this._buffer.getArrayLength());
      }

      result.writeByte(0);
      return result;
   }

   @Override
   public void read(DataBuffer packetDataBuffer) {
      int length = packetDataBuffer.available();
      if (length <= 0) {
         throw new Object("IL");
      }

      byte[] data = new byte[length];
      packetDataBuffer.read(data);
      this._buffer = (DataBuffer)(new Object(data, 0, length, true));
   }

   public int size() {
      this._buffer.trim();
      return this._buffer.getLength();
   }

   private String getString(boolean isEncoded) {
      int length = this._buffer.readCompressedInt();
      String string = null;

      try {
         string = (String)CMIMEUtilities.getTextObject(this._buffer.getArray(), this._buffer.getArrayPosition(), length, isEncoded, null);
      } finally {
         ;
      }

      this._buffer.skipBytes(length);
      return string;
   }

   private int getInteger() {
      int value = 0;
      byte length = this._buffer.readByte();
      switch (length) {
         case 0:
         case 3:
            throw new Object(((StringBuffer)(new Object("UL="))).append(length).toString());
         case 1:
         default:
            return this._buffer.readByte();
         case 2:
            return this._buffer.readShort();
         case 4:
            return this._buffer.readInt();
      }
   }

   private byte getByte() {
      byte length = this._buffer.readByte();
      if (length != 1) {
         throw new Object(((StringBuffer)(new Object("UL="))).append(length).toString());
      } else {
         return this._buffer.readByte();
      }
   }

   public void processCommands(Object param1, FolderManagementCommandListener param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 004: bipush 0
      // 005: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 008: aload 0
      // 009: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 00c: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 00f: ifgt 015
      // 012: goto 44f
      // 015: aload 0
      // 016: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 019: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 01c: istore 3
      // 01d: iload 3
      // 01e: ifne 024
      // 021: goto 44f
      // 024: bipush 0
      // 025: istore 4
      // 027: bipush 0
      // 028: istore 5
      // 02a: bipush 0
      // 02b: istore 6
      // 02d: bipush 0
      // 02e: istore 7
      // 030: aconst_null
      // 031: astore 8
      // 033: bipush 0
      // 034: istore 9
      // 036: bipush 0
      // 037: istore 10
      // 039: bipush 0
      // 03a: istore 11
      // 03c: bipush 1
      // 03d: istore 12
      // 03f: bipush 0
      // 040: istore 13
      // 042: bipush 0
      // 043: istore 14
      // 045: bipush 0
      // 046: istore 15
      // 048: bipush 0
      // 049: istore 16
      // 04b: bipush 0
      // 04c: istore 17
      // 04e: iload 3
      // 04f: tableswitch 81 0 16 681 81 81 681 81 335 681 442 81 81 81 681 681 681 681 335 81
      // 0a0: aload 0
      // 0a1: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 0a4: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 0a7: ifgt 0ad
      // 0aa: goto 325
      // 0ad: aload 0
      // 0ae: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 0b1: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 0b4: istore 18
      // 0b6: iload 18
      // 0b8: ifne 0be
      // 0bb: goto 325
      // 0be: iload 18
      // 0c0: lookupswitch 204 11 -122 158 1 100 2 109 3 118 4 136 5 127 6 148 7 168 8 177 9 186 10 195
      // 124: aload 0
      // 125: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getInteger ()I
      // 128: istore 4
      // 12a: goto 0a0
      // 12d: aload 0
      // 12e: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getInteger ()I
      // 131: istore 5
      // 133: goto 0a0
      // 136: aload 0
      // 137: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getInteger ()I
      // 13a: istore 6
      // 13c: goto 0a0
      // 13f: aload 0
      // 140: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getInteger ()I
      // 143: istore 7
      // 145: goto 0a0
      // 148: aload 0
      // 149: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getByte ()B
      // 14c: istore 13
      // 14e: bipush 1
      // 14f: istore 14
      // 151: goto 0a0
      // 154: aload 0
      // 155: bipush 0
      // 156: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getString (Z)Ljava/lang/String;
      // 159: astore 8
      // 15b: goto 0a0
      // 15e: aload 0
      // 15f: bipush 1
      // 160: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getString (Z)Ljava/lang/String;
      // 163: astore 8
      // 165: goto 0a0
      // 168: aload 0
      // 169: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getInteger ()I
      // 16c: istore 9
      // 16e: goto 0a0
      // 171: aload 0
      // 172: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getInteger ()I
      // 175: istore 15
      // 177: goto 0a0
      // 17a: aload 0
      // 17b: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getByte ()B
      // 17e: istore 16
      // 180: goto 0a0
      // 183: aload 0
      // 184: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getInteger ()I
      // 187: istore 17
      // 189: goto 0a0
      // 18c: aload 0
      // 18d: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 190: aload 0
      // 191: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 194: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 197: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // 19a: pop
      // 19b: goto 0a0
      // 19e: aload 0
      // 19f: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 1a2: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 1a5: ifgt 1ab
      // 1a8: goto 325
      // 1ab: aload 0
      // 1ac: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 1af: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 1b2: istore 18
      // 1b4: iload 18
      // 1b6: ifne 1bc
      // 1b9: goto 325
      // 1bc: iload 18
      // 1be: tableswitch 30 0 3 57 30 39 48
      // 1dc: aload 0
      // 1dd: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getByte ()B
      // 1e0: istore 10
      // 1e2: goto 19e
      // 1e5: aload 0
      // 1e6: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getByte ()B
      // 1e9: istore 11
      // 1eb: goto 19e
      // 1ee: aload 0
      // 1ef: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getByte ()B
      // 1f2: istore 12
      // 1f4: goto 19e
      // 1f7: aload 0
      // 1f8: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 1fb: aload 0
      // 1fc: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 1ff: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 202: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // 205: pop
      // 206: goto 19e
      // 209: aload 2
      // 20a: aload 1
      // 20b: invokeinterface net/rim/device/apps/api/transmission/rim/FolderManagementCommandListener.removeAllFoldersCommand (Ljava/lang/Object;)V 2
      // 210: aload 0
      // 211: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 214: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 217: ifgt 21d
      // 21a: goto 2d8
      // 21d: aload 0
      // 21e: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 221: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 224: istore 18
      // 226: iload 18
      // 228: ifne 22e
      // 22b: goto 2d8
      // 22e: iload 18
      // 230: lookupswitch 150 6 -122 122 2 60 5 103 6 112 7 132 8 141
      // 26c: aload 8
      // 26e: ifnull 28e
      // 271: aload 2
      // 272: iload 5
      // 274: iload 7
      // 276: aload 8
      // 278: iload 9
      // 27a: iload 15
      // 27c: aload 1
      // 27d: invokeinterface net/rim/device/apps/api/transmission/rim/FolderManagementCommandListener.createFolderCommand (IILjava/lang/String;IILjava/lang/Object;)V 7
      // 282: bipush 0
      // 283: istore 7
      // 285: aconst_null
      // 286: astore 8
      // 288: bipush 0
      // 289: istore 9
      // 28b: bipush 0
      // 28c: istore 15
      // 28e: aload 0
      // 28f: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getInteger ()I
      // 292: istore 5
      // 294: goto 210
      // 297: aload 0
      // 298: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getInteger ()I
      // 29b: istore 7
      // 29d: goto 210
      // 2a0: aload 0
      // 2a1: bipush 0
      // 2a2: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getString (Z)Ljava/lang/String;
      // 2a5: astore 8
      // 2a7: goto 210
      // 2aa: aload 0
      // 2ab: bipush 1
      // 2ac: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getString (Z)Ljava/lang/String;
      // 2af: astore 8
      // 2b1: goto 210
      // 2b4: aload 0
      // 2b5: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getInteger ()I
      // 2b8: istore 9
      // 2ba: goto 210
      // 2bd: aload 0
      // 2be: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.getInteger ()I
      // 2c1: istore 15
      // 2c3: goto 210
      // 2c6: aload 0
      // 2c7: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 2ca: aload 0
      // 2cb: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 2ce: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 2d1: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // 2d4: pop
      // 2d5: goto 210
      // 2d8: aload 8
      // 2da: ifnull 2ee
      // 2dd: aload 2
      // 2de: iload 5
      // 2e0: iload 7
      // 2e2: aload 8
      // 2e4: iload 9
      // 2e6: iload 15
      // 2e8: aload 1
      // 2e9: invokeinterface net/rim/device/apps/api/transmission/rim/FolderManagementCommandListener.createFolderCommand (IILjava/lang/String;IILjava/lang/Object;)V 7
      // 2ee: aload 2
      // 2ef: aload 1
      // 2f0: invokeinterface net/rim/device/apps/api/transmission/rim/FolderManagementCommandListener.folderSyncCompleteCommand (Ljava/lang/Object;)V 2
      // 2f5: goto 325
      // 2f8: aload 0
      // 2f9: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 2fc: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 2ff: ifle 325
      // 302: aload 0
      // 303: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 306: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 309: istore 18
      // 30b: iload 18
      // 30d: ifne 313
      // 310: goto 325
      // 313: aload 0
      // 314: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 317: aload 0
      // 318: getfield net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement._buffer Lnet/rim/device/api/util/DataBuffer;
      // 31b: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 31e: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // 321: pop
      // 322: goto 2f8
      // 325: iload 3
      // 326: tableswitch 82 0 16 267 82 98 267 267 112 267 267 216 236 250 184 267 206 267 148 194
      // 378: aload 2
      // 379: iload 4
      // 37b: iload 6
      // 37d: iload 5
      // 37f: aload 1
      // 380: invokeinterface net/rim/device/apps/api/transmission/rim/FolderManagementCommandListener.moveMessageCommand (IIILjava/lang/Object;)V 5
      // 385: goto 431
      // 388: aload 2
      // 389: iload 4
      // 38b: iload 6
      // 38d: aload 1
      // 38e: invokeinterface net/rim/device/apps/api/transmission/rim/FolderManagementCommandListener.deleteMessageCommand (IILjava/lang/Object;)V 4
      // 393: goto 431
      // 396: aload 0
      // 397: iload 10
      // 399: sipush 255
      // 39c: iand
      // 39d: iload 11
      // 39f: sipush 255
      // 3a2: iand
      // 3a3: iload 12
      // 3a5: sipush 255
      // 3a8: iand
      // 3a9: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.createOTAFMConfiguration (III)Lnet/rim/device/apps/api/transmission/rim/otasync/OTAFMConfiguration;
      // 3ac: astore 18
      // 3ae: aload 2
      // 3af: aload 18
      // 3b1: aload 1
      // 3b2: invokeinterface net/rim/device/apps/api/transmission/rim/FolderManagementCommandListener.otafmConfigCommand (Lnet/rim/device/apps/api/transmission/rim/otasync/OTAFMConfiguration;Ljava/lang/Object;)V 3
      // 3b7: goto 431
      // 3ba: aload 0
      // 3bb: iload 10
      // 3bd: sipush 255
      // 3c0: iand
      // 3c1: iload 11
      // 3c3: sipush 255
      // 3c6: iand
      // 3c7: iload 12
      // 3c9: sipush 255
      // 3cc: iand
      // 3cd: invokespecial net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.createOTAFMConfiguration (III)Lnet/rim/device/apps/api/transmission/rim/otasync/OTAFMConfiguration;
      // 3d0: astore 18
      // 3d2: aload 2
      // 3d3: aload 18
      // 3d5: aload 1
      // 3d6: invokeinterface net/rim/device/apps/api/transmission/rim/FolderManagementCommandListener.otafmConfigAckCommand (Lnet/rim/device/apps/api/transmission/rim/otasync/OTAFMConfiguration;Ljava/lang/Object;)V 3
      // 3db: goto 431
      // 3de: aload 2
      // 3df: aload 1
      // 3e0: invokeinterface net/rim/device/apps/api/transmission/rim/FolderManagementCommandListener.messageListRequestCommand (Ljava/lang/Object;)V 2
      // 3e5: goto 431
      // 3e8: aload 2
      // 3e9: iload 17
      // 3eb: aload 1
      // 3ec: invokeinterface net/rim/device/apps/api/transmission/rim/FolderManagementCommandListener.messageListAckCommand (ILjava/lang/Object;)V 3
      // 3f1: goto 431
      // 3f4: aload 2
      // 3f5: aload 1
      // 3f6: invokeinterface net/rim/device/apps/api/transmission/rim/FolderManagementCommandListener.configurationRequestCommand (Ljava/lang/Object;)V 2
      // 3fb: goto 431
      // 3fe: aload 2
      // 3ff: iload 5
      // 401: iload 7
      // 403: aload 8
      // 405: iload 9
      // 407: iload 15
      // 409: aload 1
      // 40a: invokeinterface net/rim/device/apps/api/transmission/rim/FolderManagementCommandListener.createFolderCommand (IILjava/lang/String;IILjava/lang/Object;)V 7
      // 40f: goto 431
      // 412: aload 2
      // 413: iload 5
      // 415: iload 16
      // 417: aload 1
      // 418: invokeinterface net/rim/device/apps/api/transmission/rim/FolderManagementCommandListener.deleteFolderCommand (IILjava/lang/Object;)V 4
      // 41d: goto 431
      // 420: aload 2
      // 421: iload 5
      // 423: iload 7
      // 425: aload 8
      // 427: iload 9
      // 429: iload 15
      // 42b: aload 1
      // 42c: invokeinterface net/rim/device/apps/api/transmission/rim/FolderManagementCommandListener.modifyFolderCommand (IILjava/lang/String;IILjava/lang/Object;)V 7
      // 431: iload 14
      // 433: ifne 439
      // 436: goto 008
      // 439: aload 2
      // 43a: iload 4
      // 43c: iload 13
      // 43e: aload 1
      // 43f: invokeinterface net/rim/device/apps/api/transmission/rim/FolderManagementCommandListener.messageStatusCommand (IILjava/lang/Object;)V 4
      // 444: goto 008
      // 447: astore 18
      // 449: goto 008
      // 44c: astore 3
      // 44d: return
      // 44e: astore 3
      // 44f: return
      // try (252 -> 345): 346 null
      // try (0 -> 348): 348 null
      // try (0 -> 348): 350 null
   }

   private OTAFMConfiguration createOTAFMConfiguration(int serverSupport, int userConfig, int conflict) {
      OTAFMConfiguration configuration = new OTAFMConfiguration();
      configuration.setServerSupport(
         (serverSupport & 1) != 0, (serverSupport & 2) != 0, (serverSupport & 4) != 0, (serverSupport & 8) != 0, (serverSupport & 64) != 0
      );
      configuration.setUserConfiguration((userConfig & 1) != 0, (userConfig & 2) != 0, (userConfig & 4) != 0, (userConfig & 64) != 0);
      configuration.setUserStatusFlags((userConfig & 16) != 0, (userConfig & 32) != 0);
      switch (conflict) {
         case 0:
            configuration.setConflictResolution(0);
            break;
         default:
            configuration.setConflictResolution(1);
      }

      return configuration;
   }

   private void writeTaggedInteger(byte tag, int value) {
      this._buffer.writeByte(tag);
      this._buffer.writeByte(4);
      this._buffer.writeInt(value);
   }

   private void writeTaggedShort(byte tag, int value) {
      this._buffer.writeByte(tag);
      this._buffer.writeByte(2);
      this._buffer.writeShort(value);
   }

   private void writeTaggedByte(byte tag, byte value) {
      this._buffer.writeByte(tag);
      this._buffer.writeByte(1);
      this._buffer.writeByte(value);
   }

   public void addDeleteMessage(int identifier, int folderId) {
      this.addDeleteMessage(identifier, folderId, false, false);
   }

   public void addDeleteMessage(int identifier, int folderId, boolean updateMessageRead, boolean messageRead) {
      synchronized (this._buffer) {
         this._buffer.writeByte(2);
         this.writeTaggedInteger((byte)1, identifier);
         if (folderId != 0) {
            this.writeTaggedShort((byte)3, folderId);
         }

         if (updateMessageRead) {
            this.writeTaggedByte((byte)4, (byte)(messageRead ? 1 : 0));
         }

         this._buffer.writeByte(0);
      }
   }

   public void addMoveMessage(int identifier, int oldFolderId, int newFolderId) {
      this.addMoveMessage(identifier, oldFolderId, newFolderId, false, false);
   }

   public void addMoveMessage(int identifier, int oldFolderId, int newFolderId, boolean updateMessageRead, boolean messageRead) {
      synchronized (this._buffer) {
         this._buffer.writeByte(1);
         this.writeTaggedInteger((byte)1, identifier);
         if (oldFolderId != 0) {
            this.writeTaggedShort((byte)3, oldFolderId);
         }

         if (newFolderId != 0) {
            this.writeTaggedShort((byte)2, newFolderId);
         }

         if (updateMessageRead) {
            this.writeTaggedByte((byte)4, (byte)(messageRead ? 1 : 0));
         }

         this._buffer.writeByte(0);
      }
   }

   public void beginMessageListCommand(int messageListId) {
      synchronized (this._buffer) {
         this._buffer.writeByte(6);
         if (messageListId != 0) {
            this.writeTaggedInteger((byte)10, messageListId);
         }
      }
   }

   public void beginRestoreMessageListCommand(int messageListId) {
      synchronized (this._buffer) {
         this._buffer.writeByte(14);
         if (messageListId != 0) {
            this.writeTaggedInteger((byte)10, messageListId);
         }
      }
   }

   public void endMessageListCommand() {
      synchronized (this._buffer) {
         this._buffer.writeByte(0);
      }
   }

   public void beginMessagesInFolder(int folderId, int count) {
      synchronized (this._buffer) {
         this.writeTaggedInteger((byte)2, folderId);
         this.writeTaggedInteger((byte)1, count * 5);
      }
   }

   public void endMessagesInFolder() {
   }

   public void addMessageInFolderEntry(int uid, boolean read) {
      synchronized (this._buffer) {
         this._buffer.writeInt(uid);
         this._buffer.writeByte(read ? 1 : 0);
      }
   }

   public void addPurgedMessageInFolderEntry(int uid, boolean read, boolean deleted) {
      synchronized (this._buffer) {
         int flags = 2;
         if (read) {
            flags |= 1;
         }

         if (deleted) {
            flags |= 16;
         }

         this._buffer.writeInt(uid);
         this._buffer.writeByte((byte)flags);
      }
   }

   public void addConfigurationRequest() {
      synchronized (this._buffer) {
         this._buffer.writeByte(13);
         this._buffer.writeByte(0);
      }
   }

   public void addConfiguration(OTAFMConfiguration configuration) {
      synchronized (this._buffer) {
         this._buffer.writeByte(5);
         byte userConfiguration = 0;
         if (configuration.getWirelessStatusUpdatesSetting()) {
            userConfiguration = (byte)(userConfiguration | 1);
         }

         if (configuration.getWirelessDeletesSetting()) {
            userConfiguration = (byte)(userConfiguration | 2);
         }

         if (configuration.getWirelessFilingSetting()) {
            userConfiguration = (byte)(userConfiguration | 4);
         }

         if (configuration.wirelessPurgeDeletedMessagesSetting()) {
            userConfiguration = (byte)(userConfiguration | 8);
         }

         if (!configuration.isTemporaryDisabled() && (userConfiguration & 15) != 0) {
            userConfiguration = (byte)(userConfiguration | 16);
         }

         if (!configuration.isFirstInitialization()) {
            userConfiguration = (byte)(userConfiguration | 32);
         }

         this.writeTaggedByte((byte)2, userConfiguration);
         byte conflicts = 0;
         if (configuration.getConflictResolutionSetting() == 1) {
            conflicts = 1;
         }

         this.writeTaggedByte((byte)3, conflicts);
         this._buffer.writeByte(0);
      }
   }

   public void addPurgeDeletedMessages() {
      synchronized (this._buffer) {
         this._buffer.writeByte(3);
         this._buffer.writeByte(0);
      }
   }

   public void addPurgeMessageList(int[] refIds, int[] statuses, int messageListId, int folderId) {
      synchronized (this._buffer) {
         this._buffer.writeByte(18);
         this.writeTaggedInteger((byte)10, messageListId);
         this.writeTaggedShort((byte)2, folderId);
         this.writeTaggedInteger((byte)1, refIds.length);

         for (int i = 0; i < refIds.length; i++) {
            this._buffer.writeInt(refIds[i]);
            this._buffer.writeByte((byte)statuses[i]);
         }

         this._buffer.writeByte(0);
      }
   }

   public void addMessageStatus(int identifier, int status) {
      synchronized (this._buffer) {
         this._buffer.writeByte(4);
         this.writeTaggedInteger((byte)1, identifier);
         this.writeTaggedByte((byte)4, (byte)(status & 0xFF));
         this._buffer.writeByte(0);
      }
   }

   public void addFolderListRequest() {
      synchronized (this._buffer) {
         this._buffer.writeByte(12);
         this._buffer.writeByte(0);
      }
   }

   public void addSendChangesRequest() {
      synchronized (this._buffer) {
         this._buffer.writeByte(17);
         this._buffer.writeByte(0);
      }
   }

   public void addModifyFolderAttributes(int folderId, int attributes) {
      synchronized (this._buffer) {
         this._buffer.writeByte(10);
         this.writeTaggedInteger((byte)2, folderId);
         this.writeTaggedInteger((byte)8, attributes);
         this._buffer.writeByte(0);
      }
   }
}
