package net.rim.tid.im.conv;

import net.rim.tid.util.SLTextDataContainer;
import net.rim.vm.Array;

class SLVariants$SLTextWithSourceDataContainer extends SLTextDataContainer {
   int[] _sources;

   @Override
   public void init(char[] words, byte[] length, int count) {
      this.init(words, length, new int[count], count);
   }

   public void init(char[] words, byte[] length, int[] sources, int count) {
      super.init(words, length, count);
      this._sources = sources;
   }

   @Override
   public boolean addVariant(SLCurrentVariant aVariant) {
      boolean result = super.addVariant(aVariant);
      if (this._sources != null) {
         Array.resize(this._sources, super._count);
      }

      return result;
   }

   @Override
   public boolean addVariant(StringBuffer aVariant) {
      boolean result = super.addVariant(aVariant);
      if (this._sources != null) {
         Array.resize(this._sources, super._count);
      }

      return result;
   }

   @Override
   public void addVariants(SLTextDataContainer aText) {
      super.addVariants(aText);
      if (this._sources != null) {
         Array.resize(this._sources, super._count);
      }
   }

   public long getCurrentAttribute() {
      return this._sources != null ? this._sources[super._index] : -1;
   }
}
