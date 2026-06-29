package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;

public final class FixedHeightManager extends Manager {
   private Field _field;
   private VerticalFieldManager _vfm;
   private int _height;

   FixedHeightManager(Field field) {
      this(field, 6);
   }

   public FixedHeightManager(Field field, int size) {
      super(0);
      this._field = field;
      this._vfm = (VerticalFieldManager)(new Object(1153220571769602048L));
      this._vfm.add(this._field);
      this.add(this._vfm);
      this._height = Display.getHeight() * size / 10;
   }

   public FixedHeightManager(int size) {
      super(0);
      this._vfm = (VerticalFieldManager)(new Object(1153220571769602048L));
      this.add(this._vfm);
      this._height = Display.getHeight() * size / 10;
   }

   public final void addField(Field field) {
      this._field = field;
      this._vfm.add(this._field);
   }

   @Override
   public final void delete(Field field) {
      try {
         this._vfm.delete(this._field);
      } finally {
         return;
      }
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.layoutChild(this._vfm, width, this._height);
      this.setPositionChild(this._vfm, 0, 0);
      height = this._vfm.getHeight();
      this.setExtent(width, this._height);
   }
}
