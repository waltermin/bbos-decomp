package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.explorer.file.ExploreCallback;
import net.rim.device.apps.internal.explorer.file.ExploreManager;
import net.rim.device.apps.internal.explorer.file.FileItemField;
import net.rim.device.apps.internal.explorer.file.FooterField;
import net.rim.device.apps.internal.explorer.file.menu.OptionsMenuItem;

public final class PictureExploreScreen extends AppsMainScreen implements ExploreCallback {
   private Field _banner;
   private ExploreManager _manager;
   private FooterField _thumbInfoField;
   private boolean _statusOn;
   private boolean _thumbInfoEnabled;

   public PictureExploreScreen(Object ctx) {
      super(562949953421312L);
      this.setTag(ThemeUtilities.SCREEN_TAG);
      Manager mainManager = this.getMainManager();
      if (mainManager != null) {
         mainManager.setTag(ThemeUtilities.SCREEN_TAG);
      }

      ContextObject context = ContextObject.castOrCreate(ctx);
      context.setFlag(45);
      this.setContext(context);
      context.putIntegerData(1);
      ContextObject.put(context, 3941043584844673548L, new Object(1));
      this._thumbInfoField = new FooterField();
      this._thumbInfoField.setTag(ThemeUtilities.TEXT_TAG);
      this._manager = new ExploreManager(this, ctx, false, 3459045988797251584L);
      this.addFocusChangeListener(this._manager);
      this._manager.setTag(ThemeUtilities.LIST_TAG);
      ListScrollbarManager scrollManager = new ListScrollbarManager(this._manager);
      this.add(scrollManager);
      scrollManager.setFieldWithFocus(this._manager);
      this.getDelegate().setFieldWithFocus(scrollManager);
      this.setHelp(32247);
   }

   public final ExploreManager getExploreManager() {
      return this._manager;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(new OptionsMenuItem());
      Field field = this.getLeafFieldWithFocus();
      if (field == null) {
         this._manager.addMenuItems(menu, instance);
      }
   }

   @Override
   public final void pathSet(Object path) {
      String title;
      if (!(path instanceof FileItemField)) {
         title = path.toString();
      } else {
         title = ((FileItemField)path).getDisplayName();
      }

      if (this._banner == null) {
         this._banner = ThemeUtilities.getTitleField(title);
         this.setTitle(this._banner);
      } else {
         RibbonBanner.getInstance().setStatusBannerTitle(this._banner, title);
      }
   }

   private final void enableThumbInfo() {
      if (!this._thumbInfoEnabled) {
         this.setStatus(this._thumbInfoField);
         this._thumbInfoEnabled = true;
      }
   }

   @Override
   public final void statusOn() {
      if (!this._statusOn) {
         this.enableThumbInfo();
         this._statusOn = true;
      }
   }

   private final void disableThumbInfo() {
      if (this._thumbInfoEnabled) {
         this.setStatus(null);
         this._thumbInfoEnabled = false;
      }
   }

   @Override
   public final void statusOff() {
      if (this._statusOn) {
         this.disableThumbInfo();
         this._statusOn = false;
      }
   }

   @Override
   public final void currentItemChanged(Field field, FileItemField item) {
      if (item == null || !item.isAlias() && !item.isDirectory()) {
         this._thumbInfoField.setItem(item);
         if (this._statusOn) {
            this.enableThumbInfo();
         }
      } else {
         this.disableThumbInfo();
         this._thumbInfoField.setItem(null);
      }
   }

   @Override
   public final boolean onClose() {
      if (super.onClose()) {
         this._manager.cleanup();
         return true;
      } else {
         return false;
      }
   }
}
