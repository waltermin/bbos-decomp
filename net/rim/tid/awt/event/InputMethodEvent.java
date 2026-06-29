package net.rim.tid.awt.event;

import net.rim.tid.awt.Event;
import net.rim.tid.im.ISupplementaryInputData;
import net.rim.tid.itie.IComponent;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.TextHitInfo;

public final class InputMethodEvent extends Event {
   private AttributedString _text;
   private int _committedCharacterCount;
   private int _convertedCharacterCount;
   private TextHitInfo _caret;
   private TextHitInfo _visiblePosition;
   private int _modifiers;
   private long _attribTextMask;
   private boolean _overrideCommittedTextAttributes;
   private ISupplementaryInputData _supplementaryInputData;
   private byte _caretShape;
   public static final int INPUT_METHOD_FIRST = 1100;
   public static final int INPUT_METHOD_TEXT_CHANGED = 1100;
   public static final int CARET_POSITION_CHANGED_UNCOMMITTED = 1101;
   public static final int CARET_POSITION_CHANGED_COMMITTED = 1102;
   public static final int INPUT_METHOD_RESTART = 1103;
   public static final int CARET_SHAPE_CHANGED = 1104;
   public static final int CARET_POSITION_CHANGED = 1101;
   public static final int INPUT_METHOD_LAST = 1104;

   public InputMethodEvent(
      IComponent source,
      int id,
      int modifiers,
      AttributedString text,
      long attribTextMask,
      int committedCharacterCount,
      int convertedCharacterCount,
      TextHitInfo caret,
      TextHitInfo visiblePosition
   ) {
      super(source, id, Event.INPUT_METHOD_EVENT_MASK);
      this.init(source, id, modifiers, text, attribTextMask, committedCharacterCount, convertedCharacterCount, caret, visiblePosition);
   }

   public InputMethodEvent(
      IComponent source,
      int id,
      int modifiers,
      AttributedString text,
      long attribTextMask,
      int committedCharacterCount,
      int convertedCharacterCount,
      TextHitInfo caret,
      TextHitInfo visiblePosition,
      int mask
   ) {
      super(source, id, mask | Event.INPUT_METHOD_EVENT_MASK);
      this.init(source, id, modifiers, text, attribTextMask, committedCharacterCount, convertedCharacterCount, caret, visiblePosition);
   }

   public final void init(
      IComponent aSource,
      int aId,
      int modifiers,
      AttributedString text,
      long attribTextMask,
      int committedCharacterCount,
      int convertedCharacterCount,
      TextHitInfo caret,
      TextHitInfo visiblePosition
   ) {
      this.init(aSource, aId, modifiers, text, attribTextMask, committedCharacterCount, convertedCharacterCount, caret, visiblePosition, true);
   }

   public final void init(
      IComponent aSource,
      int aId,
      int modifiers,
      AttributedString text,
      long attribTextMask,
      int committedCharacterCount,
      int convertedCharacterCount,
      TextHitInfo caret,
      TextHitInfo visiblePosition,
      boolean overrideCommittedTextAttributes
   ) {
      this.init(
         aSource,
         aId,
         modifiers,
         text,
         attribTextMask,
         committedCharacterCount,
         convertedCharacterCount,
         caret,
         visiblePosition,
         overrideCommittedTextAttributes,
         (byte)4
      );
   }

   public final void init(
      IComponent aSource,
      int aId,
      int modifiers,
      AttributedString text,
      long attribTextMask,
      int committedCharacterCount,
      int convertedCharacterCount,
      TextHitInfo caret,
      TextHitInfo visiblePosition,
      boolean overrideCommittedTextAttributes,
      byte caretShape
   ) {
      super._source = aSource;
      super._ID = aId;
      if (text == null || committedCharacterCount >= 0 && committedCharacterCount <= text.length()) {
         this._modifiers = modifiers;
         this._text = text;
         this._attribTextMask = attribTextMask;
         this._committedCharacterCount = committedCharacterCount;
         this._convertedCharacterCount = convertedCharacterCount;
         this._caret = caret;
         this._visiblePosition = visiblePosition;
         super._consumed = false;
         this._overrideCommittedTextAttributes = overrideCommittedTextAttributes;
         this._supplementaryInputData = null;
         this._caretShape = caretShape;
      } else {
         throw new IllegalArgumentException("committedCharacterCount outside of valid range " + committedCharacterCount + " " + text.length());
      }
   }

   public InputMethodEvent(IComponent source, int id, TextHitInfo caret, TextHitInfo visiblePosition) {
      this(source, id, 0, null, 0, 0, 0, caret, visiblePosition, 0);
   }

   public final AttributedString getText() {
      return this._text;
   }

   public final long getTextMask() {
      return this._attribTextMask;
   }

   public final int getModifiers() {
      return this._modifiers;
   }

   public final int getCommittedCharacterCount() {
      return this._committedCharacterCount;
   }

   public final int getConvertedCharacterCount() {
      return this._convertedCharacterCount;
   }

   public final TextHitInfo getCaret() {
      return this._caret;
   }

   public final TextHitInfo getVisiblePosition() {
      return this._visiblePosition;
   }

   public final boolean isOverrideCommittedTextAttributes() {
      return this._overrideCommittedTextAttributes;
   }

   public final void setCommittedTextAttributesOverride(boolean override) {
      this._overrideCommittedTextAttributes = override;
   }

   public final StringBuffer getOriginatingKeys() {
      return this._supplementaryInputData != null ? this._supplementaryInputData.getOriginatingKeys() : null;
   }

   public final int getOriginatingKeysCommittedCount() {
      return this._supplementaryInputData != null ? this._supplementaryInputData.getOriginatingKeysCommittedCount() : 0;
   }

   public final String[] getSupplementarySearchReadings() {
      return this._supplementaryInputData != null ? this._supplementaryInputData.getSupplementarySearchReadings() : null;
   }

   public final StringBuffer getAlternativeIdeographicReading() {
      return this._supplementaryInputData != null ? this._supplementaryInputData.getAlternativeIdeographicReading() : null;
   }

   public final int getAlternativeReadingCommittedCharacterCount() {
      return this._supplementaryInputData != null ? this._supplementaryInputData.getAlternativeReadingCommittedCharacterCount() : 0;
   }

   public final void setSupplementaryInputData(ISupplementaryInputData supplementaryInputData) {
      this._supplementaryInputData = supplementaryInputData;
   }

   public final StringBuffer getOriginalReading() {
      return this._supplementaryInputData != null ? this._supplementaryInputData.getOriginalReading() : null;
   }

   public final int getOriginalReadingCommitedCharacterCount() {
      return this._supplementaryInputData != null ? this._supplementaryInputData.getOriginalReadingCommitedCharacterCount() : 0;
   }

   public final byte getCaretShape() {
      return this._caretShape;
   }

   public final void setCaretShape(byte caretShape) {
      this._caretShape = caretShape;
   }
}
