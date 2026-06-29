package net.rim.device.apps.internal.bis.commands;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.api.ui.DomainCommand;
import net.rim.device.apps.internal.bis.api.ui.DomainCommandResult;
import net.rim.device.apps.internal.bis.config.BISClientConfigRecord;
import net.rim.device.apps.internal.bis.data.Filter;
import net.rim.device.apps.internal.bis.data.Mailbox;
import net.rim.device.apps.internal.bis.protocol.RestClient;
import net.rim.device.apps.internal.bis.protocol.RestClient$RESTCallResult;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.utils.ObjectRegistry;

public final class AddFilterCommand implements DomainCommand {
   public static final String PARAM_FILTER_ID = "filterid";
   public static final String PARAM_FILTER_NAME = "filtername";
   public static final String PARAM_SEND_ALERT = "sendalert";
   public static final String PARAM_HEADERS_ONLY = "headersonly";
   public static final String PARAM_LEVEL_ONE = "levelone";
   public static final String PARAM_FILTER_OPERATOR = "filteroperator";
   public static final String PARAM_FILTER_VALUE = "filtervalue";
   public static final String PARAM_FILTER_CONFIRM = "filterconfirm";

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DomainCommandResult run(Hashtable params) {
      Filter filter = ClientSessionState.getInstance().getFilterToModify();
      if (filter == null || filter.getId() != null && params.get("filterconfirm") == null) {
         filter = new Filter();
         filter.setId((String)params.get("filterid"));
         filter.setName((String)params.get("filtername"));
         filter.setSendAlert("true".equals(params.get("sendalert")));
         filter.setHeadersOnly("true".equals(params.get("headersonly")));
         filter.setLevelOne("true".equals(params.get("levelone")));
         filter.setOperator((String)params.get("filteroperator"));
         filter.setValue((String)params.get("filtervalue"));
         ClientSessionState.getInstance().setFilterToModify(filter);
      }

      Mailbox mailbox = ClientSessionState.getInstance().getMailboxToModify();
      BISClientConfigRecord configRecord = (BISClientConfigRecord)ObjectRegistry.getInstance().find("configRecord");
      RestClient restClient = (RestClient)ObjectRegistry.getInstance().find("restClient");
      if (params.get("filterconfirm") != null
         || !mailbox.getHosted()
         || filter.getSendAlert() && (!filter.getSendAlert() || !filter.getHeadersOnly()) && mailbox.getForwardMessages()) {
         boolean var12 = false /* VF: Semaphore variable */;

         DomainCommandResult var21;
         label159: {
            label158: {
               label157: {
                  DomainCommandResult callResult;
                  try {
                     label155:
                     try {
                        var12 = true;
                        String e = ClientSessionState.getInstance().getUserInfo().getUsername();
                        RestClient$RESTCallResult var17 = null;
                        if (filter.getId() == null) {
                           var17 = restClient.addFilter(
                              configRecord.getBrandName(),
                              e,
                              mailbox.getSrcMboxID(),
                              filter.getName(),
                              (Boolean)(new Object(filter.getSendAlert())),
                              (Boolean)(new Object(filter.getLevelOne())),
                              (Boolean)(new Object(filter.getHeadersOnly())),
                              filter.getOperator(),
                              filter.getValue()
                           );
                        } else {
                           var17 = restClient.updateFilter(
                              configRecord.getBrandName(),
                              e,
                              mailbox.getSrcMboxID(),
                              filter.getId(),
                              filter.getName(),
                              (Boolean)(new Object(filter.getSendAlert())),
                              (Boolean)(new Object(filter.getLevelOne())),
                              (Boolean)(new Object(filter.getHeadersOnly())),
                              filter.getOperator(),
                              filter.getValue()
                           );
                        }

                        if (var17.getRESTStatusCode() != 200) {
                           if (var17.getRESTStatusCode() == 401) {
                              var21 = DomainCommand.SESSION_TIMEOUT_RESULT;
                              var12 = false;
                              break label159;
                           }

                           BISEventLogger.logEvent(
                              ((StringBuffer)(new Object("Delete: Unhandled REST response code: "))).append(var17.getRESTStatusCode()).toString(), 0
                           );
                           var21 = new DomainCommandResult("failed", ApplicationResources.getString(192), null);
                           var12 = false;
                           break label158;
                        }

                        if (filter.getId() == null) {
                           Filter addedFilter = var17.getFilter();
                           mailbox.addFilter(addedFilter);
                        } else {
                           Filter filterToModify = mailbox.getFilter(filter.getId());
                           filterToModify.setName(filter.getName());
                           filterToModify.setSendAlert(filter.getSendAlert());
                           filterToModify.setLevelOne(filter.getLevelOne());
                           filterToModify.setHeadersOnly(filter.getHeadersOnly());
                           filterToModify.setOperator(filter.getOperator());
                           filterToModify.setValue(filter.getValue());
                        }

                        ClientSessionState.getInstance().setFilterToModify(null);
                        var12 = false;
                        break label157;
                     } catch (Throwable var15) {
                        BISEventLogger.logEvent(e.toString(), 0);
                        callResult = new DomainCommandResult("error", null, null);
                        var12 = false;
                        break label155;
                     }
                  } finally {
                     if (var12) {
                        ClientSessionState.getInstance().setFilterToModify(null);
                     }
                  }

                  ClientSessionState.getInstance().setFilterToModify(null);
                  return callResult;
               }

               ClientSessionState.getInstance().setFilterToModify(null);
               return new DomainCommandResult("success", null, null);
            }

            ClientSessionState.getInstance().setFilterToModify(null);
            return var21;
         }

         ClientSessionState.getInstance().setFilterToModify(null);
         return var21;
      } else {
         return new DomainCommandResult("confirm", null, null);
      }
   }
}
