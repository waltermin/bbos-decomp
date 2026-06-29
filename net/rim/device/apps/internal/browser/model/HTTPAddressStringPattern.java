package net.rim.device.apps.internal.browser.model;

import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.options.BrowserConfigChangeListener;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.WeakReference;

public final class HTTPAddressStringPattern extends StringPattern implements BrowserConfigChangeListener {
   private WeakReference _scanbufferWR = (WeakReference)(new Object(null));
   private boolean _validConfig;
   private boolean _constrainedNavigation;
   private boolean _enabled;
   private static final int SCAN_BUFFER_LENGTH;
   private static final int STATE_INIT;
   private static final int STATE_H;
   private static final int STATE_HT;
   private static final int STATE_HTT;
   private static final int STATE_HTTP;
   private static final int STATE_HTTP_;
   private static final int STATE_HTTPS;
   private static final int STATE_HTTPS_;
   private static final int STATE_W;
   private static final int STATE_WW;
   private static final int STATE_WWW;
   private static final int STATE_WWW_;
   private static final int STATE_WA;
   private static final int STATE_WAP;
   private static final int STATE_WAP_;
   private static final int STATE_M;
   private static final int STATE_MO;
   private static final int STATE_MOB;
   private static final int STATE_MOBI;
   private static final int STATE_MOBIL;
   private static final int STATE_MOBILE;
   private static final int STATE_MOBILE_;
   private static final int STATE_Q;
   private static final int STATE_QU;
   private static final int STATE_QUE;
   private static final int STATE_QUEU;
   private static final int STATE_QUEUE;
   private static final int STATE_QUEUE_;
   private static final int STATE_D;
   private static final int STATE_DA;
   private static final int STATE_DAT;
   private static final int STATE_DATA;
   private static final int STATE_DATA_;
   private static final int STATE_R;
   private static final int STATE_RT;
   private static final int STATE_RTS;
   private static final int STATE_RTSP;
   private static final int STATE_RTSP_;

   public final void setEnabled(boolean value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void browserConfigInvalid() {
      this._validConfig = false;
   }

   @Override
   public final void browserConfigChanged() {
      this._validConfig = true;
      this._constrainedNavigation = false;
      BrowserSession session = BrowserSession.getCurrentSession();
      if (session != null) {
         this._constrainedNavigation = session.getConfig().getPropertyAsInt(7) != 0;
      }
   }

   private final int getNextIndex(char[] scanBuffer, AbstractString str, int index, int maxIndex) {
      int state = 0;

      while (index < maxIndex) {
         int count = maxIndex - index;
         if (count > 64) {
            count = 64;
         }

         str.getChars(index, index + count, scanBuffer, 0);

         for (int bufIndex = 0; bufIndex < count; index++) {
            label122:
            switch (scanBuffer[bufIndex]) {
               case '.':
                  if (state == 22 || state == 31) {
                     return index - 3;
                  }

                  if (state == 45) {
                     return index - 6;
                  }

                  state = 0;
                  break;
               case ':':
                  switch (state) {
                     case 13:
                        return index - 4;
                     case 15:
                        return index - 5;
                     case 54:
                        return index - 5;
                     case 63:
                        return index - 4;
                     case 73:
                        if (InternalServices.isSoftwareCapable(13)) {
                           return index - 4;
                        }

                        state = 0;
                        break label122;
                     default:
                        state = 0;
                        break label122;
                  }
               case 'A':
               case 'a':
                  switch (state) {
                     case 20:
                        state = 30;
                        break label122;
                     case 60:
                        state = 61;
                        break label122;
                     case 62:
                        state = 63;
                        break label122;
                     default:
                        state = 0;
                        break label122;
                  }
               case 'B':
               case 'b':
                  if (state == 41) {
                     state = 42;
                  } else {
                     state = 0;
                  }
                  break;
               case 'D':
               case 'd':
                  if (state == 0) {
                     state = 60;
                  } else {
                     state = 0;
                  }
                  break;
               case 'E':
               case 'e':
                  switch (state) {
                     case 44:
                        state = 45;
                        break label122;
                     case 51:
                        state = 52;
                        break label122;
                     case 53:
                        state = 54;
                        break label122;
                     default:
                        state = 0;
                        break label122;
                  }
               case 'H':
               case 'h':
                  if (state == 0) {
                     state = 10;
                  } else {
                     state = 0;
                  }
                  break;
               case 'I':
               case 'i':
                  if (state == 42) {
                     state = 43;
                  } else {
                     state = 0;
                  }
                  break;
               case 'L':
               case 'l':
                  if (state == 43) {
                     state = 44;
                  } else {
                     state = 0;
                  }
                  break;
               case 'M':
               case 'm':
                  if (state == 0) {
                     state = 40;
                  } else {
                     state = 0;
                  }
                  break;
               case 'O':
               case 'o':
                  if (state == 40) {
                     state = 41;
                  } else {
                     state = 0;
                  }
                  break;
               case 'P':
               case 'p':
                  switch (state) {
                     case 12:
                        state = 13;
                        break label122;
                     case 30:
                        state = 31;
                        break label122;
                     case 72:
                        state = 73;
                        break label122;
                     default:
                        state = 0;
                        break label122;
                  }
               case 'Q':
               case 'q':
                  if (state == 0) {
                     state = 50;
                  } else {
                     state = 0;
                  }
                  break;
               case 'R':
               case 'r':
                  if (state == 0) {
                     state = 70;
                  } else {
                     state = 0;
                  }
                  break;
               case 'S':
               case 's':
                  switch (state) {
                     case 13:
                        state = 15;
                        break label122;
                     case 71:
                        state = 72;
                        break label122;
                     default:
                        state = 0;
                        break label122;
                  }
               case 'T':
               case 't':
                  switch (state) {
                     case 10:
                        state = 11;
                        break label122;
                     case 11:
                        state = 12;
                        break label122;
                     case 61:
                        state = 62;
                        break label122;
                     case 70:
                        state = 71;
                        break label122;
                     default:
                        state = 0;
                        break label122;
                  }
               case 'U':
               case 'u':
                  switch (state) {
                     case 50:
                        state = 51;
                        break label122;
                     case 52:
                        state = 53;
                        break label122;
                     default:
                        state = 0;
                        break label122;
                  }
               case 'W':
               case 'w':
                  switch (state) {
                     case 0:
                        state = 20;
                        break label122;
                     case 20:
                        state = 21;
                        break label122;
                     case 21:
                        state = 22;
                        break label122;
                     default:
                        state = 0;
                        break label122;
                  }
               default:
                  state = 0;
            }

            bufIndex++;
         }
      }

      return index;
   }

   private static final boolean isValidBeginIndex(AbstractString str, int beginIndex) {
      while (beginIndex > 0) {
         char c = str.charAt(beginIndex - 1);
         if (c == '|') {
            return true;
         }

         if (!isPunctuation(c)) {
            if (StringPattern.isWhitespace(c)) {
               return true;
            }

            return false;
         }

         beginIndex--;
      }

      return true;
   }

   private static final int getEndIndex(AbstractString str, int beginIndex, int maxIndex) {
      int endIndex = beginIndex + 1;

      while (endIndex < maxIndex && isValidChar(str.charAt(endIndex))) {
         endIndex++;
      }

      while (endIndex > beginIndex && isPunctuation(str.charAt(endIndex - 1))) {
         endIndex--;
      }

      return endIndex;
   }

   private static final boolean isValidChar(char c) {
      if (c >= 'a' && c <= 'z') {
         return true;
      }

      if (c >= 'A' && c <= 'Z') {
         return true;
      }

      if (c >= '0' && c <= '9') {
         return true;
      }

      switch (c) {
         case '!':
         case '#':
         case '$':
         case '%':
         case '&':
         case '\'':
         case '(':
         case ')':
         case '*':
         case '+':
         case ',':
         case '-':
         case '.':
         case '/':
         case ':':
         case ';':
         case '=':
         case '?':
         case '@':
         case '_':
         case '{':
         case '}':
         case '~':
            return true;
         default:
            return false;
      }
   }

   private static final boolean isPunctuation(char c) {
      switch (c) {
         case '!':
         case '"':
         case '\'':
         case '(':
         case ')':
         case ',':
         case '.':
         case ':':
         case ';':
         case '<':
         case '>':
         case '?':
         case '[':
         case ']':
         case '{':
         case '|':
         case '}':
            return true;
         default:
            return false;
      }
   }

   @Override
   public final synchronized boolean findMatch(AbstractString string, int index, int maxIndex, StringPattern$Match match) {
      if (string == null) {
         return false;
      }

      if (!this._validConfig) {
         this.browserConfigChanged();
      }

      if (this._enabled && !this._constrainedNavigation) {
         char[] scanbuffer = WeakReferenceUtilities.getCharArray(this._scanbufferWR, 64);

         while (index < maxIndex) {
            int beginIndex = this.getNextIndex(scanbuffer, string, index, maxIndex);
            if (beginIndex < index) {
               break;
            }

            if (beginIndex >= maxIndex) {
               return false;
            }

            match.prefixLength = maxIndex;
            switch (string.charAt(beginIndex)) {
               case 'M':
               case 'm':
                  match.prefixLength = 7;
                  break;
               case 'W':
               case 'w':
                  match.prefixLength = 4;
                  break;
               default:
                  if (beginIndex + 4 < maxIndex) {
                     switch (string.charAt(beginIndex + 4)) {
                        case ':':
                           match.prefixLength = 5;
                           break;
                        case 'E':
                        case 'S':
                        case 'e':
                        case 's':
                           match.prefixLength = 6;
                     }
                  }
            }

            if (isValidBeginIndex(string, beginIndex)) {
               match.id = 5019899335844518230L;
               match.beginIndex = beginIndex;
               match.endIndex = getEndIndex(string, beginIndex + match.prefixLength - 1, maxIndex);
               if (match.endIndex - beginIndex > match.prefixLength) {
                  return true;
               }
            }

            index = beginIndex + match.prefixLength;
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public final long getPatternTypeIdentifier() {
      return 5019899335844518230L;
   }

   public HTTPAddressStringPattern() {
      BrowserDaemonRegistry.addBrowserConfigChangeListener(this);
   }
}
