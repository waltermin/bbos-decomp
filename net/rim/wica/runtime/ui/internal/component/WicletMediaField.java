package net.rim.wica.runtime.ui.internal.component;

import java.util.Vector;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MediaField;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import net.rim.plazmic.mediaengine.MediaManager;
import net.rim.plazmic.mediaengine.MediaPlayer;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.control.ImageControl;
import net.rim.wica.runtime.persistence.Resource;
import net.rim.wica.runtime.ui.View;
import net.rim.wica.runtime.ui.internal.Focusable;
import net.rim.wica.runtime.ui.internal.ResourceListener;
import net.rim.wica.runtime.ui.internal.ResourceProvider;

final class WicletMediaField extends MediaField implements View, Focusable, ResourceListener, HolsterListener {
   private ScreenContext _context;
   private MediaManager _manager;
   private MediaPlayer _player;
   private Object _media;
   private Resource _resource;
   private long _mediaTime;
   private int _numTimesStopped;
   private UiApplication _app;
   private int _row = 0;
   private byte _visibility;
   private ImageControl _model;
   private boolean _focusable;

   WicletMediaField(ScreenContext context, ImageControl model, int row, long style) {
      super(style);
      this._context = context;
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
      this._row = row;
      this._player = (MediaPlayer)(new Object());
      this._manager = (MediaManager)(new Object());
      this._app = UiApplication.getUiApplication();
   }

   @Override
   final void startPlayer() {
      this._numTimesStopped--;
      if (this._numTimesStopped < 0) {
         this._numTimesStopped = 0;
      }

      try {
         if (this._player.getState() != 2 && this._numTimesStopped == 0) {
            this._player.start();
            return;
         }
      } finally {
         return;
      }
   }

   @Override
   final void stopPlayer() {
      this._numTimesStopped++;
      if (this._player.getState() == 2) {
         this._player.stop();
      }
   }

   @Override
   public final void onDisplay() {
      super.onDisplay();
      this._player.setUI(this);
      this.setMedia();
      this.reloadPlayer();
      this._app.addHolsterListener(this);
   }

   @Override
   public final void onUndisplay() {
      super.onUndisplay();
      this._mediaTime = this._player.getMediaTime();
      this._player.close();
      this._app.removeHolsterListener(this);
   }

   @Override
   public final void onExposed() {
      this.startPlayer();
      super.onExposed();
   }

   @Override
   public final void onObscured() {
      this.stopPlayer();
      super.onObscured();
   }

   @Override
   public final boolean isFocusable() {
      return this._visibility == 0 ? this._focusable : false;
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (this._visibility != 1) {
         super.sublayout(width, height);
      } else {
         this.setVirtualExtent(0, 0);
         this.setExtent(0, 0);
      }
   }

   @Override
   protected final void subpaint(Graphics graphics) {
      if (this._visibility == 0) {
         super.subpaint(graphics);
      }
   }

   @Override
   public final UIComponent getModel() {
      return this._model;
   }

   @Override
   public final void setModel(UIComponent model) {
      this._model = (ImageControl)model;
   }

   @Override
   public final void setVisibility(byte visibility) {
      if (visibility != this._visibility) {
         this._visibility = visibility;
         if (this._visibility != 0 && this.isFocus()) {
            this.onUnfocus();
         }

         if (this._visibility != 2) {
            this.updateLayout();
            return;
         }

         this.invalidate();
      }
   }

   @Override
   public final void update(int row) {
      this._row = row;
      this.setVisibility((byte)(this._model.isVisible() ? 0 : 1));
   }

   @Override
   public final int moveFocus(int amount, int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final void resourceRetrieved(Resource resource) {
      this.setResource(resource);
      this.reloadPlayer();
   }

   @Override
   public final void inHolster() {
      this.stopPlayer();
   }

   @Override
   public final void outOfHolster() {
      this.startPlayer();
   }

   @Override
   public final int getPreferredWidth() {
      this.layout(Integer.MAX_VALUE, Integer.MAX_VALUE);
      return this.getWidth();
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      int styleId = this._model.getStyle();
      if (styleId != -1) {
         ThemeAttributeSet attributes = this._context.getStyleFactory().getStyle(styleId);
         this.setThemeAttributesSpecial(attributes, null);
      }

      super.applyFont();
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (visible) {
         this.startPlayer();
      } else {
         this.stopPlayer();
      }

      super.onVisibilityChange(visible);
   }

   private final void setMedia() {
      Object value = this._model.getValue();
      String uri = null;
      if (!(value instanceof Object)) {
         uri = (String)value;
      } else {
         uri = (String)((Vector)value).elementAt(this._row);
      }

      Resource resource = null;
      if (uri != null) {
         resource = this._context.getResourceProvider().getResource(uri, this);
      } else if (this._model.getResourceId() != -1) {
         resource = this._context.getResourceProvider().getApplicationResource(this._model.getResourceId());
      }

      if (resource != null) {
         this.setResource(resource);
      } else {
         try {
            this._media = this._manager.createResource("application/x-vnd.rim.pme.b", ResourceProvider.getDefaultMediaContent(), null, null);
         } finally {
            return;
         }
      }
   }

   private final void setResource(Resource resource) {
      if (resource != null && resource != this._resource) {
         this._resource = resource;

         try {
            this._media = this._manager.createResource(resource.getContentType(), resource.getData(), null, null);
         } finally {
            try {
               this._media = this._manager.createResource("application/x-vnd.rim.pme.b", ResourceProvider.getDefaultMediaContent(), null, null);
               return;
            } finally {
               return;
            }
         }
      }
   }

   private final void reloadPlayer() {
      this.stopPlayer();

      try {
         this._player.setMedia(this._media);
         FocusInteractor fi = (FocusInteractor)super._services.getService("FocusInteractor");
         if (fi.getItemCount() > 0) {
            this._focusable = true;
         } else {
            this._focusable = false;
         }

         this._player.setMediaTime(this._mediaTime);
         if (!DeviceInfo.isInHolster()) {
            this.startPlayer();
            return;
         }
      } finally {
         return;
      }
   }
}
