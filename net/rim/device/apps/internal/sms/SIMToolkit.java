package net.rim.device.apps.internal.sms;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardATListener;
import net.rim.device.api.system.SIMCardStatusListener;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ribbon.RibbonNetworkInfo;
import net.rim.device.apps.internal.sms.resources.SMSResources;

public final class SIMToolkit implements SIMCardStatusListener, SIMCardATListener, GlobalEventListener {
   private String _setupMenuName;
   private SetupMenuEntryPoint _setupMenuEntryPoint;
   private SIMATPopupScreen _popupScreen;

   final synchronized void popupDone() {
      this._popupScreen = null;
   }

   @Override
   public final void cardReady() {
   }

   @Override
   public final void cardUpdated() {
      RibbonNetworkInfo.getInstance().setIdleModeText(null);
   }

   @Override
   public final void cardInvalid(int code, int subCode) {
      if (this._setupMenuName != null) {
         RibbonLauncher.getInstance().unregisterAction(this._setupMenuName);
         this._setupMenuName = null;
         this._setupMenuEntryPoint = null;
      }

      RibbonNetworkInfo.getInstance().setIdleModeText(null);
   }

   @Override
   public final void cardFault(int code) {
   }

   @Override
   public final void smsEFFull() {
   }

   @Override
   public final void responseDeleteSMS(int status, int packetId) {
   }

   @Override
   public final void responseMarkSMSAsRead(int status, int packetId) {
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void atDisplayText(byte[] data, int messageCoding, boolean highPriority, boolean userClear, boolean immediateResponse) {
      SIMATEventLogger.logDebug(1);
      if (highPriority || !ApplicationManager.getApplicationManager().isSystemLocked() && !DeviceInfo.isInHolster()) {
         String text = SMSService.decodeSMSData(messageCoding, data, true);
         synchronized (this) {
            SIMATDisplayTextScreen displayTextScreen = this.createDisplayTextPopupScreen(text);

            label48:
            try {
               displayTextScreen.displayText(immediateResponse, userClear);
               this._popupScreen = displayTextScreen;
            } catch (Throwable var13) {
               SIMATEventLogger.log(17, rx);
               break label48;
            }
         }

         if (immediateResponse) {
            this.atDisplayTextAck(false);
         }
      } else {
         this.atDisplayTextAck(true);
      }
   }

   @Override
   public final void atGetInkey(byte[] data, int messageCoding, int allowedKeys, boolean helpAvailable, int inputMessageCoding) {
      SIMATEventLogger.logDebug(4);
      String text = SMSService.decodeSMSData(messageCoding, data, true);
      synchronized (this) {
         this._popupScreen = this.createPopupScreen(text);
         this._popupScreen.getInkey(allowedKeys, helpAvailable, inputMessageCoding);
      }
   }

   @Override
   public final void atGetInput(
      byte[] data,
      byte[] defaultData,
      int messageCoding,
      int allowedKeys,
      int minLength,
      int maxLength,
      boolean echo,
      boolean helpAvailable,
      int inputMessageCoding
   ) {
      SIMATEventLogger.logDebug(5);
      String text = SMSService.decodeSMSData(messageCoding, data, true);
      String defaultText = SMSService.decodeSMSData(messageCoding, defaultData, true);
      synchronized (this) {
         this._popupScreen = this.createPopupScreen(text);
         this._popupScreen.getInput(defaultText, allowedKeys, minLength, maxLength, echo, helpAvailable, inputMessageCoding);
      }
   }

   @Override
   public final void atSelectItem(byte[] title, Object[] items, int[] ids, int defaultId, boolean helpAvailable) {
      SIMATEventLogger.logDebug(9);
      synchronized (this) {
         this._popupScreen = this.createPopupScreen(SIMCard.decodeAlphaId(title));
         this._popupScreen.selectItem(items, ids, defaultId, helpAvailable);
      }
   }

   @Override
   public final void atSessionEnd() {
      SIMATEventLogger.logDebug(10);
      if (this._setupMenuEntryPoint != null) {
         this._setupMenuEntryPoint.run();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void atSetUpMenu(byte[] title, Object[] items, int[] ids, boolean helpAvailable) {
      SIMATEventLogger.logDebug(12);
      if (this._setupMenuName != null) {
         RibbonLauncher.getInstance().unregisterAction(this._setupMenuName);
         this._setupMenuName = null;
         this._setupMenuEntryPoint = null;
      }

      try {
         if (items != null && (items.length > 0 || title != null && title.length > 0)) {
            if (title == null) {
               this._setupMenuName = SMSResources.getString(375);
            } else {
               this._setupMenuName = SIMCard.decodeAlphaId(title);
            }

            this._setupMenuEntryPoint = new SetupMenuEntryPoint(this, Application.getApplication(), this._setupMenuName, items, ids, helpAvailable);
            RibbonLauncher.getInstance().registerAction(this._setupMenuName, this._setupMenuEntryPoint);
         }

         SIMCard.atSetUpMenuAck(true);
      } catch (Throwable var7) {
         SIMATEventLogger.log(12, ex);
         return;
      }
   }

   @Override
   public final void atSetUpCall(byte[] firstAlphaID, byte[] secondAlphaID, boolean askUser, int action) {
   }

   @Override
   public final void atCallControl(int status, int callId) {
   }

   @Override
   public final void atPlayTone(byte[] data) {
      SIMATEventLogger.logDebug(8);
      synchronized (this) {
         this._popupScreen = this.createPopupScreen(SIMCard.decodeAlphaId(data));
         this._popupScreen.playTone();
      }
   }

   @Override
   public final void atLaunchBrowser(int param1, byte[] param2, byte[] param3, byte[] param4, int param5, byte[] param6) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: bipush 6
      // 02: invokestatic net/rim/device/apps/internal/sms/SIMATEventLogger.logDebug (I)V
      // 05: new java/lang/Object
      // 08: dup
      // 09: aload 2
      // 0a: invokespecial java/lang/String.<init> ([B)V
      // 0d: astore 7
      // 0f: new java/lang/Object
      // 12: dup
      // 13: invokespecial java/lang/StringBuffer.<init> ()V
      // 16: astore 8
      // 18: aload 4
      // 1a: ifnull 2b
      // 1d: aload 8
      // 1f: iload 5
      // 21: aload 4
      // 23: bipush 1
      // 24: invokestatic net/rim/device/apps/internal/sms/SMSService.decodeSMSData (I[BZ)Ljava/lang/String;
      // 27: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2a: pop
      // 2b: aload 6
      // 2d: ifnull 3b
      // 30: aload 8
      // 32: aload 6
      // 34: invokestatic net/rim/device/api/system/SIMCard.decodeAlphaId ([B)Ljava/lang/String;
      // 37: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 3a: pop
      // 3b: aload 8
      // 3d: invokevirtual java/lang/StringBuffer.length ()I
      // 40: ifle 6a
      // 43: aload 0
      // 44: aload 0
      // 45: astore 9
      // 47: monitorenter
      // 48: aload 0
      // 49: aload 0
      // 4a: aload 8
      // 4c: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 4f: invokespecial net/rim/device/apps/internal/sms/SIMToolkit.createPopupScreen (Ljava/lang/String;)Lnet/rim/device/apps/internal/sms/SIMATPopupScreen;
      // 52: putfield net/rim/device/apps/internal/sms/SIMToolkit._popupScreen Lnet/rim/device/apps/internal/sms/SIMATPopupScreen;
      // 55: aload 0
      // 56: getfield net/rim/device/apps/internal/sms/SIMToolkit._popupScreen Lnet/rim/device/apps/internal/sms/SIMATPopupScreen;
      // 59: invokevirtual net/rim/device/apps/internal/sms/SIMATPopupScreen.launchBrowser ()V
      // 5c: aload 9
      // 5e: monitorexit
      // 5f: goto 6a
      // 62: astore 10
      // 64: aload 9
      // 66: monitorexit
      // 67: aload 10
      // 69: athrow
      // 6a: invokestatic net/rim/device/apps/api/browser/BrowserServices.isBrowserAvailable ()Z
      // 6d: istore 9
      // 6f: iload 9
      // 71: ifeq 93
      // 74: aload 7
      // 76: invokestatic net/rim/device/apps/api/browser/BrowserServices.loadUrl (Ljava/lang/String;)Z
      // 79: istore 9
      // 7b: new java/lang/Object
      // 7e: dup
      // 7f: ldc_w "launching browser:"
      // 82: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 85: aload 7
      // 87: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 8a: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 8d: invokestatic net/rim/device/apps/internal/sms/SIMATEventLogger.logDebug (Ljava/lang/String;)V
      // 90: goto 98
      // 93: bipush 19
      // 95: invokestatic net/rim/device/apps/internal/sms/SIMATEventLogger.logDebug (I)V
      // 98: iload 9
      // 9a: invokestatic net/rim/device/api/system/SIMCard.atLaunchBrowserAck (Z)V
      // 9d: return
      // 9e: astore 10
      // a0: bipush 7
      // a2: aload 10
      // a4: invokestatic net/rim/device/apps/internal/sms/SIMATEventLogger.log (ILjava/lang/Exception;)V
      // a7: return
      // a8: astore 10
      // aa: bipush 7
      // ac: aload 10
      // ae: invokestatic net/rim/device/apps/internal/sms/SIMATEventLogger.log (ILjava/lang/Exception;)V
      // b1: return
      // try (34 -> 45): 46 null
      // try (46 -> 49): 46 null
      // try (69 -> 71): 72 null
      // try (69 -> 71): 77 null
   }

   @Override
   public final synchronized void atTimeout() {
      SIMATEventLogger.logDebug(13);
      if (this._popupScreen != null) {
         this._popupScreen.dismiss();
      }

      SIMATEventLogger.log(13, 4);
   }

   @Override
   public final void atSetUpIdleModeText(byte[] data, int messageCoding) {
      SIMATEventLogger.logDebug(11);
      String text = null;
      if (data != null) {
         text = SMSService.decodeSMSData(messageCoding, data, true);
      }

      RibbonNetworkInfo.getInstance().setIdleModeText(text);
   }

   @Override
   public final void atDisplayAlphaID(byte[] data) {
      SIMATEventLogger.logDebug(0);
      if (data != null) {
         synchronized (this) {
            this._popupScreen = this.createPopupScreen(SIMCard.decodeAlphaId(data));
            this._popupScreen.displayAlphaID();
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 6345609069135580235L) {
         try {
            SIMCard.atEventActive(32);
         } catch (Throwable var9) {
            SIMATEventLogger.log(3, ex);
            return;
         }
      }
   }

   @Override
   public final void cardInserted() {
   }

   private final void atDisplayTextAck(boolean param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: bipush 2
      // 02: invokestatic net/rim/device/apps/internal/sms/SIMATEventLogger.logDebug (I)V
      // 05: iload 1
      // 06: invokestatic net/rim/device/api/system/SIMCard.atDisplayTextAck (Z)V
      // 09: return
      // 0a: astore 2
      // 0b: bipush 2
      // 0d: aload 2
      // 0e: invokestatic net/rim/device/apps/internal/sms/SIMATEventLogger.log (ILjava/lang/Exception;)V
      // 11: return
      // 12: astore 2
      // 13: bipush 2
      // 15: aload 2
      // 16: invokestatic net/rim/device/apps/internal/sms/SIMATEventLogger.log (ILjava/lang/Exception;)V
      // 19: return
      // try (2 -> 4): 5 null
      // try (2 -> 4): 10 null
   }

   private final synchronized SIMATPopupScreen createPopupScreen(String text) {
      if (this._popupScreen != null) {
         this._popupScreen.dismiss();
      }

      return new SIMATPopupScreen(this, text);
   }

   private final synchronized SIMATDisplayTextScreen createDisplayTextPopupScreen(String text) {
      if (this._popupScreen != null) {
         this._popupScreen.dismiss();
      }

      return new SIMATDisplayTextScreen(this, text);
   }

   public SIMToolkit() {
      SIMATEventLogger.register();
   }
}
