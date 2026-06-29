package net.rim.device.apps.internal.sms;

import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardSecurityListener;
import net.rim.device.api.system.SIMCardStatusListener;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollection;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.device.apps.internal.sms.resources.SMSResources;
import net.rim.device.internal.proxy.Proxy;

public final class SIMManager implements SIMCardStatusListener, SIMCardSecurityListener, DialogClosedListener {
   private Vector _smsRequestQueue = new Vector();
   private Dialog _simFullDialog;
   private Dialog _simFaultDialog;
   private static final long SIM_MANAGER = 172149956913108052L;
   private static final int READ = 0;
   private static final int DELETE = 1;
   private static final int REQUEST_TYPE_INDEX = 0;
   private static final int PACKET_ID_INDEX = 1;
   private static SIMManager _manager;

   public final void markSMSMessageAsRead(int packetId) {
      if (packetId != 65535) {
         int[] request = new int[]{0, packetId};
         this._smsRequestQueue.addElement(request);
         SMSService.log(1397574993);
         this.processRequestQueue();
      }
   }

   public final void deleteSMSMessage(int packetId) {
      if (packetId != 65535) {
         int[] request = new int[]{1, packetId};
         this._smsRequestQueue.addElement(request);
         SMSService.log(1397572689);
         this.processRequestQueue();
      }
   }

   @Override
   public final void cardUpdated() {
   }

   @Override
   public final void cardInvalid(int code, int subCode) {
      if (RadioInfo.getNetworkType() == 4 && code == 2) {
         SIMCard.removeListener(Proxy.getInstance(), this);
      }

      VoicemailIconManager.simUpdate(false);
      this._smsRequestQueue.removeAllElements();
   }

   @Override
   public final void cardFault(int code) {
      if (this._simFaultDialog == null) {
         int id;
         switch (code) {
            case 26368:
            case 27392:
               id = 12;
               break;
            case 27904:
               id = 13;
               break;
            case 28160:
               id = 14;
               break;
            case 28416:
               id = 15;
               break;
            case 37440:
               id = 1;
               break;
            case 37632:
               id = 16;
               break;
            case 37888:
               id = 2;
               break;
            case 37890:
               id = 3;
               break;
            case 37892:
               id = 4;
               break;
            case 37896:
               id = 5;
               break;
            case 38914:
               id = 6;
               break;
            case 38916:
               id = 7;
               break;
            case 38920:
               id = 8;
               break;
            case 38928:
               id = 9;
               break;
            case 38976:
               id = 10;
               break;
            case 38992:
               id = 11;
               break;
            default:
               id = 0;
         }

         String[] messages = SMSResources.getStringArray(401);
         if (id >= messages.length) {
            id = 0;
         }

         String message = MessageFormat.format(SMSResources.getString(400), new String[]{messages[id]});
         this._simFaultDialog = new Dialog(0, message, 0, Bitmap.getPredefinedBitmap(2), 33554432);
         this._simFaultDialog.setDialogClosedListener(this);
         this._simFaultDialog.show();
      }
   }

   @Override
   public final synchronized void smsEFFull() {
      if (this._simFullDialog == null) {
         this._simFullDialog = new Dialog(0, SMSResources.getString(740), 0, Bitmap.getPredefinedBitmap(2), 33554432);
         this._simFullDialog.setDialogClosedListener(this);
         this._simFullDialog.show();
      }
   }

   @Override
   public final void responseDeleteSMS(int status, int packetId) {
      if (status == 0) {
         SMSService.log(1397834835);
         this.processRequestQueue();
      } else {
         SMSService.log(1397834822);
      }
   }

   @Override
   public final void responseMarkSMSAsRead(int status, int packetId) {
      if (status == 0) {
         SMSService.log(1397837139);
         this.processRequestQueue();
      } else {
         SMSService.log(1397837126);
      }
   }

   @Override
   public final void requestSendPIN(int retriesRemaining) {
   }

   @Override
   public final void pinValid() {
   }

   @Override
   public final void requestSendPUK(int retriesRemaining) {
   }

   @Override
   public final void responseEnablePIN(int code, int id, int remainingPINRetries) {
   }

   @Override
   public final void responseDisablePIN(int code, int id, int remainingPINRetries) {
   }

   @Override
   public final void responseChangePIN(int code, int id, int remainingPINRetries) {
      int resourceID;
      if (code == 0) {
         resourceID = 373;
      } else if (remainingPINRetries == 0) {
         resourceID = 388;
      } else if (code == 6) {
         resourceID = 394;
      } else {
         resourceID = 374;
      }

      this.displayPINMessage(resourceID, id, remainingPINRetries);
   }

   @Override
   public final void responseValidatePIN(int code, int id, int remainingPINRetries) {
      if (code != 0) {
         int resourceID;
         if (remainingPINRetries == 0) {
            resourceID = 409;
         } else {
            resourceID = 408;
         }

         this.displayPINMessage(resourceID, id, remainingPINRetries);
      }
   }

   @Override
   public final void responseDeactivateMEP(boolean success) {
   }

   @Override
   public final void wtlsKeyWriteComplete(int status) {
   }

   @Override
   public final synchronized void dialogClosed(Dialog dialog, int choice) {
      if (dialog == this._simFullDialog) {
         this._simFullDialog = null;
      } else {
         if (dialog == this._simFaultDialog) {
            this._simFaultDialog = null;
         }
      }
   }

   @Override
   public final void cardReady() {
      VoicemailIconManager.simUpdate(true);
      this.deleteInvalidSIMMessages();

      try {
         SIMCard.atSetLocale(Locale.getDefaultForSystem().getCode());
      } finally {
         return;
      }
   }

   @Override
   public final void cardInserted() {
   }

   private final void displayPINMessage(int resourceID, int id, int remainingPINRetries) {
      String pin = "";
      if (id == 2) {
         pin = "2";
      }

      String message = MessageFormat.format(SMSResources.getString(resourceID), new String[]{pin, pin});
      Status.show(message, Bitmap.getPredefinedBitmap(0), 3000, 33554432, true, false, -2147483645);
   }

   private SIMManager() {
   }

   public static final SIMManager getInstance() {
      return _manager;
   }

   private final void processRequestQueue() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/sms/SIMManager._smsRequestQueue Ljava/util/Vector;
      // 04: invokevirtual java/util/Vector.isEmpty ()Z
      // 07: ifne 48
      // 0a: aload 0
      // 0b: getfield net/rim/device/apps/internal/sms/SIMManager._smsRequestQueue Ljava/util/Vector;
      // 0e: invokevirtual java/util/Vector.firstElement ()Ljava/lang/Object;
      // 11: checkcast [I
      // 14: astore 1
      // 15: aload 1
      // 16: bipush 0
      // 17: iaload
      // 18: ifne 24
      // 1b: aload 1
      // 1c: bipush 1
      // 1d: iaload
      // 1e: invokestatic net/rim/device/api/system/SIMCard.requestMarkSMSAsRead (I)V
      // 21: goto 2a
      // 24: aload 1
      // 25: bipush 1
      // 26: iaload
      // 27: invokestatic net/rim/device/api/system/SIMCard.requestDeleteSMS (I)V
      // 2a: aload 0
      // 2b: getfield net/rim/device/apps/internal/sms/SIMManager._smsRequestQueue Ljava/util/Vector;
      // 2e: bipush 0
      // 2f: invokevirtual java/util/Vector.removeElementAt (I)V
      // 32: return
      // 33: astore 2
      // 34: aload 1
      // 35: bipush 0
      // 36: iaload
      // 37: ifne 40
      // 3a: ldc_w 1397574982
      // 3d: goto 43
      // 40: ldc_w 1397572678
      // 43: invokestatic net/rim/device/apps/internal/sms/SMSService.log (I)V
      // 46: return
      // 47: astore 2
      // 48: return
      // try (9 -> 26): 27 null
      // try (9 -> 26): 37 null
   }

   private final void deleteInvalidSIMMessages() {
      synchronized (FolderHierarchies.getLockObject()) {
         Folder folder = Storage.getSMSFolder(-441701525336570016L);
         if (folder != null) {
            PersistedSortedCollection collection = (PersistedSortedCollection)folder.getContainedItems();
            int imsiCRC = SIMCard.getIMSICRC();

            for (int i = collection.size() - 1; i >= 0; i--) {
               SMSModel model = (SMSModel)collection.getAt(i);
               if (model._payload._imsiCRC != imsiCRC) {
                  model.ungroupMessage();
                  model._payload._segmentIDs = null;
                  model.delete(null, true);
               }
            }
         }
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _manager = (SIMManager)ar.getOrWaitFor(172149956913108052L);
      if (_manager == null) {
         _manager = new SIMManager();
         ar.put(172149956913108052L, _manager);
      }
   }
}
