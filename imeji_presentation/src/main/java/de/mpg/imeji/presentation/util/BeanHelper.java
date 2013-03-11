/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.util;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

public class BeanHelper
{
    private static Logger logger = Logger.getLogger(BeanHelper.class);

    /**
     * Return any bean stored in request scope under the specified name.
     * 
     * @param cls The bean class.
     * @return the actual or new bean instance
     */
    public static Object getRequestBean(final Class<?> cls)
    {
        String name = null;
        name = cls.getSimpleName();
        Object result = FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(name);
        logger.debug("Getting bean " + name + ": " + result);
        if (result == null)
        {
            synchronized (result)
            {
                if (result == null)
                {
                    try
                    {
                        logger.debug("Creating new request bean: " + name);
                        Object newBean = cls.newInstance();
                        FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(name, newBean);
                        return newBean;
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException("Error creating new bean of type " + cls, e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return any bean stored in session scope under the specified name.
     * 
     * @param cls The bean class.
     * @return the actual or new bean instance
     */
    public static Object getSessionBean(final Class<?> cls)
    {
        String name = null;
        name = cls.getSimpleName();
        Object result = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(name);
        logger.debug("Getting bean " + name + ": " + result);
        if (result == null)
        {
            synchronized (result)
            {
                if (result == null)
                {
                    try
                    {
                        logger.debug("Creating new session bean: " + name);
                        Object newBean = cls.newInstance();
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(name, newBean);
                        return newBean;
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException("Error creating new bean of type " + cls, e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return any bean stored in application scope under the specified name.
     * 
     * @param cls The bean class.
     * @return the actual or new bean instance
     */
    public static Object getApplicationBean(final Class<?> cls)
    {
        String name = null;
        name = cls.getSimpleName();
        Object result = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(name);
        logger.debug("Getting bean " + name + ": " + result);
        if (result == null)
        {
            synchronized (result)
            {
                if (result == null)
                {
                    try
                    {
                        logger.debug("Creating new application bean: " + name);
                        Object newBean = cls.newInstance();
                        FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put(name, newBean);
                        return newBean;
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException("Error creating new bean of type " + cls, e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param summary summary text
     */
    public static void info(String summary)
    {
        info(summary, null, null);
    }

    /**
     * @param summary summary text
     */
    public static void info(String summary, String detail)
    {
        info(summary, detail, null);
    }

    /**
     * @param component associated <code>UIComponent</code>
     * @param summary summary text
     */
    public static void info(UIComponent component, String summary)
    {
        info(summary, null, component);
    }

    /**
     * @param summary summary text
     */
    public static void info(String summary, String detail, UIComponent component)
    {
        message(summary, detail, component, FacesMessage.SEVERITY_INFO);
    }

    /**
     * @param summary summary text
     */
    public static void warn(String summary)
    {
        warn(summary, null, null);
    }

    /**
     * @param summary summary text
     */
    public static void warn(String summary, String detail)
    {
        warn(summary, detail, null);
    }

    /**
     * @param component associated <code>UIComponent</code>
     * @param summary summary text
     */
    public static void warn(UIComponent component, String summary)
    {
        warn(summary, null, component);
    }

    /**
     * @param summary summary text
     */
    public static void warn(String summary, String detail, UIComponent component)
    {
        message(summary, detail, component, FacesMessage.SEVERITY_WARN);
    }

    /**
     * @param summary summary text
     */
    public static void error(String summary)
    {
        error(summary, null, null);
    }

    /**
     * @param summary summary text
     */
    public static void error(String summary, String detail)
    {
        error(summary, detail, null);
    }

    /**
     * @param component associated <code>UIComponent</code>
     * @param summary summary text
     */
    public static void error(UIComponent component, String summary)
    {
        error(summary, null, component);
    }

    /**
     * @param summary summary text
     */
    public static void error(String summary, String detail, UIComponent component)
    {
        message(summary, detail, component, FacesMessage.SEVERITY_ERROR);
    }

    /**
     * @param summary summary text
     */
    public static void fatal(String summary)
    {
        fatal(summary, null, null);
    }

    /**
     * @param summary summary text
     */
    public static void fatal(String summary, String detail)
    {
        fatal(summary, detail, null);
    }

    /**
     * @param component associated <code>UIComponent</code>
     * @param summary summary text
     */
    public static void fatal(UIComponent component, String summary)
    {
        fatal(summary, null, component);
    }

    /**
     * @param summary summary text
     */
    public static void fatal(String summary, String detail, UIComponent component)
    {
        message(summary, detail, component, FacesMessage.SEVERITY_FATAL);
    }

    /**
     * @param summary summary text
     */
    public static void message(String summary, String detail, UIComponent component, Severity severity)
    {
        FacesMessage fm = new FacesMessage(severity, summary, detail);
        if (component == null)
        {
            FacesContext.getCurrentInstance().addMessage(null, fm);
        }
        else
        {
            FacesContext.getCurrentInstance().addMessage(component.getId(), fm);
        }
    }
}
