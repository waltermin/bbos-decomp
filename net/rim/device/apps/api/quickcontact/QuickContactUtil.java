package net.rim.device.apps.api.quickcontact;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.GlyphMetrics;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.UiInternal;
import net.rim.tid.im.layout.DefaultKeyLayout;
import net.rim.tid.im.layout.SLKeyLayout;

public final class QuickContactUtil {
   private static boolean _reducedQWERTYSupport = InternalServices.isReducedFormFactor();
   private static String _validKeys = _reducedQWERTYSupport ? "123456789!?.,@" : "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
   private static final int STRING_TYPE_LABEL = 0;
   private static final int STRING_TYPE_PROMPT = 1;
   private static Tag _hotkeyTag = Tag.create("phone-hotlist-hotkey");
   private static ThemeAttributeSet _hotkeyTheme;
   private static int _themeGeneration;
   private static GlyphMetrics _glyphMetrics = new GlyphMetrics();
   private static Font _keyFont;
   private static Font _fieldFont;
   private static Image _hotkeyIcon = IconCollection.get("net_rim_Phone_SpeedDial", 1).getImage(0);

   public static final boolean fullQWERTYSupport() {
      return !_reducedQWERTYSupport;
   }

   public static final String getValidKeys() {
      return _validKeys;
   }

   public static final String getSpeedDialKeyLabel(char key) {
      return getSpeedDialString(key, 0);
   }

   public static final String getSpeedDialKeyPromptString(char key) {
      return getSpeedDialString(key, 1);
   }

   private static final String getSpeedDialString(char key, int type) {
      int resId = type == 0 ? 9135 : 9123;
      return MessageFormat.format(CommonResources.getString(resId), new String[]{"" + CharacterUtilities.toUpperCase(key, 1701707776)});
   }

   public static final void promptUserForNewQuickContactItem(int keycode) {
      char key = convertKeyForCurrentKeyboard(keycode);
      String prompt = getSpeedDialKeyPromptString(key);
      if (QuickContactPromptDialog.prompt(prompt)) {
         int stringId = 8;
         Verb[] verbs = VerbRepository.getVerbRepository(3391562901592837683L).getVerbs(null);
         Object item = QuickContactList.getInstance().assignNewQuickContactItem(key, CommonResources.getString(stringId), verbs, null);
         if (item != null) {
            QuickContactScreen.show(key);
         }
      }
   }

   public static final char convertKeyForCurrentKeyboard(int keycode) {
      if (!_reducedQWERTYSupport) {
         char key = UiInternal.map(keycode);
         int modifiers = SLKeyLayout.convertStatusToModifiers(Keypad.status(keycode));
         int originalCode = Keypad.getLayout().getOriginalKeyCode(key, modifiers);
         StringBuffer keyChars = DefaultKeyLayout.getDefaultKeyLayout().getKeyChars(originalCode, modifiers);
         return CharacterUtilities.toUpperCase(keyChars.charAt(0), 1701707776);
      } else {
         return UiInternal.mapFromFallbackLayout(Keypad.key(keycode), 1);
      }
   }

   public static final int paintHotkey(char key, Graphics g, int x, int y, int width, int height, long flags, Object context) {
      if (key == 0) {
         return 0;
      }

      Field field = (Field)ContextObject.get(context, 9045827404276417370L);
      Font originalFont = g.getFont();
      int wid = height;
      boolean rightJustified = (flags & 5) != 0;
      int xOffset = rightJustified ? width - wid : 0;
      g.pushContext(xOffset, y, wid, height, 0, 0);
      if (ThemeManager.getGeneration() != _themeGeneration || _fieldFont != originalFont) {
         _themeGeneration = ThemeManager.getGeneration();
         _fieldFont = originalFont;
         _hotkeyTheme = ThemeManager.getActiveTheme().getAttributeSet(_hotkeyTag);
         Font buttonFont = originalFont;
         if (_hotkeyTheme != null) {
            buttonFont = _hotkeyTheme.getFont();
            if (buttonFont == null) {
               buttonFont = originalFont;
            }
         }

         int fHeight = originalFont.getHeight() * 3 / 4;
         if (fHeight < 9) {
            fHeight = 9;
         }

         _keyFont = buttonFont.derive(buttonFont.getStyle(), fHeight);
      }

      ThemeAttributeSet tasSpecial = field.getThemeAttributeSetSpecial();
      field.setThemeAttributesSpecial(_hotkeyTheme, g);
      _hotkeyIcon.paint(g, xOffset, y, height, height);
      g.setFont(_keyFont);
      _keyFont.getGlyphMetrics(key, _glyphMetrics);
      if (rightJustified) {
         g.drawText(key, xOffset - _glyphMetrics.iBearingX, y + (height - _keyFont.getHeight() >> 1), 4, wid);
      } else {
         g.drawText(key, 0, y + (height - _keyFont.getHeight() >> 1), 4, wid);
      }

      g.setFont(originalFont);
      g.popContext();
      field.setThemeAttributesSpecial(tasSpecial, g);
      return wid;
   }

   static final void logEvent(Object data) {
      if (data == null) {
         EventLogger.logEvent(5258489903148434177L, 0);
      } else if (data instanceof byte[]) {
         EventLogger.logEvent(5258489903148434177L, (byte[])data);
      } else {
         if (data instanceof String) {
            EventLogger.logEvent(5258489903148434177L, ((String)data).getBytes());
         }
      }
   }
}
