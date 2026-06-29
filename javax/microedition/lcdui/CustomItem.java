package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class CustomItem extends Item {
   private VerticalFieldManager _container;
   private CustomLabelField _label;
   private CustomField _field;
   protected static final int TRAVERSE_HORIZONTAL;
   protected static final int TRAVERSE_VERTICAL;
   protected static final int KEY_PRESS;
   protected static final int KEY_RELEASE;
   protected static final int KEY_REPEAT;
   protected static final int POINTER_PRESS;
   protected static final int POINTER_RELEASE;
   protected static final int POINTER_DRAG;
   protected static final int NONE;

   protected CustomItem(String label) {
      synchronized (Application.getEventLock()) {
         this._container = new VerticalFieldManager(1152921504606846976L);
         this._field = new CustomField(this);
         this._container.setCookie(this);
         this.setLabel(label);
         this._container.add(this._field);
         this.setPeer(this._container);
      }
   }

   @Override
   Field addToForm(FieldChangeListener changeListener) {
      this._field.setChangeListener(null);
      this._field.setChangeListener(changeListener);
      return this._container;
   }

   public int getGameAction(int keyCode) {
      return Display.getGameAction(keyCode);
   }

   protected final int getInteractionModes() {
      return 31;
   }

   protected int getMinContentWidth() {
      throw null;
   }

   protected int getMinContentHeight() {
      throw null;
   }

   protected int getPrefContentWidth(int _1) {
      throw null;
   }

   protected int getPrefContentHeight(int _1) {
      throw null;
   }

   protected void sizeChanged(int w, int h) {
   }

   protected final void invalidate() {
      synchronized (Application.getEventLock()) {
         this._field.redoLayout();
      }
   }

   protected void paint(Graphics _1, int _2, int _3) {
      throw null;
   }

   protected final void repaint() {
      this._field.callInvalidate();
   }

   protected final void repaint(int x, int y, int w, int h) {
      this._field.callInvalidate(x, y, w, h);
   }

   protected boolean traverse(int dir, int viewportWidth, int viewportHeight, int[] visRect_inout) {
      return false;
   }

   protected void traverseOut() {
   }

   protected void keyPressed(int keyCode) {
   }

   protected void keyReleased(int keyCode) {
   }

   protected void keyRepeated(int keyCode) {
   }

   protected void pointerPressed(int x, int y) {
   }

   protected void pointerReleased(int x, int y) {
   }

   protected void pointerDragged(int x, int y) {
   }

   protected void showNotify() {
   }

   protected void hideNotify() {
   }

   @Override
   public void setLabel(String label) {
      synchronized (Application.getEventLock()) {
         if (label == null) {
            if (this._label != null) {
               this._container.delete(this._label);
               this._label = null;
            }
         } else if (this._label == null) {
            this._label = new CustomLabelField(label, this._field);
            this._container.insert(this._label, 0);
         } else {
            this._label.setText(label);
         }
      }
   }

   @Override
   public String getLabel() {
      synchronized (Application.getEventLock()) {
         return this._label == null ? null : this._label.getText();
      }
   }
}
