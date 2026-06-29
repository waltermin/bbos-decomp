package net.rim.device.apps.internal.profiles;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.ui.Image;

class ProfilesPopupScreen$ProfilesPopupScreenCache implements GlobalEventListener {
   private final String PHONE_ONLY = "phone only";
   private final String PHONEONLY = "phoneonly";
   private boolean _isThemeDirty;
   private int _size;
   private Bitmap[] _normalBitmaps;
   private Bitmap[] _focusBitmaps;
   private Profiles _profiles;
   public static final long PROFILES_POPUP_SCREEN_CACHE = 4956679396741038789L;

   public Bitmap getIconBitmap(int profileIndex, boolean isFocused) {
      this.validate();
      return !isFocused ? this._normalBitmaps[profileIndex] : this._focusBitmaps[profileIndex];
   }

   public boolean validate() {
      int profilesSize = this._profiles.size();
      if (!this._isThemeDirty && profilesSize == this._size) {
         return false;
      }

      this._size = profilesSize;
      this._normalBitmaps = new Object[this._size];
      this._focusBitmaps = new Object[this._size];
      Theme theme = ThemeManager.getActiveTheme();

      for (int i = 0; i < profilesSize; i++) {
         Profile profile = (Profile)this._profiles.getAt(i);
         String iconBaseName = profile.getIconBaseName();
         if ("phone only".equals(iconBaseName)) {
            iconBaseName = "phoneonly";
         }

         int iconWidth = theme.getRibbonIconWidth();
         int iconHeight = theme.getRibbonIconHeight();
         Image normalImage = theme.getApplicationIcon("net_rim_bb_profiles_app", iconBaseName, 0, iconWidth, null, 0);
         if (normalImage != null) {
            this._normalBitmaps[i] = this.imageToBitmap(normalImage, iconWidth, iconHeight);
         }

         Image focusImage = theme.getApplicationIcon("net_rim_bb_profiles_app", iconBaseName, 6, iconWidth, null, 0);
         if (focusImage != null) {
            this._focusBitmaps[i] = this.imageToBitmap(focusImage, iconWidth, iconHeight);
         }
      }

      this._isThemeDirty = false;
      return true;
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 2573494863350550132L) {
         this._isThemeDirty = true;
      }
   }

   public ProfilesPopupScreen$ProfilesPopupScreenCache() {
      Proxy.getInstance().addGlobalEventListener(this);
      this._profiles = Profiles.getInstance();
   }

   private Bitmap imageToBitmap(Image image, int width, int height) {
      Bitmap bitmap = (Bitmap)(new Object(197, width, height));
      bitmap.createAlpha(2);
      Graphics g = (Graphics)(new Object(bitmap));
      g.setGlobalAlpha(0);
      g.setBackgroundColor(0);
      g.clear();
      g.setGlobalAlpha(255);
      image.paint(g, 0, 0, width, height);
      return bitmap;
   }
}
