/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.imeji.presentation.servlet;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.internal.InternalStorageManager;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.ObjectLoader;
import de.mpg.imeji.presentation.util.PropertyReader;
import digilib.servlet.Scaler;

/**
 * Add Authentification and Authorization from imeji to {@link Scaler}. This avoid to make all files unprototected
 * through digilib
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class DigilibServlet extends Scaler
{
    /**
     * 
     */
    private static final long serialVersionUID = 1271326569919483929L;
    /**
     * imeji authentification and authorization
     */
    private Security security;
    private String internalStorageBase;
    private StorageController storageController;
    private Navigation navigation;

    /*
     * (non-Javadoc)
     * @see digilib.servlet.Scaler#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        security = new Security();
        try
        {
            navigation = new Navigation();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        storageController = new StorageController();
        InternalStorageManager ism = new InternalStorageManager();
        internalStorageBase = FilenameUtils.getBaseName(FilenameUtils.normalizeNoEndSeparator(ism.getStoragePath()));
        // Copy the digilib-config.xml before initialising the digilib servlet, which needs this file
        copyFile(getDigilibConfigPath(), config.getServletContext().getRealPath("/WEB-INF"));
        super.init(config);
    }

    /*
     * (non-Javadoc)
     * @see digilib.servlet.Scaler#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException
    {
        String url = req.getParameter("id");
        String fn = req.getParameter("fn");
        if (url != null)
        {
            String path = internalStorageBase
                    + url.replaceAll(navigation.getApplicationUrl() + FileServlet.SERVLET_PATH, "");
            path = path.replace("\\", "/");
            try
            {
                resp.sendRedirect(req.getRequestURL().toString() + "?fn=" + path + "&dw=1000");
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        else if (fn != null)
        {
            SessionBean session = getSession(req);
            url = navigation.getApplicationUrl() + FileServlet.SERVLET_PATH + fn.replace(internalStorageBase, "");
            if (security.check(OperationsType.READ, getUser(session), loadCollection(url, session)))
            {
                super.doGet(req, resp);
            }
            else
            {
                try
                {
                    resp.sendError(403, "imeji security: You are not allowed to view this file");
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Return the location of the digilib-config.xml
     * 
     * @return
     */
    private String getDigilibConfigPath()
    {
        try
        {
            return PropertyReader.getProperty("digilib.configuration.path");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Copy a file from a location to another on the fileSystem
     * 
     * @param from
     * @param to
     */
    private void copyFile(String from, String to)
    {
        try
        {
            FileUtils.copyFileToDirectory(new File(from), new File(to));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load a {@link CollectionImeji} from the session if possible, otherwise from jena
     * 
     * @param uri
     * @param user
     * @return
     */
    private CollectionImeji loadCollection(String url, SessionBean session)
    {
        URI collectionURI = getCollectionURI(url);
        if (collectionURI == null)
            return null;
        CollectionImeji collection = session.getCollectionCached().get(collectionURI);
        if (collection == null)
        {
            try
            {
                // important to use lazy load, otherwise high performance issue
                collection = ObjectLoader.loadCollectionLazy(collectionURI, session.getUser());
                session.getCollectionCached().put(collection.getId(), collection);
            }
            catch (Exception e)
            {
                /* user is not allowed to view this collection */
            }
        }
        return collection;
    }

    /**
     * Return the uri of the {@link CollectionImeji} of the file with this url
     * 
     * @param url
     * @return
     */
    private URI getCollectionURI(String url)
    {
        String id = storageController.getCollectionId(url);
        if (id != null)
        {
            return ObjectHelper.getURI(CollectionImeji.class, id);
        }
        else
        {
            Search s = new Search(SearchType.ALL, null);
            List<String> r = s.searchSimpleForQuery(SPARQLQueries.selectCollectionIdOfFile(url), null);
            if (!r.isEmpty())
                return URI.create(r.get(0));
            else
                return null;
        }
    }

    /**
     * Read the user in the session
     * 
     * @param req
     * @return
     */
    private User getUser(SessionBean sessionBean)
    {
        if (sessionBean != null)
        {
            return sessionBean.getUser();
        }
        return null;
    }

    /**
     * Return the {@link SessionBean} form the {@link HttpSession}
     * 
     * @param req
     * @return
     */
    private SessionBean getSession(HttpServletRequest req)
    {
        return (SessionBean)req.getSession(false).getAttribute(SessionBean.class.getSimpleName());
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy()
    {
        super.destroy();
    }
}
