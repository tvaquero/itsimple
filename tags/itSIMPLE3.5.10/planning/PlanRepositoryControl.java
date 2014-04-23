/***
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
*
* Copyright (C) 2007-2009 Universidade de Sao Paulo
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
* Authors:	Tiago S. Vaquero
*
**/

package planning;

import java.util.Iterator;
import java.util.List;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;

/**
 * This class is responsible for control the storage of plans in a repository.
 * This control includes saving and deleting plans, as well the mantainance of
 * the repository
 *
 * @author Tiago
 */
public class PlanRepositoryControl {
    
     public static String savePlan(Element domain, Element problem, Element xmlPlan){

        String message = "done";



        return message;
    }


     public static String deletePlan(Element planRef){

        String message = "done";



        return message;
    }



}
