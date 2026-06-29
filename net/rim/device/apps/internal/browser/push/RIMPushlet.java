package net.rim.device.apps.internal.browser.push;

import net.rim.device.api.browser.push.PushEventLogger;
import net.rim.device.api.browser.push.Pushlet;
import net.rim.device.api.browser.push.RIMPushletHelper;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.PushInputStream;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.SerializationManager;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.wappush.WAPPushConverterDescriptor;
import net.rim.device.apps.internal.browser.wappush.WAPPushExpiryManager;
import net.rim.device.cldc.io.ippp.SocketTransportBase;

public final class RIMPushlet implements Pushlet, PushEventLogger {
   private RIMPushletListener _listener;
   private static final long APPLICATION_REGISTRY_NAME = 159080113504478440L;
   public static final String CONTEXT = "net.rim.device.apps.internal.browser.wappush";
   public static final String PUSH_TYPE_KEY = "X-Rim-Push-Type";
   static RIMPushlet _instance;

   public static final RIMPushlet getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (RIMPushlet)applicationRegistry.getOrWaitFor(159080113504478440L);
         if (_instance == null) {
            _instance = new RIMPushlet();
            _instance.register();
            applicationRegistry.put(159080113504478440L, _instance);
         }
      }

      return _instance;
   }

   private RIMPushlet() {
   }

   private final void register() {
      new WAPPushConverterDescriptor().register();
      new BrowserPushConverterDescriptor().register();
      RIMPushletHelper.registerDefaultPushlet(this);
      WAPPushExpiryManager.getInstance();
   }

   public final void registerListener(RIMPushletListener listener) {
      this._listener = listener;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void messageReceived(HttpHeaders headers, PushInputStream data) {
      boolean allowed = true;
      int connectionType = data.getConnectionType();
      if (connectionType == 3) {
         int subType = SocketTransportBase.getTypeByUid(data.getSource());
         if (subType == 0 && !ITPolicy.getBoolean(2, true) || subType == 1 && !ITPolicy.getBoolean(30, 3, true)) {
            allowed = false;
         }
      } else if ((connectionType == 2 || connectionType == 1) && !ITPolicy.getBoolean(19, true)) {
         allowed = false;
      }

      if (!allowed) {
         try {
            data.decline(238);
         } finally {
            return;
         }
      } else {
         String contentType = headers.getPropertyValue("X-Rim-Push-Type");
         if (contentType == null) {
            contentType = headers.getPropertyValue("Content-Type");
         }

         if (contentType == null) {
            try {
               data.decline(238);
               return;
            } finally {
               return;
            }
         } else {
            int paramStartIndex = contentType.indexOf(59);
            if (paramStartIndex != -1) {
               contentType = contentType.substring(0, paramStartIndex).trim();
            }

            Converter converter = SerializationManager.getConverter(
               StringUtilities.toLowerCase(contentType, 1701707776), "net.rim.device.apps.internal.browser.wappush"
            );
            if (converter == null) {
               try {
                  data.decline(238);
                  return;
               } finally {
                  return;
               }
            }

            boolean var22 = false /* VF: Semaphore variable */;

            try {
               var22 = true;
               Object e = converter.convert(data, headers);
               if (this._listener != null) {
                  this._listener.messageReceived(e);
               }

               if (!(e instanceof Object[])) {
                  this.processObject(e, data);
                  return;
               }

               Object[] array = (Object[])e;
               int count = array.length;

               for (int i = 0; i < count; i++) {
                  this.processObject(array[i], data);
               }

               var22 = false;
            } finally {
               if (var22) {
                  try {
                     data.decline(238);
                     return;
                  } finally {
                     return;
                  }
               }
            }
         }
      }
   }

   private final void processObject(Object obj, PushInputStream data) {
      if (!(obj instanceof Runnable)) {
         try {
            data.decline(238);
         } finally {
            return;
         }
      } else if (!(obj instanceof BasePushModel)) {
         Runnable var60 = (Runnable)obj;

         try {
            var60.run();
            data.accept();
         } finally {
            return;
         }
      } else {
         BasePushModel model = (BasePushModel)obj;
         model.setConnectionValues(data.getConnectionType(), data.getSource());
         int rejectMessage = model.rejectMessage();
         if (rejectMessage != 0) {
            label273:
            try {
               data.decline(235);
            } finally {
               break label273;
            }

            EventLogger.logEvent(-1133226195824034738L, ("Rejected reason: " + rejectMessage + " source: " + data.getSource()).getBytes(), 5);
         } else {
            BrowserImpl browserImpl = BrowserDaemonRegistry.getInstance();
            if (browserImpl == null) {
               try {
                  model.run();
                  data.accept();
               } finally {
                  return;
               }
            } else {
               if (model.showBrowser()) {
                  boolean isForeground = browserImpl.isForeground();
                  if (!isForeground) {
                     Object lock = model.getLock();
                     synchronized (lock) {
                        browserImpl.initiatePushDisplay(model);

                        label278:
                        try {
                           lock.wait();
                        } finally {
                           break label278;
                        }
                     }
                  }

                  model.setBrowserForeground(isForeground);
               }

               browserImpl.enquePushModel(model);

               try {
                  data.accept();
               } finally {
                  return;
               }
            }
         }
      }
   }
}
