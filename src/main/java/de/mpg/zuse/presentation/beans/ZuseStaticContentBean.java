package de.mpg.zuse.presentation.beans;


import java.io.IOException;
import java.net.URISyntaxException;

import de.mpg.imeji.presentation.beans.StaticContentBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.zuse.presentation.util.ZusePropertyReader;

public class ZuseStaticContentBean extends StaticContentBean {

	
	enum ZuseStaticPageEntry {
		WELCOMEPAGE("zuse.imeji.url.welcomepage"),
		PARTNERSANDCOLLABORATORERS("zuse.imeji.url.partnersandcollaboraters"),
		PROJECT("zuse.imeji.url.project"),
		IMPRINT("zuse.imeji.url.imprint"),
		ABOUTANDCONTACT("zuse.imeji.url.aboutandcontact"),
		KONRADZUSE("zuse.imeji.url.konradzuse"),
		ENCYCLOPEDIA("zuse.imeji.url.encyclopedia"),
		Z1("zuse.imeji.url.z1"),
		Z2("zuse.imeji.url.z2"),
		Z3("zuse.imeji.url.z3"),
		Z4("zuse.imeji.url.z4"),
		ASSEMBLYLINESELFREPLICATINGSYSTEMS("zuse.imeji.url.assemblyline"),
		HELIXTOWER("zuse.imeji.url.helixtower"),
		MECHANICALSYSTEM("zuse.imeji.url.mechanicalsystem"),
		ELECTROMECHANICS("zuse.imeji.url.electromechanics"),
		ELECTRONICS("zuse.imeji.url.electronics"),
		RELAY("zuse.imeji.url.relay"),
		VACUUMTUBE("zuse.imeji.url.vacuumtube"),
		TRANSISTOR("zuse.imeji.url.transistor"),
		BINARYNUMBER("zuse.imeji.url.binarynumber"),
		BIT("zuse.imeji.url.bit"),
		PUNCHEDTAPE("zuse.imeji.url.punchedtape"),
		BOOLEANALGEBRA("zuse.imeji.url.booleanalgebra"),
		LOGICGATE("zuse.imeji.url.logicgate"),
		FLOATINGPOINT("zuse.imeji.url.floatingpoint"),
		RESOURCES("zuse.imeji.url.resources"),
		SIMULATIONS("zuse.imeji.url.simulations"),
		SIMULATIONZ1("zuse.imeji.url.simuz1"),
		SIMPLEMECHANICALSWITCH("zuse.imeji.url.simplemechswitch"),
		MORECOMPLEXVARIANTMECHANICALSWITCH("zuse.imeji.url.morecomplexmechswitch"),
		MECHANICALSWITCHTOCALCULATETHEEQUIVALENCE("zuse.imeji.url.mechswitchcalcequi"),
		MECHANICALADDITIONUNIT("zuse.imeji.url.mechaddunit"),
		Z1ADDERWEBGL("zuse.imeji.url.z1adderwebgl"),
		Z1ADDERLWJGL("zuse.imeji.url.z1adderlwjgl"),
		Z1ADDERJAVAAPPLET("zuse.imeji.url.z1adderapplet"),
		SIMULATIONZ3("zuse.imeji.url.simuz3"),
		SIMENTIREZ3("zuse.imeji.url.simuz3entire"),
		SIM3DZ3("zuse.imeji.url.simu3dz3"),
		ADDERCIRCUITZ3("zuse.imeji.url.addercirz3"),
		SHIFTERCIRCUITZ3("zuse.imeji.url.shiftercirz3"),
		NORMALIZERCIRCUITZ3("zuse.imeji.url.normalizercirz3"),
		DECIMAL2BINARYCONVERTERZ3("zuse.imeji.url.dec2binconvz3"),
		DECIMALPLACEADJUSTER("zuse.imeji.url.decplaceadj"),
		ENIAC("zuse.imeji.url.eniac"),
		PLANKALKUEL("zuse.imeji.url.plankalkuel"),
		PLANKALKUELSYSTEM("zuse.imeji.url.plansys"),
		PLANKALKUELEDITOR("zuse.imeji.url.planeditor"),
		PLANKALKUELCOMPILER("zuse.imeji.url.plancompiler"),
		PLANKALKUELAPPLICATIONS("zuse.imeji.url.planapps"),
		TOU("zuse.imeji.url.tou"),
		RECONSTRUCTIONZ3("zuse.imeji.url.recontructionz3");
		
		
		boolean enabled;
		String urlString;
		
		ZuseStaticPageEntry(boolean enable, String path) {
			enabled = enable;
			urlString = path;
		}
		
		ZuseStaticPageEntry(String path) {
			
			urlString = path;
			
			try {
				if ("".equals(ZusePropertyReader.getProperty(path)))
				{
					enabled = false;
				} else {
					enabled = true;
				}
			} catch (Exception e) {
				enabled = false;
				e.printStackTrace();
			}
			
		}
		
		void setEnabled(boolean enable) {
			enabled = enable;
		}
		
		boolean getEnabled() {
			return enabled;
		}
		
		String getUrlString() {
			
			SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
	        String language = sb.getLocale().getLanguage().toLowerCase();
	        
	        String languages = "";
			try {
				languages = PropertyReader.getProperty("imeji.i18n.languages");
				if(language == null || language.isEmpty() || languages.isEmpty() || !languages.contains(language))
					language = "en";
			} catch (Exception e) {
				language = "en";
				e.printStackTrace();
			}

			return urlString+"."+language;
		}
		
	}
    
    /**
     * Construct the {@link ZuseStaticContentBean} by reading in the imeji.properties which external content are defined
     * 
     * @throws IOException
     * @throws URISyntaxException
     */
	public ZuseStaticContentBean() throws IOException, URISyntaxException {

	}
	
	public String getWelcomePageContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.WELCOMEPAGE.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
    
    public boolean isWelcomePage()
    {
        return ZuseStaticPageEntry.WELCOMEPAGE.getEnabled();
    }

    public void setWelcomePage(boolean enable)
    {
    	ZuseStaticPageEntry.WELCOMEPAGE.setEnabled(enable);
    }
	
    public String getPartnersAndCollaboratersContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.PARTNERSANDCOLLABORATORERS.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
    
    public boolean isPartnersAndCollaboraters()
    {
        return ZuseStaticPageEntry.PARTNERSANDCOLLABORATORERS.getEnabled();
    }

    public void setPartnersAndCollaboraters(boolean enable)
    {
    	ZuseStaticPageEntry.PARTNERSANDCOLLABORATORERS.setEnabled(enable);
    }
		
    public String getProjectContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.PROJECT.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
    
    public boolean isProject()
    {
        return ZuseStaticPageEntry.PROJECT.getEnabled();
    }

    public void setProject(boolean enable)
    {
    	ZuseStaticPageEntry.PROJECT.setEnabled(enable);
    }
    
    
	public String getImprintContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.IMPRINT.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
    
    public boolean isImprint()
    {
        return ZuseStaticPageEntry.IMPRINT.getEnabled();
    }

    public void setImprint(boolean enable)
    {
    	ZuseStaticPageEntry.IMPRINT.setEnabled(enable);
    }
    
	public String getAboutAndContactContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.ABOUTANDCONTACT.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
    
    public boolean isAboutAndContact()
    {
        return ZuseStaticPageEntry.ABOUTANDCONTACT.getEnabled();
    }

    public void setAboutAndContact(boolean enable)
    {
    	ZuseStaticPageEntry.ABOUTANDCONTACT.setEnabled(enable);
    }
	
	
    /**
     * Get the HTML content of the Encyclopedia page. URL of the Legal page is defined in properties.
     * 
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public String getEncyclopediaContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.ENCYCLOPEDIA.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
    
    public boolean isEncyclopedia()
    {
        return ZuseStaticPageEntry.ENCYCLOPEDIA.getEnabled();
    }

    public void setEncyclopedia(boolean enable)
    {
    	ZuseStaticPageEntry.ENCYCLOPEDIA.setEnabled(enable);
    }

    public String getZuseContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.KONRADZUSE.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
    
	public boolean isZuse() {
		return ZuseStaticPageEntry.KONRADZUSE.getEnabled();
	}

	public void setZuse(boolean zuse) {
		ZuseStaticPageEntry.KONRADZUSE.setEnabled(zuse);
	}

	public String getZ1Content() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.Z1.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isZ1() {
		return ZuseStaticPageEntry.Z1.getEnabled();
	}

	public void setZ1(boolean z1) {
		ZuseStaticPageEntry.Z1.setEnabled(z1);
	}

	public String getZ2Content() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.Z2.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isZ2() {
		return ZuseStaticPageEntry.Z2.getEnabled();
	}

	public void setZ2(boolean z2) {
		ZuseStaticPageEntry.Z2.setEnabled(z2);
	}

	public String getZ3Content() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.Z3.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isZ3() {
		return ZuseStaticPageEntry.Z3.getEnabled();
	}

	public void setZ3(boolean z3) {
		ZuseStaticPageEntry.Z3.setEnabled(z3);
	}

	public String getZ4Content() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.Z4.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isZ4() {
		return ZuseStaticPageEntry.Z4.getEnabled();
	}

	public void setZ4(boolean z4) {
		ZuseStaticPageEntry.Z4.setEnabled(z4);
	}

	public String getAssemblyLineSelfReplicatingSystemsContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.ASSEMBLYLINESELFREPLICATINGSYSTEMS.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isAssemblyLineSelfReplicatingSystems() {
		return ZuseStaticPageEntry.ASSEMBLYLINESELFREPLICATINGSYSTEMS.getEnabled();
	}

	public void setAssemblyLineSelfReplicatingSystems(
			boolean assemblyLineSelfReplicatingSystems) {
		ZuseStaticPageEntry.ASSEMBLYLINESELFREPLICATINGSYSTEMS.setEnabled(assemblyLineSelfReplicatingSystems);
	}
	
	public String getHelixTowerContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.HELIXTOWER.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isHelixTower() {
		return ZuseStaticPageEntry.HELIXTOWER.getEnabled();
	}

	public void setHelixTower(boolean helixTower) {
		ZuseStaticPageEntry.HELIXTOWER.setEnabled(helixTower);
	}

	public String getMechanicalSystemContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.MECHANICALSYSTEM.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isMechanicalSystem() {
		return ZuseStaticPageEntry.MECHANICALSYSTEM.getEnabled();
	}

	public void setMechanicalSystem(boolean mechanicalSystem) {
		ZuseStaticPageEntry.MECHANICALSYSTEM.setEnabled(mechanicalSystem);
	}

	public String getElectromechanicsContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.ELECTROMECHANICS.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isElectromechanics() {
		return ZuseStaticPageEntry.ELECTROMECHANICS.getEnabled();
	}

	public void setElectromechanics(boolean electromechanics) {
		ZuseStaticPageEntry.ELECTROMECHANICS.setEnabled(electromechanics);
	}

	public String getElectronicsContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.ELECTRONICS.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isElectronics() {
		return ZuseStaticPageEntry.ELECTRONICS.getEnabled();
	}

	public void setElectronics(boolean electronics) {
		ZuseStaticPageEntry.ELECTRONICS.setEnabled(electronics);
	}

	public String getRelayContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.RELAY.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isRelay() {
		return ZuseStaticPageEntry.RELAY.getEnabled();
	}

	public void setRelay(boolean relay) {
		ZuseStaticPageEntry.RELAY.setEnabled(relay);
	}
	
	public String getVacuumTubeContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.VACUUMTUBE.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isVacuumTube() {
		return ZuseStaticPageEntry.VACUUMTUBE.getEnabled();
	}

	public void setVacuumTube(boolean vacuumTube) {
		ZuseStaticPageEntry.VACUUMTUBE.setEnabled(vacuumTube);
	}

	public String getTransistorContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.TRANSISTOR.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isTransistor() {
		return ZuseStaticPageEntry.TRANSISTOR.getEnabled();
	}

	public void setTransistor(boolean transistor) {
		ZuseStaticPageEntry.TRANSISTOR.setEnabled(transistor);
	}
	
	public String getBinaryNumberContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.BINARYNUMBER.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isBinaryNumber() {
		return ZuseStaticPageEntry.BINARYNUMBER.getEnabled();
	}

	public void setBinaryNumber(boolean binaryNumber) {
		ZuseStaticPageEntry.BINARYNUMBER.setEnabled(binaryNumber);
	}

	public String getBitContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.BIT.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isBit() {
		return ZuseStaticPageEntry.BIT.getEnabled();
	}

	public void setBit(boolean bit) {
		ZuseStaticPageEntry.BIT.setEnabled(bit);
	}
	
	public String getPunchedTapeContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.PUNCHEDTAPE.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isPunchedTape() {
		return ZuseStaticPageEntry.PUNCHEDTAPE.getEnabled();
	}

	public void setPunchedTape(boolean punchedTape) {
		ZuseStaticPageEntry.PUNCHEDTAPE.setEnabled(punchedTape);
	}
	
	public String getBooleanAlgebraContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.BOOLEANALGEBRA.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isBooleanAlgebra() {
		return ZuseStaticPageEntry.BOOLEANALGEBRA.getEnabled();
	}

	public void setBooleanAlgebra(boolean booleanAlgebra) {
		ZuseStaticPageEntry.BOOLEANALGEBRA.setEnabled(booleanAlgebra);
	}
	
	public String getLogicGateContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.LOGICGATE.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isLogicGate() {
		return ZuseStaticPageEntry.LOGICGATE.getEnabled();
	}

	public void setLogicGate(boolean logicGate) {
		ZuseStaticPageEntry.LOGICGATE.setEnabled(logicGate);
	}
	
	public String getFloatingPointContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.FLOATINGPOINT.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isFloatingPoint() {
		return ZuseStaticPageEntry.FLOATINGPOINT.getEnabled();
	}

	public void setFloatingPoint(boolean floatingPoint) {
		ZuseStaticPageEntry.FLOATINGPOINT.setEnabled(floatingPoint);
	}
	
	public String getResourcesContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.RESOURCES.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isResources() {
		return ZuseStaticPageEntry.RESOURCES.getEnabled();
	}

	public void setResources(boolean resources) {
		ZuseStaticPageEntry.RESOURCES.setEnabled(resources);
	}
	
	public String getSimulationsContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.SIMULATIONS.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }	

	public boolean isSimulations() {
		return ZuseStaticPageEntry.SIMULATIONS.getEnabled();
	}

	public void setSimulations(boolean simulations) {
		ZuseStaticPageEntry.SIMULATIONS.setEnabled(simulations);
	}

	public String getSimulationsZ1Content() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.SIMULATIONZ1.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isSimulationsZ1() {
		return ZuseStaticPageEntry.SIMULATIONZ1.getEnabled();
	}

	public void setSimulationsZ1(boolean simulationsZ1) {
		ZuseStaticPageEntry.SIMULATIONZ1.setEnabled(simulationsZ1);
	}
	
	public String getZ1SimpleMechSwitchContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.SIMPLEMECHANICALSWITCH.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isZ1SimpleMechSwitch() {
		return ZuseStaticPageEntry.SIMPLEMECHANICALSWITCH.getEnabled();
	}

	public void setZ1SimpleMechSwitch(boolean z1SimpleMechSwitch) {
		ZuseStaticPageEntry.SIMPLEMECHANICALSWITCH.setEnabled(z1SimpleMechSwitch);
	}
	
	public String getZ1MoreComplexMechSwitchContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.MORECOMPLEXVARIANTMECHANICALSWITCH.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isZ1MoreComplexMechSwitch() {
		return ZuseStaticPageEntry.MORECOMPLEXVARIANTMECHANICALSWITCH.getEnabled();
	}

	public void setZ1MoreComplexMechSwitch(boolean z1MoreComplexMechSwitch) {
		ZuseStaticPageEntry.MORECOMPLEXVARIANTMECHANICALSWITCH.setEnabled(z1MoreComplexMechSwitch);
	}
	
	public String getZ1MechSwitchCalcEquivalenceContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.MECHANICALSWITCHTOCALCULATETHEEQUIVALENCE.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isZ1MechSwitchCalcEquivalence() {
		return ZuseStaticPageEntry.MECHANICALSWITCHTOCALCULATETHEEQUIVALENCE.getEnabled();
	}

	public void setZ1MechSwitchCalcEquivalence(boolean z1MechSwitchCalcEquivalence) {
		ZuseStaticPageEntry.MECHANICALSWITCHTOCALCULATETHEEQUIVALENCE.setEnabled(z1MechSwitchCalcEquivalence);
	}
	
	public String getZ1MechAddUnitContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.MECHANICALADDITIONUNIT.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isZ1MechAddUnit() {
		return ZuseStaticPageEntry.MECHANICALADDITIONUNIT.getEnabled();
	}

	public void setZ1MechAddUnit(boolean z1MechAddUnit) {
		ZuseStaticPageEntry.MECHANICALADDITIONUNIT.setEnabled(z1MechAddUnit);
	}

	public String getZ1AdderWebGLContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.Z1ADDERWEBGL.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isZ1AdderWebGL() {
		return ZuseStaticPageEntry.Z1ADDERWEBGL.getEnabled();
	}

	public void setZ1AdderWebGL(boolean z1AdderWebGL) {
		ZuseStaticPageEntry.Z1ADDERWEBGL.setEnabled(z1AdderWebGL);
	}
	
	public String getZ1AdderLWJGLContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.Z1ADDERLWJGL.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isZ1AdderLWJGL() {
		return ZuseStaticPageEntry.Z1ADDERLWJGL.getEnabled();
	}

	public void setZ1AdderLWJGL(boolean z1AdderLWJGL) {
		ZuseStaticPageEntry.Z1ADDERLWJGL.setEnabled(z1AdderLWJGL);
	}
	
	public String getZ1AdderJavaAppletContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.Z1ADDERJAVAAPPLET.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isZ1AdderJavaApplet() {
		return ZuseStaticPageEntry.Z1ADDERJAVAAPPLET.getEnabled();
	}

	public void setZ1AdderJavaApplet(boolean z1AdderJavaApplet) {
		ZuseStaticPageEntry.Z1ADDERJAVAAPPLET.setEnabled(z1AdderJavaApplet);
	}
	
	public String getSimulationsZ3Content() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.SIMULATIONZ3.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isSimulationsZ3() {
		return ZuseStaticPageEntry.SIMULATIONZ3.getEnabled();
	}

	public void setSimulationsZ3(boolean simulationsZ3) {
		ZuseStaticPageEntry.SIMULATIONZ3.setEnabled(simulationsZ3);
	}

	public String getZ3SimulationsEntireContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.SIMENTIREZ3.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isZ3SimulationsEntire() {
		return ZuseStaticPageEntry.SIMENTIREZ3.getEnabled();
	}

	public void setZ3SimulationsEntire(boolean z3SimulationsEntire) {
		ZuseStaticPageEntry.SIMENTIREZ3.setEnabled(z3SimulationsEntire);
	}

	public String getZ3Simulations3DContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.SIM3DZ3.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isZ3Simulations3D() {
		return ZuseStaticPageEntry.SIM3DZ3.getEnabled();
	}

	public void setZ3Simulations3D(boolean z3Simulations3D) {
		ZuseStaticPageEntry.SIM3DZ3.setEnabled(z3Simulations3D);
	}
	
	public String getZ3AdderCircuitContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.ADDERCIRCUITZ3.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isZ3AdderCircuit() {
		return ZuseStaticPageEntry.ADDERCIRCUITZ3.getEnabled();
	}

	public void setZ3AdderCircuit(boolean z3AdderCircuit) {
		ZuseStaticPageEntry.ADDERCIRCUITZ3.setEnabled(z3AdderCircuit);
	}

	public String getZ3ShifterCircuitContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.SHIFTERCIRCUITZ3.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isZ3ShifterCircuit() {
		return ZuseStaticPageEntry.SHIFTERCIRCUITZ3.getEnabled();
	}

	public void setZ3ShifterCircuit(boolean z3ShifterCircuit) {
		ZuseStaticPageEntry.SHIFTERCIRCUITZ3.setEnabled(z3ShifterCircuit);
	}
	
	public String getZ3NormalizerCircuitContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.NORMALIZERCIRCUITZ3.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isZ3NormalizerCircuit() {
		return ZuseStaticPageEntry.NORMALIZERCIRCUITZ3.getEnabled();
	}

	public void setZ3NormalizerCircuit(boolean z3NormalizerCircuit) {
		ZuseStaticPageEntry.NORMALIZERCIRCUITZ3.setEnabled(z3NormalizerCircuit);
	}
	
	public String getZ3Dec2BinConveterContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.DECIMAL2BINARYCONVERTERZ3.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isZ3Dec2BinConveter() {
		return ZuseStaticPageEntry.DECIMAL2BINARYCONVERTERZ3.getEnabled();
	}

	public void setZ3Dec2BinConveter(boolean z3Dec2BinConveter) {
		ZuseStaticPageEntry.DECIMAL2BINARYCONVERTERZ3.setEnabled(z3Dec2BinConveter);
	}

	public String getZ3DecPlaceAdjusterContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.DECIMALPLACEADJUSTER.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isZ3DecPlaceAdjuster() {
		return ZuseStaticPageEntry.DECIMALPLACEADJUSTER.getEnabled();
	}

	public void setZ3DecPlaceAdjuster(boolean z3DecPlaceAdjuster) {
		ZuseStaticPageEntry.DECIMALPLACEADJUSTER.setEnabled(z3DecPlaceAdjuster);
	}
	
	public String getEniacContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.ENIAC.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isEniac() {
		return ZuseStaticPageEntry.ENIAC.getEnabled();
	}

	public void setEniac(boolean eniac) {
		ZuseStaticPageEntry.ENIAC.setEnabled(eniac);
	}
	
	public String getPlankalkuelContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.PLANKALKUEL.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isPlankalkuel() {
		return ZuseStaticPageEntry.PLANKALKUEL.getEnabled();
	}

	public void setPlankalkuel(boolean plankalkuel) {
		ZuseStaticPageEntry.PLANKALKUEL.setEnabled(plankalkuel);
	}
	
	public String getPlankalkuelSystemContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.PLANKALKUELSYSTEM.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isPlankalkuelSystem() {
		return ZuseStaticPageEntry.PLANKALKUELSYSTEM.getEnabled();
	}

	public void setPlankalkuelSystem(boolean plankalkuelSystem) {
		ZuseStaticPageEntry.PLANKALKUELSYSTEM.setEnabled(plankalkuelSystem);
	}
	
	public String getPlankalkuelEditorContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.PLANKALKUELEDITOR.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isPlankalkuelEditor() {
		return ZuseStaticPageEntry.PLANKALKUELEDITOR.getEnabled();
	}

	public void setPlankalkuelEditor(boolean plankalkuelEditor) {
		ZuseStaticPageEntry.PLANKALKUELEDITOR.setEnabled(plankalkuelEditor);
	}

	public String getPlankalkuelCompilerContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.PLANKALKUELCOMPILER.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isPlankalkuelCompiler() {
		return ZuseStaticPageEntry.PLANKALKUELCOMPILER.getEnabled();
	}

	public void setPlankalkuelCompiler(boolean plankalkuelCompiler) {
		ZuseStaticPageEntry.PLANKALKUELCOMPILER.setEnabled(plankalkuelCompiler);
	}

	public String getPlankalkuelapplicationContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.PLANKALKUELAPPLICATIONS.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isPlankalkuelapplication() {
		return ZuseStaticPageEntry.PLANKALKUELAPPLICATIONS.getEnabled();
	}

	public void setPlankalkuelapplication(boolean plankalkuelapplication) {
		ZuseStaticPageEntry.PLANKALKUELAPPLICATIONS.setEnabled(plankalkuelapplication);
	}
	
	public String getTouContent() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.TOU.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }
	
	public boolean isTou() {
		return ZuseStaticPageEntry.TOU.getEnabled();
	}

	public void setTou(boolean tou) {
		ZuseStaticPageEntry.TOU.setEnabled(tou);
	}
	
	public String getReconstructionZ3Content() throws IOException, URISyntaxException
    {
        String html = "";
        String urlString = ZusePropertyReader.getProperty(ZuseStaticPageEntry.RECONSTRUCTIONZ3.getUrlString());
        try
        {
            html = getContent(urlString);
        }
        catch (Exception e)
        {
            html = urlString
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

	public boolean isReconstructionZ3() {
		return ZuseStaticPageEntry.RECONSTRUCTIONZ3.getEnabled();
	}

	public void setReconstructionZ3(boolean reconstructionZ3) {
		ZuseStaticPageEntry.RECONSTRUCTIONZ3.setEnabled(reconstructionZ3);
	}

}
