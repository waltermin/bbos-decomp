package net.rim.device.api.ui.component;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldLabelProvider;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleText;
import net.rim.device.api.ui.accessibility.AccessibleValue;
import net.rim.device.api.ui.text.TextRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.StringProvider;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.Edit$Helper;
import net.rim.device.internal.ui.RichText;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.device.internal.ui.security.component.LockIconField;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.vm.Array;

public class ChoiceField extends Field implements FieldLabelProvider {
   private TextRect _label = new TextRect(this);
   private String _labelText;
   private int _xPos;
   private int _yPos;
   private int _numChoices;
   private int _selectedIndex;
   private String _optionsMenuText;
   boolean _isLabelOwnLine;
   private long _rbId;
   private int _rbKey;
   private String _rbName;
   private int _cachedLocaleCode;
   private String _emptyString;
   private Object _cachedChoice;
   private String _cachedChoiceString;
   private int _lengthOfLongestLine;
   private byte[] _lengths;
   int _selectedX;
   int _selectedWidth;
   private int _accessibleState = 0;
   private final StringBufferGap _buffer = new StringBufferGap();
   private static Tag TAG = Tag.create("choice");
   private static Tag TAG_LABEL = Tag.create("label");
   public static final int CONTEXT_CHANGE_OPTION;
   public static final long FORCE_SINGLE_LINE;
   public static final long RESTRICTED_STYLE;
   public static final long NUMERIC_STYLE;
   protected static final int PADDING;
   protected static final int CHANGE_OPTION_ORDERING;
   private static MenuItem _changeOptionsItem = new ChoiceField$ChangeOptionMenuItem();

   void changeOptionDialog() {
      this.internalChangeOptionDialog();
   }

   boolean internalChangeOptionDialog() {
      if (this.isEditable() && this.getOriginal() == this && this._numChoices != 0) {
         if (Ui.isTTSEnabled()) {
            super.accessibleEventOccurred(1, new Integer(1024), new Integer(512), this);
         }

         this.removeAccessibleState(1024);
         this.addAccessibleState(512);
         ChoiceField fake = this.getChangeOptionChoiceField(null);
         ChoiceInPlaceScreen changeDialog = new ChoiceInPlaceScreen(this, fake, 0);
         changeDialog.setFont(this.getFont());
         boolean accepted = true;
         Screen screen = this.getScreen();
         if (screen != null && screen.isGlobal()) {
            UiEngine engine = Ui.getUiEngine();
            engine.pushGlobalScreen(changeDialog, engine.getGlobalPriority(screen), 5);
         } else {
            accepted = changeDialog.doModal();
         }

         if (Ui.isTTSEnabled()) {
            super.accessibleEventOccurred(1, new Integer(512), new Integer(1024), this);
         }

         this.removeAccessibleState(512);
         this.addAccessibleState(1024);
         if (accepted) {
            int index = fake.getSelectedIndex();
            this.setSelectedIndex(index, 2);
            return true;
         } else {
            fake.setSelectedIndex(this.getSelectedIndex(), -2147483646);
            return true;
         }
      } else {
         return false;
      }
   }

   protected ChoiceField getChangeOptionChoiceField(String label) {
      ChoiceField field = new ChoiceField$ChangeOptionChoiceField(this, label, this._numChoices, this._selectedIndex, this.getStyle());
      field.setChangeListener(this.getChangeListener());
      return field;
   }

   public Object getChoice(int _1) {
      throw null;
   }

   void getInPlaceRect(XYRect rect) {
      this.getFocusRect(rect);
   }

   public void setSelectedIndex(Object object) {
      for (int index = 0; index < this._numChoices; index++) {
         if (object.equals(this.getChoice(index))) {
            this.setSelectedIndex(index);
            return;
         }
      }
   }

   public int getSelectedIndex() {
      return this._selectedIndex;
   }

   public int getSize() {
      return this._numChoices;
   }

   public int getWidthOfChoices() {
      if (this._numChoices == 0) {
         return this.getWidthOfEmptyString();
      }

      int width = 0;

      for (int lv = this._numChoices - 1; lv >= 0; lv--) {
         int w = this.getWidthOfChoice(lv);
         if (width < w) {
            width = w;
         }
      }

      return width;
   }

   protected int getWidthOfChoice(int index) {
      return this.getFont().getBounds(this.getChoiceCached(index));
   }

   protected int getHeightOfChoices() {
      int lineCount = 0;

      while (lineCount < this._lengths.length && this._lengths[lineCount] != 0) {
         lineCount++;
      }

      lineCount = Math.max(1, lineCount);
      return this.getChoiceLineHeight() * lineCount;
   }

   int getChoiceLineHeight() {
      return this.getFont().getHeight();
   }

   void moveChoiceFocus(int amount) {
      int oldSelectedIndex = this._selectedIndex;
      this._selectedIndex = this._selectedIndex + (this.isStyle(134217728) ? Ui.getIncreaseDirection() : 1) * amount;
      this._selectedIndex = MathUtilities.clamp(0, this._selectedIndex, this._numChoices - 1);
      this._cachedChoice = null;
      this._cachedChoiceString = null;
      if (this._selectedIndex != oldSelectedIndex) {
         this._selectedWidth = 0;
         this.fieldChangeNotify(0);
         int width = this.getWidth();
         if (this.shouldUpdateLayout(width)) {
            this.updateLayout();
         } else {
            this.updateLengths(width);
         }

         if (Ui.isTTSEnabled()) {
            super.accessibleEventOccurred(1, new Integer(1), new Integer(2), this);
         }
      }
   }

   protected void drawChoice(int index, Graphics graphics, int x, int y, int flags, int width) {
      String text = this.getChoiceCached(index);
      if (this._lengthOfLongestLine == 0) {
         graphics.drawText(text, x, y, flags, width);
      } else {
         int offset = 0;
         int lineHeight = this.getChoiceLineHeight();

         for (int i = 0; i < this._lengths.length && this._lengths[i] != 0; i++) {
            graphics.drawText(text, offset, this._lengths[i], x, y, flags, width);
            offset += this._lengths[i];
            y += lineHeight;
         }
      }
   }

   int getSelectedWidth() {
      if (this._numChoices == 0) {
         return this.getWidthOfEmptyString();
      } else {
         return this._lengthOfLongestLine == 0 ? this.getWidthOfChoice(this._selectedIndex) : this._lengthOfLongestLine;
      }
   }

   public void setEmptyString(ResourceBundleFamily family, int id) {
      if (family != null) {
         family.getString(id);
         this.setEmptyStringFamily(family, id);
      } else {
         this.setEmptyStringFamily(CommonResource.getBundle(), 1012);
      }
   }

   public void setTextRectPos(int x, int y) {
      this._xPos = x;
      this._yPos = y;
      this.invalidate();
   }

   protected void setSelectedIndex(int index, int context) {
      if (index >= 0 && this._numChoices > index || this._numChoices == 0 && index == -1) {
         if (this._selectedIndex != index) {
            this._selectedIndex = index;
            this._cachedChoice = null;
            this._cachedChoiceString = null;
            this.fieldChangeNotify(context);
            this.focusAdd(false);
            int width = this.getWidth();
            if (this.shouldUpdateLayout(width)) {
               Screen screen = this.getScreen();
               if (screen != null) {
                  screen.invalidate();
               }

               this.updateLayout();
            } else {
               this.updateLengths(width);
               this.invalidate();
            }

            this._selectedWidth = 0;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setSelectedIndex(int index) {
      this.setSelectedIndex(index, Integer.MIN_VALUE);
   }

   public void setOptionsMenuText(String optionsMenuText) {
      this._optionsMenuText = optionsMenuText;
   }

   protected void setSize(int size) {
      if (size < 0) {
         throw new IllegalArgumentException();
      }

      this._numChoices = size;
      this.setSelectedIndex(size > 0 ? 0 : -1);
      this.updateLayout();
   }

   @Override
   public void setLabelStringProvider(StringProvider label) {
      throw new IllegalStateException("Unsupported API");
   }

   @Override
   public void setLabel(String label) {
      this._labelText = label;
      this._label.setText(label);
      this.updateLayout();
   }

   @Override
   public String getLabel() {
      return (String)this._label.getText();
   }

   @Override
   public String toString() {
      return this.getChoiceCached(this.getSelectedIndex());
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            return this.internalChangeOptionDialog();
         default:
            return false;
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean result = false;
      if (this.isEditable()) {
         int index = this._selectedIndex;
         if (key == ' ') {
            if (this._numChoices != 0) {
               index += (status & 2) != 0 ? -1 : 1;
               if (index >= this._numChoices) {
                  index = 0;
               }

               if (index < 0) {
                  index = this._numChoices - 1;
               }
            }
         } else {
            SLKeyLayout layout = Keypad.getLayout();
            int modif = SLKeyLayout.convertStatusToModifiers(status);
            StringBuffer keys;
            if ((status & 32768) == 0) {
               keys = layout.getComplementaryChars(key, modif);
               if (keys != null) {
                  keys = new StringBuffer(keys.toString());
               }
            } else {
               keys = new StringBuffer();
               keys.append(key);
            }

            index = this.findNextItem(keys);
            if (index == -1) {
               keys = layout.getKeyChars(layout.getOriginalKeyCode(key, modif), 8);
               if (keys != null) {
                  keys = new StringBuffer(keys.toString());
               }

               index = this.findNextItem(keys);
               if ((status & 1) != 0 && index == -1) {
                  keys = new StringBuffer(1);
                  keys.append(layout.getUnaltedChar(key));
                  index = this.findNextItem(keys);
               }
            }
         }

         if (index != -1) {
            this.setSelectedIndex(index, 0);
            result = true;
         }

         if (result && Ui.isTTSEnabled()) {
            super.accessibleEventOccurred(1, new Integer(1), new Integer(2), this);
         }
      }

      return result;
   }

   @Override
   protected boolean keyControl(char ch, int status, int time) {
      switch (ch) {
         case '\u0082':
            return super.keyControl(ch, status, time);
         case '\u0083':
         case '\u0084':
         default:
            return true;
      }
   }

   @Override
   protected boolean keyStatus(int keycode, int time) {
      return Keypad.key(keycode) == 257 && (Keypad.status(keycode) & 1) != 0 ? this.internalChangeOptionDialog() : false;
   }

   private int findNextItem(StringBuffer keys) {
      if (this._numChoices != 0 && keys != null) {
         int keysLen = keys.length();

         for (int index = 0; index < this._numChoices; index++) {
            String currChoice = this.getChoice((index + this._selectedIndex + 1) % this._numChoices).toString();
            if (currChoice.length() > 0) {
               char first = CharacterUtilities.toLowerCase(currChoice.charAt(0));

               for (int i = 0; i < keysLen; i++) {
                  if (first == CharacterUtilities.toLowerCase(keys.charAt(i))) {
                     return (index + this._selectedIndex + 1) % this._numChoices;
                  }
               }
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   @Override
   protected void layout(int width, int height) {
      this.calculateLengths(width);
      int labelBounds = this.getFont().getBounds(this._labelText);
      int preferredWidth = labelBounds + 2 + this.getSelectedWidth();
      this._label.invalidateLayout();
      this._label.setPosition(this._xPos, this._yPos);
      if (this.isHAlignStyle(4294967296L) || this.isHAlignStyle(12884901888L) || this.isHAlignStyle(8589934592L)) {
         width = Math.min(width, preferredWidth);
      }

      if (this.isStyle(536870912)) {
         int usableWidth = width - this.getSelectedWidth();
         if (usableWidth < 0) {
            usableWidth = 0;
         }

         this._label.setStyle(64 | this._label.getStyle());
         this._label.layout(usableWidth, height);
         height = Math.max(this._label.getHeight(), this.getChoiceLineHeight());
      } else {
         this._label.layout(Math.min(labelBounds + 2, width), height);
         if (this._lengthOfLongestLine != 0) {
            if (preferredWidth <= width && !this._isLabelOwnLine) {
               height = Math.max(this._label.getHeight(), this.getHeightOfChoices());
            } else {
               height = this._label.getHeight() + this.getHeightOfChoices();
            }
         } else if (preferredWidth <= width && !this._isLabelOwnLine) {
            height = Math.max(this._label.getHeight(), this.getChoiceLineHeight());
         } else {
            height = this._label.getHeight() + this.getChoiceLineHeight();
         }
      }

      this.setExtent(width, height);
   }

   private boolean isHAlignStyle(long style) {
      return (this.getStyle() & 12884901888L) >>> 32 == style >>> 32;
   }

   private void calculateLengths(int width) {
      width--;
      this._lengthOfLongestLine = 0;
      Arrays.fill(this._lengths, (byte)0);
      if (this._selectedIndex > -1) {
         int labelWidth = this._label.getWidth() + 2;
         int totalWidth = labelWidth + this.getWidthOfChoice(this._selectedIndex);
         boolean labelOnOwnLine = this._isLabelOwnLine;
         if (this._label.getLineCount() > 1 || totalWidth > width) {
            totalWidth -= labelWidth;
            labelOnOwnLine = true;
         }

         if (totalWidth > width) {
            if (!labelOnOwnLine && this._label.getLineCount() == 1) {
               width -= labelWidth;
            }

            String text = this.getChoiceCached(this._selectedIndex);
            this._buffer.clear();
            this._buffer.insert(text);
            ChoiceField$EditHelperForm.fonts[0] = this.getFont();
            ChoiceField$EditHelperForm.offsets[1] = text.length();
            Edit$Helper helper = RichText.calculateLengths(
               width,
               0,
               this._buffer.length(),
               this._buffer,
               ChoiceField$EditHelperForm.offsets,
               ChoiceField$EditHelperForm.attributes,
               ChoiceField$EditHelperForm.fonts,
               true
            );
            if (this._lengths.length != helper._lineCount) {
               Array.resize(this._lengths, helper._lineCount);
            }

            System.arraycopy(helper._lengths, 0, this._lengths, 0, helper._lineCount);
            this._lengthOfLongestLine = helper._lengthOfLongestLineInPixels;
            return;
         }

         this._lengthOfLongestLine = 0;
      }
   }

   private void updateLengths(int width) {
      if (this._selectedIndex > -1 && width > 0 && !this.isStyle(536870912)) {
         if (!this._isLabelOwnLine && this._label.isLayoutValid() && this._label.getLineCount() == 1) {
            width -= this._label.getWidth();
         }

         if (this.getWidthOfChoice(this._selectedIndex) > width) {
            this.calculateLengths(width);
            return;
         }
      }

      this._lengthOfLongestLine = 0;
      Arrays.fill(this._lengths, (byte)0);
   }

   private boolean shouldUpdateLayout(int width) {
      boolean result = false;
      if (this._selectedIndex > -1 && width > 0) {
         if (!this._isLabelOwnLine && this._label.isLayoutValid() && this._label.getLineCount() == 1) {
            width -= this._label.getWidth();
         }

         int choiceWidth = this.getWidthOfChoice(this._selectedIndex);
         int estimatedLines = width == 0 ? 1 : choiceWidth / width + 1;
         int lastLineCount = 0;

         while (lastLineCount < this._lengths.length && this._lengths[lastLineCount] != 0) {
            lastLineCount++;
         }

         lastLineCount = Math.max(1, lastLineCount);
         result = estimatedLines != lastLineCount;
         int workingWidth = this._label.getWidth() + 2 + choiceWidth;
         result |= workingWidth > width && this._label.isLayoutValid() && this._label.getLineCount() == 1;
      }

      return result;
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (Ui.getMode() < 2 && this.isEditable()) {
         if (this._optionsMenuText == null) {
            contextMenu.addItem(_changeOptionsItem);
            return;
         }

         MenuItem item = new ChoiceField$ChangeOptionMenuItem(this._optionsMenuText);
         contextMenu.addItem(item);
      }
   }

   private String getEmptyString() {
      this.checkLocale();
      return this._emptyString;
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      return super.moveFocus(amount, status, time);
   }

   @Override
   protected void paint(Graphics graphics) {
      if (this._selectedWidth == 0) {
         this._selectedWidth = this.getSelectedWidth();
      }

      int contentWidth = this.getContentWidth();
      int flags = 64;
      switch ((int)((this.getStyle() & 12884901888L) >>> 32)) {
         case -1:
            break;
         case 0:
            if (this._isLabelOwnLine) {
               this._selectedX = 0;
               flags |= 6;
               break;
            }
         case 2:
            flags |= 5;
            this._selectedX = contentWidth - this._selectedWidth - 1;
            break;
         case 1:
         default:
            flags |= 6;
            if (this._isLabelOwnLine) {
               this._selectedX = 0;
            } else {
               this._selectedX = this._label.getExtent().width - 1;
            }
            break;
         case 3:
            flags |= 4;
            this._selectedX = contentWidth - this._selectedWidth - 1;
      }

      if (this._selectedX < -1) {
         this._selectedX = 0;
      }

      this._label.paintSelf(graphics);
      if (this._numChoices == 0) {
         int y = this.getContentHeight() - this.getChoiceLineHeight();
         if (this._isLabelOwnLine) {
            y = this.getChoiceLineHeight();
         }

         graphics.drawText(this.getEmptyString(), this._selectedX + 1, y, flags, contentWidth - this._selectedX - 1);
      } else {
         this.drawChoice(
            this._selectedIndex, graphics, this._selectedX + 1, this.getContentHeight() - this.getHeightOfChoices(), flags, contentWidth - this._selectedX - 1
         );
         if (this.isStyle(268435456) && (!this.isEditable() || this._numChoices == 1)) {
            int x = this._selectedX - 1 - 5;
            int y = this.getContentHeight() - this.getHeightOfChoices() + this.getFont().getBaseline() - 5;
            LockIconField.drawLock(graphics, x, y);
         }
      }
   }

   @Override
   public void getFocusRect(XYRect rect) {
      int heightOfChoices = this.getHeightOfChoices();
      int y = this.getContentHeight() - heightOfChoices;
      int x = this._selectedX;
      if (this._isLabelOwnLine) {
         y--;
         x++;
      }

      rect.set(x, y, this._selectedWidth + 1, heightOfChoices);
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      this._label.applyTheme();
      this._isLabelOwnLine = ThemeManager.getActiveTheme().isLabelOnOwnLine();
   }

   private String getChoiceCached(int index) {
      if (index != this._selectedIndex) {
         return this.getChoice(index).toString();
      }

      Object choice = this.getChoice(index);
      if (this._cachedChoiceString == null || this._cachedChoice != choice) {
         this._cachedChoice = choice;
         this._cachedChoiceString = choice.toString();
      }

      return this._cachedChoiceString;
   }

   private void setEmptyStringFamily(ResourceBundleFamily rb, int key) {
      this._rbId = rb.getId();
      this._rbName = rb.getName();
      this._rbKey = key;
      this._cachedLocaleCode = 0;
   }

   @Override
   public int getPreferredWidth() {
      int width = this.getWidthOfChoices();
      int labelWidth = 0;
      if (this._labelText != null) {
         labelWidth = this.getFont().getBounds(this._labelText);
      }

      if (this._isLabelOwnLine) {
         width = Math.max(width, labelWidth);
      } else {
         width += labelWidth;
      }

      return width + 2;
   }

   private int getWidthOfEmptyString() {
      return this.getFont().getBounds(this.getEmptyString());
   }

   protected ChoiceField(String label, int numChoices, int index, long style) {
      super(verifyStyle(style));
      this.setTag(TAG);
      this._label.setTag(TAG_LABEL);
      this._labelText = label;
      this._label.setText(label);
      this._lengths = new byte[0];
      this._numChoices = numChoices;
      index = numChoices == 0 ? -1 : index;
      this.setSelectedIndex(index);
      this._isLabelOwnLine = ThemeManager.getActiveTheme().isLabelOnOwnLine();
   }

   private void checkLocale() {
      if (this._rbId != 0) {
         int currentCode = Locale.getDefault().getCode();
         if (this._cachedLocaleCode != currentCode) {
            this._cachedLocaleCode = currentCode;
            ResourceBundleFamily family = ResourceBundle.getBundle(this._rbId, this._rbName);
            this._emptyString = family.getString(this._rbKey);
         }
      }
   }

   protected ChoiceField(String label, int numChoices, int index) {
      this(label, numChoices, index, 0);
   }

   protected ChoiceField(long style) {
      this(null, 0, 0, style);
   }

   @Override
   public int getPreferredHeight() {
      int height = this.getFont().getHeight();
      return this._isLabelOwnLine ? height * 2 + 2 : Math.max(height, this.getHeightOfChoices());
   }

   protected ChoiceField() {
      this(null, 0, 0, 0);
   }

   @Override
   protected boolean stylusTap(int x, int y, int status, int time) {
      return this.internalChangeOptionDialog();
   }

   private static long verifyStyle(long style) {
      if ((style & 13510798882111488L) == 0) {
         style |= 4503599627370496L;
      }

      if ((style & 54043195528445952L) == 0) {
         style |= 18014398509481984L;
      }

      return style;
   }

   @Override
   public String getAccessibleName() {
      return this._labelText != null ? this._labelText + " " + this.toString() : this.toString();
   }

   @Override
   public String getAccessibleDescription() {
      return null;
   }

   @Override
   public AccessibleText getAccessibleText() {
      return null;
   }

   @Override
   public AccessibleValue getAccessibleValue() {
      return null;
   }

   @Override
   public int getAccessibleStateSet() {
      return super.getAccessibleStateSet();
   }

   @Override
   public boolean isAccessibleStateSet(int state) {
      return (this._accessibleState & state) != 0;
   }

   @Override
   protected void addAccessibleState(int state) {
      if (this.isAccessibleStateSet(1)) {
         this.removeAccessibleState(1);
      }

      this._accessibleState |= state;
   }

   @Override
   protected void removeAccessibleState(int state) {
      this._accessibleState &= ~state;
   }

   @Override
   public int getAccessibleRole() {
      return 13;
   }

   @Override
   public AccessibleContext getAccessibleParent() {
      return this.getScreen();
   }

   @Override
   public int getAccessibleChildCount() {
      return this._numChoices;
   }

   @Override
   public AccessibleContext getAccessibleChildAt(int index) {
      Object temp = this.getChoice(index);
      return temp != null && temp instanceof AccessibleContext ? (AccessibleContext)temp : null;
   }

   @Override
   public String getAccessibleIconDescription() {
      return null;
   }

   @Override
   public int getAccessibleSelectionCount() {
      return 1;
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      if (index == 0) {
         Object temp = this.getChoice(this._selectedIndex);
         if (temp != null && temp instanceof AccessibleContext) {
            return (AccessibleContext)temp;
         }
      }

      return null;
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      return this._selectedIndex == index;
   }
}
