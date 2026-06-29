package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.ribbon.indicators.IndicatorManager;

public final class EmailMessageConverter implements SyncConverter {
   private int _ribbonSuspendCount;
   private IndicatorManager _indicatorManager;
   public static final int VERSION_SUPPORTED = 3;
   private static ContextObjectWR _convertContextWR = new ContextObjectWR(43, 19);

   final void endTransaction() {
      if (this._indicatorManager != null) {
         this._indicatorManager.resumeIndicatorUpdates();
      }

      this._ribbonSuspendCount = 0;
      this.createInstance();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      if (version != 3) {
         return null;
      }

      this._ribbonSuspendCount--;
      if (this._ribbonSuspendCount < 0) {
         this._ribbonSuspendCount = 20;
         if (this._indicatorManager == null) {
            this._indicatorManager = IndicatorManager.getInstance();
         }

         this._indicatorManager.suspendIndicatorUpdates();
      }

      SyncBuffer syncBuffer = new SyncBuffer(dataBuffer, version, uid);
      ContextObject convertContext = _convertContextWR.getContextObject();
      boolean var9 = false /* VF: Semaphore variable */;

      EmailMessageModelImpl message;
      try {
         var9 = true;
         convertContext.put(255, syncBuffer);
         message = this.createInstance();
         var9 = false;
      } finally {
         if (var9) {
            convertContext.remove(255);
         }
      }

      convertContext.remove(255);
      if (message != null) {
         MessageLookups.put(-4420850319371185992L, uid, message);
      }

      return message;
   }

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (object == null) {
         return false;
      }

      ConversionProvider converter = (ConversionProvider)object;
      SyncBuffer syncBuffer = new SyncBuffer(buffer, version, 0);
      return converter.convert(_convertContextWR.getContextObject(), syncBuffer);
   }

   private final EmailMessageModelImpl createInstance() {
      ContextObject context = _convertContextWR.getContextObject();
      context.setFlag(133);
      return (EmailMessageModelImpl)FactoryUtil.createInstance(-6822293833372928884L, context);
   }
}
