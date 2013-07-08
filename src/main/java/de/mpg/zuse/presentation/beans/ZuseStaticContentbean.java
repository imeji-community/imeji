package de.mpg.zuse.presentation.beans;

import java.io.IOException;
import java.net.URISyntaxException;

import de.mpg.imeji.presentation.beans.StaticContentBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.zuse.presentation.util.ZusePropertyReader;

public class ZuseStaticContentbean extends StaticContentBean {

	
	enum ZuseStaticPageEntry {
		ENCYCLOPEDIA("zuse.imeji.url.encyclopedia"),
		KONRADZUSE("zuse.imeji.url.konradzuse"),
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
		PLANKALKUELAPPLICATIONS("zuse.imeji.url.apps"),
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
	
    
    
 // Pages of zuse
    
    /**
     * Construct the {@link ZuseStaticContentBean} by reading in the imeji.properties which external content are defined
     * 
     * @throws IOException
     * @throws URISyntaxException
     */
	public ZuseStaticContentbean() throws IOException, URISyntaxException {

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

	public boolean isZuse() {
		return ZuseStaticPageEntry.KONRADZUSE.getEnabled();
	}

	public void setZuse(boolean zuse) {
		ZuseStaticPageEntry.KONRADZUSE.setEnabled(zuse);
	}

	public boolean isZ1() {
		return ZuseStaticPageEntry.Z1.getEnabled();
	}

	public void setZ1(boolean z1) {
		ZuseStaticPageEntry.Z1.setEnabled(z1);
	}

	public boolean isZ2() {
		return ZuseStaticPageEntry.Z2.getEnabled();
	}

	public void setZ2(boolean z2) {
		ZuseStaticPageEntry.Z2.setEnabled(z2);
	}

	public boolean isZ3() {
		return ZuseStaticPageEntry.Z3.getEnabled();
	}

	public void setZ3(boolean z3) {
		ZuseStaticPageEntry.Z3.setEnabled(z3);
	}

	public boolean isZ4() {
		return ZuseStaticPageEntry.Z4.getEnabled();
	}

	public void setZ4(boolean z4) {
		ZuseStaticPageEntry.Z4.setEnabled(z4);
	}

	public boolean isAssemblyLineSelfReplicatingSystems() {
		return ZuseStaticPageEntry.ASSEMBLYLINESELFREPLICATINGSYSTEMS.getEnabled();
	}

	public void setAssemblyLineSelfReplicatingSystems(
			boolean assemblyLineSelfReplicatingSystems) {
		ZuseStaticPageEntry.ASSEMBLYLINESELFREPLICATINGSYSTEMS.setEnabled(assemblyLineSelfReplicatingSystems);
	}

	public boolean isHelixTower() {
		return ZuseStaticPageEntry.HELIXTOWER.getEnabled();
	}

	public void setHelixTower(boolean helixTower) {
		ZuseStaticPageEntry.HELIXTOWER.setEnabled(helixTower);
	}

	public boolean isMechanicalSystem() {
		return ZuseStaticPageEntry.MECHANICALSYSTEM.getEnabled();
	}

	public void setMechanicalSystem(boolean mechanicalSystem) {
		ZuseStaticPageEntry.MECHANICALSYSTEM.setEnabled(mechanicalSystem);
	}

	public boolean isElectromechanics() {
		return ZuseStaticPageEntry.ELECTROMECHANICS.getEnabled();
	}

	public void setElectromechanics(boolean electromechanics) {
		ZuseStaticPageEntry.ELECTROMECHANICS.setEnabled(electromechanics);
	}

	public boolean isElectronics() {
		return ZuseStaticPageEntry.ELECTRONICS.getEnabled();
	}

	public void setElectronics(boolean electronics) {
		ZuseStaticPageEntry.ELECTRONICS.setEnabled(electronics);
	}

	public boolean isRelay() {
		return ZuseStaticPageEntry.RELAY.getEnabled();
	}

	public void setRelay(boolean relay) {
		ZuseStaticPageEntry.RELAY.setEnabled(relay);
	}

	public boolean isVacuumTube() {
		return ZuseStaticPageEntry.VACUUMTUBE.getEnabled();
	}

	public void setVacuumTube(boolean vacuumTube) {
		ZuseStaticPageEntry.VACUUMTUBE.setEnabled(vacuumTube);
	}

	public boolean isTransistor() {
		return ZuseStaticPageEntry.TRANSISTOR.getEnabled();
	}

	public void setTransistor(boolean transistor) {
		ZuseStaticPageEntry.TRANSISTOR.setEnabled(transistor);
	}

	public boolean isBinaryNumber() {
		return ZuseStaticPageEntry.BINARYNUMBER.getEnabled();
	}

	public void setBinaryNumber(boolean binaryNumber) {
		ZuseStaticPageEntry.BINARYNUMBER.setEnabled(binaryNumber);
	}

	public boolean isBit() {
		return ZuseStaticPageEntry.BIT.getEnabled();
	}

	public void setBit(boolean bit) {
		ZuseStaticPageEntry.BIT.setEnabled(bit);
	}

	public boolean isPunchedTape() {
		return ZuseStaticPageEntry.PUNCHEDTAPE.getEnabled();
	}

	public void setPunchedTape(boolean punchedTape) {
		ZuseStaticPageEntry.PUNCHEDTAPE.setEnabled(punchedTape);
	}

	public boolean isBooleanAlgebra() {
		return ZuseStaticPageEntry.BOOLEANALGEBRA.getEnabled();
	}

	public void setBooleanAlgebra(boolean booleanAlgebra) {
		ZuseStaticPageEntry.BOOLEANALGEBRA.setEnabled(booleanAlgebra);
	}

	public boolean isLogicGate() {
		return ZuseStaticPageEntry.LOGICGATE.getEnabled();
	}

	public void setLogicGate(boolean logicGate) {
		ZuseStaticPageEntry.LOGICGATE.setEnabled(logicGate);
	}

	public boolean isFloatingPoint() {
		return ZuseStaticPageEntry.FLOATINGPOINT.getEnabled();
	}

	public void setFloatingPoint(boolean floatingPoint) {
		ZuseStaticPageEntry.FLOATINGPOINT.setEnabled(floatingPoint);
	}

	public boolean isResources() {
		return ZuseStaticPageEntry.RESOURCES.getEnabled();
	}

	public void setResources(boolean resources) {
		ZuseStaticPageEntry.RESOURCES.setEnabled(resources);
	}

	public boolean isSimulations() {
		return ZuseStaticPageEntry.SIMULATIONS.getEnabled();
	}

	public void setSimulations(boolean simulations) {
		ZuseStaticPageEntry.SIMULATIONS.setEnabled(simulations);
	}

	public boolean isSimulationsZ1() {
		return ZuseStaticPageEntry.SIMULATIONZ1.getEnabled();
	}

	public void setSimulationsZ1(boolean simulationsZ1) {
		ZuseStaticPageEntry.SIMULATIONZ1.setEnabled(simulationsZ1);
	}

	public boolean isZ1SimpleMechSwitch() {
		return ZuseStaticPageEntry.SIMPLEMECHANICALSWITCH.getEnabled();
	}

	public void setZ1SimpleMechSwitch(boolean z1SimpleMechSwitch) {
		ZuseStaticPageEntry.SIMPLEMECHANICALSWITCH.setEnabled(z1SimpleMechSwitch);
	}

	public boolean isZ1MoreComplexMechSwitch() {
		return ZuseStaticPageEntry.MORECOMPLEXVARIANTMECHANICALSWITCH.getEnabled();
	}

	public void setZ1MoreComplexMechSwitch(boolean z1MoreComplexMechSwitch) {
		ZuseStaticPageEntry.MORECOMPLEXVARIANTMECHANICALSWITCH.setEnabled(z1MoreComplexMechSwitch);
	}

	public boolean isZ1MechSwitchCalcEquivalence() {
		return ZuseStaticPageEntry.MECHANICALSWITCHTOCALCULATETHEEQUIVALENCE.getEnabled();
	}

	public void setZ1MechSwitchCalcEquivalence(boolean z1MechSwitchCalcEquivalence) {
		ZuseStaticPageEntry.MECHANICALSWITCHTOCALCULATETHEEQUIVALENCE.setEnabled(z1MechSwitchCalcEquivalence);
	}

	public boolean isZ1MechAddUnit() {
		return ZuseStaticPageEntry.MECHANICALADDITIONUNIT.getEnabled();
	}

	public void setZ1MechAddUnit(boolean z1MechAddUnit) {
		ZuseStaticPageEntry.MECHANICALADDITIONUNIT.setEnabled(z1MechAddUnit);
	}

	public boolean isZ1AdderWebGL() {
		return ZuseStaticPageEntry.Z1ADDERWEBGL.getEnabled();
	}

	public void setZ1AdderWebGL(boolean z1AdderWebGL) {
		ZuseStaticPageEntry.Z1ADDERWEBGL.setEnabled(z1AdderWebGL);
	}

	public boolean isZ1AdderLWJGL() {
		return ZuseStaticPageEntry.Z1ADDERLWJGL.getEnabled();
	}

	public void setZ1AdderLWJGL(boolean z1AdderLWJGL) {
		ZuseStaticPageEntry.Z1ADDERLWJGL.setEnabled(z1AdderLWJGL);
	}

	public boolean isZ1AdderJavaApplet() {
		return ZuseStaticPageEntry.Z1ADDERJAVAAPPLET.getEnabled();
	}

	public void setZ1AdderJavaApplet(boolean z1AdderJavaApplet) {
		ZuseStaticPageEntry.Z1ADDERJAVAAPPLET.setEnabled(z1AdderJavaApplet);
	}

	public boolean isSimulationsZ3() {
		return ZuseStaticPageEntry.SIMULATIONZ3.getEnabled();
	}

	public void setSimulationsZ3(boolean simulationsZ3) {
		ZuseStaticPageEntry.SIMULATIONZ3.setEnabled(simulationsZ3);
	}

	public boolean isZ3SimulationsEntire() {
		return ZuseStaticPageEntry.SIMENTIREZ3.getEnabled();
	}

	public void setZ3SimulationsEntire(boolean z3SimulationsEntire) {
		ZuseStaticPageEntry.SIMENTIREZ3.setEnabled(z3SimulationsEntire);
	}

	public boolean isZ3Simulations3D() {
		return ZuseStaticPageEntry.SIM3DZ3.getEnabled();
	}

	public void setZ3Simulations3D(boolean z3Simulations3D) {
		ZuseStaticPageEntry.SIM3DZ3.setEnabled(z3Simulations3D);
	}

	public boolean isZ3AdderCircuit() {
		return ZuseStaticPageEntry.ADDERCIRCUITZ3.getEnabled();
	}

	public void setZ3AdderCircuit(boolean z3AdderCircuit) {
		ZuseStaticPageEntry.ADDERCIRCUITZ3.setEnabled(z3AdderCircuit);
	}

	public boolean isZ3ShifterCircuit() {
		return ZuseStaticPageEntry.SHIFTERCIRCUITZ3.getEnabled();
	}

	public void setZ3ShifterCircuit(boolean z3ShifterCircuit) {
		ZuseStaticPageEntry.SHIFTERCIRCUITZ3.setEnabled(z3ShifterCircuit);
	}

	public boolean isZ3NormalizerCircuit() {
		return ZuseStaticPageEntry.NORMALIZERCIRCUITZ3.getEnabled();
	}

	public void setZ3NormalizerCircuit(boolean z3NormalizerCircuit) {
		ZuseStaticPageEntry.NORMALIZERCIRCUITZ3.setEnabled(z3NormalizerCircuit);
	}

	public boolean isZ3Dec2BinConveter() {
		return ZuseStaticPageEntry.DECIMAL2BINARYCONVERTERZ3.getEnabled();
	}

	public void setZ3Dec2BinConveter(boolean z3Dec2BinConveter) {
		ZuseStaticPageEntry.DECIMAL2BINARYCONVERTERZ3.setEnabled(z3Dec2BinConveter);
	}

	public boolean isZ3DecPlaceAdjuster() {
		return ZuseStaticPageEntry.DECIMALPLACEADJUSTER.getEnabled();
	}

	public void setZ3DecPlaceAdjuster(boolean z3DecPlaceAdjuster) {
		ZuseStaticPageEntry.DECIMALPLACEADJUSTER.setEnabled(z3DecPlaceAdjuster);
	}

	public boolean isEniac() {
		return ZuseStaticPageEntry.ENIAC.getEnabled();
	}

	public void setEniac(boolean eniac) {
		ZuseStaticPageEntry.ENIAC.setEnabled(eniac);
	}

	public boolean isPlankalkuel() {
		return ZuseStaticPageEntry.PLANKALKUEL.getEnabled();
	}

	public void setPlankalkuel(boolean plankalkuel) {
		ZuseStaticPageEntry.PLANKALKUEL.setEnabled(plankalkuel);
	}

	public boolean isPlankalkuelSystem() {
		return ZuseStaticPageEntry.PLANKALKUELSYSTEM.getEnabled();
	}

	public void setPlankalkuelSystem(boolean plankalkuelSystem) {
		ZuseStaticPageEntry.PLANKALKUELSYSTEM.setEnabled(plankalkuelSystem);
	}

	public boolean isPlankalkuelEditor() {
		return ZuseStaticPageEntry.PLANKALKUELEDITOR.getEnabled();
	}

	public void setPlankalkuelEditor(boolean plankalkuelEditor) {
		ZuseStaticPageEntry.PLANKALKUELEDITOR.setEnabled(plankalkuelEditor);
	}

	public boolean isPlankalkuelCompiler() {
		return ZuseStaticPageEntry.PLANKALKUELCOMPILER.getEnabled();
	}

	public void setPlankalkuelCompiler(boolean plankalkuelCompiler) {
		ZuseStaticPageEntry.PLANKALKUELCOMPILER.setEnabled(plankalkuelCompiler);
	}

	public boolean isPlankalkuelapplication() {
		return ZuseStaticPageEntry.PLANKALKUELAPPLICATIONS.getEnabled();
	}

	public void setPlankalkuelapplication(boolean plankalkuelapplication) {
		ZuseStaticPageEntry.PLANKALKUELAPPLICATIONS.setEnabled(plankalkuelapplication);
	}

	public boolean isReconstructionZ3() {
		return ZuseStaticPageEntry.RECONSTRUCTIONZ3.getEnabled();
	}

	public void setReconstructionZ3(boolean reconstructionZ3) {
		ZuseStaticPageEntry.RECONSTRUCTIONZ3.setEnabled(reconstructionZ3);
	}

}
