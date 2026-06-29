package java.lang;

import java.io.PrintStream;
import java.util.TimeZone;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.SIMCard;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.SystemPropertyManager;

public final class System {
   public static final PrintStream out = getOutput();
   public static final PrintStream err = out;

   private System() {
   }

   private static final PrintStream getOutput() {
      return new PrintStream(new DebugOutputStream());
   }

   public static final native long currentTimeMillis();

   public static final native void arraycopy(Object var0, int var1, Object var2, int var3, int var4);

   public static final native int identityHashCode(Object var0);

   public static final String getProperty(String key) {
      if (key == null) {
         throw new NullPointerException("key can't be null");
      }

      if (key.length() == 0) {
         throw new IllegalArgumentException("key can't be empty");
      }

      if (key.equals("microedition.encoding")) {
         return "ISO8859_1";
      }

      if (key.equals("microedition.configuration")) {
         return "CLDC-1.1";
      }

      if (key.equals("microedition.profiles")) {
         return "MIDP-2.0";
      }

      if (key.equals("microedition.platform")) {
         return "RIM Wireless Handheld";
      }

      if (key.equals("microedition.locale")) {
         return Locale.getCLDCLocaleString();
      }

      if (key.equals("microedition.jtwi.version")) {
         return "1.0";
      }

      if (key.equals("microedition.media.version")) {
         return "1.1";
      }

      if (key.equals("microedition.pim.version")) {
         return "1.0";
      }

      if (key.equals("supports.mixing")) {
         return "false";
      }

      if (key.equals("supports.audio.capture")) {
         return InternalServices.isSoftwareCapable(12) ? "true" : "false";
      }

      if (key.equals("supports.video.capture")) {
         return "false";
      }

      if (key.equals("supports.recording")) {
         return InternalServices.isSoftwareCapable(12) ? "true" : "false";
      }

      if (key.equals("audio.encodings")) {
         return InternalServices.isSoftwareCapable(12) ? "audio/amr pcm" : null;
      }

      if (key.equals("user.timezone")) {
         return TimeZone.getDefault().getID();
      }

      if (key.equals("microedition.location.version")) {
         return "1.0.1";
      }

      if (key.equals("microedition.io.file.FileConnection.version")) {
         return "1.0";
      }

      if (key.equals("file.separator")) {
         return "/";
      }

      if (key.equals("microedition.smartcardslots") && SIMCard.isJSR177Supported()) {
         return "0C";
      }

      if (key.equals("microedition.satsa.version")) {
         return "1.0";
      }

      if (key.equals("microedition.bluetooth.version")) {
         return "1.1";
      }

      if (key.equals("microedition.chapi.version")) {
         return "1.0";
      }

      if (key.equals("microedition.global.version")) {
         return "1.0";
      }

      SystemPropertyManager spm = SystemPropertyManager.getInstance();
      return spm != null ? spm.getProperty(key) : null;
   }

   public static final void exit(int status) {
      Runtime.getRuntime().exit(status);
   }

   public static final void gc() {
      Runtime.getRuntime().gc();
   }
}
