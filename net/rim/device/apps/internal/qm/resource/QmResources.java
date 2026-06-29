package net.rim.device.apps.internal.qm.resource;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public final class QmResources extends QmResourceFetcher {
   static QmResources _instance;

   private QmResources(ResourceBundleFamily rbf) {
      super(rbf);
   }

   public static final QmResourceFetcher getInstance() {
      if (_instance == null) {
         _instance = new QmResources(ResourceBundle.getBundle(527154617836983245L, "net.rim.device.apps.internal.qm.resource.Qm"));
      }

      return _instance;
   }

   public static final String getString(int id) {
      return getInstance().fetchString(id);
   }

   public static final String format(int id, String arg1) {
      String format = getInstance().fetchString(id);
      if (format == null) {
         format = "{0}";
      }

      return MessageFormat.format(format, new Object[]{arg1});
   }

   public static final String format(int id, String arg1, String arg2) {
      String format = getInstance().fetchString(id);
      if (format == null) {
         format = "{0} {1}";
      }

      return MessageFormat.format(format, new Object[]{arg1, arg2});
   }
}
