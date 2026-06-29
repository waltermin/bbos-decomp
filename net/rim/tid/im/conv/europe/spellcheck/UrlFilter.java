package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.device.internal.ui.StringBufferGap;

public class UrlFilter implements ITextFilter {
   private static String[] URL_PROTOCOLS = new String[]{
      "http://", "ftp://", "afs://", "news://", "nntp:", "cid:", "mailto:", "wais://", "telnet://", "gopher://", "www."
   };

   @Override
   public int filter(StringBufferGap aText, int aStart, int aEnd, boolean aBackwards) {
      int start;
      for (start = aStart; start > 0; start--) {
         char ch = aText.charAt(start - 1);
         if (ch == ' ' || ch == '\n' || ch == '\t') {
            break;
         }
      }

      int len = aText.length();
      int url_len = 0;
      int email_state = 0;

      for (int index = start; index < len; index++) {
         char ch = aText.charAt(index);
         if (ch == ' ' || ch == '\n' || ch == '\t') {
            break;
         }

         if (index > start && email_state == 0 && ch == '@') {
            email_state++;
         } else if (email_state == 1 && ch == '.') {
            email_state++;
         }

         url_len++;
      }

      boolean match = email_state == 2;
      if (!match) {
         label63:
         for (int i = 0; i < URL_PROTOCOLS.length; i++) {
            String protocol = URL_PROTOCOLS[i];
            int protocol_len = protocol.length();
            if (protocol_len < url_len) {
               match = true;

               for (int j = 0; j < protocol_len; j++) {
                  if (aText.charAt(start + j) != protocol.charAt(j)) {
                     match = false;
                     continue label63;
                  }
               }
               break;
            }
         }
      }

      int end = start + url_len;
      if (match) {
         return aBackwards ? start - aStart : end - aStart;
      } else {
         return 0;
      }
   }

   @Override
   public void reset() {
   }
}
