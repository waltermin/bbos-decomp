package net.rim.tid.im.conv;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.tid.util.SLTextDataContainer;

public class SLVariants implements PersistentContentListener {
   protected SLVariants$SLTextWithSourceDataContainer _variants = new SLVariants$SLTextWithSourceDataContainer();
   protected SLVariants$SLTextWithSourceDataContainer _additionalVariants = new SLVariants$SLTextWithSourceDataContainer();
   protected int _currentVariantIndex = -1;
   protected StringBuffer _original = new StringBuffer();
   private ISLVariantsObserver _observer;
   protected int _caretPosition;
   protected boolean _isVariantsSeparated;
   protected boolean _showSelected = true;
   private long _attribMask;
   protected boolean _stickyOriginal;
   private int _contentTypeID = 0;
   private byte _highlightLength = 127;

   public void clear() {
      this._original.setLength(0);
      this._currentVariantIndex = -1;
      this._caretPosition = 0;
      if (this._observer != null) {
         this._observer.removeVariant(this);
      }

      this._observer = null;
      this._variants._count = 0;
      this._additionalVariants._count = 0;
      this.resetVariantsOffsets();
      this._isVariantsSeparated = false;
      this._highlightLength = 127;
      this._stickyOriginal = false;
   }

   public void resetVariantsCount() {
      this.resetVariantsCount(true);
   }

   public void resetVariantsCount(boolean toUpdate) {
      this._variants._count = 0;
      this._variants._totalLength = 0;
      this._additionalVariants._count = 0;
      this._additionalVariants._totalLength = 0;
      if (toUpdate) {
         this.updateObserver(true);
      }
   }

   public void setContentTypeID(int type) {
      this._contentTypeID = type;
   }

   public int getContentTypeID() {
      return this._contentTypeID;
   }

   public boolean isEmpty() {
      return this._original.length() == 0 && this._variants._count == 0 && this._additionalVariants._count == 0;
   }

   public void unsetHighlightLength() {
      this._highlightLength = 127;
   }

   public void setHighlightLength(byte len) {
      this._highlightLength = len;
   }

   public int getOverlapLengthFor(int variantPosition) {
      return 0;
   }

   public int getVariantsCount() {
      return this._variants._count + this._additionalVariants._count;
   }

   public int getAdditionalVariantsCount() {
      return this._additionalVariants._count;
   }

   public void getVariants(SLTextDataContainer aResult) {
      if (aResult != null) {
         aResult.init(this._variants._words, this._variants._lengths, this._variants._count);
      }
   }

   public void getAdditionalVariants(SLTextDataContainer aResult) {
      if (aResult != null) {
         aResult.init(this._additionalVariants._words, this._additionalVariants._lengths, this._additionalVariants._count);
      }
   }

   public void setVariants(SLTextDataContainer aData) {
      this.setVariants(aData, true);
   }

   public void setVariants(SLTextDataContainer aData, boolean toUpdate) {
      if (aData != null) {
         this._variants.init(aData._words, aData._lengths, aData._count);
         if (toUpdate) {
            this.updateObserver(true);
            return;
         }
      } else {
         this._variants._count = 0;
         if (toUpdate) {
            this.updateObserver(true);
         }
      }
   }

   public void setVariantSource(int variantIndex, int source) {
      if (this._additionalVariants._count > 0 && variantIndex < this._additionalVariants._count) {
         this._additionalVariants._sources[variantIndex] = source;
      } else {
         this._variants._sources[variantIndex - this._additionalVariants._count] = source;
      }
   }

   public int getSourceForVariant(int variantIndex) {
      return this._additionalVariants._count > 0 && variantIndex < this._additionalVariants._count
         ? this._additionalVariants._sources[variantIndex]
         : this._variants._sources[variantIndex - this._additionalVariants._count];
   }

   public void setOriginalSticky(boolean isSticky) {
      this._stickyOriginal = isSticky;
   }

   public boolean isStickyOriginal() {
      return this._stickyOriginal;
   }

   public void setVariants(char[] words, byte[] lengths, int count) {
      this.setVariants(words, lengths, count, true);
   }

   public void setVariants(char[] words, byte[] lengths, int count, boolean toUpdate) {
      this._variants.init(words, lengths, count);
      if (toUpdate) {
         this.updateObserver(true);
      }
   }

   public void setAttribMask(long mask) {
      this._attribMask = mask;
   }

   public long getAttribMask() {
      return this._attribMask;
   }

   public void appendVariant(SLTextDataContainer aData) {
      this.appendVariant(aData, true);
   }

   public void appendVariant(SLTextDataContainer aData, boolean toUpdate) {
      if (aData != null) {
         this._variants.addVariants(aData);
         if (toUpdate) {
            this.updateObserver(true);
         }
      }
   }

   public void appendVariant(StringBuffer aVariant) {
      this.appendVariant(aVariant, true);
   }

   public void appendVariant(StringBuffer aVariant, boolean toUpdate) {
      if (aVariant != null) {
         this._variants.addVariant(aVariant);
         if (toUpdate) {
            this.updateObserver(true);
         }
      }
   }

   public void appendVariant(SLCurrentVariant aVariant) {
      this.appendVariant(aVariant, true);
   }

   public void appendVariant(SLCurrentVariant aVariant, boolean toUpdate) {
      if (aVariant != null) {
         this._variants.addVariant(aVariant);
         if (toUpdate) {
            this.updateObserver(true);
         }
      }
   }

   public void setAdditionalArrays(char[] words, byte[] lengths, int count) {
      this.setAdditionalArrays(words, lengths, count, true);
   }

   public void setAdditionalArrays(char[] words, byte[] lengths, int count, boolean toUpdate) {
      this._additionalVariants.init(words, lengths, count);
      if (toUpdate) {
         this.updateObserver(true);
      }
   }

   public void setAdditionalArrays(SLTextDataContainer aData) {
      this.setAdditionalArrays(aData, true);
   }

   public void setAdditionalArrays(SLTextDataContainer aData, boolean toUpdate) {
      if (aData != null) {
         this._additionalVariants.init(aData._words, aData._lengths, aData._count);
         if (toUpdate) {
            this.updateObserver(true);
         }
      }
   }

   public boolean getVariantAt(int anIndex, SLCurrentVariant aResult) {
      boolean ret = false;
      if (aResult != null && anIndex >= 0 && anIndex < this._variants._count + this._additionalVariants._count) {
         if (this._additionalVariants._count > 0 && anIndex < this._additionalVariants._count) {
            int offset = 0;

            for (int i = 0; i < anIndex; i++) {
               offset += this._additionalVariants._lengths[i];
            }

            aResult.set(this._additionalVariants._words, offset, this._additionalVariants._lengths[anIndex]);
         } else {
            int relativeIndex = anIndex - this._additionalVariants._count;
            int offset = 0;

            for (int i = 0; i < relativeIndex; i++) {
               offset += this._variants._lengths[i];
            }

            aResult.set(this._variants._words, offset, this._variants._lengths[relativeIndex]);
         }

         ret = true;
      }

      return ret;
   }

   public boolean getVariantAt(int anIndex, StringBuffer aResult) {
      boolean ret = false;
      if (aResult != null && anIndex >= 0 && anIndex < this._variants._count + this._additionalVariants._count) {
         if (this._additionalVariants._count > 0 && anIndex < this._additionalVariants._count) {
            int offset = 0;

            for (int i = 0; i < anIndex; i++) {
               offset += this._additionalVariants._lengths[i];
            }

            aResult.append(this._additionalVariants._words, offset, this._additionalVariants._lengths[anIndex]);
         } else {
            int relativeIndex = anIndex - this._additionalVariants._count;
            int offset = 0;

            for (int i = 0; i < relativeIndex; i++) {
               offset += this._variants._lengths[i];
            }

            aResult.append(this._variants._words, offset, this._variants._lengths[relativeIndex]);
         }

         ret = true;
      }

      return ret;
   }

   public int getCaretPosition() {
      return this._caretPosition;
   }

   public void setCaretPosition(int aPosition) {
      if (aPosition >= 0 && aPosition <= this.currentVariantLength()) {
         this._caretPosition = aPosition;
      }
   }

   public void separateVariants() {
      if (this._variants._count > 0) {
         this._isVariantsSeparated = true;
      }
   }

   public boolean isVariantsSeparated() {
      return this._isVariantsSeparated;
   }

   public void higlightCurrentVariant(boolean anEnable) {
      this._showSelected = anEnable;
   }

   public void higlightCurrentVariant(boolean anEnable, boolean toUpdate) {
      this._showSelected = anEnable;
      if (toUpdate) {
         this.updateObserver(false);
      }
   }

   public boolean isCurrentVariantHiglighted() {
      return this._showSelected;
   }

   public void setVariantIndex(int aPosition) {
      this.setVariantIndex(aPosition, true);
   }

   public void setVariantIndex(int aPosition, boolean toUpdate) {
      if (this._isVariantsSeparated) {
         this._currentVariantIndex = 0;
         this.resetVariantsOffsets();
         this._isVariantsSeparated = false;
      }

      if (aPosition >= 0 && this._variants._count + this._additionalVariants._count != 0) {
         if (aPosition != this._currentVariantIndex && aPosition < this._variants._count + this._additionalVariants._count) {
            this._currentVariantIndex = aPosition;
            if (this._additionalVariants._count > 0 && this._currentVariantIndex < this._additionalVariants._count) {
               this._additionalVariants.seek(this._currentVariantIndex);
            } else {
               this._variants.seek(this._currentVariantIndex - this._additionalVariants._count);
            }

            if (toUpdate) {
               this.updateObserver(false);
            }
         }
      } else {
         this._currentVariantIndex = -1;
         this.resetVariantsOffsets();
      }
   }

   public void addCurrentVariantTo(StringBuffer aResult) {
      if (this._currentVariantIndex == -1 || this._isVariantsSeparated) {
         aResult.append(this._original);
      } else if (this._additionalVariants._count > 0 && this._currentVariantIndex < this._additionalVariants._count) {
         this._additionalVariants.addCurrentTo(aResult);
      } else {
         this._variants.addCurrentTo(aResult);
      }
   }

   public boolean currentVariant(SLCurrentVariant aVariant) {
      if (aVariant == null) {
         return false;
      } else if (this._currentVariantIndex == -1 || this._isVariantsSeparated) {
         aVariant.set(this._original, 0, this._original.length());
         return true;
      } else if (this._additionalVariants._count > 0 && this._currentVariantIndex < this._additionalVariants._count) {
         this._additionalVariants.get(aVariant);
         return true;
      } else {
         this._variants.get(aVariant);
         return true;
      }
   }

   public int currentVariantLength() {
      if (this._currentVariantIndex == -1 || this._isVariantsSeparated) {
         return this._original.length();
      } else {
         return this._additionalVariants._count > 0 && this._currentVariantIndex < this._additionalVariants._count
            ? this._additionalVariants._lengths[this._currentVariantIndex]
            : this._variants._lengths[this._currentVariantIndex - this._additionalVariants._count];
      }
   }

   public void nextVariant() {
      this.nextVariant(true);
   }

   public void nextVariant(boolean toUpdate) {
      if (this._variants._count + this._additionalVariants._count == 0) {
         this._currentVariantIndex = -1;
      } else if (this._isVariantsSeparated) {
         this._currentVariantIndex = 0;
         this.resetVariantsOffsets();
         this._isVariantsSeparated = false;
         if (toUpdate) {
            this.updateObserver(false);
         }
      } else if (this._currentVariantIndex != -1) {
         this._currentVariantIndex++;
         if (this._currentVariantIndex > this._variants._count + this._additionalVariants._count - 1) {
            this._currentVariantIndex = 0;
            this.resetVariantsOffsets();
         } else if (this._additionalVariants._count > 0) {
            if (this._currentVariantIndex < this._additionalVariants._count) {
               this._additionalVariants.next();
            } else if (this._currentVariantIndex == this._additionalVariants._count) {
               this.resetVariantsOffsets();
            } else {
               this._variants.next();
            }
         } else {
            this._variants.next();
         }

         if (toUpdate) {
            this.updateObserver(false);
         }
      }
   }

   public void previousVariant() {
      this.previousVariant(true);
   }

   public void previousVariant(boolean toUpdate) {
      if (this._variants._count + this._additionalVariants._count == 0) {
         this._currentVariantIndex = -1;
      } else if (this._isVariantsSeparated) {
         this._currentVariantIndex = 0;
         this.resetVariantsOffsets();
         this._isVariantsSeparated = false;
         if (toUpdate) {
            this.updateObserver(false);
         }
      } else if (this._currentVariantIndex != -1) {
         this._currentVariantIndex--;
         if (this._currentVariantIndex < 0) {
            this._currentVariantIndex = this._variants._count + this._additionalVariants._count - 1;
            this._variants.seek(this._currentVariantIndex - this._additionalVariants._count);
            if (toUpdate) {
               this.updateObserver(false);
            }
         } else {
            if (this._currentVariantIndex == 0) {
               this.resetVariantsOffsets();
            } else if (this._additionalVariants._count > 0) {
               if (this._currentVariantIndex == this._additionalVariants._count - 1) {
                  this._additionalVariants.seek(this._currentVariantIndex);
               } else if (this._currentVariantIndex < this._additionalVariants._count - 1) {
                  this._additionalVariants.prev();
               } else {
                  this._variants.prev();
               }
            } else {
               this._variants.prev();
            }

            if (toUpdate) {
               this.updateObserver(false);
            }
         }
      }
   }

   public void setObserver(ISLVariantsObserver anObserver) {
      this._observer = anObserver;
      this.updateObserver(true);
   }

   public void removeObserver(ISLVariantsObserver anObserver) {
      this._observer = null;
   }

   public void updateObserver(boolean isListChanged) {
      if (this._observer != null) {
         if (isListChanged) {
            this._observer.listOfVariantsChanged();
         }

         if (this._observer != null) {
            this._observer.currentIndexChanged(this._isVariantsSeparated ? 0 : this.getPosition());
         }
      }
   }

   public int getPosition() {
      return this.getCurrentVariantIndex();
   }

   public int getCurrentVariantIndex() {
      return this._currentVariantIndex;
   }

   public void setOriginal(StringBuffer aValue) {
      this._original.setLength(0);
      this._original.append(aValue);
   }

   public void setOriginal(StringBufferGap aValue) {
      this._original.setLength(0);
      this._original.append(aValue);
   }

   public void setOriginal(String aValue) {
      this._original.setLength(0);
      if (aValue != null) {
         this._original.append(aValue);
      }
   }

   public void setOriginal(SLCurrentVariant aValue) {
      this._original.setLength(0);
      if (aValue != null) {
         this._original.append(aValue._variants, aValue._offset, aValue._length);
      }
   }

   public void setCaretPositionToEnd() {
      this._caretPosition = this.currentVariantLength();
   }

   public StringBuffer getOriginal() {
      return this._original;
   }

   public void moveCaret(boolean isForward) {
      if (isForward) {
         if (this._caretPosition < this._original.length()) {
            this._caretPosition++;
            return;
         }
      } else if (this._caretPosition > 0) {
         this._caretPosition--;
      }
   }

   public void insertChar(char aChar, int aPosition) {
      if (aPosition == -1) {
         aPosition = this._caretPosition;
      }

      if (aPosition >= 0 && aPosition <= this._original.length()) {
         this._original.insert(aPosition, aChar);
         this._caretPosition = aPosition + 1;
      }
   }

   public void insertChars(char[] aChars, int aPosition) {
      if (aPosition == -1) {
         aPosition = this._caretPosition;
      }

      if (aChars.length > 0 && aPosition >= 0 && aPosition <= this._original.length()) {
         this._original.insert(aPosition, aChars);
         this._caretPosition = aPosition + aChars.length;
      }
   }

   public boolean deleteChar(boolean isForward, int aPosition) {
      return this.deleteChars(isForward, aPosition, 1);
   }

   public boolean deleteChars(boolean isForward, int aPosition, int aCount) {
      if (aPosition == -1) {
         aPosition = this._caretPosition;
      }

      if (aPosition >= 0 && aPosition <= this._original.length()) {
         if ((aPosition >= aCount || isForward) && (aPosition + aCount <= this._original.length() || !isForward)) {
            if (!isForward) {
               aPosition -= aCount;
            }

            this._caretPosition = aPosition;
            this._original.delete(aPosition, aPosition + aCount);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public int getHighlightLength() {
      return this._highlightLength == 127 ? this.currentVariantLength() : this._highlightLength;
   }

   @Override
   public void persistentContentModeChanged(int generation) {
   }

   @Override
   public void persistentContentStateChanged(int state) {
      if (state == 2) {
         this._original.setLength(0);
         this._original.setLength(this._original.capacity());
         this.clear();
      }
   }

   private void resetVariantsOffsets() {
      this._variants.resetIteration();
      this._additionalVariants.resetIteration();
   }

   @Override
   public String toString() {
      StringBuffer result = new StringBuffer();
      result.append("\nOrig=");
      result.append(this._original);
      result.append('\n');
      result.append("Pos=");
      result.append(this.getPosition());
      result.append('\n');
      result.append("CaretPos=");
      result.append(this.getCaretPosition());
      result.append('\n');
      result.append("VarCount=");
      result.append(this.getVariantsCount());
      result.append('\n');
      result.append("Variants\n");
      SLTextDataContainer textContainer = new SLTextDataContainer();
      this.getVariants(textContainer);
      textContainer.resetIteration();

      for (int i = 0; i < textContainer._count; i++) {
         textContainer.addCurrentTo(result);
         result.append("++");
         textContainer.next();
      }

      result.append("\nAddVariants\n");
      this.getAdditionalVariants(textContainer);
      textContainer.resetIteration();

      for (int i = 0; i < textContainer._count; i++) {
         textContainer.addCurrentTo(result);
         result.append("++");
         textContainer.next();
      }

      result.append('\n');
      return result.toString();
   }

   protected SLVariants(int aSize, boolean useSource) {
      this._variants.init(new char[aSize], new byte[aSize], useSource ? new int[aSize] : null, 0);
      PersistentContent.addWeakListener(this);
   }

   protected SLVariants(int aSize) {
      this(aSize, true);
   }
}
