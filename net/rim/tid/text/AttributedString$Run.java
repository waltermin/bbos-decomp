package net.rim.tid.text;

final class AttributedString$Run {
   long _attrib;
   long _xAttrib;
   AttributedString$Run _prev;
   AttributedString$Run _next;
   int _length;
   AttributedString$PictureInfo _pictureInfo;

   AttributedString$Run() {
   }

   AttributedString$Run(
      AttributedString$Run aPrev, AttributedString$Run aNext, int aLength, long aAttrib, long aXAttrib, AttributedString$PictureInfo aPictureInfo
   ) {
      this._prev = aPrev;
      this._next = aNext;
      this._length = aLength;
      this._attrib = aAttrib;
      this._xAttrib = aXAttrib;
      this._pictureInfo = aPictureInfo;
   }
}
