package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;

public class LayerManager {
   private int nlayers;
   private Layer[] component = new Layer[4];
   private int viewX;
   private int viewY;
   private int viewWidth;
   private int viewHeight;

   public LayerManager() {
      this.setViewWindow(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   public void append(Layer l) {
      this.removeImpl(l);
      this.addImpl(l, this.nlayers);
   }

   public void insert(Layer l, int index) {
      if (index >= 0 && index <= this.nlayers) {
         this.removeImpl(l);
         this.addImpl(l, index);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public Layer getLayerAt(int index) {
      if (index >= 0 && index < this.nlayers) {
         return this.component[index];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getSize() {
      return this.nlayers;
   }

   public void remove(Layer l) {
      this.removeImpl(l);
   }

   public void paint(Graphics g, int x, int y) {
      int clipX = g.getClipX();
      int clipY = g.getClipY();
      int clipW = g.getClipWidth();
      int clipH = g.getClipHeight();
      g.translate(x - this.viewX, y - this.viewY);
      g.clipRect(this.viewX, this.viewY, this.viewWidth, this.viewHeight);
      int i = this.nlayers;

      while (--i >= 0) {
         Layer comp = this.component[i];
         if (comp.visible) {
            comp.paint(g);
         }
      }

      g.translate(-x + this.viewX, -y + this.viewY);
      g.setClip(clipX, clipY, clipW, clipH);
   }

   public void setViewWindow(int x, int y, int width, int height) {
      if (width >= 0 && height >= 0) {
         this.viewX = x;
         this.viewY = y;
         this.viewWidth = width;
         this.viewHeight = height;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private void addImpl(Layer layer, int index) {
      if (this.nlayers == this.component.length) {
         Layer[] newcomponents = new Layer[this.nlayers + 4];
         System.arraycopy(this.component, 0, newcomponents, 0, this.nlayers);
         System.arraycopy(this.component, index, newcomponents, index + 1, this.nlayers - index);
         this.component = newcomponents;
      } else {
         System.arraycopy(this.component, index, this.component, index + 1, this.nlayers - index);
      }

      this.component[index] = layer;
      this.nlayers++;
   }

   private void removeImpl(Layer l) {
      if (l == null) {
         throw new NullPointerException();
      }

      int i = this.nlayers;

      while (--i >= 0) {
         if (this.component[i] == l) {
            this.remove(i);
         }
      }
   }

   private void remove(int index) {
      System.arraycopy(this.component, index + 1, this.component, index, this.nlayers - index - 1);
      this.component[--this.nlayers] = null;
   }
}
