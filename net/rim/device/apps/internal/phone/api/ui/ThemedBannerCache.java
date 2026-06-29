package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.vm.WeakReference;

public class ThemedBannerCache {
   private WeakReference _themeRef;
   private WeakReference _bannerRef;

   public Field createBanner() {
      throw null;
   }

   public Field getBanner() {
      return this.getBanner(true);
   }

   public Field getBanner(boolean reset) {
      Field banner = this.getCachedBanner();
      if (banner != null) {
         if (reset) {
            this.resetBanner(banner);
            return banner;
         }
      } else {
         banner = this.createBanner();
         this.setCachedBanner(banner);
      }

      return banner;
   }

   private Field getCachedBanner() {
      if (this._bannerRef == null) {
         return null;
      }

      if (ThemeManager.getActiveTheme() != this.getCachedTheme()) {
         return null;
      }

      Field banner = (Field)this._bannerRef.get();
      if (banner == null) {
         return null;
      }

      Manager manager = banner.getManager();
      return manager != null && manager.isValidLayout() ? null : banner;
   }

   private Object getCachedTheme() {
      return this._themeRef != null ? this._themeRef.get() : null;
   }

   private void setCachedTheme(Object theme) {
      if (theme != this.getCachedTheme()) {
         if (theme != null) {
            this._themeRef = new WeakReference(theme);
            return;
         }

         this._themeRef = null;
      }
   }

   private void setCachedBanner(Field banner) {
      this.setCachedTheme(ThemeManager.getActiveTheme());
      if (banner != null) {
         this._bannerRef = new WeakReference(banner);
      } else {
         this._bannerRef = null;
      }
   }

   private void resetBanner(Field banner) {
      Manager manager = banner.getManager();
      if (manager != null) {
         banner.setChangeListener(null);
         banner.setFocusListener(null);
         manager.delete(banner);
      }
   }
}
