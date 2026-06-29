package net.rim.tid.im.conv.europe;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;

class DirectEuropeanConv$Invoker implements Runnable {
   private byte _type;
   private int _timeout;
   private int _invocationID;
   private Application _appCache;
   private final DirectEuropeanConv this$0;
   private static final byte INV_TYPE_KEYDOWN_TIMEOUT = 0;
   private static final byte INV_TYPE_SYMSCREEN_TIMEOUT = 1;
   private static final byte INV_TYPE_KEYREPEAT_TIMEOUT = 2;
   private static final short KEY_REPEATE_INV_GAP = 100;

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void cancel() {
      if (this._invocationID != -1) {
         Application app = this._appCache;
         if (app != null) {
            label24:
            try {
               app.cancelInvokeLater(this._invocationID);
               if (Application.getApplication() != app) {
                  EventLogger.logEvent(-7509200465648525729L, "cancel invoke later after application switch!!!".toString().trim().getBytes(), 0);
               }
            } catch (Throwable var4) {
               e.printStackTrace();
               break label24;
            }
         }
      }

      this.clean();
   }

   public void init(byte type, int time) {
      if (this._invocationID != -1) {
         this.cancel();
         System.out.println("WARNING: invoker init without cancel, type=" + type);
      } else {
         this._type = type;
         this._timeout = time;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void start() {
      if (this._invocationID == -1 && this._timeout != -1) {
         try {
            this._appCache = Application.getApplication();
            this._invocationID = this._appCache.invokeLaterInternal(this.this$0._timeoutInvoker, this._timeout, false);
         } catch (Throwable var3) {
            this.clean();
            e.printStackTrace();
            return;
         }

         if (this._invocationID == -1) {
            this.clean();
         }
      }
   }

   public byte getType() {
      return this._type;
   }

   @Override
   public void run() {
      this._invocationID = -1;
      this._timeout = -1;
      this.this$0.timeoutInvoke(this._type);
   }

   private void clean() {
      this._appCache = null;
      this._timeout = -1;
      this._invocationID = -1;
   }

   DirectEuropeanConv$Invoker(DirectEuropeanConv _1) {
      this.this$0 = _1;
      this._timeout = -1;
      this._invocationID = -1;
   }
}
