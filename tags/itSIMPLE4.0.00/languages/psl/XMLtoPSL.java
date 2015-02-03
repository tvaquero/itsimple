/***
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
*
* Copyright (C) 2007-2010 Universidade de Sao Paulo
*
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

package languages.psl;

import itSIMPLE.ItSIMPLE;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import languages.pddl.ToXPDDL;
import languages.xml.XMLUtilities;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import util.database.DataBase;

/**
 *
 * @author tiago
 */
public class XMLtoPSL {




    public static String generatePslProjectProlog(Element project){
        StringBuilder pslProject = new StringBuilder();


        String projectName = project.getChildText("name").trim().replace("-", "").replace(" ","").toLowerCase();

        pslProject.append("\n");

        //Main definitions (Header)
        pslProject.append("%Project: "+project.getChildText("name").trim()+"\n");
        pslProject.append("project("+projectName+").\n");


        // generate PDDL problem
        String pddlVersion = ItSIMPLE.getInstance().getSelectedPDDLversion();
        Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(project, pddlVersion, null);
        if (xpddlDomain!=null){
            //XMLUtilities.printXML(xpddlDomain);

            //TODO: check if the below code cause error in the swiprolog
            // It seems that in tuProlog it does not but in swi does
            pslProject.append("% Domain types of objects\n");
            Element types = xpddlDomain.getChild("types");
            for (Iterator it = types.getChildren().iterator(); it.hasNext();) {
                Element thetype = (Element)it.next();
                String parent = thetype.getAttributeValue("parent").toLowerCase();
                String currenttype = thetype.getAttributeValue("name").toLowerCase();
                pslProject.append(parent+"(X) :- "+currenttype+"(X). \n");
            }

            pslProject.append("% Domain actions\n");
            //Primitive actions
            //actions' specifications
            Element actions = xpddlDomain.getChild("actions");
            StringBuilder primiviteActions = new StringBuilder();
            StringBuilder subactivityActions = new StringBuilder();
            StringBuilder prepostconditionsActions = new StringBuilder();


            for (Iterator it = actions.getChildren().iterator(); it.hasNext();) {
                Element action = (Element)it.next();
                primiviteActions.append(createPrimitiveActivityStatement(action));
                subactivityActions.append(createSubActivityStatement(action, projectName));
                prepostconditionsActions.append(createPrePostConditionsStatement(action));
            }

            pslProject.append(primiviteActions);
            pslProject.append(subactivityActions);
            pslProject.append(prepostconditionsActions);

        }


        //Metrics


        //System.out.println(pslProject.toString());
        return pslProject.toString();
    }




    /**
     * This method generates the PSL representation of a domain in the Prolog format
     * @param project
     * @param domain
     * @return
     */
    public static String generatePslDomainProlog(Element project, Element domain){
        StringBuilder pslDomain = new StringBuilder();


        String projectName = project.getChildText("name").trim().replace("-", "").replace(" ","").toLowerCase();
        String domaiName = domain.getChildText("name").trim().replace("-", "").replace(" ","").toLowerCase();

        pslDomain.append("\n");

        //Main definitions (Header)
        pslDomain.append("% Domain: "+domain.getChildText("name").trim()+"\n");
        pslDomain.append("domain("+domaiName+").\n");

        //Metrics


        //System.out.println(pslDomain.toString());

        return pslDomain.toString();
    }

    
    /**
     * This method generates the PSL representation of a problem instance in \
     * the Prolog format
     * @param project
     * @param domain
     * @param problem
     * @return
     */
    public static String generatePslProblemProlog(Element project, Element domain, Element problem){
        StringBuilder pslProblem = new StringBuilder();


        String projectName = project.getChildText("name").trim().replace("-", "").replace(" ","").toLowerCase();
        String domaiName = domain.getChildText("name").trim().replace("-", "").replace(" ","").toLowerCase();
        String problemName = problem.getChildText("name").trim().replace("-", "").replace(" ","").toLowerCase();

        String problemSolvingActivity = "solving"+problemName;

        pslProblem.append("\n");

        //Main definitions (Header)
        pslProblem.append("% Problem instance: "+problem.getChildText("name").trim()+"\n");
        pslProblem.append("problem("+problemName+").\n");
        pslProblem.append("problem_of("+problemName+","+projectName+","+domaiName+").\n");
        pslProblem.append("activity("+problemSolvingActivity+").\n");
        pslProblem.append("problem_solving_activity_of("+problemSolvingActivity+","+problemName+").\n");

        //Metrics


        //Initial State
        //tate-based approach. We use 'fluent_of(f,s)' to declare the fluent f that is true in a state s.
        //we are using the pddl model as a reference to generate the psl initial state

        //TODO give a better name (e.g. s0+problemName
        String initialStateName = "s0";
         pslProblem.append("state("+initialStateName+").\n");

        // generate PDDL problem
        String pddlVersion = ItSIMPLE.getInstance().getSelectedPDDLversion();
        Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(problem, pddlVersion);
        if (xpddlProblem!=null){

            //objects
            Element types = xpddlProblem.getChild("objects");
            for (Iterator it = types.getChildren().iterator(); it.hasNext();) {
                Element object = (Element)it.next();
                String objType = object.getAttributeValue("type").toLowerCase();
                String objName = object.getAttributeValue("name").toLowerCase();
                pslProblem.append(objType+"("+objName+").\n");
            }


            //initial state
            Element init = xpddlProblem.getChild("init");
            for (Iterator it = init.getChildren().iterator(); it.hasNext();) {
                Element fact = (Element)it.next();
                //XMLUtilities.printXML(fact);
                String pslfluent = "";

                if (fact.getName().equals("predicate")){
                    pslfluent = fact.getAttributeValue("id").toLowerCase();
                    if(fact.getChildren().size()>0){
                        pslfluent += "(";
                        for (Iterator it1 = fact.getChildren().iterator(); it1.hasNext();) {
                            Element object = (Element)it1.next();
                            pslfluent += object.getAttributeValue("id").toLowerCase();
                            if (it1.hasNext()){
                                pslfluent += ",";
                            }
                        }
                        pslfluent += ")";
                    }
                    if (!pslfluent.trim().equals("")){
                        pslProblem.append("fluent_of("+pslfluent+","+initialStateName+").\n");
                    }
                }
                //TODO: function
                else if (fact.getName().equals("equals")){
                    Element function = fact.getChild("function");
                    pslfluent = function.getAttributeValue("id").toLowerCase();
                    if(function.getChildren().size()>0){
                        pslfluent += "(";
                        for (Iterator it1 = function.getChildren().iterator(); it1.hasNext();) {
                            Element object = (Element)it1.next();
                            pslfluent += object.getAttributeValue("id").toLowerCase();
                            if (it1.hasNext()){
                                pslfluent += ",";
                            }
                        }
                        pslfluent += ")";
                    }
                    Element value = fact.getChild("value");
                    String valueStr = value.getAttributeValue("number");
                    if (!pslfluent.trim().equals("")){
                        pslProblem.append("numeric_fluent_of("+pslfluent+","+valueStr+","+initialStateName+").\n");
                    }


                }                
                
            }
            
        }

        //System.out.println(pslProblem.toString());

        return pslProblem.toString();
    }


/**
     * This method generates a psl representation of the plan (Prolog format).
     * @param xmlPlan
     * @param id
     * @return
     */
    public static String generatePslPlanProlog(Element xmlPlan, String id){
        StringBuilder pslPlan = new StringBuilder();


        //if (id.equals("-1")){
        //    id = "New";
        //}

         //1. Get the actions
        List<Element> actions = null;
        try {
                XPath path = new JDOMXPath("plan/action");
                actions = path.selectNodes(xmlPlan);
        } catch (JaxenException e) {
                e.printStackTrace();
        }
        if (actions.size() > 0){

            String planid = "plan";
            if (id.equals("-1")){
                planid += "New";
            }else{
                planid += id;
            }
            //String planid = "plan"+id;
            String project = xmlPlan.getChildText("project").replace("-","").replace(" ", "").toLowerCase();
            String domain = xmlPlan.getChildText("domain").replace("-","").replace(" ", "").toLowerCase();
            String problem = xmlPlan.getChildText("problem").replace("-","").replace(" ", "").toLowerCase();

            String problemSolvingActivity = "solving"+problem;

            String initialState = "s0"; //+problem;

            pslPlan.append("\n");
            pslPlan.append("\n");
            pslPlan.append("% Plan "+planid+" \n");
            pslPlan.append("plan("+planid+").\n");
            pslPlan.append("occurrence_of("+planid+","+problemSolvingActivity+").\n");
            pslPlan.append("solution_of("+planid+","+project+","+domain+","+problem+").\n");

            StringBuilder occurrenceOfSet = new StringBuilder();
            StringBuilder subactivityOccurrenceSet = new StringBuilder();
            StringBuilder nextSuboccSet = new StringBuilder();
            StringBuilder stateStructure = new StringBuilder();

            int counter = 1;
            for (Iterator<Element> it = actions.iterator(); it.hasNext();) {
                Element eaAction = it.next();

                //occurrence ID
                String actionOccID = "o"+ Integer.toString(counter) +planid;
                //activity name (sub activity of solving planning problem
                String activity = eaAction.getAttributeValue("id").toLowerCase();
                Element paramenters = eaAction.getChild("parameters");
                if (paramenters.getChildren().size() > 0){
                    activity += "(";
                    for (Iterator<Element> it1 = paramenters.getChildren().iterator(); it1.hasNext();) {
                        Element parameter = it1.next();
                        activity += parameter.getAttributeValue("id").toLowerCase();

                        if (it1.hasNext()){
                            activity += ",";
                        }
                    }
                    activity += ")";
                }

                //occurrence_of
                occurrenceOfSet.append("occurrence_of("+actionOccID+","+activity+").\n");

                //subactivity_occurrence
                subactivityOccurrenceSet.append("subactivity_occurrence("+actionOccID+","+planid+").\n");

                //next_subocc
                //first is the root
                if(counter == 1){
                    nextSuboccSet.append("root("+actionOccID+","+problemSolvingActivity+").\n");
                    nextSuboccSet.append("root_occ("+actionOccID+","+planid+").\n");
                }
                if (it.hasNext()){
                    //next_subocc
                    String nextActionOccID = "o"+ Integer.toString(counter+1) +planid;
                    nextSuboccSet.append("next_subocc("+actionOccID+","+nextActionOccID+","+problemSolvingActivity+").\n");
                }
                else{//last the leaf
                    nextSuboccSet.append("leaf("+actionOccID+","+problemSolvingActivity+").\n");
                    nextSuboccSet.append("leaf_occ("+actionOccID+","+planid+").\n");
                }


                //State structure

                String stateID = "s"+ Integer.toString(counter) +planid;
                //first action need the initial state
                if(counter == 1){
                    stateStructure.append("prior("+initialState+","+actionOccID+").\n");
                }
                if (it.hasNext()){
                    //next_subocc
                    String nextActionOccID = "o"+ Integer.toString(counter+1) +planid;
                    stateStructure.append("holds("+stateID+","+actionOccID+").\n");
                    stateStructure.append("prior("+stateID+","+nextActionOccID+").\n");
                }
                else{//last the leaf
                    stateStructure.append("holds("+stateID+","+actionOccID+").\n");
                }


                counter++;
            }

            pslPlan.append(occurrenceOfSet);
            pslPlan.append(subactivityOccurrenceSet);
            pslPlan.append(nextSuboccSet);
            pslPlan.append(stateStructure);

            //Plan quality
            String planQuality = xmlPlan.getChild("evaluation").getAttributeValue("value");
            pslPlan.append("quality("+planid+","+planQuality+").\n");


            //Metrics values and evaluation
            Element metrics = xmlPlan.getChild("metrics");
            if(metrics!=null){
                for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                    Element metric = it.next();
                    String metricName = metric.getChildText("name").toLowerCase().replace(" ", "");
                    String metricValue  = metric.getChildText("value");
                    String metricQuality = metric.getChild("evaluation").getAttributeValue("value");

                    pslPlan.append("metric_value("+planid+","+metricName+","+metricValue+").\n");
                    pslPlan.append("metric_quality("+planid+","+metricName+","+metricQuality+").\n");

                }
            }
        }

        return pslPlan.toString().trim();

    }


    /**
     * This method generates collects rationales from database based on the given plan
     * and generates a Prolog representation of them using the PSL ontology
     * 
     * @param xmlPlan
     * @param id
     * @return
     */
    public static String generatePslRationalePrologfromDatabase(Element xmlPlan, String id){
        StringBuilder pslRationales = new StringBuilder();


        String project = xmlPlan.getChildText("project").trim();
        String domain = xmlPlan.getChildText("domain").trim();
        String problem = xmlPlan.getChildText("problem").trim();

        //Gathering Rationales
        Element rationales = xmlPlan.getChild("evaluation").getChild("rationales");


        DataBase eSelectType = new DataBase();
        String whereclause = "";
        //get rationales registered for this plan
        //DataBase eSelectTypeRationale = new DataBase();
        //eSelectType.setColumnList("rationale.id, rationale.xmlrationale, rationale.planid, rationale_plan.id"); //please, don't use *
        //eSelectType.setTableName("rationale, rationale_plan");
        //whereclause = "rationale_plan.planid = "+ id + " and rationale.id = rationale_plan.rationaleid";

        //filter the ones that belongs to the plan (planid != id)
        eSelectType.setColumnList("id, xmlrationale, planid"); //please, don't use *
        eSelectType.setTableName("rationale");
        whereclause = "planid != "+id+" AND ( "+
            "(abstractionlevel = 'project-independent') OR "+
            "(abstractionlevel = 'project-specific' AND project = '"+project+"') OR "+
            "(abstractionlevel = 'domain-specific' AND project = '"+project+"' AND domain = '"+domain+"') OR "+
            "(abstractionlevel = 'problem-specific' AND project = '"+project+"' AND domain = '"+domain+"' AND problem = '"+problem+"') "+
            ")";
        //select rationale.name from rationale,rationale_plan
        //where
        //rationale.planid != 27 AND rationale_plan.planid != 27 AND
        //rationale.id = rationale_plan.rationaleid AND (
        //(abstractionlevel = 'project-independent') OR
        //(abstractionlevel = 'project-specific' AND project = 'Gold Miner domain Original v2') OR
        //(abstractionlevel = 'domain-specific' AND project = 'Gold Miner domain Original v2' AND domain = 'Grid 5x5') OR
        //(abstractionlevel = 'problem-specific' AND project = 'Gold Miner domain Original v2' AND domain = 'Grid 5x5' AND problem = 'gold-miner-target-5x5-01')
        //)


        //Do select in the database
        eSelectType.setWhereClause(whereclause);
        eSelectType.setOrderClause("id"); //order by clause, null if not applicable
        //eSelectType.setOrderClause("id"); //order by clause, null if not applicable
        eSelectType.Select();

        pslRationales.append("\n");
        pslRationales.append("\n");
        pslRationales.append("% Rationales \n \n");

        //Get results
        ResultSet rs = eSelectType.getRs();
        try {
            while (rs.next()) {
                //String rationaleID = rs.getString("id");
                String rationaleID = rs.getString(1);
                String xmlRationaleString = rs.getString("xmlrationale");
                String targetPlanID = rs.getString("planid");

                //convert xml string to a xml element
                SAXBuilder builder = new SAXBuilder();
                Reader in = new StringReader(xmlRationaleString);
                Document doc = null;
                Element theRationale = null;
                try {
                    doc = builder.build(in);
                    theRationale = doc.getRootElement();
                } catch (JDOMException ex) {
                    Logger.getLogger(XMLtoPSL.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(XMLtoPSL.class.getName()).log(Level.SEVERE, null, ex);
                }

                pslRationales.append(createRationaleStatement(theRationale,rationaleID));

                /*
                if (theRationale != null){

                    //Write the rationale in the PSL prolog format
                    String pslRationaleID = "r"+rationaleID;

                    pslRationales.append("% Rationale "+rationaleID+" \n");
                    //rationale declaration
                    pslRationales.append("rationale("+pslRationaleID+").\n");
                    //description
                    pslRationales.append("description("+pslRationaleID+",'"+theRationale.getChildText("description").replace("\n", " ").trim()+"').\n");
                    //rule
                    String rule = theRationale.getChildText("rule").trim().replace("this", pslRationaleID);
                    pslRationales.append(rule+"\n");
                    pslRationales.append("\n");

                }
                 */
                //XMLUtilities.printXML(theRationale);

            }
        } catch (SQLException se) {
          se.printStackTrace();
          //System.exit(1);
        }


        return pslRationales.toString();

    }





    public static String createPrimitiveActivityStatement(Element xpddlAction){
        String pslPrimitive = "";
        
        String activity = xpddlAction.getAttributeValue("name").trim().toLowerCase();
        Element parameters = xpddlAction.getChild("parameters");
        if (parameters.getChildren().size() > 0){
            activity += "(";
            for (Iterator it = parameters.getChildren().iterator(); it.hasNext();) {
                Element parameter = (Element)it.next();
                activity += parameter.getAttributeValue("name").trim().toUpperCase();
                if (it.hasNext()){
                    activity += ",";
                }
            }
            activity += ")";
        }

        pslPrimitive = "primitive("+activity+"). \n";

        return pslPrimitive;
    }


    public static String createSubActivityStatement(Element xpddlAction, String projectName){
        String pslSubActivity = "";

        String activity = xpddlAction.getAttributeValue("name").trim().toLowerCase();
        Element parameters = xpddlAction.getChild("parameters");
        if (parameters.getChildren().size() > 0){
            activity += "(";
            for (Iterator it = parameters.getChildren().iterator(); it.hasNext();) {
                Element parameter = (Element)it.next();
                activity += "_";
                if (it.hasNext()){
                    activity += ",";
                }
            }
            activity += ")";
        }

        pslSubActivity = "subactivity("+activity+",SolvAct) :- problem_of(P,"+projectName+",_), problem_solving_activity_of(SolvAct,P). \n";

        return pslSubActivity;
    }


    public static String createPrePostConditionsStatement(Element xpddlAction){
        StringBuilder pslPrePostActivity = new StringBuilder();

        String activity = xpddlAction.getAttributeValue("name").trim().toLowerCase();
        Element parameters = xpddlAction.getChild("parameters");
        if (parameters.getChildren().size() > 0){
            activity += "(";
            for (Iterator it = parameters.getChildren().iterator(); it.hasNext();) {
                Element parameter = (Element)it.next();
                activity += parameter.getAttributeValue("name").toUpperCase();
                if (it.hasNext()){
                    activity += ",";
                }
            }
            activity += ")";
        }

        //precondition
        Element preconditions = xpddlAction.getChild("precondition");
        StringBuilder pslPreconditions = new StringBuilder();
        if (preconditions.getChildren().size() > 0){
            for (Iterator it = preconditions.getChildren().iterator(); it.hasNext();) {
                Element precond = (Element)it.next();
                pslPreconditions.append(createConditionStatement(precond,activity,"precondition"));
            }
        }

        //effect
        Element effects = xpddlAction.getChild("effect");
        StringBuilder pslEffects = new StringBuilder();
        if (effects.getChildren().size() > 0){
            for (Iterator it = effects.getChildren().iterator(); it.hasNext();) {
                Element effect = (Element)it.next();
                pslEffects.append(createConditionStatement(effect,activity,"effect"));
            }
        }

        pslPrePostActivity.append("%"+xpddlAction.getAttributeValue("name").trim().toLowerCase() +" \n");
        pslPrePostActivity.append(pslPreconditions);
        pslPrePostActivity.append(pslEffects);


        return pslPrePostActivity.toString();
    }



    public static String createConditionStatement(Element xpddlnode, String activityName, String prepost){
        String pslCondition = "";

        //Restricted to simple conditions and (the or is not supported)

        if (xpddlnode.getName().equals("and")){
            for (Iterator it = xpddlnode.getChildren().iterator(); it.hasNext();) {
                Element node = (Element)it.next();
                pslCondition += createConditionStatement(node,activityName,prepost);
            }
        }
        else if (xpddlnode.getName().equals("not")){
            for (Iterator it = xpddlnode.getChildren().iterator(); it.hasNext();) {
                Element node = (Element)it.next();
                pslCondition += createConditionStatement(node,activityName,prepost);
            }
        }
        else if (xpddlnode.getName().equals("predicate")){
            String fluent = xpddlnode.getAttributeValue("id").trim().toLowerCase();
            if (xpddlnode.getChildren().size() > 0){
                fluent += "(";
                for (Iterator it = xpddlnode.getChildren().iterator(); it.hasNext();) {
                    Element parameter = (Element)it.next();
                    //fluent += parameter.getAttributeValue("id").trim().toUpperCase();
                    if(parameter.getName().equals("parameter")){
                        fluent += parameter.getAttributeValue("id").trim().toUpperCase();
                    }else if(parameter.getName().equals("object")){
                        fluent += parameter.getAttributeValue("id").trim().toLowerCase();
                    }
                    if (it.hasNext()){
                        fluent += ",";
                    }
                }
                fluent += ")";
            }
            
            if (xpddlnode.getParentElement().getName().equals("not")){
                pslCondition = "negative_"+prepost+"("+activityName+","+fluent+"). \n";
            }else{
                pslCondition = prepost+"("+activityName+","+fluent+"). \n";
            }
            
        }
        //TODO: function
        else if (xpddlnode.getName().equals("decrease") || xpddlnode.getName().equals("increase")){

            Element function = xpddlnode.getChild("function");
            String fluent = function.getAttributeValue("id").trim().toLowerCase();
            if (function.getChildren().size() > 0){
                fluent += "(";
                for (Iterator it = function.getChildren().iterator(); it.hasNext();) {
                    Element parameter = (Element)it.next();
                    if(parameter.getName().equals("parameter")){
                        fluent += parameter.getAttributeValue("id").trim().toUpperCase();
                    }else if(parameter.getName().equals("object")){
                        fluent += parameter.getAttributeValue("id").trim().toLowerCase();
                    }
                    if (it.hasNext()){
                        fluent += ",";
                    }
                }
                fluent += ")";
            }
            //declare that the action changes/assigns the given numeric fluent
            pslCondition = "assign("+activityName+","+fluent+"). \n";
            //declare how it changes the numeric fluent
            Element value = xpddlnode.getChild("value");
            String operator = "";
            if (xpddlnode.getName().equals("decrease")){
                operator = "-";
            }else{
                operator = "+";
            }
            pslCondition += "numeric_fluent_of("+fluent+",V1,S1):- prior(S0,Occ), holds(S1,Occ), occurrence_of(Occ,"+activityName+"), numeric_fluent_of("+fluent+",V0,S0), V1 is (V0 "+ operator+" "+value.getAttributeValue("number")+"),!. \n";
        }
        else if (xpddlnode.getName().equals("assign")){

            Element function = xpddlnode.getChild("function");
            String fluent = function.getAttributeValue("id").trim().toLowerCase();
            if (function.getChildren().size() > 0){
                fluent += "(";
                for (Iterator it = function.getChildren().iterator(); it.hasNext();) {
                    Element parameter = (Element)it.next();
                    if(parameter.getName().equals("parameter")){
                        fluent += parameter.getAttributeValue("id").trim().toUpperCase();
                    }else if(parameter.getName().equals("object")){
                        fluent += parameter.getAttributeValue("id").trim().toLowerCase();
                    }
                    if (it.hasNext()){
                        fluent += ",";
                    }
                }
                fluent += ")";
            }
            //declare that the action changes/assigns the given numeric fluent
            pslCondition = "assign("+activityName+","+fluent+"). \n";
            //declare how it changes the numeric fluent
            //TODO: notnecessary we will have a value, we can find a expression
            Element value = xpddlnode.getChild("value");
            if (value != null){
                String operator = "";
                pslCondition += "numeric_fluent_of("+fluent+",V1,S1):- prior(S0,Occ), holds(S1,Occ), occurrence_of(Occ,"+activityName+"), numeric_fluent_of("+fluent+",V0,S0), V1 is "+value.getAttributeValue("number")+",!. \n";
            }

        }



                //assign(move(R,X,Y),traveldistance(R)).
                //numeric_fluent_of(traveldistance(R),V1,S1):- prior(S0,Occ), holds(S1,Occ), occurrence_of(Occ,move(R,X,Y)), numeric_fluent_of(traveldistance(R),V0,S0), V1 is V0 + 1, !.%numeric_effect(A,Function,V1)).

        return pslCondition;
    }


    public static String createRationaleStatement(Element theRationale, String rationaleID){
        String pslRationale = "";

         if (theRationale != null){

                    //Write the rationale in the PSL prolog format
                    String pslRationaleID = "r"+rationaleID;

                    pslRationale = "% Rationale "+rationaleID+" \n";
                    //rationale declaration
                    pslRationale += "rationale("+pslRationaleID+").\n";
                    //description
                    pslRationale += "description("+pslRationaleID+",'"+theRationale.getChildText("description").replace("\n", " ").trim()+"').\n";
                    //rule
                    String rule = theRationale.getChildText("rule").trim().replace("this", pslRationaleID);
                    pslRationale += rule+"\n";
                    pslRationale += "\n";

                }
         return pslRationale;

    }



}
