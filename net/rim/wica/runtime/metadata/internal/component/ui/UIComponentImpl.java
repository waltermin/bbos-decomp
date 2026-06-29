package net.rim.wica.runtime.metadata.internal.component.ui;

import net.rim.wica.runtime.metadata.component.ui.ScreenModel;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.ui.View;

public class UIComponentImpl implements UIComponent {
   private int _id;
   private int _type;
   private int _style;
   private UIContainer _parent;
   private int _x;
   private int _y;
   private Object _view;
   protected int _bits;
   private ScreenModel _screenModel;
   private int _initId;

   public void clean() {
      this.setView(null);
   }

   public void init() {
      if (this._initId != -1) {
         ((ScreenModelImpl)this.getScreen()).handleEvent(Integer.MAX_VALUE, this._initId);
      }
   }

   protected void reset() {
   }

   public void updateData() {
   }

   public void updateUI() {
   }

   @Override
   public int getId() {
      return this._id;
   }

   @Override
   public int getType() {
      return this._type;
   }

   @Override
   public boolean hasPlacement() {
      return false;
   }

   @Override
   public int getX() {
      return this._x;
   }

   @Override
   public int getY() {
      return this._y;
   }

   @Override
   public int getStyle() {
      return this._style;
   }

   @Override
   public boolean isRepetition() {
      return this.getView() instanceof View[];
   }

   @Override
   public boolean isVisible() {
      if ((this._bits & 2) == 0) {
         return false;
      } else {
         return this._parent != null ? this._parent.isVisible() : true;
      }
   }

   @Override
   public void setVisible(boolean visible) {
      boolean currentlyVisible = this.isVisible();
      if (visible) {
         this._bits |= 2;
      } else {
         this._bits &= -3;
      }

      if (visible != currentlyVisible) {
         ((ScreenModelImpl)this.getScreen()).invalidateUI(false);
      }
   }

   @Override
   public void setView(Object view) {
      this._view = view;
   }

   @Override
   public Object getView() {
      return this._view;
   }

   @Override
   public ScreenModel getScreen() {
      if (this._screenModel == null) {
         this._screenModel = !(this instanceof ScreenModel) ? this._parent.getScreen() : (ScreenModel)this;
      }

      return this._screenModel;
   }

   @Override
   public UIContainer getParent() {
      return this._parent;
   }

   protected UIComponentImpl(int id, int type, UIContainer parent, int style, int bits, int x, int y, int initId) {
      this._id = id;
      this._parent = parent;
      this._type = type;
      this._style = style;
      this._bits = bits;
      this._x = x;
      this._y = y;
      this._initId = initId;
   }

   protected UIComponentImpl(int id, int type, int style, int bits, int x, int y, int initId) {
      this._id = id;
      this._type = type;
      this._style = style;
      this._bits = bits;
      this._x = x;
      this._y = y;
      this._initId = initId;
   }
}
