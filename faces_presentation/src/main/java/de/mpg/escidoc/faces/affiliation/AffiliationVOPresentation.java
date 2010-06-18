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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.faces.affiliation;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.escidoc.faces.statistics.StatisticsBean;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.OrgUnitsSearchResult;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;

public class AffiliationVOPresentation extends AffiliationVO implements Comparable<AffiliationVOPresentation>
{
    private List<AffiliationVOPresentation> children = null;
    private AffiliationVOPresentation parent = null;
    private String namePath;
    private String idPath;
    
    private List<AffiliationVO> predecessors = new java.util.ArrayList<AffiliationVO>();
    
    private List<AffiliationVO> successors = null;

    /**
     * Public constructor
     */
    public AffiliationVOPresentation ()
    {
        
    }
    
    public AffiliationVOPresentation(AffiliationVO affiliation)
    {
        super(affiliation);
        this.namePath = getDetails().getName();
        this.idPath = getReference().getObjectId();       
        this.predecessors = getAffiliationVOfromRO(getPredecessorAffiliations());       
    }
    
    
//    public List<AffiliationVOPresentation> AffiliationVOPresentationList (List<AffiliationVO> list)
//    {
//        List<AffiliationVOPresentation> affiliationList = new ArrayList<AffiliationVOPresentation>();
//        for (int i = 0; i < list.size(); i++)
//        {
//            affiliationList.add(new AffiliationVOPresentation(list.get(i)));
//        }
//        
//        return affiliationList;
//    }

    public List<AffiliationVOPresentation> getChildren() throws Exception
    {
        if (children == null)
        {
            children = searchChildAffiliations(this);
        }
        return children;
    }
    
    public int compareTo(AffiliationVOPresentation other)
    {
        return getSortOrder().compareTo(other.getSortOrder());
    }
    
    public MdsOrganizationalUnitDetailsVO getDetails()
    {
        if (getMetadataSets().size() > 0 && getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
        {
            return (MdsOrganizationalUnitDetailsVO) getMetadataSets().get(0);
        }
        else
        {
            return new MdsOrganizationalUnitDetailsVO();
        }
    }

    public boolean getMps()
    {
        return getDetails().getAlternativeNames().contains("MPS");
    }

    public boolean getTopLevel()
    {
        return (parent == null);
    }

    /**
     * This returns a description of the affiliation in a html form.
     * 
     * @return html description
     */
    public String getHtmlDescription()
    {
        StringBuffer html = new StringBuffer();
        html.append("<html><head></head><body>");
        html.append("<div class=\"affDetails\"><h1>" + "Details" + "</h1>");
        html.append("<div class=\"formField\">");
        if (getDetails().getDescriptions().size() > 0 && !"".equals(getDetails().getDescriptions().get(0)))
        {
            html.append("<div>");
            html.append(getDetails().getDescriptions().get(0));
            html.append("</div><br/>");
        }
        for (IdentifierVO identifier : getDetails().getIdentifiers())
        {
            if (!identifier.getId().trim().equals(""))
            {
                html.append("<span>, &nbsp;");
                html.append(identifier.getId());
                html.append("</span>");
            }
        }
        html.append("</div></div>");
        html.append("</body></html>");
        return html.toString();
    }

    public AffiliationVOPresentation getParent()
    {
        return parent;
    }

    public void setParent(AffiliationVOPresentation parent)
    {
        this.parent = parent;
    }

    /** Returns the complete path to this affiliation as a string with the name of the affiliations. */
    public String getNamePath()
    {
        return namePath;
    }

    public void setNamePath(String path)
    {
        this.namePath = path;
    }

    /** Returns the complete path to this affiliation as a string with the ids of the affiliations */
    public String getIdPath()
    {
        return idPath;
    }

    public void setIdPath(String idPath)
    {
        this.idPath = idPath;
    }

    public String getSortOrder()
    {
        if ("closed".equals(this.getPublicStatus()))
        {
            return "3" + getName().toLowerCase();
        }
        else if ("opened".equals(this.getPublicStatus()))
        {
            return "1" + getName().toLowerCase();
        }
        else if ("created".equals(this.getPublicStatus()))
        {
            return "2" + getName().toLowerCase();
        }
        else
        {
            return "9" + getName().toLowerCase();
        }
    }
    
    public String getName()
    {
        if (getMetadataSets().size() > 0 && getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO)
        {
            return ((MdsOrganizationalUnitDetailsVO) getMetadataSets().get(0)).getName();
        }
        else
        {
            return null;
        }
    }

    public List<String> getUris()
    {
        List<IdentifierVO> identifiers = getDefaultMetadata().getIdentifiers();
        List<String> uriList = new ArrayList<String>();
        for (IdentifierVO identifier : identifiers)
        {
            if (identifier.getType() != null && identifier.getType().equals(IdentifierVO.IdType.URI))
            {
                uriList.add(identifier.getId());
            }
        }
        return uriList;
    }

    public boolean getIsClosed()
    {
        return getPublicStatus().equals("closed");
    }

    private List<AffiliationVO> getAffiliationVOfromRO( List<AffiliationRO> affiliations  )
    {
        List<AffiliationVO> transformedAffs = new ArrayList<AffiliationVO>();
        InitialContext initialContext = null;
        XmlTransforming xmlTransforming = null;
        if( affiliations.size() == 0 ) {
            return transformedAffs;
        }
        try
        {
            initialContext = new InitialContext();
            xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
            for( AffiliationRO affiliation : affiliations )
            {
                StatisticsBean statisticsBean = (StatisticsBean) BeanHelper.getApplicationBean(StatisticsBean.class);
                String userHandle = statisticsBean.getAdminUserHandle();
                OrganizationalUnitHandler ouHandler = ServiceLocator.getOrganizationalUnitHandler(userHandle);
                String ouXml = ouHandler.retrieve(affiliation.getObjectId());
                AffiliationVO affVO = xmlTransforming.transformToAffiliation(ouXml);
                transformedAffs.add(affVO);
            }
            return transformedAffs;
        } 
        catch (Exception e)
        {
            return transformedAffs;
        }
    }
    
    /**
     * Returns all child affiliations of a given affiliation.
     * 
     * @param parentAffiliation The parent affiliation
     * 
     * @return all child affiliations
     * @throws Exception if framework access fails
     */
    public List<AffiliationVOPresentation> searchChildAffiliations(AffiliationVOPresentation parentAffiliation) throws Exception
    {

        InitialContext initialContext = new InitialContext();
        Search search = (Search) initialContext.lookup(Search.SERVICE_NAME);
        AffiliationBean affTree = new AffiliationBean();
        PlainCqlQuery cqlQuery = new PlainCqlQuery("(escidoc.parent.objid=" + parentAffiliation.getReference().getObjectId() + ")");
        OrgUnitsSearchResult results = search.searchForOrganizationalUnits(cqlQuery);
        
        List<AffiliationVOPresentation> wrappedAffiliationList =
            affTree.convertToAffiliationVOPresentationList(results.getResults());
        
        for (AffiliationVOPresentation affiliationVOPresentation : wrappedAffiliationList)
        {
            affiliationVOPresentation.setParent(parentAffiliation);
            affiliationVOPresentation.setNamePath(affiliationVOPresentation.getDetails().getName()+", "+parentAffiliation.getNamePath());
            affiliationVOPresentation.setIdPath(affiliationVOPresentation.getReference().getObjectId() +" "+parentAffiliation.getIdPath());
        }
        return wrappedAffiliationList; 
    }
}
