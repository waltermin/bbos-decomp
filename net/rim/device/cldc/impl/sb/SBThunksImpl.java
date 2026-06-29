package net.rim.device.cldc.impl.sb;

import net.rim.device.api.servicebook.SBThunks;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.cldc.impl.sb.editor.SBAppContents;

public final class SBThunksImpl implements SBThunks {
   @Override
   public final void displayEditor(ServiceBook sb, int viewMode) {
      SBAppContents myScreen = new SBAppContents(sb, viewMode);
      myScreen.go();
   }
}
