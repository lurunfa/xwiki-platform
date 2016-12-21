/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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
 */
package com.xpn.xwiki.doc;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.xwiki.localization.LocaleUtils;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.store.XWikiHibernateDeletedDocumentContent;
import com.xpn.xwiki.util.AbstractSimpleClass;
import com.xpn.xwiki.util.Util;

/**
 * Archive of deleted document, stored in {@link com.xpn.xwiki.store.XWikiRecycleBinStoreInterface}. Immutable, because
 * we don't need modify deleted document.
 *
 * @version $Id$
 * @since 1.2M1
 */
public class XWikiDeletedDocument extends AbstractSimpleClass
{
    /**
     * Synthetic id.
     */
    private long id;

    /**
     * @see XWikiDocument#getFullName()
     */
    private String fullName;

    /**
     * @see XWikiDocument#getLocale()
     */
    private Locale locale;

    /**
     * date of delete action.
     */
    private Date date;

    /**
     * @see XWikiDeletedDocument#getDeleter()
     */
    private String deleter;

    private String storeType;

    private XWikiDeletedDocumentContent content;

    /**
     * Default constructor. Used only in hibernate.
     */
    protected XWikiDeletedDocument()
    {
    }

    /**
     * @param fullName the local reference of the document
     * @param locale the locale of the document
     * @param storeType - the way to store the document
     * @param deleter - user which delete document
     * @param deleteDate - date of delete action
     * @throws XWikiException if any error
     */
    private XWikiDeletedDocument(String fullName, Locale locale, String storeType, String deleter, Date deleteDate)
        throws XWikiException
    {
        this.fullName = fullName;
        this.locale = locale;
        this.deleter = deleter;
        this.date = deleteDate;
        this.storeType = storeType;
    }

    /**
     * @param doc - deleted document
     * @param deleter - user which delete document
     * @param deleteDate - date of delete action
     * @param context - used for environment
     * @throws XWikiException if any error
     */
    public XWikiDeletedDocument(XWikiDocument doc, String deleter, Date deleteDate, XWikiContext context)
        throws XWikiException
    {
        this(doc.getFullName(), doc.getLocale(), null, deleter, deleteDate);

        setDocument(doc, context);
    }

    /**
     * @param fullName the local reference of the document
     * @param locale the locale of the document
     * @param storeType the way to store the document
     * @param deleter the user who delete document
     * @param deleteDate date of delete action
     * @param content the stored deleted document
     * @throws XWikiException if any error
     */
    public XWikiDeletedDocument(String fullName, Locale locale, String storeType, String deleter, Date deleteDate,
        XWikiDeletedDocumentContent content) throws XWikiException
    {
        this(fullName, locale, storeType, deleter, deleteDate);

        this.content = content;
    }

    /**
     * @return the synthetic id of this deleted document. unique only for document.
     */
    public long getId()
    {
        return this.id;
    }

    /**
     * @param id - the synthetic id to set. used only in Hibernate.
     */
    protected void setId(long id)
    {
        this.id = id;
    }

    /**
     * @return {@link XWikiDocument#getFullName()}
     */
    public String getFullName()
    {
        return this.fullName;
    }

    /**
     * @param docFullName - {@link XWikiDocument#getFullName()} to set
     */
    protected void setFullName(String docFullName)
    {
        this.fullName = docFullName;
    }

    /**
     * @return {@link XWikiDocument#getLanguage()}
     * @deprecated since 8.0M1, use {@link #getLocale()} instead
     */
    @Deprecated
    public String getLanguage()
    {
        return getLocale().toString();
    }

    /**
     * @return {@link XWikiDocument#getLocale()}
     * @since 8.0M1
     */
    public Locale getLocale()
    {
        return this.locale != null ? this.locale : Locale.ROOT;
    }

    /**
     * @param locale - {@link XWikiDocument#getLanguage()} to set
     * @deprecated since 8.0M1
     */
    @Deprecated
    protected void setLanguage(String locale)
    {
        this.locale = LocaleUtils.toLocale(Util.normalizeLanguage(locale), Locale.ROOT);
    }

    /**
     * @return the date of delete action
     */
    public Date getDate()
    {
        return this.date;
    }

    /**
     * @param date - the date of delete action to set
     */
    protected void setDate(Date date)
    {
        this.date = date;
    }

    /**
     * @return the user which has removed the document
     */
    public String getDeleter()
    {
        return this.deleter;
    }

    /**
     * @param deleter - the user which has removed the document to set
     */
    protected void setDeleter(String deleter)
    {
        this.deleter = deleter;
    }

    /**
     * @return the type of the stored used for the content
     */
    public String getStoreType()
    {
        return this.storeType;
    }

    /**
     * @param storeType the type of store (supported values are "hibernate" and "file")
     */
    protected void setStoreType(String storeType)
    {
        this.storeType = storeType;
    }

    /**
     * Only used in Hibernate.
     * 
     * @return xml serialization of {@link XWikiDocument}
     */
    public String getXml()
    {
        if (this.content != null) {
            try {
                return this.content.getContentAsString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Return empty String instead of null because this field is configured as not null at database level
        return "";
    }

    /**
     * Only used in Hibernate.
     * 
     * @param xml - xml serialization of {@link XWikiDocument}
     */
    protected void setXml(String xml)
    {
        if (StringUtils.isNotEmpty(xml)) {
            try {
                this.content = new XWikiHibernateDeletedDocumentContent(xml);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Export {@link XWikiDocument} to {@link XWikiDeletedDocument}.
     *
     * @param doc - the deleted document
     * @param context - used in {@link XWikiDocument#toXML(XWikiContext)}
     * @throws XWikiException in error in {@link XWikiDocument#toXML(XWikiContext)}
     * @deprecated since 9.0RC1, use
     *             {@link XWikiDeletedDocument#XWikiDeletedDocument(String, Locale, String, String, Date, XWikiDeletedDocumentContent)
     *             instead}
     */
    @Deprecated
    protected void setDocument(XWikiDocument doc, XWikiContext context) throws XWikiException
    {
        this.content = new XWikiHibernateDeletedDocumentContent(doc);
    }

    /**
     * @return restored document
     * @param doc - restore to this document, if not null
     * @param context - may be useful in future
     * @throws XWikiException if error in {@link XWikiDocument#fromXML(String)}
     */
    public XWikiDocument restoreDocument(XWikiDocument doc, XWikiContext context) throws XWikiException
    {
        try {
            return this.content.getXWikiDocument(doc);
        } catch (IOException e) {
            throw new XWikiException(XWikiException.MODULE_XWIKI_DOC, XWikiException.ERROR_DOC_XML_PARSING,
                "Error restoring document", e, null);
        }
    }

    /**
     * @return restored document
     * @param context - may be useful in future
     * @throws XWikiException if error in {@link XWikiDocument#fromXML(String)}
     * @since 9.0RC1
     */
    public XWikiDocument restoreDocument(XWikiContext context) throws XWikiException
    {
        return restoreDocument(null, context);
    }
}
