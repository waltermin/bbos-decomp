package net.rim.tid.text;

import net.rim.device.internal.ui.StringBufferGap;

public class AttributedString$Iterator {
   private AttributedString$Run _cur_run;
   private int _total_length;
   private int _run_length;
   private int _pos;
   private int _start;
   private final AttributedString this$0;

   public AttributedString$Iterator(AttributedString _1) {
      this.this$0 = _1;
      this._total_length = _1._length;
      this._cur_run = _1._run;
      this._run_length = _1._run._length;
      this._start = this._pos = 0;
   }

   public AttributedString$Iterator(AttributedString _1, int aStart, int aEnd) {
      this.this$0 = _1;
      this.set(aStart, aEnd);
   }

   public void set(int aStart, int aEnd) {
      if (aStart >= 0 && aStart <= aEnd && aEnd <= this.this$0._length) {
         this._total_length = aEnd - aStart;
         this._cur_run = this.this$0._cursor_run;
         this._start = this._pos = aStart;
         int run_start = this.this$0._cursor_run_start;
         int run_end = run_start + this.this$0._cursor_run._length;
         if (this._pos >= run_end) {
            while (this._pos >= run_end && this._cur_run._next != null) {
               this._cur_run = this._cur_run._next;
               run_end += this._cur_run._length;
            }
         } else {
            while (this._pos < run_start) {
               this._cur_run = this._cur_run._prev;
               run_end = run_start;
               run_start -= this._cur_run._length;
            }
         }

         if (aEnd < run_end) {
            this._run_length = aEnd - this._pos;
         } else {
            this._run_length = run_end - this._pos;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int pos() {
      return this._pos;
   }

   public int length() {
      return this._total_length;
   }

   public StringBufferGap text() {
      return this.this$0._text;
   }

   public int runLength() {
      return this._run_length;
   }

   public long runAttrib() {
      return this._cur_run._attrib;
   }

   public long runXAttrib() {
      return this._cur_run._xAttrib;
   }

   public AttributedString$PictureInfo runPictureInfo() {
      return this._cur_run._pictureInfo;
   }

   public AttributedString$Picture runPicture() {
      return this._cur_run._pictureInfo == null ? null : this._cur_run._pictureInfo._picture;
   }

   public boolean next() {
      if (this._total_length == 0) {
         return false;
      }

      this._total_length = this._total_length - this._run_length;
      if (this._total_length == 0) {
         this._total_length = this._total_length + this._run_length;
         return false;
      }

      this._pos = this._pos + this._run_length;
      this._cur_run = this._cur_run._next;
      this._run_length = this._cur_run._length;
      if (this._total_length < this._run_length) {
         this._run_length = this._total_length;
      }

      return true;
   }

   public boolean prev() {
      if (this._pos == this._start) {
         return false;
      } else {
         this._cur_run = this._cur_run._prev;
         this._run_length = this._cur_run._length;
         this._pos = this._pos - this._run_length;
         if (this._pos < this._start) {
            int diff = this._start - this._pos;
            this._pos = this._start;
            this._run_length -= diff;
            this._total_length -= diff;
            return true;
         } else {
            this._total_length = this._total_length + this._run_length;
            return true;
         }
      }
   }

   public long getGlobalAttrib() {
      return this.this$0._global_attrib;
   }
}
