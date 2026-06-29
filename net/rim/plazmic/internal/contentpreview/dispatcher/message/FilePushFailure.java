package net.rim.plazmic.internal.contentpreview.dispatcher.message;

import net.rim.plazmic.internal.contentpreview.dispatcher.DispatcherEventHandler;

public final class FilePushFailure extends FailureModel {
   public static final String rcsid;

   public FilePushFailure(String message) {
      super(message);
   }

   @Override
   public final void toEvent(DispatcherEventHandler handler) {
      handler.filePushFailure(this.getMessage());
   }

   @Override
   final String getClassName() {
      return "FilePushFailure";
   }
}
