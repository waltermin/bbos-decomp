package org.apache.oro.text.regexp;

final class Perl5MatchResult implements MatchResult {
   int _matchBeginOffset;
   int[] _beginGroupOffset;
   int[] _endGroupOffset;
   String _match;

   Perl5MatchResult(int groups) {
      this._beginGroupOffset = new int[groups];
      this._endGroupOffset = new int[groups];
   }

   @Override
   public final int length() {
      int length = this._endGroupOffset[0] - this._beginGroupOffset[0];
      return length > 0 ? length : 0;
   }

   @Override
   public final int groups() {
      return this._beginGroupOffset.length;
   }

   @Override
   public final String group(int group) {
      if (group < this._beginGroupOffset.length) {
         int begin = this._beginGroupOffset[group];
         int end = this._endGroupOffset[group];
         int length = this._match.length();
         if (begin >= 0 && end >= 0) {
            if (begin < length && end <= length && end > begin) {
               return this._match.substring(begin, end);
            }

            if (begin <= end) {
               return "";
            }
         }
      }

      return null;
   }

   @Override
   public final int begin(int group) {
      if (group < this._beginGroupOffset.length) {
         int begin = this._beginGroupOffset[group];
         int end = this._endGroupOffset[group];
         if (begin >= 0 && end >= 0) {
            return begin;
         }
      }

      return -1;
   }

   @Override
   public final int end(int group) {
      if (group < this._beginGroupOffset.length) {
         int begin = this._beginGroupOffset[group];
         int end = this._endGroupOffset[group];
         if (begin >= 0 && end >= 0) {
            return end;
         }
      }

      return -1;
   }

   @Override
   public final int beginOffset(int group) {
      if (group < this._beginGroupOffset.length) {
         int begin = this._beginGroupOffset[group];
         int end = this._endGroupOffset[group];
         if (begin >= 0 && end >= 0) {
            return this._matchBeginOffset + begin;
         }
      }

      return -1;
   }

   @Override
   public final int endOffset(int group) {
      if (group < this._endGroupOffset.length) {
         int begin = this._beginGroupOffset[group];
         int end = this._endGroupOffset[group];
         if (begin >= 0 && end >= 0) {
            return this._matchBeginOffset + end;
         }
      }

      return -1;
   }

   @Override
   public final String toString() {
      return this.group(0);
   }
}
