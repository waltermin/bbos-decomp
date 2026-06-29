package net.rim.device.api.util;

import java.util.Vector;
import net.rim.device.internal.util.ExternalStringPattern;

public final class StringPatternEnumerator {
   private StringPatternContainer _patterns;
   private AbstractString _string;
   private StringPattern$Match _nextMatch = new StringPattern$Match();
   private boolean _nextMatchIsValid;
   private int[] _beginIndicies;
   private int[] _endIndicies;
   private long[] _matchIndicies;
   private int[] _prefixLengths;
   int _beginIndex;
   int _prevEndIndex;
   int _finalEndIndex;
   private boolean _endOfEnum;
   private Vector _queuedMatches;

   public StringPatternEnumerator(Object stringToScan, StringPatternContainer patternContainer) {
      this._patterns = patternContainer;
      int patternCount = this._patterns.size();
      this._beginIndicies = new int[patternCount];
      this._endIndicies = new int[patternCount];
      this._matchIndicies = new long[patternCount];
      this._prefixLengths = new int[patternCount];
      this._queuedMatches = new Vector(1);
      if (stringToScan == null) {
         this._string = null;
         this._endOfEnum = true;
      } else {
         this.setStringToScan(stringToScan);
         this.resetScanRange(0, this._string.length());
      }
   }

   public StringPatternEnumerator(Object stringToScan, int beginIndex, int endIndex, StringPatternContainer patternContainer) {
      this(stringToScan, patternContainer);
      if (this._string != null) {
         this.resetScanRange(beginIndex, endIndex);
      }
   }

   public final void reset() {
      this.resetScanRange(this._beginIndex, this._finalEndIndex);
   }

   public final void reset(Object stringToScan) {
      this.setStringToScan(stringToScan);
      this.resetScanRange(0, this._string.length());
   }

   public final void reset(Object stringToScan, int beginIndex, int endIndex) {
      this.setStringToScan(stringToScan);
      this.resetScanRange(beginIndex, endIndex);
   }

   public final boolean hasMoreMatches() {
      if (this._endOfEnum) {
         return false;
      }

      if (!this._nextMatchIsValid) {
         this.findNextMatch();
         if (!this._nextMatchIsValid) {
            this._endOfEnum = true;
            return false;
         }
      }

      return true;
   }

   public final boolean nextMatch(StringPattern$Match match) {
      if (!this.hasMoreMatches()) {
         return false;
      } else {
         match.setMatch(this._nextMatch);
         if (this._queuedMatches.size() > 0) {
            this._nextMatch.setMatch((StringPattern$Match)this._queuedMatches.elementAt(0));
            this._queuedMatches.removeElementAt(0);
            return true;
         } else {
            this._prevEndIndex = this._nextMatch.endIndex;
            this._nextMatchIsValid = false;
            return true;
         }
      }
   }

   private final void findNextMatch() {
      int nextMatchIndex = 0;
      StringPattern[] patterns = this._patterns.getElements();
      int patternCount = patterns.length;
      int stringLength = this._string.length();
      this._nextMatchIsValid = false;

      for (int idx = 0; idx < patternCount; idx++) {
         StringPattern currentPattern = patterns[idx];
         if (currentPattern != null && this._beginIndicies[idx] < stringLength) {
            if (this._beginIndicies[idx] < this._prevEndIndex) {
               boolean validTry = false;

               try {
                  validTry = currentPattern.findMatch(this._string, this._prevEndIndex, this._finalEndIndex, this._nextMatch);
               } catch (IndexOutOfBoundsException var12) {
               } catch (Throwable var13) {
               }

               if (validTry
                  && (
                     this._nextMatch.beginIndex < this._prevEndIndex
                        || this._nextMatch.beginIndex >= this._nextMatch.endIndex
                        || this._nextMatch.endIndex > this._finalEndIndex
                  )) {
                  validTry = false;
               }

               if (!validTry) {
                  this._beginIndicies[idx] = stringLength;
                  continue;
               }

               this._beginIndicies[idx] = this._nextMatch.beginIndex;
               this._endIndicies[idx] = this._nextMatch.endIndex;
               this._matchIndicies[idx] = this._nextMatch.id;
               this._prefixLengths[idx] = this._nextMatch.prefixLength;
            }

            if (!this._nextMatchIsValid || this._endIndicies[idx] <= this._beginIndicies[nextMatchIndex]) {
               this._nextMatchIsValid = true;
               nextMatchIndex = idx;
            } else if (this._endIndicies[nextMatchIndex] > this._beginIndicies[idx]) {
               int idxNew = this._beginIndicies[idx] + this._prefixLengths[idx];
               int idxOld = this._beginIndicies[nextMatchIndex] + this._prefixLengths[nextMatchIndex];
               if (idxNew < idxOld) {
                  nextMatchIndex = idx;
                  this._queuedMatches.removeAllElements();
               } else if (idxNew == idxOld) {
                  int lenNew = this._endIndicies[idx] - idxNew;
                  int lenOld = this._endIndicies[nextMatchIndex] - idxOld;
                  if (lenNew > lenOld) {
                     nextMatchIndex = idx;
                     this._queuedMatches.removeAllElements();
                  } else if (lenNew == lenOld) {
                     if (this._beginIndicies[idx] < this._beginIndicies[nextMatchIndex]) {
                        nextMatchIndex = idx;
                        this._queuedMatches.removeAllElements();
                     } else if (currentPattern instanceof ExternalStringPattern && this._beginIndicies[idx] == this._beginIndicies[nextMatchIndex]) {
                        StringPattern$Match queuedMatch = new StringPattern$Match();
                        queuedMatch.beginIndex = this._beginIndicies[nextMatchIndex];
                        queuedMatch.endIndex = this._endIndicies[nextMatchIndex];
                        queuedMatch.id = this._matchIndicies[nextMatchIndex];
                        queuedMatch.prefixLength = this._prefixLengths[nextMatchIndex];
                        this._queuedMatches.addElement(queuedMatch);
                        nextMatchIndex = idx;
                     }
                  }
               }
            }
         }
      }

      if (this._nextMatchIsValid) {
         this._nextMatch.beginIndex = this._beginIndicies[nextMatchIndex];
         this._nextMatch.endIndex = this._endIndicies[nextMatchIndex];
         this._nextMatch.id = this._matchIndicies[nextMatchIndex];
         this._nextMatch.prefixLength = this._prefixLengths[nextMatchIndex];
      }
   }

   private final void setStringToScan(Object stringToScan) {
      if (stringToScan == null) {
         this._string = null;
      } else if (stringToScan instanceof AbstractString) {
         this._string = (AbstractString)stringToScan;
      } else if (!(this._string instanceof AbstractStringWrapper)) {
         this._string = AbstractStringWrapper.createInstance(stringToScan);
      } else {
         AbstractStringWrapper strWrap = (AbstractStringWrapper)this._string;

         try {
            strWrap.reset(stringToScan);
         } catch (IllegalArgumentException e) {
            this._string = AbstractStringWrapper.createInstance(stringToScan);
         }
      }
   }

   private final void resetScanRange(int beginIndex, int endIndex) {
      Arrays.fill(this._beginIndicies, -1);
      Arrays.fill(this._endIndicies, -1);
      Arrays.fill(this._matchIndicies, -1);
      Arrays.fill(this._prefixLengths, -1);
      this._nextMatchIsValid = false;
      this._prevEndIndex = beginIndex;
      this._beginIndex = beginIndex;
      this._finalEndIndex = endIndex;
      this._endOfEnum = beginIndex >= endIndex;
      this._queuedMatches.removeAllElements();
   }
}
