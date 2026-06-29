package net.rim.device.api.collection.util;

import net.rim.device.api.util.BitSet;

public class KeywordPrefixSearchResult {
   private BitSet _primarySet;
   private BitSet _secondarySet;

   public KeywordPrefixSearchResult(BitSet primarySet, BitSet secondarySet) {
      this.setResults(primarySet, secondarySet);
   }

   public BitSet getPrimaryMatches() {
      return this._primarySet;
   }

   public BitSet getSecondaryMatches() {
      return this._secondarySet;
   }

   public final void setResults(BitSet primarySet, BitSet secondarySet) {
      this._primarySet = primarySet;
      this._secondarySet = secondarySet;
   }

   public int getMatchCount() {
      return this._primarySet.getNumSet() + this._secondarySet.getNumSet();
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof KeywordPrefixSearchResult)) {
         return false;
      }

      KeywordPrefixSearchResult p = (KeywordPrefixSearchResult)obj;
      return this._primarySet.equals(p._primarySet) && this._secondarySet.equals(p._secondarySet);
   }
}
