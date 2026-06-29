package net.rim.device.apps.internal.browser.util;

import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.internal.api.quincy.QuincyManager;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.page.Page;
import net.rim.device.apps.internal.browser.stack.RawDataCache;
import net.rim.vm.DebugSupport;
import net.rim.vm.Memory;
import net.rim.vm.Process;

public final class QuincyUtil {
   private static int _lastUrlHash;
   private static int _lastExceptionHash;

   public static final void sendQuincy(Throwable e, boolean gc) {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      if (!(e instanceof Object)) {
         if (gc) {
            Memory.thoroughGC();
         }

         String url = browser.getLastUrl();
         if (url != null && e != null) {
            int throwableHash = Process.getThrowableHash(e);
            int urlHash = url.hashCode();
            if (urlHash == _lastUrlHash && throwableHash == _lastExceptionHash) {
               return;
            }

            _lastUrlHash = urlHash;
            _lastExceptionHash = throwableHash;
         }

         QuincyManager.sendUncaughtException("Browser");
      } else {
         RawDataCache rawDataCache = browser.getRawDataCache();
         int rdc = rawDataCache.getLongTermCacheCount();
         int rdnc = rawDataCache.getShortTermCacheCount();
         int rds = rawDataCache.getLongTermCacheSize();
         int rdns = rawDataCache.getShortTermCacheSize();
         rawDataCache.clearShortTermCache();
         Memory.thoroughGC();
         StringBuffer internalBuff = (StringBuffer)(new Object("BOOM rdc="));
         internalBuff.append(rdc);
         internalBuff.append(", rds=");
         internalBuff.append(rds);
         internalBuff.append(", rdnc=");
         internalBuff.append(rdnc);
         internalBuff.append(", rdns=");
         internalBuff.append(rdns);
         internalBuff.append(", url=");
         internalBuff.append(browser.getLastUrl());
         EventLogger.logEvent(1907089860548946979L, internalBuff.toString().getBytes(), 0);
         if (e instanceof Object) {
            DebugSupport.logStackTraces();
         }

         QuincyManager.sendUncaughtException("Browser:BOOM");
      }
   }

   public static final void sendLogworthyQuincy(String subject, String url) {
      if (url == null) {
         Page currentPage = BrowserDaemonRegistry.getInstance().getCurrentPage();
         if (currentPage != null) {
            url = currentPage.getURL();
         }
      }

      if (url != null) {
         EventLogger.logEvent(1907089860548946979L, url.getBytes(), 0);
      }

      if (subject != null) {
         QuincyManager.sendJavaLogworthy(subject);
      }
   }
}
