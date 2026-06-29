package net.rim.device.internal.browser.markup;

final class MarkupContext {
   public byte[] _state = new byte[0];
   public MarkupDataContext _mainContext = new MarkupDataContext();
   public int[] _tagStack = new int[0];
   public int[] _actionStateTable;
   public byte[] _configurationData;
   public String[] _tags;
   public int[] _tagIds;
   public int[] _tagIndicies;
   public String[] _entities;
   public int[] _entityIds;
   public int[] _entityIndicies;
   public String[] _attributes;
   public int[] _attributeIds;
   public int[] _attributeIndicies;
   public String[] _attributeValues;
   public int[] _attributeValueIds;
   public int[] _attributeValueIndicies;
   public byte[] _patternToConsume = new byte[0];
   public int _currentCosumeIndex;
   public int _mainIndex = -1;
   public int _numStringRefs = -1;
   public int _compressWhitespace = 1;
   public boolean _firstBlock = true;
   public boolean _lastBlock;
   public int _currentState;
   public int _inputEncoding;
   public boolean _guessEncoding;
   public byte[] _newEncodingType;
   public byte[] _consumedData = new byte[0];
   public int _currentConsumedDataIndex;
   public int _currentConsumedScriptState;
   public int _currentPIndex = -1;
   public int _currentEndReadPosition;
   public boolean _currentWhitespaceAppend;
   public int _currentTagCount;

   public final void reset() {
      this._state = new byte[0];
      this._mainContext = new MarkupDataContext();
      this._tagStack = new int[0];
      this._patternToConsume = new byte[0];
      this._consumedData = new byte[0];
      this._currentConsumedDataIndex = 0;
      this._currentCosumeIndex = 0;
      this._mainIndex = -1;
      this._numStringRefs = -1;
      this._compressWhitespace = 1;
      this._firstBlock = true;
      this._lastBlock = false;
      this._currentState = 0;
      this._inputEncoding = 0;
      this._guessEncoding = false;
      this._newEncodingType = null;
      this._currentConsumedScriptState = 0;
      this._currentPIndex = -1;
      this._currentEndReadPosition = 0;
      this._currentWhitespaceAppend = false;
      this._currentTagCount = 0;
   }
}
