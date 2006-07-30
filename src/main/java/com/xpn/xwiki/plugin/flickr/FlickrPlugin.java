/*
 * Copyright 2006, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * @author sdumitriu
 */

package com.xpn.xwiki.plugin.flickr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aetrion.flickr.RequestContext;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;

public class FlickrPlugin extends XWikiDefaultPlugin implements XWikiPluginInterface {
        private static Log mLogger =
                LogFactory.getFactory().getInstance(com.xpn.xwiki.plugin.flickr.FlickrPlugin.class);

        public FlickrPlugin(String name, String className, XWikiContext context) {
            super(name, className, context);
            init(context);
        }

    public String getName() {
        return "flickr";
    }

    public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context) {
        return new FlickrPluginApi((FlickrPlugin) plugin, context);
    }

    public void flushCache() {
    }

    public void init(XWikiContext context) {
        super.init(context);
    }

    public void endRendering(XWikiContext context) {
        RequestContext.getRequestContext().setAuthentication(null);
    }

}
