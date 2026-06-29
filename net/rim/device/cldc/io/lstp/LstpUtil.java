package net.rim.device.cldc.io.lstp;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.internal.proxy.Proxy;

public final class LstpUtil {
   private Proxy _proxy = Proxy.getInstance();
   private boolean _linkState;
   private Object[] _listeners;
   private String[] _appNames = new Object[1];
   private static final long GUID;

   public static final LstpUtil getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      LstpUtil lstpUtil = (LstpUtil)ar.getOrWaitFor(-715130445863266572L);
      if (lstpUtil == null) {
         lstpUtil = new LstpUtil();
         ar.put(-715130445863266572L, lstpUtil);
      }

      return lstpUtil;
   }

   private LstpUtil() {
      this._appNames[0] = "muxer";
   }

   public final synchronized void addListener(LstpListener listener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, listener);
   }

   public final synchronized void removeListener(LstpListener listener) {
      this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
   }

   public final synchronized boolean getLinkState() {
      return this._linkState;
   }

   protected final void setLinkState(boolean linkState, boolean redirect) {
      Object[] listeners;
      synchronized (this) {
         if (this._linkState == linkState) {
            return;
         }

         this._linkState = linkState;
         listeners = this._listeners;
      }

      if (listeners != null) {
         if (redirect) {
            this._proxy.invokeRunnable(new LstpUtilRunnable(listeners, linkState));
            return;
         }

         for (int i = listeners.length - 1; i >= 0; i--) {
            try {
               ((LstpListener)listeners[i]).lstpLinkStateChanged(linkState);
            } finally {
               continue;
            }
         }
      }
   }

   public final int registerAppName(String appName) {
      synchronized (this._appNames) {
         int appId = Arrays.getIndex(this._appNames, appName);
         if (appId < 0) {
            Arrays.add(this._appNames, appName);
            appId = this._appNames.length - 1;
         }

         return appId;
      }
   }

   public final int getAppId(String appName) {
      synchronized (this._appNames) {
         return Arrays.getIndex(this._appNames, appName);
      }
   }

   public final String getAppName(int appId) {
      synchronized (this._appNames) {
         return appId >= 0 && appId < this._appNames.length ? this._appNames[appId] : "";
      }
   }

   public final String[] getAppNames() {
      synchronized (this._appNames) {
         return this._appNames;
      }
   }
}
