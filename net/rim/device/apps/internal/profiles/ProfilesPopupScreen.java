package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.ConvenienceKeyOptionsProvider;

public class ProfilesPopupScreen extends PopupScreen implements ListFieldCallback {
   private ResourceBundleFamily _res;
   private boolean _exitWhenDismissed;
   private XYRect _position;
   private CollectionListField _list;
   private int _separation;
   private int _rowHeight;
   private boolean _acceptsForeground;
   private Profiles _profiles;
   private Theme _currentTheme;
   private int _iconWidth;
   private int _iconHeight;
   private Font _font;
   private Font _selectedFont;
   private static Tag TAG_PROFILES_MENU = Tag.create("profiles-menu");
   private static Tag TAG_PROFILES_MENU_SELECTED = Tag.create("profiles-menu-selected");
   private static final String PROFILE_MODULE_NAME = "net_rim_bb_profiles_app";
   private static String _advancedText;
   private static String _activeText;
   private static ProfilesPopupScreen$ProfilesPopupScreenCache _cache;

   public ProfilesPopupScreen(boolean exitWhenDismissed, boolean acceptsForeground) {
      super((Manager)(new Object(299067162755072L)), 0);
      this.setTag(TAG_PROFILES_MENU);
      this.setId("homescreen");
      this._res = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
      _advancedText = this._res.getString(250);
      _activeText = this._res.getString(248);
      this._exitWhenDismissed = exitWhenDismissed;
      this._acceptsForeground = acceptsForeground;
      this._profiles = Profiles.getInstance();
      if (this._list == null) {
         this._list = (CollectionListField)(new Object(this._profiles, this));
         this._list.setExtraRowCount(1);
         this._list.setSelectedIndex(this._profiles.getIndex(this._profiles.getEnabled()));
         this._list.setExtraRowAtBottom(true);
         this._list.addExtraRowName(_advancedText);
      }

      this.add(this._list);
      this._currentTheme = ThemeManager.getActiveTheme();
      this._iconHeight = this._currentTheme.getRibbonIconHeight();
      this._iconWidth = this._currentTheme.getRibbonIconWidth();
      this._position = this._currentTheme.getAttributeSet(TAG_PROFILES_MENU).getPosition();
      this._rowHeight = this._currentTheme.getRibbonIconHeight();
      this._list.setRowHeight(this._rowHeight);
      ThemeAttributeSet themeAttrs = this._currentTheme.getAttributeSet(TAG_PROFILES_MENU_SELECTED);
      if (themeAttrs != null) {
         this._selectedFont = themeAttrs.getFont();
      }

      String value = this._currentTheme.getOption("ProfilesMenuTextDisplacementFromIcon");
      if (value != null) {
         try {
            this._separation = Integer.parseInt(value);
         } finally {
            return;
         }
      }
   }

   static void register(boolean b) {
      ProfilesPopupScreen$ProfilesMenuAction.register(b);
   }

   static void show(boolean exit, boolean acceptForeground) {
      ProfilesPopupScreen screen = new ProfilesPopupScreen(exit, acceptForeground);
      UiApplication.getUiApplication().pushGlobalScreen(screen, 10, 2);
   }

   private void hide() {
      if (this.isDisplayed()) {
         UiApplication.getUiApplication().invokeLater(new ProfilesPopupScreen$1(this));
      }
   }

   public boolean acceptsForeground() {
      return this._acceptsForeground;
   }

   void setAcceptsForeground(boolean acceptsForeground) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public void onUiEngineAttached(boolean attached) {
      if (attached) {
         _cache = (ProfilesPopupScreen$ProfilesPopupScreenCache)ApplicationRegistry.getApplicationRegistry().get(4956679396741038789L);
         if (_cache == null) {
            _cache = new ProfilesPopupScreen$ProfilesPopupScreenCache();
            ApplicationRegistry.getApplicationRegistry().replace(4956679396741038789L, _cache);
         }

         _cache.validate();
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      int current = this._list.getSelectedIndex();
      if (current < Profiles.getInstance().size()) {
         Profile profile = (Profile)this._profiles.getAt(current);
         this._profiles.enable(profile);
         if (this._exitWhenDismissed) {
            System.exit(0);
            return true;
         }
      } else {
         this._acceptsForeground = true;
         UiApplication.getUiApplication().pushScreen(new ProfilesScreen(this._exitWhenDismissed));
         UiApplication.getUiApplication().requestForeground();
         UiApplication.getUiApplication().popScreen(this);
      }

      return true;
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      return this.trackwheelClick(status, time);
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      boolean handled = true;
      if ((key == 19 || key == 21) && this.profilesOwnsConvenienceKey(key)) {
         this.keyChar(' ', 0, time);
         if (this._exitWhenDismissed) {
            System.exit(0);
         }

         return true;
      } else {
         if (key != 27 && key != 4098 && key != 17 && key != 18 && key != 19 && key != 21) {
            return super.keyDown(keycode, time);
         }

         if (key == 17) {
            handled = false;
         }

         this.hide();
         return handled;
      }
   }

   @Override
   protected boolean keyChar(char keyChar, int statusInt, int timeInt) {
      int current = this._list.getSelectedIndex();
      switch (keyChar) {
         case '\n':
         case ' ':
            if (current < Profiles.getInstance().size()) {
               Profile profile = (Profile)this._profiles.getAt(current);
               this._profiles.enable(profile);
               if (this._exitWhenDismissed) {
                  System.exit(0);
               }
            } else {
               this._acceptsForeground = true;
               UiApplication.getUiApplication().pushScreen(new ProfilesScreen(this._exitWhenDismissed));
               UiApplication.getUiApplication().requestForeground();
               UiApplication.getUiApplication().popScreen(this);
            }
            break;
         default:
            this.updateCurrentSelection(keyChar);
      }

      return super.keyChar(keyChar, statusInt, timeInt);
   }

   private void updateCurrentSelection(char key) {
      if (key != 0) {
         StringBuffer keys = Keypad.getLayout().getComplementaryChars(Character.toLowerCase(key), 0);
         if (keys != null) {
            int size = this._list.getSize();
            boolean focusedEntryFound = false;
            int firstMatchingIndex = -1;
            int indexWithFocus = this._list.getSelectedIndex();

            for (int index = 0; index < size; index++) {
               String currentProfileName;
               if (index == size - 1) {
                  currentProfileName = _advancedText;
               } else {
                  Profile currentProfile = (Profile)this._profiles.getAt(index);
                  if (currentProfile != null) {
                     currentProfileName = currentProfile.getName();
                  } else {
                     currentProfileName = null;
                  }
               }

               if (this.startsWith(currentProfileName, keys)) {
                  if (firstMatchingIndex == -1) {
                     firstMatchingIndex = index;
                  }

                  if (focusedEntryFound) {
                     this._list.setSelectedIndex(index);
                     return;
                  }
               }

               if (indexWithFocus == index) {
                  focusedEntryFound = true;
               }
            }

            if (firstMatchingIndex != -1) {
               this._list.setSelectedIndex(firstMatchingIndex);
            }
         }
      }
   }

   private boolean startsWith(String entry, StringBuffer keys) {
      boolean startsWith = false;
      if (entry != null) {
         char charToCheck = Character.toLowerCase(entry.charAt(0));
         int bufferLength = keys.length();

         for (int bufferIndex = 0; bufferIndex < bufferLength; bufferIndex++) {
            if (charToCheck == keys.charAt(bufferIndex)) {
               return true;
            }
         }
      }

      return startsWith;
   }

   @Override
   protected void sublayout(int width, int height) {
      if (this._position == null) {
         super.sublayout(width, height);
      } else {
         this.setPositionDelegate(0, 0);
         this.layoutDelegate(this._position.width, this._position.height);
         this.setPosition(this._position.x, this._position.y);
         this.setExtent(this._position.width, this._position.height);
      }
   }

   @Override
   public boolean dispatchKeyEvent(int event, char key, int keycode, int time) {
      int keypress = Keypad.key(keycode);
      if (keypress == 18 && event == 513) {
         this.hide();
         return false;
      } else {
         return super.dispatchKeyEvent(event, key, keycode, time);
      }
   }

   @Override
   public void drawListRow(ListField listField, Graphics g, int index, int y, int width) {
      this._font = g.getFont();
      Profile profile = null;
      String profileName = "";
      Bitmap iconNormal = null;
      Bitmap iconFocus = null;
      int padding = 0;
      boolean isAdvancedRow = index >= this._profiles.size();
      int rowHeight = listField.getRowHeight();
      if (isAdvancedRow) {
         profileName = _advancedText;
      } else {
         profile = (Profile)this._profiles.getAt(index);
         profileName = profile.getName();
         iconNormal = _cache.getIconBitmap(index, false);
         iconFocus = _cache.getIconBitmap(index, true);
      }

      padding = rowHeight - this._iconHeight >> 1;
      if (padding < 0) {
         padding = 0;
      }

      if (listField.getSelectedIndex() != index) {
         if (iconNormal != null) {
            g.drawBitmap(0, y + padding, this._iconWidth, this._iconHeight, iconNormal, 0, 0);
         }
      } else if (iconFocus != null) {
         g.drawBitmap(0, y + padding, this._iconWidth, this._iconHeight, iconFocus, 0, 0);
      }

      padding = rowHeight - g.getFont().getHeight() >> 1;
      if (padding < 0) {
         padding = 0;
      }

      int profileNameOffset = 0;
      if (iconNormal != null) {
         profileNameOffset = this._iconWidth;
      }

      profileNameOffset += this._separation;
      g.drawText(profileName, profileNameOffset, y + padding, 64, width - this._iconWidth);
      if (profile != null && this._profiles.getEnabled() == profile) {
         if (this._selectedFont != null) {
            g.setFont(this._selectedFont);
         }

         int profileActiveNameOffset = 0;
         profileActiveNameOffset = profileNameOffset;
         profileActiveNameOffset += this._font.getAdvance(profileName);
         profileActiveNameOffset += this._font.getAdvance(' ');
         g.drawText(_activeText, profileActiveNameOffset, y + padding, 64, width - profileActiveNameOffset);
         g.setFont(this._font);
      }

      if (isAdvancedRow) {
         g.setColor(0);
         g.drawRect(0, y, width, 2);
      }
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return this._position.width;
   }

   @Override
   public Object get(ListField listField, int index) {
      return Profiles.getInstance().getAt(index);
   }

   @Override
   public int indexOfList(ListField listField, String d, int h) {
      return this._list.getSelectedIndex();
   }

   private boolean profilesOwnsConvenienceKey(int key) {
      ConvenienceKeyOptionsProvider convKeyProvider = ConvenienceKeyOptionsProvider.getInstance();
      String keyOwner = null;
      if (convKeyProvider != null) {
         switch (key) {
            case 19:
               keyOwner = convKeyProvider.getConvenienceKey1Owner();
               break;
            case 21:
               keyOwner = convKeyProvider.getConvenienceKey2Owner();
               break;
            default:
               return false;
         }
      }

      return keyOwner == null ? key == 21 : keyOwner.startsWith("net_rim_bb_profiles_app");
   }
}
