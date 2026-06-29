package net.rim.tid.im.conv.europe.spellcheck;

import java.util.Hashtable;
import net.rim.device.internal.ui.StringBufferGap;

public class FileNameFilter implements ITextFilter {
   private static final String[] FILE_NAME_EXTENSIONS = new String[]{
      "asp",
      "bat",
      "bmp",
      "c",
      "cgi",
      "class",
      "cpp",
      "csv",
      "db",
      "doc",
      "exe",
      "gif",
      "h",
      "hpp",
      "htm",
      "html",
      "java",
      "jar",
      "jpg",
      "lib",
      "mov",
      "mp3",
      "mpg",
      "pl",
      "png",
      "ppt",
      "py",
      "sql",
      "txt",
      "wav",
      "wma",
      "xls",
      "xml",
      "zip"
   };
   private static final Hashtable extTable = new Hashtable();

   @Override
   public int filter(StringBufferGap text, int inStart, int end, boolean backwards) {
      int start;
      for (start = inStart; start > 0; start--) {
         char ch = text.charAt(start - 1);
         if (ch == ' ' || ch == '\n' || ch == '\t') {
            break;
         }
      }

      int len = text.length();
      int dotPos = -1;

      int index;
      label38:
      for (index = start; index < len; index++) {
         char ch = text.charAt(index);
         switch (ch) {
            case '\t':
            case '\n':
            case ' ':
               break label38;
            case '.':
               dotPos = index;
               break;
         }
      }

      index--;
      if (dotPos != -1 && index - dotPos != 0 && index - dotPos <= 5) {
         String ext = text.getText(dotPos + 1, index - dotPos).toLowerCase();
         if (!extTable.containsKey(ext)) {
            return 0;
         } else {
            return backwards ? start - inStart : 1 + index - inStart;
         }
      } else {
         return 0;
      }
   }

   @Override
   public void reset() {
   }

   static {
      for (int i = 0; i < FILE_NAME_EXTENSIONS.length; i++) {
         extTable.put(FILE_NAME_EXTENSIONS[i], FILE_NAME_EXTENSIONS[i]);
      }
   }
}
