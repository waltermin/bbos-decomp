package net.rim.device.apps.internal.api.serialformats;

import java.io.InputStream;
import java.io.Reader;
import java.util.Calendar;
import java.util.Date;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

final class TokenParser {
   private Reader _in;
   private int _pushBackByte = -1;
   private byte[] _buffer = new byte[32];
   private int _previousCharacter;
   private byte[] _attributes = new byte[256];
   private boolean _ignoreWhitespace;
   private boolean _isLineBreak;
   private boolean _isUTCTime;
   private static final int FOLDING_LIMIT;
   private static final int LIMIT_TO_READ;
   private static final int END_STATE;
   private static final int CR;
   private static final int LF;
   private static final int TAB;
   private static final int SPACE;
   private static final int BACKSLASH;
   private static final int COMMA;
   private static final int COLON;
   private static final int SEMICOLON;
   private static final int EQUAL;
   private static final int END_OF_TOKEN;

   TokenParser(InputStream in, String encoding) {
      if (encoding.length() == 0) {
         this._in = (Reader)(new Object(in, "UTF8"));
      } else {
         this._in = (Reader)(new Object(in, encoding));
      }
   }

   final void setEndOfTokenCharacters(String chars) {
      byte[] c = chars.getBytes();

      for (int i = 0; i < c.length; i++) {
         this._attributes[c[i]] = (byte)(this._attributes[c[i]] | 1);
      }
   }

   final void clearEndOfTokenCharacters(String chars) {
      byte[] c = chars.getBytes();

      for (int i = 0; i < c.length; i++) {
         this._attributes[c[i]] = (byte)(this._attributes[c[i]] & -2);
      }
   }

   final void ignoreWhitespaceTokens(boolean flag) {
      this._ignoreWhitespace = flag;
   }

   private final void addToBuffer(int b, int length) {
      if (length >= this._buffer.length) {
         Array.resize(this._buffer, this._buffer.length * 2);
      }

      this._buffer[length] = (byte)b;
   }

   final String nextToken() {
      int length = 0;
      int charLength = 0;
      int state = 1;
      boolean quoteFlag = false;
      this._isLineBreak = false;

      do {
         int b;
         if (this._pushBackByte >= 0) {
            b = this._pushBackByte;
         } else {
            b = this._in.read();
            this._previousCharacter = b;
         }

         this.addToBuffer(b, length);
         length++;
         charLength++;
         int attribute = this._attributes[b];
         if (attribute == 1) {
            if (!quoteFlag) {
               if (b == 61) {
                  if (this._pushBackByte >= 0) {
                     this._pushBackByte = -1;
                  } else {
                     this._pushBackByte = b;
                     length--;
                  }
                  break;
               }

               if (b != 34) {
                  length--;
                  break;
               }

               quoteFlag = true;
            } else if (b == 34) {
               quoteFlag = false;
            }
         }

         switch (state) {
            case 0:
               break;
            case 1:
            default:
               if (b == 92) {
                  state = 2;
               } else if (b == 13) {
                  state = 3;
               } else if (b == 10) {
                  state = 4;
                  int limitToRead = 375;
                  this._in.mark(limitToRead);
                  length++;
               } else if (this._ignoreWhitespace && (b == 9 || b == 32)) {
                  length--;
               }
               break;
            case 2:
               if (b == 44 || b == 92 || b == 59) {
                  this._buffer[length - 2] = (byte)b;
                  length--;
                  state = 1;
               } else if (b != 110 && b != 78) {
                  if (!this._ignoreWhitespace || b != 9 && b != 32) {
                     state = 1;
                  } else {
                     length--;
                     state = 1;
                  }
               } else {
                  state = 5;
                  int limitToRead = 375;
                  this._in.mark(limitToRead);
               }
               break;
            case 3:
               if (b == 10) {
                  state = 4;
                  int limitToRead = 375;
                  this._in.mark(limitToRead);
               } else if (!this._ignoreWhitespace || b != 9 && b != 32) {
                  state = 1;
               } else {
                  length--;
                  state = 1;
               }
               break;
            case 4:
               if (b != 32 && b != 9) {
                  this._in.reset();
                  length -= 3;
                  this._isLineBreak = true;
                  state = 1000;
               } else {
                  if (charLength - 3 > 75) {
                     throw new InvalidFormatException();
                  }

                  charLength = 0;
                  length -= 3;
                  state = 1;
               }
               break;
            case 5:
               if (b == 13) {
                  state = 6;
               } else {
                  this._in.reset();
                  length -= 3;
                  this._isLineBreak = true;
                  state = 1000;
               }
               break;
            case 6:
               if (b != 10) {
                  throw new InvalidFormatException();
               }

               int limitToRead = 375;
               this._in.mark(limitToRead);
               state = 7;
               break;
            case 7:
               if (b != 32 && b != 9) {
                  this._in.reset();
                  length -= 5;
                  this._isLineBreak = true;
                  state = 1000;
               } else {
                  length -= 5;
                  state = 1;
               }
         }
      } while (state != 1000);

      return (String)(new Object(this._buffer, 0, length));
   }

   final String nextProperty() {
      int length = 0;
      int periodPosition = 0;
      this._isLineBreak = false;

      while (true) {
         int b = this._in.read();
         this._previousCharacter = b;
         if (b < 0) {
            throw new Object();
         }

         if (b != 9 && b != 32) {
            this.addToBuffer(b, length);
            length++;
            if (b == 46) {
               periodPosition = length;
            }

            if (b == 59 || b == 58) {
               length--;
               break;
            }

            if (b == 45 && length == 2) {
               break;
            }
         }
      }

      return StringUtilities.toUpperCase((String)(new Object(this._buffer, periodPosition, length - periodPosition)), 1701707776);
   }

   final String getCommonOneLine(int additionalStopSign) {
      int length = 0;
      int charLength = 0;
      int state = 1;

      do {
         int b = this._in.read();
         this._previousCharacter = b;
         if (b < 0) {
            throw new Object();
         }

         this.addToBuffer(b, length);
         length++;
         charLength++;
         switch (state) {
            case 0:
               break;
            case 1:
            default:
               if (b == additionalStopSign) {
                  this._isLineBreak = false;
                  length--;
                  state = 1000;
               } else if (b == 13) {
                  state = 2;
               } else if (b == 92) {
                  state = 3;
               } else if (b == 10) {
                  this._in.mark(375);
                  length++;
                  state = 4;
               }
               break;
            case 2:
               if (b == 10) {
                  state = 4;
                  int limitToRead = 375;
                  this._in.mark(limitToRead);
               } else {
                  state = 1;
               }
               break;
            case 3:
               if (b == 110 || b == 78) {
                  this._buffer[length - 2] = 10;
                  length--;
                  state = 1;
               } else if (b != 44 && b != 59 && b != 92) {
                  state = 1;
               } else {
                  this._buffer[length - 2] = (byte)b;
                  length--;
                  state = 1;
               }
               break;
            case 4:
               if (b != 32 && b != 9) {
                  this._in.reset();
                  length -= 3;
                  this._isLineBreak = true;
                  state = 1000;
               } else {
                  if (charLength - 3 > 75) {
                     throw new InvalidFormatException();
                  }

                  charLength = 0;
                  length -= 3;
                  state = 1;
               }
         }
      } while (state != 1000);

      return (String)(new Object(this._buffer, 0, length));
   }

   final Date getDateTime() {
      int state = 1;
      int length = 0;
      Calendar calendar = Calendar.getInstance();
      DateTimeUtilities.zeroCalendarTime(calendar);
      this._isLineBreak = false;
      this._isUTCTime = false;

      do {
         int b = this._in.read();
         this._buffer[length] = (byte)b;
         this._previousCharacter = b;
         length++;
         switch (state) {
            case 0:
               break;
            case 1:
            default:
               if (b == 32) {
                  length--;
               } else if (b == 13) {
                  calendar = null;
                  state = 6;
               } else if (b == 10) {
                  calendar = null;
                  this._isLineBreak = true;
                  state = 1000;
               } else if (b == 92) {
                  calendar = null;
                  state = 5;
               } else if (length == 4) {
                  String str = (String)(new Object(this._buffer, 0, 4));
                  int year = Integer.parseInt(str, 10);
                  calendar.set(1, year);
                  length = 0;
                  state = 2;
               }
               break;
            case 2:
               if (b == 92) {
                  state = 5;
               } else if (b == 13) {
                  state = 6;
               } else if (b == 10) {
                  this._isLineBreak = true;
                  state = 1000;
               } else if (b == 44 || b == 59) {
                  this._isLineBreak = false;
                  state = 1000;
               } else if (b == 45) {
                  length = 0;
               } else if (length == 2) {
                  String var10 = new Object(this._buffer, 0, 2);
                  int month = Integer.parseInt((String)var10, 10);
                  calendar.set(2, month - 1);
                  length = 0;
                  state = 3;
               }
               break;
            case 3:
               if (b == 92) {
                  state = 5;
               } else if (b == 13) {
                  state = 6;
               } else if (b == 10) {
                  this._isLineBreak = true;
                  state = 1000;
               } else if (b == 44 || b == 59) {
                  this._isLineBreak = false;
                  state = 1000;
               } else if (b == 45) {
                  length = 0;
               } else if (length == 2) {
                  String var9 = new Object(this._buffer, 0, 2);
                  int day = Integer.parseInt((String)var9, 10);
                  calendar.set(5, day);
                  length = 0;
                  state = 4;
               }
               break;
            case 4:
               if (b == 92) {
                  state = 5;
               } else if (b == 13) {
                  state = 6;
               } else if (b == 10) {
                  this._isLineBreak = true;
                  state = 1000;
               } else if (b != 44 && b != 59) {
                  if (b != 84 && b != 116) {
                     throw new InvalidFormatException();
                  }

                  length = 0;
                  state = 7;
               } else {
                  this._isLineBreak = false;
                  state = 1000;
               }
               break;
            case 5:
               if (b != 78 || b != 110) {
                  throw new InvalidFormatException();
               }

               this._isLineBreak = true;
               state = 1000;
               break;
            case 6:
               if (b != 10) {
                  throw new InvalidFormatException();
               }

               this._isLineBreak = true;
               state = 1000;
               break;
            case 7:
               if (length == 2) {
                  String var8 = new Object(this._buffer, 0, 2);
                  int hour = Integer.parseInt((String)var8, 10);
                  calendar.set(11, hour);
                  length = 0;
                  state = 8;
               }
               break;
            case 8:
               if (b == 58) {
                  length = 0;
               } else if (length == 2) {
                  String var7 = new Object(this._buffer, 0, 2);
                  int minute = Integer.parseInt((String)var7, 10);
                  calendar.set(12, minute);
                  length = 0;
                  state = 9;
               }
               break;
            case 9:
               if (b == 58) {
                  length = 0;
                  state = 10;
               } else if (b == 43 || b == 45) {
                  length = 0;
                  state = 13;
               } else if (b >= 48 && b <= 57) {
                  state = 10;
               } else {
                  if (b != 90) {
                     throw new InvalidFormatException();
                  }

                  this._isUTCTime = true;
                  state = 12;
               }
               break;
            case 10:
               if (length == 2) {
                  String str = new Object(this._buffer, 0, 2);
                  int second = Integer.parseInt((String)str, 10);
                  calendar.set(13, second);
                  length = 0;
                  state = 11;
               }
               break;
            case 11:
               if (b == 92) {
                  state = 5;
               } else if (b == 13) {
                  state = 6;
               } else if (b == 10) {
                  this._isLineBreak = true;
                  state = 1000;
               } else if (b == 44 || b == 59) {
                  this._isLineBreak = false;
                  state = 1000;
               } else if (b != 43 && b != 45) {
                  if (b != 90) {
                     throw new InvalidFormatException();
                  }

                  this._isUTCTime = true;
                  state = 12;
               } else {
                  length = 0;
                  state = 13;
               }
               break;
            case 12:
               if (b == 92) {
                  state = 5;
               } else if (b == 13) {
                  state = 6;
               } else if (b == 10) {
                  this._isLineBreak = true;
                  state = 1000;
               } else {
                  if (b != 44 && b != 59) {
                     throw new InvalidFormatException();
                  }

                  this._isLineBreak = false;
                  state = 1000;
               }
               break;
            case 13:
               if (length == 2) {
                  length = 0;
                  state = 14;
               }
               break;
            case 14:
               if (b == 58) {
                  length = 0;
                  state = 15;
               } else {
                  if (b < 48 || b > 57) {
                     throw new InvalidFormatException();
                  }

                  state = 15;
               }
               break;
            case 15:
               if (length == 2) {
                  length = 0;
                  state = 16;
               }
               break;
            case 16:
               if (b == 92) {
                  state = 5;
               } else if (b == 13) {
                  state = 6;
               } else if (b == 10) {
                  this._isLineBreak = true;
                  state = 1000;
               } else {
                  if (b != 44 && b != 59) {
                     throw new InvalidFormatException();
                  }

                  this._isLineBreak = false;
                  state = 1000;
               }
         }
      } while (state != 1000);

      return calendar == null ? null : calendar.getTime();
   }

   final boolean isUTCTime() {
      return this._isUTCTime;
   }

   private final void readChars(int numchars) {
      this._in.mark(375);

      int b;
      do {
         b = this._in.read();
      } while (b == 32);

      this._buffer[0] = (byte)b;

      for (int i = 1; i < numchars; i++) {
         this._buffer[i] = (byte)this._in.read();
      }
   }

   final boolean isLeadingString(String text) {
      this.readChars(text.length());
      this._in.reset();
      return text.compareTo((String)(new Object(this._buffer, 0, text.length()))) == 0;
   }

   final boolean readIfLeadingString(String text) {
      this.readChars(text.length());
      if (text.compareTo((String)(new Object(this._buffer, 0, text.length()))) == 0) {
         this._in.mark(0);
         this._in.reset();
         return true;
      } else {
         this._in.reset();
         return false;
      }
   }

   final byte[] getQuotedPrintableContent(int additionalStopSign) {
      int MAXIMUM_LENGTH = 76;
      int length = 0;
      int charLength = 0;
      int state = 1;
      int stateBeforeBreak = 1;

      do {
         int b = this._in.read();
         this.addToBuffer(b, length);
         length++;
         charLength++;
         switch (state) {
            case 0:
               break;
            case 1:
            default:
               if (b == additionalStopSign) {
                  length--;
                  this._isLineBreak = false;
                  state = 1000;
               } else if (b == 13) {
                  state = 2;
               } else if (b == 92) {
                  state = 3;
               } else {
                  if (b != 9 && b != 32) {
                     if (b == 61) {
                        state = 5;
                        stateBeforeBreak = 1;
                     } else if (!this.isLiteralRepresentation(b)) {
                        throw new InvalidFormatException();
                     }
                     continue;
                  }

                  state = 4;
               }
               break;
            case 2:
               if (b != 10) {
                  throw new InvalidFormatException();
               }

               this._isLineBreak = true;
               state = 1000;
               break;
            case 3:
               if (b != 59 && b != 44 && b != 92) {
                  if (b == additionalStopSign) {
                     length--;
                     this._isLineBreak = false;
                     state = 1000;
                     break;
                  }

                  if (b != 9 && b != 32) {
                     if (b == 61) {
                        state = 5;
                        stateBeforeBreak = 1;
                     } else {
                        if (!this.isLiteralRepresentation(b)) {
                           throw new InvalidFormatException();
                        }

                        state = 1;
                     }
                     break;
                  }

                  state = 4;
                  break;
               }

               length--;
               this._buffer[length - 1] = (byte)b;
               state = 1;
               break;
            case 4:
               if (b == additionalStopSign) {
                  length--;
                  this._isLineBreak = false;
                  state = 1000;
               } else if (b == 92) {
                  state = 3;
               } else {
                  if (b != 9 && b != 32) {
                     if (b == 61) {
                        stateBeforeBreak = 1;
                        state = 5;
                     } else {
                        if (!this.isLiteralRepresentation(b)) {
                           throw new InvalidFormatException();
                        }

                        state = 1;
                     }
                     continue;
                  }

                  state = 4;
               }
               break;
            case 5:
               if (b == additionalStopSign) {
                  length--;
                  this._isLineBreak = false;
                  state = 1000;
               } else if (b == 13) {
                  state = 6;
               } else if (b == 92) {
                  state = 3;
               } else {
                  if (b != 9 && b != 32) {
                     if (b == 61) {
                        state = 5;
                        continue;
                     }

                     if (b == 48) {
                        state = 7;
                        continue;
                     }

                     if (b >= 49 && b <= 57) {
                        state = 13;
                        continue;
                     }

                     if (!this.isLiteralRepresentation(b)) {
                        throw new InvalidFormatException();
                     }

                     state = 1;
                     continue;
                  }

                  state = 4;
               }
               break;
            case 6:
               if (b != 10) {
                  throw new InvalidFormatException();
               }

               if (charLength - 3 > MAXIMUM_LENGTH) {
                  throw new InvalidFormatException();
               }

               length -= 3;
               charLength = 0;
               state = stateBeforeBreak;
               break;
            case 7:
               stateBeforeBreak = 1;
               if (b == additionalStopSign) {
                  length--;
                  this._isLineBreak = false;
                  state = 1000;
               } else {
                  if (b != 68 && b != 100) {
                     if ((b < 66 || b > 90) && (b < 98 || b > 122)) {
                        if (b == 13) {
                           stateBeforeBreak = 7;
                           state = 6;
                           continue;
                        }

                        if (b == 92) {
                           state = 3;
                           continue;
                        }

                        if (b != 9 && b != 32) {
                           if (b == 61) {
                              state = 5;
                           } else {
                              if (!this.isLiteralRepresentation(b)) {
                                 throw new InvalidFormatException();
                              }

                              state = 1;
                           }
                           continue;
                        }

                        state = 4;
                        continue;
                     }

                     byte bt = this.translate8BitRepresentation(48, b);
                     length -= 2;
                     this._buffer[length - 1] = bt;
                     state = 1;
                     continue;
                  }

                  state = 8;
               }
               break;
            case 8:
               stateBeforeBreak = 1;
               if (b == additionalStopSign) {
                  length--;
                  this._isLineBreak = false;
                  state = 1000;
               } else if (b == 61) {
                  state = 9;
               } else if (b == 13) {
                  stateBeforeBreak = 8;
                  state = 6;
               } else if (b == 92) {
                  state = 3;
               } else {
                  if (b != 9 && b != 32) {
                     if (b == 61) {
                        state = 5;
                     } else {
                        if (!this.isLiteralRepresentation(b)) {
                           throw new InvalidFormatException();
                        }

                        state = 1;
                     }
                     continue;
                  }

                  state = 4;
               }
               break;
            case 9:
               stateBeforeBreak = 1;
               if (b == additionalStopSign) {
                  length--;
                  this._isLineBreak = false;
                  state = 1000;
               } else if (b == 48) {
                  state = 10;
               } else if (b == 13) {
                  stateBeforeBreak = 8;
                  state = 6;
               } else if (b == 92) {
                  state = 3;
               } else {
                  if (b != 9 && b != 32) {
                     if (b == 61) {
                        stateBeforeBreak = 9;
                        state = 5;
                     } else {
                        if (!this.isLiteralRepresentation(b)) {
                           throw new InvalidFormatException();
                        }

                        state = 1;
                     }
                     continue;
                  }

                  state = 4;
               }
               break;
            case 10:
               stateBeforeBreak = 1;
               if (b == additionalStopSign) {
                  length--;
                  this._isLineBreak = false;
                  state = 1000;
               } else {
                  if (b != 65 && b != 97) {
                     if (b != 68 && b != 100) {
                        if (b == 13) {
                           stateBeforeBreak = 10;
                           state = 6;
                           continue;
                        }

                        if (b == 92) {
                           state = 3;
                           continue;
                        }

                        if (b != 9 && b != 32) {
                           if (b == 61) {
                              state = 5;
                              continue;
                           }

                           if (!this.isLiteralRepresentation(b)) {
                              throw new InvalidFormatException();
                           }

                           if (b >= 65 && b <= 90 || b >= 97 && b <= 122) {
                              byte bt = this.translate8BitRepresentation(48, b);
                              length -= 2;
                              this._buffer[length - 1] = bt;
                           }

                           state = 1;
                           continue;
                        }

                        state = 4;
                        continue;
                     }

                     state = 8;
                     continue;
                  }

                  state = 11;
               }
               break;
            case 11:
               if (b == additionalStopSign) {
                  length -= 7;
                  this._isLineBreak = false;
                  state = 1000;
               } else if (b == 13) {
                  state = 12;
               } else {
                  length -= 5;
                  charLength = 0;
                  this._buffer[length - 2] = 10;
                  this._buffer[length - 1] = (byte)b;
                  this._isLineBreak = true;
                  if (b == 92) {
                     state = 3;
                  } else {
                     if (b != 9 && b != 32) {
                        if (b == 61) {
                           state = 5;
                        } else {
                           if (!this.isLiteralRepresentation(b)) {
                              throw new InvalidFormatException();
                           }

                           state = 1;
                        }
                        continue;
                     }

                     state = 4;
                  }
               }
               break;
            case 12:
               if (b != 10) {
                  throw new InvalidFormatException();
               }

               length -= 8;
               this._isLineBreak = true;
               state = 1000;
               break;
            case 13:
               stateBeforeBreak = 1;
               if (b == additionalStopSign) {
                  length--;
                  this._isLineBreak = false;
                  state = 1000;
               } else if (b == 61) {
                  state = 5;
               } else if (b == 13) {
                  stateBeforeBreak = 13;
                  state = 6;
               } else if (b == 92) {
                  state = 3;
               } else if (b != 9 && b != 32) {
                  if (!this.isLiteralRepresentation(b)) {
                     throw new InvalidFormatException();
                  }

                  if (b >= 65 && b <= 90 || b >= 97 && b <= 122) {
                     length -= 2;
                     byte bt = this.translate8BitRepresentation(this._buffer[length], b);
                     this._buffer[length - 1] = bt;
                  }

                  state = 1;
               } else {
                  state = 4;
               }
         }
      } while (state != 1000);

      return Arrays.copy(this._buffer, 0, length);
   }

   final byte[] getBase64Content(int additionalStopSign) {
      int length = 0;
      int charLength = 0;
      int state = 1;

      do {
         int b = this._in.read();
         this.addToBuffer(b, length);
         length++;
         charLength++;
         switch (state) {
            case 0:
               break;
            case 1:
            default:
               if (b == additionalStopSign) {
                  this._isLineBreak = false;
                  state = 1000;
               } else if (b == 13) {
                  state = 2;
               }
               break;
            case 2:
               if (b != 10) {
                  throw new InvalidFormatException();
               }

               state = 3;
               int limitToRead = 375;
               this._in.mark(limitToRead);
               break;
            case 3:
               if (b != 32 && b != 9) {
                  this._in.reset();
                  length -= 3;
                  this._isLineBreak = true;
                  state = 1000;
               } else {
                  if (charLength - 3 > 75) {
                     throw new InvalidFormatException();
                  }

                  charLength = 0;
                  length -= 3;
                  state = 1;
               }
         }
      } while (state != 1000);

      Base64InputStream base64In = (Base64InputStream)(new Object((InputStream)(new Object(this._buffer, 0, length))));
      length = 0;

      while (true) {
         int var8 = base64In.read();
         if (var8 == -1) {
            return Arrays.copy(this._buffer, 0, length);
         }

         this._buffer[length] = (byte)var8;
         length++;
      }
   }

   private final boolean isLiteralRepresentation(int ch) {
      return ch >= 33 && ch <= 126;
   }

   private final byte translate8BitRepresentation(int ch1, int ch2) {
      int ch;
      if (ch2 >= 97) {
         ch = ch2 - 97 + 10;
      } else {
         ch = ch2 - 65 + 10;
      }

      ch = (ch1 - 48) * 16 + ch;
      return (byte)ch;
   }

   final int getBreakingCharacter() {
      return this._previousCharacter;
   }

   final boolean isLineBreak() {
      return this._isLineBreak;
   }
}
