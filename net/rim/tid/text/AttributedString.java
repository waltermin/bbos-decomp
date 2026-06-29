package net.rim.tid.text;

import net.rim.device.api.ui.Ui;
import net.rim.device.internal.ui.StringBufferGap;

public class AttributedString {
   private int _length;
   private AttributedString$Run _run;
   private int _cursor;
   private AttributedString$Run _cursor_run;
   private int _cursor_run_start;
   private StringBufferGap _text;
   private long _new_attrib;
   private long _new_xattrib;
   private long _global_attrib;
   public static final long FONT_HEIGHT_MASK;
   public static final long ANTI_ALIAS_MODE_MASK;
   public static final long BOLD_MASK;
   public static final long ITALIC_MASK;
   public static final long FONT_NAME_INDEX_MASK;
   public static final long BASELINE_OFFSET_MASK;
   public static final long UNDERLINE_MASK;
   public static final long STRIKETHROUGH_MASK;
   public static final long JUSTIFY_MASK;
   public static final long PAR_DIR_MASK;
   public static final long HIGHLIGHT_MASK;
   public static final long PASSWORD_MODE_MASK;
   public static final long SYSTEM_ATTRIB_MASK;
   public static final long FOREGROUND_COLOR_MASK;
   public static final long BACKGROUND_COLOR_MASK;
   public static final long FONT_ATTRIB_MASK;
   public static final int FONT_HEIGHT_SHIFT;
   public static final int ANTI_ALIAS_MODE_SHIFT;
   public static final int BOLD_SHIFT;
   public static final int ITALIC_SHIFT;
   public static final int FONT_NAME_INDEX_SHIFT;
   public static final int BASELINE_OFFSET_SHIFT;
   public static final int UNDERLINE_SHIFT;
   public static final int STRIKETHROUGH_SHIFT;
   public static final int JUSTIFY_SHIFT;
   public static final int PAR_DIR_SHIFT;
   public static final int HIGHLIGHT_SHIFT;
   public static final int FOREGROUND_COLOR_SHIFT;
   public static final int BACKGROUND_COLOR_SHIFT;
   public static final long NO_ANTIALIAS;
   public static final long STANDARD_ANTIALIAS;
   public static final long SUB_PIXEL_RENDERING;
   public static final long LOW_RESOLUTION_ANTIALIAS;
   public static final long NO_BOLD;
   public static final long BOLD;
   public static final long EXTRA_BOLD;
   public static final long NO_ITALIC;
   public static final long ITALIC;
   public static final long STANDARD_BASELINE;
   public static final long SUPERSCRIPT;
   public static final long SUBSCRIPT;
   public static final long NO_UNDERLINE;
   public static final long SOLID_UNDERLINE;
   public static final long BROKEN_UNDERLINE;
   public static final long DOTTED_UNDERLINE;
   public static final long NO_STRIKETHROUGH;
   public static final long SOLID_STRIKETHROUGH;
   public static final long BROKEN_STRIKETHROUGH;
   public static final long DOTTED_STRIKETHROUGH;
   public static final long JUSTIFY_STANDARD;
   public static final long JUSTIFY_REVERSE;
   public static final long JUSTIFY_FULL;
   public static final long JUSTIFY_CENTER;
   public static final long PAR_DEFAULT_LEFT_TO_RIGHT;
   public static final long PAR_DEFAULT_RIGHT_TO_LEFT;
   public static final long PAR_FORCE_LEFT_TO_RIGHT;
   public static final long PAR_FORCE_RIGHT_TO_LEFT;
   public static final long NO_HIGHLIGHT;
   public static final long HIGHLIGHT;
   public static final long X_ATTRIB_PICTURE_FLOAT_MASK;
   public static final long X_ATTRIB_PICTURE_CLEAR_MASK;
   public static final long X_ATTRIB_PICTURE_FLOAT_SHIFT;
   public static final long X_ATTRIB_PICTURE_CLEAR_SHIFT;
   public static final long PICTURE_FLOAT_NONE;
   public static final long PICTURE_FLOAT_LEFT;
   public static final long PICTURE_FLOAT_RIGHT;
   public static final long PICTURE_CLEAR_NONE;
   public static final long PICTURE_CLEAR_LEFT;
   public static final long PICTURE_CLEAR_RIGHT;
   public static final long PICTURE_CLEAR_BOTH;
   public static final long HAN_STYLE_MASK;
   public static final long HAN_STYLE_SHIFT;
   public static final long UNSPECIFIED_HAN;
   public static final long TRADITIONAL_HAN;
   public static final long SIMPLIFIED_HAN;
   public static final long JAPANESE_HAN;
   public static final long KOREAN_HAN;
   public static final int PICTURE_LINE_BREAK_DEFAULT;
   public static final int MAX_FONT_SIZE;

   public static int fontHeight(long aAttrib) {
      return (int)((aAttrib & 63) >> 0);
   }

   public static boolean bold(long aAttrib) {
      return (aAttrib & 134217984) != 0;
   }

   public static boolean italic(long aAttrib) {
      return (aAttrib & 512) != 0;
   }

   public static int fontNameIndex(long aAttrib) {
      return (int)((aAttrib & 64512) >> 10);
   }

   public static long setForegroundColor(long aAttrib, int aColor) {
      return aAttrib & -281470681743361L | Ui.convertColorTo16bit(aColor) << 32;
   }

   public AttributedString() {
      this._text = new StringBufferGap();
      this._run = new AttributedString$Run();
      this._cursor_run = this._run;
   }

   public AttributedString(String aText) {
      this.set(aText, 0, 0);
   }

   public AttributedString(String aText, long aAttrib, long aXAttrib) {
      this.set(aText, aAttrib, aXAttrib);
   }

   public AttributedString(StringBuffer aText) {
      this.set(aText.toString(), 0, 0);
   }

   public AttributedString(StringBufferGap aText) {
      this.set(aText.toString(), 0, 0);
   }

   public AttributedString(AttributedString aString) {
      this.set(aString.getIterator());
   }

   public AttributedString(AttributedString aString, int aStart, int aEnd) {
      this.set(aString.getIterator(aStart, aEnd));
   }

   public void set(String aText) {
      this.set(aText, 0, 0, 0);
   }

   public void set(String aText, long aAttrib, long aXAttrib) {
      this.set(aText, aAttrib, aXAttrib, 0);
   }

   public void set(String aText, long aAttrib, long aXAttrib, long aGlobalAttrib) {
      this._text = new StringBufferGap(aText);
      this._run = new AttributedString$Run(null, null, aText.length(), aAttrib, aXAttrib, null);
      this._cursor_run = this._run;
      this._cursor_run_start = 0;
      this._length = this._run._length;
      this._global_attrib = aGlobalAttrib;
   }

   public void set(AttributedString$Iterator aIter) {
      this._run = null;
      this._text = null;
      this._length = aIter.length();
      this._global_attrib = aIter.getGlobalAttrib();
      AttributedString$Run p = null;
      this._text = new StringBufferGap(aIter.text().getText(aIter.pos(), this._length));

      do {
         AttributedString$Run new_run = new AttributedString$Run(p, null, aIter.runLength(), aIter.runAttrib(), aIter.runXAttrib(), aIter.runPictureInfo());
         if (p == null) {
            this._run = new_run;
         } else {
            p._next = new_run;
         }

         p = new_run;
      } while (aIter.next());

      this._cursor = 0;
      this._cursor_run = this._run;
      this._cursor_run_start = 0;
   }

   public void set(StringBuffer aText) {
      this.set(aText.toString());
   }

   public void set(StringBufferGap aText) {
      this.set(aText.toString());
   }

   public String getString() {
      return this._text.toString();
   }

   @Override
   public String toString() {
      return this._text.toString();
   }

   public int length() {
      return this._length;
   }

   public char charAt(int aPos) {
      return this._text.charAt(aPos);
   }

   private void seekRun(int aPos) {
      if (this._cursor != aPos) {
         if (aPos > this._cursor) {
            while (aPos >= this._cursor_run_start + this._cursor_run._length && this._cursor_run._next != null) {
               this._cursor_run_start = this._cursor_run_start + this._cursor_run._length;
               this._cursor_run = this._cursor_run._next;
            }
         } else {
            while (aPos < this._cursor_run_start && this._cursor_run_start > 0) {
               this._cursor_run = this._cursor_run._prev;
               this._cursor_run_start = this._cursor_run_start - this._cursor_run._length;
            }
         }

         this._cursor = aPos;
      }
   }

   public void seek(int aPos) {
      if (aPos >= 0 && aPos <= this._length) {
         this._text.seek(aPos);
         if (this._cursor != aPos) {
            this.seekRun(aPos);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setInsertAttrib(long aAttrib) {
      this._new_attrib = aAttrib;
   }

   public void setInsertXAttrib(long aAttrib) {
      this._new_xattrib = aAttrib;
   }

   public void setGlobalAttrib(long aAttrib, long aAttribMask) {
      this._global_attrib = this._global_attrib & (aAttribMask ^ -1) | aAttrib & aAttribMask;
   }

   public long getGlobalAttrib() {
      return this._global_attrib;
   }

   private void deleteRun(AttributedString$Run aRun) {
      if (aRun._prev != null || aRun._next != null) {
         if (aRun._prev != null) {
            aRun._prev._next = aRun._next;
         }

         if (aRun._next != null) {
            aRun._next._prev = aRun._prev;
         }

         if (aRun == this._run) {
            this._run = aRun._next;
         }

         aRun._next = aRun._prev = null;
      }
   }

   private void insertRun(AttributedString$Run aPrevRun, int aLength, long aAttrib, long aXAttrib, AttributedString$PictureInfo aPictureInfo) {
      AttributedString$Run next = aPrevRun == null ? this._run : aPrevRun._next;
      AttributedString$Run new_run = new AttributedString$Run(aPrevRun, next, aLength, aAttrib, aXAttrib, aPictureInfo);
      if (aPrevRun != null) {
         aPrevRun._next = new_run;
      } else {
         this._run = new_run;
      }

      if (next != null) {
         next._prev = new_run;
      }
   }

   private void insertAttribRun(int aLength, long aAttrib, long aAttribMask, long aXAttrib, long aXAttribMask, AttributedString$PictureInfo aPictureInfo) {
      long attrib = this._new_attrib & (aAttribMask ^ -1) | aAttrib & aAttribMask;
      long x_attrib = this._new_xattrib & (aXAttribMask ^ -1) | aXAttrib & aXAttribMask;
      boolean no_pictures = aPictureInfo == null && this._cursor_run._pictureInfo == null;
      boolean isEmptyRun = this._cursor_run._length == 0;
      if (!isEmptyRun && (!no_pictures || (attrib != this._cursor_run._attrib || x_attrib != this._cursor_run._xAttrib) && this._cursor_run._length != 0)) {
         if (this._cursor > this._cursor_run_start && this._cursor < this._cursor_run_start + this._cursor_run._length) {
            this.insertRun(this._cursor_run._prev, this._cursor - this._cursor_run_start, this._cursor_run._attrib, this._cursor_run._xAttrib, null);
            this._cursor_run._length = this._cursor_run._length - this._cursor_run._prev._length;
            this._cursor_run_start = this._cursor;
         }

         if (this._cursor == this._cursor_run_start) {
            if (this._cursor_run._prev != null && attrib == this._cursor_run._prev._attrib && x_attrib == this._cursor_run._prev._xAttrib && no_pictures) {
               this._cursor_run = this._cursor_run._prev;
               this._cursor_run_start = this._cursor_run_start - this._cursor_run._length;
               this._cursor_run._length += aLength;
            } else {
               this.insertRun(this._cursor_run._prev, aLength, attrib, x_attrib, aPictureInfo);
               this._cursor_run_start += aLength;
            }
         } else {
            if (this._cursor_run._next != null && attrib == this._cursor_run._next._attrib && x_attrib == this._cursor_run._next._xAttrib && no_pictures) {
               this._cursor_run._next._length += aLength;
            } else {
               this.insertRun(this._cursor_run, aLength, attrib, x_attrib, aPictureInfo);
            }

            this._cursor_run_start = this._cursor_run_start + this._cursor_run._length;
            this._cursor_run = this._cursor_run._next;
         }
      } else {
         this._cursor_run._length += aLength;
         this._cursor_run._attrib = attrib;
         this._cursor_run._xAttrib = x_attrib;
         this._cursor_run._pictureInfo = aPictureInfo;
      }

      this._cursor += aLength;
      this._length += aLength;
   }

   public void delete(int aCount) {
      if (aCount < 0) {
         throw new IllegalArgumentException();
      }

      if (aCount > this._cursor) {
         throw new IndexOutOfBoundsException();
      }

      if (aCount != 0) {
         this._text.delete(aCount);
         this._length -= aCount;

         while (aCount > 0) {
            int n = aCount;
            if (n > this._cursor - this._cursor_run_start) {
               n = this._cursor - this._cursor_run_start;
               if (n == 0) {
                  this._cursor_run = this._cursor_run._prev;
                  this._cursor_run_start = this._cursor_run_start - this._cursor_run._length;
                  continue;
               }
            }

            this._cursor_run._length -= n;
            this._cursor -= n;
            aCount -= n;
            if (this._cursor_run._length == 0) {
               AttributedString$Run prev = this._cursor_run._prev;
               this.deleteRun(this._cursor_run);
               if (prev == null) {
                  this._cursor_run = this._run;
               } else {
                  this._cursor_run = prev;
                  this._cursor_run_start = this._cursor_run_start - prev._length;
                  if (this._cursor_run._next != null
                     && this._cursor_run._attrib == this._cursor_run._next._attrib
                     && this._cursor_run._xAttrib == this._cursor_run._next._xAttrib
                     && this._cursor_run._pictureInfo == null
                     && this._cursor_run._next._pictureInfo == null) {
                     this._cursor_run._length = this._cursor_run._length + this._cursor_run._next._length;
                     this.deleteRun(this._cursor_run._next);
                  } else if (this._cursor == this._cursor_run_start + this._cursor_run._length && this._cursor_run._next != null) {
                     this._cursor_run_start = this._cursor_run_start + this._cursor_run._length;
                     this._cursor_run = this._cursor_run._next;
                  }
               }
            }
         }

         this.seekRun(this._cursor);
      }
   }

   public void insert(char aChar) {
      this._text.insert(aChar);
      this.insertAttribRun(1, this._new_attrib, -1, this._new_xattrib, -1, null);
   }

   public void insert(int aPos, char aChar) {
      this.seek(aPos);
      this.insert(aChar);
   }

   public void insert(String aString) {
      if (aString.length() > 0) {
         this._text.insert(aString);
         this.insertAttribRun(aString.length(), this._new_attrib, -1, this._new_xattrib, -1, null);
      }
   }

   public void insert(StringBuffer aString) {
      if (aString.length() > 0) {
         this._text.insert(aString);
         this.insertAttribRun(aString.length(), this._new_attrib, -1, this._new_xattrib, -1, null);
      }
   }

   public void insert(int aPos, String aString) {
      this.seek(aPos);
      this.insert(aString);
   }

   public void insert(int aPos, AttributedString aString) {
      this.replace(aPos, aPos, aString, 0, aString.length());
   }

   public void insert(AttributedString$Picture aPicture) {
      this._text.insert('￼');
      this.insertAttribRun(1, this._new_attrib, -1, this._new_xattrib, -1, aPicture.getInfo());
   }

   public void insert(int aPos, AttributedString$Picture aPicture) {
      this.seek(aPos);
      this.insert(aPicture);
   }

   public void replace(int aStart, int aEnd, char aChar) {
      if (aStart >= 0 && aStart <= aEnd && aEnd <= this._length) {
         this.seek(aEnd);
         if (aStart < aEnd) {
            this.delete(aEnd - aStart);
         }

         this.insert(aChar);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void replace(int aStart, int aEnd, String aText) {
      if (aStart >= 0 && aStart <= aEnd && aEnd <= this._length) {
         this.seek(aEnd);
         if (aStart < aEnd) {
            this.delete(aEnd - aStart);
         }

         if (aText.length() > 0) {
            this.insert(aText);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void replace(int aStart, int aEnd, StringBuffer aText) {
      if (aStart >= 0 && aStart <= aEnd && aEnd <= this._length) {
         this.seek(aEnd);
         if (aStart < aEnd) {
            this.delete(aEnd - aStart);
         }

         if (aText.length() > 0) {
            this.insert(aText);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void replace(int aStart, int aEnd, AttributedString$Iterator aIter) {
      this.replace(aStart, aEnd, aIter, -1, -1);
   }

   public void replace(int aStart, int aEnd, AttributedString$Iterator aIter, long aAttribMask, long aXAttribMask) {
      if (aStart >= 0 && aStart <= aEnd && aEnd <= this._length) {
         this.seek(aEnd);
         if (aStart < aEnd) {
            this.delete(aEnd - aStart);
         }

         if (aIter.length() <= 0) {
            if (this._length == 0) {
               this._run._attrib = this._run._attrib & (aAttribMask ^ -1) | aIter.runAttrib() & aAttribMask;
               this._run._xAttrib = this._run._xAttrib & (aXAttribMask ^ -1) | aIter.runXAttrib() & aXAttribMask;
            }
         } else {
            this._text.insert(aIter.text().getText(aIter.pos(), aIter.length()));

            do {
               this.insertAttribRun(aIter.runLength(), aIter.runAttrib(), aAttribMask, aIter.runXAttrib(), aXAttribMask, aIter.runPictureInfo());
            } while (aIter.next());
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void replace(int aStart, int aEnd, AttributedString aString, int aStringStart, int aStringEnd) {
      AttributedString$Iterator iter = aString.getIterator(aStringStart, aStringEnd);
      this.replace(aStart, aEnd, iter);
   }

   public void replace(int aStart, int aEnd, AttributedString aString) {
      AttributedString$Iterator iter = aString.getIterator();
      this.replace(aStart, aEnd, iter);
   }

   public void delete(int aStart, int aEnd) {
      if (aStart >= 0 && aStart <= aEnd && aEnd <= this._length) {
         if (aStart < aEnd) {
            this.seek(aEnd);
            this.delete(aEnd - aStart);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void setAttrib(int aCount, long aAttrib, long aAttribMask) {
      this.setAttrib(aCount, aAttrib, aAttribMask, 0, 0);
   }

   public void setAttrib(int aCount, long aAttrib, long aAttribMask, long aXAttrib, long aXAttribMask) {
      AttributedString$Run r = this._cursor_run;
      int pos = this._cursor - this._cursor_run_start;

      while (aCount != 0 || this._cursor >= this._length) {
         int n = aCount;
         if (n > r._length - pos) {
            n = r._length - pos;
         }

         long attrib = r._attrib & (aAttribMask ^ -1) | aAttrib & aAttribMask;
         long x_attrib = r._xAttrib & (aXAttribMask ^ -1) | aXAttrib & aXAttribMask;
         if (attrib != r._attrib || x_attrib != r._xAttrib) {
            if (n == r._length) {
               r._attrib = attrib;
               r._xAttrib = x_attrib;
            } else {
               if (pos > 0) {
                  this.insertRun(r._prev, pos, r._attrib, r._xAttrib, null);
                  r._length -= pos;
                  if (r == this._cursor_run) {
                     this._cursor_run_start += pos;
                  }
               }

               if (n == r._length) {
                  r._attrib = attrib;
                  r._xAttrib = x_attrib;
               } else {
                  this.insertRun(r._prev, n, attrib, x_attrib, null);
                  r._length -= n;
                  if (r == this._cursor_run) {
                     if (this._cursor >= this._cursor_run_start + n) {
                        this._cursor_run_start += n;
                     } else {
                        this._cursor_run = r._prev;
                     }
                  }
               }
            }
         }

         if (r._prev != null && r._prev._attrib == r._attrib && r._prev._xAttrib == r._xAttrib && r._prev._pictureInfo == null && r._pictureInfo == null) {
            r._length = r._length + r._prev._length;
            if (this._cursor_run == r) {
               this._cursor_run_start = this._cursor_run_start - r._prev._length;
            } else if (this._cursor_run == r._prev) {
               this._cursor_run = r;
            }

            this.deleteRun(r._prev);
         }

         aCount -= n;
         pos = 0;
         r = r._next;
         if (aCount == 0) {
            break;
         }
      }

      if (r != null
         && r._prev != null
         && r._prev._attrib == r._attrib
         && r._prev._xAttrib == r._xAttrib
         && r._prev._pictureInfo == null
         && r._pictureInfo == null) {
         r._length = r._length + r._prev._length;
         if (this._cursor_run == r) {
            this._cursor_run_start = this._cursor_run_start - r._prev._length;
         } else if (this._cursor_run == r._prev) {
            this._cursor_run = r;
         }

         this.deleteRun(r._prev);
      }
   }

   public void setAttrib(int aStart, int aEnd, long aAttrib, long aAttribMask) {
      this.setAttrib(aStart, aEnd, aAttrib, aAttribMask, 0, 0);
   }

   public void setAttrib(int aStart, int aEnd, long aAttrib, long aAttribMask, long aXAttrib, long aXAttribMask) {
      if (aStart >= 0 && aStart <= aEnd && aEnd <= this._length) {
         int saved_cursor = this._cursor;
         this.seekRun(aStart);
         this.setAttrib(aEnd - aStart, aAttrib, aAttribMask, aXAttrib, aXAttribMask);
         this.seekRun(saved_cursor);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public String getText(int aStart, int aEnd) {
      if (aStart >= 0 && aStart <= aEnd && aEnd <= this._length) {
         return this._text.getText(aStart, aEnd - aStart);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public StringBufferGap getText() {
      return this._text;
   }

   public void setAttribToZero(int aStart, int aEnd, long aAttribMask) {
      this.setAttrib(aStart, aEnd, 0, aAttribMask);
   }

   public AttributedString$Iterator getIterator() {
      return new AttributedString$Iterator(this);
   }

   public AttributedString$Iterator getIterator(int aStart, int aEnd) {
      return new AttributedString$Iterator(this, aStart, aEnd);
   }

   public void assertValid() {
      if (this._run == null) {
         throw new IllegalStateException();
      }

      if (this._cursor_run == null) {
         throw new IllegalStateException();
      }

      if (this._length < 0) {
         throw new IllegalStateException();
      }

      if (this._length != this._text.length()) {
         throw new IllegalStateException();
      }

      if (this._cursor >= this._cursor_run_start && this._cursor <= this._cursor_run_start + this._cursor_run._length) {
         AttributedString$Run r = this._run;
         int l = 0;
         int s = 0;

         while (r != null) {
            if (r == this._cursor_run) {
               s = l;
            }

            if (r._pictureInfo != null && r._length != 1) {
               throw new IllegalStateException();
            }

            l += r._length;
            r = r._next;
         }

         if (s != this._cursor_run_start) {
            throw new IllegalStateException();
         }

         if (l != this._length) {
            throw new IllegalStateException();
         }
      } else {
         throw new IllegalStateException();
      }
   }
}
