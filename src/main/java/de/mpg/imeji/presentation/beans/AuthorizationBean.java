///*
// *
// * CDDL HEADER START
// *
// * The contents of this file are subject to the terms of the
// * Common Development and Distribution License, Version 1.0 only
// * (the "License"). You may not use this file except in compliance
// * with the License.
// *
// * You can obtain a copy of the license at license/ESCIDOC.LICENSE
// * or http://www.escidoc.de/license.
// * See the License for the specific language governing permissions
// * and limitations under the License.
// *
// * When distributing Covered Code, include this CDDL HEADER in each
// * file and include the License file at license/ESCIDOC.LICENSE.
// * If applicable, add the following below this CDDL HEADER, with the
// * fields enclosed by brackets "[]" replaced with your own identifying
// * information: Portions Copyright [yyyy] [name of copyright owner]
// *
// * CDDL HEADER END
// */
///*
// * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
// * für wissenschaftlich-technische Information mbH and Max-Planck-
// * Gesellschaft zur Förderung der Wissenschaft e.V.
// * All rights reserved. Use is subject to license terms.
// */
//package de.mpg.imeji.presentation.beans;
//
//import java.net.URI;
//
//import de.mpg.imeji.logic.auth.Security;
//import de.mpg.imeji.logic.security.Operations.OperationsType;
//import de.mpg.imeji.logic.vo.CollectionImeji;
//import de.mpg.imeji.logic.vo.Item;
//import de.mpg.imeji.logic.vo.Properties.Status;
//import de.mpg.imeji.logic.vo.User;
//import de.mpg.imeji.presentation.session.SessionBean;
//import de.mpg.imeji.presentation.util.BeanHelper;
//
///**
// * Java Bean for the imeji authorization for the current {@link User} in the {@link SessionBean}. Uses {@link Security}
// * 
// * @author saquet (initial creation)
// * @author $Author$ (last modification)
// * @version $Revision$ $LastChangedDate$
// */
//public class AuthorizationBean
//{
//    private Security security;
//    private User user;
//    // Authorization
//    private boolean edit = false;
//    private boolean imageEdit = false;
//    private boolean view = false;
//    private boolean delete = false;
//    private boolean admin = false;
//
//    /**
//     * Default constructor
//     */
//    public AuthorizationBean()
//    {
//        security = new Security();
//    }
//
//    /**
//     * Constructor with a {@link CollectionImeji}
//     * 
//     * @param collection
//     */
//    public void init(CollectionImeji collection)
//    {
//        user = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getUser();
//        admin = security.isAdmin(user);
//        edit = security.check(OperationsType.UPDATE, user, collection);
//        imageEdit = collectionItemAllEditable(collection);
//        view = security.check(OperationsType.READ, user, collection);
//        delete = security.check(OperationsType.DELETE, user, collection);
//        if (collection.getStatus().equals(Status.RELEASED) && security.isAdmin(user))
//        {
//            delete = false;
//        }
//    }
//
//    /**
//     * Check if all item of {@link CollectionImeji} can be edited by the {@link User}
//     * 
//     * @param collection
//     * @return
//     */
//    private boolean collectionItemAllEditable(CollectionImeji collection)
//    {
//        Item item = new Item();
//        item.setCollection(collection.getId());
//        for (URI uri : collection.getImages())
//        {
//            item.setId(uri);
//            if (!security.check(OperationsType.UPDATE, user, item))
//            {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * @return the admin
//     */
//    public boolean isAdmin()
//    {
//        return admin;
//    }
//
//    /**
//     * @return the delete
//     */
//    public boolean isDelete()
//    {
//        return delete;
//    }
//
//    /**
//     * @return the edit
//     */
//    public boolean isEdit()
//    {
//        return edit;
//    }
//
//    /**
//     * @return the imageEdit
//     */
//    public boolean isImageEdit()
//    {
//        return imageEdit;
//    }
//
//    /**
//     * @return the view
//     */
//    public boolean isView()
//    {
//        return view;
//    }
//}
