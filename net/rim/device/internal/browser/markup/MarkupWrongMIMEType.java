package net.rim.device.internal.browser.markup;

import java.io.IOException;

public final class MarkupWrongMIMEType extends IOException {
   private String _newMIMEType;

   public MarkupWrongMIMEType(String newMIMEType) {
      this._newMIMEType = newMIMEType;
   }

   public final String getNewMIMEType() {
      return this._newMIMEType;
   }
}
