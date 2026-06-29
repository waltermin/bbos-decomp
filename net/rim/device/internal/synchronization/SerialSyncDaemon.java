package net.rim.device.internal.synchronization;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimeZone;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.lowmemory.LowMemoryFailedListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.MultiServiceSyncCollection;
import net.rim.device.api.synchronization.StateInfoListener;
import net.rim.device.api.synchronization.SummaryParameterListener;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatusProvider;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.DeviceInternal;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.cldc.io.commlink.Protocol;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.cldc.util.TimeService;
import net.rim.device.internal.i18n.UnicodeServiceRegistry;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.resources.Resource;
import net.rim.vm.Array;
import net.rim.vm.MemStats;
import net.rim.vm.Memory;

final class SerialSyncDaemon implements Runnable, GlobalEventListener, LowMemoryFailedListener {
   private DatagramBase _command;
   private DatagramBase _reply;
   private byte[] _dummyBytes;
   private DataBuffer _appData;
   private boolean _started;
   private Thread _thread;
   private Object _synchObject = RIMPersistentStore.getSynchObject();
   private int _periodicCounter;
   private boolean _linkStarted;
   private boolean _lowMemoryFailed;
   private boolean _previousLowMemoryFailed;
   private boolean _canSendEncoded;
   private short _restoreAction;
   private boolean _restoreCompleted;
   private Object _ticket;
   private DatagramConnection _connection;
   private SyncManagerImpl _syncManager;
   private SyncCollection[] _syncCollections;
   private Hashtable _syncAllCollections;
   private SerialSyncCollectionData _lastUpdated;
   private SerialSyncCollectionData[] _allCollections;
   private int _handleBase = 1;
   private static final boolean OUTPUT_CMDS_INTO_LOG = false;
   private static final int PERIODIC_COUNTER_MASK = 63;
   private static final int REMOVES_FOR_PERSISTENT_GC = 16;
   private static final char DEVICE_VERSION_NUMBER = 'V';
   private static final char DEVICE_MINOR_VERSION_NUMBER = 'W';
   private static final char DEVICE_LIST_DATABASES = 'L';
   private static final char DEVICE_LIST_DATABASES_EX = 'J';
   private static final char DEVICE_LIST_ENCODINGS = 'G';
   private static final char DEVICE_SUMMARIZE = 'S';
   private static final char DEVICE_RECORD_DATA = 'D';
   private static final char DEVICE_RECORD_DATA_EX = 'O';
   private static final char DEVICE_RECORD_UID = 'U';
   private static final char DEVICE_NUMBER_RECORDS = 'N';
   private static final char DEVICE_MEMORY = 'M';
   private static final char DEVICE_DATETIME = 'T';
   private static final char DEVICE_ERROR = 'E';
   private static final char HOST_VERSION_NUMBER = 'V';
   private static final char HOST_MINOR_VERSION_NUMBER = 'W';
   private static final char HOST_LIST_DATABASES = 'L';
   private static final char HOST_LIST_DATABASES_EX = 'J';
   private static final char HOST_BACKUP = 'B';
   private static final char HOST_BACKUP_EX = 'O';
   private static final char HOST_CLEAN = 'C';
   private static final char HOST_SUMMARIZE = 'S';
   private static final char HOST_SET_SUMMARY_STATE = 'T';
   private static final char HOST_ADD_RECORD = 'A';
   private static final char HOST_ADD_RECORD_EX = 'E';
   private static final char HOST_UPDATE_RECORD = 'U';
   private static final char HOST_REMOVE_RECORD = 'R';
   private static final char HOST_FETCH_RECORD = 'F';
   private static final char HOST_FETCH_RECORD_EX = 'P';
   private static final char HOST_NUMBER_RECORDS = 'N';
   private static final char HOST_MEMORY = 'M';
   private static final char HOST_CLEAN_DIRTY_FLAG = 'D';
   private static final char HOST_GET_DATETIME = 'H';
   private static final char HOST_SET_DEVICE_DATETIME = 'Y';
   private static final char HOST_LIST_ENCODINGS = 'G';
   private static final char HOST_BACKUP_RESTORE = 'K';
   private static final int SERIAL_DB_ACCESS_PROTOCOL_VERSION = 2;
   private static final int SERIAL_DB_ACCESS_PROTOCOL_MINOR_VERSION = 2;
   private static final byte DATABASE_HEADER_VERSION = 1;
   private static final short DATABASE_HEADER_SIZE = 13;
   private static final byte RECORD_HEADER_VERSION = 1;
   private static final short RECORD_HEADER_SIZE = 10;
   private static final byte DATABASE_NAME = 1;
   private static final int BR_START = 0;
   private static final int BR_STOP = 1;
   static final int SR_NO_ERROR = 0;
   static final int SR_GENERAL_ERROR = 1;
   static final int SR_INVALID_DATA = 2;
   static final int SR_UNKNOWN_APP = 4;
   static final int SR_UNKNOWN_COMMAND = 5;
   static final int SR_UNKNOWN_DATABASE = 6;
   static final int SR_UNKNOWN_RECORD = 7;
   static final int SR_OUT_OF_MEMORY = 8;
   static final int SR_OPERATION_CANCELLED = 9;
   static final int SR_CONTENT_PROTECTION_ENABLED = 16;
   private static final int DAYLIGHT_ENABLED = 128;
   private static final int DAYLIGHT_SET = 1;
   private static final short RESTORE_INACTIVE = 0;
   private static final short RESTORE_INITIALIZING = 1;
   private static final short RESTORE_WRITING = 2;

   public static final void initialize() {
      new SerialSyncDaemon();
   }

   private SerialSyncDaemon() {
      EventLogger.register(4907703648615910489L, "net.rim.serialsync", 2);
      this._syncManager = (SyncManagerImpl)SyncManager.getInstance();
      Proxy.getInstance().addGlobalEventListener(this);
      LowMemoryManager.addLowMemoryFailedListener(this);
      this.checkThread();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4783788168994715579L) {
         this.checkThread();
      } else {
         if (guid == 945659952435832745L) {
            this._periodicCounter = -1;
         }
      }
   }

   private final void checkThread() {
      if (this._thread == null || !this._thread.isAlive()) {
         this._thread = (Thread)(new Object(this));
         Proxy.getInstance().startThread(this._thread);
      }
   }

   @Override
   public final void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._connection Ljavax/microedition/io/DatagramConnection;
      // 04: ifnonnull 60
      // 07: invokestatic net/rim/vm/Process.currentProcess ()Lnet/rim/vm/Process;
      // 0a: ldc_w 128000000
      // 0d: invokevirtual net/rim/vm/Process.setHeapQuota (I)V
      // 10: aload 0
      // 11: ldc_w "commlink:Database Access"
      // 14: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 17: checkcast java/lang/Object
      // 1a: putfield net/rim/device/internal/synchronization/SerialSyncDaemon._connection Ljavax/microedition/io/DatagramConnection;
      // 1d: aload 0
      // 1e: aload 0
      // 1f: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._connection Ljavax/microedition/io/DatagramConnection;
      // 22: bipush 0
      // 23: invokeinterface javax/microedition/io/DatagramConnection.newDatagram (I)Ljavax/microedition/io/Datagram; 2
      // 28: checkcast java/lang/Object
      // 2b: putfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 2e: aload 0
      // 2f: aload 0
      // 30: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._connection Ljavax/microedition/io/DatagramConnection;
      // 33: bipush 0
      // 34: invokeinterface javax/microedition/io/DatagramConnection.newDatagram (I)Ljavax/microedition/io/Datagram; 2
      // 39: checkcast java/lang/Object
      // 3c: putfield net/rim/device/internal/synchronization/SerialSyncDaemon._reply Lnet/rim/device/api/io/DatagramBase;
      // 3f: aload 0
      // 40: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._reply Lnet/rim/device/api/io/DatagramBase;
      // 43: bipush 0
      // 44: invokevirtual net/rim/device/api/util/DataBuffer.setBigEndian (Z)V
      // 47: aload 0
      // 48: bipush 0
      // 49: newarray 8
      // 4b: putfield net/rim/device/internal/synchronization/SerialSyncDaemon._dummyBytes [B
      // 4e: aload 0
      // 4f: new java/lang/Object
      // 52: dup
      // 53: aload 0
      // 54: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._dummyBytes [B
      // 57: bipush 0
      // 58: bipush 0
      // 59: bipush 0
      // 5a: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 5d: putfield net/rim/device/internal/synchronization/SerialSyncDaemon._appData Lnet/rim/device/api/util/DataBuffer;
      // 60: bipush 0
      // 61: istore 1
      // 62: aload 0
      // 63: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._linkStarted Z
      // 66: ifeq 8a
      // 69: aload 0
      // 6a: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.getNextCommand ()V
      // 6d: aload 0
      // 6e: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 71: bipush 4
      // 73: invokevirtual net/rim/device/api/io/DatagramBase.isFlagSet (I)Z
      // 76: ifeq 7c
      // 79: goto 8a
      // 7c: aload 0
      // 7d: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 80: bipush 2
      // 82: invokevirtual net/rim/device/api/io/DatagramBase.isFlagSet (I)Z
      // 85: ifeq 69
      // 88: bipush 1
      // 89: istore 1
      // 8a: aload 0
      // 8b: iload 1
      // 8c: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.messageLoop1 (Z)V
      // 8f: aload 0
      // 90: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.cleanup ()V
      // 93: return
      // 94: astore 1
      // 95: aload 0
      // 96: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.cleanup ()V
      // 99: return
      // 9a: astore 1
      // 9b: aload 0
      // 9c: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.cleanup ()V
      // 9f: return
      // a0: astore 2
      // a1: aload 0
      // a2: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.cleanup ()V
      // a5: aload 2
      // a6: athrow
      // try (0 -> 66): 69 null
      // try (0 -> 66): 73 null
      // try (0 -> 66): 77 null
      // try (69 -> 70): 77 null
      // try (73 -> 74): 77 null
      // try (77 -> 78): 77 null
   }

   private final void messageLoop1(boolean param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: iload 1
      // 01: ifne 08
      // 04: aload 0
      // 05: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.getNextCommand ()V
      // 08: aload 0
      // 09: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 0c: bipush 1
      // 0d: invokevirtual net/rim/device/api/io/DatagramBase.isFlagSet (I)Z
      // 10: ifeq 16
      // 13: goto f0
      // 16: aload 0
      // 17: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 1a: bipush 2
      // 1c: invokevirtual net/rim/device/api/io/DatagramBase.isFlagSet (I)Z
      // 1f: ifeq 2e
      // 22: aload 0
      // 23: bipush 1
      // 24: putfield net/rim/device/internal/synchronization/SerialSyncDaemon._linkStarted Z
      // 27: aload 0
      // 28: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.startup ()V
      // 2b: goto f0
      // 2e: aload 0
      // 2f: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 32: bipush 4
      // 34: invokevirtual net/rim/device/api/io/DatagramBase.isFlagSet (I)Z
      // 37: ifeq 46
      // 3a: aload 0
      // 3b: bipush 0
      // 3c: putfield net/rim/device/internal/synchronization/SerialSyncDaemon._linkStarted Z
      // 3f: aload 0
      // 40: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.cleanup ()V
      // 43: goto f0
      // 46: aload 0
      // 47: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 4a: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 4d: i2c
      // 4e: istore 2
      // 4f: iload 2
      // 50: lookupswitch 77 6 65 60 68 60 69 60 82 60 84 60 85 60
      // 8c: aload 0
      // 8d: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.messageLoop2 ()Z
      // 90: ifeq f0
      // 93: aload 0
      // 94: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 97: invokevirtual net/rim/device/api/util/DataBuffer.rewind ()V
      // 9a: goto 08
      // 9d: aload 0
      // 9e: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // a1: invokevirtual net/rim/device/api/util/DataBuffer.rewind ()V
      // a4: bipush 1
      // a5: istore 3
      // a6: aload 0
      // a7: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.processCommand ()I
      // aa: istore 3
      // ab: aload 0
      // ac: iload 3
      // ad: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.sendReply (I)V
      // b0: goto f0
      // b3: astore 4
      // b5: bipush 0
      // b6: istore 3
      // b7: aload 0
      // b8: iload 3
      // b9: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.sendReply (I)V
      // bc: goto f0
      // bf: astore 4
      // c1: bipush 2
      // c3: istore 3
      // c4: aload 0
      // c5: iload 3
      // c6: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.sendReply (I)V
      // c9: goto f0
      // cc: astore 4
      // ce: bipush 2
      // d0: istore 3
      // d1: aload 0
      // d2: iload 3
      // d3: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.sendReply (I)V
      // d6: goto f0
      // d9: astore 4
      // db: bipush 2
      // dd: istore 3
      // de: aload 0
      // df: iload 3
      // e0: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.sendReply (I)V
      // e3: goto f0
      // e6: astore 5
      // e8: aload 0
      // e9: iload 3
      // ea: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.sendReply (I)V
      // ed: aload 5
      // ef: athrow
      // f0: aload 0
      // f1: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.getNextCommand ()V
      // f4: goto 08
      // try (51 -> 54): 58 null
      // try (51 -> 54): 65 null
      // try (51 -> 54): 72 null
      // try (51 -> 54): 79 null
      // try (51 -> 54): 86 null
      // try (58 -> 61): 86 null
      // try (65 -> 68): 86 null
      // try (72 -> 75): 86 null
      // try (79 -> 82): 86 null
      // try (86 -> 87): 86 null
   }

   private final boolean messageLoop2() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._synchObject Ljava/lang/Object;
      // 004: dup
      // 005: astore 1
      // 006: monitorenter
      // 007: aload 0
      // 008: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 00b: invokevirtual net/rim/device/api/util/DataBuffer.rewind ()V
      // 00e: bipush 0
      // 00f: istore 2
      // 010: bipush 1
      // 011: istore 3
      // 012: aload 0
      // 013: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 016: bipush 1
      // 017: invokevirtual net/rim/device/api/io/DatagramBase.isFlagSet (I)Z
      // 01a: ifeq 020
      // 01d: goto 11b
      // 020: aload 0
      // 021: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 024: bipush 2
      // 026: invokevirtual net/rim/device/api/io/DatagramBase.isFlagSet (I)Z
      // 029: ifeq 039
      // 02c: aload 0
      // 02d: bipush 1
      // 02e: putfield net/rim/device/internal/synchronization/SerialSyncDaemon._linkStarted Z
      // 031: aload 0
      // 032: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.startup ()V
      // 035: bipush 0
      // 036: aload 1
      // 037: monitorexit
      // 038: ireturn
      // 039: aload 0
      // 03a: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 03d: bipush 4
      // 03f: invokevirtual net/rim/device/api/io/DatagramBase.isFlagSet (I)Z
      // 042: ifeq 052
      // 045: aload 0
      // 046: bipush 0
      // 047: putfield net/rim/device/internal/synchronization/SerialSyncDaemon._linkStarted Z
      // 04a: aload 0
      // 04b: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.cleanup ()V
      // 04e: bipush 0
      // 04f: aload 1
      // 050: monitorexit
      // 051: ireturn
      // 052: aload 0
      // 053: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 056: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 059: i2c
      // 05a: istore 4
      // 05c: iload 4
      // 05e: lookupswitch 185 6 65 58 68 58 69 58 82 58 84 58 85 58
      // 098: iload 3
      // 099: ifeq 0a9
      // 09c: aload 0
      // 09d: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 0a0: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedShort ()I
      // 0a3: istore 2
      // 0a4: bipush 0
      // 0a5: istore 3
      // 0a6: goto 0b8
      // 0a9: iload 2
      // 0aa: aload 0
      // 0ab: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 0ae: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedShort ()I
      // 0b1: if_icmpeq 0b8
      // 0b4: bipush 1
      // 0b5: aload 1
      // 0b6: monitorexit
      // 0b7: ireturn
      // 0b8: aload 0
      // 0b9: getfield net/rim/device/internal/synchronization/SerialSyncDaemon._command Lnet/rim/device/api/io/DatagramBase;
      // 0bc: invokevirtual net/rim/device/api/util/DataBuffer.rewind ()V
      // 0bf: bipush 1
      // 0c0: istore 5
      // 0c2: aload 0
      // 0c3: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.processCommand ()I
      // 0c6: istore 5
      // 0c8: aload 0
      // 0c9: iload 5
      // 0cb: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.sendReply (I)V
      // 0ce: goto 11b
      // 0d1: astore 6
      // 0d3: bipush 0
      // 0d4: istore 5
      // 0d6: aload 0
      // 0d7: iload 5
      // 0d9: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.sendReply (I)V
      // 0dc: goto 11b
      // 0df: astore 6
      // 0e1: bipush 2
      // 0e3: istore 5
      // 0e5: aload 0
      // 0e6: iload 5
      // 0e8: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.sendReply (I)V
      // 0eb: goto 11b
      // 0ee: astore 6
      // 0f0: bipush 2
      // 0f2: istore 5
      // 0f4: aload 0
      // 0f5: iload 5
      // 0f7: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.sendReply (I)V
      // 0fa: goto 11b
      // 0fd: astore 6
      // 0ff: bipush 2
      // 101: istore 5
      // 103: aload 0
      // 104: iload 5
      // 106: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.sendReply (I)V
      // 109: goto 11b
      // 10c: astore 7
      // 10e: aload 0
      // 10f: iload 5
      // 111: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.sendReply (I)V
      // 114: aload 7
      // 116: athrow
      // 117: bipush 1
      // 118: aload 1
      // 119: monitorexit
      // 11a: ireturn
      // 11b: aload 0
      // 11c: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.getNextCommand ()V
      // 11f: goto 012
      // 122: astore 8
      // 124: aload 1
      // 125: monitorexit
      // 126: aload 8
      // 128: athrow
      // try (76 -> 79): 83 null
      // try (76 -> 79): 90 null
      // try (76 -> 79): 97 null
      // try (76 -> 79): 104 null
      // try (76 -> 79): 111 null
      // try (83 -> 86): 111 null
      // try (90 -> 93): 111 null
      // try (97 -> 100): 111 null
      // try (104 -> 107): 111 null
      // try (111 -> 112): 111 null
      // try (5 -> 31): 124 null
      // try (32 -> 45): 124 null
      // try (46 -> 70): 124 null
      // try (71 -> 120): 124 null
      // try (121 -> 127): 124 null
   }

   private final void getNextCommand() {
      this._connection.receive(this._command);
      this._command.setBigEndian(false);
   }

   private final void startup() {
      this._previousLowMemoryFailed = false;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void cleanup() {
      boolean doGC = this._started;
      this._previousLowMemoryFailed = false;
      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         this.updatingCollection(null);
         if (this._started) {
            synchronized (this._synchObject) {
               this._syncManager.fireSerialSyncStartedOrStopped(false, this.getAndResetRestoreStatus());
            }

            this._started = false;
         }

         this.freeCollectionData();
         this.freeIOData();
         var7 = false;
      } finally {
         if (var7) {
            this._ticket = null;
         }
      }

      this._ticket = null;
      if (doGC) {
         Memory.fullGC();
         if (this.needRAMRecover()) {
            Memory.RAMRecover();
         }
      }
   }

   private final boolean needRAMRecover() {
      MemStats stats = net.rim.device.api.system.Memory.getRAMStats();
      int freeRAM = stats.getFree();
      int allocatedRAM = stats.getAllocated();
      return 2 * allocatedRAM > freeRAM;
   }

   private final int processCommand() {
      this._command.setBigEndian(false);
      char cmd = (char)this._command.readByte();
      switch (cmd) {
         case '@':
         case 'I':
         case 'Q':
         case 'X':
            return 5;
         case 'A':
         case 'B':
         case 'C':
         case 'D':
         case 'E':
         case 'N':
         case 'O':
         case 'S':
            int dbIDx = this._command.readUnsignedShort();
            SerialSyncCollectionData scdx;
            switch (cmd) {
               case 'B':
               case 'D':
               case 'O':
               case 'S':
                  scdx = this.getCollectionData(dbIDx, true, true);
                  break;
               default:
                  scdx = this.getCollectionData(dbIDx, false, false);
            }

            if (scdx == null) {
               return 6;
            } else {
               switch (cmd) {
                  case 'A':
                     this.getContentProtectionTicket(scdx);
                     this.updatingCollection(scdx);
                     return this.addSyncRecord(scdx, false);
                  case 'B':
                     this.getContentProtectionTicket(scdx);
                     this.backup(scdx, false);
                     return 0;
                  case 'C':
                     this.getContentProtectionTicket(scdx);
                     this.updatingCollection(scdx);
                     return this.cleanCollection(scdx);
                  case 'D':
                     this.getContentProtectionTicket(scdx);
                     this.updatingCollection(scdx);
                     return this.cleanDirtyFlag(scdx);
                  case 'E':
                     this.getContentProtectionTicket(scdx);
                     this.updatingCollection(scdx);
                     return this.addSyncRecord(scdx, true);
                  case 'N':
                     this.numRecords(scdx);
                     return 0;
                  case 'O':
                     this.getContentProtectionTicket(scdx);
                     this.backup(scdx, true);
                     return 0;
                  case 'S':
                     this.getContentProtectionTicket(scdx);
                     boolean sentSummaryParameter = false;
                     if (this._command.available() > 0 && scdx.getSyncCollection() instanceof Object) {
                        ((SummaryParameterListener)scdx.getSyncCollection()).setSummaryParameter(this._command);
                        sentSummaryParameter = true;
                     }

                     this.summarizeCollection(scdx);
                     if (sentSummaryParameter) {
                        ((SummaryParameterListener)scdx.getSyncCollection()).clearSummaryParameter();
                        return 0;
                     }

                     return 0;
                  default:
                     return 0;
               }
            }
         case 'F':
         case 'P':
         case 'R':
            int var8 = this._command.readUnsignedShort();
            SerialSyncCollectionData var11 = this.getCollectionData(var8, true, false);
            if (var11 == null) {
               return 6;
            } else {
               int var10 = this._command.readUnsignedShort();
               SyncObject var13 = var11.getSyncObject(var10);
               if (var13 == null) {
                  return 7;
               } else {
                  switch (cmd) {
                     case 'F':
                        this.getContentProtectionTicket(var11);
                        this.sendSyncRecord(var11, var13, var10, false);
                        return 0;
                     case 'P':
                        this.getContentProtectionTicket(var11);
                        this.sendSyncRecord(var11, var13, var10, true);
                        return 0;
                     case 'R':
                        this.getContentProtectionTicket(var11);
                        this.updatingCollection(var11);
                        return this.removeSyncRecord(var11, var13, var10);
                     default:
                        return 0;
                  }
               }
            }
         case 'G':
            this.sendDeviceEncodingCapabilities();
            return 0;
         case 'H':
            this.sendDateTime();
            return 0;
         case 'J':
            this.listDatabasesEx();
            return 0;
         case 'K':
            return this.backupRestore();
         case 'L':
            this.listDatabases();
            return 0;
         case 'M':
            this.sendFreeMemory();
            return 0;
         case 'T':
         case 'U':
            int dbID = this._command.readUnsignedShort();
            SerialSyncCollectionData scd = this.getCollectionData(dbID, true, false);
            if (scd == null) {
               return 6;
            } else {
               int version = this._command.readUnsignedByte();
               int handle = this._command.readUnsignedShort();
               SyncObject so = scd.getSyncObject(handle);
               if (so == null) {
                  return 7;
               } else {
                  switch (cmd) {
                     case 'S':
                        return 0;
                     case 'T':
                        this.getContentProtectionTicket(scd);
                        this.updatingCollection(scd);
                        return this.setSummaryState(scd, so, handle, version);
                     case 'U':
                     default:
                        this.getContentProtectionTicket(scd);
                        this.updatingCollection(scd);
                        return this.updateSyncRecord(scd, so, handle, version);
                  }
               }
            }
         case 'V':
         default:
            this.sendProtocolVersionNumber();
            return 0;
         case 'W':
            this.sendProtocolMinorVersionNumber();
            return 0;
         case 'Y':
            this.setDateTime();
            return 0;
      }
   }

   private final void sendProtocolVersionNumber() {
      DatagramBase reply = this.resetReply(3);
      reply.writeByte(86);
      reply.writeShort(2);
      this.sendReply(false);
   }

   private final void sendProtocolMinorVersionNumber() {
      DatagramBase reply = this.resetReply(3);
      reply.writeByte(87);
      reply.writeShort(2);
      this.sendReply(false);
   }

   private final void sendFreeMemory() {
      DatagramBase reply = this.resetReply(9);
      reply.writeByte(77);
      reply.writeInt(net.rim.device.api.system.Memory.getRAMStats().getFree());
      reply.writeInt(net.rim.device.api.system.Memory.getPersistentStats().getFree() - Memory.getFlashSlack());
      this.sendReply(false);
   }

   private final void sendDateTime() {
      DatagramBase reply = this.resetReply(10);
      this._reply.writeByte(84);
      Calendar now = Calendar.getInstance();
      reply.writeByte(now.get(13));
      reply.writeByte(now.get(12));
      reply.writeByte(now.get(11));
      reply.writeByte(now.get(5));
      reply.writeByte(now.get(2) + 1);
      reply.writeByte(now.get(7));
      reply.writeShort(now.get(1));
      reply.writeByte(now.getTimeZone().getRawOffset() / 1000 / 60 / 6);
      reply.writeByte(0);
      this.sendReply(false);
   }

   private final void sendDeviceEncodingCapabilities() {
      byte[] encodings = ConverterUtilities.getSupportedSerializationEncodings();
      if (encodings == null) {
         encodings = new byte[0];
      }

      int encodings_length = encodings.length;
      DatagramBase command = this._command;
      int host_encodings_length = command.readUnsignedShort();
      if (host_encodings_length > 0) {
         byte[] host_encodings = new byte[host_encodings_length];
         command.readFully(host_encodings, 0, host_encodings_length);
         this._canSendEncoded = ConverterUtilities.indicatePossibleSerializationEncodings(encodings, host_encodings);
      }

      DatagramBase reply = this.resetReply(encodings_length + 3);
      reply.writeByte(71);
      reply.writeShort(encodings_length);

      for (int i = 0; i < encodings_length; i++) {
         reply.writeByte(encodings[i]);
      }

      this.sendReply(false);
   }

   private final void setDateTime() {
      DatagramBase command = this._command;
      Calendar newtime = Calendar.getInstance(TimeZone.getTimeZone(DateTimeUtilities.GMT));
      newtime.set(13, command.readUnsignedByte());
      newtime.set(12, command.readUnsignedByte());
      newtime.set(11, command.readUnsignedByte());
      newtime.set(5, command.readUnsignedByte());
      newtime.set(2, command.readUnsignedByte() - 1);
      command.readUnsignedByte();
      newtime.set(1, command.readUnsignedShort());
      int tzOffset = command.readByte();
      command.readUnsignedByte();
      int daylight = command.readByte();
      if ((daylight & 129) == 129) {
         tzOffset += 10;
      }

      int tzid = command.readUnsignedShort();
      newtime.set(0, 1);
      DeviceInternal.setDateTime(((CalendarExtensions)newtime).getTimeLong() - 360000 * tzOffset);
      TimeService ts = TimeService.getTimeService();
      ts.setDefaultTimeZone(ts.getTimeZoneIDFromSerialSyncID(tzid));
   }

   private final boolean getAndResetRestoreStatus() {
      boolean restoreCompletedTemp = this._restoreCompleted;
      this._restoreCompleted = false;
      this._restoreAction = 0;
      return restoreCompletedTemp;
   }

   private final int backupRestore() {
      int which = this._command.readShort();
      switch (which) {
         case -1:
            return 2;
         case 0:
         default:
            this._restoreAction = 1;
            synchronized (this._synchObject) {
               this._syncManager.fireSerialSyncStartedOrStopped(true, false);
            }

            this._started = true;
            return 0;
         case 1:
            if (this._restoreAction == 2) {
               this._restoreCompleted = true;
            }

            this._restoreAction = 0;
            this.cleanup();
            return 0;
      }
   }

   private final void getContentProtectionTicket(SerialSyncCollectionData scd) {
      if (this._ticket == null) {
         if (!(scd.getSyncCollection() instanceof NoProtectedContentInCollection)) {
            if (PersistentContent.isEncryptionEnabled()) {
               ApplicationManager applicationManager = ApplicationManager.getApplicationManager();
               if (applicationManager.isSystemLocked()) {
                  if (Alert.isBuzzerSupported()) {
                     short[] tune = new short[]{440, 100, 880, 100, 1760, 100, 256, 3171, -30099, 594, -22995, 29858};
                     Alert.startBuzzer(tune, 100);
                  } else if (Alert.isMIDISupported()) {
                     Resource rsrc = Resource.getResourceClass();
                     byte[] tune = rsrc.getResource("ContentProtectionNotification.mid");
                     Alert.setVolume(100);
                     Alert.startMIDI(tune, 2);
                  }
               }

               ((Protocol)this._connection).popStatusScreen();
               applicationManager.unlockSystem();
               ((Protocol)this._connection).pushStatusScreen();
            }

            this._ticket = PersistentContent.getTicket();
         }
      }
   }

   private final void updatingCollection(SerialSyncCollectionData scd) {
      if (this._lastUpdated != scd) {
         if (this._lastUpdated != null) {
            this._lastUpdated.endTransaction();
            this._lastUpdated.collectionUpdatedSerially();
         }

         this._lastUpdated = scd;
         if (this._lastUpdated != null) {
            this._lastUpdated.beginTransaction();
         }
      }
   }

   private final SyncCollection[] getSyncCollections() {
      if (this._syncCollections == null) {
         SyncCollection[] syncCollections = this._syncManager.getSyncCollections();
         int desktopBackupPolicy = ITPolicy.getInteger(24, 47, 0);
         int numSyncCollections = syncCollections.length;
         int num = 0;

         for (int i = 0; i < numSyncCollections; i++) {
            SyncCollection sc = syncCollections[i];
            if (!(sc instanceof Object) || ((SyncCollectionStatusProvider)sc).isReadableForSerialSync()) {
               syncCollections[num++] = sc;
            }
         }

         Array.resize(syncCollections, num);
         if (desktopBackupPolicy != 0) {
            numSyncCollections = syncCollections.length;
            num = 0;

            for (int i = 0; i < numSyncCollections; i++) {
               SyncCollection sc = syncCollections[i];
               if (sc instanceof AlwaysSyncCollection || desktopBackupPolicy == 1 && sc instanceof MinimalSyncCollection) {
                  syncCollections[num++] = sc;
               }
            }

            Array.resize(syncCollections, num);
         }

         this._syncCollections = new Object[syncCollections.length * 2];
         this._syncAllCollections = (Hashtable)(new Object());
         int finalSize = 0;

         for (int i = 0; i < syncCollections.length; i++) {
            SyncCollection collection = syncCollections[i];
            if (!(collection instanceof Object)) {
               this._syncCollections[finalSize] = collection;
               finalSize++;
            } else {
               MultiServiceSyncCollection mssc = (MultiServiceSyncCollection)collection;
               String collectionName = mssc.getSyncName();
               SyncAllCollection sac = (SyncAllCollection)this._syncAllCollections.get(collectionName);
               if (sac == null) {
                  sac = new SyncAllCollection(collectionName);
                  this._syncAllCollections.put(collectionName, sac);
               }

               sac.addSyncCollection(mssc, mssc.getSid());
               if (mssc.isDefault()) {
                  this._syncCollections[finalSize] = collection;
                  finalSize++;
               }
            }
         }

         for (Enumeration e = this._syncAllCollections.elements(); e.hasMoreElements(); finalSize++) {
            SyncAllCollection sac = (SyncAllCollection)e.nextElement();
            this._syncCollections[finalSize] = sac;
         }

         Array.resize(this._syncCollections, finalSize);
      }

      return this._syncCollections;
   }

   private final SyncCollection getSyncCollection(int index) {
      SyncCollection[] syncCollections = this.getSyncCollections();
      return index < syncCollections.length ? syncCollections[index] : null;
   }

   private final int getSyncCollectionCount() {
      return this.getSyncCollections().length;
   }

   private final SerialSyncCollectionData getCollectionData(int dbID, boolean needRecords, boolean forceUpdate) {
      if (this._allCollections == null) {
         this._allCollections = new SerialSyncCollectionData[this.getSyncCollectionCount()];
      }

      if (dbID >= 0 && dbID < this._allCollections.length) {
         SerialSyncCollectionData result = this._allCollections[dbID];
         if (result == null) {
            SyncCollection sc = this.getSyncCollection(dbID);
            if (sc == null) {
               return null;
            }

            if (sc instanceof SyncAllCollection) {
               result = new SerialSyncAllCollectionData((SyncAllCollection)sc);
            } else {
               result = new SerialSyncCollectionData(sc);
            }

            this._allCollections[dbID] = result;
         }

         if (needRecords && (result.getSyncObjects() == null || forceUpdate && result.isStale())) {
            result.loadSyncObjectsFromCollection();
            if (result.getHandleBase() == 0) {
               result.setHandleBase(this._handleBase);
               this._handleBase = this._handleBase + result.getCount() * 2;
            }

            result.setStale(false);
         }

         return result;
      } else {
         return null;
      }
   }

   private final void freeCollectionData() {
      this._syncCollections = null;
      if (this._allCollections != null) {
         for (int i = 0; i < this._allCollections.length; i++) {
            if (this._allCollections[i] != null) {
               this._allCollections[i].dispose();
            }
         }

         this._allCollections = null;
      }

      this._handleBase = 1;
   }

   private final void listDatabases() {
      int num = this.getSyncCollectionCount();
      DatagramBase reply = this.resetReply();
      reply.writeByte(76);
      reply.writeShort(num);

      for (int i = 0; i < num; i++) {
         SyncCollection sc = this.getSyncCollection(i);
         if (sc != null) {
            String name = sc.getSyncName();
            reply.writeShort(i);
            reply.writeByte(sc.getSyncVersion());
            reply.writeInt(0);
            reply.writeInt(sc.getSyncObjectCount());
            reply.writeShort(name.length() + 1);
            reply.write(name.getBytes());
            reply.writeByte(0);
         }
      }

      this.sendReply(false);
   }

   private final void listDatabasesEx() {
      int num = this.getSyncCollectionCount();
      DatagramBase reply = this.resetReply();
      reply.writeByte(74);
      reply.writeShort(num);
      reply.writeByte(1);
      reply.writeShort(13);
      short flags = 0;

      for (int i = 0; i < num; flags = 0) {
         SyncCollection sc = this.getSyncCollection(i);
         if (sc != null) {
            reply.writeShort(i);
            reply.writeByte(sc.getSyncVersion());
            reply.writeInt(0);
            reply.writeInt(sc.getSyncObjectCount());
            flags = (short)(flags | (isWritable(sc) ? 0 : 1));
            reply.writeShort(flags);
            String name = this.getSyncName(sc);
            reply.writeShort(name.length() + 1);
            reply.writeByte(1);
            reply.write(name.getBytes());
            reply.writeByte(0);
            reply.writeShort(0);
         }

         i++;
      }

      this.sendReply(false);
   }

   private final String getSyncName(SyncCollection sc) {
      return sc.getSyncName();
   }

   private final void summarizeCollection(SerialSyncCollectionData scd) {
      DatagramBase reply = this.resetReply();
      SyncCollection sc = scd.getSyncCollection();
      int version = sc.getSyncVersion();
      reply.writeByte(83);
      reply.writeByte(version);
      int count = scd.getCount();
      reply.writeInt(count);
      SyncObject[] objects = scd.getSyncObjects();
      StateInfoListener stateInfoListener = null;
      if (sc instanceof Object) {
         stateInfoListener = (StateInfoListener)sc;
      }

      count = objects.length;
      int handleBase = scd.getHandleBase();

      for (int i = 0; i < count; i++) {
         SyncObject so = objects[i];
         if (so != null) {
            reply.writeByte(version);
            reply.writeShort(i + handleBase);
            reply.writeInt(so.getUID());
            if (sc.isSyncObjectDirty(so)) {
               reply.writeByte(1);
            } else {
               reply.writeByte(0);
            }

            int stateFlag;
            if (stateInfoListener != null) {
               stateFlag = stateInfoListener.getStateInfo(so);
            } else {
               stateFlag = 0;
            }

            reply.writeInt(stateFlag);
            if ((i + 1) % 16 == 0) {
               this.sendReply(true);
               reply = this.resetReply();
            }
         }
      }

      this.sendReply(false);
   }

   private final void numRecords(SerialSyncCollectionData scd) {
      DatagramBase reply = this.resetReply(5);
      reply.writeByte(78);
      reply.writeInt(scd.getNumberOfRecords());
      this.sendReply(false);
   }

   private final void backup(SerialSyncCollectionData param1, boolean param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: invokevirtual net/rim/device/internal/synchronization/SerialSyncCollectionData.getSyncObjects ()[Lnet/rim/device/api/synchronization/SyncObject;
      // 04: astore 3
      // 05: aload 3
      // 06: arraylength
      // 07: istore 4
      // 09: aload 1
      // 0a: invokevirtual net/rim/device/internal/synchronization/SerialSyncCollectionData.getHandleBase ()I
      // 0d: istore 5
      // 0f: bipush 0
      // 10: istore 6
      // 12: iload 6
      // 14: iload 4
      // 16: if_icmpge 41
      // 19: aload 3
      // 1a: iload 6
      // 1c: aaload
      // 1d: astore 7
      // 1f: aload 7
      // 21: ifnull 3b
      // 24: aload 0
      // 25: aload 1
      // 26: aload 7
      // 28: iload 6
      // 2a: iload 5
      // 2c: iadd
      // 2d: iload 2
      // 2e: invokespecial net/rim/device/internal/synchronization/SerialSyncDaemon.sendSyncRecord (Lnet/rim/device/internal/synchronization/SerialSyncCollectionData;Lnet/rim/device/api/synchronization/SyncObject;IZ)V
      // 31: goto 3b
      // 34: astore 8
      // 36: aload 8
      // 38: athrow
      // 39: astore 8
      // 3b: iinc 6 1
      // 3e: goto 12
      // 41: return
      // try (20 -> 28): 29 null
      // try (20 -> 28): 32 null
   }

   private final int removeSyncRecord(SerialSyncCollectionData scd, SyncObject so, int handle) {
      SyncCollection sc = scd.getSyncCollection();
      if (!scd.isWritable()) {
         return 2;
      }

      sc.removeSyncObject(so);
      scd.getSyncObjects()[handle - scd.getHandleBase()] = null;
      scd.decrementCount();
      if (scd.getCount() == 0 && scd.getSyncObjects().length > 16) {
         Memory.persistentGC();
      }

      return 0;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void sendSyncRecord(SerialSyncCollectionData scd, SyncObject so, int handle, boolean extendedCommand) {
      DatagramBase reply = this.resetReply();
      SyncCollection sc = scd.getSyncCollection();
      int version = sc.getSyncVersion();
      if (extendedCommand) {
         reply.writeByte(79);
         reply.writeByte(1);
         reply.writeShort(10);
      } else {
         reply.writeByte(68);
      }

      reply.writeByte(version);
      reply.writeShort(handle);
      reply.writeInt(so.getUID());
      if (sc.isSyncObjectDirty(so)) {
         reply.writeByte(1);
      } else {
         reply.writeByte(0);
      }

      int position = reply.getPosition();
      int flags = 0;
      if (extendedCommand) {
         reply.writeShort(flags);
      }

      UnicodeServiceRegistry ur = UnicodeServiceRegistry.getInstance();
      if (ur != null) {
         ur.setFlags(0);
      }

      boolean var14 = false /* VF: Semaphore variable */;

      try {
         var14 = true;
         scd.convertFromSyncObject(so, reply, version);
         if (ur != null) {
            flags |= ur.getFlags() & 1;
            var14 = false;
         } else {
            var14 = false;
         }
      } finally {
         if (var14) {
            if (ur != null) {
               ur.setFlags(0);
            }
         }
      }

      if (ur != null) {
         ur.setFlags(0);
      }

      boolean abort = false;
      if (extendedCommand) {
         if ((flags & 1) == 1 && !this._canSendEncoded) {
            abort = true;
         }

         int tempPos = reply.getPosition();
         reply.setPosition(position);
         reply.writeShort(flags);
         reply.setPosition(tempPos);
      } else if ((flags & 1) == 1) {
         abort = true;
      }

      if (abort) {
         EventLogger.logEvent(-7509200465648525729L, "Wide data synch aborted".getBytes(), 5);
         throw new Object();
      }

      this.sendReply(false);
   }

   private final boolean previousOpRanOutOfMemory() {
      if (this._previousLowMemoryFailed) {
         this._previousLowMemoryFailed = false;
         return true;
      } else {
         return false;
      }
   }

   private final int addSyncRecord(SerialSyncCollectionData scd, boolean extendedCommand) {
      if (this._restoreAction == 1) {
         this._restoreAction = 2;
      }

      if (!scd.isWritable()) {
         return 2;
      }

      DatagramBase command = this._command;
      int version = command.readByte();
      int uniqueID = command.readInt();
      int dirty = command.readByte();
      if (extendedCommand) {
         uniqueID = 0;
      }

      this._appData.setData(command.getArray(), command.getPosition(), command.available());
      SerialSyncDaemon$UidWrapper uidWrapper = new SerialSyncDaemon$UidWrapper();
      int convertReturnCode = scd.convertToSyncObjectAndAdd(this._appData, version, uniqueID, dirty, uidWrapper);
      this._appData.setData(this._dummyBytes, 0, 0);
      if (this.previousOpRanOutOfMemory()) {
         return 8;
      }

      this._lowMemoryFailed = false;
      if ((++this._periodicCounter & 63) == 0) {
         LowMemoryManager.poll();
      }

      if (this._lowMemoryFailed) {
         this._previousLowMemoryFailed = true;
      }

      if (convertReturnCode != 0) {
         return convertReturnCode;
      }

      scd.incrementCount();
      scd.setStale(true);
      if (extendedCommand) {
         DatagramBase reply = this.resetReply();
         reply.writeByte(85);
         reply.writeInt(uidWrapper.getUid());
         this.sendReply(false);
      }

      return 0;
   }

   private final int updateSyncRecord(SerialSyncCollectionData scd, SyncObject so, int handle, int version) {
      SyncCollection sc = scd.getSyncCollection();
      if (!scd.isWritable()) {
         return 2;
      }

      DatagramBase command = this._command;
      SyncConverter c = sc.getSyncConverter();
      if (c == null) {
         return 1;
      }

      this._appData.setData(command.getArray(), command.getPosition(), command.available());
      SyncObject o = c.convert(this._appData, version, so.getUID());
      this._appData.setData(this._dummyBytes, 0, 0);
      if (o == null) {
         return 2;
      }

      if (this.previousOpRanOutOfMemory()) {
         return 8;
      }

      this._lowMemoryFailed = false;
      if (!sc.updateSyncObject(so, o)) {
         return 2;
      }

      if ((++this._periodicCounter & 63) == 0) {
         LowMemoryManager.poll();
      }

      if (this._lowMemoryFailed) {
         this._previousLowMemoryFailed = true;
      }

      scd.getSyncObjects()[handle - scd.getHandleBase()] = so;
      return 0;
   }

   private final int cleanCollection(SerialSyncCollectionData scd) {
      if (!scd.isWritable()) {
         return 2;
      }

      synchronized (this._synchObject) {
         scd.cleanCollection();
      }

      Memory.persistentGC();
      return 0;
   }

   private final int cleanDirtyFlag(SerialSyncCollectionData scd) {
      SyncCollection sc = scd.getSyncCollection();
      if (!scd.isWritable()) {
         return 2;
      }

      for (SyncObject so : scd.getSyncObjects()) {
         if (so != null) {
            sc.clearSyncObjectDirty(so);
         }
      }

      return 0;
   }

   private final int setSummaryState(SerialSyncCollectionData scd, SyncObject so, int handle, int version) {
      SyncCollection sc = scd.getSyncCollection();
      if (!scd.isWritable()) {
         return 2;
      }

      int dirtyFlag = this._command.readUnsignedByte();
      int stateInfo = this._command.readInt();
      if (sc instanceof Object) {
         ((StateInfoListener)sc).setStateInfo(so, version, stateInfo);
      }

      if (dirtyFlag != 0) {
         sc.setSyncObjectDirty(so);
         return 0;
      } else {
         sc.clearSyncObjectDirty(so);
         return 0;
      }
   }

   private final DatagramBase resetReply() {
      this._reply.simpleReset();
      this._reply.setLength(0);
      this._reply.setBigEndian(false);
      return this._reply;
   }

   private final DatagramBase resetReply(int length) {
      this.resetReply();
      this._reply.ensureCapacity(length);
      this._reply.setLength(0);
      return this._reply;
   }

   private final void sendReply(boolean more) {
      if (more) {
         this._reply.setFlag(8, true);
      }

      this._connection.send(this._reply);
      if (!more) {
         this._connection.receive(this._reply);
         int reply = this._reply.readShort();
         if (!this._reply.isFlagSet(1) || reply != 0) {
            if (this._reply.isFlagSet(2)) {
               this._linkStarted = true;
            } else if (this._reply.isFlagSet(4)) {
               this._linkStarted = false;
            }

            if (reply == 9) {
               throw new Object();
            }

            throw new Object("Bad reply");
         }
      }
   }

   @Override
   public final void lowMemoryManagerFailed() {
      this._lowMemoryFailed = true;
   }

   private final void sendReply(int reply) {
      DatagramBase rdg = this._reply;
      rdg.rewind();
      rdg.setFlag(1, true);
      rdg.writeShort(reply);
      this._connection.send(rdg);
   }

   private final void freeIOData() {
      this._command.rewind();
      this._command.trim();
      this._reply.rewind();
      this._reply.trim();
      this._appData.setData(this._dummyBytes, 0, 0);
   }

   protected static final boolean isWritable(SyncCollection sc) {
      if (sc instanceof Object) {
         SyncCollectionStatusProvider scsp = (SyncCollectionStatusProvider)sc;
         if (!scsp.isWritableForSerialSync()) {
            return false;
         }
      }

      return !((SyncManagerImpl)SyncManager.getInstance()).isOTASynchronizationEnabled(sc);
   }
}
