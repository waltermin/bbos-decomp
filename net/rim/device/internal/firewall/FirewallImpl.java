package net.rim.device.internal.firewall;

import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.MIDletSecurity;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.vm.Array;
import net.rim.vm.EventLog;

public final class FirewallImpl extends Firewall {
   private FirewallImpl$FirewallSyncItem _syncItem;
   private PersistentObject _persistentObject;
   private Vector _settings;
   private ToIntHashtable _pipeControl;
   private FirewallImpl$SettingStore _settingStore;
   private boolean _doCommits;
   private Vector _blockedCountListeners;
   private IntHashtable _blockings;
   private IntHashtable _droppings;
   private ResourceBundle _rb = ResourceBundle.getBundle(3100685609005034344L, "net.rim.device.internal.resource.Firewall");
   private static final long KEY;
   private static final long GUID;
   private static String[] ALLOWED_PROTOCOLS_WHITELIST = new String[]{
      "apdu",
      "btgoep",
      "btl2cap",
      "btspp",
      "cod",
      "comm",
      "data",
      "datagram",
      "file",
      "http",
      "https",
      "jar",
      "jcrmi",
      "mms",
      "mms_send",
      "mms_receive",
      "proxyhttp",
      "pushregistry",
      "sms",
      "sms_send",
      "sms_receive",
      "socket",
      "ssl",
      "tcp",
      "tls",
      "udp",
      "wtls"
   };

   private final FirewallImpl$Setting findSetting(String moduleName, int appIndex, String protocol, String target, byte[] moduleHash) {
      for (int i = this._settings.size() - 1; i >= 0; i--) {
         FirewallImpl$Setting s = (FirewallImpl$Setting)this._settings.elementAt(i);
         if (moduleName.equals(s._moduleName)
            && appIndex == s._appIndex
            && protocol.equals(s._protocol)
            && (moduleHash == null || Arrays.equals(moduleHash, s._moduleHash))
            && StringUtilities.strEqual(target, s._target)) {
            return s;
         }
      }

      return null;
   }

   private final FirewallImpl$Setting[] findSettings(String moduleName, byte[] moduleHash) {
      FirewallImpl$Setting[] sa = null;

      for (int i = this._settings.size() - 1; i >= 0; i--) {
         FirewallImpl$Setting s = (FirewallImpl$Setting)this._settings.elementAt(i);
         if (moduleName.equals(s._moduleName) && Arrays.equals(moduleHash, s._moduleHash)) {
            if (sa == null) {
               sa = new FirewallImpl$Setting[1];
            } else {
               Array.resize(sa, sa.length + 1);
            }

            sa[sa.length - 1] = s;
         }
      }

      return sa;
   }

   public FirewallImpl() {
      this._persistentObject = RIMPersistentStore.getPersistentObject(-6336176786833674023L);
      this._settingStore = (FirewallImpl$SettingStore)this._persistentObject.getContents();
      this._doCommits = true;
      if (this._settingStore == null) {
         synchronized (this._persistentObject) {
            this._settingStore = (FirewallImpl$SettingStore)this._persistentObject.getContents();
            if (this._settingStore == null) {
               this._settingStore = new FirewallImpl$SettingStore();
               this._persistentObject.setContents(this._settingStore, 51);
               this._persistentObject.commit();
            }
         }
      }

      this._settings = this._settingStore._settings;
      this._pipeControl = this._settingStore._pipeControl;
      this._blockings = this._settingStore._blockings;
      this._droppings = this._settingStore._droppings;
      this._blockedCountListeners = (Vector)(new Object());
      this.syncSetup();
      EventLog.registerApp(7954265007165122082L, 2, "FW");
      Proxy.getInstance().addRealtimeClockListener(new FirewallImpl$BlockedCountSyncRunnable(this, null));
   }

   protected final void syncSetup() {
      this._syncItem = new FirewallImpl$FirewallSyncItem(this);
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.enableSynchronization(this._syncItem);
      }
   }

   protected final void commit() {
      this._persistentObject.commit();
      this._syncItem.fireSyncItemUpdated();
   }

   private final void setPermission(String moduleName, int appIndex, String protocol, String target, int permission) {
      switch (permission) {
         case -1:
         case 2:
         case 3:
            break;
         case 0:
            permission = 2;
            break;
         case 1:
         default:
            permission = 2;
            target = null;
            break;
         case 4:
            permission = 6;
            break;
         case 5:
            permission = 6;
            target = null;
      }

      int moduleHandle = CodeModuleManager.getModuleHandle(moduleName);
      if (moduleHandle != 0) {
         byte[] moduleHash = CodeModuleManager.getModuleHash(moduleHandle);
         synchronized (this._settings) {
            FirewallImpl$Setting setting = this.findSetting(moduleName, appIndex, protocol, target, null);
            if (setting != null) {
               this._settings.removeElement(setting);
            }

            this._settings.addElement(new FirewallImpl$Setting(moduleName, appIndex, protocol, target, permission, moduleHash));
         }

         if (this._doCommits) {
            this.commit();
         }
      }
   }

   private final int getPermission(String moduleName, int appIndex, String protocol, String target) {
      int moduleHandle = CodeModuleManager.getModuleHandle(moduleName);
      byte[] moduleHash = CodeModuleManager.getModuleHash(moduleHandle);
      FirewallImpl$Setting s = this.findSetting(moduleName, appIndex, protocol, target, moduleHash);
      if (s == null) {
         s = this.findSetting(moduleName, appIndex, protocol, null, moduleHash);
      }

      return s == null ? 3 : s._permission;
   }

   protected final synchronized int askUser(String application, String protocol, String target) {
      String finalapplication = application;
      String finalprotocol = protocol;
      int result = 0;
      String finaltarget;
      if (StringUtilities.strEqualIgnoreCase(target.substring(0, 2), "//", 1701707776)) {
         finaltarget = this._rb.getString(12);
      } else {
         finaltarget = target;
      }

      String[] details = new Object[]{finalapplication, finalprotocol, finaltarget};
      FirewallDialog dialog = (FirewallDialog)(new Object(details, finaltarget == null || finaltarget.length() <= 0));
      BackgroundDialog.showOnProxy(dialog);
      result = dialog.getSelectedValue();
      if (dialog.isProtocolChecked()) {
         result = result == 2 ? 1 : 5;
      } else if (dialog.isTargetChecked()) {
         result = result == 2 ? 0 : 4;
      }

      return result;
   }

   @Override
   public final boolean allowConnection(String protocol, String target, boolean internal) {
      return this.allowConnection(protocol, target, internal ? 1 : 2);
   }

   @Override
   public final boolean allowConnection(String protocol, String target, int properties) {
      return this.allowConnection(protocol, target, properties, null);
   }

   @Override
   public final boolean allowConnection(String param1, String param2, int param3, FirewallContext param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: getstatic net/rim/device/internal/firewall/FirewallImpl.ALLOWED_PROTOCOLS_WHITELIST [Ljava/lang/String;
      // 003: aload 1
      // 004: invokestatic net/rim/device/api/util/Arrays.contains ([Ljava/lang/Object;Ljava/lang/Object;)Z
      // 007: ifne 00c
      // 00a: bipush 0
      // 00b: ireturn
      // 00c: aload 2
      // 00d: ifnull 047
      // 010: aload 1
      // 011: ldc_w "apdu"
      // 014: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 017: ifne 047
      // 01a: aload 1
      // 01b: ldc_w "jcrmi"
      // 01e: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 021: ifne 047
      // 024: aload 2
      // 025: ldc_w "utf-8"
      // 028: invokestatic net/rim/device/cldc/io/utility/URIDecoder.decode (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
      // 02b: astore 2
      // 02c: new java/lang/Object
      // 02f: dup
      // 030: aload 1
      // 031: aload 2
      // 032: invokespecial net/rim/device/cldc/io/utility/URL.<init> (Ljava/lang/String;Ljava/lang/String;)V
      // 035: astore 5
      // 037: aload 5
      // 039: invokevirtual net/rim/device/cldc/io/utility/URL.getHost ()Ljava/lang/String;
      // 03c: astore 2
      // 03d: goto 047
      // 040: astore 5
      // 042: goto 047
      // 045: astore 5
      // 047: aload 0
      // 048: aload 1
      // 049: aload 2
      // 04a: invokespecial net/rim/device/internal/firewall/FirewallImpl.getMIDPPermission (Ljava/lang/String;Ljava/lang/String;)I
      // 04d: istore 5
      // 04f: bipush 1
      // 050: istore 6
      // 052: iload 5
      // 054: bipush -1
      // 056: if_icmpeq 060
      // 059: iload 5
      // 05b: invokestatic net/rim/device/internal/system/MIDletSecurity.checkPermissionNoPrompt (I)I
      // 05e: istore 6
      // 060: aload 2
      // 061: ifnonnull 068
      // 064: ldc_w ""
      // 067: astore 2
      // 068: invokestatic net/rim/vm/Process.currentProcess ()Lnet/rim/vm/Process;
      // 06b: invokevirtual net/rim/vm/Process.getModuleName ()Ljava/lang/String;
      // 06e: astore 7
      // 070: invokestatic net/rim/device/api/system/ApplicationDescriptor.currentApplicationDescriptor ()Lnet/rim/device/api/system/ApplicationDescriptor;
      // 073: astore 8
      // 075: iload 3
      // 076: bipush 1
      // 077: iand
      // 078: ifeq 07f
      // 07b: bipush 1
      // 07c: goto 080
      // 07f: bipush 0
      // 080: istore 9
      // 082: iload 3
      // 083: bipush 2
      // 085: iand
      // 086: ifeq 08d
      // 089: bipush 1
      // 08a: goto 08e
      // 08d: bipush 0
      // 08e: istore 10
      // 090: iload 3
      // 091: bipush 16
      // 093: iand
      // 094: ifeq 09b
      // 097: bipush 1
      // 098: goto 09c
      // 09b: bipush 0
      // 09c: istore 11
      // 09e: iload 3
      // 09f: bipush 32
      // 0a1: iand
      // 0a2: ifeq 0a9
      // 0a5: bipush 1
      // 0a6: goto 0aa
      // 0a9: bipush 0
      // 0aa: istore 12
      // 0ac: iload 3
      // 0ad: bipush 64
      // 0af: iand
      // 0b0: ifeq 0b7
      // 0b3: bipush 1
      // 0b4: goto 0b8
      // 0b7: bipush 0
      // 0b8: istore 13
      // 0ba: iload 3
      // 0bb: sipush 128
      // 0be: iand
      // 0bf: ifeq 0c6
      // 0c2: bipush 1
      // 0c3: goto 0c7
      // 0c6: bipush 0
      // 0c7: istore 14
      // 0c9: iload 3
      // 0ca: sipush 256
      // 0cd: iand
      // 0ce: ifeq 0d5
      // 0d1: bipush 1
      // 0d2: goto 0d6
      // 0d5: bipush 0
      // 0d6: istore 15
      // 0d8: iload 9
      // 0da: ifne 0ff
      // 0dd: iload 10
      // 0df: ifne 0ff
      // 0e2: iload 11
      // 0e4: ifne 0ff
      // 0e7: iload 12
      // 0e9: ifne 0ff
      // 0ec: iload 13
      // 0ee: ifne 0ff
      // 0f1: iload 14
      // 0f3: ifne 0ff
      // 0f6: iload 15
      // 0f8: ifne 0ff
      // 0fb: bipush 1
      // 0fc: goto 100
      // 0ff: bipush 0
      // 100: istore 16
      // 102: aconst_null
      // 103: astore 17
      // 105: aload 4
      // 107: ifnull 111
      // 10a: aload 4
      // 10c: invokevirtual net/rim/device/internal/firewall/FirewallContext.getAdditionalModules ()[I
      // 10f: astore 17
      // 111: aload 0
      // 112: invokevirtual net/rim/device/internal/firewall/FirewallImpl.isEnabled ()Z
      // 115: ifne 11b
      // 118: goto 62a
      // 11b: aload 0
      // 11c: invokespecial net/rim/device/internal/firewall/FirewallImpl.isApplicationControlSupported ()Z
      // 11f: ifne 125
      // 122: goto 62a
      // 125: bipush 0
      // 126: istore 18
      // 128: bipush 0
      // 129: istore 19
      // 12b: bipush 0
      // 12c: istore 20
      // 12e: bipush 0
      // 12f: istore 21
      // 131: bipush 0
      // 132: istore 22
      // 134: bipush 0
      // 135: istore 23
      // 137: bipush 0
      // 138: istore 24
      // 13a: bipush 0
      // 13b: istore 25
      // 13d: bipush 0
      // 13e: istore 26
      // 140: bipush 0
      // 141: istore 27
      // 143: bipush 0
      // 144: istore 28
      // 146: bipush 0
      // 147: istore 29
      // 149: iload 12
      // 14b: ifeq 16f
      // 14e: bipush 1
      // 14f: aload 17
      // 151: invokestatic net/rim/device/internal/applicationcontrol/ApplicationControl.isPIMAllowed (Z[I)Z
      // 154: ifeq 163
      // 157: aload 0
      // 158: iload 18
      // 15a: bipush 0
      // 15b: invokespecial net/rim/device/internal/firewall/FirewallImpl.combinePermissions (II)I
      // 15e: istore 18
      // 160: goto 16f
      // 163: aload 0
      // 164: iload 18
      // 166: bipush 1
      // 167: invokespecial net/rim/device/internal/firewall/FirewallImpl.combinePermissions (II)I
      // 16a: istore 18
      // 16c: bipush 1
      // 16d: istore 21
      // 16f: iload 15
      // 171: ifeq 195
      // 174: bipush 1
      // 175: aload 17
      // 177: invokestatic net/rim/device/internal/applicationcontrol/ApplicationControl.isFileApiAllowed (Z[I)Z
      // 17a: ifeq 189
      // 17d: aload 0
      // 17e: iload 18
      // 180: bipush 0
      // 181: invokespecial net/rim/device/internal/firewall/FirewallImpl.combinePermissions (II)I
      // 184: istore 18
      // 186: goto 195
      // 189: aload 0
      // 18a: iload 18
      // 18c: bipush 1
      // 18d: invokespecial net/rim/device/internal/firewall/FirewallImpl.combinePermissions (II)I
      // 190: istore 18
      // 192: bipush 1
      // 193: istore 20
      // 195: iload 14
      // 197: ifeq 1fe
      // 19a: bipush 24
      // 19c: bipush 16
      // 19e: bipush 1
      // 19f: invokestatic net/rim/device/api/itpolicy/ITPolicy.getBoolean (IIZ)Z
      // 1a2: ifne 1dd
      // 1a5: aload 0
      // 1a6: aload 0
      // 1a7: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 1aa: bipush 27
      // 1ac: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 1af: bipush 3
      // 1b1: anewarray 983
      // 1b4: dup
      // 1b5: bipush 0
      // 1b6: aload 7
      // 1b8: aastore
      // 1b9: dup
      // 1ba: bipush 1
      // 1bb: aload 0
      // 1bc: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 1bf: bipush 33
      // 1c1: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 1c4: aastore
      // 1c5: dup
      // 1c6: bipush 2
      // 1c8: aload 0
      // 1c9: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 1cc: bipush 25
      // 1ce: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 1d1: aastore
      // 1d2: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 1d5: ldc_w "AC-ITP-lcD"
      // 1d8: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 1db: bipush 0
      // 1dc: ireturn
      // 1dd: bipush 1
      // 1de: aload 17
      // 1e0: invokestatic net/rim/device/internal/applicationcontrol/ApplicationControl.isLocalConnectionAllowed (Z[I)Z
      // 1e3: ifeq 1f2
      // 1e6: aload 0
      // 1e7: iload 18
      // 1e9: bipush 0
      // 1ea: invokespecial net/rim/device/internal/firewall/FirewallImpl.combinePermissions (II)I
      // 1ed: istore 18
      // 1ef: goto 1fe
      // 1f2: aload 0
      // 1f3: iload 18
      // 1f5: bipush 1
      // 1f6: invokespecial net/rim/device/internal/firewall/FirewallImpl.combinePermissions (II)I
      // 1f9: istore 18
      // 1fb: bipush 1
      // 1fc: istore 25
      // 1fe: iload 13
      // 200: ifne 206
      // 203: goto 2b6
      // 206: bipush 34
      // 208: bipush 1
      // 209: bipush 0
      // 20a: invokestatic net/rim/device/api/itpolicy/ITPolicy.getBoolean (IIZ)Z
      // 20d: ifeq 248
      // 210: aload 0
      // 211: aload 0
      // 212: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 215: bipush 27
      // 217: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 21a: bipush 3
      // 21c: anewarray 1059
      // 21f: dup
      // 220: bipush 0
      // 221: aload 7
      // 223: aastore
      // 224: dup
      // 225: bipush 1
      // 226: aload 0
      // 227: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 22a: bipush 32
      // 22c: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 22f: aastore
      // 230: dup
      // 231: bipush 2
      // 233: aload 0
      // 234: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 237: bipush 25
      // 239: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 23c: aastore
      // 23d: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 240: ldc_w "AC-ITP-btD"
      // 243: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 246: bipush 0
      // 247: ireturn
      // 248: aload 1
      // 249: ldc_w "btspp"
      // 24c: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 24f: ifeq 295
      // 252: bipush 34
      // 254: bipush 5
      // 256: bipush 0
      // 257: invokestatic net/rim/device/api/itpolicy/ITPolicy.getBoolean (IIZ)Z
      // 25a: ifeq 295
      // 25d: aload 0
      // 25e: aload 0
      // 25f: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 262: bipush 27
      // 264: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 267: bipush 3
      // 269: anewarray 1125
      // 26c: dup
      // 26d: bipush 0
      // 26e: aload 7
      // 270: aastore
      // 271: dup
      // 272: bipush 1
      // 273: aload 0
      // 274: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 277: bipush 32
      // 279: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 27c: aastore
      // 27d: dup
      // 27e: bipush 2
      // 280: aload 0
      // 281: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 284: bipush 25
      // 286: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 289: aastore
      // 28a: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 28d: ldc_w "AC-ITP-btD"
      // 290: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 293: bipush 0
      // 294: ireturn
      // 295: bipush 1
      // 296: aload 17
      // 298: invokestatic net/rim/device/internal/applicationcontrol/ApplicationControl.isBluetoothSerialProfileAllowed (Z[I)Z
      // 29b: ifeq 2aa
      // 29e: aload 0
      // 29f: iload 18
      // 2a1: bipush 0
      // 2a2: invokespecial net/rim/device/internal/firewall/FirewallImpl.combinePermissions (II)I
      // 2a5: istore 18
      // 2a7: goto 2b6
      // 2aa: aload 0
      // 2ab: iload 18
      // 2ad: bipush 1
      // 2ae: invokespecial net/rim/device/internal/firewall/FirewallImpl.combinePermissions (II)I
      // 2b1: istore 18
      // 2b3: bipush 1
      // 2b4: istore 26
      // 2b6: iload 11
      // 2b8: ifeq 326
      // 2bb: bipush 40
      // 2bd: bipush 20
      // 2bf: bipush 0
      // 2c0: invokestatic net/rim/device/api/itpolicy/ITPolicy.getBoolean (IIZ)Z
      // 2c3: ifeq 2fe
      // 2c6: aload 0
      // 2c7: aload 0
      // 2c8: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 2cb: bipush 27
      // 2cd: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 2d0: bipush 3
      // 2d2: anewarray 1201
      // 2d5: dup
      // 2d6: bipush 0
      // 2d7: aload 7
      // 2d9: aastore
      // 2da: dup
      // 2db: bipush 1
      // 2dc: aload 0
      // 2dd: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 2e0: bipush 28
      // 2e2: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 2e5: aastore
      // 2e6: dup
      // 2e7: bipush 2
      // 2e9: aload 0
      // 2ea: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 2ed: bipush 25
      // 2ef: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 2f2: aastore
      // 2f3: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 2f6: ldc_w "AC-ITP-wcD"
      // 2f9: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 2fc: bipush 0
      // 2fd: ireturn
      // 2fe: bipush 1
      // 2ff: aload 17
      // 301: invokestatic net/rim/device/internal/applicationcontrol/ApplicationControl.isWiFiAllowed (Z[I)I
      // 304: istore 19
      // 306: iload 19
      // 308: bipush 1
      // 309: if_icmpne 312
      // 30c: bipush 1
      // 30d: istore 24
      // 30f: goto 31c
      // 312: iload 19
      // 314: bipush 2
      // 316: if_icmpne 31c
      // 319: bipush 1
      // 31a: istore 29
      // 31c: aload 0
      // 31d: iload 18
      // 31f: iload 19
      // 321: invokespecial net/rim/device/internal/firewall/FirewallImpl.combinePermissions (II)I
      // 324: istore 18
      // 326: iload 9
      // 328: ifeq 397
      // 32b: bipush 24
      // 32d: bipush 19
      // 32f: bipush 1
      // 330: invokestatic net/rim/device/api/itpolicy/ITPolicy.getBoolean (IIZ)Z
      // 333: ifne 36e
      // 336: aload 0
      // 337: aload 0
      // 338: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 33b: bipush 27
      // 33d: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 340: bipush 3
      // 342: anewarray 1271
      // 345: dup
      // 346: bipush 0
      // 347: aload 7
      // 349: aastore
      // 34a: dup
      // 34b: bipush 1
      // 34c: aload 0
      // 34d: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 350: bipush 30
      // 352: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 355: aastore
      // 356: dup
      // 357: bipush 2
      // 359: aload 0
      // 35a: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 35d: bipush 25
      // 35f: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 362: aastore
      // 363: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 366: ldc_w "AC-ITP-icD"
      // 369: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 36c: bipush 0
      // 36d: ireturn
      // 36e: aload 2
      // 36f: bipush 1
      // 370: aload 17
      // 372: invokestatic net/rim/device/internal/applicationcontrol/ApplicationControl.isInternalConnectionAllowed (Ljava/lang/String;Z[I)I
      // 375: istore 19
      // 377: iload 19
      // 379: bipush 1
      // 37a: if_icmpne 383
      // 37d: bipush 1
      // 37e: istore 22
      // 380: goto 38d
      // 383: iload 19
      // 385: bipush 2
      // 387: if_icmpne 38d
      // 38a: bipush 1
      // 38b: istore 27
      // 38d: aload 0
      // 38e: iload 18
      // 390: iload 19
      // 392: invokespecial net/rim/device/internal/firewall/FirewallImpl.combinePermissions (II)I
      // 395: istore 18
      // 397: iload 16
      // 399: ifne 3a1
      // 39c: iload 10
      // 39e: ifeq 40d
      // 3a1: bipush 24
      // 3a3: bipush 20
      // 3a5: bipush 1
      // 3a6: invokestatic net/rim/device/api/itpolicy/ITPolicy.getBoolean (IIZ)Z
      // 3a9: ifne 3e4
      // 3ac: aload 0
      // 3ad: aload 0
      // 3ae: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 3b1: bipush 27
      // 3b3: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 3b6: bipush 3
      // 3b8: anewarray 1341
      // 3bb: dup
      // 3bc: bipush 0
      // 3bd: aload 7
      // 3bf: aastore
      // 3c0: dup
      // 3c1: bipush 1
      // 3c2: aload 0
      // 3c3: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 3c6: bipush 31
      // 3c8: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 3cb: aastore
      // 3cc: dup
      // 3cd: bipush 2
      // 3cf: aload 0
      // 3d0: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 3d3: bipush 25
      // 3d5: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 3d8: aastore
      // 3d9: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 3dc: ldc_w "AC-ITP-ecD"
      // 3df: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 3e2: bipush 0
      // 3e3: ireturn
      // 3e4: aload 2
      // 3e5: bipush 1
      // 3e6: aload 17
      // 3e8: invokestatic net/rim/device/internal/applicationcontrol/ApplicationControl.isExternalConnectionAllowed (Ljava/lang/String;Z[I)I
      // 3eb: istore 19
      // 3ed: iload 19
      // 3ef: bipush 1
      // 3f0: if_icmpne 3f9
      // 3f3: bipush 1
      // 3f4: istore 23
      // 3f6: goto 403
      // 3f9: iload 19
      // 3fb: bipush 2
      // 3fd: if_icmpne 403
      // 400: bipush 1
      // 401: istore 28
      // 403: aload 0
      // 404: iload 18
      // 406: iload 19
      // 408: invokespecial net/rim/device/internal/firewall/FirewallImpl.combinePermissions (II)I
      // 40b: istore 18
      // 40d: iload 18
      // 40f: bipush 1
      // 410: if_icmpeq 416
      // 413: goto 5b5
      // 416: iload 20
      // 418: ifeq 451
      // 41b: aload 0
      // 41c: aload 0
      // 41d: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 420: bipush 27
      // 422: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 425: bipush 3
      // 427: anewarray 1405
      // 42a: dup
      // 42b: bipush 0
      // 42c: aload 7
      // 42e: aastore
      // 42f: dup
      // 430: bipush 1
      // 431: aload 0
      // 432: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 435: bipush 34
      // 437: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 43a: aastore
      // 43b: dup
      // 43c: bipush 2
      // 43e: aload 0
      // 43f: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 442: bipush 26
      // 444: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 447: aastore
      // 448: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 44b: ldc_w "AC-ifD"
      // 44e: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 451: iload 21
      // 453: ifeq 48c
      // 456: aload 0
      // 457: aload 0
      // 458: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 45b: bipush 27
      // 45d: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 460: bipush 3
      // 462: anewarray 1457
      // 465: dup
      // 466: bipush 0
      // 467: aload 7
      // 469: aastore
      // 46a: dup
      // 46b: bipush 1
      // 46c: aload 0
      // 46d: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 470: bipush 29
      // 472: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 475: aastore
      // 476: dup
      // 477: bipush 2
      // 479: aload 0
      // 47a: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 47d: bipush 26
      // 47f: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 482: aastore
      // 483: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 486: ldc_w "AC-isD"
      // 489: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 48c: iload 24
      // 48e: ifeq 4c7
      // 491: aload 0
      // 492: aload 0
      // 493: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 496: bipush 27
      // 498: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 49b: bipush 3
      // 49d: anewarray 1509
      // 4a0: dup
      // 4a1: bipush 0
      // 4a2: aload 7
      // 4a4: aastore
      // 4a5: dup
      // 4a6: bipush 1
      // 4a7: aload 0
      // 4a8: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 4ab: bipush 28
      // 4ad: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 4b0: aastore
      // 4b1: dup
      // 4b2: bipush 2
      // 4b4: aload 0
      // 4b5: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 4b8: bipush 26
      // 4ba: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 4bd: aastore
      // 4be: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 4c1: ldc_w "AC-wcD"
      // 4c4: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 4c7: iload 22
      // 4c9: ifeq 502
      // 4cc: aload 0
      // 4cd: aload 0
      // 4ce: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 4d1: bipush 27
      // 4d3: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 4d6: bipush 3
      // 4d8: anewarray 1561
      // 4db: dup
      // 4dc: bipush 0
      // 4dd: aload 7
      // 4df: aastore
      // 4e0: dup
      // 4e1: bipush 1
      // 4e2: aload 0
      // 4e3: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 4e6: bipush 30
      // 4e8: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 4eb: aastore
      // 4ec: dup
      // 4ed: bipush 2
      // 4ef: aload 0
      // 4f0: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 4f3: bipush 26
      // 4f5: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 4f8: aastore
      // 4f9: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 4fc: ldc_w "AC-icD"
      // 4ff: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 502: iload 23
      // 504: ifeq 53d
      // 507: aload 0
      // 508: aload 0
      // 509: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 50c: bipush 27
      // 50e: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 511: bipush 3
      // 513: anewarray 1613
      // 516: dup
      // 517: bipush 0
      // 518: aload 7
      // 51a: aastore
      // 51b: dup
      // 51c: bipush 1
      // 51d: aload 0
      // 51e: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 521: bipush 31
      // 523: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 526: aastore
      // 527: dup
      // 528: bipush 2
      // 52a: aload 0
      // 52b: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 52e: bipush 26
      // 530: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 533: aastore
      // 534: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 537: ldc_w "AC-ecD"
      // 53a: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 53d: iload 25
      // 53f: ifeq 578
      // 542: aload 0
      // 543: aload 0
      // 544: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 547: bipush 27
      // 549: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 54c: bipush 3
      // 54e: anewarray 1665
      // 551: dup
      // 552: bipush 0
      // 553: aload 7
      // 555: aastore
      // 556: dup
      // 557: bipush 1
      // 558: aload 0
      // 559: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 55c: bipush 33
      // 55e: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 561: aastore
      // 562: dup
      // 563: bipush 2
      // 565: aload 0
      // 566: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 569: bipush 26
      // 56b: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 56e: aastore
      // 56f: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 572: ldc_w "AC-lcD"
      // 575: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 578: iload 26
      // 57a: ifeq 5b3
      // 57d: aload 0
      // 57e: aload 0
      // 57f: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 582: bipush 27
      // 584: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 587: bipush 3
      // 589: anewarray 1717
      // 58c: dup
      // 58d: bipush 0
      // 58e: aload 7
      // 590: aastore
      // 591: dup
      // 592: bipush 1
      // 593: aload 0
      // 594: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 597: bipush 32
      // 599: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 59c: aastore
      // 59d: dup
      // 59e: bipush 2
      // 5a0: aload 0
      // 5a1: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 5a4: bipush 26
      // 5a6: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 5a9: aastore
      // 5aa: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 5ad: ldc_w "AC-btD"
      // 5b0: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 5b3: bipush 0
      // 5b4: ireturn
      // 5b5: iload 18
      // 5b7: bipush 2
      // 5b9: if_icmpne 615
      // 5bc: bipush 1
      // 5bd: istore 30
      // 5bf: invokestatic net/rim/device/api/system/CodeModuleManager.isMidlet ()Z
      // 5c2: ifeq 5d1
      // 5c5: iload 6
      // 5c7: ifne 5d1
      // 5ca: aload 0
      // 5cb: iload 5
      // 5cd: aload 2
      // 5ce: invokevirtual net/rim/device/internal/firewall/FirewallImpl.midletSecurity_checkPermission (ILjava/lang/String;)V
      // 5d1: iload 30
      // 5d3: ifeq 5f0
      // 5d6: iload 29
      // 5d8: ifeq 5f0
      // 5db: iload 30
      // 5dd: aload 0
      // 5de: aload 7
      // 5e0: ldc_w "WiFi"
      // 5e3: aload 2
      // 5e4: aload 8
      // 5e6: iload 5
      // 5e8: aload 4
      // 5ea: invokevirtual net/rim/device/internal/firewall/FirewallImpl.prompt (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lnet/rim/device/api/system/ApplicationDescriptor;ILnet/rim/device/internal/firewall/FirewallContext;)Z
      // 5ed: iand
      // 5ee: istore 30
      // 5f0: iload 30
      // 5f2: ifeq 612
      // 5f5: iload 27
      // 5f7: ifne 5ff
      // 5fa: iload 28
      // 5fc: ifeq 612
      // 5ff: iload 30
      // 601: aload 0
      // 602: aload 7
      // 604: aload 1
      // 605: aload 2
      // 606: aload 8
      // 608: iload 5
      // 60a: aload 4
      // 60c: invokevirtual net/rim/device/internal/firewall/FirewallImpl.prompt (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lnet/rim/device/api/system/ApplicationDescriptor;ILnet/rim/device/internal/firewall/FirewallContext;)Z
      // 60f: iand
      // 610: istore 30
      // 612: iload 30
      // 614: ireturn
      // 615: invokestatic net/rim/device/api/system/CodeModuleManager.isMidlet ()Z
      // 618: ifeq 628
      // 61b: iload 6
      // 61d: bipush 1
      // 61e: if_icmpge 628
      // 621: aload 0
      // 622: iload 5
      // 624: aload 2
      // 625: invokevirtual net/rim/device/internal/firewall/FirewallImpl.midletSecurity_checkPermission (ILjava/lang/String;)V
      // 628: bipush 1
      // 629: ireturn
      // 62a: aload 0
      // 62b: invokevirtual net/rim/device/internal/firewall/FirewallImpl.isEnabled ()Z
      // 62e: ifne 634
      // 631: goto 76f
      // 634: iload 9
      // 636: iload 15
      // 638: ior
      // 639: istore 9
      // 63b: iload 9
      // 63d: ifeq 683
      // 640: bipush 24
      // 642: bipush 19
      // 644: bipush 1
      // 645: invokestatic net/rim/device/api/itpolicy/ITPolicy.getBoolean (IIZ)Z
      // 648: ifne 6cb
      // 64b: aload 0
      // 64c: aload 0
      // 64d: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 650: bipush 27
      // 652: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 655: bipush 3
      // 657: anewarray 1819
      // 65a: dup
      // 65b: bipush 0
      // 65c: aload 7
      // 65e: aastore
      // 65f: dup
      // 660: bipush 1
      // 661: aload 0
      // 662: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 665: bipush 30
      // 667: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 66a: aastore
      // 66b: dup
      // 66c: bipush 2
      // 66e: aload 0
      // 66f: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 672: bipush 25
      // 674: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 677: aastore
      // 678: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 67b: ldc_w "ITP-icD"
      // 67e: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 681: bipush 0
      // 682: ireturn
      // 683: iload 10
      // 685: ifeq 6cb
      // 688: bipush 24
      // 68a: bipush 20
      // 68c: bipush 1
      // 68d: invokestatic net/rim/device/api/itpolicy/ITPolicy.getBoolean (IIZ)Z
      // 690: ifne 6cb
      // 693: aload 0
      // 694: aload 0
      // 695: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 698: bipush 27
      // 69a: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 69d: bipush 3
      // 69f: anewarray 1877
      // 6a2: dup
      // 6a3: bipush 0
      // 6a4: aload 7
      // 6a6: aastore
      // 6a7: dup
      // 6a8: bipush 1
      // 6a9: aload 0
      // 6aa: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 6ad: bipush 31
      // 6af: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 6b2: aastore
      // 6b3: dup
      // 6b4: bipush 2
      // 6b6: aload 0
      // 6b7: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 6ba: bipush 25
      // 6bc: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 6bf: aastore
      // 6c0: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 6c3: ldc_w "ITP-ecD"
      // 6c6: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 6c9: bipush 0
      // 6ca: ireturn
      // 6cb: iload 6
      // 6cd: ifne 6d7
      // 6d0: aload 0
      // 6d1: iload 5
      // 6d3: aload 2
      // 6d4: invokevirtual net/rim/device/internal/firewall/FirewallImpl.midletSecurity_checkPermission (ILjava/lang/String;)V
      // 6d7: bipush 24
      // 6d9: bipush 21
      // 6db: bipush 0
      // 6dc: invokestatic net/rim/device/api/itpolicy/ITPolicy.getBoolean (IIZ)Z
      // 6df: ifne 760
      // 6e2: aload 0
      // 6e3: getfield net/rim/device/internal/firewall/FirewallImpl._pipeControl Lnet/rim/device/api/util/ToIntHashtable;
      // 6e6: aload 7
      // 6e8: invokevirtual net/rim/device/api/util/ToIntHashtable.get (Ljava/lang/Object;)I
      // 6eb: istore 18
      // 6ed: iload 18
      // 6ef: bipush -1
      // 6f1: if_icmpeq 747
      // 6f4: iload 18
      // 6f6: bipush 1
      // 6f7: if_icmpne 71d
      // 6fa: iload 9
      // 6fc: ifne 71d
      // 6ff: aload 0
      // 700: aload 0
      // 701: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 704: bipush 10
      // 706: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 709: bipush 1
      // 70a: anewarray 1953
      // 70d: dup
      // 70e: bipush 0
      // 70f: aload 7
      // 711: aastore
      // 712: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 715: ldc_w "ITP-SP-icD"
      // 718: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 71b: bipush 0
      // 71c: ireturn
      // 71d: iload 18
      // 71f: bipush 2
      // 721: if_icmpne 760
      // 724: iload 9
      // 726: ifeq 760
      // 729: aload 0
      // 72a: aload 0
      // 72b: getfield net/rim/device/internal/firewall/FirewallImpl._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 72e: bipush 10
      // 730: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 733: bipush 1
      // 734: anewarray 1981
      // 737: dup
      // 738: bipush 0
      // 739: aload 7
      // 73b: aastore
      // 73c: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 73f: ldc_w "ITP-SP-ecD"
      // 742: invokespecial net/rim/device/internal/firewall/FirewallImpl.inform (Ljava/lang/String;Ljava/lang/String;)V
      // 745: bipush 0
      // 746: ireturn
      // 747: aload 0
      // 748: getfield net/rim/device/internal/firewall/FirewallImpl._pipeControl Lnet/rim/device/api/util/ToIntHashtable;
      // 74b: aload 7
      // 74d: iload 9
      // 74f: ifeq 756
      // 752: bipush 1
      // 753: goto 758
      // 756: bipush 2
      // 758: invokevirtual net/rim/device/api/util/ToIntHashtable.put (Ljava/lang/Object;I)I
      // 75b: pop
      // 75c: aload 0
      // 75d: invokevirtual net/rim/device/internal/firewall/FirewallImpl.commit ()V
      // 760: aload 0
      // 761: aload 7
      // 763: aload 1
      // 764: aload 2
      // 765: aload 8
      // 767: iload 5
      // 769: aload 4
      // 76b: invokevirtual net/rim/device/internal/firewall/FirewallImpl.prompt (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lnet/rim/device/api/system/ApplicationDescriptor;ILnet/rim/device/internal/firewall/FirewallContext;)Z
      // 76e: ireturn
      // 76f: iload 6
      // 771: bipush 1
      // 772: if_icmpge 77c
      // 775: aload 0
      // 776: iload 5
      // 778: aload 2
      // 779: invokevirtual net/rim/device/internal/firewall/FirewallImpl.midletSecurity_checkPermission (ILjava/lang/String;)V
      // 77c: bipush 1
      // 77d: ireturn
      // try (20 -> 29): 30 null
      // try (20 -> 29): 32 null
   }

   private final int getMIDPPermission(String protocol, String target) {
      int perm = -1;
      if (protocol.equals("datagram")) {
         return target == null ? 3 : 2;
      }

      if (protocol.equals("http") || protocol.equals("proxyhttp")) {
         return 0;
      }

      if (protocol.equals("https")) {
         return 1;
      }

      if (protocol.equals("socket")) {
         return target == null ? 5 : 4;
      }

      if (protocol.equals("ssl")) {
         return 6;
      }

      if (protocol.equals("file")) {
         return 12;
      }

      if (protocol.equals("comm")) {
         return 7;
      }

      if (protocol.equals("sms")) {
         return 13;
      }

      if (protocol.equals("pushregistry")) {
         return 8;
      }

      if (protocol.equals("sms_receive")) {
         return 16;
      }

      if (protocol.equals("sms_send")) {
         return 15;
      }

      if (protocol.equals("mms_send")) {
         return 35;
      }

      if (protocol.equals("mms_receive")) {
         return 36;
      }

      if (protocol.equals("mms")) {
         return 34;
      }

      if (protocol.equals("apdu")) {
         return target != null && target.indexOf("target=SAT") >= 0 ? 27 : 28;
      }

      if (protocol.equals("jcrmi")) {
         return 29;
      }

      if (!protocol.equals("btspp") && !protocol.equals("btl2cap")) {
         if (protocol.equals("btgoep")) {
            if (target != null && target.startsWith("//localhost")) {
               return 33;
            }

            perm = 32;
         }

         return perm;
      } else {
         return target != null && target.startsWith("//localhost") ? 31 : 30;
      }
   }

   private final int combinePermissions(int oldPermission, int newPermission) {
      if (oldPermission == 1) {
         return oldPermission;
      } else if (oldPermission == 2) {
         return newPermission == 1 ? newPermission : oldPermission;
      } else {
         return newPermission;
      }
   }

   protected final void midletSecurity_checkPermission(int permission, String target) {
      MIDletSecurity.checkPermission(permission, target);
   }

   private final synchronized void inform(String message, String code) {
      BackgroundDialog.showMessageOnProxy(message);
      EventLog.logEvent(7954265007165122082L, System.currentTimeMillis(), (byte)5, code.getBytes());
   }

   @Override
   public final void reset() {
      this._settings.removeAllElements();
      this.commit();
   }

   @Override
   public final void reset(int moduleHandle) {
      String moduleName = CodeModuleManager.getModuleName(moduleHandle);
      if (moduleHandle != 0 && moduleName != null) {
         byte[] moduleHash = CodeModuleManager.getModuleHash(moduleHandle);
         synchronized (this._settings) {
            FirewallImpl$Setting[] settings = this.findSettings(moduleName, moduleHash);
            if (settings != null) {
               for (int i = 0; i < settings.length; i++) {
                  this._settings.removeElement(settings[i]);
               }
            }
         }

         if (this._doCommits) {
            this.commit();
         }
      }
   }

   @Override
   public final boolean isEnabled() {
      return ITPolicyInternal.isITPolicyEnabled() ? true : this._settingStore._enabled;
   }

   @Override
   public final void setEnabled(boolean enabled) {
      if (!ITPolicyInternal.isITPolicyEnabled()) {
         this._settingStore._enabled = enabled;
         this._persistentObject.commit();
      }
   }

   @Override
   public final boolean isBlockingEnabled(byte type) {
      if (this.isBlockingEnabledByItPolicy(type)) {
         return true;
      }

      FirewallImpl$Blocking b = this.getBlocking(type);
      return b == null ? false : b._enabled;
   }

   @Override
   public final boolean isBlockingEnabledByItPolicy(byte type) {
      if (type < 0) {
         return true;
      }

      if (type > 5) {
         return false;
      }

      int policy = ITPolicy.getInteger(24, 72, 0);
      return (policy & 1 << type - 1) != 0;
   }

   private final FirewallImpl$Blocking getBlocking(byte type) {
      if (type <= 5 && type > 0) {
         synchronized (this._blockings) {
            FirewallImpl$Blocking b = (FirewallImpl$Blocking)this._blockings.get(type);
            if (b == null) {
               b = new FirewallImpl$Blocking(type, false, 0);
               this._blockings.put(type, b);
               this._persistentObject.commit();
            }

            return b;
         }
      } else if (type >= -5 && type < 0) {
         synchronized (this._droppings) {
            FirewallImpl$Blocking b = (FirewallImpl$Blocking)this._droppings.get(type);
            if (b == null) {
               b = new FirewallImpl$Blocking(type, false, 0);
               this._droppings.put(type, b);
               this._persistentObject.commit();
            }

            return b;
         }
      } else {
         return null;
      }
   }

   @Override
   public final int getBlockedCount(byte type) {
      synchronized (this._blockings) {
         int var10000;
         synchronized (this._droppings) {
            FirewallImpl$Blocking b = this.getBlocking(type);
            if (b == null) {
               return -1;
            }

            var10000 = b._count;
         }

         return var10000;
      }
   }

   @Override
   public final void resetBlockedCount(byte type) {
      synchronized (this._blockings) {
         synchronized (this._droppings) {
            FirewallImpl$Blocking b = this.getBlocking(type);
            if (b != null) {
               b._count = 0;
               this._persistentObject.commit();
            }
         }
      }
   }

   @Override
   public final void resetBlockedCounts() {
      for (byte i = 5; i > 0; i--) {
         this.resetBlockedCount(i);
      }

      for (byte var2 = -5; var2 < 0; var2++) {
         this.resetBlockedCount(var2);
      }

      if (this._doCommits) {
         this.commit();
      }
   }

   @Override
   public final void incrementBlockedCount(byte type) {
      FirewallImpl$Blocking b;
      synchronized (this._blockings) {
         synchronized (this._droppings) {
            b = this.getBlocking(type);
            if (b != null) {
               b._count++;
               this._persistentObject.commit();
            }
         }
      }

      if (b != null) {
         this.notifyBlockedCountListeners(type, b._count);
      }
   }

   @Override
   public final void addBlockedCountListener(BlockedCountListener blockedCountListener) {
      if (!this._blockedCountListeners.contains(blockedCountListener)) {
         this._blockedCountListeners.addElement(blockedCountListener);
      }
   }

   @Override
   public final void removeBlockedCountListener(BlockedCountListener blockedCountListener) {
      this._blockedCountListeners.removeElement(blockedCountListener);
   }

   private final void notifyBlockedCountListeners(byte type, int count) {
      int numElements;
      BlockedCountListener[] listeners;
      synchronized (this._blockedCountListeners) {
         numElements = this._blockedCountListeners.size();
         listeners = new Object[numElements];
         this._blockedCountListeners.copyInto(listeners);
      }

      for (int i = numElements - 1; i >= 0; i--) {
         try {
            listeners[i].blockedCountIncremented(type, count);
         } finally {
            continue;
         }
      }
   }

   @Override
   public final boolean setBlocking(byte type, boolean enabled) {
      return this.setBlocking(type, enabled, -1);
   }

   final boolean setBlocking(byte type, boolean enabled, int count) {
      synchronized (this._blockings) {
         synchronized (this._droppings) {
            FirewallImpl$Blocking b = this.getBlocking(type);
            if (b == null) {
               return false;
            }

            b._enabled = enabled;
            if (count > -1) {
               b._count = count;
            }

            if (this._doCommits) {
               this.commit();
            }
         }

         return true;
      }
   }

   protected final boolean prompt(String moduleName, String protocol, String target, ApplicationDescriptor descriptor, int perm, FirewallContext context) {
      String moduleNameToUse = null;
      ApplicationDescriptor descriptorToUse = null;
      int contextModule = this.parseContext(context);
      if (contextModule != -1) {
         moduleNameToUse = CodeModuleManager.getModuleName(contextModule);
         descriptorToUse = context.getRequestingDescriptor();
      } else {
         moduleNameToUse = moduleName;
         descriptorToUse = descriptor;
      }

      int permission = this.getPermission(moduleNameToUse, descriptorToUse.getIndex(), protocol, target);
      if (permission == 3) {
         permission = this.askUser(descriptorToUse.getName(), protocol, target);
         if (permission != 2 && permission != 6) {
            this.setPermission(moduleNameToUse, descriptorToUse.getIndex(), protocol, target, permission);
         }
      }

      boolean result;
      switch (permission) {
         case -1:
            result = false;
            break;
         case 0:
         case 1:
         case 2:
         default:
            result = true;
      }

      if (perm != -1 && CodeModuleManager.isMidlet()) {
         MIDletSecurity.setPermission(perm, MIDletSecurity.checkRealPermissionNoPrompt(perm), result);
      }

      return result;
   }

   private final int parseContext(FirewallContext context) {
      int currentModuleHandle = -1;
      if (context != null) {
         int[] additionalModules = context.getAdditionalModules();
         if (additionalModules != null) {
            for (int i = additionalModules.length - 1; i >= 0; i--) {
               currentModuleHandle = additionalModules[i];
               if (!ControlledAccess.verifyRRISignature(currentModuleHandle)) {
                  return currentModuleHandle;
               }
            }
         }
      }

      return currentModuleHandle;
   }

   private final boolean isApplicationControlDataPresent() {
      return ITPolicy.getByteArray(24, 25) != null;
   }

   private final boolean isApplicationControlSupported() {
      String version = ITPolicy.getString(21, 3);
      boolean policyPresent = this.isEnabled();
      if (this.isApplicationControlDataPresent()) {
         return true;
      }

      if (!policyPresent || version != null && version.length() >= 1) {
         if (!policyPresent) {
            return true;
         }

         int period = version != null ? version.indexOf(46) : 0;
         if (period == 0) {
            return false;
         }

         String major;
         if (period > 0) {
            major = version.substring(0, period);
         } else {
            major = version;
         }

         return Integer.parseInt(major) >= 4;
      } else {
         return false;
      }
   }
}
