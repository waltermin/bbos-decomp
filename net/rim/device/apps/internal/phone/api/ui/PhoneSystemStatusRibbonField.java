package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;

public final class PhoneSystemStatusRibbonField extends Manager implements RibbonComponent$RibbonComponentChangeListener {
   private boolean _closed;
   private SimpleRibbonComponent _indicatorField;
   private int _indicatorX1;
   private int _indicatorY1;
   private int _indicatorX2;
   private int _indicatorY2;
   private static Tag TAG = Tag.create("title");
   public static final int TIME_AND_DATE;
   public static final int TIME_ONLY;
   static final int SEPARATOR;
   static final int DATE_TIME;
   static final int SIGNAL_INDICATOR;
   static final int OPERATOR_NAME;
   static final int GPS_MODE;
   static final int NUM_FIELDS;
   static final int OPERATOR_NAME_TOP_SPACE;

   public PhoneSystemStatusRibbonField(FieldProvider ribbonFieldProvider, int flags) {
      super(0);
      this.setTag(TAG);
      this.setId("activecall");
      if (ribbonFieldProvider != null) {
         this.add((Field)(new Object()));
         int ribbonTimeComponent = (flags & 2) != 0 ? 1 : 2;
         Field field = ribbonFieldProvider.getField(new Object(ribbonTimeComponent));
         if (field != null) {
            this.add(field);
         }

         field = ribbonFieldProvider.getField(new Object(0));
         if (field != null) {
            this.add(field);
         }

         this.add(new ActiveScreenONSField());
         field = ribbonFieldProvider.getField(new Object(3));
         if (field != null) {
            this.add(field);
         }

         FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
         Factory factory = repos.getFactory("GridIndicators");
         if (factory != null) {
            RibbonComponent rc = (RibbonComponent)factory.createInstance(null);
            rc.setChangeListener(this);
            this._indicatorField = (SimpleRibbonComponent)rc;
         }
      }
   }

   final void onClose() {
      this._closed = true;
      int count = this.getFieldCount();
      if (count >= 1 && count < 2) {
         this.deleteRange(1, 2);
      }
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (!this._closed) {
         int count = this.getFieldCount();
         this._indicatorX1 = Integer.MAX_VALUE;
         this._indicatorX2 = Integer.MAX_VALUE;

         for (int i = 0; i < count; i++) {
            Field field = this.getField(i);
            int x = 0;
            int y = 0;
            switch (i) {
               case -1:
                  break;
               case 0:
               default:
                  x = 0;
                  y = 0;
                  this._indicatorY1 = y + 1;
                  break;
               case 1:
                  x = 0;
                  y = 3;
                  this._indicatorX1 = field.getPreferredWidth() + 1;
                  break;
               case 2:
                  x = width - field.getPreferredWidth();
                  if (count > 4) {
                     y = (height - this.getField(4).getPreferredHeight() - field.getPreferredHeight()) / 2;
                  } else {
                     y = (height - field.getPreferredHeight()) / 2;
                  }

                  if (x < this._indicatorX2) {
                     this._indicatorX2 = x - 1;
                  }
                  break;
               case 3:
                  x = 0;
                  y = height - field.getPreferredHeight();
                  this._indicatorY2 = y - 1;
                  break;
               case 4:
                  x = width - field.getPreferredWidth();
                  y = height - field.getPreferredHeight();
                  if (x < this._indicatorX2) {
                     this._indicatorX2 = x - 1;
                  }
            }

            if (this._indicatorField != null) {
               this._indicatorField.setDimensionsAvailable(this._indicatorX2 - this._indicatorX1, this._indicatorY2 - this._indicatorY1);
            }

            this.setPositionChild(field, x, y);
            this.layoutChild(field, width, height);
         }

         this.setExtent(width, height);
      }
   }

   @Override
   public final int getPreferredWidth() {
      return Display.getWidth();
   }

   @Override
   public final int getPreferredHeight() {
      if (this._closed) {
         return 0;
      } else {
         return this.getFieldCount() > 3
            ? this.getField(0).getPreferredHeight() + this.getField(1).getPreferredHeight() + this.getField(3).getPreferredHeight() + 2
            : 0;
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      super.paint(graphics);
      if (this._indicatorField != null) {
         this._indicatorField
            .paintComponent(graphics, this._indicatorX1, this._indicatorY1, this._indicatorX2 - this._indicatorX1, this._indicatorY2 - this._indicatorY1, null);
      }
   }

   @Override
   public final void ribbonComponentChanged(RibbonComponent component) {
   }
}
