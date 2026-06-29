package net.rim.ecmascript.compiler;

class Tokenizer$StringToBinary {
   int _index;
   int _endIndex;
   int _logRadix;
   String _string;
   int _digit;
   int _digitMask;
   int _currentMask;

   Tokenizer$StringToBinary(String str, int start, int end, int radix) {
      this._string = str;
      this._index = start;
      this._endIndex = end;
      this._logRadix = 0;

      for (this._digitMask = 1; radix > 2; this._digitMask <<= 1) {
         radix >>= 1;
      }

      this._currentMask = 0;
   }

   int getBit() {
      if (this._currentMask == 0) {
         if (this._index >= this._endIndex) {
            return -1;
         }

         char ch = this._string.charAt(this._index++);
         if (ch >= '0' && ch <= '9') {
            this._digit = ch - '0';
         } else if (ch >= 'a' && ch <= 'z') {
            this._digit = ch - 'a' + 10;
         } else {
            this._digit = ch - 'A' + 10;
         }

         this._currentMask = this._digitMask;
      }

      int bit = (this._digit & this._currentMask) != 0 ? 1 : 0;
      this._currentMask >>= 1;
      return bit;
   }
}
