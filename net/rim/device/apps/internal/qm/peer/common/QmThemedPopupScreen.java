package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.PopupScreen;

public class QmThemedPopupScreen extends PopupScreen {
   private Manager _mainManager;

   public QmThemedPopupScreen(Manager manager) {
      this(manager, 0);
   }

   public QmThemedPopupScreen(Manager manager, long style) {
      super(manager, style);
      this._mainManager = manager;
   }

   protected final Manager getMainManager() {
      return this._mainManager;
   }

   @Override
   protected void paintBackground(Graphics graphics) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public void add(Field field) {
      if (this._mainManager == null) {
         this._mainManager = new QmThemedPopupScreen$QmThemedVerticalFieldManager();
      }

      this._mainManager.add(field);
   }

   @Override
   public void delete(Field field) {
      this._mainManager.delete(field);
   }

   @Override
   public void deleteRange(int start, int count) {
      this._mainManager.deleteRange(start, count);
   }

   @Override
   public void insert(Field field, int index) {
      this._mainManager.insert(field, index);
   }

   @Override
   public Field getField(int index) {
      return this._mainManager.getField(index);
   }

   @Override
   public int getFieldAtLocation(int x, int y) {
      return this._mainManager.getFieldAtLocation(x, y);
   }

   @Override
   public int getFieldCount() {
      return this._mainManager.getFieldCount();
   }

   @Override
   public Field getFieldWithFocus() {
      return this._mainManager.getFieldWithFocus();
   }

   @Override
   public int getFieldWithFocusIndex() {
      return this._mainManager.getFieldWithFocusIndex();
   }

   @Override
   public void getFocusRect(XYRect rect) {
      this._mainManager.getFocusRect(rect);
   }

   @Override
   public void getFocusRectPhantom(XYRect rect) {
      this._mainManager.getFocusRectPhantom(rect);
   }

   @Override
   public Field getLeafFieldWithFocus() {
      return this._mainManager.getLeafFieldWithFocus();
   }

   @Override
   public void replace(Field oldField, Field newField) {
      this._mainManager.replace(oldField, newField);
   }
}
