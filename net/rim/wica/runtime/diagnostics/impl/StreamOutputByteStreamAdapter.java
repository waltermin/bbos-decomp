package net.rim.wica.runtime.diagnostics.impl;

import javax.microedition.io.StreamConnection;
import net.rim.wica.common.debug.io.AbstractOutputByteStreamAdapter;

final class StreamOutputByteStreamAdapter extends AbstractOutputByteStreamAdapter {
   StreamOutputByteStreamAdapter(StreamConnection connection) {
      this.init(connection.openOutputStream());
   }
}
