package net.rim.device.apps.internal.phone.api.livecall;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.ui.SelfDrawingListField;
import net.rim.device.apps.api.utility.columninfo.ColumnInformation;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;

public final class LiveCallStatusField extends SelfDrawingListField {
   private RIMModel _liveCall;
   private boolean _showTimer;
   private ColumnInformation _columnInformation;
   private int _fontSize = -1;
   private ContextObject _contextObject = (ContextObject)(new Object());
   private int _statusWidth = Display.getWidth();
   private boolean _rightJustified;
   public static final int STATUS_COLUMN = 0;
   public static final int SPACER_COLUMN = 1;
   public static final int TIMER_COLUMN = 2;
   public static final int DEFAULT_NUM_COLUMNS = 3;
   private static final int DEFAULT_PAINTING_FLAGS = 64;
   private static final int DEFAULT_STATUS_WIDTH = 115;
   private static final int DEFAULT_SPACER_WIDTH = 3;
   private static final int DEFAULT_TIMER_WIDTH = 40;

   public LiveCallStatusField(RIMModel liveCall, Object context) {
      super(1, 36028797018963968L);
      if (liveCall == null) {
         throw new Object();
      }

      this._liveCall = liveCall;
      this._showTimer = ContextObject.getFlag(context, 60);
      if (PhoneUtilities.getPrivateFlag(context, 60)) {
         this._rightJustified = true;
      }

      if (PhoneUtilities.getPrivateFlag(context, 13)) {
         this._fontSize = 12;
      } else if (PhoneUtilities.getPrivateFlag(context, 13)) {
         this._fontSize = 8;
      } else {
         this._fontSize = 10;
      }

      Font currentFont = this.getFont();
      int style = PhoneUtilities.getPrivateFlag(context, 5) ? 1 : currentFont.getStyle();
      Font newFont = currentFont.derive(style, this._fontSize);
      this.setFont(newFont);
      this._columnInformation = (ColumnInformation)ContextObject.get(context, 5141706140756983937L);
      if (this._columnInformation == null) {
         if (this._showTimer) {
            this._columnInformation = (ColumnInformation)(new Object(3));
            this._columnInformation.setColumnWidth(0, 115);
            this._columnInformation.setColumnWidth(1, 3);
            this._columnInformation.setColumnWidth(2, 40);
            return;
         }

         this._columnInformation = (ColumnInformation)(new Object(1));
         this._columnInformation.setColumnWidth(0, this._statusWidth);
      }
   }

   @Override
   protected final void layout(int width, int height) {
      super.layout(Math.min(width, this._statusWidth), height);
   }

   private final void paintTimer(Graphics graphics, int y) {
      int columnOffset = this._columnInformation.getColumnOffset(2);
      int columnWidth = this._columnInformation.getColumnWidth(2);
      this._contextObject.reset();
      this._contextObject.setFlag(60);
      Font currentFont = graphics.getFont();
      int paintingHeight = currentFont.getHeight();
      if (this._fontSize != -1) {
         Font fieldFont = this.getFont();
         graphics.setFont(fieldFont);
         paintingHeight = fieldFont.getHeight();
      }

      if (this._liveCall instanceof Object) {
         PaintProvider paintProvider = (PaintProvider)this._liveCall;
         paintProvider.paint(graphics, columnOffset, y, columnWidth, paintingHeight, this._contextObject);
      }

      graphics.setFont(currentFont);
   }

   private final void paintStatus(Graphics graphics, int y) {
      int columnOffset = this._columnInformation.getColumnOffset(0);
      int columnWidth = this._columnInformation.getColumnWidth(0);
      this._contextObject.reset();
      this._contextObject.setFlag(27);
      if (this._rightJustified) {
         PhoneUtilities.setPrivateFlag(this._contextObject, 60);
      }

      Font currentFont = graphics.getFont();
      int paintingHeight = currentFont.getHeight();
      if (this._fontSize != -1) {
         Font fieldFont = this.getFont();
         graphics.setFont(fieldFont);
         paintingHeight = fieldFont.getHeight();
      }

      if (this._liveCall instanceof Object) {
         PaintProvider paintProvider = (PaintProvider)this._liveCall;
         paintProvider.paint(graphics, columnOffset, y, columnWidth, paintingHeight, this._contextObject);
      }

      graphics.setFont(currentFont);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      this.paintStatus(graphics, y);
      if (this._showTimer) {
         this.paintTimer(graphics, y);
      }
   }

   public final void updateTime() {
      this.invalidate();
   }
}
