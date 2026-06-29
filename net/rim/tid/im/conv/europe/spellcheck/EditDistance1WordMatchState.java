package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.tid.im.conv.europe.repository.RegularExpressionState;

public class EditDistance1WordMatchState implements RegularExpressionState {
   private EditDistance1WordMatch expr;
   int t1PathIndex;
   int deletePathIndex;
   int swap1PathIndex;
   int swap2PathIndex;
   int insertPathIndex;
   int replacePathIndex;
   int lastAccepted;

   public void set(EditDistance1WordMatch expr) {
      this.expr = expr;
      this.t1PathIndex = 0;
      this.deletePathIndex = -1;
      this.swap1PathIndex = -1;
      this.swap2PathIndex = -1;
      this.insertPathIndex = -1;
      this.replacePathIndex = -1;
      this.lastAccepted = -1;
   }

   @Override
   public Object newMark() {
      EditDistance1WordMatchState$CachedState state = new EditDistance1WordMatchState$CachedState();
      state.t1PathIndex = this.t1PathIndex;
      state.deletePathIndex = this.deletePathIndex;
      state.swap1PathIndex = this.swap1PathIndex;
      state.swap2PathIndex = this.swap2PathIndex;
      state.insertPathIndex = this.insertPathIndex;
      state.replacePathIndex = this.replacePathIndex;
      return state;
   }

   @Override
   public void rollback(Object obj) {
      EditDistance1WordMatchState$CachedState state = (EditDistance1WordMatchState$CachedState)obj;
      this.t1PathIndex = state.t1PathIndex;
      this.deletePathIndex = state.deletePathIndex;
      this.swap1PathIndex = state.swap1PathIndex;
      this.swap2PathIndex = state.swap2PathIndex;
      this.insertPathIndex = state.insertPathIndex;
      this.replacePathIndex = state.replacePathIndex;
   }

   @Override
   public long mark() {
      long mm = 0;
      if (this.t1PathIndex != -1) {
         mm |= this.t1PathIndex;
      } else {
         mm |= this.deletePathIndex == -1 ? 255 : 128 | this.deletePathIndex & 127;
         mm <<= 8;
         mm |= this.insertPathIndex == -1 ? 255 : this.insertPathIndex & 127;
         mm <<= 8;
         mm |= this.replacePathIndex == -1 ? 255 : this.replacePathIndex & 127;
         mm <<= 8;
         if (this.swap1PathIndex != -1) {
            mm |= 128;
            mm |= this.swap1PathIndex & 63;
         } else if (this.swap2PathIndex != -1) {
            mm |= this.swap2PathIndex & 63;
         } else {
            mm |= 255;
         }
      }

      return this.lastAccepted == -1 ? mm | 1095216660480L : mm | (long)(this.lastAccepted & 0xFF) << 32;
   }

   @Override
   public void rollback(long mark) {
      if ((mark & 2147483648L) == 0) {
         this.t1PathIndex = (int)mark;
         this.deletePathIndex = -1;
         this.swap1PathIndex = -1;
         this.swap2PathIndex = -1;
         this.insertPathIndex = -1;
         this.replacePathIndex = -1;
         mark >>= 32;
      } else {
         this.t1PathIndex = -1;
         long mm = mark & 255;
         if (mm == 255) {
            this.swap1PathIndex = -1;
            this.swap2PathIndex = -1;
         } else if ((mm & 128) != 0) {
            this.swap1PathIndex = (int)(mm & 63);
            this.swap2PathIndex = -1;
         } else {
            this.swap1PathIndex = -1;
            this.swap2PathIndex = (int)(mm & 63);
         }

         mark >>= 8;
         mm = mark & 255;
         this.replacePathIndex = mm == 255 ? -1 : (int)mm;
         mark >>= 8;
         mm = mark & 255;
         this.insertPathIndex = mm == 255 ? -1 : (int)mm;
         mark >>= 8;
         mm = mark & 255;
         this.deletePathIndex = mm == 255 ? -1 : (int)(mm & 127);
         mark >>= 8;
      }

      mark &= 255;
      this.lastAccepted = mark == 255 ? -1 : (int)mark;
   }

   @Override
   public boolean isFinal() {
      if (this.t1PathIndex != -1) {
         return this.expr.maxEditDistance == 0 ? this.t1PathIndex == this.expr.len : this.t1PathIndex == this.expr.len || this.t1PathIndex == this.expr.len - 1;
      } else {
         return this.swap1PathIndex == this.expr.len
            || this.swap2PathIndex == this.expr.len
            || this.replacePathIndex == this.expr.len
            || this.insertPathIndex == this.expr.len
            || this.deletePathIndex == this.expr.len;
      }
   }
}
