package com.xpn.xwiki.notify;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: ludovic
 * Date: 8 avr. 2006
 * Time: 00:13:39
 * To change this template use File | Settings | File Templates.
 */
public class XWikiPageNotification implements XWikiActionNotificationInterface {
    private static final Log log = LogFactory.getLog(XWikiPageNotification.class);

    public void notify(XWikiNotificationRule rule, XWikiDocument doc, String action, XWikiContext context) {
        try {
            String notifpages = context.getWiki().getXWikiPreference("notification_pages", context);
            if ((notifpages!=null)&&(!notifpages.equals(""))) {
                String[] notifpages2 = StringUtils.split(notifpages, " ,");
                for (int i=0;i<notifpages2.length;i++) {
                    notifyPage(notifpages2[i], rule, doc, action, context);
                }
            }
            String xnotif = (context.getRequest()!=null) ? context.getRequest().getParameter("xnotification") : null;
            if ((xnotif!=null)&&(!xnotif.equals(""))) {
              notifyPage(xnotif, rule, doc, action, context);
            }
        } catch (Throwable e) {
            XWikiException e2 = new XWikiException(XWikiException.MODULE_XWIKI_NOTIFICATION, XWikiException.ERROR_XWIKI_NOTIFICATION, "Error executing notifications", e);
            if (log.isErrorEnabled())
                log.error(e2.getFullMessage());
        }
    }

    protected void notifyPage(String page, XWikiNotificationRule rule, XWikiDocument doc, String action, XWikiContext context) {
        XWikiActionNotificationInterface notif = null;
        try {
            XWiki xwiki = context.getWiki();
            XWikiDocument pagedoc = xwiki.getDocument(page, context);
            if (xwiki.getRightService().hasProgrammingRights(pagedoc, context))
                notif = (XWikiActionNotificationInterface) xwiki.parseGroovyFromString(pagedoc.getContent(), context);
        } catch (Throwable e) {
            Object[] args = { page };
            XWikiException e2 = new XWikiException(XWikiException.MODULE_XWIKI_GROOVY, XWikiException.ERROR_XWIKI_GROOVY_COMPILE_FAILED, "Error parsing groovy notification for page {0}", e, args);
            if (log.isErrorEnabled())
                log.error(e2.getFullMessage());
        }
        try {
            notif.notify(rule, doc, action, context);
        } catch (Throwable e) {
            Object[] args = { page };
            XWikiException e2 = new XWikiException(XWikiException.MODULE_XWIKI_GROOVY, XWikiException.ERROR_XWIKI_GROOVY_EXECUTION_FAILED, "Error executing groovy notification for page {0}", e, args);
            if (log.isErrorEnabled())
                log.error(e2.getFullMessage());
        }
    }
}
