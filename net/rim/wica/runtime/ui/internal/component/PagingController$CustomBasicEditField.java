package net.rim.wica.runtime.ui.internal.component;

import net.rim.device.api.ui.component.BasicEditField;

public final class PagingController$CustomBasicEditField extends BasicEditField {
   @Override
   protected final boolean keyChar(char key, int status, int time) {
      return key == '\n' ? false : super.keyChar(key, status, time);
   }
}
