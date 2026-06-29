package net.rim.device.api.ui.container;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.theme.Tag;

public final class Tooltip {
   private int _duration = 2000;
   Field _content;
   XYPoint _calloutPoint;
   final XYPoint _position = new XYPoint();
   private final Tooltip$PopScreenRunnable _popScreenRunnable = new Tooltip$PopScreenRunnable(this);
   private final Tooltip$TooltipScreen _screen = new Tooltip$TooltipScreen(this);
   private static final Tag TAG = Tag.create("tooltip");
   private static final int DEFAULT_TIP_DURATION;
   private static Tooltip _tooltip = new Tooltip();

   private Tooltip() {
   }

   private final void dismiss() {
      this._popScreenRunnable.cancelInvokeLater();
      this._popScreenRunnable.run();
   }

   final XYPoint getPosition() {
      return this._position;
   }

   public static final void init() {
      Ui.addUiEngineListener(new Tooltip$MyUiEngineListener());
   }

   private final void reset() {
      this._duration = 2000;
      this._content = null;
      this._calloutPoint = null;
   }

   public final void setContent(Field field) {
      this._content = field;
   }

   public final void setContent(String text) {
      if (!(this._content instanceof LabelField)) {
         this._content = new LabelField(text);
      } else {
         LabelField field = (LabelField)this._content;
         field.setText(text);
      }
   }

   public final void setDuration(int duration) {
      this._duration = duration;
   }

   public final void setPosition(int x, int y) {
      this._position.set(x, y);
   }

   static final void show(Tooltip$TooltipProvider tooltipProvider) {
      if (_tooltip._screen.isDisplayed()) {
         _tooltip._popScreenRunnable.cancelInvokeLater();
         Application.getApplication().invokeLater(_tooltip._popScreenRunnable);
      }

      _tooltip.reset();
      tooltipProvider.provideTooltip(_tooltip);
      if (_tooltip._content != null) {
         Application.getApplication().invokeLater(new Tooltip$TooltipInvoker(_tooltip));
      }
   }
}
