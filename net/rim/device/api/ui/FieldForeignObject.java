package net.rim.device.api.ui;

import net.rim.device.api.system.Application;
import net.rim.plazmic.internal.mediaengine.ui.AbstractForeignObject;

public class FieldForeignObject extends AbstractForeignObject implements FieldChangeListener, FocusChangeListener {
   private Field _field;
   private int _width;
   private int _height;
   private FieldChangeListener _changeListener;
   private FocusChangeListener _focusListener;
   private Runnable _setFocus;
   private Runnable _killFocus;
   private Runnable _updateLayout;

   public FocusChangeListener getFocusListener() {
      return this._focusListener;
   }

   public FieldChangeListener getChangeListener() {
      return this._changeListener;
   }

   public void setChangeListener(FieldChangeListener listener) {
      if (listener != null && this._changeListener != null) {
         throw new Object("Multiple change listeners not allowed.");
      }

      this._changeListener = listener;
   }

   public void setFocusListener(FocusChangeListener listener) {
      if (listener != null && this._focusListener != null) {
         throw new Object("Multiple focus listeners not allowed.");
      }

      this._focusListener = listener;
   }

   @Override
   public void focusChanged(Field field, int eventType) {
      if (this.getPeer() != null) {
         this.getPeer().invalidate(this);
      }

      this.myInvalidate(this._field);
      if (this._focusListener != null) {
         this._focusListener.focusChanged(field, eventType);
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (this.getPeer() != null) {
         this.getPeer().invalidate(this);
      }

      this.myInvalidate(this._field);
      if (this._changeListener != null) {
         this._changeListener.fieldChanged(field, context);
      }
   }

   @Override
   public Object getInstance() {
      return this._field;
   }

   @Override
   public void setPosition(int x, int y) {
      this._field.setPosition(x, y);
   }

   @Override
   public void setExtent(int width, int height) {
      this._width = width;
      this._height = height;
      this._field.setExtent(width, height);
      if (this._updateLayout == null) {
         this._updateLayout = new FieldForeignObject$1(this);
      }

      this.executeInEventThread(this._updateLayout);
   }

   @Override
   public boolean isFocusable() {
      return this._field.isFocusable();
   }

   @Override
   public void setFocus() {
      if (this._setFocus == null) {
         this._setFocus = new FieldForeignObject$2(this);
      }

      this.executeInEventThread(this._setFocus);
   }

   @Override
   public void killFocus() {
      if (this._killFocus == null) {
         this._killFocus = new FieldForeignObject$3(this);
      }

      this.executeInEventThread(this._killFocus);
   }

   private void executeInEventThread(Runnable r) {
      Application app = Application.getApplication();
      if (app.isEventThread()) {
         r.run();
      } else {
         app.invokeLater(r);
      }
   }

   private void myInvalidate(Field field) {
      MediaField mf = null;
      Field f = field;

      Manager m;
      while ((m = f.getManager()) != f) {
         if (m instanceof MediaField) {
            mf = (MediaField)m;
            break;
         }

         f = m;
      }

      if (mf != null) {
         mf.invalidate(f.getLeft() - 1, f.getTop() - 1, f.getWidth() + 2, f.getHeight() + 2);
      }
   }

   @Override
   public void draw(Object graphics, int x, int y) {
      if (this._field.getManager() != null) {
         this._field.paintSelf((Graphics)graphics, true, 0, 0);
      }
   }

   @Override
   public int getHeight() {
      return this._height;
   }

   @Override
   public int getY() {
      return this._field.getTop();
   }

   @Override
   public int getWidth() {
      return this._width;
   }

   @Override
   public int getX() {
      return this._field.getLeft();
   }

   public FieldForeignObject(Field field) {
      this._field = field;
      FieldChangeListener changeListener = field.getChangeListener();
      if (changeListener != null) {
         field.setChangeListener(null);
         this.setChangeListener(changeListener);
      }

      this._field.setChangeListener(this);
      FocusChangeListener focusListener = field.getFocusListener();
      if (focusListener != null) {
         field.setFocusListener(null);
         this.setFocusListener(focusListener);
      }

      this._field.setFocusListener(this);
   }
}
