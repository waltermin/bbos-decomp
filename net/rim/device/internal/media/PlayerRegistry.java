package net.rim.device.internal.media;

import java.util.Hashtable;
import javax.microedition.lcdui.List;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.internal.lcdui.MMAPIConnector;

public class PlayerRegistry {
   private static final long GUID = 7114352773123562211L;
   public static final long MMAPI_CONNECTOR_GUID = -79929455318757284L;
   private static Hashtable _classnames = ApplicationRegistry.getApplicationRegistry().getHashtable(7114352773123562211L);
   private static MMAPIConnector _mediaConnector;

   private PlayerRegistry() {
   }

   public static Player createPlayer(String contentType) throws MediaException {
      String classname = (String)_classnames.get(contentType);
      if (classname != null) {
         try {
            return (Player)Class.forName(classname).newInstance();
         } catch (Exception e) {
            throw new MediaException(e.getMessage());
         }
      } else {
         return null;
      }
   }

   public static void register(String contentType, String classname) {
      synchronized (_classnames) {
         if (!_classnames.containsKey(contentType)) {
            _classnames.put(contentType, classname);
         }
      }
   }

   public static MMAPIConnector getMMAPIConnector() {
      if (_mediaConnector == null) {
         List.SELECT_COMMAND.getPriority();
         _mediaConnector = (MMAPIConnector)ApplicationRegistry.getApplicationRegistry().get(-79929455318757284L);
      }

      return _mediaConnector;
   }

   public static boolean hasInternalMMAPIAccess() {
      return ControlledAccess.verifyRRISignatures(false);
   }
}
