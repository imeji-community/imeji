/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.zuse.presentation.beans;

import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * Defines the page names and Path for imeji of the Zuse instance. All changes here must be synchronized with WEB-INF/pretty-config.xml The
 * Pages are used by the History
 * 
 * @author Nguyen (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ZuseNavigationBean extends Navigation
{

	// Pages of zuse
    public final Page ENCYCLOPEDIA = new Page("Encyclopedia", "encyclopedia");
    
    public final Page PROJECT = new Page("Project", "project");
    public final Page KONRADZUSE = new Page("Konrad Zuse", "zuse");
    public final Page Z1 = new Page("Z1", "z1");
    public final Page Z2 = new Page("Z2", "z2");
    public final Page Z3 = new Page("Z3", "z3");
    public final Page Z4 = new Page("Z4", "z4");
    public final Page ASSEMBLYLINESELFREPLICATINGSYSTEMS = new Page("Assembly Line Self-Repilcating Systems", "assemblyLineSelfReplicatingSystems");
    public final Page HELIXTOWER = new Page("Helix-Tower", "helixTower");
    public final Page MECHANICALSYSTEM = new Page("Mechanical System", "mechanicalSystem");
    public final Page ELECTROMECHANICS = new Page("Electromechanics", "electromechanics");
    public final Page ELECTRONICS = new Page("Electronics", "electronics");
    public final Page RELAY = new Page("Relay", "relay");
    public final Page VACUUMTUBE = new Page("VacuumTube", "vacuumTube");
    public final Page TRANSISTOR = new Page("Transistor", "transistor");
    public final Page BINARYNUMBER = new Page("Binary Number", "binaryNumber");
    public final Page BIT = new Page("Bit", "bit");
    public final Page PUNCHEDTAPE = new Page("Punched Tape", "punchedTape");
    public final Page BOOLEANALGEBRA = new Page("Boolean Algebra", "booleanAlgebra");
    public final Page LOGICGATE = new Page("Logic Gate", "logicGate");
    public final Page FLOATINGPOINT = new Page("Floating Point", "floatingPoint");
    
    public final Page RESOURCES = new Page("Resources", "resources");
    
    public final Page SIMULATIONS = new Page("Simulations", "simulations");
    
    public final Page SIMULATIONZ1 = new Page("Simulations for Z1", "simulationsZ1");
    public final Page SIMPLEMECHANICALSWITCH = new Page("Simple Mechanical Switch", "z1SimpleMechSwitch"); // http://zuse.zib.de/infos/simulations/Z1/Z1Sim/simple.html
    public final Page MORECOMPLEXVARIANTMECHANICALSWITCH = new Page("More complex variant of the mechanical switch", "z1MoreComplexMechSwitch"); // http://zuse.zib.de/infos/simulations/Z1/Z1Sim/variant.html
    public final Page MECHANICALSWITCHTOCALCULATETHEEQUIVALENCE = new Page("Mechanical switch to calculate the equivalence", "z1MechSwitchCalcEquivalence"); // http://zuse.zib.de/infos/simulations/Z1/Z1Sim/equiv.html
    public final Page MECHANICALADDITIONUNIT = new Page("Mechanical Addition Unit","z1MechAddUnit"); // http://zuse.zib.de/infos/simulations/Z1/Z1Adder/adder.html
    public final Page Z1ADDERWEBGL = new Page("Z1 Adder (WebGL)", "z1AdderWebGL"); // http://zuse.zib.de/infos/simulations/z1-adder-wgl
    public final Page Z1ADDERLWJGL = new Page("Z1 Adder (LWJGL)", "z1AdderLWJGL"); // http://zuse-z1.zib.de/simulation/adder
    public final Page Z1ADDERJAVAAPPLET = new Page("Z1 Adder (Java Applet)", "z1AdderJavaApplet"); // http://zuse.zib.de/infos/simulations/Z1/Simulation/addierer.html
    
    public final Page SIMULATIONZ3 = new Page("Simulations for Z3", "simulationsZ3");
    public final Page SIMENTIREZ3 = new Page("Entire Simulation", "z3SimulationsEntire"); // http://zuse.zib.de/infos/simulations/Z3_Sim
    public final Page SIM3DZ3 = new Page("3D Simulation", "z3Simulations3D"); // http://zuse.zib.de/infos/simulations/Z3_3D_Sim
    public final Page ADDERCIRCUITZ3 = new Page("Adder Circuit", "z3AdderCircuit"); // http://zuse.zib.de/infos/simulations/AdderV2/adder.html
    
    public final Page SHIFTERCIRCUITZ3 = new Page("Shifter Circuit", "z3ShifterCircuit"); // http://zuse.zib.de/infos/simulations/Shifter/shifter.html
    public final Page NORMALIZERCIRCUITZ3 = new Page("Normalizer Circuit", "z3NormalizerCircuit"); // http://zuse.zib.de/infos/simulations/Normalizer/normalizer.html
    public final Page DECIMAL2BINARYCONVERTERZ3 = new Page("Decimal to Binary Converter", "z3Dec2BinConverter"); // http://zuse.zib.de/infos/simulations/DecKey/decKey.html
    public final Page DECIMALPLACEADJUSTER = new Page("Decimal Place Adjuster", "z3DecPlaceAdjuster"); // http://zuse.zib.de/infos/simulations/DecPlaceAdjuster/decPlaceAdjuster.html
    
    public final Page ENIAC = new Page("Eniac", "eniac");
    
    public final Page PLANKALKUEL = new Page("Plankalkül", "plankalkuel");
    public final Page PLANKALKUELSYSTEM = new Page("Plankalkül-System", "plankalkuelSystem");
    public final Page PLANKALKUELEDITOR = new Page("Editor", "plankalkuelEditor"); // http://zuse.zib.de/infos/simulations/Plankalkuel/Editor/pk.html
    public final Page PLANKALKUELCOMPILER = new Page("Compiler", "plankalkuelCompiler"); // http://zuse.zib.de/infos/simulations/Plankalkuel/Compiler/plankalk.html
    
    public final Page PLANKALKUELAPPLICATIONS = new Page("Applications", "plankalkuelApplication"); // http://zuse.zib.de/infos/simulations/Plankalkuel/Chess/JavaApplet/chess.html
    
    public final Page RECONSTRUCTIONZ3 = new Page("Recontruction of the Z3", "reconstructionZ3");
    
    public final Page PDFS = new Page("PDF Documents", "pdfs");

    
    // session
    private SessionBean sessionBean = null;

    /**
     * Application bean managing navigation
     * 
     * @throws Exception
     */
    public ZuseNavigationBean() throws Exception
    {
        
    }
    
    public String getEcyclopediaUrl()
    {
        return applicationUrl + ENCYCLOPEDIA.getPath() + "/";
    }

    public String getResourcesUrl()
    {
        return applicationUrl + RESOURCES.getPath() + "/";
    }
    

    /**
     * Get the context for the context sensitive search.
     * 
     * @return
     */
    public String getContext()
    {
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (sessionBean.getCurrentPage() == null)
        {
            return "";
        }
        String context = "#";
        if ("help".equals(sessionBean.getCurrentPage().getName()))
        {
            context += "";
        }
        if ("welcome".equals(sessionBean.getCurrentPage().getName()) || "about".equals(sessionBean.getCurrentPage().getName())
                || "legal".equals(sessionBean.getCurrentPage().getName()))
        {
            context += "1._Home";
        }
        if ("home".equals(sessionBean.getCurrentPage().getName()))
        {
            context += "2._Pictures";
        }
        if ("search".equals(sessionBean.getCurrentPage().getName())
                || "searchResult".equals(sessionBean.getCurrentPage().getName()))
        {
            context += "4.1_Advanced_Search";
        }
        if ("albumssearch".equals(sessionBean.getCurrentPage().getName()))
        {
            context += "4.2_Public_Album_Search";
        }
        if ("details".equals(sessionBean.getCurrentPage().getName())
                || "comparison".equals(sessionBean.getCurrentPage().getName())
                || "detailsFromAlbum".equals(sessionBean.getCurrentPage().getName())
                || "comparisonFromAlbum".equals(sessionBean.getCurrentPage().getName())
                || "person".equals(sessionBean.getCurrentPage().getName()))
        {
            context += "2.2_Picture_View";
        }
        if ("albums".equals(sessionBean.getCurrentPage().getName())
                || "createalbum".equals(sessionBean.getCurrentPage().getName())
                || "editalbum".equals(sessionBean.getCurrentPage().getName()))
        {
            context += "3._Album";
        }
        if ("viewAlbum".equals(sessionBean.getCurrentPage().getName()))
        {
            context += "3.2_Album_View";
        }
        return context;
    }

}
