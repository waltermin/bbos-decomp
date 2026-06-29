package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.secureemail.server.AbortablePleaseWaitDialog;

final class PGPUniversalServer$UniversalServerVerb extends Verb {
   private int _verbType;
   private String _authenticationCookie;
   private final PGPUniversalServer this$0;
   public static final int TYPE_ENROLL = 0;
   public static final int TYPE_AUTHENTICATE = 1;
   public static final int TYPE_DOWNLOAD_KEYS = 2;
   public static final int TYPE_CLEAR_CACHE = 3;

   public PGPUniversalServer$UniversalServerVerb(
      PGPUniversalServer _1, int verbType, int menuOrdering, ResourceBundleFamily rb, int rbKey, String authenticationCookie
   ) {
      super(menuOrdering, rb, rbKey);
      this.this$0 = _1;
      this._verbType = verbType;
      this._authenticationCookie = authenticationCookie;
   }

   @Override
   public final Object invoke(Object context) {
      AbortablePleaseWaitDialog abortablePleaseWaitDialog = (AbortablePleaseWaitDialog)(new Object(
         new PGPUniversalServer$UniversalServerVerb$UniversalVerbWorkerThread(this, null)
      ));
      abortablePleaseWaitDialog.display();
      return null;
   }
}
