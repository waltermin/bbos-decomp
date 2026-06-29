package org.apache.oro.text.regexp;

public final class PatternMatcherInput {
   String _originalStringInput;
   char[] _originalCharInput;
   char[] _originalBuffer;
   char[] _toLowerBuffer;
   int _beginOffset;
   int _endOffset;
   int _currentOffset;
   int _matchBeginOffset = -1;
   int _matchEndOffset = -1;

   public PatternMatcherInput(String input, int begin, int length) {
      this.setInput(input, begin, length);
   }

   public PatternMatcherInput(String input) {
      this(input, 0, input.length());
   }

   public PatternMatcherInput(char[] input, int begin, int length) {
      this.setInput(input, begin, length);
   }

   public PatternMatcherInput(char[] input) {
      this(input, 0, input.length);
   }

   public final int length() {
      return this._endOffset - this._beginOffset;
   }

   public final void setInput(String input, int begin, int length) {
      this._originalStringInput = input;
      this._originalCharInput = null;
      this._toLowerBuffer = null;
      this._originalBuffer = input.toCharArray();
      this.setCurrentOffset(begin);
      this.setBeginOffset(begin);
      this.setEndOffset(this._beginOffset + length);
   }

   public final void setInput(String input) {
      this.setInput(input, 0, input.length());
   }

   public final void setInput(char[] input, int begin, int length) {
      this._originalStringInput = null;
      this._toLowerBuffer = null;
      this._originalBuffer = this._originalCharInput = input;
      this.setCurrentOffset(begin);
      this.setBeginOffset(begin);
      this.setEndOffset(this._beginOffset + length);
   }

   public final void setInput(char[] input) {
      this.setInput(input, 0, input.length);
   }

   public final char charAt(int offset) {
      return this._originalBuffer[this._beginOffset + offset];
   }

   public final String substring(int beginOffset, int endOffset) {
      return new String(this._originalBuffer, this._beginOffset + beginOffset, endOffset - beginOffset);
   }

   public final String substring(int beginOffset) {
      beginOffset += this._beginOffset;
      return new String(this._originalBuffer, beginOffset, this._endOffset - beginOffset);
   }

   public final Object getInput() {
      return this._originalStringInput == null ? this._originalCharInput : this._originalStringInput;
   }

   public final char[] getBuffer() {
      return this._originalBuffer;
   }

   public final boolean endOfInput() {
      return this._currentOffset >= this._endOffset;
   }

   public final int getBeginOffset() {
      return this._beginOffset;
   }

   public final int getEndOffset() {
      return this._endOffset;
   }

   public final int getCurrentOffset() {
      return this._currentOffset;
   }

   public final void setBeginOffset(int offset) {
      this._beginOffset = offset;
   }

   public final void setEndOffset(int offset) {
      this._endOffset = offset;
   }

   public final void setCurrentOffset(int offset) {
      this._currentOffset = offset;
      this.setMatchOffsets(-1, -1);
   }

   @Override
   public final String toString() {
      return new String(this._originalBuffer, this._beginOffset, this.length());
   }

   public final String preMatch() {
      return new String(this._originalBuffer, this._beginOffset, this._matchBeginOffset - this._beginOffset);
   }

   public final String postMatch() {
      return new String(this._originalBuffer, this._matchEndOffset, this._endOffset - this._matchEndOffset);
   }

   public final String match() {
      return new String(this._originalBuffer, this._matchBeginOffset, this._matchEndOffset - this._matchBeginOffset);
   }

   public final void setMatchOffsets(int matchBeginOffset, int matchEndOffset) {
      this._matchBeginOffset = matchBeginOffset;
      this._matchEndOffset = matchEndOffset;
   }

   public final int getMatchBeginOffset() {
      return this._matchBeginOffset;
   }

   public final int getMatchEndOffset() {
      return this._matchEndOffset;
   }
}
