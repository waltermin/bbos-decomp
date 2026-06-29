package net.rim.device.api.ui.chart;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

public class LegendField extends ChartField {
   private String _title;
   private String[] _labels = new String[0];

   public LegendField() {
      this(0);
   }

   public LegendField(long style) {
      super(style);
   }

   @Override
   public int getPreferredHeight() {
      int fontHeight = Font.getDefault().getHeight();
      int legendHeight = (int)(4608308318706860032L * fontHeight * this._labels.length);
      if (this._title != null) {
         legendHeight += fontHeight;
      }

      return legendHeight;
   }

   @Override
   public int getPreferredWidth() {
      Font font = Font.getDefault();
      int theWidth = 0;

      for (int i = this._labels.length - 1; i >= 0; i--) {
         if (font.getAdvance(this._labels[i]) > theWidth) {
            theWidth = font.getAdvance(this._labels[i]);
         }
      }

      if (font.getAdvance(this._title) > theWidth) {
         theWidth = font.getAdvance(this._title);
      }

      return theWidth + font.getAdvance((char)32) + font.getHeight();
   }

   @Override
   protected void paint(Graphics graphics) {
      int width = this.getPreferredWidth();
      Font font = Font.getDefault();
      int rowHeight = font.getHeight();
      int spaceWidth = font.getAdvance(' ');
      int tempY;
      if (this._title != null) {
         int tempX = (width - font.getAdvance(this._title)) / 2;
         graphics.setFont(font);
         graphics.drawText(this._title, tempX, 0);
         tempY = (int)(rowHeight * 4608308318706860032L);
      } else {
         tempY = (int)(rowHeight * 4598175219545276416L);
      }

      int tempX = rowHeight + spaceWidth;
      int numLabels = this._labels.length;

      for (int count = 0; count < numLabels; count++) {
         graphics.setColor(this.getColor(count));
         graphics.fillRect(0, tempY, rowHeight, rowHeight);
         graphics.setColor(0);
         graphics.drawRect(0, tempY, rowHeight, rowHeight);
         graphics.drawText(this._labels[count], tempX, tempY);
         tempY += (int)(rowHeight * 4608308318706860032L);
      }
   }

   public int getNumLabels() {
      return this._labels.length;
   }

   public String getLabel(int index) {
      try {
         return this._labels[index];
      } catch (ArrayIndexOutOfBoundsException e) {
         throw new IllegalArgumentException();
      }
   }

   public String getTitle() {
      return this._title;
   }

   public void setChartData(String[] labels, int[] colors) {
      ChartField.assertLegal(labels.length == colors.length);

      for (int i = labels.length - 1; i >= 0; i--) {
         if (labels[i] == null) {
            throw new NullPointerException();
         }
      }

      this._labels = new String[labels.length];
      System.arraycopy(labels, 0, this._labels, 0, labels.length);
      super.setChartData(colors);
      this.updateChart();
   }

   public void setLabel(int index, String label) {
      try {
         if (!label.equals(this._labels[index])) {
            this._labels[index] = label;
            this.updateChart();
         }
      } catch (ArrayIndexOutOfBoundsException e) {
         throw new IllegalArgumentException();
      }
   }

   public void setTitle(String title) {
      if (this._title == null && title != null || this._title != null && !this._title.equals(title)) {
         this._title = title;
         this.updateChart();
      }
   }
}
