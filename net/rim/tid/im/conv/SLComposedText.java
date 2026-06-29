package net.rim.tid.im.conv;

import java.util.Stack;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.tid.text.AttributedString;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class SLComposedText {
   protected SLVariants[] _variantsContainer;
   private Stack _poolOfFreeVariants = (Stack)(new Object());
   private int _hCountInitial;
   private int _committedVariantsCount;
   private int _committedCharactersCount;
   private int _hCount;
   private int _vCount;
   private boolean _islookupVisable;
   protected boolean _highlight;
   private boolean _useHighlightMask;
   private boolean _isUnderlined = true;
   protected int _currentVariantPosition = -1;
   protected int[] _lookupRange = new int[2];
   private int _boldedIndex = -1;
   private int _convertedCharacterCount;
   private int _type = 1;
   private WeakReference _outputWR = (WeakReference)(new Object(null));
   private WeakReference _outputBufferWR = (WeakReference)(new Object(null));
   private long _underlineAttribute;
   private SLVariants _editModeVariant;
   private int _editModeCaretPos = -1;
   private byte _lookupStyle;
   public static final byte TYPE_GENERAL;
   public static final byte TYPE_CHINESE;
   public static final byte TYPE_JAPANESE;

   public SLComposedText(int hCount, int vCount, int type) {
      this._type = type;
      this._vCount = vCount;
      this._hCountInitial = hCount;
      switch (this._type) {
         case 1:
            this._variantsContainer = new SLVariants[hCount];
            break;
         case 2:
         default:
            this._variantsContainer = new SlChVariants[hCount];
            break;
         case 3:
            this._variantsContainer = new JapaneseVariants[hCount];
      }

      this.createVariants(hCount < 2 ? 2 : hCount);
      this._underlineAttribute = 524288;
   }

   public void setUnderlineAttrib(long newAtrib) {
      this._underlineAttribute = newAtrib;
   }

   public void resizeInternalBuffers(int newSize) {
      this.removeAllVariants();
      if (newSize > 0) {
         Array.resize(this._variantsContainer, newSize);
         if (this._poolOfFreeVariants.size() > newSize) {
            this._poolOfFreeVariants.setSize(newSize);
            this._poolOfFreeVariants.trimToSize();
            return;
         }

         this.createVariants(newSize - this._poolOfFreeVariants.size());
      }
   }

   private void createVariants(int count) {
      for (int i = 0; i < count; i++) {
         switch (this._type) {
            case 1:
               this._poolOfFreeVariants.addElement(new SLVariants(this._vCount));
               break;
            case 2:
            default:
               this._poolOfFreeVariants.addElement(new SlChVariants(this._vCount));
               break;
            case 3:
               this._poolOfFreeVariants.addElement(new JapaneseVariants(this._hCountInitial, this._vCount));
         }
      }
   }

   public SLComposedText(int hCount, int vCount) {
      this(hCount, vCount, 1);
   }

   public void setCurrentVariantPosition(int position) {
      if (position < this._hCount) {
         this._currentVariantPosition = position;
      }
   }

   public int getCurrentVariantPosition() {
      return this._currentVariantPosition;
   }

   public SLVariants[] getVariantsContainer() {
      return this._variantsContainer;
   }

   public int getVariantsCount() {
      return this._hCount;
   }

   public void setVariantsCount(int count) {
      if (count >= 0 && count < this._variantsContainer.length) {
         this._hCount = count;
      }
   }

   public int getFreeVariantsCount() {
      return this._poolOfFreeVariants.size();
   }

   public int getCommittedVariantsCount() {
      return this._committedVariantsCount;
   }

   public void setCommittedVariantsCount(int count) {
      this._committedVariantsCount = count;
   }

   public void setConvertedCharacterCount(int count) {
      this._convertedCharacterCount = count;
   }

   public int getConvertedCharacterCount() {
      return this._convertedCharacterCount;
   }

   public int getCommittedCharactersCount() {
      int result = 0;

      for (int i = 0; i < this._committedVariantsCount && i < this._hCount; i++) {
         result += this._variantsContainer[i].currentVariantLength();
      }

      return result;
   }

   public int getCommittedCharactersCountL() {
      return this._committedCharactersCount;
   }

   public void setLookVisible(boolean isVisible) {
      this._islookupVisable = isVisible;
   }

   public void setUnderlined(boolean aUnderlined) {
      this._isUnderlined = aUnderlined;
   }

   public void setBolded(int anIndex) {
      this._boldedIndex = anIndex;
   }

   public int[] getLookupRange() {
      return this.getLookupRange(false);
   }

   public int[] getLookupRange(boolean editMode) {
      if (editMode) {
         this._lookupRange[0] = this.getEditModeCaretPosition();
         this._lookupRange[1] = this._lookupRange[0] + 1;
         return this._lookupRange;
      }

      this._lookupRange[0] = this._lookupRange[1] = 0;

      for (int i = 0; i < this._currentVariantPosition; i++) {
         SLVariants next = this._variantsContainer[i];
         this._lookupRange[0] = this._lookupRange[0] + (next.isStickyOriginal() ? next.getOriginal().length() : next.currentVariantLength());
      }

      this._lookupRange[1] = this._lookupRange[0];
      if (this._currentVariantPosition != -1) {
         SLVariants last = this._variantsContainer[this._currentVariantPosition];
         this._lookupRange[1] = this._lookupRange[1] + (last.isStickyOriginal() ? last.getOriginal().length() : last.currentVariantLength());
      }

      return this._lookupRange;
   }

   public boolean isLookupVisible() {
      return this._islookupVisable;
   }

   public void setHighlight(boolean isOn) {
      this._highlight = isOn;
      if (isOn) {
         this._useHighlightMask = true;
      }
   }

   public boolean isHighlight() {
      return this._highlight;
   }

   public boolean insertVariant(SLVariants aVariant, int aPosition) {
      if (aPosition >= 0 && aPosition <= this._variantsContainer.length - 1 && this._hCount < this._variantsContainer.length) {
         if (aPosition == this._hCount) {
            this._variantsContainer[aPosition] = aVariant;
         } else {
            SLVariants tmp = this._variantsContainer[aPosition];
            this._variantsContainer[aPosition] = aVariant;
            int count = this._hCount - aPosition;

            for (int i = 0; i < count; i++) {
               this._variantsContainer[this._hCount - i] = this._variantsContainer[this._hCount - i - 1];
            }

            this._variantsContainer[aPosition + 1] = tmp;
         }

         this._currentVariantPosition = aPosition;
         this._hCount++;
         return true;
      } else {
         return false;
      }
   }

   private void commitVariants(int aFromIndex, int aToIndex) {
      int count = aToIndex - aFromIndex;

      for (int i = aFromIndex; i < aToIndex; i++) {
         this._variantsContainer[i].clear();
         this._poolOfFreeVariants.addElement(this._variantsContainer[i]);
         this._variantsContainer[i] = null;
      }

      if (count == this._hCount) {
         this._currentVariantPosition = -1;
      } else if (aFromIndex == 0) {
         int var6 = aToIndex;

         for (int j = 0; var6 < this._hCount; j++) {
            this._variantsContainer[j] = this._variantsContainer[var6];
            this._variantsContainer[var6] = null;
            var6++;
         }

         this._currentVariantPosition -= count;
         if (this._currentVariantPosition < 0) {
            this._currentVariantPosition = 0;
         }
      } else {
         int var7 = aToIndex;

         for (int j = aFromIndex; var7 < this._hCount; j++) {
            this._variantsContainer[j] = this._variantsContainer[var7];
            this._variantsContainer[var7] = null;
            var7++;
         }

         if (this._currentVariantPosition > aToIndex - 1 || this._currentVariantPosition == this._hCount - 1) {
            this._currentVariantPosition -= count;
         }
      }

      this._hCount -= count;
   }

   public void deleteCommittedVariants() {
      if (this._hCount == this._committedVariantsCount) {
         this.removeAllVariants();
      } else {
         this.commitVariants(0, this._committedVariantsCount);
      }

      this._committedVariantsCount = 0;
      this._convertedCharacterCount = 0;
   }

   protected int fillOutputBuffer(StringBuffer output) {
      int committedCount = 0;

      for (int i = 0; i < this._hCount; i++) {
         SLVariants next = this._variantsContainer[i];
         if (next.isStickyOriginal()) {
            output.append(next.getOriginal());
         } else {
            this._variantsContainer[i].addCurrentVariantTo(output);
         }

         if (i < this._committedVariantsCount) {
            committedCount = output.length();
         }
      }

      return committedCount;
   }

   public AttributedString getOutput() {
      StringBuffer outputBuffer = WeakReferenceUtilities.getStringBuffer(this._outputBufferWR);
      AttributedString output = (AttributedString)this._outputWR.get();
      if (output == null) {
         output = (AttributedString)(new Object());
         this._outputWR.set(output);
      }

      outputBuffer.setLength(0);
      this._committedCharactersCount = this.fillOutputBuffer(outputBuffer);
      output.set(outputBuffer);
      if (this._convertedCharacterCount > outputBuffer.length() - this._committedCharactersCount) {
         this._convertedCharacterCount = 0;
      }

      if (outputBuffer.length() != 0) {
         int unStart = this._committedCharactersCount;
         int unEnd = outputBuffer.length();
         if (this._isUnderlined && unEnd > unStart) {
            output.setAttrib(unStart, unEnd, this._underlineAttribute, 786432);
         }

         this.setHighlight(output);
         if (this._boldedIndex != -1 && this._boldedIndex < unEnd) {
            output.setAttrib(this._boldedIndex, this._boldedIndex + 1, 512, 512);
         }
      }

      return output;
   }

   protected void setHighlight(AttributedString output) {
      if (this._highlight && this._currentVariantPosition >= 0 && this._currentVariantPosition < this._hCount) {
         this.getLookupRange();
         int to = Math.min(this._lookupRange[1], this._lookupRange[0] + this._variantsContainer[this._currentVariantPosition].getHighlightLength());
         output.setAttrib(this._lookupRange[0], to, 67108864, 67108864);
      }
   }

   public long getAttributeMask() {
      long mask = 0;
      if (this._isUnderlined) {
         mask |= 786432;
      }

      if (this._useHighlightMask) {
         mask |= 67108864;
      }

      if (this._boldedIndex != -1) {
         mask |= 512;
      }

      return mask;
   }

   public boolean isEmpty() {
      return this._hCount == 0;
   }

   public SLVariants getCurrentVariant(boolean aFlag) {
      if (this._currentVariantPosition != -1) {
         return this._variantsContainer[this._currentVariantPosition];
      } else if (aFlag) {
         this._currentVariantPosition = 0;
         this._variantsContainer[this._currentVariantPosition] = (SLVariants)this._poolOfFreeVariants.pop();
         this._hCount = 1;
         return this._variantsContainer[this._currentVariantPosition];
      } else {
         return null;
      }
   }

   public SLVariants getFreeVariant() {
      if (this._poolOfFreeVariants.isEmpty()) {
         switch (this._type) {
            case 1:
               this._poolOfFreeVariants.addElement(new SLVariants(0));
               break;
            case 2:
            default:
               this._poolOfFreeVariants.addElement(new SlChVariants(120));
               break;
            case 3:
               this._poolOfFreeVariants.addElement(new JapaneseVariants(this._hCountInitial, this._vCount));
         }
      }

      return (SLVariants)this._poolOfFreeVariants.pop();
   }

   public int getCaretPosition() {
      int result = 0;
      if (this._hCount > 0) {
         for (int i = this._committedVariantsCount; i < this._currentVariantPosition; i++) {
            result += this._variantsContainer[i].currentVariantLength();
         }

         result += this._variantsContainer[this._currentVariantPosition].getCaretPosition();
      }

      return result;
   }

   public void commitAll() {
      this._committedVariantsCount = this._hCount;
      this._islookupVisable = false;
      this._highlight = false;
   }

   public void moveCarentVariantPos(boolean forward) {
      if (this._hCount > 1) {
         if (forward) {
            this._currentVariantPosition = this._currentVariantPosition == this._hCount - 1 ? 0 : this._currentVariantPosition + 1;
            return;
         }

         this._currentVariantPosition = this._currentVariantPosition == 0 ? this._hCount - 1 : this._currentVariantPosition - 1;
      }
   }

   public void removeVariant(int position) {
      if (position >= 0 && position <= this._hCount - 1) {
         this.commitVariants(position, position + 1);
         if (this._currentVariantPosition < 0 && this._hCount != 0) {
            this._currentVariantPosition = 0;
         }
      }
   }

   public void removeAllVariants() {
      this.commitVariants(0, this._hCount);
   }

   public SLVariants getEditModeVariant() {
      if (this._editModeVariant == null) {
         this._editModeVariant = new SLVariants(3, false);
         this._editModeVariant.higlightCurrentVariant(false);
      }

      return this._editModeVariant;
   }

   public int getEditModeCaretPosition() {
      return this._editModeCaretPos == -1 ? this.getCaretPosition() : this._editModeCaretPos;
   }

   public void setEditModeCaretPosition(int pos) {
      this._editModeCaretPos = pos;
   }

   public void resetEditMode() {
      this._editModeCaretPos = -1;
   }

   public boolean isLookupStyle(byte style) {
      return (this._lookupStyle & style) != 0;
   }

   public void setLookupStyle(byte style) {
      this._lookupStyle = style;
   }

   public byte getLookupStyle() {
      return this._lookupStyle;
   }
}
