package net.rim.tid.im.conv.repository;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.BitSet;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.util.algorithm.LongVector;
import net.rim.tid.util.Utils;
import net.rim.vm.Array;

public class ReIterator implements PersistentContentListener {
   protected char[] iRe = new char[15];
   protected int iReLength;
   protected int iCurrentIndex = 2;
   protected boolean iInWildcard;
   protected int iCurrentWildcardId;
   protected boolean iWildcardExausted;
   protected int iLength = -1;
   protected int iMaxStackSize;
   protected byte[] iCurrentIndexStack;
   protected boolean[] iInWildcardStack;
   protected byte[] iCurrentWildcardIdStack;
   protected byte[] iCurrentWildcardIndexStack;
   protected boolean[] iWildcardExaustedStack;
   protected int iStackIndex;
   protected int iWildcardNo;
   protected byte[] iWildcardStart;
   protected byte[] iWildcardLen;
   protected byte[] iWildcardIndex;
   protected byte[] iCurrentWildcardIndex;
   protected boolean[] iWildcardIsRange;
   protected boolean iCaseSensitive;
   protected boolean iHasWildcards;
   protected boolean iStartFromThird;
   private BitSet _foundSubst;
   public static final char ASTERISK = '\u0001';
   public static final char OPEN_BRACKET = '\u0002';
   public static final char CLOSED_BRACKET = '\u0003';
   public static final char DOT = '\u0004';
   private static final int MAX_WILDCARD_NO = 10;
   private static final int INC = 10;

   public ReIterator(int aMaxStackSize) {
      this.iMaxStackSize = aMaxStackSize;
      this.iCurrentIndexStack = new byte[this.iMaxStackSize];
      PersistentContent.addWeakListener(this);
   }

   public void init(String aRe) {
      this.init(aRe, true);
   }

   public void init(String aRe, boolean aStartFromThird) {
      this.iReLength = aRe.length();
      if (this.iReLength > this.iRe.length) {
         this.iRe = new char[this.iReLength];
      }

      aRe.getChars(0, this.iReLength, this.iRe, 0);
      this.initWildcards(aStartFromThird, null);
   }

   public void init(char[] aRe, int aOffset, int aLen) {
      this.init(aRe, aOffset, aLen, true);
   }

   public void init(SLCurrentVariant aRe) {
      this.init(aRe._variants, aRe._offset, aRe._length, true);
   }

   public void init(char[] aRe, int aOffset, int aLen, boolean aStartFromThird) {
      this.iReLength = aLen;
      if (this.iReLength > this.iRe.length) {
         this.iRe = new char[this.iReLength];
      }

      System.arraycopy(aRe, aOffset, this.iRe, 0, this.iReLength);
      this.initWildcards(aStartFromThird, null);
   }

   public void addIterData(char[] aRe, int aOffset, int aLen) {
      if (this.iReLength + aLen > this.iRe.length) {
         Array.resize(this.iRe, this.iReLength + aLen + 10);
      }

      System.arraycopy(aRe, aOffset, this.iRe, this.iReLength, aLen);
      this.iReLength += aLen;
   }

   public void addIterData(char ch) {
      if (this.iReLength == this.iRe.length) {
         Array.resize(this.iRe, this.iReLength + 10);
      }

      this.iRe[this.iReLength++] = ch;
   }

   public void initWildcards(boolean aStartFromThird, BitSet foundSubst) {
      this.iWildcardNo = 0;
      this.iCurrentIndex = aStartFromThird ? 2 : 0;
      this.iCurrentWildcardId = -1;
      this.iInWildcard = false;
      this.iWildcardExausted = false;
      this.iStackIndex = 0;
      int id = this.indexOf('\u0002');
      if (id == -1) {
         this.iLength = this.iReLength;
      } else {
         if (this.iInWildcardStack == null) {
            this.iInWildcardStack = new boolean[this.iMaxStackSize];
            this.iCurrentWildcardIdStack = new byte[this.iMaxStackSize];
            this.iCurrentWildcardIndexStack = new byte[this.iMaxStackSize];
            this.iWildcardExaustedStack = new boolean[this.iMaxStackSize];
         }

         if (this.iWildcardStart == null) {
            this.iWildcardStart = new byte[10];
            this.iWildcardLen = new byte[10];
            this.iWildcardIndex = new byte[10];
            this.iCurrentWildcardIndex = new byte[10];
            this.iWildcardIsRange = new boolean[10];
         }

         this.checkWildcard();
         int length = 0;
         boolean in_re = false;
         char last_wildcard_char = 0;

         for (int i = 0; i < this.iReLength; i++) {
            char ch = this.iRe[i];
            if (in_re) {
               if (ch == 3) {
                  this.iWildcardLen[this.iWildcardNo] = (byte)(i - this.iWildcardStart[this.iWildcardNo]);
                  this.iWildcardNo++;
                  in_re = false;
               } else {
                  if (last_wildcard_char != 0 && last_wildcard_char + 1 != ch) {
                     this.iWildcardIsRange[this.iWildcardNo] = false;
                  }

                  last_wildcard_char = ch;
               }
            } else {
               if (ch == 2) {
                  in_re = true;
                  this.iWildcardStart[this.iWildcardNo] = (byte)(i + 1);
                  this.iWildcardIndex[this.iWildcardNo] = (byte)length;
                  this.iWildcardIsRange[this.iWildcardNo] = true;
                  this.iCurrentWildcardIndex[this.iWildcardNo] = 0;
                  last_wildcard_char = 0;
               }

               length++;
            }
         }

         this.iLength = length;
      }

      this.iHasWildcards = this.iWildcardNo > 0 || this.indexOf('\u0004') != -1;
      this.iStartFromThird = aStartFromThird;
      this._foundSubst = foundSubst;
   }

   private void checkWildcard() {
      this.iInWildcard = false;
      this.iWildcardExausted = false;
      if (this.iCurrentIndex < this.iReLength && this.iRe[this.iCurrentIndex] == 2) {
         this.iInWildcard = true;
         this.iCurrentWildcardId++;
         this.iCurrentIndex++;
         this.iCurrentWildcardIndex[this.iCurrentWildcardId] = 0;
      }
   }

   public void setCaseSensitive(boolean value) {
      this.iCaseSensitive = value;
   }

   public boolean isCaseSensitive() {
      return this.iCaseSensitive;
   }

   private int indexOf(char aCh) {
      for (int i = 0; i < this.iReLength; i++) {
         if (this.iRe[i] == aCh) {
            return i;
         }
      }

      return -1;
   }

   public int getLength() {
      return this.iLength;
   }

   public int getReLength() {
      return this.iReLength;
   }

   public char[] getRe() {
      return this.iRe;
   }

   public int getWildcardSubstNo() {
      int no = 1;

      for (int i = 0; i < this.iWildcardNo; i++) {
         no *= this.iWildcardLen[i];
      }

      return no;
   }

   public int getCurrentWildcardSubstIndex() {
      int id = 0;
      int factor = 1;

      for (int i = this.iCurrentWildcardId; i >= 0; i--) {
         id += this.iCurrentWildcardIndex[i] * factor;
         factor *= this.iWildcardLen[i];
      }

      return id;
   }

   public void nextWildcard() {
      if (this.iInWildcard) {
         for (int i = this.iCurrentIndex; i < this.iReLength; i++) {
            if (this.iRe[i] == 3) {
               this.iCurrentIndex = i + 1;
               break;
            }
         }
      } else {
         this.iCurrentIndex++;
      }

      this.checkWildcard();
   }

   public final void skipWildcards(int toSkip) {
      for (int i = 0; i < toSkip; i++) {
         this.nextWildcard();
      }
   }

   public char nextChar() {
      if (this.iWildcardExausted) {
         return '\u0000';
      }

      char ch = this.iRe[this.iCurrentIndex];
      if (this.iInWildcard) {
         if (ch == 3) {
            this.iWildcardExausted = true;
            return '\u0000';
         } else {
            this.iCurrentWildcardIndex[this.iCurrentWildcardId] = (byte)(this.iCurrentIndex - this.iWildcardStart[this.iCurrentWildcardId]);
            this.iCurrentIndex++;
            return ch;
         }
      } else {
         this.iWildcardExausted = true;
         return ch;
      }
   }

   public boolean accept(char aCh) {
      if (!this.iCaseSensitive) {
         aCh = Utils.toLowerCase(aCh);
      }

      if (this.iInWildcard) {
         if (this.iWildcardIsRange[this.iCurrentWildcardId]) {
            char ch1 = this.iRe[this.iCurrentIndex];
            if (aCh >= ch1 && aCh < ch1 + this.iWildcardLen[this.iCurrentWildcardId]) {
               this.iCurrentWildcardIndex[this.iCurrentWildcardId] = (byte)(aCh - ch1);
               return true;
            } else {
               return false;
            }
         } else {
            int i = this.iCurrentIndex;

            for (char ch = this.iRe[i++]; ch != 3; ch = this.iRe[i++]) {
               if (ch == aCh) {
                  this.iCurrentWildcardIndex[this.iCurrentWildcardId] = (byte)(i - 1 - this.iCurrentIndex);
                  return true;
               }
            }

            return false;
         }
      } else {
         char ch = this.iRe[this.iCurrentIndex];
         return ch == 4 || ch == aCh;
      }
   }

   public boolean accept(String aString) {
      return this.accept(aString, 0, aString.length());
   }

   public boolean accept(String aString, int aStart, int aEnd) {
      for (int i = aStart; i < aEnd; i++) {
         if (this.iCurrentIndex >= this.iReLength) {
            return true;
         }

         char ch = aString.charAt(i);
         if (!this.accept(ch)) {
            return false;
         }

         this.nextWildcard();
      }

      return true;
   }

   public boolean accept(char[] aString, int aStart, int aEnd) {
      for (int i = aStart; i < aEnd; i++) {
         if (this.iCurrentIndex >= this.iReLength) {
            return true;
         }

         char ch = aString[i];
         if (!this.accept(ch)) {
            return false;
         }

         this.nextWildcard();
      }

      return true;
   }

   public void pushState() {
      this.iCurrentIndexStack[this.iStackIndex] = (byte)this.iCurrentIndex;
      if (this.iWildcardNo > 0) {
         this.iInWildcardStack[this.iStackIndex] = this.iInWildcard;
         this.iCurrentWildcardIdStack[this.iStackIndex] = (byte)this.iCurrentWildcardId;
         this.iWildcardExaustedStack[this.iStackIndex] = this.iWildcardExausted;
         if (this.iCurrentWildcardId != -1) {
            this.iCurrentWildcardIndexStack[this.iStackIndex] = this.iCurrentWildcardIndex[this.iCurrentWildcardId];
         }
      }

      this.iStackIndex++;
   }

   public void popState() {
      this.iStackIndex--;
      this.iCurrentIndex = this.iCurrentIndexStack[this.iStackIndex];
      if (this.iWildcardNo > 0) {
         this.iInWildcard = this.iInWildcardStack[this.iStackIndex];
         this.iCurrentWildcardId = this.iCurrentWildcardIdStack[this.iStackIndex];
         this.iWildcardExausted = this.iWildcardExaustedStack[this.iStackIndex];
         if (this.iCurrentWildcardId != -1) {
            this.iCurrentWildcardIndex[this.iCurrentWildcardId] = this.iCurrentWildcardIndexStack[this.iStackIndex];
            return;
         }
      } else {
         this.iWildcardExausted = true;
      }
   }

   public boolean hasMoreSubst() {
      return !this.iWildcardExausted;
   }

   public void nextSubst() {
      if (this.iWildcardNo != 0 && this.iCurrentWildcardIndex[0] != this.iWildcardLen[0]) {
         int wildcard_id = this.iWildcardNo - 1;
         this.iCurrentWildcardIndex[wildcard_id]++;

         while (this.iCurrentWildcardIndex[wildcard_id] == this.iWildcardLen[wildcard_id]) {
            if (wildcard_id == 0) {
               this.iWildcardExausted = true;
               return;
            }

            this.iCurrentWildcardIndex[wildcard_id--] = 0;
            this.iCurrentWildcardIndex[wildcard_id]++;
         }
      } else {
         this.iWildcardExausted = true;
      }
   }

   public void getChars(char[] aBuffer) {
      int wildcard_id = 0;
      int regexp_id = 0;

      for (int i = 0; i < this.iLength; i++) {
         if (wildcard_id < this.iWildcardNo && i == this.iWildcardIndex[wildcard_id]) {
            aBuffer[i] = this.iRe[regexp_id + 1 + this.iCurrentWildcardIndex[wildcard_id]];
            regexp_id += 2 + this.iWildcardLen[wildcard_id++];
         } else {
            aBuffer[i] = this.iRe[regexp_id++];
         }
      }
   }

   public void getSepPointsForCurrentSubst(long[] rawData, int prefOffset, LongVector result) {
      result.setSize(0);
      int wildcard_id = 0;
      int regexp_id = 0;

      for (int i = 0; i < this.iLength; i++) {
         long ch;
         if (wildcard_id < this.iWildcardNo && i == this.iWildcardIndex[wildcard_id]) {
            ch = rawData[regexp_id + 1 + this.iCurrentWildcardIndex[wildcard_id] + prefOffset];
            regexp_id += 2 + this.iWildcardLen[wildcard_id++];
         } else {
            ch = rawData[regexp_id + prefOffset];
            regexp_id++;
         }

         result.addElement(ch);
      }
   }

   public boolean hasWildcards() {
      return this.iHasWildcards;
   }

   public boolean hasQuantifiers() {
      return false;
   }

   public void clear() {
      this.iReLength = 0;
      this.iCurrentIndex = -1;
      this.iCurrentWildcardId = -1;
      this.iInWildcard = false;
      this.iWildcardExausted = false;
      this.iStackIndex = 0;
   }

   public void reset() {
      this.reset(this.iStartFromThird);
   }

   public void reset(boolean aStartFromThird) {
      if (this.iStartFromThird != aStartFromThird) {
         this.initWildcards(aStartFromThird, this._foundSubst);
      } else {
         this.iCurrentIndex = this.iStartFromThird ? 2 : 0;
         this.iCurrentWildcardId = -1;
         this.iInWildcard = false;
         this.iWildcardExausted = false;
         this.iStackIndex = 0;
         this.checkWildcard();
      }
   }

   public void setMaxStackSize(int aMaxStackSize) {
      if (this.iMaxStackSize != aMaxStackSize) {
         this.iMaxStackSize = aMaxStackSize;
         this.iCurrentIndexStack = new byte[this.iMaxStackSize];
         this.iInWildcardStack = new boolean[this.iMaxStackSize];
         this.iCurrentWildcardIdStack = new byte[this.iMaxStackSize];
         this.iCurrentWildcardIndexStack = new byte[this.iMaxStackSize];
         this.iWildcardExaustedStack = new boolean[this.iMaxStackSize];
      }
   }

   public int getMaxStackSize() {
      return this.iMaxStackSize;
   }

   public int getNonREPrefixLength() {
      int i;
      for (i = 0; i < this.iReLength && this.iRe[i] != 4; i++) {
         if (this.iRe[i] == 2) {
            return i;
         }

         if (this.iRe[i] == 1) {
            return i - 1;
         }
      }

      return i;
   }

   public void setWildcardSubstFound() {
      if (this._foundSubst != null) {
         int indx = this.getCurrentWildcardSubstIndex();
         if (indx == Integer.MAX_VALUE) {
            throw new Object();
         }

         this._foundSubst.set(indx);
      }
   }

   @Override
   public void persistentContentStateChanged(int state) {
      if (state == 2) {
         if (this.iRe != null) {
            Arrays.fill(this.iRe, ' ');
         }

         this.iReLength = 0;
         this.iLength = 0;
      }
   }

   @Override
   public void persistentContentModeChanged(int generation) {
   }
}
