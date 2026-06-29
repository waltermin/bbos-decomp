package net.rim.device.internal.media;

import javax.microedition.media.control.MetaDataControl;

public interface StreamDataControl extends MetaDataControl {
   String NAME;
   String LOCATOR_KEY;
   String MIMETYPE_KEY;
   String SOURCESTREAMS_KEY;
   String DATASOURCE_KEY;
   String AUDIO_SOURCE_KEY;

   Object getKeyValueObject(String var1);

   void setKeyValue(String var1, Object var2);
}
