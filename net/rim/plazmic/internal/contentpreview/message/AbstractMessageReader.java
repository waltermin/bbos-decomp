package net.rim.plazmic.internal.contentpreview.message;

import java.io.DataInput;

public class AbstractMessageReader {
   public static final String rcsid = "$Id:$";

   protected static String readString(DataInput din) {
      int len = din.readInt();
      char[] ca = new char[len];

      for (int i = 0; i < len; i++) {
         ca[i] = din.readChar();
      }

      return new String(ca);
   }

   protected static String[] readStringArray(DataInput din) {
      int count = din.readInt();
      String[] sa = new String[count];

      for (int i = 0; i < count; i++) {
         sa[i] = readString(din);
      }

      return sa;
   }
}
