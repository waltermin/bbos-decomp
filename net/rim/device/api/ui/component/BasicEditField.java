package net.rim.device.api.ui.component;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.AbstractString;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.tid.util.Utils;
import net.rim.vm.WeakReference;

public class BasicEditField extends TextField {
   public static final long FILTER_DEFAULT;
   public static final long FILTER_EMAIL;
   public static final long FILTER_NUMERIC;
   public static final long FILTER_REAL_NUMERIC;
   public static final long FILTER_UPPERCASE;
   public static final long FILTER_LOWERCASE;
   public static final long FILTER_HEXADECIMAL;
   public static final long FILTER_INTEGER;
   public static final long FILTER_PHONE;
   public static final long FILTER_URL;
   public static final long FILTER_PIN_ADDRESS;
   public static final long FILTER_FILENAME;
   private static WeakReference _changeInputLanguageItem = new WeakReference(null);

   public BasicEditField() {
      super(null, null, 1000000, 0);
   }

   public BasicEditField(long style) {
      super(null, null, 1000000, style);
   }

   public BasicEditField(String label, String initialValue) {
      super(label, initialValue, 1000000, 0);
   }

   public BasicEditField(String label, String initialValue, int maxNumChars, long style) {
      super(label, initialValue, maxNumChars, style);
   }

   @Override
   protected boolean isComposedTextExist() {
      return this.getComposedTextEnd() != this.getComposedTextStart();
   }

   @Override
   TextFilter getFilterFromStyle(long style) {
      if ((style & 117440512) != 0) {
         this.setAllowUnicodeInput(false);
      }

      return TextFilter.get((int)(style >> 24 & 31));
   }

   @Override
   protected void onUnfocus() {
      super.onUnfocus();
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      Object changeL = _changeInputLanguageItem.get();
      if (changeL == null) {
         changeL = new BasicEditField$1(this, CommonResource.getBundle(), 10089, 50680656, Integer.MAX_VALUE);
         _changeInputLanguageItem.set(changeL);
      }

      if (Utils.getAvailableInputLocales(false).length > 1 && (this.getStyle() & 117440512) == 0) {
         contextMenu.addItem((MenuItem)changeL);
      }
   }

   @Override
   protected boolean backspace() {
      return super.backspace();
   }

   @Override
   public int backspace(int count) {
      return super.backspace(count);
   }

   @Override
   protected int backspace(int count, int context) {
      return super.backspace(count, context);
   }

   @Override
   public char charAt(int offset) {
      return super.charAt(offset);
   }

   @Override
   public void clear(int context) {
      super.clear(context);
   }

   @Override
   public void wipe() {
      super.wipe();
   }

   @Override
   public void selectionDelete() {
      super.selectionDelete();
   }

   @Override
   protected void displayFieldFullMessage() {
      super.displayFieldFullMessage();
   }

   @Override
   public String getLabel() {
      return super.getLabel();
   }

   @Override
   public int getLabelLength() {
      return super.getLabelLength();
   }

   @Override
   public int getMaxSize() {
      return super.getMaxSize();
   }

   @Override
   public int getPreferredHeight() {
      return super.getPreferredHeight();
   }

   @Override
   public int getPreferredWidth() {
      return super.getPreferredWidth();
   }

   @Override
   public String getText() {
      return super.getText();
   }

   @Override
   public String getText(int offset, int length) {
      return super.getText(offset, length);
   }

   @Override
   public void getText(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      super.getText(srcBegin, srcEnd, dst, dstBegin);
   }

   @Override
   public AbstractString getTextAbstractString() {
      return super.getTextAbstractString();
   }

   @Override
   public int getTextLength() {
      return super.getTextLength();
   }

   @Override
   public int insert(String text) {
      return super.insert(text);
   }

   @Override
   protected int insert(String text, int context) {
      return super.insert(text, context);
   }

   @Override
   protected boolean isSymbolScreenAllowed() {
      return super.isSymbolScreenAllowed();
   }

   @Override
   protected void layout(int width, int height) {
      super.layout(width, height);
   }

   @Override
   protected void paint(Graphics graphics) {
      super.paint(graphics);
   }

   @Override
   public void setLabel(String newLabel) {
      super.setLabel(newLabel);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      return super.keyChar(key, status, time);
   }

   @Override
   protected boolean keyControl(char key, int status, int time) {
      return super.keyControl(key, status, time);
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      return super.keyDown(keycode, time);
   }

   @Override
   protected void update(int delta) {
      super.update(delta);
   }

   @Override
   public void setMaxSize(int maxSize) {
      super.setMaxSize(maxSize);
   }

   @Override
   public void setCursorPosition(int offset) {
      super.setCursorPosition(offset);
   }

   @Override
   protected void setCursorPosition(int offset, int context) {
      super.setCursorPosition(offset, context);
   }

   @Override
   public int getCursorPosition() {
      return super.getCursorPosition();
   }

   @Override
   public boolean paste(Clipboard cb) {
      return super.paste(cb);
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      return super.moveFocus(amount, status, time);
   }

   @Override
   protected void moveFocus(int x, int y, int status, int time) {
      super.moveFocus(x, y, status, time);
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      super.drawFocus(graphics, on);
   }

   @Override
   public void setFilter(TextFilter filter) {
      super.setFilter(filter);
   }

   @Override
   public TextFilter getFilter() {
      return super.getFilter();
   }

   @Override
   public void setText(String text) {
      super.setText(text);
   }

   @Override
   protected void setText(String text, int context) {
      super.setText(text, context);
   }

   @Override
   public void setFont(Font font) {
      super.setFont(font);
   }
}
