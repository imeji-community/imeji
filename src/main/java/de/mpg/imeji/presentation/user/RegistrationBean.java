package de.mpg.imeji.presentation.user;

import de.mpg.imeji.logic.util.UrlHelper;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Bean for registration workflow
 *
 * @author makarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "RegistrationBean")
@ViewScoped
public class RegistrationBean {

    @PostConstruct
    public void init()
    {
        //get token etc
        String q = UrlHelper.getParameterValue("q");
    }
}
