package net.rim.device.apps.internal.browser.html;

class ResizableTextArray {
   private byte[] _array;
   private int _sectionSize;
   private int _firstFreeIndex = 0;
   private char[] _expandedText;
   private int _expandedTextSectionSize;
   private int _expandedTextLength;

   ResizableTextArray() {
      this._array = new byte[0];
      this._sectionSize = ArrayResize.getSectionSize(this._array);
      this._expandedText = new char[0];
      this._expandedTextSectionSize = ArrayResize.getSectionSize(this._expandedText);
   }

   void shrink() {
      this._array = ArrayResize.byteArrayResize(this._array, ArrayResize.roundToSectionSize(this._firstFreeIndex, this._sectionSize));
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   void expandText(int handle, int length) {
      int i;
      label47:
      for (i = 0; i < length; i++) {
         int b = this._array[handle];
         handle++;
         char ch;
         switch (b & 192) {
            case 128:
               ch = (char)(((b & 63) << 8) + (this._array[handle] & 255));
               handle++;
               break;
            case 192:
               int c1 = (this._array[handle] & 255) << 8;
               ch = (char)(c1 + (this._array[++handle] & 255));
               handle++;
               break;
            default:
               if (b == 0) {
                  break label47;
               }
            case 64:
               ch = (char)(b & 0xFF);
         }

         boolean var8 = false /* VF: Semaphore variable */;

         try {
            var8 = true;
            this._expandedText[i] = ch;
            var8 = false;
         } finally {
            if (var8) {
               this._expandedText = ArrayResize.charArrayResize(
                  this._expandedText, ArrayResize.roundToSectionSize(i + 1, this._expandedTextSectionSize) + this._expandedTextSectionSize
               );
               this._expandedText[i] = ch;
               continue;
            }
         }
      }

      this._expandedTextLength = i;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void appendByte(int b) {
      boolean var4 = false /* VF: Semaphore variable */;

      label17:
      try {
         var4 = true;
         this._array[this._firstFreeIndex] = (byte)b;
         var4 = false;
      } finally {
         if (var4) {
            this._array = ArrayResize.byteArrayResize(this._array, ArrayResize.roundToSectionSize(this._firstFreeIndex, this._sectionSize) + this._sectionSize);
            this._array[this._firstFreeIndex] = (byte)b;
            break label17;
         }
      }

      this._firstFreeIndex++;
   }

   void append(int ch) {
      if (ch <= 127) {
         this.appendByte(ch);
      } else if (ch <= 16383) {
         this.appendByte(128 + (ch >> 8));
         this.appendByte(ch & 0xFF);
      } else {
         this.appendByte(255);
         this.appendByte(ch >> 8);
         this.appendByte(ch & 0xFF);
      }
   }

   void append(String str) {
      int length = str.length();

      for (int i = 0; i < length; i++) {
         this.append(str.charAt(i));
      }
   }

   void appendWithNull(String str) {
      this.append(str);
      this.append(0);
   }

   void append(char[] buffer, int start, int length) {
      int end = start + length;

      for (int i = start; i < end; i++) {
         this.append(buffer[i]);
      }
   }

   int splitHandle(int handle, int splitIndex) {
      return handle + splitIndex;
   }

   int newNode() {
      return this._firstFreeIndex;
   }

   String makeString(int handle) {
      this.expandText(handle, Integer.MAX_VALUE);
      return (String)(new Object(this._expandedText, 0, this._expandedTextLength));
   }

   String makeString(int handle, int length) {
      this.expandText(handle, length);
      return (String)(new Object(this._expandedText, 0, length));
   }

   String makeSubstring(int handle, int offset, int count) {
      this.expandText(handle, count + offset);
      return (String)(new Object(this._expandedText, offset, count));
   }

   char[] getExpandedTextArray() {
      return this._expandedText;
   }
}
