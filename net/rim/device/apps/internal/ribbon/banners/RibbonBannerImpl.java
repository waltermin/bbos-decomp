package net.rim.device.apps.internal.ribbon.banners;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ribbon.RibbonComponentIDs;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;
import net.rim.device.apps.api.utility.props.StringProps;
import net.rim.device.apps.internal.ribbon.components.TitleFieldFactory;

public final class RibbonBannerImpl extends RibbonBanner {
   private boolean _compressedBanners = Display.getHeight() < 160;
   private BannerManager _bannerManager = new BannerManager();
   private SimpleRibbonComponent _bannerStandard;
   private static final int NORMAL_BANNERS_SCREEN_HEIGHT = 160;

   public static final void init() {
      RibbonBannerImpl instance = new RibbonBannerImpl();
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(-6860171651452498676L, instance);
   }

   private RibbonBannerImpl() {
   }

   @Override
   public final Field getRibbonBanner() {
      return this.getStatusBanner(null, 1);
   }

   final Field getSkinnedBanner(Tag tag, String id, String title) {
      Theme theme = ThemeManager.getActiveTheme();
      if (theme == null) {
         return null;
      }

      ThemeAttributeSet attributes = theme.getAttributeSet(tag, id, 0);
      if (attributes == null) {
         return null;
      }

      ContextObject context = new ContextObject();
      ContextObject.put(context, 265370977573465368L, new Long(524288));
      Field field = attributes.getLayout(context);
      if (field instanceof StringProps) {
         StringProps props = (StringProps)field;
         props.set("title".hashCode(), title);
      }

      return field;
   }

   final synchronized SimpleRibbonComponent getStandardRibbonBannerPaintable() {
      if (this._bannerStandard == null) {
         this._bannerStandard = new StandardRibbonBannerPaintable();
      }

      return this._bannerStandard;
   }

   @Override
   public final synchronized Field getStatusBanner(String title, int style) {
      Field banner = null;
      if (title != null) {
         title = title.trim();
         if (title.length() == 0) {
            title = null;
         }
      }

      switch (style) {
         case 0:
            throw new IllegalArgumentException();
         case 1:
         default:
            banner = new StandardRibbonBannerField(this._bannerManager, title, false);
            break;
         case 2:
            banner = new CompressedRibbonBannerField(this._bannerManager, title);
            break;
         case 3:
            if (this._compressedBanners) {
               banner = new CompressedRibbonBannerField(this._bannerManager, title);
            } else {
               banner = new StandardRibbonBannerField(this._bannerManager, title, false);
            }
            break;
         case 4:
            banner = new PaintableField(new SignalBatteryRibbonBannerField(this._bannerManager));
            break;
         case 5:
            banner = new PaintableField(new TimeDateRibbonBannerField(this._bannerManager, true, true));
            break;
         case 6:
            banner = new PaintableField(new TimeDateRibbonBannerField(this._bannerManager, true, false));
            break;
         case 7:
            banner = new PaintableField(new TimeDateRibbonBannerField(this._bannerManager, false, true));
            break;
         case 8:
            banner = new CompressedSignalAndIndicatorsRibbonBannerField(this._bannerManager, title);
            break;
         case 9:
            Factory factory = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L).getFactory(RibbonComponentIDs.GPS_MODE_NAME);
            if (factory != null) {
               banner = new PaintableField((SimpleRibbonComponent)factory.createInstance(null));
            }
            break;
         case 10:
            banner = new StandardRibbonBannerField(this._bannerManager, title, true);
            break;
         case 11:
            if (this._compressedBanners) {
               banner = new CompressedRibbonBannerField(this._bannerManager, title);
            } else {
               banner = new StandardRibbonBannerField(this._bannerManager, title, true);
            }
      }

      if (banner != null) {
         this._bannerManager.registerBannerForUpdates((BannerField)banner);
      }

      return banner;
   }

   @Override
   public final void unregisterBanner(Field banner) {
      if (banner instanceof BannerField) {
         BannerField rb = (BannerField)banner;
         this._bannerManager.unregisterBanner(rb);
      }
   }

   @Override
   public final void setStatusBannerTitle(Field field, String title) {
      if (title != null) {
         title = title.trim();
         if (title.length() == 0) {
            title = null;
         }
      }

      if (!(field instanceof StringProps)) {
         if (field instanceof BannerField) {
            BannerField rb = (BannerField)field;
            rb.setTitle(title);
         }
      } else {
         StringProps props = (StringProps)field;
         props.set(TitleFieldFactory.TITLE_ID, title);
      }
   }

   @Override
   public final String getStatusBannerTitle(Field field) {
      if (!(field instanceof BannerField)) {
         return null;
      }

      BannerField rb = (BannerField)field;
      return rb.getTitle();
   }
}
