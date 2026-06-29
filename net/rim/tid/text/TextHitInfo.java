package net.rim.tid.text;

public final class TextHitInfo {
   public int _index;
   public boolean _leadingEdge;
   private static TextHitInfo _tempHitInfo = new TextHitInfo();

   public TextHitInfo() {
   }

   public TextHitInfo(int aIndex, boolean aLeadingEdge) {
      this._index = aIndex;
      this._leadingEdge = aLeadingEdge;
   }

   public final void set(int aIndex, boolean aLeadingEdge) {
      this._index = aIndex;
      this._leadingEdge = aLeadingEdge;
   }

   public final boolean less(TextHitInfo aInfo) {
      return this._index < aInfo._index || this._index == aInfo._index && !this._leadingEdge && aInfo._leadingEdge;
   }

   public final Object clone() {
      return new TextHitInfo(this._index, this._leadingEdge);
   }

   public final boolean equals(TextHitInfo aInfo) {
      return this._index == aInfo._index && this._leadingEdge == aInfo._leadingEdge;
   }

   public final int getIndex() {
      return this._index;
   }

   public final boolean getLeadingEdge() {
      return this._leadingEdge;
   }

   public static final TextHitInfo leading(int aIndex) {
      _tempHitInfo._index = aIndex;
      _tempHitInfo._leadingEdge = true;
      return _tempHitInfo;
   }

   public static final TextHitInfo trailing(int aIndex) {
      _tempHitInfo._index = aIndex;
      _tempHitInfo._leadingEdge = false;
      return _tempHitInfo;
   }

   @Override
   public final String toString() {
      return this._leadingEdge ? "TextHitInfo: Index = " + this._index + ", leading." : "TextHitInfo: Index = " + this._index + ", trailing.";
   }
}
