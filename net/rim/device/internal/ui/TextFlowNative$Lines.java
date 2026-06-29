package net.rim.device.internal.ui;

import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public final class TextFlowNative$Lines {
   private int _count = 0;
   private short[] _length = new short[0];
   private short[] _xOffset = new short[0];
   private short[] _width = new short[0];
   private short[] _widthNominal = new short[0];
   private byte[] _flags = new byte[0];
   private short[] _baseline = new short[0];
   private short[] _height = new short[0];
   private short[] _cellId = new short[0];
   private short[] _bidiStateIndex = new short[0];
   private byte[] _bidiState = new byte[0];
   private int _bidiStateCount = 0;
   public static final int KParStartFlag;
   public static final int KParEndFlag;
   public static final int KForcedStartFlag;
   public static final int KForcedEndFlag;
   public static final int KRightToLeftFlag;
   public static final int KNeedsBidiReorderingFlag;

   public final void reset() {
      this._count = 0;
      this._bidiStateCount = 0;
   }

   private final void grow(int aNewCount, int aNewBidiStateCount) {
      if (this._length.length < aNewCount) {
         int old_size = this._length.length;
         int new_size = Math.max(aNewCount, this._length.length + Array.getSectionSize(this._length));
         Array.resize(this._length, new_size);
         Array.resize(this._xOffset, new_size);
         Array.resize(this._width, new_size);
         Array.resize(this._widthNominal, new_size);
         Array.resize(this._flags, new_size);
         Array.resize(this._baseline, new_size);
         Array.resize(this._height, new_size);
         Array.resize(this._cellId, new_size);
         Arrays.fill(this._cellId, (short)0, old_size, new_size - old_size);
         Array.resize(this._bidiStateIndex, new_size);
      }

      this._count = aNewCount;
      if (this._bidiState.length < aNewBidiStateCount) {
         int new_size = Math.max(aNewBidiStateCount, this._bidiState.length + Array.getSectionSize(this._bidiState));
         Array.resize(this._bidiState, new_size);
      }

      this._bidiStateCount = aNewBidiStateCount;
   }

   public final void append(TextFlowNative$Lines aSourceLines, int aSourceStart, int aSourceCount) {
      this.replace(this._count, 0, aSourceLines, aSourceStart, aSourceCount);
   }

   public final void append(TextFlowNative$Lines aSourceLines) {
      this.replace(this._count, 0, aSourceLines, 0, aSourceLines._count);
   }

   public final void replace(int aDestStart, int aDestCount, TextFlowNative$Lines aSourceLines) {
      this.replace(aDestStart, aDestCount, aSourceLines, 0, aSourceLines._count);
   }

   public final void replace(int aDestStart, int aDestCount, TextFlowNative$Lines aSourceLines, int aSourceStart, int aSourceCount) {
      if (aDestStart < 0 || aDestCount < 0 || aDestStart + aDestCount > this._count) {
         throw new IllegalArgumentException();
      }

      if (aSourceStart >= 0 && aSourceCount >= 0 && aSourceStart + aSourceCount <= aSourceLines._count) {
         int old_count = this._count;
         int new_count = this._count + aSourceCount - aDestCount;
         int old_bidi_state_count = this._bidiStateCount;
         int dest_bidi_state_start = aDestStart < this._count ? this._bidiStateIndex[aDestStart] : this._bidiStateCount;
         int dest_bidi_state_end = aDestStart + aDestCount < this._count ? this._bidiStateIndex[aDestStart + aDestCount] : this._bidiStateCount;
         int dest_bidi_state_count = dest_bidi_state_end - dest_bidi_state_start;
         int source_bidi_state_start = aSourceStart < aSourceLines._count ? aSourceLines._bidiStateIndex[aSourceStart] : aSourceLines._bidiStateCount;
         int source_bidi_state_end = aSourceStart + aSourceCount < aSourceLines._count
            ? aSourceLines._bidiStateIndex[aSourceStart + aSourceCount]
            : aSourceLines._bidiStateCount;
         int source_bidi_state_count = source_bidi_state_end - source_bidi_state_start;
         int new_bidi_state_count = this._bidiStateCount + source_bidi_state_count - dest_bidi_state_count;
         if (new_count != old_count || new_bidi_state_count != old_bidi_state_count) {
            this.grow(new_count, new_bidi_state_count);
            int from = aDestStart + aDestCount;
            int to = aDestStart + aSourceCount;
            int n = old_count - from;
            if (n > 0) {
               System.arraycopy(this._length, from, this._length, to, n);
               System.arraycopy(this._xOffset, from, this._xOffset, to, n);
               System.arraycopy(this._width, from, this._width, to, n);
               System.arraycopy(this._widthNominal, from, this._widthNominal, to, n);
               System.arraycopy(this._flags, from, this._flags, to, n);
               System.arraycopy(this._baseline, from, this._baseline, to, n);
               System.arraycopy(this._height, from, this._height, to, n);
               System.arraycopy(this._cellId, from, this._cellId, to, n);
               System.arraycopy(this._bidiStateIndex, from, this._bidiStateIndex, to, n);
            }

            from = dest_bidi_state_start + dest_bidi_state_count;
            to = dest_bidi_state_start + source_bidi_state_count;
            n = old_bidi_state_count - from;
            if (n > 0) {
               System.arraycopy(this._bidiState, from, this._bidiState, to, n);
               int diff = to - from;

               for (int i = aDestStart + aSourceCount; i < new_count; i++) {
                  this._bidiStateIndex[i] = (short)(this._bidiStateIndex[i] + diff);
               }
            }
         }

         if (aSourceCount > 0) {
            System.arraycopy(aSourceLines._length, aSourceStart, this._length, aDestStart, aSourceCount);
            System.arraycopy(aSourceLines._xOffset, aSourceStart, this._xOffset, aDestStart, aSourceCount);
            System.arraycopy(aSourceLines._width, aSourceStart, this._width, aDestStart, aSourceCount);
            System.arraycopy(aSourceLines._widthNominal, aSourceStart, this._widthNominal, aDestStart, aSourceCount);
            System.arraycopy(aSourceLines._flags, aSourceStart, this._flags, aDestStart, aSourceCount);
            System.arraycopy(aSourceLines._baseline, aSourceStart, this._baseline, aDestStart, aSourceCount);
            System.arraycopy(aSourceLines._height, aSourceStart, this._height, aDestStart, aSourceCount);
            System.arraycopy(aSourceLines._cellId, aSourceStart, this._cellId, aDestStart, aSourceCount);
            System.arraycopy(aSourceLines._bidiStateIndex, aSourceStart, this._bidiStateIndex, aDestStart, aSourceCount);
         }

         if (source_bidi_state_count > 0) {
            System.arraycopy(aSourceLines._bidiState, source_bidi_state_start, this._bidiState, dest_bidi_state_start, source_bidi_state_count);
         }

         int diff = dest_bidi_state_start - source_bidi_state_start;
         int end = aDestStart + aSourceCount;

         for (int i = aDestStart; i < end; i++) {
            this._bidiStateIndex[i] = (short)(this._bidiStateIndex[i] + diff);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void appendVerticalSpace(int aHeight, short aXOffset, short aWidth, short aCellId) {
      int lines = (aHeight + 32767 - 1) / 32767;
      int old_count = this._count;
      this.grow(this._count + lines, this._bidiStateCount);

      for (int i = old_count; i < this._count; i++) {
         this._length[i] = 0;
         this._xOffset[i] = aXOffset;
         this._flags[i] = 3;
         this._width[i] = aWidth;
         this._widthNominal[i] = aWidth;
         this._baseline[i] = 0;
         this._height[i] = (short)Math.min(32767, aHeight);
         this._cellId[i] = aCellId;
         this._bidiStateIndex[i] = (short)this._bidiStateCount;
         aHeight -= 32767;
      }
   }

   public final void appendZeroHeightCharacters(int aChars, short aXOffset, short aWidth, short aCellId) {
      int lines = (aChars + 32767 - 1) / 32767;
      int old_count = this._count;
      this.grow(this._count + lines, this._bidiStateCount);

      for (int i = old_count; i < this._count; i++) {
         this._length[i] = (short)Math.min(32767, aChars);
         this._xOffset[i] = aXOffset;
         this._flags[i] = 3;
         this._width[i] = aWidth;
         this._widthNominal[i] = aWidth;
         this._baseline[i] = 0;
         this._height[i] = 0;
         this._cellId[i] = aCellId;
         this._bidiStateIndex[i] = (short)this._bidiStateCount;
         aChars -= 32767;
      }
   }

   public final void appendZeroHeightZeroWidthCharacter(short aXOffset, short aCellId) {
      int i = this._count;
      this.grow(this._count + 1, this._bidiStateCount);
      this._length[i] = 0;
      this._xOffset[i] = aXOffset;
      this._flags[i] = 3;
      this._width[i] = 0;
      this._widthNominal[i] = 0;
      this._baseline[i] = 0;
      this._height[i] = 0;
      this._cellId[i] = aCellId;
      this._bidiStateIndex[i] = (short)this._bidiStateCount;
   }

   public final int getCount() {
      return this._count;
   }

   public final short[] getLengths() {
      return this._length;
   }

   public final short[] getXOffsets() {
      return this._xOffset;
   }

   public final short[] getWidths() {
      return this._width;
   }

   public final short[] getWidthsNominal() {
      return this._widthNominal;
   }

   public final byte[] getFlags() {
      return this._flags;
   }

   public final short[] getBaselines() {
      return this._baseline;
   }

   public final short[] getHeights() {
      return this._height;
   }

   public final short[] getCellIds() {
      return this._cellId;
   }

   public final byte[] getBidiState(int aLineIndex) {
      int start = this._bidiStateIndex[aLineIndex];
      int length = (aLineIndex + 1 < this._count ? this._bidiStateIndex[aLineIndex + 1] : this._bidiStateCount) - start;
      if (length <= 0) {
         return null;
      }

      byte[] b = new byte[length];

      for (int i = 0; i < length; i++) {
         b[i] = this._bidiState[start + i];
      }

      return b;
   }
}
