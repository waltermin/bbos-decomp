package net.rim.device.apps.internal.qm.peer;

import java.util.Hashtable;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.AbstractStringWrapper;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.EmoticonStringPattern;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentIDs;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;
import net.rim.device.apps.internal.smileys.Smileys;
import net.rim.vm.Array;

class TitleField extends Field implements RibbonComponent$RibbonComponentChangeListener {
   private Object _title;
   private SimpleRibbonComponent _indicatorField;
   private SimpleRibbonComponent _signalField;
   private int _indicatorHeight;
   private int _contentHeight;
   private int _x1;
   private TitleField$RedrawRunnable _redrawRunnable = new TitleField$RedrawRunnable(this);
   private Application _application;
   private String _smileysTitle;
   private int[] _smileyOffsets = new int[0];
   private long[] _smileyIds = new long[0];
   private static Hashtable _indicatorParameters = (Hashtable)(new Object());
   private static Hashtable _signalParameters = (Hashtable)(new Object());
   private static ThemeAttributeSet _signalAttributes;
   private static final int COMPONENT_GAP;
   static EmoticonStringPattern _smileyFacility = Smileys.getSmileyFacility();
   private static final char PLACEHOLDER;

   int drawIcon(Graphics graphics, int x, int y) {
      return 0;
   }

   int getIconHeight() {
      return 0;
   }

   public void onFontChanged() {
      this.updateLayout();
   }

   public void setTitle(String title) {
      if (title != null) {
         title = title.trim();
         if (title.length() == 0) {
            title = null;
         }
      }

      this._title = PersistentContent.encode(title, true, true);
      this._smileysTitle = null;
   }

   void lock() {
      this._title = PersistentContent.reEncode(this._title, true, true);
      this._smileysTitle = null;
   }

   int drawTitle(Graphics graphics, int x, int text_y, int smileys_y, int width) {
      this.smileysScan();
      int runner = x;
      int offset = 0;
      int smileysLen = this._smileyOffsets.length;
      int emoticonWidth = _smileyFacility.emoticonSize();

      for (int i = 0; i < smileysLen && runner < width; i++) {
         runner += graphics.drawText(this._smileysTitle, offset, this._smileyOffsets[i] - offset, runner, text_y, 70, width - runner);
         if (runner + emoticonWidth >= width) {
            runner += graphics.drawText('…', runner, text_y, 0, -1);
            return runner - x;
         }

         _smileyFacility.drawEmoticon(graphics, (int)this._smileyIds[i], runner + 1, smileys_y);
         runner += emoticonWidth;
         offset = this._smileyOffsets[i] + 1;
      }

      if (offset != this._smileysTitle.length() && runner < width) {
         runner += graphics.drawText(this._smileysTitle, offset, this._smileysTitle.length() - offset, runner, text_y, 70, width - runner);
      }

      return runner - x;
   }

   @Override
   public void ribbonComponentChanged(RibbonComponent component) {
      if (this._application.isForeground()) {
         synchronized (this._redrawRunnable) {
            if (TitleField$RedrawRunnable.access$000(this._redrawRunnable)) {
               return;
            }

            TitleField$RedrawRunnable.access$002(this._redrawRunnable, true);
         }

         PeerApplication.getInstance().postInvokeLaterInternal(this._redrawRunnable);
      }
   }

   @Override
   public int getPreferredWidth() {
      return Display.getWidth();
   }

   @Override
   public int getPreferredHeight() {
      int signalFieldHeight = this._signalField != null ? this._signalField.getComponentHeight() : 0;
      this._contentHeight = Math.max(this._indicatorField.getComponentHeight(), signalFieldHeight);
      this._contentHeight = Math.max(this._contentHeight, this.getFont().getHeight());
      return Math.max(Math.max(this._contentHeight, this.getIconHeight()), _smileyFacility.emoticonSize());
   }

   @Override
   protected void layout(int width, int height) {
      int fieldWidth = Math.min(width, this.getPreferredWidth());
      int fieldHeight = Math.min(height, this.getPreferredHeight());
      if (height >= fieldHeight) {
         int widthAvailable = fieldWidth;
         int signalWidth = 0;
         if (this._signalField != null) {
            signalWidth = this._signalField.getComponentWidth();
         }

         this._x1 = widthAvailable - signalWidth;
         this.setExtent(fieldWidth, fieldHeight + 1);
      } else {
         this.setExtent(fieldWidth, 0);
      }
   }

   @Override
   protected void paint(Graphics graphics) {
      graphics.pushRegion(0, 0, 1073741823, 1073741823, 0, 0);
      int heightAvailable = this.getPreferredHeight();
      int y1 = Math.max(0, heightAvailable - this._indicatorHeight >> 1);
      if (this._signalField != null) {
         this._signalField.paintComponent(graphics, this._x1, y1, this._signalField.getComponentWidth(), heightAvailable, null);
      }

      int remaining = this._x1 - 2;
      int indicatorWidth = remaining;
      int indicatorX = 0;
      this._indicatorField.setDimensionsAvailable(indicatorWidth, this._indicatorHeight > heightAvailable ? heightAvailable : this._indicatorHeight);
      this._indicatorField
         .paintComponent(
            graphics, indicatorX, y1, indicatorWidth, this._indicatorHeight > heightAvailable ? heightAvailable + 1 : this._indicatorHeight + 1, null
         );
      graphics.popContext();
      remaining -= this._indicatorField.getComponentWidth() + 2;
      int fontHeight = this.getFont().getHeight();
      int iconHeight = this.getIconHeight();
      int icon_y = Math.max(0, heightAvailable - iconHeight >> 1);
      int indent = fontHeight >> 2;
      int x = indent;
      x += this.drawIcon(graphics, x, icon_y);
      if (x != indent) {
         x += indent;
      }

      int text_y = Math.max(0, heightAvailable - fontHeight >> 1);
      int smileys_y = Math.max(0, heightAvailable - _smileyFacility.emoticonSize() >> 1);
      if (this._title != null && remaining > 0) {
         this.drawTitle(graphics, x, text_y, smileys_y, remaining);
      }

      if (!Graphics.isColor()) {
         XYRect extent = this.getExtent();
         graphics.drawLine(x, heightAvailable, extent.width - x, heightAvailable);
      }
   }

   private String getTitle() {
      try {
         return PersistentContent.decodeString(this._title);
      } finally {
         ;
      }
   }

   TitleField(boolean showSignalField) {
      this._application = PeerApplication.getInstance();
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      Factory factory = repos.getFactory(RibbonComponentIDs.SIGNAL_NAME);
      RibbonComponent component = (RibbonComponent)factory.createInstance(null);
      if (showSignalField) {
         RibbonComponentInitializer rci = (RibbonComponentInitializer)component;
         rci.initialize(_signalParameters, _signalAttributes);
         this._signalField = (SimpleRibbonComponent)component;
      }

      factory = repos.getFactory(RibbonComponentIDs.HORIZ_INDICATOR_NAME);
      component = (RibbonComponent)factory.createInstance(null);
      this._indicatorField = (SimpleRibbonComponent)component;
      ((RibbonComponentInitializer)this._indicatorField).initialize(_indicatorParameters, null);
      if (this._signalField != null) {
         this._indicatorHeight = this._signalField.getComponentHeight();
      }

      if (this._indicatorField != null) {
         this._indicatorHeight = Math.max(this._indicatorHeight, this._indicatorField.getComponentHeight());
      }

      this.onDisplay();
   }

   TitleField(String title, boolean showSignalField) {
      this(showSignalField);
      this._title = PersistentContent.encode(title, true, true);
   }

   @Override
   public void onDisplay() {
      this._indicatorField.setChangeListener(this);
      if (this._signalField != null) {
         this._signalField.setChangeListener(this);
      }
   }

   @Override
   public boolean isFocusable() {
      return false;
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      this.updateLayout();
      RibbonComponentInitializer rci = (RibbonComponentInitializer)this._indicatorField;
      rci.initialize(_indicatorParameters, null);
      this._indicatorField.applyTheme();
   }

   private void smileysScan() {
      if (this._smileysTitle == null) {
         Array.resize(this._smileyOffsets, 0);
         Array.resize(this._smileyIds, 0);
         String text = this.getTitle();
         if (text != null) {
            char[] text2 = new char[text.length()];
            StringPattern$Match match = (StringPattern$Match)(new Object());
            match.endIndex = 0;
            int pos = 0;
            int length = text.length();
            int dstIndex = 0;
            if (_smileyFacility != null) {
               for (AbstractString abstractString = AbstractStringWrapper.createInstance(text);
                  _smileyFacility.findMatch(abstractString, pos, length, match);
                  pos = match.endIndex
               ) {
                  if (match.beginIndex != pos) {
                     text.getChars(pos, match.beginIndex, text2, dstIndex);
                     dstIndex += match.beginIndex - pos;
                  }

                  Arrays.add(this._smileyOffsets, dstIndex);
                  Arrays.add(this._smileyIds, match.id);
                  text2[dstIndex++] = '￼';
               }
            }

            if (pos < length) {
               text.getChars(pos, length, text2, dstIndex);
               dstIndex += length - pos;
            }

            Array.resize(text2, dstIndex);
            this._smileysTitle = (String)(new Object(text2));
         }
      }
   }

   static void access$100(TitleField x0) {
      x0.invalidate();
   }

   static {
      _indicatorParameters.put("align", "right");
      _indicatorParameters.put("reportUsed", "reportUsed");
      Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
      ThemeAttributeSet$Writer attributesWriter = themeWriter.createThemeAttributeSetWriter(null);
      attributesWriter.setFontFamily(FontFamily.FAMILY_SYSTEM);
      attributesWriter.setFontStyle(0);
      attributesWriter.setFontSize(1000, 4194306, false);
      _signalAttributes = attributesWriter.getThemeAttributeSet();
      _signalAttributes.apply();
      _signalParameters.put("xOfs", "10");
      _signalParameters.put("yOfs", "2");
      _signalParameters.put("align", "right");
      _signalParameters.put("icon", "net_rim_Browser_SignalLevel");
   }
}
