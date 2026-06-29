package net.rim.device.internal.media;

import javax.microedition.media.control.MetaDataControl;

public interface StreamDataControl extends MetaDataControl {
   String NAME = "net.rim.device.internal.StreamDataControl";
   String LOCATOR_KEY = "locator";
   String MIMETYPE_KEY = "mimetype";
   String SOURCESTREAMS_KEY = "sourcestreams";
   String DATASOURCE_KEY = "datasource";
   String AUDIO_SOURCE_KEY = "audiosource";

   Object getKeyValueObject(String var1);

   void setKeyValue(String var1, Object var2);
}
