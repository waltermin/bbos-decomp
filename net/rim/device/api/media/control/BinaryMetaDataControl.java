package net.rim.device.api.media.control;

import javax.microedition.media.Control;
import net.rim.device.api.media.MetaDataObject;

public interface BinaryMetaDataControl extends Control {
   MetaDataObject[] getMetaDataObjects();
}
