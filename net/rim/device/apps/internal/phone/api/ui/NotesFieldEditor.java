package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;

public final class NotesFieldEditor extends VerticalFieldManager {
   private int _prefHeight;

   public NotesFieldEditor(Field notesField) {
      this(notesField, false);
   }

   public NotesFieldEditor(Field notesField, boolean addSeparator) {
      super(299067162755072L);
      if (addSeparator) {
         this.add((Field)(new Object()));
      }

      this.add(notesField);
      this.setCookie(notesField.getCookie());
   }

   public final void setPrefHeight(int height) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected final void sublayout(int width, int height) {
      super.sublayout(width, this._prefHeight);
      this.setExtent(width, this._prefHeight);
   }

   public final Field getNotesField() {
      return this.getField(0);
   }
}
