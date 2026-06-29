package net.rim.device.apps.internal.explorer.file.options;

import net.rim.device.api.system.Application;
import net.rim.device.internal.io.file.RootRegister;

final class MediaCardOptionsItem$FormatThread extends Thread {
   @Override
   public final void run() {
      Application.getApplication().invokeAndWait(new MediaCardOptionsItem$FormatThread$1(this));
      boolean success = RootRegister.getInstance().formatSDCard();
      Application.getApplication().invokeAndWait(new MediaCardOptionsItem$FormatThread$2(this, success));
   }
}
