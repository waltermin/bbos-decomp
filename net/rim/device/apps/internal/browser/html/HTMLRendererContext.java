package net.rim.device.apps.internal.browser.html;

import java.io.InputStream;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.internal.browser.stack.WAPInputStream;
import net.rim.device.internal.browser.util.Pipe;

final class HTMLRendererContext {
   int _currentStringRef = -1;
   int _richTextStringsLength;
   int _pushedCellCount;
   String[] _richTextStrings;
   IntHashtable _richTextProperties;
   WAPInputStream _in;
   InputStream _compressedStrings;
   Pipe _pipe;
   boolean _appendNewLine = true;
   boolean _allTagsProvided;
   boolean _transcodedOnDevice;
   int _markupMinorVersion = -1;
   JavaScriptItem _currentScript;
   HTMLObject _currentObject;

   HTMLRendererContext(Pipe pipe) {
      this._richTextStrings = new String[0];
      this._pipe = pipe;
   }
}
