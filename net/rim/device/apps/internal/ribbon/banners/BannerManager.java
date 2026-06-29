package net.rim.device.apps.internal.ribbon.banners;

import net.rim.device.api.system.Application;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

final class BannerManager implements Runnable, RibbonComponent$RibbonComponentChangeListener {
   private WeakReference[] _apps = new WeakReference[0];
   private Object[] _bannerArrays = new Object[0];
   private boolean[] _updatesPending = new boolean[0];
   private Object[] _appBannerRefs = new Object[0];

   final synchronized void registerBannerForUpdates(BannerField banner) {
      this.cleanWRArrays();
      Application app = Application.getApplication();
      int appPosition = this.findAppPosition(app);
      if (appPosition < 0) {
         appPosition = this.addApp(app);
      }

      WeakReference[] banners = (WeakReference[])this._bannerArrays[appPosition];
      Array.resize(banners, banners.length + 1);
      banners[banners.length - 1] = new WeakReference(banner);
   }

   final synchronized void unregisterBanner(BannerField banner) {
      int appMax = this._apps.length;

      for (int i = 0; i < appMax; i++) {
         WeakReference[] banners = (WeakReference[])this._bannerArrays[i];
         int bannerMax = banners.length;

         for (int j = 0; j < bannerMax; j++) {
            if (banners[j].get() == banner) {
               System.arraycopy(banners, j + 1, banners, j, bannerMax - j - 1);
               Array.resize(banners, banners.length - 1);
               return;
            }
         }
      }

      this.cleanWRArrays();
   }

   private final synchronized void refreshBanners() {
      int appMax = this._apps.length;

      for (int i = 0; i < appMax; i++) {
         Application app = (Application)this._apps[i].get();
         if (app != null && !this._updatesPending[i]) {
            this._updatesPending[i] = true;
            app.invokeLater(this);
         }
      }
   }

   @Override
   public final void ribbonComponentChanged(RibbonComponent component) {
      this.refreshBanners();
   }

   private final synchronized void cleanWRArrays() {
      int appMax = this._apps.length;

      for (int i = 0; i < appMax; i++) {
         if (this._apps[i].get() == null) {
            int srcO = i + 1;
            int count = appMax - srcO;
            System.arraycopy(this._apps, srcO, this._apps, i, count);
            System.arraycopy(this._bannerArrays, srcO, this._bannerArrays, i, count);
            System.arraycopy(this._updatesPending, srcO, this._updatesPending, i, count);
            System.arraycopy(this._appBannerRefs, srcO, this._appBannerRefs, i, count);
            appMax--;
         } else {
            WeakReference[] banners = (WeakReference[])this._bannerArrays[i];
            int bannerMax = banners.length;

            for (int j = 0; j < bannerMax; j++) {
               if (banners[j].get() == null) {
                  System.arraycopy(banners, j + 1, banners, j, bannerMax - j - 1);
                  bannerMax--;
               }
            }

            if (bannerMax == 0) {
               int srcO = i + 1;
               int count = appMax - srcO;
               System.arraycopy(this._apps, srcO, this._apps, i, count);
               System.arraycopy(this._bannerArrays, srcO, this._bannerArrays, i, count);
               System.arraycopy(this._updatesPending, srcO, this._updatesPending, i, count);
               System.arraycopy(this._appBannerRefs, srcO, this._appBannerRefs, i, count);
               appMax--;
            } else if (bannerMax != banners.length) {
               Array.resize(banners, bannerMax);
            }
         }
      }

      if (appMax != this._apps.length) {
         Array.resize(this._apps, appMax);
         Array.resize(this._bannerArrays, appMax);
         Array.resize(this._updatesPending, appMax);
         Array.resize(this._appBannerRefs, appMax);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      WeakReference[] banners = null;
      Application app = Application.getApplication();
      BannerField[] appBanners = null;
      this.cleanWRArrays();
      synchronized (this) {
         int max = this._apps.length;

         for (int i = 0; i < max; i++) {
            if (app == this._apps[i].get()) {
               this._updatesPending[i] = false;
               banners = (WeakReference[])this._bannerArrays[i];
               appBanners = (BannerField[])this._appBannerRefs[i];
               break;
            }
         }

         if (banners == null) {
            return;
         }

         max = banners.length;
         Array.resize(appBanners, max);

         for (int var17 = 0; var17 < max; var17++) {
            appBanners[var17] = (BannerField)banners[var17].get();
         }
      }

      if (appBanners != null) {
         boolean var12 = false /* VF: Semaphore variable */;

         try {
            var12 = true;

            for (BannerField banner : appBanners) {
               if (banner != null) {
                  banner.bannerInvalidate();
               }
            }

            var12 = false;
         } finally {
            if (var12) {
               if (appBanners != null) {
                  Array.resize(appBanners, 0);
               }
            }
         }

         if (appBanners != null) {
            Array.resize(appBanners, 0);
         }
      }
   }

   private final int findAppPosition(Application app) {
      int max = this._apps.length;

      for (int i = 0; i < max; i++) {
         if (this._apps[i].get() == app) {
            return i;
         }
      }

      return -1;
   }

   private final int addApp(Application app) {
      int newlength = this._apps.length + 1;
      int length = this._apps.length;
      Array.resize(this._apps, newlength);
      this._apps[length] = new WeakReference(app);
      Array.resize(this._bannerArrays, newlength);
      this._bannerArrays[length] = new WeakReference[0];
      Array.resize(this._updatesPending, newlength);
      this._updatesPending[length] = false;
      Array.resize(this._appBannerRefs, newlength);
      this._appBannerRefs[length] = new BannerField[0];
      return length;
   }
}
