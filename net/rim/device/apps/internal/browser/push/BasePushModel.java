package net.rim.device.apps.internal.browser.push;

import net.rim.device.api.browser.push.PushOptions;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.internal.proxy.Proxy;

public class BasePushModel implements Persistable, Runnable {
   protected int _connectionType;
   protected String _connectionSource;
   private boolean _foreground;
   protected Object _activateObject = new Object();
   public static final int ALLOW_MESSAGE;
   public static final int REJECT_DUPLICATE;
   public static final int REJECT_ALL;
   public static final int REJECT_FILTER;
   public static final int REJECT_DENY_ALL;
   public static final int REJECT_EXPIRED;
   public static final int REJECT_INVALID_SECURITY;
   public static final int REJECT_NO_SECURITY;
   public static final int REJECT_NO_MAC;

   public boolean getBrowserForeground() {
      return this._foreground;
   }

   public void browserActivated() {
      Proxy.getInstance().invokeRunnable(new BasePushModel$1(this));
   }

   public Object getLock() {
      return this._activateObject;
   }

   public boolean showBrowser() {
      throw null;
   }

   protected int mapPushSourceToProtocolType(int source) {
      switch (source) {
         case 0:
            return 2;
         case 1:
         default:
            return 1;
         case 2:
            return 0;
      }
   }

   public int rejectMessage() {
      throw null;
   }

   public void setBrowserForeground(boolean value) {
      this._foreground = value;
   }

   public void setConnectionValues(int connectionType, String source) {
      this._connectionType = connectionType;
      this._connectionSource = source;
   }

   public int getConnectionType() {
      return this._connectionType;
   }

   public String getConnectionSource() {
      return this._connectionSource;
   }

   protected int rejectMessage(int pushType) {
      int protocolType = this.mapPushSourceToProtocolType(this._connectionType);
      PushOptions options = PushOptions.getOptions();
      if (options.getAcceptMode(pushType, protocolType) == 2) {
         return 2;
      }

      int mode = options.getFilterMode(pushType, protocolType);
      String filter = options.getFilterValue(pushType, protocolType);
      if (mode == 0) {
         return 0;
      }

      if (mode == 2) {
         return 4;
      }

      if (filter == null) {
         return 0;
      }

      if (this._connectionSource == null) {
         return 0;
      }

      String source = this._connectionSource.trim();
      Object comparisonObject = null;
      if (this._connectionType == 1) {
         label119:
         try {
            ContextObject context = (ContextObject)(new Object());
            ContextObject.put(context, 253, source);
            comparisonObject = FactoryUtil.createInstance(3797587162219887872L, context);
         } finally {
            break label119;
         }
      }

      StringTokenizer tokenizer = (StringTokenizer)(new Object(filter, ','));

      while (tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken();
         if (comparisonObject != null && this._connectionType == 1) {
            label109:
            try {
               ContextObject context = (ContextObject)(new Object());
               ContextObject.put(context, 253, token);
               Object testObject = FactoryUtil.createInstance(3797587162219887872L, context);
               if (testObject != null && testObject.equals(comparisonObject)) {
                  return 0;
               }
            } finally {
               break label109;
            }
         }

         if (token.indexOf(source) != -1) {
            return 0;
         }
      }

      EventLogger.logEvent(-1133226195824034738L, ((StringBuffer)(new Object("RMnf\n"))).append(source).toString().getBytes(), 0);
      return 3;
   }

   @Override
   public void run() {
      throw null;
   }
}
