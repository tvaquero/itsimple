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
* Authors:	Tiago S. Vaquero, 

**/

package languages.pddl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import languages.xml.XMLUtilities;
import org.jdom.Element;
import org.jdom.JDOMException;
import pddl4j.Domain;
import pddl4j.Parser;
import pddl4j.Problem;
import pddl4j.*;
import pddl4j.ErrorManager.Message;
import pddl4j.exp.*;
import pddl4j.exp.AndExp;
import pddl4j.exp.ExpID;
import pddl4j.exp.InitEl;
import pddl4j.exp.Literal;
import pddl4j.exp.assign.AssignOpExp;
import pddl4j.exp.fcomp.Comp;
import pddl4j.exp.fcomp.EqualComp;
import pddl4j.exp.fcomp.FCompExp;
import pddl4j.exp.fexp.BinaryOp;
import pddl4j.exp.fexp.FHead;
import pddl4j.exp.fexp.NArityMultiply;
import pddl4j.exp.term.Constant;
import pddl4j.exp.term.Term;
import pddl4j.exp.term.TermID;
import pddl4j.exp.type.TypeSet;
import pddl4j.exp.fexp.Number;
import pddl4j.exp.metric.MetricExp;
import pddl4j.exp.term.Variable;

public class PDDLToXPDDL {


    /**
     * Returns the default options of the compiler.
     *
     * @return the default options.
     */
    private static Properties getParserOptions() {
        Properties options = new Properties();
        options.put("source", Source.V3_0);
        options.put(RequireKey.STRIPS, true);
        options.put(RequireKey.TYPING, true);
        options.put(RequireKey.EQUALITY, true);
        options.put(RequireKey.FLUENTS, false);
        options.put(RequireKey.NEGATIVE_PRECONDITIONS, true);
        options.put(RequireKey.DISJUNCTIVE_PRECONDITIONS, true);
        options.put(RequireKey.EXISTENTIAL_PRECONDITIONS, true);
        options.put(RequireKey.UNIVERSAL_PRECONDITIONS, true);
        options.put(RequireKey.QUANTIFIED_PRECONDITIONS, true);
        options.put(RequireKey.CONDITIONAL_EFFECTS, true);
        options.put(RequireKey.DURATIVE_ACTIONS, true);
        options.put(RequireKey.ADL, true);
        options.put(RequireKey.TIMED_INITIAL_LITERALS, true);
        options.put(RequireKey.DERIVED_PREDICATES, true);
        return options;
    }


    public static Element parsePDDLdomainToPDDL(String domainFile){
            Element xpddl = new Element("xpddlDomain");


            return xpddl;
    }



    /**
     * This method generates a XPDDL verion of the given PDDL file string
     * @param problemFile
     * @return xpddl
     */
    public static Element parsePDDLproblemToXPDDL2(String problemFile){
            Element xpddl = null;

            try {
                xpddl = (Element) XMLUtilities.readFromFile("resources/settings/commonData.xml").getRootElement().getChild("xpddlNodes").getChild("xpddlProblem").clone();
            } catch (JDOMException ex) {
                Logger.getLogger(PDDLToXPDDL.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PDDLToXPDDL.class.getName()).log(Level.SEVERE, null, ex);
            }


            System.out.println("(!) Parsing problem instance ...");

            Parser parser = new Parser(getParserOptions());
            ErrorManager mgr = parser.getErrorManager();

            //Reading the file and parsing it
            Problem problem = null;
            try {
                problem = parser.parse(new File(problemFile));
                if (mgr.contains(Message.ERROR)) {
                    mgr.print(Message.ALL);
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(PDDLToXPDDL.class.getName()).log(Level.SEVERE, null, ex);
            }

            if(problem == null){
                System.out.println("Parsed problem is null");
                return null;

            }

            //1. Problem name and domain name
            System.out.println(problem.getProblemName());
            xpddl.getChild("name").setText(problem.getProblemName());
            xpddl.getChild("domain").setText(problem.getDomainName());


            //2. objects
            Element xobjects = xpddl.getChild("objects");
            for (Iterator<Constant> it = problem.constantsIterator(); it.hasNext();) {

                Constant object = it.next();
                TypeSet obType = object.getTypeSet();
                System.out.println(object.toTypedString());

                Element xobj = new Element("object");
                xobj.setAttribute("name", object.toString());
                xobj.setAttribute("type", obType.toString());

                xobjects.addContent(xobj);
            }



            //3. init state
            //get every declaration in the inital state
            Element xinit = xpddl.getChild("init");
            for (Iterator<InitEl> it = problem.getInit().iterator(); it.hasNext();) {
                InitEl element = it.next();
                //System.out.println(element);

                //TODO: treat all cases

                //System.out.println(element.getExpID());
                if(element.getExpID().equals(ExpID.F_COMP)){
                    //function case
                    EqualComp fluentExp = (EqualComp) element;
                    Element xfluentExp = toXpddlEqualsFunction(fluentExp);
                    xinit.addContent(xfluentExp);
                }
                else if(element.getExpID().equals(ExpID.ATOMIC_FORMULA)){
                    //predicate case
                    AtomicFormula predicate = (AtomicFormula)element;
                    Element xpredicate = toXpddlPredicate(predicate);
                    xinit.addContent(xpredicate);
                }

            }


            //4. goal state
            //get every declaration in the goal state
            System.out.println(problem.getGoal().toString());
            System.out.println(problem.getGoal().toTypedString());
            System.out.println(problem.getGoal().getExpID());

            Element xgoal = xpddl.getChild("goal");
            //Check the first node (and, or, single predicate of fluent

            //AND CASE
            if (problem.getGoal().getExpID().equals(ExpID.AND)){
                AndExp gandexp = (AndExp) problem.getGoal();
                Element xand = new Element("and");

                for (Iterator<Exp> it = gandexp.iterator(); it.hasNext();) {
                    Exp element = it.next();

                    //TODO: treat the other cases
                    if(element.getExpID().equals(ExpID.F_COMP)){
                        //function case
                        EqualComp fluentExp = (EqualComp) element;
                        Element xfluentExp = toXpddlEqualsFunction(fluentExp);
                        xand.addContent(xfluentExp);
                    }
                    else if(element.getExpID().equals(ExpID.ATOMIC_FORMULA)){
                        //predicate case
                        AtomicFormula predicate = (AtomicFormula)element;
                        Element xpredicate = toXpddlPredicate(predicate);
                        xand.addContent(xpredicate);
                    }

                }
                xgoal.addContent(xand);


            }
            //OR CASE
            else if (problem.getGoal().getExpID().equals(ExpID.OR)){
                OrExp gorexp = (OrExp) problem.getGoal();
            }
            //TODO: the other cases, assign, etc...
            //ONE SENTENCE CASE
            else{

                Exp theSingleGoal = problem.getGoal();

                //TODO: treat the other cases
                if(theSingleGoal.getExpID().equals(ExpID.F_COMP)){

                    //function case
                    EqualComp fluentExp = (EqualComp) theSingleGoal;
                    Element xfluentExp = toXpddlEqualsFunction(fluentExp);
                    xgoal.addContent(xfluentExp);
                }
                else if(theSingleGoal.getExpID().equals(ExpID.ATOMIC_FORMULA)){
                    //predicate case
                    AtomicFormula predicate = (AtomicFormula)theSingleGoal;
                    Element xpredicate = toXpddlPredicate(predicate);
                    xgoal.addContent(xpredicate);
                }

                
                //a single predicate
//                Literal predicate = (Literal) theSingleGoal;
//                Element xpredicate = new Element("predicate");
//                xpredicate.setAttribute("id", predicate.getPredicate());
//                for (Iterator<Term> it1 = predicate.iterator(); it1.hasNext();) {
//                    Term parameter = it1.next();
//                    Element xparameter = new Element("object");
//                    xparameter.setAttribute("id", parameter.toString());
//                    xpredicate.addContent(xparameter);
//                }
//                xgoal.addContent(xpredicate);

            }




            System.out.println("(!) Parsing problem done");

            //XMLUtilities.printXML(xpddl);
            return xpddl;
    }





        /**
     * This method generates a XPDDL verion of the given PDDL file string
     * @param problemFile
     * @return xpddl
     */
    public static Element parsePDDLproblemToXPDDL(String problemFile){
            Element xpddl = null;

            try {
                xpddl = (Element) XMLUtilities.readFromFile("resources/settings/commonData.xml").getRootElement().getChild("xpddlNodes").getChild("xpddlProblem").clone();
            } catch (JDOMException ex) {
                Logger.getLogger(PDDLToXPDDL.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PDDLToXPDDL.class.getName()).log(Level.SEVERE, null, ex);
            }


            System.out.println("(!) Parsing problem instance ...");

            Parser parser = new Parser(getParserOptions());


            //Reading the file and parsing it
            Problem problem = null;
            try {
                problem = parser.parse(new File(problemFile));




            } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }

            if(problem == null){
                System.out.println("Unexpected errors while parsing the pddl problem. Please check itSIMPLE's importer support.");
                return xpddl;
            }

            //1. Problem name and domain name
            System.out.println(problem.getProblemName());
            xpddl.getChild("name").setText(problem.getProblemName());
            xpddl.getChild("domain").setText(problem.getDomainName());


            //2. objects
            Element xobjects = xpddl.getChild("objects");
            for (Iterator<Constant> it = problem.constantsIterator(); it.hasNext();) {

                Constant object = it.next();
                TypeSet obType = object.getTypeSet();
                System.out.println(object.toTypedString());

                Element xobj = new Element("object");
                xobj.setAttribute("name", object.toString());
                xobj.setAttribute("type", obType.toString());

                xobjects.addContent(xobj);
            }



            //3. init state
            //get every declaration in the inital state
            Element xinit = xpddl.getChild("init");
            for (Iterator<InitEl> it = problem.getInit().iterator(); it.hasNext();) {
                InitEl element = it.next();
                //System.out.println(element);
                toXpddlExp(element,xinit);
            }


            //4. goal state
            //get every declaration in the goal state
            Element xgoal = xpddl.getChild("goal");
            toXpddlExp(problem.getGoal(),xgoal);


            //5. metrics
            MetricExp metric = problem.getMetric();
            if (metric != null){
                Element xmetric = null;
                try {
                    xmetric = (Element) XMLUtilities.readFromFile("resources/settings/commonData.xml").getRootElement().getChild("xpddlNodes").getChild("metric").clone();
                } catch (JDOMException ex) {
                    Logger.getLogger(PDDLToXPDDL.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PDDLToXPDDL.class.getName()).log(Level.SEVERE, null, ex);
                }
                xmetric.getChild("optimization").setText(metric.getOptimization().toString().toLowerCase());
                toXpddlExp(metric.getExp(), xmetric.getChild("expression"));

                //System.out.println();
                xpddl.addContent(xmetric);

            }




            System.out.println("(!) Parsing problem done");

            //XMLUtilities.printXML(xpddl);
            return xpddl;
    }






    private static Element toXpddlPredicate(AtomicFormula predicate) {
        Element xpredicate = new Element("predicate");
        xpredicate.setAttribute("id", predicate.getPredicate());

        for (Iterator<Term> it1 = predicate.iterator(); it1.hasNext();) {
            Term parameter = it1.next();
            if (parameter.getTermID().equals(TermID.CONSTANT)){
                Element xparameter = new Element("object");
                xparameter.setAttribute("id", parameter.toString());
                xpredicate.addContent(xparameter);
            }
            else if (parameter.getTermID().equals(TermID.VARIABLE)){
                Element xparameter = new Element("paremeter");
                xparameter.setAttribute("id", parameter.toString().replaceAll("\\?", ""));
                xpredicate.addContent(xparameter);

            }
        }

        return xpredicate;
    }

    private static Element toXpddlFunction(FHead function) {
        //System.out.println(function.getImage());
        //System.out.println(function.getTermID());
        Element xfunction = new Element("function");
        xfunction.setAttribute("id", function.getImage());
        for (Iterator<Term> it1 = function.iterator(); it1.hasNext();) {
            Term parameter = it1.next();

            if (parameter.getTermID().equals(TermID.CONSTANT)){
                Element xparameter = new Element("object");
                xparameter.setAttribute("id", parameter.toString());
                xfunction.addContent(xparameter);
            }
            else if (parameter.getTermID().equals(TermID.VARIABLE)){
                Element xparameter = new Element("paremeter");
                xparameter.setAttribute("id", parameter.toString().replaceAll("\\?", ""));
                xfunction.addContent(xparameter);

            }            
        }

        return xfunction;
    }


    private static Element toXpddlEqualsFunction(EqualComp fluentExp) {

        Element xfluentExp = new Element("equals");

        //the function
        Term firstTerm = fluentExp.getArg1();
        //System.out.println(firstTerm.toString());
        //System.out.println(firstTerm.getExpID());
        //System.out.println(firstTerm.getClass()); //FHEAD

        toXpddlExp(firstTerm, xfluentExp);

        //the value
        Term secondTerm = fluentExp.getArg2();
        toXpddlExp(secondTerm, xfluentExp);

        return xfluentExp;
    }


    private static Element toXpddlVariable(Variable var, boolean withType){

        Element xvar = new Element("parameter");

        if(withType){
            xvar.setAttribute("name",var.getImage().replaceAll("\\?", ""));
            xvar.setAttribute("type",var.getTypeSet().toString());
        }else{
            xvar.setAttribute("id",var.getImage().replaceAll("\\?", ""));

        }
        return xvar;
    }


     public static void toXpddlExp(Exp exp, Element parent){

        
         if(exp.getExpID().equals(ExpID.TERM)){
             Term term = (Term) exp;
             System.out.println(exp.getExpID() + " - " + term.getTermID().toString());
             
         }else{
             System.out.println(exp.getExpID() + " - " + exp.getClass().getSimpleName());

         }

        
      
        //predicate
        if(exp.getExpID().equals(ExpID.ATOMIC_FORMULA)){
            AtomicFormula predicate = (AtomicFormula)exp;
            Element xpredicate = toXpddlPredicate(predicate);
            parent.addContent(xpredicate);

        }
        //equals function (= functoin value)) where the comparison operator can be =,<,>, <=,>=
        else if(exp.getExpID().equals(ExpID.F_COMP)){
            
            FCompExp op = (FCompExp)exp;
            Element xop = null;
            
            if(op.getOp().equals(Comp.EQUAL)){
                xop = new Element("equals");
            }
            else if(op.getOp().equals(Comp.GREATER)){
                xop = new Element("gt");
            }
            else if(op.getOp().equals(Comp.LESS)){
                xop = new Element("lt");
            }
            else if(op.getOp().equals(Comp.GEQUAL)){
                xop = new Element("ge");                
            }
            else if(op.getOp().equals(Comp.LEQUAL)){
                xop = new Element("le");                
            }
            
            if(xop!=null){
                //the function
                toXpddlExp(op.getArg1(), xop);
                //the value
                toXpddlExp(op.getArg2(), xop);
                parent.addContent(xop);

            }

        }
        //term       can be object function, value, etc
        else if(exp.getExpID().equals(ExpID.TERM)){
            Term term = (Term) exp;

            //constant
            if(term.getTermID().equals(TermID.CONSTANT)){

            }
            //function
            else if(term.getTermID().equals(TermID.FUNCTION)){
                FHead function = (FHead)exp;
                Element xfunction = toXpddlFunction(function);
                parent.addContent(xfunction);
            }
            //number
            else if(term.getTermID().equals(TermID.NUMBER)){
                Number value = (Number)term;
                Element xvalue = new Element("value");
                xvalue.setAttribute("number",value.toString());
                parent.addContent(xvalue);
            }
            //variable
            else if(term.getTermID().equals(TermID.VARIABLE)){
                //TODO: discovery the case

//                Variable var = (Variable)term;
//                Element xvar = new Element("parameter");
//                xvar.setAttribute("name",var.getImage().replaceAll("\\?", ""));
//                xvar.setAttribute("type",var.getTypeSet().toString());
//                parent.addContent(xvar);
            }
            //arithmetic function (*, +, -, /)
            else if(term.getTermID().equals(TermID.ARITHMETIC_FUNCTION)){
                 //System.out.println(term.getClass().getSimpleName());
                 //System.out.println(term.getImage());
                 Element xarith = null;
                 if(term.getImage().equals("*")){
                     xarith = new Element("times");
                 }
                 else if(term.getImage().equals("+")){
                     xarith = new Element("add");
                 }
                 else if(term.getImage().equals("-")){
                     xarith = new Element("subtract");
                 }
                 else if(term.getImage().equals("/")){
                     xarith = new Element("divide");
                 }
                 if(term.getClass().getSimpleName().equals("NArityMultiply") ||
                         term.getClass().getSimpleName().equals("NArityAdd")){
                     NArityMultiply arith = (NArityMultiply)term;
                     for (Iterator<Term> it = arith.iterator(); it.hasNext();) {
                         Term theterm = it.next();
                         toXpddlExp(theterm, xarith);
                     }                     
                 }else{
                     BinaryOp arith = (BinaryOp)term;
                     toXpddlExp(arith.getArg1(), xarith);
                     toXpddlExp(arith.getArg2(), xarith);
                 }


                 parent.addContent(xarith);
            }

        }
        //and
        else if(exp.getExpID().equals(ExpID.AND)){
            AndExp gandexp = (AndExp) exp;
            Element xand = new Element("and");
            for (Iterator<Exp> it = gandexp.iterator(); it.hasNext();) {
                Exp element = it.next();
                toXpddlExp(element,xand);
            }
            parent.addContent(xand);
        }
        //or
        else if(exp.getExpID().equals(ExpID.OR)){
            OrExp gorexp = (OrExp) exp;
            Element xor = new Element("or");
            for (Iterator<Exp> it = gorexp.iterator(); it.hasNext();) {
                Exp element = it.next();
                toXpddlExp(element,xor);
            }
            parent.addContent(xor);
        }
        //not
        else if(exp.getExpID().equals(ExpID.NOT)){
            NotExp notexp = (NotExp) exp;
            Element xnot = new Element("not");
            toXpddlExp(notexp.getExp(),xnot);
            parent.addContent(xnot);
        }
        //imply
        else if(exp.getExpID().equals(ExpID.IMPLY)){
            ImplyExp implyexp = (ImplyExp) exp;
            Element ximply = new Element("imply");
            toXpddlExp(implyexp.getHead(), ximply);
            toXpddlExp(implyexp.getBody(), ximply);
            parent.addContent(ximply);
        }
        //exists
        else if(exp.getExpID().equals(ExpID.EXIST)){
            ExistsExp existsexp = (ExistsExp) exp;
            Element xexists = new Element("exists");
            System.out.println(existsexp.toTypedString());

            for (Iterator it = existsexp.iterator(); it.hasNext();) {
                Term objvar = (Term)it.next();
                System.out.println(objvar.getTypeSet());
                System.out.println(objvar.toTypedString());
                if (objvar.getTermID().equals(TermID.VARIABLE)){
                    Variable var = (Variable)objvar;
                    Element xvar = toXpddlVariable(var,true);
                    xexists.addContent(xvar);
                }
            }
            toXpddlExp(existsexp.getExp(), xexists);
            parent.addContent(xexists);
        }
        //when
        else if(exp.getExpID().equals(ExpID.WHEN)){
            WhenExp whenexp = (WhenExp) exp;
            Element xwhen = new Element("when");
            toXpddlExp(whenexp.getCondition(), xwhen);
            toXpddlExp(whenexp.getEffect(), xwhen);
            parent.addContent(xwhen);
        }


    }



    public static void main(String[] args) {
            System.out.println("Parsing");

             Parser parser = new Parser(getParserOptions());

            //domain
            Domain domain = null;
            try {
                //domain = parser.parse(new File("/home/tiago/Desktop/gold_miner_domain_v2.pddl"));
                domain = parser.parse(new File("/home/tiago/Desktop/Logistic_Domain_v1.pddl"));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PDDLToXPDDL.class.getName()).log(Level.SEVERE, null, ex);
            }

             //parsePDDLproblemToPDDL("/home/tiago/Desktop/gold-miner-target-5x5-02.pddl");
             parsePDDLproblemToXPDDL("/home/tiago/Desktop/LogisticTwoPackages.pddl");



/*
            

            //Reading the file and parsing it
            Problem problem = null;
            try {
                problem = parser.parse(new File("/home/tiago/Desktop/LogisticTwoPackages.pddl"));

            } catch (FileNotFoundException ex) {
                Logger.getLogger(PDDLToXPDDL.class.getName()).log(Level.SEVERE, null, ex);
            }


            System.out.println(problem.getProblemName());

             PDDLObject obj = parser.link(domain, problem);
             // Gets the error manager of the pddl parsermgr.print(Message.ALL);
                }
             ErrorManager mgr = parser.getErrorManager();
             // If the parser produces errors we print it and stop
             if (mgr.contains(Message.ERROR)) {
                 mgr.print(Message.ALL);
             } // else we print the warnings
             else { if (mgr.contains(Message.ERROR)) {
//                            mgr.print(Message.ALL);
//                        }
                 mgr.print(Message.WARNING);
                 System.out.println("\nParsing domain \"" + domain.getDomainName()
                             + "\" done successfully ...");
                 System.out.println("Parsing problem \"" + problem.getProblemName()
                             + "\" done successfully ...\n");
             }
             *
             */




//                try {
//                    // Creates an instance of the java pddl parser
//                    Parser parser = new Parser(getParserOptions());
//                    //  Gets the error manager of the pddl parser
//                    ErrorManager mgr = parser.getErrorManager();
//
//                    boolean success;
//                    //PDDLObject domain = parser.parse(new File(args[0]));
//                    PDDLObject domain = parser.parse(new File("/home/tiago/Desktop/Logistic_Domain_v1.pddl"));
//
//                    if (mgr.contains(Message.ERROR)) {
//                        mgr.print(Message.ALL);
//                    } // else we print the warning and start the planning process
//                    else {
//                        mgr.print(Message.WARNING);
//                        System.out.println("\nParsing domain \"" + domain.getDomainName()
//                                    + "\" done successfully ...");
//                        System.out.println(domain);
//
//
//                        //PDDLObject problem = parser.parse(new File(args[1]));
//                        PDDLObject problem = parser.parse(new File("/home/tiago/Desktop/LogisticTwoPackages.pddl"));
//                        if (mgr.contains(Message.ERROR)) {
//                            mgr.print(Message.ALL);
//                        } // else we print the warning and start the planning process
//                        else {
//                            mgr.print(Message.WARNING);
//                            System.out.println("\nParsing problem \"" + problem.getProblemName()
//                                    + "\" done successfully ...");
//                            //System.out.println(problem);
//
//
//                            System.out.println(problem.getProblemName());
//                            PDDLObject obj = parser.link(domain, problem);
//
//
//                            if (mgr.contains(Message.LINKER_ERROR)) {
//                                mgr.print(Message.LINKER_ERROR);
//                                } // else we print the warning and start the planning process
//                            else {
//                                mgr.print(Message.LINKER_WARNING);
//                                System.out.println("Linking \"" + domain.getDomainName() + "\" with \"" + problem.getProblemName()
//                                    + "\" done successfully ...");
//                                System.out.println(obj);
//
//                                /*Iterator<ActionDef> i = obj.actionsIterator();
//                                while (i.hasNext()) {
//                                    ActionDef action = i.next();
//                                    System.out.println(action.toTypedString());
//                                    System.out.println(action.normalize().toTypedString());
//
//                                }*/
//
//
//                            }
//                        }
//                    }
//
//                    // If the parser produces errors we print it and stop
//
//                } catch (FileNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }


    }





	
}
