package net.rim.device.internal.io;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;
import net.rim.device.api.io.FilterBaseInterface;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.utility.MalformedURLException;
import net.rim.device.internal.firewall.Firewall;
import net.rim.device.internal.firewall.FirewallContext;
import net.rim.device.internal.midlet.MIDPSupport;
import net.rim.device.internal.system.MIDletSecurity;

public class RIMConnector {
   private RIMConnector() {
   }

   public static Connection open(int moduleHandle, String name) {
      return open(moduleHandle, name, 3, false, null);
   }

   public static Connection open(int moduleHandle, String name, int mode, boolean timeouts) {
      return open(moduleHandle, name, mode, timeouts, null);
   }

   public static Connection open(int moduleHandle, String name, int mode, boolean timeouts, int[] additionalModules, ApplicationDescriptor requestingDescriptor) {
      FirewallContext context = null;
      if (additionalModules != null || requestingDescriptor != null) {
         context = new FirewallContext();
         context.setAdditionalModules(additionalModules);
         context.setRequestingDescriptor(requestingDescriptor);
      }

      return open(moduleHandle, name, mode, timeouts, context);
   }

   private static Connection open(int moduleHandle, String name, int mode, boolean timeouts, FirewallContext context) throws IOException, ConnectionNotFoundException {
      Class clazz = null;
      if (name == null) {
         throw new IllegalArgumentException("Null URL");
      }

      int colon = name.indexOf(58);
      if (colon < 1) {
         throw new IllegalArgumentException("no ':' in URL");
      }

      String protocol = name.substring(0, colon);
      String original = name;
      name = name.substring(colon + 1);
      String className;
      if (protocol.indexOf(46) == -1) {
         className = "net.rim.device.cldc.io." + StringUtilities.toLowerCase(protocol, 1701707776);
      } else {
         className = protocol;
      }

      try {
         clazz = Class.forName(className + ".Protocol");
         Object instance = clazz.newInstance();
         if (CodeModuleManager.isMidlet(moduleHandle)) {
            if (!original.startsWith("file:")
               && !original.startsWith("comm:")
               && !original.startsWith("btspp:")
               && !original.startsWith("btgoep:")
               && !original.startsWith("btl2cap:")
               && !original.startsWith("apdu:")
               && !original.startsWith("jcrmi:")
               && !original.startsWith("mms:")) {
               try {
                  checkConnectionSupported(original);
               } catch (MalformedURLException e) {
                  throw new IllegalArgumentException(e.toString());
               }
            }

            Connection c = PushRegistryHelper.checkConnection(original);
            if (MIDletSecurity.checkUntrustedMIDlet()) {
               colon = name.indexOf(58);
               if (colon >= 0) {
                  int start = 2;
                  int positionOfSlash = name.indexOf(47, start);
                  int positionOfQuestionMark = name.indexOf(63, start);
                  int positionOfSemiColon = name.indexOf(59, start);
                  int positionOfHash = name.indexOf(35, start);
                  int portStringEndsAt = name.length();
                  if (positionOfSlash != -1) {
                     portStringEndsAt = Math.min(portStringEndsAt, positionOfSlash);
                  }

                  if (positionOfQuestionMark != -1) {
                     portStringEndsAt = Math.min(portStringEndsAt, positionOfQuestionMark);
                  }

                  if (positionOfSemiColon != -1) {
                     portStringEndsAt = Math.min(portStringEndsAt, positionOfSemiColon);
                  }

                  if (positionOfHash != -1) {
                     portStringEndsAt = Math.min(portStringEndsAt, positionOfHash);
                  }

                  int port;
                  try {
                     port = Integer.parseInt(name.substring(colon + 1, portStringEndsAt));
                  } catch (NumberFormatException nfe) {
                     port = 0;
                  } catch (IndexOutOfBoundsException ioobe) {
                     port = 0;
                  }

                  if (port == 8080 && protocol.equals("socket")) {
                     throw new SecurityException("Cannot open " + protocol + " connection on port 8080");
                  }

                  if (port == 443 && (protocol.equals("ssl") || protocol.equals("socket"))) {
                     throw new SecurityException("Cannot open " + protocol + " connection on port 443");
                  }

                  if (port == 80 && protocol.equals("socket")) {
                     throw new SecurityException("Cannot open " + protocol + " connection on port 80");
                  }
               }
            }

            if (c != null) {
               return c;
            }
         }

         if (instance instanceof ConnectionBaseInterface) {
            ConnectionBaseInterface uc = (ConnectionBaseInterface)instance;
            if ((context != null || !ControlledAccess.verifyCodeModuleSignature(moduleHandle, 51))
               && !Firewall.getInstance().allowConnection(protocol, name, uc.getProperties(name), context)) {
               throw new SecurityException("Permission denied");
            } else {
               return uc.openPrim(name, mode, timeouts);
            }
         } else {
            return !(instance instanceof FilterBaseInterface) ? null : ((FilterBaseInterface)instance).openFilter(name, mode, timeouts);
         }
      } catch (InstantiationException x) {
         throw new IOException(x.toString());
      } catch (IllegalAccessException x) {
         throw new IOException(x.toString());
      } catch (ClassCastException x) {
         throw new IOException(x.toString());
      } catch (ClassNotFoundException x) {
         StringBuffer message = new StringBuffer("Protocol not found: ");
         if (clazz != null) {
            message.append(clazz.toString());
         } else {
            message.append(className).append(".Protocol");
         }

         throw new ConnectionNotFoundException(message.toString());
      }
   }

   public static void doFirewallCheck(int moduleHandle, String name, int[] additionalModules, ApplicationDescriptor requestingDescriptor) throws IOException, ConnectionNotFoundException {
      if (name == null) {
         throw new IllegalArgumentException("Null URL");
      }

      int colon = name.indexOf(58);
      if (colon < 1) {
         throw new IllegalArgumentException("no ':' in URL");
      }

      String protocol = name.substring(0, colon);
      name = name.substring(colon + 1);
      String className;
      if (protocol.indexOf(46) == -1) {
         className = "net.rim.device.cldc.io." + StringUtilities.toLowerCase(protocol, 1701707776);
      } else {
         className = protocol;
      }

      Class clazz = null;

      try {
         clazz = Class.forName(className + ".Protocol");
         Object instance = clazz.newInstance();
         if (instance instanceof ConnectionBaseInterface) {
            ConnectionBaseInterface uc = (ConnectionBaseInterface)instance;
            FirewallContext context = null;
            if (additionalModules != null || requestingDescriptor != null) {
               context = new FirewallContext();
               context.setAdditionalModules(additionalModules);
               context.setRequestingDescriptor(requestingDescriptor);
            }

            if ((context != null || !ControlledAccess.verifyCodeModuleSignature(moduleHandle, 51))
               && !Firewall.getInstance().allowConnection(protocol, name, uc.getProperties(name), context)) {
               throw new SecurityException("Permission denied");
            }
         }
      } catch (InstantiationException x) {
         throw new IOException(x.toString());
      } catch (IllegalAccessException x) {
         throw new IOException(x.toString());
      } catch (ClassCastException x) {
         throw new IOException(x.toString());
      } catch (ClassNotFoundException x) {
         StringBuffer message = new StringBuffer("Protocol not found: ");
         if (clazz != null) {
            message.append(clazz.toString());
         } else {
            message.append(className).append(".Protocol");
         }

         throw new ConnectionNotFoundException(message.toString());
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static DataInputStream openDataInputStream(int moduleHandle, String name) {
      InputConnection con = (InputConnection)open(moduleHandle, name, 1, false, null);
      boolean var6 = false /* VF: Semaphore variable */;

      DataInputStream var3;
      try {
         var6 = true;
         var3 = con.openDataInputStream();
         var6 = false;
      } finally {
         if (var6) {
            con.close();
         }
      }

      con.close();
      return var3;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static DataOutputStream openDataOutputStream(int moduleHandle, String name) {
      OutputConnection con = (OutputConnection)open(moduleHandle, name, 2, false, null);
      boolean var6 = false /* VF: Semaphore variable */;

      DataOutputStream var3;
      try {
         var6 = true;
         var3 = con.openDataOutputStream();
         var6 = false;
      } finally {
         if (var6) {
            con.close();
         }
      }

      con.close();
      return var3;
   }

   private static void checkConnectionSupported(String name) throws ConnectionNotFoundException {
      if (MIDPSupport.connectionNotSupported(name)) {
         throw new ConnectionNotFoundException();
      }
   }
}
