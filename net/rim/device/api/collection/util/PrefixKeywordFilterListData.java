package net.rim.device.api.collection.util;

import net.rim.device.api.util.Persistable;

public final class PrefixKeywordFilterListData implements KeywordFilterListData, Persistable {
   SparseList _objectList;
   KeywordPrefixManager _prefixList;
   BigIntVector _orderList;
   boolean _firstWordBias;
   int _version;

   PrefixKeywordFilterListData(SparseList objectList, KeywordPrefixManager prefixList, BigIntVector orderList, boolean firstWordBias, int version) {
      this._objectList = objectList;
      this._prefixList = prefixList;
      this._orderList = orderList;
      this._firstWordBias = firstWordBias;
      this._version = version;
   }
}
