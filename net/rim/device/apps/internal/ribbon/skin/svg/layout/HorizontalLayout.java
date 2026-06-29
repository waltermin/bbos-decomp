package net.rim.device.apps.internal.ribbon.skin.svg.layout;

import java.util.Hashtable;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.ui.AbstractForeignObject;
import net.rim.plazmic.internal.mediaengine.ui.LayoutManager;

class HorizontalLayout extends AbstractForeignObject implements LayoutManager {
   protected ModelInteractorImpl _model;
   protected int _width;
   protected int _align = 1;
   public static final int ALIGN_LEFT;
   public static final int ALIGN_CENTER;
   public static final int ALIGN_RIGHT;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public HorizontalLayout(ModelInteractorImpl model, Hashtable params) {
      this._model = model;
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         String e = params.get("width");
         if (e != null) {
            this._width = Integer.parseInt((String)e);
         }

         e = params.get("align");
         if (e != null) {
            if (((String)e).equals("left")) {
               this._align = 0;
               var5 = false;
            } else if (((String)e).equals("center")) {
               this._align = 1;
               var5 = false;
            } else {
               if (((String)e).equals("right")) {
                  this._align = 2;
                  return;
               }

               var5 = false;
            }
         } else {
            var5 = false;
         }
      } finally {
         if (var5) {
            System.out.println("Invalid number format in HorizontalLayout");
            return;
         }
      }
   }

   @Override
   public void draw(Object graphics, int x, int y) {
   }

   @Override
   public int getWidth() {
      return 0;
   }

   @Override
   public int getHeight() {
      return 0;
   }

   @Override
   public void layout() {
      throw null;
   }
}
