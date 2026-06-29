package net.rim.device.api.collection.util;

import net.rim.device.api.util.BitSet;

public class PatriciaKeywordSearchResult {
   public PatriciaKeywordFilterList _list;
   public byte[] _hitCount;
   public int _wordNumber;
   public BitSet _primarySet;
   public BitSet _theSet;

   public PatriciaKeywordSearchResult(PatriciaKeywordFilterList list, byte[] hitCount, BitSet primarySet) {
      this._list = list;
      this._hitCount = hitCount;
      this._primarySet = primarySet;
   }
}
