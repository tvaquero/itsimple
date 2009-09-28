/***
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
*
* Copyright (C) 2007,2008,2009 Universidade de Sao Paulo
*

* This file is part of itSIMPLE.
*
* itSIMPLE is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version. Other licenses might be available
* upon written agreement.
*
* itSIMPLE is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with itSIMPLE.  If not, see <http://www.gnu.org/licenses/>.
*
* Authors:	Matheus Haddad,
 *              Tiago S. Vaquero
*			.
**/

package planning;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom.Element;

/**
 *
 * @author Matheus
 */
public class PlannerSuggestion {

    private List<Element> suggestedPlanners = new ArrayList<Element>();

    private List<Element> discardedPlanners = new ArrayList<Element>();

    /**
     * @return the suggestedPlanners
     */
    public List<Element> getSuggestedPlanners() {
        return suggestedPlanners;
    }

    /**
     * @param aSuggestedPlanners the suggestedPlanners to set
     */
    public  void setSuggestedPlanners(List<Element> SuggestedPlanners) {
        suggestedPlanners = SuggestedPlanners;
    }

    /**
     * @return the discardedPlanners
     */
    public List<Element> getDiscardedPlanners() {
        return discardedPlanners;
    }

    /**
     * @param aDiscardedPlanners the discardedPlanners to set
     */
    public void setDiscardedPlanners(List<Element> DiscardedPlanners) {
        discardedPlanners = DiscardedPlanners;
    }
    
    /**
     * This method select the planners that can deal with the given domain based on its requirements
     * @param xppdldomain, A XML representaion of a PDDL domain
     * @param plannersXml, a list planners available in itSIMPLE
     * @return
     */
    public void initialPlannerSelection(Element xppdldomain, Element plannersXml){

        this.suggestedPlanners.clear();
        this.discardedPlanners.clear();
        
        List domainRequirements = xppdldomain.getChild("requirements").getChildren();
        List planners = plannersXml.getChild("planners").getChildren("planner");

        Iterator plannerIterator = planners.iterator();
        while (plannerIterator.hasNext()) {
            Element planner = (Element) plannerIterator.next();
            List plannerRequirements = planner.getChild("requirements").getChildren();
            if (this.containsRequirements(plannerRequirements,domainRequirements) && this.runOnOperatingSystem(planner)){
                this.suggestedPlanners.add(planner);
            }
            else{
                this.discardedPlanners.add(planner);
            }
        }

    }
    
    /**
     * This method verify if planner requirements contains domain requirements
     * @param plannerRequirements, list if DOM Element List Planner Requirements
     * @param planners, list of DOM Element Planner Requirements
     * @return
     */
    private boolean containsRequirements(List<Element> plannerRequirements,List<Element> domainRequirements){

        boolean _containsRequirements = true;

        Iterator domainRequirementsIterator = domainRequirements.iterator();
        while (domainRequirementsIterator.hasNext() && _containsRequirements) {

            Element domainRequirement = (Element) domainRequirementsIterator.next();

            boolean plannerContainsRequirement = false;
            Iterator plannerRequirementsIterator = plannerRequirements.iterator();
            while (plannerRequirementsIterator.hasNext() && !plannerContainsRequirement) {
                Element plannerRequirement = (Element) plannerRequirementsIterator.next();
                plannerContainsRequirement = (plannerRequirement.getName().equals(domainRequirement.getName()));
            }
            
            _containsRequirements = _containsRequirements && plannerContainsRequirement;

        }

        return _containsRequirements;
    }

    /**
     * This method verify if planner can be run on operating system
     * @param planner, DOM Planner Element
     * @return
     */
    private boolean runOnOperatingSystem(Element planner){

        boolean _runOnOperatingSystem = false;
        String thisOperatingSystem = this.getOperatingSystem();

        List operatingSystems = planner.getChild("platform").getChildren();
        Iterator soIterator = operatingSystems.iterator();
        while (soIterator.hasNext()) {
            Element plataform = (Element) soIterator.next();
            _runOnOperatingSystem =  _runOnOperatingSystem || (plataform.getName().equals(thisOperatingSystem));
        }

        return _runOnOperatingSystem;
    }
    
    /**
     * This method get the operating system name on which the itSIMPLE running
     * @return
     */
    private String getOperatingSystem(){

        String operatingSystem = System.getProperty("os.name").toLowerCase();

        if (operatingSystem.indexOf("linux")==0)
            operatingSystem = "linux";
        else if (operatingSystem.indexOf("windows")==0)
             operatingSystem = "windows";
        else if (operatingSystem.indexOf("mac")==0)
             operatingSystem = "mac";

        return operatingSystem;
    }
}
