package net.rim.wica.runtime.diagnostics.impl;

import javax.microedition.io.StreamConnection;
import net.rim.wica.common.debug.io.AbstractInputByteStreamAdapter;

final class StreamInputByteStreamAdapter extends AbstractInputByteStreamAdapter {
   StreamInputByteStreamAdapter(StreamConnection connection) {
      this.init(connection.openInputStream());
   }
}
