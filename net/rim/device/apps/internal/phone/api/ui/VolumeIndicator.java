package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.AudioRouterListener;
import net.rim.device.api.system.TrackwheelListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.IconCollection;
import net.rim.vm.WeakReference;

public final class VolumeIndicator extends Field implements AudioRouterListener {
   private boolean _listening;
   private boolean _volumeChangeSupported = true;
   private AudioRouter _router;
   private TrackwheelListener _trackwheelListener = new VolumeIndicator$1(this);
   private UiApplication _app = UiApplication.getUiApplication();
   private WeakReference _screenRef;
   private static final int ICON_LEVEL_INCREMENTS = 10;
   private static final int NUM_ICONS = 11;
   private static final int DEFAULT_WIDTH = 41;
   private static final int DEFAULT_HEIGHT = 76;
   private static IconCollection _icons = IconCollection.get("net_rim_Phone_Volume", 11);
   private static final int X_LABEL_OFFSET = 1;
   private static final int X_LABEL_IMAGE_BUFFER = 3;

   public final int getImageHeight() {
      return this.getPreferredHeight();
   }

   public final void audioSinkRequested(int sink) {
   }

   @Override
   public final void audioSinkChanged() {
      this._volumeChangeSupported = this._router.isVolumeChangeSupported();
      this.invalidate();
   }

   @Override
   public final void audioSourceChanged() {
   }

   @Override
   public final void audioVolumeChanged(boolean remote) {
      this.invalidate();
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      Screen screen = (Screen)(this._screenRef != null ? this._screenRef.get() : null);
      if (visible) {
         this._volumeChangeSupported = this._router.isVolumeChangeSupported();
         this.onExposed();
         if (!this._listening) {
            if (!Trackball.isSupported()) {
               if (screen != null) {
                  screen.addTrackwheelListener(this._trackwheelListener);
               } else {
                  this._app.addTrackwheelListener(this._trackwheelListener);
               }
            }

            AudioRouter.addListener(Application.getApplication(), this);
            this._listening = true;
            return;
         }
      } else {
         if (!Trackball.isSupported()) {
            if (screen != null) {
               screen.removeTrackwheelListener(this._trackwheelListener);
            } else {
               this._app.removeTrackwheelListener(this._trackwheelListener);
            }
         }

         AudioRouter.removeListener(Application.getApplication(), this);
         this._listening = false;
      }
   }

   public VolumeIndicator() {
      this(false, null);
   }

   @Override
   public final int getPreferredHeight() {
      return getIcons().getHeight(this.getIconWidth(), this.getIconHeight());
   }

   private final int getIconWidth() {
      return getIcons().getWidth(41, 76);
   }

   private final int getIconHeight() {
      return getIcons().getHeight(41, 76);
   }

   @Override
   public final int getPreferredWidth() {
      int w = getIcons().getWidth(this.getIconWidth(), this.getIconHeight());
      String label = this.getLabel();
      if (this.getIconWidth() == this.getIconHeight() && label != null) {
         Font font = Font.getDefault().derive(1, 10, 3);
         int textWidth = font.getBounds(label);
         w += textWidth + 1 + 3;
      }

      return w;
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(this.getPreferredWidth(), this.getPreferredHeight());
   }

   @Override
   public final void paint(Graphics graphics) {
      int x = 0;
      if (this.getIconWidth() == this.getIconHeight()) {
         int y = (this.getPreferredHeight() >> 1) - (graphics.getFont().getHeight() >> 1);
         Font oldFont = graphics.getFont();
         graphics.setFont(Font.getDefault().derive(1, 10, 3));
         x += graphics.drawText(this.getLabel(), 1, y) + 3;
         graphics.setFont(oldFont);
      }

      int index;
      if (this._volumeChangeSupported) {
         index = this._router.getMasterVolume() / 10;
      } else {
         index = 5;
      }

      getIcons().paint(graphics, x, 0, this.getIconWidth(), this.getIconHeight(), index);
   }

   @Override
   public final void onExposed() {
      this.invalidate();
      super.onExposed();
   }

   private static final IconCollection getIcons() {
      if (_icons == null) {
         _icons = IconCollection.get("net_rim_Phone_Volume", 11);
      }

      return _icons;
   }

   public VolumeIndicator(boolean small, Screen screen) {
      if (screen != null) {
         this._screenRef = new WeakReference(screen);
      }

      this._router = AudioRouter.getInstance();
   }

   private final String getLabel() {
      return PhoneResources.getString(207);
   }

   public VolumeIndicator(boolean small) {
      this(small, null);
   }
}
