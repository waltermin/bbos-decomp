package net.rim.device.apps.internal.alarm;

import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;
import net.rim.device.internal.ui.IconCollection;

public final class AlarmStateField implements SimpleRibbonComponent, RibbonComponent {
   final IconCollection _icons = IconCollection.get("net_rim_Alarm_Indicator", 1);
   private RibbonComponent$RibbonComponentChangeListener _listener;
   private int _width;
   private int _height;
   private AlarmManager _alarmManager;
   static final int ICON_ALARM;

   final synchronized void dataChanged() {
      if (this._listener != null) {
         this._listener.ribbonComponentChanged(this);
      }
   }

   @Override
   public final void applyTheme() {
   }

   @Override
   public final int getComponentHeight() {
      return this._height;
   }

   @Override
   public final int getComponentWidth() {
      return this._alarmManager.getAlarmState() == 0 ? 0 : this._width;
   }

   @Override
   public final void setDimensionsAvailable(int width, int height) {
      this._width = this._icons.getWidth(width, height);
      this._height = this._icons.getHeight(width, height);
   }

   @Override
   public final int paintComponent(Graphics g, int x, int y, int width, int height, Object context) {
      int state = this._alarmManager.getAlarmState();
      if (state != 0) {
         this._icons.paint(g, x, y, this._width, this._height, 0);
      }

      return 0;
   }

   @Override
   public final synchronized void setChangeListener(RibbonComponent$RibbonComponentChangeListener listener) {
      this._listener = listener;
   }

   public AlarmStateField(AlarmManager alarmManager) {
      this._alarmManager = alarmManager;
   }
}
