package net.rim.device.api.browser.push;

import java.util.Hashtable;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.PushInputStream;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.push.PushLoader;
import net.rim.device.cldc.io.utility.PacketLogger;
import net.rim.device.internal.browser.wap.IWAPPushProvider;
import net.rim.device.internal.browser.wap.WAPPushProviderRegistry;
import net.rim.vm.Array;

public final class PushProcessor implements Pushlet, GlobalEventListener, PushEventLogger, IWAPPushProvider, RadioStatusListener {
   private boolean _started;
   private boolean _optionsRegistered;
   private Object _syncObject = new Object();
   private PushSource[] _connection;
   private Hashtable _applications = (Hashtable)(new Object());
   private Pushlet _defaultApplication;
   private Application _dispatchApplication;
   private boolean _wapProvisioningEnabled;
   private boolean _doPPGConnect;
   private static PushProcessor _instance;
   private static final long APPLICATION_REGISTRY_NAME;

   public final Application getDispatchApplication() {
      return this._dispatchApplication;
   }

   public final void start() {
      synchronized (this._syncObject) {
         if (this._dispatchApplication != null) {
            this._dispatchApplication.invokeLater(new PushProcessor$StartStop(true));
         }
      }
   }

   public final void stop() {
      synchronized (this._syncObject) {
         if (this._dispatchApplication != null) {
            this._dispatchApplication.invokeLater(new PushProcessor$StartStop(false));
         }
      }
   }

   public final void mobilityManagementEvent(int eventCode, int cause) {
   }

   @Override
   public final void pushReceived(HttpHeaders headers, PushInputStream pushMessage) {
      if (headers != null && pushMessage != null) {
         EventLogger.logEvent(-1133226195824034738L, 1349544304, 5);
         if (!PushOptions.getOptions().getWAPEnablePush() && (pushMessage.getConnectionType() == 2 || pushMessage.getConnectionType() == 1)) {
            try {
               pushMessage.decline(236);
               return;
            } finally {
               return;
            }
         }

         if (this._dispatchApplication != null) {
            this._dispatchApplication.invokeLater(new PushProcessor$ProcessPush(this, headers, pushMessage));
         }
      }
   }

   @Override
   public final boolean pushEnabled() {
      return WAPPushSource.doWAPPushServiceRecordsExist() && PushOptions.getOptions().getWAPEnablePush();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4220058463650496006L || guid == 2522898683889177438L || guid == 8288627527798139133L) {
         ServiceBook sb = ServiceBook.getSB();
         ServiceRecord sr = sb.getRecordById(data0);
         if (sr != null) {
            String cid = sr.getCid();
            EventLogger.logEvent(-1133226195824034738L, 1349546851, 5);
            if (StringUtilities.strEqualIgnoreCase(cid, "WAPPushConfig", 1701707776)) {
               if ((guid == -4220058463650496006L || guid == 8288627527798139133L) && sr.getType() == 0) {
                  WAPPushSource source = WAPPushSource.getService(sr);
                  if (source != null) {
                     PushOptions.getOptions().addFilters(source);
                  }
               }

               if (guid == -4220058463650496006L) {
                  this._doPPGConnect = true;
               }

               this.stop();
               this.start();
            }
         }
      }
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void messageReceived(HttpHeaders param1, PushInputStream param2) {
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
      // 00: ldc2_w -1133226195824034738
      // 03: ldc_w 1349545837
      // 06: bipush 5
      // 08: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 0b: pop
      // 0c: aload 1
      // 0d: ldc_w "x-wap-application-id"
      // 10: invokevirtual net/rim/device/api/io/http/HttpHeaders.getPropertyValue (Ljava/lang/String;)Ljava/lang/String;
      // 13: astore 3
      // 14: aconst_null
      // 15: astore 4
      // 17: aconst_null
      // 18: astore 5
      // 1a: aload 0
      // 1b: getfield net/rim/device/api/browser/push/PushProcessor._syncObject Ljava/lang/Object;
      // 1e: dup
      // 1f: astore 6
      // 21: monitorenter
      // 22: aload 3
      // 23: ifnull 39
      // 26: aload 0
      // 27: getfield net/rim/device/api/browser/push/PushProcessor._applications Ljava/util/Hashtable;
      // 2a: aload 3
      // 2b: ldc_w 1701707776
      // 2e: invokestatic net/rim/device/api/util/StringUtilities.toLowerCase (Ljava/lang/String;I)Ljava/lang/String;
      // 31: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 34: checkcast java/lang/Object
      // 37: astore 4
      // 39: aload 4
      // 3b: ifnonnull 4b
      // 3e: aload 0
      // 3f: getfield net/rim/device/api/browser/push/PushProcessor._defaultApplication Lnet/rim/device/api/browser/push/Pushlet;
      // 42: ifnull 4b
      // 45: aload 0
      // 46: getfield net/rim/device/api/browser/push/PushProcessor._defaultApplication Lnet/rim/device/api/browser/push/Pushlet;
      // 49: astore 5
      // 4b: aload 6
      // 4d: monitorexit
      // 4e: goto 59
      // 51: astore 7
      // 53: aload 6
      // 55: monitorexit
      // 56: aload 7
      // 58: athrow
      // 59: aload 4
      // 5b: ifnull 8d
      // 5e: ldc2_w -1133226195824034738
      // 61: ldc_w 1349542241
      // 64: bipush 5
      // 66: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 69: pop
      // 6a: invokestatic net/rim/device/api/browser/push/PushOptions.getOptions ()Lnet/rim/device/api/browser/push/PushOptions;
      // 6d: astore 6
      // 6f: aload 6
      // 71: invokevirtual net/rim/device/api/browser/push/PushOptions.getAllowOtherApplications ()Z
      // 74: ifeq 83
      // 77: aload 4
      // 79: aload 1
      // 7a: aload 2
      // 7b: invokeinterface net/rim/device/api/browser/push/Pushlet.messageReceived (Lnet/rim/device/api/io/http/HttpHeaders;Lnet/rim/device/api/io/http/PushInputStream;)V 3
      // 80: goto b1
      // 83: aload 2
      // 84: sipush 236
      // 87: invokevirtual net/rim/device/api/io/http/PushInputStream.decline (I)V
      // 8a: goto b1
      // 8d: aload 5
      // 8f: ifnull aa
      // 92: ldc2_w -1133226195824034738
      // 95: ldc_w 1349547364
      // 98: bipush 5
      // 9a: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 9d: pop
      // 9e: aload 5
      // a0: aload 1
      // a1: aload 2
      // a2: invokeinterface net/rim/device/api/browser/push/Pushlet.messageReceived (Lnet/rim/device/api/io/http/HttpHeaders;Lnet/rim/device/api/io/http/PushInputStream;)V 3
      // a7: goto b1
      // aa: aload 2
      // ab: sipush 236
      // ae: invokevirtual net/rim/device/api/io/http/PushInputStream.decline (I)V
      // b1: aload 2
      // b2: invokevirtual java/io/DataInputStream.close ()V
      // b5: return
      // b6: astore 3
      // b7: return
      // b8: astore 3
      // b9: ldc2_w -1133226195824034738
      // bc: new java/lang/Object
      // bf: dup
      // c0: ldc_w "PPme\n"
      // c3: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // c6: aload 3
      // c7: invokevirtual java/lang/Throwable.toString ()Ljava/lang/String;
      // ca: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // cd: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // d0: invokevirtual java/lang/String.getBytes ()[B
      // d3: bipush 0
      // d4: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // d7: pop
      // d8: aload 2
      // d9: sipush 237
      // dc: invokevirtual net/rim/device/api/io/http/PushInputStream.decline (I)V
      // df: goto e4
      // e2: astore 4
      // e4: aload 2
      // e5: invokevirtual java/io/DataInputStream.close ()V
      // e8: return
      // e9: astore 3
      // ea: return
      // eb: astore 8
      // ed: aload 2
      // ee: invokevirtual java/io/DataInputStream.close ()V
      // f1: goto f6
      // f4: astore 9
      // f6: aload 8
      // f8: athrow
      // try (18 -> 38): 39 null
      // try (39 -> 42): 39 null
      // try (80 -> 82): 83 null
      // try (5 -> 80): 85 null
      // try (86 -> 102): 103 null
      // try (104 -> 106): 107 null
      // try (5 -> 80): 109 null
      // try (85 -> 104): 109 null
      // try (110 -> 112): 113 null
      // try (109 -> 110): 109 null
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      EventLogger.logEvent(-1133226195824034738L, 1349545587, 5);
      if ((service & 4) != 0) {
         this.dataAvailable();
      }
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      if ((service & 4) != 0) {
         this.dataAvailable();
      }
   }

   public static final PushProcessor getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (PushProcessor)applicationRegistry.getOrWaitFor(4580961082022641941L);
         if (_instance == null) {
            _instance = new PushProcessor();
            applicationRegistry.put(4580961082022641941L, _instance);
         }

         synchronized (_instance) {
            if (_instance._dispatchApplication == null && PushLoader._dispatchApplication != null) {
               _instance._dispatchApplication = PushLoader._dispatchApplication;
               _instance._dispatchApplication.addGlobalEventListener(_instance);
               _instance._dispatchApplication.addRadioListener(_instance);
            }
         }
      }

      return _instance;
   }

   private PushProcessor() {
      EventLogger.register(-1133226195824034738L, "net.rim.browserpush", 2);
      WAPPushProviderRegistry.setInstance(this);
   }

   static final void setWAPProvisioningMode(boolean value) {
      getInstance();
      synchronized (_instance._syncObject) {
         _instance._wapProvisioningEnabled = value;
         _instance.stop();
         _instance.start();
      }
   }

   private final void startImpl() {
      PacketLogger.getInstance();
      this.registerOptions();
      EventLogger.logEvent(-1133226195824034738L, 1349546866, 5);
      synchronized (this._syncObject) {
         if (!this._started) {
            this._connection = null;
            PushOptions options = PushOptions.getOptions();
            if (options.getEnablePush()) {
               boolean smsDetected = false;
               this._connection = PushSource.getAllServices();
               if (this._connection != null) {
                  for (int i = 0; i < this._connection.length; i++) {
                     try {
                        if (this._connection[i] != null) {
                           this._connection[i].startListening(this);
                           if (this._connection[i].getSourceType() == 2) {
                              smsDetected |= ((WAPPushSource)this._connection[i]).getConnectionType() == 6;
                           }

                           if (this._doPPGConnect) {
                              this._connection[i].startPPGConnection(this);
                           }
                        }
                     } finally {
                        continue;
                     }
                  }
               }

               if (!smsDetected && this._wapProvisioningEnabled && options.getWAPEnablePush()) {
                  if (this._connection == null) {
                     this._connection = new PushSource[1];
                  } else {
                     Array.resize(this._connection, this._connection.length + 1);
                  }

                  this._connection[this._connection.length - 1] = new WAPPushSource(
                     2948, null, null, null, 6, 2, null, 2, null, 2, null, 2, 2, 2, 0, 0, null, -1, -1, 1, null, null
                  );
                  this._connection[this._connection.length - 1].startListening(this);
               }

               this._started = true;
               this._doPPGConnect = false;
            }
         }
      }
   }

   private final void stopImpl() {
      EventLogger.logEvent(-1133226195824034738L, 1349546868, 5);
      synchronized (this._syncObject) {
         if (this._started) {
            if (this._connection != null) {
               for (int i = 0; i < this._connection.length; i++) {
                  if (this._connection[i] != null) {
                     this._connection[i].close();
                     this._connection[i] = null;
                  }
               }

               this._connection = null;
            }

            this._started = false;
         }
      }
   }

   static final void registerDefaultPushlet(Pushlet app) {
      getInstance();
      synchronized (_instance._syncObject) {
         if (_instance._defaultApplication == null) {
            _instance._defaultApplication = app;
         }

         _instance.start();
      }
   }

   static final void deregisterDefaultPushlet(Pushlet app) {
      getInstance();
      synchronized (_instance._syncObject) {
         if (_instance._defaultApplication == app) {
            _instance._defaultApplication = null;
         }

         if (_instance._started && _instance._defaultApplication == null && _instance._applications.isEmpty()) {
            _instance.stop();
         }
      }
   }

   private final void registerOptions() {
      if (!this._optionsRegistered) {
         this._optionsRegistered = true;
         PushLoader.registerOptions();
      }
   }

   public static final void registerPushlet(String applicationId, Pushlet app) {
      if (applicationId != null && app != null) {
         applicationId = StringUtilities.toLowerCase(applicationId, 1701707776);
         getInstance();
         synchronized (_instance._syncObject) {
            if (!_instance._applications.containsKey(applicationId)) {
               _instance._applications.put(applicationId, app);
               if (!_instance._started) {
                  _instance.start();
               }
            }
         }
      } else {
         throw new Object();
      }
   }

   public static final void deregisterPushlet(String applicationId, Pushlet app) {
      if (applicationId != null && app != null) {
         getInstance();
         synchronized (_instance._syncObject) {
            Pushlet anApp = (Pushlet)_instance._applications.get(applicationId);
            if (anApp != null) {
               if (app != anApp) {
                  throw new Object();
               }

               _instance._applications.remove(applicationId);
            }

            if (_instance._started && _instance._defaultApplication == null && _instance._applications.isEmpty()) {
               _instance.stop();
            }
         }
      } else {
         throw new Object();
      }
   }

   private final void dataAvailable() {
      EventLogger.logEvent(-1133226195824034738L, 1349543009, 5);
      synchronized (this._syncObject) {
         boolean oldPPGConnect = this._doPPGConnect;
         this._doPPGConnect = true;
         if (this._started && this._connection != null) {
            for (int i = 0; i < this._connection.length; i++) {
               if (this._connection[i] != null) {
                  this._connection[i].dataNetworkChanged(true);
               }
            }

            if (!oldPPGConnect && this._dispatchApplication != null) {
               this._dispatchApplication.invokeLater(new PushProcessor$1(this), 10000, false);
            }
         }
      }
   }

   private final void doPPGConnections() {
      EventLogger.logEvent(-1133226195824034738L, 1349546099, 5);
      synchronized (this._syncObject) {
         if (this._started && this._connection != null) {
            for (int i = 0; i < this._connection.length; i++) {
               if (this._connection[i] != null) {
                  this._connection[i].startPPGConnection(this);
               }
            }

            this._doPPGConnect = false;
         }
      }
   }
}
