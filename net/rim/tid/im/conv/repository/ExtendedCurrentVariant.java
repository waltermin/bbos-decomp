package net.rim.tid.im.conv.repository;

import net.rim.tid.im.conv.SLCurrentVariant;

public class ExtendedCurrentVariant extends SLCurrentVariant {
   public int _frequency;
   private byte _validWord;
   public byte _distance;
   public byte _subDistance;
   public byte _source;
   public int _compoundRelationsMatrix;
   public int _lastCompoundLen;
   public Object _userObject;
   private static final byte VW_WORD;
   private static final byte VW_PREFIX;
   private static final byte VW_UNSET;

   public ExtendedCurrentVariant() {
   }

   public ExtendedCurrentVariant(int size, char[] buffer, int start, int len) {
   }

   public ExtendedCurrentVariant(int size) {
   }

   public void setData(char[] aVariantBuffer, int aOffset, int aLength, int aFreq) {
      super._variants = aVariantBuffer;
      super._offset = aOffset;
      super._length = aLength;
      this._frequency = aFreq;
      this._validWord = 2;
      this._userObject = null;
      this._compoundRelationsMatrix = 0;
      this._lastCompoundLen = 0;
   }

   public void setData(SLCurrentVariant aVariant, int aFreq) {
      this.setData(aVariant._variants, aVariant._offset, aVariant._length, aFreq);
   }

   public void append(SLCurrentVariant aVariant) {
      this.insureCapacity(super._length + aVariant._length);
      System.arraycopy(aVariant._variants, aVariant._offset, super._variants, super._length, aVariant._length);
      super._length = super._length + aVariant._length;
   }

   public final void append(ExtendedCurrentVariant aVariant, boolean useCharDataOnly) {
      this.append(aVariant);
      if (!useCharDataOnly) {
         this._frequency = aVariant._frequency;
         this._distance = aVariant._distance;
         this._userObject = aVariant._userObject;
         this._source = aVariant._source;
         this._validWord = aVariant._validWord;
      }
   }

   public void setValidWord(boolean aIsValid) {
      this._validWord = (byte)(aIsValid ? 0 : 1);
   }

   public void setValidWordIfNeeded(boolean aIsValid) {
      if (this._validWord == 2) {
         this._validWord = (byte)(aIsValid ? 0 : 1);
      }
   }

   public boolean isValidWord() {
      return this._validWord != 1;
   }

   @Override
   public boolean runSecureClean() {
      this._userObject = null;
      return super.runSecureClean();
   }
}
