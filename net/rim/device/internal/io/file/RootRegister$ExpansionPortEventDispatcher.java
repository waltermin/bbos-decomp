package net.rim.device.internal.io.file;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Status;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.EventDispatcher;
import net.rim.device.internal.system.Expansion;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.USBPortInternal;
import net.rim.vm.Message;

class RootRegister$ExpansionPortEventDispatcher extends EventDispatcher {
   private final RootRegister this$0;

   RootRegister$ExpansionPortEventDispatcher(RootRegister _1) {
      this.this$0 = _1;
   }

   @Override
   public void dispatch(Message message, Object listener) {
      if (listener == this.this$0) {
         int event = message.getEvent();
         int subMessage = message.getSubMessage();
         long expansionResult = Expansion.getExpansionType(subMessage);
         if (expansionResult >> 32 != 1) {
            return;
         }

         switch (event) {
            case 3840:
               break;
            case 3841:
            case 3842:
            default:
               if ((expansionResult & 4294967295L) != 0) {
                  return;
               }

               this.this$0._attemptedMountOnInsert = true;
               this.this$0._cardInserted = true;
               int usbState = USBPortInternal.getConnectionState();
               if ((usbState & 16) != 0) {
                  this.this$0.usbConnectionStateChange(usbState, true);
               }

               if (!this.this$0._massStorageActive) {
                  this.this$0.mountSDCard();
               }

               if (!ApplicationManager.getApplicationManager().inStartup() && InternalServices.isDeviceCapable(20)) {
                  Status.show(CommonResource.getString(10130), Bitmap.getPredefinedBitmap(0), 2000, 33554432, true, false, -2147483647);
                  return;
               }
               break;
            case 3843:
            case 3844:
               if ((expansionResult & 4294967295L) != 1) {
                  return;
               }

               this.this$0._cardInserted = false;
               this.this$0.unmountSDCard(false);
               this.this$0.disableUSBMassStorage();
               if (!ApplicationManager.getApplicationManager().inStartup() && InternalServices.isDeviceCapable(20)) {
                  Status.show(CommonResource.getString(10131), Bitmap.getPredefinedBitmap(0), 2000, 33554432, true, false, -2147483647);
               }
         }
      }
   }
}
