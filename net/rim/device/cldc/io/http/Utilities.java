package net.rim.device.cldc.io.http;

import java.io.InputStream;
import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.api.util.StringTokenizer;
import net.rim.vm.DebugSupport;

public final class Utilities implements HttpProtocolConstants {
   private Utilities() {
   }

   public static final String receiveLine(InputStream dins) {
      return receiveLine(dins, false);
   }

   public static final String receiveLine(InputStream dins, boolean throwEOFExceptionIfNoInput) {
      boolean shouldContinue = true;
      StringBuffer result = (StringBuffer)(new Object());

      while (shouldContinue) {
         int value = dins.read();
         switch (value) {
            case -1:
               if (throwEOFExceptionIfNoInput) {
                  throw new Object();
               }
            case 10:
               shouldContinue = false;
            case 13:
               throwEOFExceptionIfNoInput = false;
               break;
            default:
               throwEOFExceptionIfNoInput = false;
               result.append((char)value);
         }
      }

      return result.toString().trim();
   }

   public static final String[] processTransmissionLine(String lineString) {
      String[] line = new Object[3];
      if (lineString != null && lineString.length() != 0) {
         StringTokenizer tokenizer = (StringTokenizer)(new Object(lineString));
         int index = 0;

         while (tokenizer.hasMoreTokens()) {
            lineString = index != 2 ? tokenizer.nextToken() : tokenizer.nextToken("");
            line[index++] = lineString.trim();
         }
      }

      return line;
   }

   public static final String getDefaultHttpStackHint() {
      String hint = DebugSupport.getenv("DefaultHttpStack");
      if (hint == null) {
         hint = DefaultHttpStack.getInstance().getDirective();
      }

      return hint;
   }
}
