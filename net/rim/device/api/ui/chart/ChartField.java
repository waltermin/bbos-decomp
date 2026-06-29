package net.rim.device.api.ui.chart;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.Arrays;

public class ChartField extends Field {
   private ResourceBundle _resources = ResourceBundle.getBundle(3711053710409943671L, "net.rim.device.internal.resource.UI");
   private Axis[] _axes = new Axis[4];
   private ChartRenderer[] _renderers = new ChartRenderer[0];
   private int _preferredWidth;
   private int _preferredHeight;
   private int[] _colors;
   private int _focusX;
   private int _focusY;
   private Font _previousFont;
   private boolean _needsSpaceCalculations = true;
   private static final int STATUS_MOVE_FOCUS_MASK;
   public static final int AXIS_TOP;
   public static final int AXIS_RIGHT;
   public static final int AXIS_BOTTOM;
   public static final int AXIS_LEFT;

   public ChartField() {
      this(0);
   }

   public ChartField(long style) {
      super(style);
      this._colors = new int[0];
   }

   public void addRenderer(ChartRenderer renderer) {
      Arrays.add(this._renderers, renderer);
   }

   protected static void assertLegal(boolean condition) {
      if (!condition) {
         throw new IllegalArgumentException();
      }
   }

   private void doSpaceCalculations() {
      Font currentFont = this.getFont();
      if (this._previousFont != currentFont || this._needsSpaceCalculations) {
         for (int index = 0; index < this._axes.length; index++) {
            if (this._axes[index] != null) {
               this._axes[index].calculateWidth();
            }
         }

         int plotX = this._axes[3] == null ? 0 : this._axes[3].getPreferredWidth();
         int plotY = this._axes[0] == null ? 0 : this._axes[0].getPreferredHeight();
         int plotWidth = 0;
         int plotHeight = 0;

         for (int index = this._renderers.length - 1; index >= 0; index--) {
            plotWidth = Math.max(plotWidth, this._renderers[index].getPreferredWidth());
            plotHeight = Math.max(plotHeight, this._renderers[index].getPreferredHeight());
         }

         for (int index = this._renderers.length - 1; index >= 0; index--) {
            this._renderers[index].layout(plotWidth, plotHeight);
            this._renderers[index].setPosition(plotX, plotY);
         }

         Axis axis = this._axes[0];
         if (axis != null) {
            axis.layout(plotWidth, axis.getPreferredHeight());
            axis.setPosition(plotX, 0);
         }

         axis = this._axes[1];
         if (axis != null) {
            axis.layout(axis.getPreferredWidth(), plotHeight);
            axis.setPosition(plotX + plotWidth, plotY);
         }

         axis = this._axes[2];
         if (axis != null) {
            axis.layout(plotWidth, axis.getPreferredHeight());
            axis.setPosition(plotX, plotY + plotHeight);
         }

         axis = this._axes[3];
         if (this._axes[3] != null) {
            this._axes[3].layout(axis.getPreferredWidth(), plotHeight);
            this._axes[3].setPosition(0, plotY);
         }

         this._previousFont = currentFont;
         this._needsSpaceCalculations = false;
         this._preferredWidth = plotX + plotWidth + (this._axes[1] != null ? this._axes[1].getWidth() : 0);
         this._preferredHeight = plotY + plotHeight + (this._axes[2] != null ? this._axes[2].getHeight() : 0);
      }
   }

   public int getNumColors() {
      return this._colors.length;
   }

   public int getColor(int index) {
      try {
         return this._colors[index];
      } catch (ArrayIndexOutOfBoundsException e) {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public int getPreferredHeight() {
      this.doSpaceCalculations();
      return this._preferredHeight;
   }

   @Override
   public int getPreferredWidth() {
      this.doSpaceCalculations();
      return this._preferredWidth;
   }

   public boolean isEmpty() {
      return this._renderers.length == 0;
   }

   @Override
   public boolean isFocusable() {
      return true;
   }

   @Override
   public void getFocusRect(XYRect rect) {
      Manager manager = this.getManager();
      int availableScreenWidth;
      int availableScreenHeight;
      if (manager == null) {
         availableScreenWidth = Display.getWidth();
         availableScreenHeight = Display.getHeight();
      } else {
         availableScreenWidth = manager.getVisibleWidth();
         availableScreenHeight = manager.getVisibleHeight();
      }

      rect.x = this._focusX;
      rect.y = this._focusY;
      rect.width = Math.min(availableScreenWidth, this.getPreferredWidth());
      rect.height = Math.min(availableScreenHeight, this.getPreferredHeight());
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      int fontHeight = this.getFont().getHeight();
      amount *= fontHeight;
      if ((status & 196608) != 0) {
         switch (status & 196608) {
            case 65536:
               amount = this.moveFocusHorizontally(amount);
               break;
            case 131072:
               amount = this.moveFocusVertically(amount);
         }
      } else {
         switch (status & 1) {
            case 1:
               amount = this.moveFocusHorizontally(amount);
               break;
            default:
               amount = this.moveFocusVertically(amount);
         }
      }

      return super.moveFocus(amount / fontHeight, status, time);
   }

   private int moveFocusHorizontally(int preferredAmount) {
      XYRect rect = Ui.getTmpXYRect();
      this.getFocusRect(rect);
      int actualAmount;
      if (preferredAmount < 0) {
         int spaceAvailable = -rect.x;
         actualAmount = Math.max(preferredAmount, spaceAvailable);
      } else {
         int spaceAvailable = this.getPreferredWidth() - rect.X2();
         actualAmount = Math.min(preferredAmount, spaceAvailable);
      }

      this._focusX += actualAmount;
      Ui.returnTmpXYRect(rect);
      return preferredAmount - actualAmount;
   }

   private int moveFocusVertically(int preferredAmount) {
      XYRect rect = Ui.getTmpXYRect();
      this.getFocusRect(rect);
      int actualAmount;
      if (preferredAmount < 0) {
         int spaceAvailable = -rect.y;
         actualAmount = Math.max(preferredAmount, spaceAvailable);
      } else {
         int spaceAvailable = this.getPreferredHeight() - rect.Y2();
         actualAmount = Math.min(preferredAmount, spaceAvailable);
      }

      this._focusY += actualAmount;
      Ui.returnTmpXYRect(rect);
      return preferredAmount - actualAmount;
   }

   @Override
   protected void layout(int width, int height) {
      width = Math.min(width, this.getPreferredWidth());
      height = Math.min(height, this.getPreferredHeight());
      this.setExtent(width, height);
   }

   @Override
   protected void paint(Graphics graphics) {
      if (this.isEmpty()) {
         graphics.drawText(this._resources.getString(32), 0, 0, 4, this._preferredWidth);
      } else {
         this.doSpaceCalculations();

         for (int index = 0; index < this._axes.length; index++) {
            Axis axis = this._axes[index];
            if (axis != null) {
               graphics.pushContext(axis.getLeft(), axis.getTop(), axis.getWidth(), axis.getHeight(), axis.getLeft(), axis.getTop());
               axis.paint(graphics);
               graphics.popContext();
            }
         }

         int end = this._renderers.length;

         for (int index = 0; index < end; index++) {
            ChartRenderer renderer = this._renderers[index];
            graphics.pushContext(renderer.getLeft(), renderer.getTop(), renderer.getWidth(), renderer.getHeight(), renderer.getLeft(), renderer.getTop());
            renderer.paint(graphics);
            graphics.popContext();
         }
      }
   }

   public void setAxis(int axisIndex, Axis axis) {
      this._axes[axisIndex] = axis;
      if (axis != null) {
         axis.setEdge(axisIndex);
      }
   }

   protected void setColor(int index, int color) {
      try {
         this._colors[index] = color;
      } catch (ArrayIndexOutOfBoundsException e) {
         throw new IllegalArgumentException();
      }
   }

   protected void setChartData(int[] colors) {
      this._colors = new int[colors.length];
      System.arraycopy(colors, 0, this._colors, 0, colors.length);
   }

   protected void updateChart() {
      if (this.getWidth() != this.getPreferredWidth() || this.getHeight() != this.getPreferredHeight()) {
         this.updateLayout();
      }

      this.invalidate();
   }
}
