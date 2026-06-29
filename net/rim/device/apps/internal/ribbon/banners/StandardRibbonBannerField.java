package net.rim.device.apps.internal.ribbon.banners;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;
import net.rim.device.apps.api.utility.props.StringProps;

final class StandardRibbonBannerField extends Manager implements BannerField, RibbonComponent$RibbonComponentChangeListener {
   private boolean _compressedBanners = Display.getHeight() < 160;
   private String _title;
   private int _themeGeneration;
   private Field _bannerField;
   private SimpleRibbonComponent _banner;
   private SeparatorField _separator;
   private int _width = -1;
   private int _height = -1;
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private boolean _twoLine;
   public static final int SIZE_DONT_CARE = -1;
   private static final int SMALL_SCREEN_THRESHOLD = 160;
   private static final int BANNER_HEIGHT = Display.getHeight() < 160 ? 34 : 35;
   private static final int BANNER_WITH_SEPARATOR_HEIGHT = Display.getHeight() < 160 ? 35 : 38;
   private static final int TITLE_HEIGHT = 8;
   private static final int NORMAL_BANNERS_SCREEN_HEIGHT = 160;

   StandardRibbonBannerField(RibbonComponent$RibbonComponentChangeListener listener, String title, boolean twoLine) {
      super(0);
      this._twoLine = twoLine;
      this.setTag(Tag.create(this._twoLine ? "twolinebanner" : "banner"));
      this._listener = listener;
      this.setTitle(title);
   }

   @Override
   protected final void applyTheme() {
      int themeGeneration = ThemeManager.getGeneration();
      if (this._themeGeneration != themeGeneration) {
         this._themeGeneration = themeGeneration;
         super.applyTheme();
         RibbonBannerImpl bannerImpl = (RibbonBannerImpl)RibbonBanner.getInstance();
         if (this._bannerField != null) {
            Field tmp = this._bannerField;
            this._bannerField = null;
            this.delete(tmp);
         }

         if (this._separator != null) {
            Field tmp = this._separator;
            this._separator = null;
            this.delete(tmp);
         }

         this._banner = null;
         this._bannerField = bannerImpl.getSkinnedBanner(this.getTag(), this.getId(), this._title);
         if (this._bannerField == null) {
            this._banner = bannerImpl.getStandardRibbonBannerPaintable();
            this._banner.setChangeListener(this);
            this._banner.applyTheme();
            if (this._compressedBanners) {
               label49:
               try {
                  this._separator = new SeparatorField(1152921504615235584L);
                  this._separator.setFont(FontFamily.forName(FontFamily.FAMILY_SYSTEM).getFont(0, 8));
               } finally {
                  break label49;
               }
            } else {
               this._separator = new SeparatorField(1152921504606846976L);
            }

            this._separator.setId(this._twoLine ? "twolinebanner" : "banner");
            this.add(this._separator);
         } else {
            this.insert(this._bannerField, 0);
         }

         this.setTitle(this._title);
      }
   }

   @Override
   public final int getPreferredWidth() {
      return this._width != -1 ? this._width : Display.getWidth();
   }

   @Override
   public final int getPreferredHeight() {
      if (this._height != -1) {
         return this._height;
      } else {
         return this._bannerField != null ? this._bannerField.getPreferredHeight() : BANNER_WITH_SEPARATOR_HEIGHT;
      }
   }

   @Override
   protected final void sublayout(int width, int height) {
      int preferredWidth = this.getPreferredWidth();
      int preferredHeight = this.getPreferredHeight();
      if (this._banner != null) {
         this._banner.setDimensionsAvailable(preferredWidth, BANNER_HEIGHT);
         if (this._separator != null) {
            this.layoutChild(this._separator, width, height);
            this.setPositionChild(this._separator, 0, BANNER_HEIGHT);
         }
      } else if (this._bannerField != null) {
         this.layoutChild(this._bannerField, preferredWidth, preferredHeight);
      }

      this.setExtent(preferredWidth, preferredHeight);
      this.setVirtualExtent(preferredWidth, preferredHeight);
   }

   @Override
   protected final void subpaint(Graphics g) {
      try {
         super.subpaint(g);
         if (this._banner != null) {
            this._banner.paintComponent(g, 0, 0, this.getWidth(), this._banner.getComponentHeight(), this.getTitle());
            return;
         }
      } finally {
         return;
      }
   }

   @Override
   public final void setTitle(String title) {
      this._title = title;
      if (this._bannerField instanceof StringProps) {
         StringProps props = (StringProps)this._bannerField;
         props.set("title".hashCode(), title);
      }

      this.ribbonComponentChanged(null);
   }

   @Override
   public final String getTitle() {
      return this._title;
   }

   @Override
   public final void ribbonComponentChanged(RibbonComponent component) {
      this._listener.ribbonComponentChanged(null);
   }

   @Override
   public final void bannerInvalidate() {
      Screen screen = this.getScreen();
      if (screen != null && screen.isVisible()) {
         this.invalidate();
      }
   }
}
