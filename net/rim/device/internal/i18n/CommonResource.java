package net.rim.device.internal.i18n;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.vm.WeakReference;

public final class CommonResource implements net.rim.device.internal.resource.CommonResource {
   public static final int RESPONSES = 0;
   public static final int RESPONSE_VALUES = 1;
   public static final int DEFAULT = 2;
   public static final int MESSAGE = 3;
   public static final int DLG_SAVE = 10000;
   public static final int DLG_OK = 10004;
   public static final int DLG_DELETE = 10008;
   public static final int DLG_YES_NO = 10012;
   public static final int DLG_OK_CANCEL = 10041;
   public static final int DLG_ALLOW_DENY = 10170;
   private static ResourceBundleFamily _bundle = ResourceBundle.getBundle(-6812884907508133143L, "net.rim.device.internal.resource.Common");
   private static Object[] _params = new Object[1];
   private static WeakReference _bufferWR = new WeakReference(null);

   public static final ResourceBundleFamily getBundle() {
      return _bundle;
   }

   public static final String format(int key, String param) {
      synchronized (_params) {
         _params[0] = param;
         String result = format(key, _params);
         _params[0] = null;
         return result;
      }
   }

   public static final String format(int key, Object[] params) {
      String message = _bundle.getString(key);
      MessageFormat format = new MessageFormat(message);
      StringBuffer _buffer = WeakReferenceUtilities.getStringBuffer(_bufferWR);
      synchronized (_buffer) {
         format.format(params, _buffer, null);
         String result = _buffer.toString();
         _buffer.setLength(0);
         return result;
      }
   }

   public static final String getString(int key) {
      return _bundle.getString(key);
   }

   public static final String[] getStringArray(int key) {
      return _bundle.getStringArray(key);
   }
}
