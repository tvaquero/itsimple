/***
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
*
* Copyright (C) 2007-2011 University of Sao Paulo
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
*               Matheus Haddad
*
**/


package src.planning;


import java.awt.Color;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.nfunk.jep.JEP;

import src.gui.ItSIMPLE;
import src.languages.xml.XMLUtilities;
import src.util.database.DataBase;

/**
 * This class is responsible for all plan analysis processes including plan evaluation, plan comparison.
 * @author Tiago
 */
public class PlanAnalyzer {


    /**
     * This method evaluates a plan based on the metrics preferences
     * @param metrics
     * @return
     */
    public static double evaluatePlan(Element metrics){

        double overallGrade = 0;

        double upExpression = 0;
        double weights = 0;

        if(metrics!=null){
            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element qualityMetric = it.next();
                String metricWeight = qualityMetric.getChildText("weight");

                double weight = 1;
                if (!metricWeight.trim().equals("")){
                    try {
                        weight = Double.parseDouble(metricWeight);
                    } catch (Exception e) {
                        weight = 1;
                    }
                }
                            //evaluate the metric
                double metricGrade = evaluateMetric(qualityMetric);

                upExpression += metricGrade*weight;
                weights += weight;
            }

        }



        if (weights > 0){
            overallGrade = upExpression/weights;
        }

        return overallGrade;
    }

/**
     * This method evaluates a plan based on the specified metric evaluations.
     *  The evaluation of the metrics is redone only if necessary, empty)
     * @param thePlan
     * @return the plan score recomputed
     */
    public static double reEvaluatePlan(Element thePlan){

        double overallScore = 0;

        double upExpression = 0;
        double weights = 0;

        Element metrics = thePlan.getChild("metrics");

        if(metrics!=null){
            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element qualityMetric = it.next();
                String metricWeight = qualityMetric.getChildText("weight");

                double weight = 1;
                if (!metricWeight.trim().equals("")){
                    try {
                        weight = Double.parseDouble(metricWeight);
                    } catch (Exception e) {
                        weight = 1;
                    }
                }
                //evaluate the metric
                Element metricEvaluation = qualityMetric.getChild("evaluation");
                double metricScore = 0.0;
                if (metricEvaluation!=null){
                    String metricScoreStr = metricEvaluation.getAttributeValue("value");
                    if (!metricScoreStr.trim().equals("")){
                        metricScore = Double.parseDouble(metricScoreStr);
                    }else{
                        metricScore = evaluateMetric(qualityMetric);
                    }
                }
                else{
                    metricScore = evaluateMetric(qualityMetric);
                }

                upExpression += metricScore*weight;
                weights += weight;
            }

        }



        if (weights > 0){
            overallScore = upExpression/weights;
        }

        return overallScore;
    }


    /**
     * This method evalutes a single metric based on its preferences
     * @param metric
     * @return
     */
    public static double evaluateMetric(Element metric){

        double metricGrade = 0;

        String metricname = metric.getChildText("name");
        String metrictype = metric.getChildText("type");
        String metricID = metric.getAttributeValue("id");
        String metricLevel = metric.getAttributeValue("level");

        


        float finalValue = getMetricValue(metric);

                     
        //calculate grade
        List<Element> preferenceFunctions = metric.getChild("preferenceFunction").getChildren("function");
        for (int i = 0; i < preferenceFunctions.size(); i++) {
            Element afunction = preferenceFunctions.get(i);

            Element lower = afunction.getChild("domain").getChild("lowerbound");
            Element upper = afunction.getChild("domain").getChild("upperbound");
            String func = afunction.getChildText("rule");

            String lowerbound = lower.getAttributeValue("value");
            String lowerIncluded = lower.getAttributeValue("included");
            String upperbound = upper.getAttributeValue("value");
            String upperIncluded = upper.getAttributeValue("included");

            boolean lowerOk = false;
            boolean upperOk = false;
            float lowerlimit = 0;
            float upperlimit = 0;


            //checking lower limit
            if (lowerbound.equals("-inf")){
                lowerOk = true;
            }
            else{
                if (!lowerbound.contains(".")){//integer case
                    lowerlimit = Integer.parseInt(lowerbound);
                }
                else{//float case
                    lowerlimit = Float.parseFloat(lowerbound);
                }

                if (lowerIncluded.toLowerCase().equals("true")){
                    if (lowerlimit <= finalValue){
                        lowerOk = true;
                    }
                }
                else{
                    if (lowerlimit < finalValue){
                        lowerOk = true;
                    }
                }
            }

            //checking upper limit
            if (upperbound.equals("+inf")){
                upperOk = true;
            }
            else{
                if (!upperbound.contains(".")){//integer case
                    upperlimit = Integer.parseInt(upperbound);
                }
                else{//float case
                    upperlimit = Float.parseFloat(upperbound);
                }

                if (upperIncluded.toLowerCase().equals("true")){
                    if (finalValue <= upperlimit){
                        upperOk = true;
                    }
                }
                else{
                    if (finalValue < upperlimit){
                        upperOk = true;
                    }
                }

            }

            if (lowerOk && upperOk){
                JEP myParser = new JEP();
                myParser.addStandardFunctions();
                myParser.addStandardConstants();
                myParser.addVariable("x", finalValue);
                myParser.parseExpression(func);
                metricGrade = myParser.getValue();
                //System.out.println(metricGrade);
            }

        }

        return metricGrade;
    }



    /**
     * This method evaluate the cost or award of a given metric (SUM (value*weight))
     * @param metrics
     * @return
     */
    public static double evaluateCostAward(Element metrics){

        double overallCostAward = 0;


        if(metrics!=null){
            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element qualityMetric = it.next();
                String metricWeight = qualityMetric.getChildText("weight");

                double weight = 1;
                if (!metricWeight.trim().equals("")){
                    try {
                        weight = Double.parseDouble(metricWeight);
                    } catch (Exception e) {
                        weight = 1;
                    }
                }
                            //evaluate the metric
                double metricValue = getMetricValue(qualityMetric);

                overallCostAward += metricValue*weight;
            }            
        }
  

        return overallCostAward;
    }


    /**
     * This method returns the value of the metric after a given plan being executed
     * It shoud considered the final/last value, average, min, max (but it restricted to
     * final/last value so far)
     * @param metric
     * @return
     */
    public static float getMetricValue(Element metric){

        float value = 0;

        Element dataset = metric.getChild("dataset");

        if (dataset.getChildren().size() > 0){
            
            //Case: FINAL VALUE
            int lastIndex = dataset.getChildren().size()-1;
            Element finalSet = (Element)dataset.getChildren().get(lastIndex);

            try {
                value = Float.parseFloat(finalSet.getAttributeValue("value"));
            } catch (Exception e) {
                System.out.println("System could not parse the last value");
                return 0;
            }

            //TODO: CASE min

            //TODO: CASE max

            //TODO: CASE average
            
        }

        return value;
    }

   /**
     * This method generated a fuul html report of a given plan. It shows the basic data such as
     * the planner that solved the problem, its statistics, the Gantt chart and the charts representing
     * metric values (if defined) with a initial evaluation of the plan.
     * @param domain
     * @param problem
     * @param xmlPlan
     * @param metrics
     * @return
     */
    public static String generateFullPlanReport(Element domain, Element problem, Element xmlPlan, Element metrics){
        String html = "";

        //Get the main elements
        Element project = domain.getDocument().getRootElement();
        Element planner = xmlPlan.getChild("planner");
        Element statistics = xmlPlan.getChild("statistics");
        Element plan = xmlPlan.getChild("plan");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateTime = dateFormat.format(date);
        //String dateTime = xmlPlan.getChildText("datetime");



        String domainName = project.getChildText("name") + " - " + domain.getChildText("name");
        String domainDescription = domain.getChildText("description");
        String problemName = problem.getChildText("name");
        String problemDescription = problem.getChildText("description");
        String plannerName = planner.getChildText("name");


        //HTML
        //1. HEAD
        html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"> \n";
        html += "<html xmlns=\"http://www.w3.org/1999/xhtml\"> \n";
        html += "<head> \n";
        html += "	<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" /> \n";
        html += "	<title>postDAM - Plan Analysis</title> \n";
        html += "	<meta name=\"keywords\" content=\"\" /> \n";
        html += "	<meta name=\"description\" content=\"\" /> \n";
        html += "	<link href=\"default.css\" rel=\"stylesheet\" type=\"text/css\" /> \n";
        html += "	<script language=\"JavaScript\" src=\"JSClass/FusionCharts.js\"></script> \n";
        html += "</head> \n";

        //2. BODY
        html += "<body> \n";

        String htmlhead = "	<div id=\"header\"> \n";
        htmlhead += "		<div id=\"logo\"> \n";
        htmlhead += "			<h1><span><a href=\"#\">plan</a></span><a href=\"#\">Report</a></h1> \n";
        htmlhead += "			<h2><a href=\"http://dlab.poli.usp.br\">by itSIMPLE</a></h2> \n";
        htmlhead += "		</div> \n";
        htmlhead += "		<div id=\"menu\"> \n";
        htmlhead += "			<ul> \n";
        htmlhead += "				<li class=\"first\"><a href=\"#\" accesskey=\"1\" title=\"\">Home</a></li> \n";
        htmlhead += "				<li><a href=\"#metrics\" accesskey=\"2\" title=\"\">Metrics</a></li> \n";
        htmlhead += "				<li><a href=\"#jointview\" accesskey=\"3\" title=\"\">Joint View</a></li> \n";
        htmlhead += "				<li><a href=\"#evaluation\" accesskey=\"3\" title=\"\">Evaluation</a></li>  \n";
        htmlhead += "				<li><a href=\"#about\" accesskey=\"4\" title=\"\">About</a></li> \n";
        htmlhead += "			</ul> \n";
        htmlhead += "		</div> \n";
        htmlhead += "	</div> \n";
        htmlhead += "	<div id=\"splash\"><a href=\"#\"><img src=\"images/img4.jpg\" alt=\"\" width=\"877\" height=\"140\" /></a></div> \n";


        //Content

        //2.1 Introduction
        String htmlcontent = "	<div id=\"content\"> \n";
        htmlcontent += "		<div id=\"colOne\"> \n";
        htmlcontent += "		<h2>Introduction</h2> \n";
        htmlcontent += "		<p><strong>PlanReport</strong> is a plan analysis interface for helping designers investigate solutions provided by automated planners.<br> \n";
        htmlcontent += "		This report was generted based on the information that follows:</p> \n";
        htmlcontent += "		<ul> \n";
        htmlcontent += "			<li><strong>. Domain: </strong>"+domainName +"</li> \n";
        htmlcontent += "			<li><strong>. Planning problem: </strong>"+problemName+"</li> \n";
        htmlcontent += "			<li><strong>. Solution provided by: </strong>"+plannerName+"</li> \n";
        htmlcontent += "		<ul> <br>\n";
        htmlcontent += "		<p>Planner's info:</p> \n";
        htmlcontent += "		<ul> \n";
        htmlcontent += "			<li><strong>. Name: </strong>"+plannerName+"</li> \n";
        htmlcontent += "			<li><strong>. Version: </strong>"+planner.getChildText("version")+"</li> \n";
        htmlcontent += "			<li><strong>. Date: </strong>"+planner.getChildText("date")+"</li> \n";
        htmlcontent += "			<li><strong>. Institution: </strong>"+planner.getChildText("institution")+"</li> \n";
        htmlcontent += "			<li><strong>. Author(s): </strong>"+planner.getChildText("author")+"</li> \n";
        String link = planner.getChildText("link");
        if (!link.trim().equals("")){
            htmlcontent += "			<li><strong>. Website: </strong><a href='"+link+"' target='_blank'>"+link+"</a></li> \n";
        }
        else{
            htmlcontent += "			<li><strong>. Website: </strong>not available</li> \n";
        }
        htmlcontent += "			<li><strong>. Description: </strong>"+planner.getChildText("description") +"</li> \n";
        htmlcontent += "		<ul> <br> \n";
        htmlcontent += "		<p>Planner's statistics:</p> \n";
        htmlcontent += "		<ul> \n";
        htmlcontent += "			<li><strong>. Plan size: </strong>"+Integer.toString(plan.getChildren().size())+"</li> \n";
        htmlcontent += "			<li><strong>. Time seen by itSIMPLE: </strong>"+statistics.getChildText("toolTime")+" seconds</li> \n";
        htmlcontent += "			<li><strong>. Time: </strong>"+statistics.getChildText("time")+"</li> \n";
        htmlcontent += "			<li><strong>. Parsing time: </strong>"+statistics.getChildText("parsingTime")+"</li> \n";
        htmlcontent += "			<li><strong>. Number of actions: </strong>"+ xmlPlan.getChild("plan").getChildren().size()+"</li> \n";
        htmlcontent += "			<li><strong>. Makespan: </strong>"+statistics.getChildText("makeSpan")+"</li> \n";
        htmlcontent += "			<li><strong>. Metric value (given by the planner): </strong>"+statistics.getChildText("metricValue")+"</li> \n";
        htmlcontent += "			<li><strong>. Planning Technique: </strong>"+statistics.getChildText("planningTechnique")+"</li> \n";
        htmlcontent += "			<li><strong>. Additional: </strong>"+statistics.getChildText("additional")+"</li> \n";
        htmlcontent += "		<ul> <br>\n";

        int chartIndex = 0;



        //2.2 Gantt chart

        //find out total duratrion
        float totalDuration = 0;
        List<Element> theactions = plan.getChildren();
        for (Iterator<Element> it = theactions.iterator(); it.hasNext();) {
            Element eaction = it.next();
            String starttime = eaction.getChildText("startTime");
            String duration = eaction.getChildText("duration");
            float endtime = Float.parseFloat(starttime) + Float.parseFloat(duration);
            if (totalDuration < endtime){totalDuration = endtime;}
        }

        Date todayDate = new Date();
        //System.out.println(todayDate);
        DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String todayStr = sdf.format(todayDate);

        int totalDurationInt = (int) totalDuration;

        Calendar now = Calendar.getInstance();
        now.setTime(todayDate);
        now.add(Calendar.DAY_OF_MONTH, totalDurationInt);
        Date finalDay = now.getTime();
        String finalDayStr = sdf.format(finalDay);
        //System.out.println(finalDay);
        //System.out.println(totalDuration);

        String dataXML = "<graph dateFormat='mm/dd/yyyy'>";
        dataXML += "<categories>";
        dataXML += "<category start='"+ todayStr + "' end='"+finalDayStr+"' name='Plan' />";
        dataXML += "</categories>";
        dataXML += "<categories>";
        dataXML += "<category start='"+ todayStr + "' end='"+finalDayStr+"' name='period' />" ;
        dataXML += "</categories>";

        String dataXMLprocesses = "<processes fontSize='9' isBold='1' align='left' headerText='Actions' headerFontSize='16' headerVAlign='bottom' headerAlign='right'>";
        String dataXMLtasks = "<tasks showname='1'>";

        int actionid = 1;

        for (Iterator<Element> it = theactions.iterator(); it.hasNext();) {
            Element eaction = it.next();

            String actionName = eaction.getAttributeValue("id");
            //params = eaction.findall("parameters/parameter")
            for (Iterator<Element> it1 = eaction.getChild("parameters").getChildren().iterator(); it1.hasNext();) {
                Element par = it1.next();
                String parameterName = par.getAttributeValue("id");
                actionName += " " + parameterName;
            }
            String starttime = eaction.getChildText("startTime");
            String duration = eaction.getChildText("duration");

            dataXMLprocesses += "<process name='"+ actionName +"' id='"+Integer.toString(actionid)+"'/>";

            float fstarttime = Float.parseFloat(starttime);
            float fduration = Float.parseFloat(duration)-1;

            int fstarttimeInt = (int) fstarttime;
            int fdurationInt = (int) fduration;

            //get start time
            Calendar nowS = Calendar.getInstance();
            nowS.setTime(todayDate);
            nowS.add(Calendar.DAY_OF_MONTH, fstarttimeInt);
            Date stday = nowS.getTime();
            String startDay = sdf.format(stday);
            //get end time
            Calendar nowE = Calendar.getInstance();
            nowE.setTime(stday);
            nowE.add(Calendar.DAY_OF_MONTH, fdurationInt);
            Date enday = nowE.getTime();
            String endDay = sdf.format(enday);
            //System.out.println(startDay);
            //System.out.println(endDay);

            dataXMLtasks += "<task start='"+startDay+"' end='"+endDay+"' processId='"+Integer.toString(actionid)+"' name='' showName='1' />";

            actionid += 1;
        }

        dataXMLprocesses += "</processes>";
        dataXMLtasks += "</tasks>";

        dataXML += dataXMLprocesses;
        dataXML += dataXMLtasks;
        dataXML += "</graph>";


        int gheight = plan.getChildren().size()*20 + 50;

        String htmlgantt = "		<h2><a name=\"gantt\">Gantt chart</a></h2> \n";
        htmlgantt +="		<p>The following Gantt chart ilustrates the plan given by the planner.\n";
        htmlgantt += "		<table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\"> \n";
        htmlgantt += "			<tr> \n";
        htmlgantt += "				<td valign=\"top\" class=\"text\" align=\"center\"> \n";
        htmlgantt += "					<div id=\"chartdiv" + Integer.toString(chartIndex) + "\" align=\"center\">Gantt Chart</div> \n";
        htmlgantt += "					<script type=\"text/javascript\"> \n";
        htmlgantt += "						var chart"+Integer.toString(chartIndex)+" = new FusionCharts(\"charts/FCF_Gantt.swf\", \"ChartId\", \"700\", \""+Integer.toString(gheight)+"\"); \n";
        htmlgantt += "						chart"+Integer.toString(chartIndex)+".setDataXML(\"" + dataXML + "\"); \n";
        htmlgantt += "						chart"+Integer.toString(chartIndex)+".render(\"chartdiv" + Integer.toString(chartIndex) + "\"); \n";
        htmlgantt += "					</script> \n";
        htmlgantt += "				</td> \n";
        htmlgantt += "			</tr> \n";
        htmlgantt += "		</table> \n";
        htmlgantt += "		<div class=\"posted\"> \n";
        htmlgantt += "			<p>generated by <a href=\"#\">postDAM</a> on " + todayStr + "</p> \n";
        htmlgantt += "			<p class=\"comments\"><a href=\"#\">comments</a></p> \n";
        htmlgantt += "		</div> \n";

        htmlcontent += htmlgantt;


        //Metrics charts
        chartIndex += 1;

        //If there are metrics build the charts
        if (metrics!=null && metrics.getChildren().size() > 0){

            //2.3 Metrics
            String htmlmetrics = "		<h2><a name=\"metrics\">Metrics</a></h2> \n";
            htmlmetrics +="		<p>The following metrics were chosen to be analyzed.\n";


            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element metric = it.next();

                String metricname = metric.getChildText("name");
                String metrictype = metric.getChildText("type");
                String metricID = metric.getAttributeValue("id");
                String metricLevel = metric.getAttributeValue("level");

                //TODO: select the name of the chart. Variable, expression, pslPlan.
                String chartTitle = metricname;


                htmlmetrics +=  "		<h3><strong>.: " + metricname + "</strong></h3> \n";
                htmlmetrics +=  "       	<p>The following chart presents the metric <strong>" + chartTitle + "</strong></p> \n";


                //TODO:Lets suppose all of them are numeric/integer

                //Preparing Fusion Chart Instance
                dataXML = "<graph caption='Attribute " + chartTitle + "' subcaption='' ";
                dataXML += "xAxisName='Steps' yAxisMinValue='0' yAxisName='Value' ";
                dataXML += "decimalPrecision='0' formatNumberScale='0' showNames='1' ";
                dataXML += "showValues='0' showAlternateHGridColor='1' AlternateHGridColor='ff5904' ";
                dataXML += "divLineColor='ff5904' divLineAlpha='20' alternateHGridAlpha='5' >";


                //additional info: minimum, maximum, avetage (when applied)
                Element dataset = metric.getChild("dataset");
                Element firstValue = (Element)dataset.getChildren().get(0);
                Element lastValue = (Element)dataset.getChildren().get(dataset.getChildren().size()-1);

                float minimum = Float.parseFloat(firstValue.getAttributeValue("value")); //get first record, value y (0 -> x, 1 ->, 2 -> comments)
                float maximum = Float.parseFloat(lastValue.getAttributeValue("value"));

                //read each value (X,Y) and creat each set of the graph
                for (Iterator<Element> it1 = dataset.getChildren().iterator(); it1.hasNext();) {
                    Element set = it1.next();
                    String x = set.getAttributeValue("name");
                    String y = set.getAttributeValue("value");

                    dataXML += "<set name='" + x + "' value='" + y + "' hoverText='" + x + "'/>";

                    float yValue = Float.parseFloat(y.trim());
                    //additional info
                    if (yValue < minimum){          
                        minimum = yValue;
                    }
                    if (yValue > maximum){
                        maximum = yValue;
                    }

                    //TODO: extend the X Axis by one unit for convinience when viewing the last points

                }



                dataXML +="</graph>";

                //Create the html chart component with the dataXML
                htmlmetrics += "		<table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\"> \n";
                htmlmetrics += "			<tr> \n";
                htmlmetrics += "				<td valign=\"top\" class=\"text\" align=\"center\"> \n";
                htmlmetrics += "					<div id=\"chartdiv" + Integer.toString(chartIndex) + "\" align=\"center\">" + chartTitle + "</div> \n";
                htmlmetrics += "					<script type=\"text/javascript\"> \n";
                htmlmetrics += "						var chart"+Integer.toString(chartIndex)+" = new FusionCharts(\"charts/FCF_Line.swf\", \"ChartId\", \"700\", \"360\"); \n";
                htmlmetrics += "						chart"+Integer.toString(chartIndex)+".setDataXML(\"" + dataXML + "\"); \n";
                htmlmetrics += "						chart"+Integer.toString(chartIndex)+".render(\"chartdiv" + Integer.toString(chartIndex) + "\"); \n";
                htmlmetrics += "                    </script> \n";
                htmlmetrics += "				</td> \n";
                htmlmetrics += "			</tr> \n";
                htmlmetrics += "			<tr> \n";
                htmlmetrics += "				<td valign=\"top\" class=\"text\"> \n";
                htmlmetrics += "					<p><strong>Minimum:</strong> "+ Float.toString(minimum)+"<br> \n";
                htmlmetrics += "					<strong>Maximum:</strong> "+ Float.toString(maximum)+"<br> </p>\n";
                htmlmetrics += "				</td> \n";
                htmlmetrics += "			</tr> \n";
                htmlmetrics += "		</table> \n";
                htmlmetrics += "		<div class=\"posted\"> \n";
                htmlmetrics += "			<p>generated by <a href=\"#\">postDAM</a> on "+todayStr+"</p> \n";
                htmlmetrics += "			<p class=\"comments\"><a href=\"#\">comments</a></p> \n";
                htmlmetrics += "		</div> \n";

                //Numeric/Integer Attributes type ends here

                chartIndex += 1;


            }

            htmlcontent += htmlmetrics;



            //2.4 A joint view of all metrics
            String htmljointview =  "		<h2><a name=\"jointview\">Joint View</a></h2> \n";
            htmljointview +=  "		<p>The following chart presents a combination of all metric charts.</p> \n";


            dataXML = "<graph caption='Joint view of metrics' xAxisName='Steps' yAxisName='Value' decimalPrecision='2' ";
            dataXML += "formatNumberScale='0' showValues='0'>";

            //Get the x values
            dataXML += "<categories>";
            //get the first metric as a base
            Element fistMetric = (Element)metrics.getChildren().get(0);
            Element firstDataset = fistMetric.getChild("dataset");
            for (Iterator<Element> it = firstDataset.getChildren().iterator(); it.hasNext();) {
                Element set = it.next();
                String x = set.getAttributeValue("name");
                dataXML += "<category name='" + x + "' />";

            }
            dataXML += "</categories>";


            //get data set
            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element metric = it.next();

                String metricname = metric.getChildText("name");
                String metrictype = metric.getChildText("type");
                String metricID = metric.getAttributeValue("id");
                String metricLevel = metric.getAttributeValue("level");

                //TODO: put the appropriate name
                String chartTitle = metricname;

                String color = randomColor();

                String dataset =  "<dataset seriesName='"+chartTitle+"' color='"+color+"' anchorBorderColor='"+color+"' anchorBgColor='"+color+"' >";


                //TODO: need to check the attr type in roder to choose the correct 2d Graph
                // 	Lets suppose all of them are numeric/integer

                //read each value (X,Y) and creat each set of the graph
                for (Iterator<Element> it1 = metric.getChild("dataset").getChildren().iterator(); it1.hasNext();) {
                    Element set = it1.next();
                    String y = set.getAttributeValue("value");
                    dataset += "<set value='" + y + "' />";

                    //TODO: extend the X Axis by one unit for convinience when viewing the last points

                }
                dataset += "</dataset>";

                //TODO: Numeric/Integer Attributes type ends here

                dataXML += dataset;
            }

            dataXML += "</graph>";

            //Create the html for joint view with the dataXML
            htmljointview += "		<table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\"> \n";
            htmljointview += "			<tr> \n";
            htmljointview += "				<td valign=\"top\" class=\"text\" align=\"center\"> \n";
            htmljointview += "					<div id=\"chartdiv" + Integer.toString(chartIndex) + "\" align=\"center\">Joint view</div> \n";
            htmljointview += "					<script type=\"text/javascript\"> \n";
            htmljointview += "						var chart"+Integer.toString(chartIndex)+" = new FusionCharts(\"charts/FCF_MSLine.swf\", \"ChartId\", \"700\", \"360\"); \n";
            htmljointview += "						chart"+Integer.toString(chartIndex)+".setDataXML(\"" + dataXML + "\"); \n";
            htmljointview += "						chart"+Integer.toString(chartIndex)+".render(\"chartdiv" + Integer.toString(chartIndex) + "\"); \n";
            htmljointview += "					</script> \n";
            htmljointview += "				</td> \n";
            htmljointview += "			</tr> \n";
            htmljointview += "		</table> \n";
            htmljointview += "		<div class=\"posted\"> \n";
            htmljointview += "			<p>generated by <a href=\"#\">postDAM</a> on "+ todayStr + "</p> \n";
            htmljointview += "			<p class=\"comments\"><a href=\"#\">comments</a></p> \n";
            htmljointview += "		</div> \n";


            htmlcontent += htmljointview;

            chartIndex += 1;




            //2.5 Plan Evaluation

            String htmlevaluation = "		<h2><a name=\"evaluation\">Initial Plan Evaluation</a></h2> \n"	;
            htmlevaluation += "		<p>The following plan evaluation is based on quality preferences described in itSIMPLE.</p> \n";


            //Gathering the final values
            dataXML = "<graph caption='Initial PlanEvaluation' xAxisName='Metrics' yAxisName='Preference Scores' decimalPrecision='2' ";
            dataXML += "formatNumberScale='0'>";

            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element metric = it.next();

                String metricname = metric.getChildText("name");
                String metrictype = metric.getChildText("type");
                String metricID = metric.getAttributeValue("id");
                String metricLevel = metric.getAttributeValue("level");
                String metricWeight = metric.getChildText("weight");

                //TODO: put the appropriate name
                String name = metricname;


                float weight = 1;
                if (!metricWeight.trim().equals("")){
                    weight = Float.parseFloat(metricWeight);
                }

                String value = Double.toString(evaluateMetric(metric));

                dataXML += "<set name='" + name + "' value='"+ value +"' color='008ED6' hoverText='"+ name +" (weight " + Float.toString(weight) + ")' />";
            }


            //2.6 Overall evaluation (plan grade)
            double overallgrade = evaluatePlan(metrics);

            DecimalFormat overall = new DecimalFormat("0.00");

            dataXML += "<set name='Final' value='"+ Double.toString(overallgrade) +"' color='9D080D' />";
            dataXML += "</graph>";

            //Create the html evaluation chart component with the dataXML
            htmlevaluation += "		<table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\"> \n";
            htmlevaluation += "			<tr> \n";
            htmlevaluation += "				<td valign=\"top\" class=\"text\" align=\"center\"> \n";
            htmlevaluation += "					<div id=\"chartdiv" + Integer.toString(chartIndex) + "\" align=\"center\">Initial Plan Evaluation</div> \n";
            htmlevaluation += "					<script type=\"text/javascript\"> \n";
            htmlevaluation += "						var chart"+Integer.toString(chartIndex)+" = new FusionCharts(\"charts/FCF_Column3D.swf\", \"ChartId\", \"700\", \"360\"); \n";
            htmlevaluation += "						chart"+Integer.toString(chartIndex)+".setDataXML(\"" + dataXML + "\"); \n";
            htmlevaluation += "						chart"+Integer.toString(chartIndex)+".render(\"chartdiv" + Integer.toString(chartIndex) + "\"); \n";
            htmlevaluation += "					</script> \n";
            htmlevaluation += "				</td> \n";
            htmlevaluation += "			</tr> \n";
            htmlevaluation += "           		<tr> \n";
            htmlevaluation += "               		<td valign=\"top\" class=\"text\">  \n";
            htmlevaluation += "               			<p><strong>Plan Quality: <a name=\"planquality\">"+overall.format(overallgrade)+"</a></strong></p>  \n";
            htmlevaluation += "               		</td> \n";
            htmlevaluation += "          		</tr> \n";
            htmlevaluation += "		</table> \n";
            htmlevaluation += "		<div class=\"posted\"> \n";
            htmlevaluation += "			<p>generated by <a href=\"#\">postDAM</a> on "+todayStr+"</p> \n";
            htmlevaluation += "			<p class=\"comments\"><a href=\"#\">comments</a></p> \n";
            htmlevaluation += "		</div> \n";

            htmlcontent += htmlevaluation;


        }
        //If no metrics were specified
        else{
            String nometrics = "		<h2><a name=\"metrics\">Metrics</a></h2> \n";
            nometrics +="		<p>No metrics were used.\n";
            nometrics +=  "		<h2><a name=\"jointview\">Joint View</a></h2> \n";
            nometrics +=  "		<p>No metrics were used.</p> \n";
            nometrics += "		<h2><a name=\"evaluation\">Initial Plan Evaluation</a></h2> \n"	;
            nometrics += "		<p>No metrics were used.</p> \n";
            htmlcontent += nometrics;

        }





        //2.7 second column
        htmlcontent += "	</div> \n";
        htmlcontent += "	<div id=\"colTwo\"> \n"; //Second column
        htmlcontent += "		<h3>Links</h3> \n";
        htmlcontent += "		<ul> \n";
        htmlcontent += "			<li><a href=\"#metrics\">Metrics</a></li> \n";
        htmlcontent += "			<li><a href=\"#jointview\">Joint View</a></li> \n";
        htmlcontent += "			<li><a href=\"#evaluation\">Plan Evaluation</a></li> \n";
        htmlcontent += "		</ul> \n";
        htmlcontent += "	</div> \n";
        htmlcontent += "	<div style=\"clear: both;\">&nbsp;</div> \n";
        htmlcontent += "</div> \n";


        //2.8 footer
        String htmlfooter = "<div id=\"footer\"> \n";
        htmlfooter += "	<p>(c) 2010 itSIMPLE. Design by <a href=\"http://dlab.poli.usp.br/\">itSIMPLE team</a>.</p> \n";
        htmlfooter += "</div> \n";



        //complete html
        html += htmlhead;
        html += htmlcontent;
        html += htmlfooter;

        html += " </body> \n";
        html += "</html>" ;


        return html;
    }






    //TODO: Must change chart elements to google charts apps
    /**
     * This method generated a fuul html report of a given plan. It shows the basic data such as
     * the planner that solved the problem, its statistics, the Gantt chart and the charts representing
     * metric values (if defined) with a initial evaluation of the plan.
     * @param domain
     * @param problem
     * @param xmlPlan
     * @param metrics
     * @return
     */
    public static String generateFullPlanReport2(Element domain, Element problem, Element xmlPlan, Element metrics){



        StringBuilder html = new StringBuilder();


        Date todayDate = new Date();
        //System.out.println(todayDate);
        DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String todayStr = sdf.format(todayDate);


        //Get the main elements
        Element project = domain.getDocument().getRootElement();
        Element planner = xmlPlan.getChild("planner");
        Element statistics = xmlPlan.getChild("statistics");
        Element plan = xmlPlan.getChild("plan");
        Element validity = xmlPlan.getChild("validity");
        Element validator = xmlPlan.getChild("validator");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateTime = dateFormat.format(date);
        //String dateTime = xmlPlan.getChildText("datetime");



        String domainName = project.getChildText("name") + " - " + domain.getChildText("name");
        String domainDescription = domain.getChildText("description");
        String problemName = problem.getChildText("name");
        String problemDescription = problem.getChildText("description");
        String plannerName = planner.getChildText("name");



         //2.1.1 Main script (google tables and charts)
        StringBuilder script = new StringBuilder();

        script.append("	<script type='text/javascript'>\n");
        script.append("               google.load('visualization', '1', {packages:['linechart', 'columnchart']}); \n");
        script.append("               google.setOnLoadCallback(buildComponents); \n\n");
        script.append("               function buildComponents() {\n");



         StringBuilder evaluationChart = new StringBuilder();
         StringBuilder htmlevaluation = new StringBuilder();
         
         
         StringBuilder jointviewChart = new StringBuilder();
         StringBuilder htmljointview = new StringBuilder();

         StringBuilder metricCharts = new StringBuilder();
         StringBuilder htmlthemetrics = new StringBuilder();




        //DATA for GOOGLE CHARTS
        //If there are metrics instaciate the google charts
        if (metrics!=null && metrics.getChildren().size() > 0){

            int oIndex = 0;

            String odataName ="";
            String ochartName = "";


            //Individual metric charts

            //Metrics
            htmlthemetrics.append("		<h2><a name=\"metrics\">Metrics</a></h2> \n");
            htmlthemetrics.append("		<p>The following metrics were chosen to be analyzed.\n");


            int chartIndex = 0;

            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element metric = it.next();

                metricCharts = new StringBuilder();

                String metricname = metric.getChildText("name");
                String metrictype = metric.getChildText("type");
                String metricID = metric.getAttributeValue("id");
                String metricLevel = metric.getAttributeValue("level");

                //TODO: select the name of the chart. Variable, expression, pslPlan.

                String chartTitle = metricname;

                htmlthemetrics.append("		<h3><strong>.: " + metricname + "</strong></h3> \n");
                htmlthemetrics.append("       	<p>The following chart presents the metric <strong>" + chartTitle + "</strong></p> \n");



                 //Line Chart (google)
                odataName = "dataMetric"+chartIndex;
                metricCharts.append("var "+odataName+" = new google.visualization.DataTable(); \n");
                //Columns
                metricCharts.append(odataName+".addColumn('string', 'Steps'); \n");
                metricCharts.append(odataName+".addColumn('number', '"+metricname+"'); \n");
                //Rows
                Element dataset = metric.getChild("dataset");
                metricCharts.append(odataName+".addRows("+dataset.getChildren().size()+"); \n");

                //TODO:Lets suppose all of them are numeric/integer

                //additional info: minimum, maximum, avetage (when applied)
                Element firstValue = (Element)dataset.getChildren().get(0);
                Element lastValue = (Element)dataset.getChildren().get(dataset.getChildren().size()-1);

                float minimum = Float.parseFloat(firstValue.getAttributeValue("value")); //get first record, value y (0 -> x, 1 ->, 2 -> comments)
                float maximum = Float.parseFloat(lastValue.getAttributeValue("value"));

                oIndex = 0;
                //read each value (X,Y) and creat each set of the graph
                for (Iterator<Element> it1 = dataset.getChildren().iterator(); it1.hasNext();) {
                    Element set = it1.next();
                    String x = set.getAttributeValue("name");
                    String y = set.getAttributeValue("value");

                    metricCharts.append(odataName+".setValue("+oIndex+", 0, '"+x+"'); \n");
                    metricCharts.append(odataName+".setValue("+oIndex+", 1, "+y+"); \n");

                    float yValue = Float.parseFloat(y.trim());
                    //additional info
                    if (yValue < minimum){
                        minimum = yValue;
                    }
                    if (yValue > maximum){
                        maximum = yValue;
                    }
                    
                    oIndex++;


                    //TODO: extend the X Axis by one unit for convinience when viewing the last points

                }
                ochartName = "chartMetric"+chartIndex;

                metricCharts.append("var " + ochartName + " = new google.visualization.LineChart(document.getElementById('metric-graph-" + ochartName + "'));\n");
                metricCharts.append(ochartName + ".draw(" + odataName + ", {width: 840, height: 300, title:'"+chartTitle+"'});\n\n");

                script.append(metricCharts);



                //Create the html chart component with the dataXML
                htmlthemetrics.append("		<table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\"> \n");
                htmlthemetrics.append("			<tr> \n");
                htmlthemetrics.append("				<td valign=\"top\" class=\"text\" align=\"center\"> \n");
                htmlthemetrics.append("                             <div id=\"metric-graph-" + ochartName + "\"></div> \n");
                htmlthemetrics.append("				</td> \n");
                htmlthemetrics.append("			</tr> \n");
                htmlthemetrics.append("			<tr> \n");
                htmlthemetrics.append("				<td valign=\"top\" class=\"text\"> \n");
                htmlthemetrics.append("					<p><strong>Minimum:</strong> "+ Float.toString(minimum)+"<br> \n");
                htmlthemetrics.append("					<strong>Maximum:</strong> "+ Float.toString(maximum)+"<br> </p>\n");
                htmlthemetrics.append("				</td> \n");
                htmlthemetrics.append("			</tr> \n");
                htmlthemetrics.append("		</table> \n");
                htmlthemetrics.append("		<div class=\"posted\"> \n");
                htmlthemetrics.append("			<p>generated by <a href=\"#\">postDAM</a> on "+todayStr+"</p> \n");
                htmlthemetrics.append("			<p class=\"comments\"><a href=\"#\">comments</a></p> \n");
                htmlthemetrics.append("		</div> \n");

                //Numeric/Integer Attributes type ends here

                chartIndex += 1;


            }






            //Joint View chart

            //Line Chart (google)
            odataName = "dataJointView";
            jointviewChart.append("  var "+odataName+" = new google.visualization.DataTable(); \n");

            //Columns
            jointviewChart.append(odataName+".addColumn('string', 'Steps'); \n");

            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element metric = it.next();
                String metricname = metric.getChildText("name");
                jointviewChart.append(odataName+".addColumn('number', '"+metricname+"'); \n");
            }

            //Get the series
            //get the first metric as a base
            Element fistMetric = (Element)metrics.getChildren().get(0);
            Element firstDataset = fistMetric.getChild("dataset");

            jointviewChart.append(odataName+".addRows("+firstDataset.getChildren().size()+"); \n");

            oIndex = 0;
            List<String> xaxis = new ArrayList<String>();
            for (Iterator<Element> it = firstDataset.getChildren().iterator(); it.hasNext();) {
                Element set = it.next();
                String x = set.getAttributeValue("name");
                xaxis.add(dateTime);
                jointviewChart.append(odataName+".setValue("+oIndex+", 0, '"+x+"'); \n");
                oIndex++;
            }

            oIndex = 1;
            //get data set
            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element metric = it.next();

                String metricname = metric.getChildText("name");
                String metrictype = metric.getChildText("type");
                String metricID = metric.getAttributeValue("id");
                String metricLevel = metric.getAttributeValue("level");

                //TODO: put the appropriate name
                String chartTitle = metricname;

                //String color = randomColor();

                //TODO: need to check the attr type in roder to choose the correct 2d Graph
                // 	Lets suppose all of them are numeric/integer

                //read each value (X,Y) and creat each set of the graph
                int theXindex = 0;
                for (Iterator<Element> it1 = metric.getChild("dataset").getChildren().iterator(); it1.hasNext();) {
                    Element set = it1.next();
                    String y = set.getAttributeValue("value");
                    jointviewChart.append(odataName+".setValue("+theXindex+", "+oIndex+", "+y+"); \n");

                    //TODO: extend the X Axis by one unit for convinience when viewing the last points
                    theXindex++;
                }
                oIndex++;


            }


            ochartName = "chartJointview";

            jointviewChart.append("var " + ochartName + " = new google.visualization.LineChart(document.getElementById('analysis-graph-" + ochartName + "'));\n");
            jointviewChart.append(ochartName + ".draw(" + odataName + ", {width: 840, height: 300, title:'Joint View of Metrics'});\n\n");

            script.append(jointviewChart);


            //joint view html content
            htmljointview = new StringBuilder();
            htmljointview.append("		<h2><a name=\"jointview\">Joint View</a></h2> \n");
            htmljointview.append("		<p>The following chart presents a combination of all metric charts.</p> \n");
            htmljointview.append("		<table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\"> \n");
            htmljointview.append("			<tr> \n");
            htmljointview.append("				<td valign=\"top\" class=\"text\" align=\"center\"> \n");
            htmljointview.append("                             <div id=\"analysis-graph-" + ochartName + "\"></div> \n");
            htmljointview.append("				</td> \n");
            htmljointview.append("			</tr> \n");
            htmljointview.append("		</table> \n");
            htmljointview.append("		<div class=\"posted\"> \n");
            htmljointview.append("			<p>generated by <a href=\"#\">postDAM</a> on "+ todayStr + "</p> \n");
            htmljointview.append("			<p class=\"comments\"><a href=\"#\">comments</a></p> \n");
            htmljointview.append("		</div> \n");






            //Plan Evaluation chart

            //StringBuilder evaluationChart = new StringBuilder();

            //Evaluation - Column Chart (google)
            odataName = "dataEvaluation";
            evaluationChart.append("  var "+odataName+" = new google.visualization.DataTable(); \n");

            //Columns
            evaluationChart.append(odataName+".addColumn('string', 'Metrics'); \n");
            evaluationChart.append(odataName+".addColumn('number', 'Score'); \n");
            evaluationChart.append(odataName+".addColumn('number', 'Overall'); \n");

            //Rows
            evaluationChart.append(odataName+".addRows("+(metrics.getChildren().size()+1)+"); \n");

            //Individual Metric evaluations
            oIndex = 0;
            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element metric = it.next();

                String metricname = metric.getChildText("name");
                String metrictype = metric.getChildText("type");
                String metricID = metric.getAttributeValue("id");
                String metricLevel = metric.getAttributeValue("level");
                String metricWeight = metric.getChildText("weight");
                Element metricEvaluation = metric.getChild("evaluation");
                if (metricEvaluation == null){
                    metricEvaluation = new Element("evaluation");
                    metricEvaluation.setAttribute("value", "");
                    metric.addContent(metricEvaluation);
                }


                //TODO: put the appropriate name
                String name = metricname;

                float weight = 1;
                if (!metricWeight.trim().equals("")){
                    weight = Float.parseFloat(metricWeight);
                }

                String value = metricEvaluation.getAttributeValue("value").trim();
                if (value.equals("")){
                    value = Double.toString(evaluateMetric(metric));
                    metricEvaluation.setAttribute("value", value);
                }

                //String value = Double.toString(evaluateMetric(metric));
                //Gathering data
                evaluationChart.append(odataName+".setValue("+oIndex+", 0, '"+name+"'); \n");
                evaluationChart.append(odataName+".setValue("+oIndex+", 1, "+value+"); \n");

                //evaluationChart.append("<set name='" + name + "' value='"+ value +"' color='008ED6' hoverText='"+ name +" (weight " + Float.toString(weight) + ")' />");
                oIndex++;
            }

            //General/Overall evaluation
            Element planEvaluation = xmlPlan.getChild("evaluation");
            if (planEvaluation == null){
                planEvaluation = new Element("evaluation");
                planEvaluation.setAttribute("value", "");
                xmlPlan.addContent(planEvaluation);
            }

            String overallScore = planEvaluation.getAttributeValue("value").trim();
            if (overallScore.equals("")){
                double overallgrade = evaluatePlan(metrics);
                DecimalFormat overall = new DecimalFormat("0.00");
                overallScore = overall.format(overallgrade);
                planEvaluation.setAttribute("value", overallScore);
            }


            DecimalFormat overall = new DecimalFormat("0.00");
            evaluationChart.append(odataName+".setValue("+oIndex+", 0, 'Final'); \n");
            evaluationChart.append(odataName+".setValue("+oIndex+", 2, "+overallScore+"); \n");


            ochartName = "chartEvaluation";
            evaluationChart.append("var " + ochartName + " = new google.visualization.ColumnChart(document.getElementById('analysis-graph-" + ochartName + "'));\n");
            evaluationChart.append(ochartName + ".draw("+  odataName + ", {isStacked: true, max: 1, legend: 'none', width: 600, height: 240, is3D: true, title:'Initial Plan Evaluation'});\n\n");

            script.append(evaluationChart);


            //html content
            //StringBuilder htmlevaluation = new StringBuilder();

            htmlevaluation.append("		<h2><a name=\"evaluation\">Initial Plan Evaluation</a></h2> \n");
            htmlevaluation.append("		<p>The following plan evaluation is based on quality preferences described in itSIMPLE.</p> \n");

            htmlevaluation.append("		<table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\"> \n");
            htmlevaluation.append("			<tr> \n");
            htmlevaluation.append("				<td valign=\"top\" class=\"text\" align=\"center\"> \n");

            //htmlevaluation.append("                           <div id=\"time-graph-" + chartName + "\"></div> \n");
            htmlevaluation.append("                             <div id=\"analysis-graph-" + ochartName + "\"></div> \n");

            htmlevaluation.append("				</td> \n");
            htmlevaluation.append("			</tr> \n");
            htmlevaluation.append("           		<tr> \n");
            htmlevaluation.append("               		<td valign=\"top\" class=\"text\">  \n");
            htmlevaluation.append("               			<p><strong>Plan Quality: <a name=\"planquality\">"+overallScore+"</a></strong></p>  \n");
            htmlevaluation.append("               		</td> \n");
            htmlevaluation.append("          		</tr> \n");
            htmlevaluation.append("		</table> \n");
            htmlevaluation.append("		<div class=\"posted\"> \n");
            htmlevaluation.append("			<p>generated by <a href=\"#\">postDAM</a> on "+todayStr+"</p> \n");
            htmlevaluation.append("			<p class=\"comments\"><a href=\"#\">comments</a></p> \n");
            htmlevaluation.append("		</div> \n");

        }



        //finishing script (google)
        script.append("} \n");
        script.append("           </script>\n");




        //Plan Validation
        boolean isValid = true;
        boolean planChecked = false;
        String validityString = "";
        if (validity!= null){
            if (!validity.getAttributeValue("isValid").trim().equals("") && !validator.getAttributeValue("id").trim().equals("")){
               planChecked = true;
            }
            if(validity.getAttributeValue("isValid").trim().equals("false")){
                isValid = false;
            }
        }

        //Plan Validity
        if (plan.getChildren().size() > 0){
            if (planChecked){
                if (isValid){
                    validityString += "			<li><strong>. Plan validity: </strong><font color='green'>Valid plan</font></li> \n ";
                }else{
                    validityString += "			<li><strong>. Plan validity: </strong><font color='red'>Invalid plan</font> ("+validity.getText()+")</li> \n ";
                }
                if(!validator.getAttributeValue("id").trim().equals("")){
                    String validatorName = validator.getChildText("name") + " " + validator.getChildText("version");
                    String link = validator.getChildText("link");
                    if (!link.trim().equals("")){
                        validatorName += " (<a href='"+link+"' target='_blank'>"+link+"</a>)";
                    }
                    validityString += "			<li><strong>. Validated by: </strong>"+validatorName+"</li> \n ";
                }else{

                }
            }
            else{
                validityString += "			<li><strong>. Plan validity: </strong>not validated</li> \n ";
            }
        }else{
            validityString += "			<li><strong>. Plan validity: </strong>Empty plan</li> \n ";
        }




        //HTML
        //1. HEAD
        html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"> \n");
        html.append("<html xmlns=\"http://www.w3.org/1999/xhtml\"> \n");
        html.append("<head> \n");
        html.append("	<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" /> \n");
        html.append("	<title>postDAM - Plan Analysis</title> \n");
        html.append("	<meta name=\"keywords\" content=\"\" /> \n");
        html.append("	<meta name=\"description\" content=\"\" /> \n");
        html.append("	<link href=\"default.css\" rel=\"stylesheet\" type=\"text/css\" /> \n");
        html.append("	<script language=\"JavaScript\" src=\"JSClass/FusionCharts.js\"></script> \n");
        html.append("	<script type=\"text/javascript\" src=\"http://www.google.com/jsapi\"></script> \n");
        html.append(script);
        html.append("</head> \n");


        //2. BODY
        html.append("<body> \n");

        //top menu
        html.append("	<div id=\"header\"> \n");
        html.append("		<div id=\"logo\"> \n");
        html.append("			<h1><span><a href=\"#\">plan</a></span><a href=\"#\">Report</a></h1> \n");
        html.append("			<h2><a href=\"http://dlab.poli.usp.br\">by itSIMPLE</a></h2> \n");
        html.append("		</div> \n");
        html.append("		<div id=\"menu\"> \n");
        html.append("			<ul> \n");
        html.append("				<li class=\"first\"><a href=\"#\" accesskey=\"1\" title=\"\">Home</a></li> \n");
        html.append("				<li><a href=\"#metrics\" accesskey=\"2\" title=\"\">Metrics</a></li> \n");
        html.append("				<li><a href=\"#jointview\" accesskey=\"3\" title=\"\">Joint View</a></li> \n");
        html.append("				<li><a href=\"#evaluation\" accesskey=\"3\" title=\"\">Evaluation</a></li>  \n");
        html.append("				<li><a href=\"#about\" accesskey=\"4\" title=\"\">About</a></li> \n");
        html.append("			</ul> \n");
        html.append("		</div> \n");
        html.append("	</div> \n");
        html.append("	<div id=\"splash\"><a href=\"#\"><img src=\"images/img4.jpg\" alt=\"\" width=\"877\" height=\"140\" /></a></div> \n");


        //Content

        //2.1 Introduction

        html.append("	<div id=\"content\"> \n");
        
        StringBuilder introduction = new StringBuilder();
        introduction.append("		<div id=\"colOne\"> \n");
        introduction.append("		<h2>Introduction</h2> \n");
        introduction.append("		<p><strong>PlanReport</strong> is a plan analysis interface for helping designers investigate solutions provided by automated planners.<br> \n");
        introduction.append("		This report was generted based on the information that follows:</p> \n");
        introduction.append("		<ul> \n");
        introduction.append("			<li><strong>. Domain: </strong>"+domainName +"</li> \n");
        introduction.append("			<li><strong>. Planning problem: </strong>"+problemName+"</li> \n");
        introduction.append("			<li><strong>. Solution provided by: </strong>"+plannerName+"</li> \n");
        if (!validityString.trim().equals("")){
            introduction.append(validityString);
        }
        introduction.append("		<ul> <br>\n");
        introduction.append("		<p>Planner's info:</p> \n");
        introduction.append("		<ul> \n");
        introduction.append("			<li><strong>. Name: </strong>"+plannerName+"</li> \n");
        introduction.append("			<li><strong>. Version: </strong>"+planner.getChildText("version")+"</li> \n");
        introduction.append("			<li><strong>. Date: </strong>"+planner.getChildText("date")+"</li> \n");
        introduction.append("			<li><strong>. Institution: </strong>"+planner.getChildText("institution")+"</li> \n");
        introduction.append("			<li><strong>. Author(s): </strong>"+planner.getChildText("author")+"</li> \n");
        String link = planner.getChildText("link");
        if (!link.trim().equals("")){
            introduction.append("			<li><strong>. Website: </strong><a href='"+link+"' target='_blank'>"+link+"</a></li> \n");
        }
        else{
            introduction.append("			<li><strong>. Website: </strong>not available</li> \n");
        }
        introduction.append("			<li><strong>. Description: </strong>"+planner.getChildText("description") +"</li> \n");
        introduction.append("		<ul> <br> \n");
        introduction.append("		<p>Planner's statistics:</p> \n");
        introduction.append("		<ul> \n");
        introduction.append("			<li><strong>. Plan size: </strong>"+Integer.toString(plan.getChildren().size())+"</li> \n");
        introduction.append("			<li><strong>. Time seen by itSIMPLE: </strong>"+statistics.getChildText("toolTime")+" seconds</li> \n");
        introduction.append("			<li><strong>. Time: </strong>"+statistics.getChildText("time")+"</li> \n");
        introduction.append("			<li><strong>. Parsing time: </strong>"+statistics.getChildText("parsingTime")+"</li> \n");
        introduction.append("			<li><strong>. Number of actions: </strong>"+ xmlPlan.getChild("plan").getChildren().size()+"</li> \n");
        introduction.append("			<li><strong>. Makespan: </strong>"+statistics.getChildText("makeSpan")+"</li> \n");
        introduction.append("			<li><strong>. Metric value (given by the planner): </strong>"+statistics.getChildText("metricValue")+"</li> \n");
        introduction.append("			<li><strong>. Planning Technique: </strong>"+statistics.getChildText("planningTechnique")+"</li> \n");
        introduction.append("			<li><strong>. Additional: </strong>"+statistics.getChildText("additional")+"</li> \n");
        introduction.append("		<ul> <br>\n");

        html.append(introduction);



        int chartIndex = 0;



        //2.2 Gantt chart

        //find out total duratrion
        float totalDuration = 0;
        List<Element> theactions = plan.getChildren();
        for (Iterator<Element> it = theactions.iterator(); it.hasNext();) {
            Element eaction = it.next();
            String starttime = eaction.getChildText("startTime");
            String duration = eaction.getChildText("duration");
            float endtime = Float.parseFloat(starttime) + Float.parseFloat(duration);
            if (totalDuration < endtime){totalDuration = endtime;}
        }



        int totalDurationInt = (int) totalDuration;

        Calendar now = Calendar.getInstance();
        now.setTime(todayDate);
        now.add(Calendar.DAY_OF_MONTH, totalDurationInt);
        Date finalDay = now.getTime();
        String finalDayStr = sdf.format(finalDay);
        //System.out.println(finalDay);
        //System.out.println(totalDuration);

        String dataXML = "<graph dateFormat='mm/dd/yyyy'>";
        dataXML += "<categories>";
        dataXML += "<category start='"+ todayStr + "' end='"+finalDayStr+"' name='Plan' />";
        dataXML += "</categories>";
        dataXML += "<categories>";
        dataXML += "<category start='"+ todayStr + "' end='"+finalDayStr+"' name='period' />" ;
        dataXML += "</categories>";

        String dataXMLprocesses = "<processes fontSize='9' isBold='1' align='left' headerText='Actions' headerFontSize='16' headerVAlign='bottom' headerAlign='right'>";
        String dataXMLtasks = "<tasks showname='1'>";

        int actionid = 1;

        for (Iterator<Element> it = theactions.iterator(); it.hasNext();) {
            Element eaction = it.next();

            String actionName = eaction.getAttributeValue("id");
            //params = eaction.findall("parameters/parameter")
            for (Iterator<Element> it1 = eaction.getChild("parameters").getChildren().iterator(); it1.hasNext();) {
                Element par = it1.next();
                String parameterName = par.getAttributeValue("id");
                actionName += " " + parameterName;
            }
            String starttime = eaction.getChildText("startTime");
            String duration = eaction.getChildText("duration");

            dataXMLprocesses += "<process name='"+ actionName +"' id='"+Integer.toString(actionid)+"'/>";

            float fstarttime = Float.parseFloat(starttime);
            float fduration = Float.parseFloat(duration)-1;

            int fstarttimeInt = (int) fstarttime;
            int fdurationInt = (int) fduration;

            //get start time
            Calendar nowS = Calendar.getInstance();
            nowS.setTime(todayDate);
            nowS.add(Calendar.DAY_OF_MONTH, fstarttimeInt);
            Date stday = nowS.getTime();
            String startDay = sdf.format(stday);
            //get end time
            Calendar nowE = Calendar.getInstance();
            nowE.setTime(stday);
            nowE.add(Calendar.DAY_OF_MONTH, fdurationInt);
            Date enday = nowE.getTime();
            String endDay = sdf.format(enday);
            //System.out.println(startDay);
            //System.out.println(endDay);

            dataXMLtasks += "<task start='"+startDay+"' end='"+endDay+"' processId='"+Integer.toString(actionid)+"' name='' showName='1' />";

            actionid += 1;
        }

        dataXMLprocesses += "</processes>";
        dataXMLtasks += "</tasks>";

        dataXML += dataXMLprocesses;
        dataXML += dataXMLtasks;
        dataXML += "</graph>";


        int gheight = plan.getChildren().size()*20 + 50;

        StringBuilder gantt = new StringBuilder();

        gantt.append("		<h2><a name=\"gantt\">Gantt chart</a></h2> \n");
        gantt.append("		<p>The following Gantt chart ilustrates the plan given by the planner.\n");
        gantt.append("		<table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\"> \n");
        gantt.append("			<tr> \n");
        gantt.append("				<td valign=\"top\" class=\"text\" align=\"center\"> \n");
        gantt.append("					<div id=\"chartdiv" + Integer.toString(chartIndex) + "\" align=\"center\">Gantt Chart</div> \n");
        gantt.append("					<script type=\"text/javascript\"> \n");
        gantt.append("						var chart"+Integer.toString(chartIndex)+" = new FusionCharts(\"charts/FCF_Gantt.swf\", \"ChartId\", \"700\", \""+Integer.toString(gheight)+"\"); \n");
        gantt.append("						chart"+Integer.toString(chartIndex)+".setDataXML(\"" + dataXML + "\"); \n");
        gantt.append("						chart"+Integer.toString(chartIndex)+".render(\"chartdiv" + Integer.toString(chartIndex) + "\"); \n");
        gantt.append("					</script> \n");
        gantt.append("				</td> \n");
        gantt.append("			</tr> \n");
        gantt.append("		</table> \n");
        gantt.append("		<div class=\"posted\"> \n");
        gantt.append("			<p>generated by <a href=\"#\">postDAM</a> on " + todayStr + "</p> \n");
        gantt.append("			<p class=\"comments\"><a href=\"#\">comments</a></p> \n");
        gantt.append("		</div> \n");

        html.append(gantt);



        //METRICS

        //Metrics charts



        chartIndex += 1;

        //If there are metrics build the charts
        if (metrics!=null && metrics.getChildren().size() > 0){


            html.append(htmlthemetrics);

            html.append(htmljointview);

            html.append(htmlevaluation);


            /*

            StringBuilder htmlmetrics = new StringBuilder();

            //2.3 Metrics
            htmlmetrics.append("		<h2><a name=\"metrics\">Metrics</a></h2> \n");
            htmlmetrics.append("		<p>The following metrics were chosen to be analyzed.\n");


            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element metric = it.next();

                String metricname = metric.getChildText("name");
                String metrictype = metric.getChildText("type");
                String metricID = metric.getAttributeValue("id");
                String metricLevel = metric.getAttributeValue("level");

                //TODO: select the name of the chart. Variable, expression, pslPlan.
                String chartTitle = metricname;


                htmlmetrics.append("		<h3><strong>.: " + metricname + "</strong></h3> \n");
                htmlmetrics.append("       	<p>The following chart presents the metric <strong>" + chartTitle + "</strong></p> \n");


                //TODO:Lets suppose all of them are numeric/integer

                //Preparing Fusion Chart Instance
                dataXML = "<graph caption='Attribute " + chartTitle + "' subcaption='' ";
                dataXML += "xAxisName='Steps' yAxisMinValue='0' yAxisName='Value' ";
                dataXML += "decimalPrecision='0' formatNumberScale='0' showNames='1' ";
                dataXML += "showValues='0' showAlternateHGridColor='1' AlternateHGridColor='ff5904' ";
                dataXML += "divLineColor='ff5904' divLineAlpha='20' alternateHGridAlpha='5' >";


                //additional info: minimum, maximum, avetage (when applied)
                Element dataset = metric.getChild("dataset");
                Element firstValue = (Element)dataset.getChildren().get(0);
                Element lastValue = (Element)dataset.getChildren().get(dataset.getChildren().size()-1);

                float minimum = Float.parseFloat(firstValue.getAttributeValue("value")); //get first record, value y (0 -> x, 1 ->, 2 -> comments)
                float maximum = Float.parseFloat(lastValue.getAttributeValue("value"));

                //read each value (X,Y) and creat each set of the graph
                for (Iterator<Element> it1 = dataset.getChildren().iterator(); it1.hasNext();) {
                    Element set = it1.next();
                    String x = set.getAttributeValue("name");
                    String y = set.getAttributeValue("value");

                    dataXML += "<set name='" + x + "' value='" + y + "' hoverText='" + x + "'/>";

                    float yValue = Float.parseFloat(y.trim());
                    //additional info
                    if (yValue < minimum){
                        minimum = yValue;
                    }
                    if (yValue > maximum){
                        maximum = yValue;
                    }

                    //TODO: extend the X Axis by one unit for convinience when viewing the last points

                }



                dataXML +="</graph>";

                //Create the html chart component with the dataXML
                htmlmetrics.append("		<table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\"> \n");
                htmlmetrics.append("			<tr> \n");
                htmlmetrics.append("				<td valign=\"top\" class=\"text\" align=\"center\"> \n");
                htmlmetrics.append("					<div id=\"chartdiv" + Integer.toString(chartIndex) + "\" align=\"center\">" + chartTitle + "</div> \n");
                htmlmetrics.append("					<script type=\"text/javascript\"> \n");
                htmlmetrics.append("						var chart"+Integer.toString(chartIndex)+" = new FusionCharts(\"charts/FCF_Line.swf\", \"ChartId\", \"700\", \"360\"); \n");
                htmlmetrics.append("						chart"+Integer.toString(chartIndex)+".setDataXML(\"" + dataXML + "\"); \n");
                htmlmetrics.append("						chart"+Integer.toString(chartIndex)+".render(\"chartdiv" + Integer.toString(chartIndex) + "\"); \n");
                htmlmetrics.append("                    </script> \n");
                htmlmetrics.append("				</td> \n");
                htmlmetrics.append("			</tr> \n");
                htmlmetrics.append("			<tr> \n");
                htmlmetrics.append("				<td valign=\"top\" class=\"text\"> \n");
                htmlmetrics.append("					<p><strong>Minimum:</strong> "+ Float.toString(minimum)+"<br> \n");
                htmlmetrics.append("					<strong>Maximum:</strong> "+ Float.toString(maximum)+"<br> </p>\n");
                htmlmetrics.append("				</td> \n");
                htmlmetrics.append("			</tr> \n");
                htmlmetrics.append("		</table> \n");
                htmlmetrics.append("		<div class=\"posted\"> \n");
                htmlmetrics.append("			<p>generated by <a href=\"#\">postDAM</a> on "+todayStr+"</p> \n");
                htmlmetrics.append("			<p class=\"comments\"><a href=\"#\">comments</a></p> \n");
                htmlmetrics.append("		</div> \n");

                //Numeric/Integer Attributes type ends here

                chartIndex += 1;


            }

            html.append(htmlmetrics);



            //2.4 A joint view of all metrics

            htmljointview = new StringBuilder();
            htmljointview.append("		<h2><a name=\"jointview\">Joint View</a></h2> \n");
            htmljointview.append("		<p>The following chart presents a combination of all metric charts.</p> \n");


            dataXML = "<graph caption='Joint view of metrics' xAxisName='Steps' yAxisName='Value' decimalPrecision='2' ";
            dataXML += "formatNumberScale='0' showValues='0'>";

            //Get the x values
            dataXML += "<categories>";
            //get the first metric as a base
            Element fistMetric = (Element)metrics.getChildren().get(0);
            Element firstDataset = fistMetric.getChild("dataset");
            for (Iterator<Element> it = firstDataset.getChildren().iterator(); it.hasNext();) {
                Element set = it.next();
                String x = set.getAttributeValue("name");
                dataXML += "<category name='" + x + "' />";

            }
            dataXML += "</categories>";


            //get data set
            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element metric = it.next();

                String metricname = metric.getChildText("name");
                String metrictype = metric.getChildText("type");
                String metricID = metric.getAttributeValue("id");
                String metricLevel = metric.getAttributeValue("level");

                //TODO: put the appropriate name
                String chartTitle = metricname;

                String color = randomColor();

                String dataset =  "<dataset seriesName='"+chartTitle+"' color='"+color+"' anchorBorderColor='"+color+"' anchorBgColor='"+color+"' >";


                //TODO: need to check the attr type in roder to choose the correct 2d Graph
                // 	Lets suppose all of them are numeric/integer

                //read each value (X,Y) and creat each set of the graph
                for (Iterator<Element> it1 = metric.getChild("dataset").getChildren().iterator(); it1.hasNext();) {
                    Element set = it1.next();
                    String y = set.getAttributeValue("value");
                    dataset += "<set value='" + y + "' />";

                    //TODO: extend the X Axis by one unit for convinience when viewing the last points

                }
                dataset += "</dataset>";

                //TODO: Numeric/Integer Attributes type ends here

                dataXML += dataset;
            }

            dataXML += "</graph>";

            //Create the html for joint view with the dataXML
            htmljointview.append("		<table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\"> \n");
            htmljointview.append("			<tr> \n");
            htmljointview.append("				<td valign=\"top\" class=\"text\" align=\"center\"> \n");
            htmljointview.append("					<div id=\"chartdiv" + Integer.toString(chartIndex) + "\" align=\"center\">Joint view</div> \n");
            htmljointview.append("					<script type=\"text/javascript\"> \n");
            htmljointview.append("						var chart"+Integer.toString(chartIndex)+" = new FusionCharts(\"charts/FCF_MSLine.swf\", \"ChartId\", \"700\", \"360\"); \n");
            htmljointview.append("						chart"+Integer.toString(chartIndex)+".setDataXML(\"" + dataXML + "\"); \n");
            htmljointview.append("						chart"+Integer.toString(chartIndex)+".render(\"chartdiv" + Integer.toString(chartIndex) + "\"); \n");
            htmljointview.append("					</script> \n");
            htmljointview.append("				</td> \n");
            htmljointview.append("			</tr> \n");
            htmljointview.append("		</table> \n");
            htmljointview.append("		<div class=\"posted\"> \n");
            htmljointview.append("			<p>generated by <a href=\"#\">postDAM</a> on "+ todayStr + "</p> \n");
            htmljointview.append("			<p class=\"comments\"><a href=\"#\">comments</a></p> \n");
            htmljointview.append("		</div> \n");


            html.append(htmljointview);

            chartIndex += 1;




            //2.5 Plan Evaluation

            htmlevaluation = new StringBuilder();
            htmlevaluation.append("		<h2><a name=\"evaluation\">Initial Plan Evaluation</a></h2> \n");
            htmlevaluation.append("		<p>The following plan evaluation is based on quality preferences described in itSIMPLE.</p> \n");


            //Gathering the final values
            dataXML = "<graph caption='Initial PlanEvaluation' xAxisName='Metrics' yAxisName='Preference Grades' decimalPrecision='2' ";
            dataXML += "formatNumberScale='0'>";

            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element metric = it.next();

                String metricname = metric.getChildText("name");
                String metrictype = metric.getChildText("type");
                String metricID = metric.getAttributeValue("id");
                String metricLevel = metric.getAttributeValue("level");
                String metricWeight = metric.getChildText("weight");

                //TODO: put the appropriate name
                String name = metricname;


                float weight = 1;
                if (!metricWeight.trim().equals("")){
                    weight = Float.parseFloat(metricWeight);
                }

                String value = Double.toString(evaluateMetric(metric));

                dataXML += "<set name='" + name + "' value='"+ value +"' color='008ED6' hoverText='"+ name +" (weight " + Float.toString(weight) + ")' />";
            }




            //2.6 Overall evaluation (plan grade)
            double overallgrade = evaluatePlan(metrics);

            DecimalFormat overall = new DecimalFormat("0.00");

            dataXML += "<set name='Final' value='"+ Double.toString(overallgrade) +"' color='9D080D' />";
            dataXML += "</graph>";

            //Create the html evaluation chart component with the dataXML
            htmlevaluation.append("		<table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\"> \n");
            htmlevaluation.append("			<tr> \n");
            htmlevaluation.append("				<td valign=\"top\" class=\"text\" align=\"center\"> \n");
            htmlevaluation.append("					<div id=\"chartdiv" + Integer.toString(chartIndex) + "\" align=\"center\">Initial Plan Evaluation</div> \n");
            htmlevaluation.append("					<script type=\"text/javascript\"> \n");
            htmlevaluation.append("						var chart"+Integer.toString(chartIndex)+" = new FusionCharts(\"charts/FCF_Column3D.swf\", \"ChartId\", \"700\", \"360\"); \n");
            htmlevaluation.append("						chart"+Integer.toString(chartIndex)+".setDataXML(\"" + dataXML + "\"); \n");
            htmlevaluation.append("						chart"+Integer.toString(chartIndex)+".render(\"chartdiv" + Integer.toString(chartIndex) + "\"); \n");
            htmlevaluation.append("					</script> \n");
            htmlevaluation.append("				</td> \n");
            htmlevaluation.append("			</tr> \n");
            htmlevaluation.append("           		<tr> \n");
            htmlevaluation.append("               		<td valign=\"top\" class=\"text\">  \n");
            htmlevaluation.append("               			<p><strong>Plan Quality: <a name=\"planquality\">"+overall.format(overallgrade)+"</a></strong></p>  \n");
            htmlevaluation.append("               		</td> \n");
            htmlevaluation.append("          		</tr> \n");
            htmlevaluation.append("		</table> \n");
            htmlevaluation.append("		<div class=\"posted\"> \n");
            htmlevaluation.append("			<p>generated by <a href=\"#\">postDAM</a> on "+todayStr+"</p> \n");
            htmlevaluation.append("			<p class=\"comments\"><a href=\"#\">comments</a></p> \n");
            htmlevaluation.append("		</div> \n");

            html.append(htmlevaluation);

            */


        }
        //If no metrics were specified
        else{
            StringBuilder nometrics = new StringBuilder();
            nometrics.append("		<h2><a name=\"metrics\">Metrics</a></h2> \n");
            nometrics.append("		<p>No metrics were used.\n");
            nometrics.append("		<h2><a name=\"jointview\">Joint View</a></h2> \n");
            nometrics.append("		<p>No metrics were used.</p> \n");
            nometrics.append("		<h2><a name=\"evaluation\">Initial Plan Evaluation</a></h2> \n");
            nometrics.append("		<p>No metrics were used.</p> \n");
            html.append(nometrics);

        }

       



        //2.7 second column
        html.append("	</div> \n");
        html.append("	<div id=\"colTwo\"> \n"); //Second column
        html.append("		<h3>Links</h3> \n");
        html.append("		<ul> \n");
        html.append("			<li><a href=\"#metrics\">Metrics</a></li> \n");
        html.append("			<li><a href=\"#jointview\">Joint View</a></li> \n");
        html.append("			<li><a href=\"#evaluation\">Plan Evaluation</a></li> \n");
        html.append("		</ul> \n");
        html.append("	</div> \n");
        html.append("	<div style=\"clear: both;\">&nbsp;</div> \n");
        html.append("</div> \n");


        //2.8 footer
        html.append("<div id=\"footer\"> \n");
        html.append("	<p>(c) 2010 itSIMPLE. Design by <a href=\"http://dlab.poli.usp.br/\">itSIMPLE team</a>.</p> \n");
        html.append("</div> \n");

        html.append(" </body> \n");
        html.append("</html>");


        return html.toString();
    }





    //TODO: How about including google charts apis as images ?
    /**
     * This method creates a HTML version of the information contained in the xmlPlan
     * @param xmlPlan
     * @return a html string containing a simple plan report (basic info). In fact,itSIMPLE class also has
     * such function (is is duplicated, use itSIMPLE's one) .
     */
    public static String generateHTMLSinglePlanReport(Element xmlPlan){


    	/*
        // get the date
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = new Date();
        String dateTime = dateFormat.format(date);
    	*/

        String dateTime = xmlPlan.getChildText("datetime");
        // head

        String info = "<html> \n";
        info += "<html>\n";
        info += "<head>\n";
        info += "<title>Plan Report</title>\n";
        info +="<style type=\"text/css\">\n";
        //info +="th{padding:0 2em;}\n";
        info +="</style>\n";
        info += "</head>\n";
        info += "<body>\n";
        info += "<h2>Plan Report</h2>\n";

        info += "<TABLE width='100%' BORDER='0' align='center'>"+
                                "<TR><TD bgcolor='333399'><font size=4 face=arial color='FFFFFF'>" +
                                "<b>Introduction</b></font></TD></TR>";

        Element projectName = xmlPlan.getChild("project");
        Element domainName = xmlPlan.getChild("domain");
        Element problemName = xmlPlan.getChild("problem");
        Element planvalidity = xmlPlan.getChild("validity");
        Element validator = xmlPlan.getChild("validator");

        String toolMessage = xmlPlan.getChild("toolInformation").getChildText("message");

        // project, domain and problem
        if(domainName != null && problemName != null){
                String projectNameStr = "";
                if (projectName!=null){
                    projectNameStr = projectName.getText();
                }

                info += "<TR><TD><font size=3 face=arial><b>Project: </b>"+projectNameStr+
                                "</font></TD></TR>"+
                                "<TR><TD><font size=3 face=arial><b>Domain: </b>"+ domainName.getText()+
                                "</font></TD></TR>" +
                                "<TR><TD><font size=3 face=arial><b>Problem: </b>"+ problemName.getText()+
                                "</font></TD></TR>" +
                                "<TR><TD><font size=3 face=arial><b>Date/Time: </b>"+ dateTime+
                                "</font></TD></TR>";
                if (planvalidity != null && validator != null){
                    String validityString = "";
                    String validatorname = validator.getChildText("name") + " " + validator.getChildText("version");
                    if (!validator.getAttributeValue("id").trim().equals("")){
                        String link = validator.getChildText("link");
                        if (!link.trim().equals("")){
                            validatorname += ": "+link;
                        }
                    }
                    
                    String isvalid = planvalidity.getAttributeValue("isValid");
                    if (isvalid.equals("true")){
                        validityString = "<font color='green'><strong>Valid plan.</strong></font> (validated by "+validatorname+")";
                    }
                    else if (isvalid.equals("false")){
                        validityString = "<font color='red'><strong>Invalid plan. "+planvalidity.getText()+".</strong></font> (validated by "+validatorname+")";

                    }
                    else{
                       validityString = "<font color='blue'><strong>Unknown. Plan not validated. "+planvalidity.getText()+"</strong>";
                    }
                    
                    //check if this is a empty plan
                    if (xmlPlan.getChild("plan").getChildren().size() < 1){
                        validityString = "<font color='red'><strong>Empty plan. Goal not satisfied.</strong>";
                    }
                     info += "<TR><TD><font size=3 face=arial><b>Plan validity: </b>"+ validityString+
                                "</font></TD></TR>";

                }
        }

        info += "<TR><TD bgcolor='FFFFFF'><font size=3 face=arial><b>itSIMPLE message:<br></b>"+
                        toolMessage.replaceAll("\n", "<br>") +"<p></TD></TR>";

        // planner
        Element planner = xmlPlan.getChild("planner");
        Element settingsPlanner = null;
        try {
                XPath path = new JDOMXPath("planners/planner[@id='"+ planner.getAttributeValue("id") +"']");
                settingsPlanner = (Element)path.selectSingleNode(ItSIMPLE.getItPlanners());
        } catch (JaxenException e) {
                e.printStackTrace();
        }

        if(settingsPlanner != null){
                info += "<TR><TD bgcolor='gray'><font size=4 face=arial color='FFFFFF'><b>Planner</b></TD></TR>" +
                                "<TR><TD><font size=3 face=arial><b>Name: </b>"+ settingsPlanner.getChildText("name")+
                                "</font></TD></TR>"+
                                "<TR><TD><font size=3 face=arial><b>Version: </b>"+ settingsPlanner.getChildText("version")+
                                "</font></TD></TR>"+
                                "<TR><TD><font size=3 face=arial><b>Author(s): </b>"+ settingsPlanner.getChildText("author")+
                                "</font></TD></TR>"+
                                "<TR><TD><font size=3 face=arial><b>Institution(s): </b>"+ settingsPlanner.getChildText("institution")+
                                "</font></TD></TR>"+
                                "<TR><TD><font size=3 face=arial><b>Link: </b>"+ settingsPlanner.getChildText("link")+
                                "</font></TD></TR>"+
                                "<TR><TD><font size=3 face=arial><b>Description: </b>"+ settingsPlanner.getChildText("description")+
                                "</font><p></TD></TR>";
        }

        // statistics
        Element statistics = xmlPlan.getChild("statistics");
        info += "<TR><TD bgcolor='gray'><font size=4 face=arial color='FFFFFF'><b>Statistics</b>" +
                        "</TD></TR>"+
                        "<TR><TD><font size=3 face=arial><b>Tool total time: </b>"+ statistics.getChildText("toolTime")+
                        "</font></TD></TR>" +
                        "<TR><TD><font size=3 face=arial><b>Planner time: </b>"+ statistics.getChildText("time")+
                        "</font></TD></TR>" +
                        "<TR><TD><font size=3 face=arial><b>Parsing time: </b>"+ statistics.getChildText("parsingTime")+
                        "</font></TD></TR>" +
                        "<TR><TD><font size=3 face=arial><b>Number of actions: </b>"+  xmlPlan.getChild("plan").getChildren().size()+
                        "</font></TD></TR>" +
                        "<TR><TD><font size=3 face=arial><b>Makespan: </b>"+ statistics.getChildText("makeSpan")+
                        "</font></TD></TR>" +
                        "<TR><TD><font size=3 face=arial><b>Metric value: </b>"+ statistics.getChildText("metricValue")+
                        "</font></TD></TR>" +
                        "<TR><TD><font size=3 face=arial><b>Planning technique: </b>"+ statistics.getChildText("planningTechnique")+
                        "</font></TD></TR>" +
                        "<TR><TD><font size=3 face=arial><b>Additional: </b>"+ statistics.getChildText("additional").replaceAll("\n", "<br>")+
                        "</font><p></TD></TR>";


        // plan
        info += "<TR><TD bgcolor='gray'><font size=4 face=arial color='FFFFFF'><b>Plan</b></TD></TR>";


        List<?> actions = xmlPlan.getChild("plan").getChildren("action");
        if (actions.size() > 0) {
                for (Iterator<?> iter = actions.iterator(); iter.hasNext();) {
                        Element action = (Element) iter.next();
                        // build up the action string
                        // start time
                        String actionStr = action.getChildText("startTime") + ": ";

                        // action name
                        actionStr += "(" + action.getAttributeValue("id") + " ";

                        // action parameters
                        List<?> parameters = action.getChild("parameters")
                                        .getChildren("parameter");
                        for (Iterator<?> iterator = parameters.iterator(); iterator
                                        .hasNext();) {
                                Element parameter = (Element) iterator.next();
                                actionStr += parameter.getAttributeValue("id");
                                if (iterator.hasNext()) {
                                        actionStr += " ";
                                }
                        }
                        actionStr += ")";

                        // action duration
                        String duration = action.getChildText("duration");
                        if (!duration.equals("")) {
                                actionStr += " [" + duration + "]";
                        }

                        if(iter.hasNext()){
                                info += "<TR><TD><font size=3 face=arial>"+ actionStr +"</font></TD></TR>";
                        }
                        else{
                                info += "<TR><TD><font size=3 face=arial>"+ actionStr +"</font><p></TD></TR>";
                        }
                }
        }
        else{
                info += "<TR><TD><font size=3 face=arial>No plan found.</font><p></TD></TR>";
        }


        // planner console output
        if (planner!=null && planner.getChild("consoleOutput") != null){
        info += "<TR><TD bgcolor='gray'><font size=3 face=arial color='FFFFFF'>" +
                        "<b>Planner Console Output</b></TD></TR>"+
                        "<TR><TD><font size=4 face=courier>" +
                        planner.getChildText("consoleOutput").replaceAll("\n", "<br>")+"</font><p></TD></TR>";
        }


        info += "</TABLE>";

        info += "</body>\n";
        info += "</html>\n";


     	return info;
    }




    /**
     * This method generates a short html report for set of plans.
     * @param generalXml
     * @return
     */
    public static String generatePlannersComparisonReport(Element generalXml){
        StringBuilder html = new StringBuilder();
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("<title>Plan Report</title>\n");
        html.append("<style type=\"text/css\">\n");
        html.append("th{padding:0 2em;}");
        html.append("</style>");
        html.append("</head>\n");
        html.append("<body>\n");

        List<Element> projects = generalXml.getChildren("project");
        if (projects.size()==1){html.append("<h3>" + projects.get(0).getChildText("name") + "</h3>");}


        //main table with main information (Project | Domain | Problem | planner | Time (s) | Steps

        html.append("<table bgcolor=\"#EEEEEE\" cellpadding=\"5\" cellspacing=\"1\" width=\"100%\">\n");
        html.append("<tr style=\"background-color:#000066; color:#FFFFFF; height:35px;\" >\n");
        if (projects.size()>1){html.append("<th>Project</th>\n");}
        html.append("<th>Domain</th>\n");
        html.append("<th>Problem</th>\n");
        html.append("<th>Planner</th>\n");
        html.append("<th width=\"50px\">Time (s)</th>\n");
        html.append("<th width=\"50px\">Steps</th>\n");
        html.append("<th width=\"50px\">Validity</th>\n");
        html.append("</tr>\n");

        for(int p=0;p<projects.size();p++){
            List<Element> domains = projects.get(p).getChild("domains").getChildren("domain");
            for(int d=0;d<domains.size();d++){
                List<Element> problems = domains.get(d).getChild("problems").getChildren("problem");
                for(int pb=0;pb<problems.size();pb++){
                    List<Element> plans = problems.get(pb).getChild("plans").getChildren("xmlPlan");
                    for(int pl=0;pl<plans.size();pl++){
                         Element plan = (Element)plans.get(pl);

                         html.append("<tr>\n");
                         if (projects.size()>1){
                             if (pl==0){
                                html.append("<td bgcolor=\"#CCCCCC\"><b>" + projects.get(p).getChildText("name") + "</b></td>\n");
                                html.append("<td bgcolor=\"#DDDDDD\"><b>" + domains.get(d).getChildText("name") + "<b></td>\n");
                                html.append("<td bgcolor=\"#EEEEEE\">" + problems.get(pb).getChildText("name") + "</td>\n");
                             }
                             else{
                                  html.append("<td bgcolor=\"#FFFFFF\"></td>\n");
                                  html.append("<td bgcolor=\"#FFFFFF\"></td>\n");
                                  html.append("<td bgcolor=\"#FFFFFF\"></td>\n");
                             }
                         }
                         else{
                             if (pl==0){
                                html.append("<td bgcolor=\"#DDDDDD\"><b>" + domains.get(d).getChildText("name") + "<b></td>\n");
                                html.append("<td bgcolor=\"#EEEEEE\">" + problems.get(pb).getChildText("name") + "</td>\n");
                             }
                             else{
                                  html.append("<td bgcolor=\"#FFFFFF\"></td>\n");
                                  html.append("<td bgcolor=\"#FFFFFF\"></td>\n");
                             }

                         }
                         html.append("<td bgcolor=\"#FFFFFF\">" + plan.getChild("planner").getChildText("name") + " - " + plan.getChild("planner").getChildText("version") + "</td>\n");

                         int planLength = plan.getChild("plan").getChildren().size();
                         boolean isValid = true;
                         boolean planchecked = true;

                         //check the validity of the plan
                         Element validity = plan.getChild("validity");
                         if (validity != null){
                             
                             if(validity.getAttributeValue("isValid").equals("false")){
                                //planLength = 0;
                                isValid = false;
                                //XMLUtilities.printXML(validity);
                             }
                             else if(validity.getAttributeValue("isValid").trim().equals("")){
                                planchecked = false;
                             }
                            
                         }else{
                             planchecked = false;
                         }

                         if(planLength < 1){
                             String reason = "no plan found";
                             //check if there any reason to that
                             Element status = plan.getChild("statistics").getChild("forcedQuit");
                             if(!status.getText().trim().equals("")){
                                reason = status.getText().trim();
                             }
                             //Check if it was a invalid plan
                             //if(!isValid){
                             //    reason = "invalid plan";
                             //}
                             html.append("<td bgcolor=\"#FFFFFF\" align=\"center\">" + plan.getChild("statistics").getChildText("toolTime") + "</td>\n");
                             html.append("<td bgcolor=\"#FFFFFF\" align=\"center\"><em>0</em></td>\n");
                             html.append("<td bgcolor=\"#FFFFFF\" align=\"center\"><font color='red'><em>"+reason+"</em></font></td>\n");
                         }
                         else{
                             //Check if it was a invalid plan
                             html.append("<td bgcolor=\"#FFFFFF\" align=\"center\">" + plan.getChild("statistics").getChildText("toolTime") + "</td>\n");
                             html.append("<td bgcolor=\"#FFFFFF\" align=\"center\">" + plan.getChild("plan").getChildren().size() + "</td>\n");
                            String reason = "";
                            if(!isValid){//a validated plan classified as invalid by the validator
                                reason = "invalid plan";                               
                                html.append("<td bgcolor=\"#FFFFFF\" align=\"center\"><font color='red'>"+ reason+ "</font> </td>\n");
                            }
                            else if (!planchecked){//a plan that was not validated by the validator
                                reason = "not validated";
                                html.append("<td bgcolor=\"#FFFFFF\" align=\"center\"><font color='blue'>"+ reason+ "</font> </td>\n");
                            }
                            else if (isValid && planchecked){//a plan that was validated and classifyed as valid by the validator
                                reason = "valid";
                                html.append("<td bgcolor=\"#FFFFFF\" align=\"center\"><font color='green'>"+ reason+ "</font> </td>\n");
                            }
                            
                         }

                         html.append("</tr>\n");



                     }

                }

            }

        }
        html.append("</table>\n");

        html.append("<br><br><br> \n");


        
        //metrics tables

        /*   it can be uncomment in case we want to show it rightaway

        StringBuilder metricTable = new StringBuilder();
        for(int p=0;p<projects.size();p++){
            List<Element> domains = projects.get(p).getChild("domains").getChildren("domain");
            for(int d=0;d<domains.size();d++){
                Element eaDomain = domains.get(d);
                List<Element> problems = eaDomain.getChild("problems").getChildren("problem");
                for(int pb=0;pb<problems.size();pb++){
                    Element eaProblem = problems.get(pb);
                    
                    // for each problem show the metrics info (Problem | metrics | Time (s) | # actions | Cost/Award | Evaluation
                    Element metrics = eaProblem.getChild("metrics");

                    if (metrics !=null && metrics.getChildren().size() > 0){

                        
                        metricTable.append("<h3>"+eaDomain.getChildText("name")+" - "+eaProblem.getChildText("name")+"</h3> \n");
                        //metricTable.append("<table bgcolor=\"#EEEEEE\" cellpadding=\"5\" cellspacing=\"1\" width=\"100%\">\n");

                        metricTable.append("<table bgcolor=\"#EEEEEE\" cellpadding=\"5\" cellspacing=\"1\" width=\"100%\">\n");

                        //Header
                        metricTable.append(getHtmlMetricTableHeader(metrics));

                        //table body
                        List<Element> plans = eaProblem.getChild("plans").getChildren("xmlPlan");
                        for(int pl=0;pl<plans.size();pl++){
                            Element plan = (Element)plans.get(pl);
                            Element planMetrics = plan.getChild("metrics");
                            if (planMetrics != null && planMetrics.getChildren().size() > 0){
                                metricTable.append(getHtmlMetricTableRow(planMetrics, plan));
                            }
                        }

                        metricTable.append("</table>");
                        metricTable.append("<br><br>");

                       
                        //System.out.print(metricTable.toString());
                    }

                }

            }

        }
        String metricsSummary = metricTable.toString();
        if (!metricsSummary.trim().equals("")){

            html.append("<h3>Metrics Summary</h3> \n");
            html.append("<br> \n");
            html.append(metricTable);
        }

        */



        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }



    /**
     * This method generate a full html report that shows a table comparing all executed planners,
     * charts such as Time vs. Problems and Number of Action Vs. Problems. If metrics are defined in the
     * domain tables will be created showing each planner evaluation.
     * @param generalXml
     * @return
     */
    public static String generateFullPlannersComparisonReport(Element generalXml){

        /**TODO: for each project we need to show
         * Charts needed: time vs problem, number of actions vs problem, quality vs problem
         *   (quality must be represented by cost, and overall score), a column bar showing
         *   how many problems were solved by each planner
         * Information needed:
         *   - Basic information to go some where
         *      (1) how many problems were involved
         *      (2) how many planners were involved
         *      (3) What was the timeout (if specific were considered just notify the reader)
         *   - We can put at the same column bar the following criteria:
         *      . how many times each planner was better in speed
         *      . how many times each planner was better in number of actions
         *      . how many times each planner was better in quality
         *      . how many problems it solved
         *   - We must show as a result
         *      (1) best planner in solvability (and second)
         *      (2) best planner in speed (and second)
         *      (3) best planner in quality (and second)
         *      (5) largest problem solved by PPPP with xxx actions
         *
         * Inforation needed for the overall analysis:
         * (1) best planner in solvability
         * (2) best planner in speed
         * (3) best planner in quality
         */
        //


        JLabel status = ItSIMPLE.getInstance().getPlanSimStatusBar();
        status.setText("Status: Generating report... (0%)");
        int progressIndex = 0;



        StringBuilder html = new StringBuilder();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateTime = dateFormat.format(date);

        //HTML
        //1. HEAD
        html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"> \n");
        html.append("<html xmlns=\"http://www.w3.org/1999/xhtml\"> \n");
        html.append("<head> \n");
        html.append("	<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" /> \n");
        html.append("	<title>Planners Comparison Report</title> \n");
        html.append("	<link href=\"default.css\" rel=\"stylesheet\" type=\"text/css\" /> \n");
        html.append("	<script type=\"text/javascript\" src=\"http://www.google.com/jsapi\"></script> \n");
        html.append("</head> \n");

        //2. BODY
        html.append("<body> \n");

        html.append("	<div id=\"header\"> \n");
        html.append("		<div id=\"logo\"> \n");
        html.append("			<h1><span><a href=\"#\">plan</a></span><a href=\"#\"> Comparison Report</a></h1>\n");
        html.append("			<h2><a href=\"#\">By itSIMPLE</a></h2>  \n");
        html.append("		</div> \n");
        html.append("		<div id=\"menu\"> \n");
        html.append("			<ul> \n");
        html.append("				<li class=\"first\"><a href=\"#table\" accesskey=\"1\" title=\"\">Comparison Table</a></li> \n");
        html.append("				<li><a href=\"#graphs\" accesskey=\"2\" title=\"\">Comparison Graphs</a></li> \n");
        html.append("				<li><a href=\"#about\" accesskey=\"4\" title=\"\">About</a></li> \n");
        html.append("			</ul> \n");
        html.append("		</div> \n");
        html.append("	</div> \n");
        html.append("	<div id=\"splash\"><a href=\"#\"><img src=\"images/img4.jpg\" alt=\"\" width=\"877\" height=\"140\" /></a></div> \n");



        //2.1 Scripts


        //2.1.1 Main script (google tables and charts)
        StringBuilder script = new StringBuilder();

        script.append("           <script type='text/javascript'>\n");
        script.append("               google.load('visualization', '1', {packages:['table','linechart','columnchart']})\n");
        script.append("               google.setOnLoadCallback(buildComponents)\n\n");
        script.append("               function buildComponents() {\n");


        //2.1.2 Comparison Table
        StringBuilder comparisonTable = new StringBuilder();
        comparisonTable.append("                   var data = new google.visualization.DataTable();\n");
        comparisonTable.append("                   data.addColumn('string', 'Project');\n");
        comparisonTable.append("                   data.addColumn('string', 'Domain');\n");
        comparisonTable.append("                   data.addColumn('string', 'Problem');\n");
        comparisonTable.append("                   data.addColumn('string', 'Planner');\n");
        comparisonTable.append("                   data.addColumn('number', 'Time');\n");
        comparisonTable.append("                   data.addColumn('number', 'Steps');\n");
        comparisonTable.append("                   data.addColumn('string', 'Validity');\n");

        StringBuilder cells = new StringBuilder();
        int number_of_rows = 0;
        List<Element> projects = generalXml.getChildren("project");
        for(int p=0;p<projects.size();p++){
            int problemId = 0;
            List<Element> domains = projects.get(p).getChild("domains").getChildren("domain");
            for(int d=0;d<domains.size();d++){
                List<Element> problems = domains.get(d).getChild("problems").getChildren("problem");
                for(int pb=0;pb<problems.size();pb++){
                    List<Element> plans = problems.get(pb).getChild("plans").getChildren("xmlPlan");
                    for(int pl=0;pl<plans.size();pl++){
                         Element plan = (Element)plans.get(pl);
                         cells.append("data.setCell(" + number_of_rows + ",0,'" + projects.get(p).getChildText("name") + "');\n");
                         cells.append("data.setCell(" + number_of_rows + ",1,'" + domains.get(d).getChildText("name") + "');\n");
                         //cells.append("data.setCell(" + number_of_rows + ",2,'" + problems.get(pb).getChildText("name") + "(P"+pb+")');\n");
                         cells.append("data.setCell(" + number_of_rows + ",2,'" + problems.get(pb).getChildText("name") + "(P"+problemId+")');\n");
                         cells.append("data.setCell(" + number_of_rows + ",3,'" + plan.getChild("planner").getChildText("name") + " - " + plan.getChild("planner").getChildText("version") + "');\n");
                         String thetooltimeStr = plan.getChild("statistics").getChildText("toolTime");
                         if (thetooltimeStr.trim().equals("")){
                             thetooltimeStr = "0";
                         }
                         cells.append("data.setCell(" + number_of_rows + ",4," + thetooltimeStr + ");\n");
                         
                         cells.append("data.setCell(" + number_of_rows + ",5," + plan.getChild("plan").getChildren().size() + ");\n");

                         //if(plan.getChild("plan").getChildren().size() < 1) {
                         //   cells.append("data.setCell(" + number_of_rows + ",5,-1,' ');\n");
                         //}
                         //else{
                         //   cells.append("data.setCell(" + number_of_rows + ",5," + plan.getChild("plan").getChildren().size() + ");\n");
                         //}

                         Element validity = plan.getChild("validity");
                         //check the validity of the plan
                         if (validity != null){
                             if(plan.getChild("plan").getChildren().size() < 1) {
                                 cells.append("data.setCell(" + number_of_rows + ",6,'no plan found');\n");
                             }
                             else if (validity.getAttributeValue("isValid").equals("true")){
                                 cells.append("data.setCell(" + number_of_rows + ",6,'valid');\n");
                             }
                             else if (validity.getAttributeValue("isValid").equals("false")){
                                 cells.append("data.setCell(" + number_of_rows + ",6,'invalid');\n");
                             }
                             else if (validity.getAttributeValue("isValid").trim().equals("")){
                                 cells.append("data.setCell(" + number_of_rows + ",6,'not validated');\n");
                             }

                         }
                         else{
                             cells.append("data.setCell(" + number_of_rows + ",6,'not validated');\n");
                         }


                         number_of_rows++;
                     }
                    problemId++;
                }
            }
        }

        comparisonTable.append("data.addRows(" + number_of_rows + "); \n");
        comparisonTable.append(cells.toString());
        comparisonTable.append("               var table = new google.visualization.Table(document.getElementById('comparison-table'));\n");
        comparisonTable.append("               table.draw(data, {showRowNumber: true,width:'100%'});\n");
        //script.append("               drawChart();\n");
        //script.append("}\n");
        comparisonTable.append("\n");

        script.append(comparisonTable);




        //2.1.3 Comparison Charts
        StringBuilder comparisonGraphs = new StringBuilder();
        
        //html.append("function drawChart(){\n");
        int data = 0;
        StringBuilder graphs_div = new StringBuilder();

        /* Grouping graphs by project (project->domain->problems) */

        for(int p=0;p<projects.size();p++){
            Element project = projects.get(p);

            //Project title
            graphs_div.append("<h3>.: "+project.getChildText("name")+"</h3> \n");


            //get domains
            List<Element> domains = project.getChild("domains").getChildren("domain");

            //get the fist problem as a reference for creating the graph
            Element firstProblemAsReference = (Element)domains.get(0).getChild("problems").getChildren("problem").get(0);            
            List<Element> plans = firstProblemAsReference.getChild("plans").getChildren("xmlPlan");

            //get the amount of problems in the project
            List<?> totalProblems = null;
            try {
                    XPath path = new JDOMXPath("domains/domain/problems/problem");
                    totalProblems = path.selectNodes(project);
            } catch (JaxenException e) {
                    e.printStackTrace();
            }


            //DATA CHARTS

            //Chart: Number of actions (considering all planners involved)
            comparisonGraphs.append("var data" + data + "S= new google.visualization.DataTable();\n");
            comparisonGraphs.append("data" + data + "S.addColumn('string', 'Problems');\n");
            for(int pl=0;pl<plans.size();pl++){
                 Element plan = (Element)plans.get(pl);
                 comparisonGraphs.append("data" + data + "S.addColumn('number', '"+ plan.getChild("planner").getChildText("name") + " - " + plan.getChild("planner").getChildText("version") +"');\n");
            }
            comparisonGraphs.append("data" + data + "S.addRows("+totalProblems.size()+");\n");
            int probleId = 0;

            for(int d=0;d<domains.size();d++){

                List<Element> problems = domains.get(d).getChild("problems").getChildren("problem");
                for(int pb=0;pb<problems.size();pb++){
                   plans = problems.get(pb).getChild("plans").getChildren("xmlPlan");

                   //comparisonGraphs.append("data" + data + "S.setValue("+pb+",0,'P"+pb+"');\n");
                   comparisonGraphs.append("data" + data + "S.setValue("+probleId+",0,'P"+probleId+"');\n");
                   for(int pl=0;pl<plans.size();pl++){
                        Element plan = (Element)plans.get(pl);
                        //comparisonGraphs.append("data" + data + "S.setValue("+pb+","+(pl+1)+","+ plan.getChild("plan").getChildren().size() +");\n");
                        //comparisonGraphs.append("data" + data + "S.setValue("+probleId+","+(pl+1)+","+ plan.getChild("plan").getChildren().size() +");\n");

                        //check plan validity
                        boolean isValid = true;
                        Element validity = plan.getChild("validity");
                        if (validity != null){
                            if (validity.getAttributeValue("isValid").trim().equals("false")){
                                isValid = false;
                            }
                        }
                        if (isValid){
                            comparisonGraphs.append("data" + data + "S.setValue("+probleId+","+(pl+1)+","+ plan.getChild("plan").getChildren().size() +");\n");
                        }else{
                            comparisonGraphs.append("data" + data + "S.setValue("+probleId+","+(pl+1)+",0);\n");
                        }


                   }
                   probleId++;
                }

            }
            if (totalProblems.size()==1){
                comparisonGraphs.append("var chart" + data + "S = new google.visualization.ColumnChart(document.getElementById('comparison-graph-" + data + "S'));\n");
                comparisonGraphs.append("chart" + data + "S.draw(data" + data + "S, {width: 840, height: 300,is3D: true,title:'Number of Actions'});\n\n");
            }
            else{
                comparisonGraphs.append("var chart" + data + "S = new google.visualization.LineChart(document.getElementById('comparison-graph-" + data + "S'));\n");
                comparisonGraphs.append("chart" + data + "S.draw(data" + data + "S, {width: 840, height: 300, min: 0,title:'Number of Actions'});\n\n");
            }
            graphs_div.append("<div id=\"comparison-graph-" + data + "S\"></div> <br>\n");



            //Chart: Runtime(speed) (considering all planners involved)
            comparisonGraphs.append("var data" + data + "T= new google.visualization.DataTable();\n");
            comparisonGraphs.append("data" + data + "T.addColumn('string', 'Problems');\n");
            plans = firstProblemAsReference.getChild("plans").getChildren("xmlPlan");
            for(int pl=0;pl<plans.size();pl++){
                 Element plan = (Element)plans.get(pl);
                 comparisonGraphs.append("data" + data + "T.addColumn('number', '"+ plan.getChild("planner").getChildText("name") + " - " + plan.getChild("planner").getChildText("version") +"');\n");
            }
            comparisonGraphs.append("data" + data + "T.addRows("+totalProblems.size()+");\n");
            probleId = 0;
            for(int d=0;d<domains.size();d++){

                List<Element> problems = domains.get(d).getChild("problems").getChildren("problem");
                for(int pb=0;pb<problems.size();pb++){
                   plans = problems.get(pb).getChild("plans").getChildren("xmlPlan");
                   //comparisonGraphs.append("data" + data + "T.setValue("+pb+",0,'P"+pb+"');\n");
                   comparisonGraphs.append("data" + data + "T.setValue("+probleId+",0,'P"+probleId+"');\n");
                   for(int pl=0;pl<plans.size();pl++){
                        Element plan = (Element)plans.get(pl);
                        //comparisonGraphs.append("data" + data + "T.setValue("+pb+","+(pl+1)+","+ (plan.getChild("plan").getChildren().size()>0?plan.getChild("statistics").getChildText("toolTime"):0) +");\n");
                        //comparisonGraphs.append("data" + data + "T.setValue("+probleId+","+(pl+1)+","+ (plan.getChild("plan").getChildren().size()>0?plan.getChild("statistics").getChildText("toolTime"):0) +");\n");

                        //check plan validity
                        boolean isValid = true;
                        Element validity = plan.getChild("validity");
                        if (validity != null){
                            if (validity.getAttributeValue("isValid").trim().equals("false")){
                                isValid = false;
                            }
                        }
                        if (isValid && plan.getChild("plan").getChildren().size()>0){
                            if (!plan.getChild("statistics").getChildText("toolTime").trim().equals("")){
                                comparisonGraphs.append("data" + data + "T.setValue("+probleId+","+(pl+1)+","+ plan.getChild("statistics").getChildText("toolTime") +");\n");
                            }else{
                                comparisonGraphs.append("data" + data + "T.setValue("+probleId+","+(pl+1)+","+ (plan.getChild("plan").getChildren().size()>0?plan.getChild("statistics").getChildText("toolTime"):0) +");\n");
                            }
                        }

                   }
                   probleId++;
                }

            }
            if (totalProblems.size()==1){
               comparisonGraphs.append("var chart" + data + "T = new google.visualization.ColumnChart(document.getElementById('comparison-graph-" + data + "T'));\n");
               comparisonGraphs.append("chart" + data + "T.draw(data" + data + "T, {width: 840, height: 300, is3D: true,title:'Runtime (seconds) to solve problem'});\n\n");
            }
            else{
                comparisonGraphs.append("var chart" + data + "T = new google.visualization.LineChart(document.getElementById('comparison-graph-" + data + "T'));\n");
                comparisonGraphs.append("chart" + data + "T.draw(data" + data + "T, {width: 840, height: 300, min: 0,title:'Runtime (seconds) to solve problem'});\n\n");
            }


            graphs_div.append("<div id=\"comparison-graph-" + data + "T\"></div> <br>\n");




            //RESULTS CHARTS

            //Analysis data (time, plan length, cost, quality)
            Element plannersStatistics = new Element("plannerStatistics");

            //get the first problem as a reference and get the planners
            plans = firstProblemAsReference.getChild("plans").getChildren("xmlPlan");

            int index = 1;
            for (Iterator<Element> it = plans.iterator(); it.hasNext();) {
                Element plan = it.next();
                //prepare statistcs support nodes
                Element eaPrannerStat = (Element)ItSIMPLE.getCommonData().getChild("planAnalysis").getChild("plannerStatisticSupport").clone();
                eaPrannerStat.setAttribute("id", plan.getChild("planner").getAttributeValue("id"));
                eaPrannerStat.getChild("name").setText(plan.getChild("planner").getChildText("name") + " - " + plan.getChild("planner").getChildText("version"));

                //Add it to the projectsStaticts
                plannersStatistics.addContent(eaPrannerStat);
                index++;
            }

            //Gathing statistics
            for(int d=0;d<domains.size();d++){
                List<Element> problems = domains.get(d).getChild("problems").getChildren("problem");

                for(int pb=0;pb<problems.size();pb++){
                   plans = problems.get(pb).getChild("plans").getChildren("xmlPlan");
                   float bestruntime = -1;
                   int bestplanlength = -1;
                   double bestplancost = -1;
                   float bestevaluation = -1;

                   //Gather data for statistics in this problem
                   for(int pl=0;pl<plans.size();pl++){
                        Element plan = (Element)plans.get(pl);

                        Element theplannerstat = null;

                        //find the planner in the staticts
                        try {
                            XPath path = new JDOMXPath("plannerStatisticSupport[@id='"+ plan.getChild("planner").getAttributeValue("id") +"']");
                            theplannerstat = (Element)path.selectSingleNode(plannersStatistics);

                        } catch (JaxenException e1) {e1.printStackTrace();}

                        if (theplannerstat != null){//if we found it go on

                            
                            int plansize = plan.getChild("plan").getChildren().size();

                            //Check if this is a valid plan
                            boolean isValid = true;
                            boolean planChecked = true;

                            Element validity = plan.getChild("validity");
                            if (validity!=null){
                                if (plan.getChild("validity").getAttributeValue("isValid").equals("false")){
                                    isValid = false;
                                }
                                else if (plan.getChild("validity").getAttributeValue("isValid").trim().equals("")){
                                    planChecked = false;
                                }
                            }else{
                                planChecked = false;

                            }


                            if (plansize > 0 && isValid){


                                //General data

                                //Problem solved counter
                                int problemSolvedCounter = Integer.parseInt(theplannerstat.getChild("problemssolved").getAttributeValue("counter"));
                                problemSolvedCounter++;
                                theplannerstat.getChild("problemssolved").setAttribute("counter", Integer.toString(problemSolvedCounter));



                                //Best performance data

                                Element betterCriteria = theplannerstat.getChild("ntimesbetter");
                                //recording the values in the temp
                                String theruntime = plan.getChild("statistics").getChildText("toolTime").trim();
                                betterCriteria.getChild("runtime").setAttribute("temp", theruntime);
                                betterCriteria.getChild("planlength").setAttribute("temp", Integer.toString(plansize));
                                
                                double plancost = -1;
                                
                               
                                if (plan.getChild("metrics") != null && plan.getChild("metrics").getChildren().size()>0){
                                    
                                    Element theCurrentmetrics = plan.getChild("metrics");
                                    plancost = evaluateCostAward(theCurrentmetrics);
                                    betterCriteria.getChild("plancost").setAttribute("temp", Double.toString(plancost));
                                    
                                }


                                //Checking it the values are the best
                                //1.time
                                if (!theruntime.equals("")){
                                    float theTime = -1;
                                    try {
                                        theTime = Float.parseFloat(theruntime);
                                    } catch (Exception e) {
                                    }
                                    //if this model/project reached the best time then score it
                                    if (theTime < bestruntime || bestruntime == -1) {
                                        bestruntime = theTime;
                                    }
                                }
                                //2.plan length
                                if (plansize < bestplanlength || bestplanlength ==-1){
                                    bestplanlength = plansize;
                                }

                                //3. plan cost
                                if (plancost > -1){
                                    if (plancost < bestplancost || bestplancost ==-1){
                                     bestplancost = plancost;
                                    }
                                }



                            }
                            else if(plan.getChild("statistics").getChildText("forcedQuit").trim().equals("timeout")){

                                //Timeout counter
                                int timeoutsreachedCounter = Integer.parseInt(theplannerstat.getChild("timeoutsreached").getAttributeValue("counter"));
                                timeoutsreachedCounter++;
                                theplannerstat.getChild("timeoutsreached").setAttribute("counter", Integer.toString(timeoutsreachedCounter));
                            }
                            else if(plan.getChild("statistics").getChildText("forcedQuit").trim().equals("skipped")){
                                //no solution counter
                                int skippedCounter = Integer.parseInt(theplannerstat.getChild("skipped").getAttributeValue("counter"));
                                skippedCounter++;
                                theplannerstat.getChild("skipped").setAttribute("counter", Integer.toString(skippedCounter));

                            }
                            else if(!isValid){
                                //no solution counter
                                int invalidsolutionCounter = Integer.parseInt(theplannerstat.getChild("invalidsolution").getAttributeValue("counter"));
                                invalidsolutionCounter++;
                                theplannerstat.getChild("invalidsolution").setAttribute("counter", Integer.toString(invalidsolutionCounter));

                            }
                            else{
                                //no solution counter
                                int nosolutionfoundCounter = Integer.parseInt(theplannerstat.getChild("nosolutionfound").getAttributeValue("counter"));
                                nosolutionfoundCounter++;
                                theplannerstat.getChild("nosolutionfound").setAttribute("counter", Integer.toString(nosolutionfoundCounter));
                            }

                        }




                   }


                   //Identify best performance

                   //System.out.println(bestruntime);
                   //System.out.println(bestplanlength);
                   //System.out.println(bestplancost);
                   //System.out.println();

                   for (Iterator<Element> it = plannersStatistics.getChildren().iterator(); it.hasNext();) {
                       Element stact = it.next();

                        Element eaStat = stact.getChild("ntimesbetter");

                        //1.time
                        Element eaTimeStat = eaStat.getChild("runtime");
                        String timeStr = eaTimeStat.getAttributeValue("temp");
                        if (!timeStr.trim().equals("")){
                            float theTime = -1;
                            try {
                                theTime = Float.parseFloat(timeStr);
                            } catch (Exception e) {
                            }
                            //if this planner reached the best time then score it
                            if (theTime == bestruntime) {
                                int timeCounter = Integer.parseInt(eaTimeStat.getAttributeValue("counter"));
                                timeCounter++;
                                eaTimeStat.setAttribute("counter", Integer.toString(timeCounter));
                            }


                        }
                        eaTimeStat.setAttribute("temp", "");

                        //2.plan length
                        Element eaPlanLengthStat = eaStat.getChild("planlength");
                        String planLengthStr = eaPlanLengthStat.getAttributeValue("temp");
                        if (!planLengthStr.trim().equals("")){
                            //if this planner reached the best planLength then score it
                            int theLength = Integer.parseInt(planLengthStr);
                            if (theLength == bestplanlength) {
                                int planLengthCounter = Integer.parseInt(eaPlanLengthStat.getAttributeValue("counter"));
                                planLengthCounter++;
                                eaPlanLengthStat.setAttribute("counter", Integer.toString(planLengthCounter));
                            }
                        }
                        eaPlanLengthStat.setAttribute("temp", "");


                        //3.cost
                        Element eaPlanCostStat = eaStat.getChild("plancost");
                        String planCostStr = eaPlanCostStat.getAttributeValue("temp");
                        if (!planCostStr.trim().equals("")){
                            //if this planner reached the best planLength then score it
                            double theCost = Double.parseDouble(planCostStr);
                            if (theCost == bestplancost) {
                                int planCostCounter = Integer.parseInt(eaPlanCostStat.getAttributeValue("counter"));
                                planCostCounter++;
                                eaPlanCostStat.setAttribute("counter", Integer.toString(planCostCounter));

                                //score 1 when reached the best found
                                double thescore = Double.parseDouble(stact.getChild("costqualityscore").getAttributeValue("value"));
                                thescore += 1;
                                stact.getChild("costqualityscore").setAttribute("value", Double.toString(thescore));

                            }else if (theCost > bestplancost) {
                                
                                //score (cost of best known plan / cost of generated plan)
                                double currentscore = bestplancost/theCost;
                                double thescore = Double.parseDouble(stact.getChild("costqualityscore").getAttributeValue("value"));
                                thescore += currentscore;
                                stact.getChild("costqualityscore").setAttribute("value", Double.toString(thescore));
                                        
                            }


                        }
                        eaPlanCostStat.setAttribute("temp", "");



//                        //2.evaluation
//                        Element eaEvaluationStat = eaStat.getChild("evaluation");
//                        String planEvaluationStr = eaEvaluationStat.getAttributeValue("temp");
//                        if (!planEvaluationStr.trim().equals("")){
//                            //if this model/project reached the best evaluation then score it
//                            double theEvaluation = Double.parseDouble(planEvaluationStr);
//                            if (theEvaluation == bestEvaluation) {
//                                int evaluationCounter = Integer.parseInt(eaEvaluationStat.getAttributeValue("counter"));
//                                evaluationCounter++;
//                                eaEvaluationStat.setAttribute("counter", Integer.toString(evaluationCounter));
//                            }
//                        }


                    }



                }

            }


            //Generate the summary charts and content (who was the winner in this project?)


            //XMLUtilities.printXML(plannersStatistics);

            //Solvability Results
            StringBuffer solvabilityData = new StringBuffer();
             //Chart: Problem solved, timeouts and nosolution
            String dataNameSolvability = "dataSolvability" + data;
            comparisonGraphs.append("var " + dataNameSolvability + "= new google.visualization.DataTable();\n");
            comparisonGraphs.append(dataNameSolvability + ".addColumn('string', 'Results');\n");
            int dindex = 1;
            for (Iterator<Element> it = plannersStatistics.getChildren().iterator(); it.hasNext();) {
                Element eaPlannerStat = it.next();
                comparisonGraphs.append(dataNameSolvability + ".addColumn('number', '"+eaPlannerStat.getChildText("name")+"');\n");


                solvabilityData.append(dataNameSolvability + ".setValue(0, "+dindex+", "+eaPlannerStat.getChild("problemssolved").getAttributeValue("counter")+");\n");

                solvabilityData.append(dataNameSolvability + ".setValue(1, "+dindex+", "+eaPlannerStat.getChild("timeoutsreached").getAttributeValue("counter")+");\n");

                solvabilityData.append(dataNameSolvability + ".setValue(2, "+dindex+", "+eaPlannerStat.getChild("nosolutionfound").getAttributeValue("counter")+");\n");

                solvabilityData.append(dataNameSolvability + ".setValue(3, "+dindex+", "+eaPlannerStat.getChild("invalidsolution").getAttributeValue("counter")+");\n");

                dindex++;
            }
            comparisonGraphs.append(dataNameSolvability + ".addRows(4);\n");
            comparisonGraphs.append(dataNameSolvability + ".setValue(0, 0, 'Problems solved');\n");
            comparisonGraphs.append(dataNameSolvability + ".setValue(1, 0, 'Timeouts');\n");
            comparisonGraphs.append(dataNameSolvability + ".setValue(2, 0, 'No-solution');\n");
            comparisonGraphs.append(dataNameSolvability + ".setValue(3, 0, 'Invalid-solution');\n");


            comparisonGraphs.append(solvabilityData);

            String chartNameSolvability = "chartSolvability"+ data;
            comparisonGraphs.append("var " + chartNameSolvability + " = new google.visualization.ColumnChart(document.getElementById('analysis-"+chartNameSolvability+"'));\n");
            comparisonGraphs.append(chartNameSolvability + ".draw(" + dataNameSolvability + ", {width: 750, height: 300, is3D: true, title:'Solvability Results'});\n\n");

            graphs_div.append("<div id=\"analysis-"+chartNameSolvability+"\"></div> <br>\n");


            
            //Best Performance Results
            StringBuffer bestData = new StringBuffer();
             //Chart: Problem solved, timeouts and nosolution
            String dataNameBest = "dataBest" + data;
            comparisonGraphs.append("var " + dataNameBest + "= new google.visualization.DataTable();\n");
            comparisonGraphs.append(dataNameBest + ".addColumn('string', 'Performance');\n");
            dindex = 1;
            for (Iterator<Element> it = plannersStatistics.getChildren().iterator(); it.hasNext();) {
                Element eaPlannerStat = it.next();
                comparisonGraphs.append(dataNameBest + ".addColumn('number', '"+eaPlannerStat.getChildText("name")+"');\n");


                Element ntimesbetter = eaPlannerStat.getChild("ntimesbetter");

                bestData.append(dataNameBest + ".setValue(0, "+dindex+", "+ntimesbetter.getChild("runtime").getAttributeValue("counter")+");\n");

                bestData.append(dataNameBest + ".setValue(1, "+dindex+", "+ntimesbetter.getChild("planlength").getAttributeValue("counter")+");\n");

                bestData.append(dataNameBest + ".setValue(2, "+dindex+", "+ntimesbetter.getChild("plancost").getAttributeValue("counter")+");\n");

                dindex++;
            }
            comparisonGraphs.append(dataNameBest + ".addRows(3);\n");
            //comparisonGraphs.append(dataNameBest + ".addRows(2);\n");
            comparisonGraphs.append(dataNameBest + ".setValue(0, 0, 'Best runtime');\n");
            comparisonGraphs.append(dataNameBest + ".setValue(1, 0, 'Best plan length');\n");
            comparisonGraphs.append(dataNameBest + ".setValue(2, 0, 'Best plan cost');\n");

            comparisonGraphs.append(bestData);

            String chartNameBest = "chartBest"+ data;
            comparisonGraphs.append("var " + chartNameBest + " = new google.visualization.ColumnChart(document.getElementById('analysis-"+chartNameBest+"'));\n");
            comparisonGraphs.append(chartNameBest + ".draw(" + dataNameBest + ", {width: 750, height: 300, is3D: true, title:'Best Performance Results (Number of times planners were best in each category)'});\n\n");


            graphs_div.append("<div id=\"analysis-"+chartNameBest+"\"></div> <br>\n");



            //Quality Results
            StringBuffer qualityData = new StringBuffer();
             //Chart: Problem solved, timeouts and nosolution
            String dataNameQuality = "dataQuality" + data;
            comparisonGraphs.append("var " + dataNameQuality + "= new google.visualization.DataTable();\n");
            comparisonGraphs.append(dataNameQuality + ".addColumn('string', 'Quality');\n");
            dindex = 1;
            for (Iterator<Element> it = plannersStatistics.getChildren().iterator(); it.hasNext();) {
                Element eaPlannerStat = it.next();
                comparisonGraphs.append(dataNameQuality + ".addColumn('number', '"+eaPlannerStat.getChildText("name")+"');\n");

                qualityData.append(dataNameQuality + ".setValue(0, "+dindex+", "+eaPlannerStat.getChild("costqualityscore").getAttributeValue("value")+");\n");


                dindex++;
            }
            //comparisonGraphs.append(dataNameQuality + ".addRows(3);\n");
            comparisonGraphs.append(dataNameQuality + ".addRows(1);\n");
            comparisonGraphs.append(dataNameQuality + ".setValue(0, 0, 'Quality score');\n");
            //comparisonGraphs.append(dataNameQuality + ".setValue(1, 0, 'Best plan length');\n");
            //comparisonGraphs.append(dataNameQuality + ".setValue(2, 0, 'Plan cost');\n");

            comparisonGraphs.append(qualityData);

            String chartNameQuality = "chartQuality"+ data;
            comparisonGraphs.append("var " + chartNameQuality + " = new google.visualization.ColumnChart(document.getElementById('analysis-"+chartNameQuality+"'));\n");
            comparisonGraphs.append(chartNameQuality + ".draw(" + dataNameQuality + ", {width: 750, height: 300, is3D: true, title:'Quality Score (planners get 0.00–1.00 points per solved task, depending on the plan cost)'});\n\n");


            graphs_div.append("<div id=\"analysis-"+chartNameQuality+"\"></div> <br>\n");
            graphs_div.append("<p> In order to calculate the quality score we do the following: score is 1.00 for optimal or best known solutions; " +
                    "otherwise score is (cost of best known plan) / (cost of generated plan). </p>");



            data++;


            // refresh the status bar
            int progressPercentage = (int)((double)++progressIndex/(double)projects.size() * 100);
            status.setText("Status: Generating report... ("+ progressPercentage +"%)");




        }
        comparisonGraphs.append("\n");

        script.append(comparisonGraphs);

        /*
         * Grouping graphs by domain (project->domain->problems). It is an alternative
         *
        for(int p=0;p<projects.size();p++){
            List<Element> domains = projects.get(p).getChild("domains").getChildren("domain");
            for(int d=0;d<domains.size();d++){

                graphs_div.append("<h3>"+domains.get(d).getChildText("name")+"</h3>");

                List<Element> problems = domains.get(d).getChild("problems").getChildren("problem");

                //Graph: Number of actions x Planners
                html.append("var data" + data + "S= new google.visualization.DataTable();\n");
                html.append("data" + data + "S.addColumn('string', 'Problems')metrics;\n");

                List<Element> plans = problems.get(0).getChild("plans").getChildren("xmlPlan");
                for(int pl=0;pl<plans.size();pl++){
                     Element plan = (Element)plans.get(pl);
                     html.append("data" + data + "S.addColumn('number', '"+ plan.getChild("planner").getChildText("name") + " - " + plan.getChild("planner").getChildText("version") +"');\n");
                }
                html.append("data" + data + "S.addRows("+problems.size()+");\n");
                for(int pb=0;pb<problems.size();pb++){
                   plans = problems.get(pb).getChild("plans").getChildren("xmlPlan");
                   html.append("data" + data + "S.setValue("+pb+",0,'P"+pb+"');\n");
                   for(int pl=0;pl<plans.size();pl++){
                        Element plan = (Element)plans.get(pl);
                        html.append("data" + data + "S.setValue("+pb+","+(pl+1)+","+ plan.getChild("plan").getChildren().size() +");\n");
                   }
                }

                if (problems.size()==1){
                    html.append("var chart" + data + "S = new google.visualization.ColumnChart(document.getElementById('comparison-graph-" + data + "S'));\n");
                    html.append("chart" + data + "S.draw(data" + data + "S, {width: 840, height: 300,is3D: true,title:'Number of Actions x Planners'});\n\n");
                }
                else{
                    html.append("var chart" + data + "S = new google.visualization.LineChart(document.getElementById('comparison-graph-" + data + "S'));\n");
                    html.append("chart" + data + "S.draw(data" + data + "S, {width: 840, height: 300, min: 0,title:'Number of Actions x Planners'});\n\n");
                }
                
                graphs_div.append("<div id=\"comparison-graph-" + data + "S\"></div>\n");


                //Graph: Time(speed) x Planners
                html.append("var data" + data + "T= new google.visualization.DataTable();\n");
                html.append("data" + data + "T.addColumn('string', 'Problems');\n");

                plans = problems.get(0).getChild("plans").getChildren("xmlPlan");
                for(int pl=0;pl<plans.size();pl++){
                     Element plan = (Element)plans.get(pl);
                     html.append("data" + data + "T.addColumn('number', '"+ plan.getChild("planner").getChildText("name") + " - " + plan.getChild("planner").getChildText("version") +"');\n");
                }
                html.append("data" + data + "T.addRows("+problems.size()+");\n");
                for(int pb=0;pb<problems.size();pb++){
                   plans = problems.get(pb).getChild("plans").getChildren("xmlPlan");
                   html.append("data" + data + "T.setValue("+pb+",0,'P"+pb+"');\n");
                   for(int pl=0;pl<plans.size();pl++){
                        Element plan = (Element)plans.get(pl);
                        html.append("data" + data + "T.setValue("+pb+","+(pl+1)+","+ (plan.getChild("plan").getChildren().size()>0?plan.getChild("statistics").getChildText("toolTime"):0) +");\n");
                   }
                }

                if (problems.size()==1){
                   html.append("var chart" + data + "T = new google.visualization.ColumnChart(document.getElementById('comparison-graph-" + data + "T'));\n");
                   html.append("chart" + data + "T.draw(data" + data + "T, {width: 840, height: 300, is3D: true,title:'Time(seconds) x Planners'});\n\n");
                }
                else{
                    html.append("var chart" + data + "T = new google.visualization.LineChart(document.getElementById('comparison-graph-" + data + "T'));\n");
                    html.append("chart" + data + "T.draw(data" + data + "T, {width: 840, height: 300, min: 0,title:'Time(seconds) x Planners'});\n\n");
                }
                
                graphs_div.append("<div id=\"comparison-graph-" + data + "T\"></div>\n");

                data++;
            }
        }
        */


        //2.1.4 Metrics

        //TODO: metrics graphs



        //end of function and script
        script.append("           }\n");
        script.append("           </script>\n");

  


        //Content
        //2.2 Introduction
        //add script that contains all tables and graphs
        html.append(script);
        html.append("	<div id=\"content\"> \n");
        html.append("		<div id=\"colOne\" style=\"width:100%\"> \n");
        html.append("		<h2 style=\"margin-top:0\">Introduction</h2> \n");
        html.append("		<p>This <strong>plan comparison report</strong> is a plan analysis interface for helping designers investigate solutions provided by automated planners.</p> \n");

        //2.3 Comparison Table
        html.append("       <h2><a name=\"table\">Comparison Table</a></h2>\n");
        //Add Comparison table
        html.append("       <div id=\"comparison-table\"></div>\n");
        
        // Add Comparison Graphs
        html.append("       <h2><a name=\"graphs\">Comparison Graphs</a></h2>\n");
        html.append(graphs_div.toString());



        //2.4 Metrics (tables)


        //TODO: google table for metrics for each problems
        //TODO: google chart for the set of problems (as in the Time and # of action charts)
        StringBuilder metricTable = new StringBuilder();
        for(int p=0;p<projects.size();p++){
            List<Element> domains = projects.get(p).getChild("domains").getChildren("domain");
            metricTable.append("<h3>.: "+projects.get(p).getChildText("name")+"</h3> \n");
            for(int d=0;d<domains.size();d++){
                Element eaDomain = domains.get(d);
                List<Element> problems = eaDomain.getChild("problems").getChildren("problem");
                for(int pb=0;pb<problems.size();pb++){
                    Element eaProblem = problems.get(pb);

                    // for each problem show the metrics info (Problem | metrics | Time (s) | # actions | Cost/Award | Evaluation
                    Element metrics = eaProblem.getChild("metrics");

                    if (metrics !=null && metrics.getChildren().size() > 0){


                        //metricTable.append("<h3>"+eaDomain.getChildText("name")+" - "+eaProblem.getChildText("name")+"</h3> \n");
                        metricTable.append("<h3>"+eaProblem.getChildText("name")+"</h3> \n");

                        //metricTable.append("<table bgcolor=\"#EEEEEE\" cellpadding=\"5\" cellspacing=\"1\" width=\"100%\">\n");
                        metricTable.append("<table bgcolor=\"#EEEEEE\" cellpadding=\"5\" cellspacing=\"1\" width=\"100%\">\n");

                        //Header
                        metricTable.append(getHtmlMetricTableHeader(metrics));

                        //table body
                        List<Element> plans = eaProblem.getChild("plans").getChildren("xmlPlan");
                        for(int pl=0;pl<plans.size();pl++){
                            Element plan = (Element)plans.get(pl);
                            Element planMetrics = plan.getChild("metrics");
                            if (planMetrics != null && planMetrics.getChildren().size() > 0){
                                metricTable.append(getHtmlMetricTableRow(planMetrics, plan));
                            }
                        }

                        metricTable.append("</table>");
                        metricTable.append("<br><br>");


                        //System.out.print(metricTable.toString());
                    }

                }

            }

        }
        
        String metricsSummary = metricTable.toString();
        if (!metricsSummary.trim().equals("")){
            html.append("       <h2><a name=\"table\">Metrics Summary</a></h2>\n");  
            html.append(metricTable);
        }


        //End of Content
        html.append("   </div> \n");




        //2.5 footer
        html.append("<div id=\"footer\"> \n");
        html.append("	<p>(c) 2010 itSIMPLE. Design by <a href=\"http://dlab.poli.usp.br/\">itSIMPLE team</a>.</p> \n");
        html.append("</div> \n");

        html.append("</body> \n");
        html.append("</html>") ;



        status.setText("Status: Report done!");
        //ItSIMPLE.getInstance().appendOutputPanelText(" (!) Report generated! \n");


        return html.toString();
    }



    /**
     * This method creates an table containing the metric values and evaluations of a plan
     * @param xmlPlan
     * @param metrics
     * @return
     */
    public static String generatePlanMetricsSummary(Element xmlPlan, Element metrics){

        StringBuilder html = new StringBuilder();

         //If there are metrics build the charts
        if (metrics!=null && metrics.getChildren().size() > 0){
            html.append("<h3>Metrics Summary</h3>");

            html.append("<table cellpadding=\"5\" cellspacing=\"1\" width=\"100%\">\n");

            //Header
            html.append(getHtmlMetricTableHeader(metrics));

            //table body                        
            html.append(getHtmlMetricTableRow(metrics, xmlPlan));
            
            html.append("</table>");

        }else{
             html.append("<h3>Metrics Summary</h3> \n");

             html.append("<p>No evaluation performed.</p><br>\n");

             html.append("<table cellpadding=\"5\" cellspacing=\"1\" width=\"100%\">\n");

            //Header
            html.append(getHtmlMetricTableHeader(metrics));

            //table body
            html.append(getHtmlMetricTableRow(metrics, xmlPlan));

            html.append("</table> \n<");
        }


        return html.toString();
    }


/**
     * This method creates an short html summary containing the metric values and evaluations of a plan
     * @param xmlPlan
     * @return
     */
    public static String generatePlanMetricsEvaluationSummary(Element xmlPlan){

        StringBuilder html = new StringBuilder();

        Element metrics = xmlPlan.getChild("metrics");

        html.append("<h1>Evaluation Summary</h1>");

        //Planner
        html.append("Planner: " + xmlPlan.getChild("planner").getChildText("name") + " " + xmlPlan.getChild("planner").getChildText("version") + "<br>");

        int planlength = xmlPlan.getChild("plan").getContentSize();
        html.append("Plan length: " + planlength +"<br>");

        //Validity
        Element theValidity = xmlPlan.getChild("validity");
        boolean isValid = true;
        boolean planChecked = true;
        if (theValidity != null){
                if (theValidity.getAttributeValue("isValid").equals("false")){
                    //planlength = 0;
                    isValid = false;
                }
                else if(theValidity.getAttributeValue("isValid").trim().equals("")){
                   planChecked = false;
                }
        }else{
            planChecked = false;
        }

        String validityString = "";
        if (planlength < 1){
            validityString = "<font color='red'>empty plan</font>";
        }
        else if (planChecked){
            if(isValid){validityString = "<font color='green'>valid</font>";}
            else{validityString = "<font color='red'>invalid</font>";}
        }
        else if (!planChecked){
            validityString = "<font color='blue'>not validated</font>";
        }

        if(validityString.trim().equals("")){
            validityString = "<font color='blue'>not validated</font>";
        }

        html.append("Plan validity: " + validityString +"<br>");
        
        html.append("<br>");



        //Metrics evaluation
        html.append("<h2>Metrics</h2>");

        if (metrics!=null && metrics.getChildren().size() > 0){
            html.append(getHtmlMetricVerticalTable(xmlPlan));
            //html.append(getHtmlMetricHorizontalTable(xmlPlan));
        }else{
            html.append("<p>No evaluation performed.</p><br>\n");
        }
        html.append("<br>");


        //Overall evaluation
        html.append("<h2>Plan evaluation</h2>");


        //Overall evaluation (plan score)

        double overallCostAward = 0;

        //compute the plan cost based on the metrics.
        //if (isValid && planlength > 0){
            overallCostAward = evaluateCostAward(metrics);
        //}

        double overallgrade = 0;
        String overallScore = "";
        Element planEvaluation = xmlPlan.getChild("evaluation");
        if(planEvaluation == null){
            planEvaluation = new Element("evaluation");
            planEvaluation.setAttribute("value", "");
            Element rationales = new Element("rationales");
            planEvaluation.addContent(rationales);
            xmlPlan.addContent(planEvaluation);
        }
        //check (or compute if necessary) planEvaluation
        if (planEvaluation.getAttributeValue("value").trim().equals("")){
            if (isValid && planlength > 0){
                overallgrade = evaluatePlan(metrics);
                DecimalFormat overall = new DecimalFormat("0.00");
                planEvaluation.setAttribute("value", overall.format(overallgrade));
            }
        }

        overallScore = planEvaluation.getAttributeValue("value").trim();
        if (overallScore.equals("")){
            overallScore = "0.00";
        }

        html.append("<p>Plan cost: "+Double.toString(overallCostAward)+"</p>");
        html.append("<p>Plan evaluation: <strong>"+overallScore+"</strong></p>");


        html.append("<br><br>");


        //Rationales
        Element rationales = planEvaluation.getChild("rationales");
        if (rationales == null){
            rationales = new Element("rationales");
            planEvaluation.addContent(rationales);
        }
        if (rationales.getChildren().size() > 0){
            html.append("<h2>Evaluation rationales</h2>");

            for (Iterator<Element> it = rationales.getChildren().iterator(); it.hasNext();) {
                Element rationale = it.next();
                String enabled = "";
                //XMLUtilities.printXML(rationale);
                if (rationale.getAttributeValue("enabled").equals("false")){
                    enabled = " (NOT APPLIED)";
                }
                html.append("<p>");
                html.append("<strong>Name: <font color='blue'>"+rationale.getChildText("name").trim()+enabled+"</font></strong><br>");
                html.append("<strong>Description: </strong>"+rationale.getChildText("description")+"<br>");
                html.append("<strong>Formal description: </strong><font face='courier'>"+rationale.getChildText("rule")+"</font><br>");
                html.append("<strong>Quality impact: </strong>"+rationale.getChild("impact").getAttributeValue("quality")+"<br>");
                html.append("<strong>Abstraction level: </strong>"+rationale.getChild("abstractionlevel").getAttributeValue("range")+"<br>");
                String validityStr = "";
                if (rationale.getChild("validity").getAttributeValue("isValid").trim().equals("")){
                    validityStr = "<font color='blue'>not verified</font>";
                }else if (rationale.getChild("validity").getAttributeValue("isValid").trim().equals("true")){
                    validityStr = "<font color='green'>valid</font>";
                }else if (rationale.getChild("validity").getAttributeValue("isValid").trim().equals("false")){
                    validityStr = "<font color='red'>unvalid</font>";
                }
                html.append("<strong>Validity: </strong>"+validityStr+"<br>");
                html.append("<strong>Comments: </strong><br> \n");
                String commentsStr = "";
                Element comments = rationale.getChild("comments");
                for (Iterator<Element> it1 = comments.getContent().iterator(); it1.hasNext();) {
                    Element comment = it1.next();
                    commentsStr += comment.getText() + " <br>";
                }
                if (!commentsStr.trim().equals("")){
                    html.append(commentsStr);
                }else{
                    html.append("no comment.");
                }

                html.append("</p>");

                html.append("<br>");

            }
        }



        return html.toString();
    }


    /**
     * This methos generates the header of a html table for a set of metrics
     * @param metrics the xml node that holds the metrics
     * @return
     */
    public static String getHtmlMetricTableHeader(Element metrics){

        boolean emptyMetrics = false;
        if (metrics == null){
            emptyMetrics = true;
        }else if (metrics.getChildren().size() < 1){
            emptyMetrics = true;
        }

        //rowspan
        String rowspan = "rowspan=\"2\"";
        if (emptyMetrics){
            rowspan = "";
        }

        //Header
        StringBuilder tableHeader = new StringBuilder();
        //Firstline
        // Planner(s) | Metric 1 ... Metric N | Time (s) | # actions | Evaluation
        StringBuilder firstLine = new StringBuilder();
        firstLine.append("<tr style=\"background-color:#000066; color:#FFFFFF; height:35px;\" >\n");
        firstLine.append("  <th "+rowspan+" valign=\"top\">Planner</th>\n");

        StringBuilder secondLine = new StringBuilder();
        

        //table body
        if(metrics!=null && metrics.getChildren().size() > 0){
            secondLine.append("<tr style=\"background-color:#000066; color:#FFFFFF; height:35px;\" >\n");

            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element metric = it.next();
                String metricname = metric.getChildText("name");

                firstLine.append("   <th colspan=\"3\">"+ metricname +"</th> \n");

                secondLine.append(" <th>Value</th> \n");
                secondLine.append(" <th>Score</th> \n");
                secondLine.append(" <th>Weight</th> \n");
            }
            //Finishing second line
            secondLine.append("</tr>\n");

        }

        //Finishing first line
        //Time (in seconds)
        firstLine.append("<th "+rowspan+" valign=\"top\" >Time(sec)</th>\n");
        //Number of actions
        firstLine.append("<th "+rowspan+" valign=\"top\">Steps</th>\n");
        //Plan validity
        firstLine.append("<th "+rowspan+" valign=\"top\">Validity</th>\n");
        //Cost/awars
        firstLine.append("<th "+rowspan+" valign=\"top\">Cost</th>\n");
        //Evaluation of the plan [0,1]
        firstLine.append("<th "+rowspan+" valign=\"top\">Evaluation</th>\n");
        
        firstLine.append("</tr>\n");
       

        tableHeader.append(firstLine);
        tableHeader.append(secondLine);


        return tableHeader.toString();
    }


/**
     * This methos generates the header of a html table for a set of metrics
     * @param metrics the xml node that holds the metrics
     * @return
     */
    public static String getHtmlMetricTableHeaderOnly(Element xmlPlan){

        //Metric Header
        StringBuilder tableHeader = new StringBuilder();
        Element metrics = xmlPlan.getChild("metrics");

        if (metrics != null && metrics.getChildren().size() > 0){
            //Firstline
            //| Metric 1 ... Metric N |
            StringBuilder firstLine = new StringBuilder();
            firstLine.append("<tr style=\"background-color:#000066; color:#FFFFFF; height:35px;\" >\n");
            StringBuilder secondLine = new StringBuilder();


            //table body
            if(metrics!=null && metrics.getChildren().size() > 0){
                secondLine.append("<tr style=\"background-color:#000066; color:#FFFFFF; height:35px;\" >\n");

                for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                    Element metric = it.next();
                    String metricname = metric.getChildText("name");

                    firstLine.append("   <th colspan=\"3\">"+ metricname +"</th> \n");

                    secondLine.append(" <th>Value</th> \n");
                    secondLine.append(" <th>Score</th> \n");
                    secondLine.append(" <th>Weight</th> \n");
                }
                //Finishing second line
                secondLine.append("</tr>\n");

            }
            firstLine.append("</tr>\n");
            tableHeader.append(firstLine);
            tableHeader.append(secondLine);

        }
        return tableHeader.toString();


    }


    /**
     * This method generates a single html table row for a given plan showing 
     * the metric data that the node 'metrics' holds
     * @param metrics
     * @param xmlPlan
     * @return an String containing a html row
     */
    public static String getHtmlMetricTableRow(Element metrics, Element xmlPlan){

        StringBuilder tableRow = new StringBuilder();

        tableRow.append("<tr> \n");

        //Planner's name
        Element planner = xmlPlan.getChild("planner");
        String plannerName = planner.getChildText("name") + "- " + planner.getChildText("version");
        tableRow.append("   <td bgcolor=\"#FFFFFF\">"+plannerName+"</td> \n");

        DecimalFormat indgrade = new DecimalFormat("0.00");

        //metric values (value, individual evaluation, weight)
        if (metrics!=null){

            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element metric = it.next();



                String value = "";
                String individualEval = "";
                //String metricname = metric.getChildText("name");
                String metricWeight = metric.getChildText("weight");

                //check if it need evaluation (the values and evaluation can be already available in the value and evaluation node of the metric)
                Element metricValue = metric.getChild("value");
                Element metricEvaluation = metric.getChild("evaluation");

                if (metricValue == null){
                   metricValue = new  Element("value");
                   metric.addContent(metricValue);
                }
                if (metricEvaluation == null){
                   metricEvaluation = new  Element("evaluation");
                   metricEvaluation.setAttribute("value", "");
                   metric.addContent(metricEvaluation);
                }

                Element dataset = metric.getChild("dataset");
                if (metricValue.getText().trim().equals("") && dataset.getChildren().size() > 0){
                    //Getting metric value
                    value = Float.toString(getMetricValue(metric));
                    metricValue.setText(value);
                }
                if (metricEvaluation.getAttributeValue("value").trim().equals("") && dataset.getChildren().size() > 0){
                    //individual evaluation
                    individualEval = indgrade.format(evaluateMetric(metric));
                    metricEvaluation.setAttribute("value", individualEval);
                }

                //Element dataset = metric.getChild("dataset");
    //            if (dataset.getChildren().size() > 0){
    //                //Getting metric value
    //                value = Float.toString(getMetricValue(metric));
    //
    //                //individual evaluation
    //                individualEval = indgrade.format(evaluateMetric(metric));
    //
    //            }

                value = metricValue.getText().trim();
                individualEval = metricEvaluation.getAttributeValue("value").trim();

                if (value.equals("")){value = "-"; }
                if (individualEval.equals("")){individualEval = "-"; }

                tableRow.append("   <td align=\"center\" bgcolor=\"#FFFFFF\">"+value+"</td> \n");
                tableRow.append("   <td align=\"center\" bgcolor=\"#FFFFFF\">"+individualEval+"</td> \n");
                tableRow.append("   <td align=\"center\" bgcolor=\"#FFFFFF\">"+metricWeight+"</td> \n");
            }
        }


        //Time (seconds)
        Element statistics = xmlPlan.getChild("statistics");
        String time = statistics.getChildText("toolTime");

        //Number of actions
        Element plan = xmlPlan.getChild("plan");
        int planlength = plan.getChildren().size();

        //check the validity of the plan
        String validityString = "";

        Element theValidity = xmlPlan.getChild("validity");
        boolean isValid = true;
        boolean planChecked = true;
        if (theValidity != null){
                if (theValidity.getAttributeValue("isValid").equals("false")){
                    //planlength = 0;
                    isValid = false;
                }
                else if(theValidity.getAttributeValue("isValid").trim().equals("")){
                   planChecked = false;
                }
         }else{
            planChecked = false;
         }

        String numberOfActions = Integer.toString(planlength);


        //check if it was timeout or skipped or
        boolean timeoutOrSkipped = false;
        if (planlength < 1){
            Element reason = xmlPlan.getChild("statistics").getChild("forcedQuit");
            if (reason != null && !reason.getText().trim().equals("")){
                time += " ("+ reason.getText().trim() + ")";
                timeoutOrSkipped = true;
            }
            validityString = "<font color='red'>empty plan</font>";
            //numberOfActions += " (empty plan)";
        }
        else if (planChecked){
            if(isValid){
                validityString = "<font color='green'>valid</font>";
            }
            else{
                validityString = "<font color='red'>invalid</font>";
                //numberOfActions += " (invalid)";
            }
        }
        else if (!planChecked){
            validityString = "<font color='blue'>not validated</font>";
            //numberOfActions += " (not validated)";
        }

        if(validityString.trim().equals("")){
            validityString = "<font color='blue'>not validated</font>";
        }

        String overallScore = "";

        Element planEvaluation = xmlPlan.getChild("evaluation");

        if(planEvaluation == null){
            planEvaluation = new Element("evaluation");
            planEvaluation.setAttribute("value", "");
            xmlPlan.addContent(planEvaluation);
        }

        //Overall evaluation (plan score)
        double overallgrade = 0;
        double overallCostAward = 0;

        //compute the plan cost based on the metrics.
        if (!timeoutOrSkipped && isValid && planlength > 0){           
            overallCostAward = evaluateCostAward(metrics);
        }

        //check (or compute if necessary) planEvaluation
        if (planEvaluation.getAttributeValue("value").trim().equals("")){
            if (!timeoutOrSkipped && isValid && planlength > 0){
                overallgrade = evaluatePlan(metrics);
                DecimalFormat overall = new DecimalFormat("0.00");
                planEvaluation.setAttribute("value", overall.format(overallgrade));
            }
        }

        overallScore = planEvaluation.getAttributeValue("value").trim();
        if (overallScore.equals("")){
            overallScore = "0.00";
        }


        tableRow.append("   <td align=\"center\" bgcolor=\"#FFFFFF\">"+time+"</td> \n");

        tableRow.append("   <td align=\"center\" bgcolor=\"#FFFFFF\">"+numberOfActions+"</td> \n");

        tableRow.append("   <td align=\"center\" bgcolor=\"#FFFFFF\">"+validityString+"</td> \n");

        tableRow.append("   <td align=\"center\" bgcolor=\"#FFFFFF\">"+Double.toString(overallCostAward)+"</td> \n");
      
        tableRow.append("   <td align=\"center\" bgcolor=\"#FFFFFF\">"+overallScore+"</td> \n");

        tableRow.append("</tr> \n");

        return tableRow.toString();
    }



    public static String getHtmlMetricTableRowOnly(Element xmlPlan){

        StringBuilder tableRow = new StringBuilder();

        Element metrics = xmlPlan.getChild("metrics");

        tableRow.append("<tr> \n");


        DecimalFormat indgrade = new DecimalFormat("0.00");

        //metric values (value, individual evaluation, weight)
        if (metrics!=null){

            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element metric = it.next();

                String value = "";
                String individualEval = "";
                //String metricname = metric.getChildText("name");
                String metricWeight = metric.getChildText("weight");

                //check if it need evaluation (the values and evaluation can be already available in the value and evaluation node of the metric)
                Element metricValue = metric.getChild("value");
                Element metricEvaluation = metric.getChild("evaluation");

                if (metricValue == null){
                   metricValue = new  Element("value");
                   metric.addContent(metricValue);
                }
                if (metricEvaluation == null){
                   metricEvaluation = new  Element("evaluation");
                   metricEvaluation.setAttribute("value", "");
                   metric.addContent(metricEvaluation);
                }

                Element dataset = metric.getChild("dataset");
                if (metricValue.getText().trim().equals("") && dataset.getChildren().size() > 0){
                    //Getting metric value
                    value = Float.toString(getMetricValue(metric));
                    metricValue.setText(value);
                }
                if (metricEvaluation.getAttributeValue("value").trim().equals("") && dataset.getChildren().size() > 0){
                    //individual evaluation
                    individualEval = indgrade.format(evaluateMetric(metric));
                    metricEvaluation.setAttribute("value", individualEval);
                }

                //Element dataset = metric.getChild("dataset");
    //            if (dataset.getChildren().size() > 0){
    //                //Getting metric value
    //                value = Float.toString(getMetricValue(metric));
    //
    //                //individual evaluation
    //                individualEval = indgrade.format(evaluateMetric(metric));
    //
    //            }

                value = metricValue.getText().trim();
                individualEval = metricEvaluation.getAttributeValue("value").trim();

                if (value.equals("")){value = "-"; }
                if (individualEval.equals("")){individualEval = "-"; }

                tableRow.append("   <td align=\"center\" bgcolor=\"#FFFFFF\">"+value+"</td> \n");
                tableRow.append("   <td align=\"center\" bgcolor=\"#FFFFFF\">"+individualEval+"</td> \n");
                tableRow.append("   <td align=\"center\" bgcolor=\"#FFFFFF\">"+metricWeight+"</td> \n");
            }
        }

        tableRow.append("</tr> \n");

        return tableRow.toString();
    }

    /**
     * This methods creates a html (horizontal) table with the metrics value, weight and evaluation (only the metrics
     * @param xmlPlan
     * @return
     */
    public static String getHtmlMetricHorizontalTable(Element xmlPlan){

        StringBuilder table = new StringBuilder();

        //horizontal Table
        table.append("<table cellpadding=\"5\" cellspacing=\"1\" width=\"100%\">\n");
        //Header
        table.append(getHtmlMetricTableHeaderOnly(xmlPlan));
        //body
        table.append(getHtmlMetricTableRowOnly(xmlPlan));
        table.append("</table>");


        return table.toString();

    }


    /**
     * This methods creates a html (vertical) table with the metrics value, weight and evaluation (only the metrics
     * @param xmlPlan
     * @return
     */
    public static String getHtmlMetricVerticalTable(Element xmlPlan){

        StringBuilder table = new StringBuilder();
        table.append("<table cellpadding=\"5\" cellspacing=\"1\" width=\"100%\">\n");
        //header
        table.append("<tr style=\"background-color:#000066; color:#FFFFFF; height:35px;\" >\n");
        table.append("   <th>Metric name</th> \n");
        table.append("   <th>Weight</th> \n");
        table.append("   <th>Value</th> \n");
        table.append("   <th>Score</th> \n");
        table.append("</tr> \n");

        //table body
        Element metrics = xmlPlan.getChild("metrics");

        DecimalFormat indgrade = new DecimalFormat("0.00");

        //metric values (value, individual evaluation, weight)
        if (metrics!=null){
            table.append("<tr> \n");

            for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
                Element metric = it.next();

                String value = "";
                String individualEval = "";
                String metricname = metric.getChildText("name");
                String metricWeight = metric.getChildText("weight");

                //check if it need evaluation (the values and evaluation can be already available in the value and evaluation node of the metric)
                Element metricValue = metric.getChild("value");
                Element metricEvaluation = metric.getChild("evaluation");

                if (metricValue == null){
                   metricValue = new  Element("value");
                   metric.addContent(metricValue);
                }
                if (metricEvaluation == null){
                   metricEvaluation = new  Element("evaluation");
                   metricEvaluation.setAttribute("value", "");
                   metric.addContent(metricEvaluation);
                }

                Element dataset = metric.getChild("dataset");
                if (metricValue.getText().trim().equals("") && dataset.getChildren().size() > 0){
                    //Getting metric value
                    value = Float.toString(getMetricValue(metric));
                    metricValue.setText(value);
                }
                if (metricEvaluation.getAttributeValue("value").trim().equals("") && dataset.getChildren().size() > 0){
                    //individual evaluation
                    individualEval = indgrade.format(evaluateMetric(metric));
                    metricEvaluation.setAttribute("value", individualEval);
                }

                //Element dataset = metric.getChild("dataset");
    //            if (dataset.getChildren().size() > 0){
    //                //Getting metric value
    //                value = Float.toString(getMetricValue(metric));
    //
    //                //individual evaluation
    //                individualEval = indgrade.format(evaluateMetric(metric));
    //
    //            }

                value = metricValue.getText().trim();
                individualEval = metricEvaluation.getAttributeValue("value").trim();

                if (value.equals("")){value = "-"; }
                if (individualEval.equals("")){individualEval = "-"; }

                table.append("   <td align=\"center\" bgcolor=\"#FFFFFF\">"+metricname+"</td> \n");
                table.append("   <td align=\"center\" bgcolor=\"#FFFFFF\">"+metricWeight+"</td> \n");
                table.append("   <td align=\"center\" bgcolor=\"#FFFFFF\">"+value+"</td> \n");
                table.append("   <td align=\"center\" bgcolor=\"#FFFFFF\">"+individualEval+"</td> \n");

                table.append("</tr> \n");
            }
        }

        table.append("</table>");

        return table.toString();
    }


/**
     * This method sets the value of the metric and its evaluation (when available)
     * @param metrics
     * @param xmlPlan
     * @return an String containing a html row
     */
    public static void setMetricsValuesAndEvaluations(Element metrics, Element xmlPlan){


        //Planner's name
        Element planner = xmlPlan.getChild("planner");

        DecimalFormat individualScoreFormat = new DecimalFormat("0.00");

        //metric values (value, individual evaluation, weight)
        for (Iterator<Element> it = metrics.getChildren().iterator(); it.hasNext();) {
            Element metric = it.next();

            String value = "";
            String individualEval = "";

            Element dataset = metric.getChild("dataset");
            if (dataset.getChildren().size() > 0){
                //Getting metric value
                value = Float.toString(getMetricValue(metric));

                //individual evaluation
                individualEval = individualScoreFormat.format(evaluateMetric(metric));
            }

            Element metricValue = metric.getChild("value");
            Element metricEvaluation = metric.getChild("evaluation");

            if (metricValue==null){
               metricValue = new  Element("value");
               metric.addContent(metricValue);
            }
            if (metricEvaluation==null){
               metricEvaluation = new  Element("evaluation");
               metricEvaluation.setAttribute("value", "");
               metric.addContent(metricEvaluation);
            }

            metricValue.setText(value);
            metricEvaluation.setAttribute("value", individualEval);

        }


        //Time (seconds)
        Element statistics = xmlPlan.getChild("statistics");
        String time = statistics.getChildText("toolTime");

        //Number of actions
        Element plan = xmlPlan.getChild("plan");
        int planlength = plan.getChildren().size();

        //check the validity of the plan
        String validityString = "";

        Element theValidity = xmlPlan.getChild("validity");
        boolean isValid = true;
        boolean planChecked = true;
        if (theValidity != null){
                if (theValidity.getAttributeValue("isValid").equals("false")){
                    //planlength = 0;
                    isValid = false;
                }
                else if(theValidity.getAttributeValue("isValid").trim().equals("")){
                   planChecked = false;
                }
         }else{
            planChecked = false;
         }

        String numberOfActions = Integer.toString(planlength);


         //Overall evaluation (plan grade)
        double overallscore = 0;
        //double overallCostAward = 0;
        if (isValid && planlength > 0){
            overallscore = evaluatePlan(metrics);
            //overallCostAward = evaluateCostAward(metrics);
        }

        DecimalFormat overall = new DecimalFormat("0.00");

        Element planEvaluation = xmlPlan.getChild("evaluation");
        if (planEvaluation == null){
            planEvaluation = new Element("evaluation");
            planEvaluation.setAttribute("value", "");
            xmlPlan.addContent(planEvaluation);
        }
        planEvaluation.setAttribute("value", overall.format(overallscore));


    }



    public static String generateProjectComparisonReport(Element baseProject, List<Element> projects){


        //Calculating the progress status
        double numberOfProblems = 0;
        try {
                XPath path = new JDOMXPath("count(descendant::problems/problem)");
                numberOfProblems = (Double) path.selectSingleNode(baseProject);
        } catch (JaxenException e) {
                e.printStackTrace();
        }

        JLabel status = ItSIMPLE.getInstance().getPlanSimStatusBar();
        status.setText("Status: Generating report... (0%)");
        int progressIndex = 0;


        StringBuilder html = new StringBuilder();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateTime = dateFormat.format(date);




        

        //GATHERING BODY DATA

         //2.1.1 Main script (google tables and charts)
        StringBuilder script = new StringBuilder();

        script.append("           <script type='text/javascript'>\n");
        script.append("               google.load('visualization', '1', {packages:['piechart', 'columnchart']}); \n");
        script.append("               google.setOnLoadCallback(buildComponents); \n\n");
        script.append("               function buildComponents() {\n");





        //2.3 Metrics & Planner comparison

        //Set the overall statistics variable to compare the projects (how many
        // time they were better in time, plan length, evaluation, etc.

        //Analysis data (time, plan length, cost, quality)
        List<Element> OverAllProjectsStatistics = new ArrayList<Element>();

        //prepare statistcs support nodes
        Element overAllBaseProjStat = (Element)ItSIMPLE.getCommonData().getChild("planAnalysis").getChild("statisticSupport").clone();
        overAllBaseProjStat.setAttribute("id", "base");
        overAllBaseProjStat.getChild("name").setText(baseProject.getChildText("name"));
        Element overAllBaseTimeStat = overAllBaseProjStat.getChild("time");
        Element overAllBasePlanLengthStat = overAllBaseProjStat.getChild("planlength");
        Element overAllBaseEvaluationStat = overAllBaseProjStat.getChild("evaluation");

        //Add it to the projectsStaticts
        OverAllProjectsStatistics.add(overAllBaseProjStat);

        int projectIndex = 1;
        for (Iterator<Element> it2 = projects.iterator(); it2.hasNext();) {
            Element eaComparableProject = it2.next();
            //prepare statistcs support nodes
            Element eaProjStat = (Element)ItSIMPLE.getCommonData().getChild("planAnalysis").getChild("statisticSupport").clone();
            eaProjStat.setAttribute("id", Integer.toString(projectIndex));
            eaProjStat.getChild("name").setText(eaComparableProject.getChildText("name"));

            //Add it to the projectsStaticts
            OverAllProjectsStatistics.add(eaProjStat);
            projectIndex++;
        }




        //TODO: google table for metrics for each problems
        //TODO: google chart for the set of problems (as in the Time and # of action charts)
        StringBuilder metricTable = new StringBuilder();
        List<Element> domains = baseProject.getChild("domains").getChildren("domain");
        for(int d=0;d<domains.size();d++){
            Element eaDomain = domains.get(d);
            List<Element> problems = eaDomain.getChild("problems").getChildren("problem");
            for(int pb=0;pb<problems.size();pb++){
                Element eaProblem = problems.get(pb);

                // for each problem show the metrics info (Problem | metrics | Time (s) | # actions | Cost/Award | Evaluation
                Element metrics = eaProblem.getChild("metrics");

                if (metrics !=null && metrics.getChildren().size() > 0){

                    //metricTable.append("<h3>"+eaDomain.getChildText("name")+" - "+eaProblem.getChildText("name")+"</h3> \n");
                    metricTable.append("<h3>"+eaProblem.getChildText("name")+"</h3> \n");

                    //metricTable.append("<table bgcolor=\"#EEEEEE\" cellpadding=\"5\" cellspacing=\"1\" width=\"100%\">\n");
                    metricTable.append("<table bgcolor=\"#EEEEEE\" cellpadding=\"5\" cellspacing=\"1\" width=\"100%\">\n");

                    //Header
                    metricTable.append(getHtmlMetricTableHeader(metrics));

                    //table body


                    //STATISTICS
                    //Analysis data (time, plan length, cost, quality)
                    List<Element> projectsStatistics = new ArrayList<Element>();

                    //prepare statistcs support nodes
                    Element baseProjStat = (Element)ItSIMPLE.getCommonData().getChild("planAnalysis").getChild("statisticSupport").clone();
                    baseProjStat.setAttribute("id", "base");
                    baseProjStat.getChild("name").setText(baseProject.getChildText("name"));
                    Element timeStat = baseProjStat.getChild("time");
                    Element planLengthStat = baseProjStat.getChild("planlength");
                    Element planCostStat = baseProjStat.getChild("plancost");
                    Element evaluationStat = baseProjStat.getChild("evaluation");

                    //Add it to the projectsStaticts
                    projectsStatistics.add(baseProjStat);

                    int index = 1;
                    for (Iterator<Element> it2 = projects.iterator(); it2.hasNext();) {
                        Element eaComparableProject = it2.next();
                        //prepare statistcs support nodes
                        Element eaProjStat = (Element)ItSIMPLE.getCommonData().getChild("planAnalysis").getChild("statisticSupport").clone();
                        eaProjStat.setAttribute("id", Integer.toString(index));
                        eaProjStat.getChild("name").setText(eaComparableProject.getChildText("name"));

                        //Add it to the projectsStaticts
                        projectsStatistics.add(eaProjStat);
                        index++;
                    }



                    //Check each plan and its caparable plans
                    List<Element> plans = eaProblem.getChild("plans").getChildren("xmlPlan");
                    for(int pl=0;pl<plans.size();pl++){
                        Element basePlan = (Element)plans.get(pl);

                        //HTML
                        //Base (referencial) planner in the comparison
                        //put the html row of the base planner
                        Element basePlanner = basePlan.getChild("planner");
                        Element basePlanMetrics = basePlan.getChild("metrics");
                        if (basePlanMetrics != null && basePlanMetrics.getChildren().size() > 0){
                            String baseRow = getHtmlMetricTableRow(basePlanMetrics, basePlan);
                            //changing the color of the first/base row
                            baseRow = baseRow.replaceAll("FFFFFF", "CCCCCC");
                            metricTable.append(baseRow);
                        }


                        //STATISTICS
                        //it start in each base plan
                        float bestTime = -1;
                        float bestPlanLength = -1;
                        double bestPlanCost = -1;
                        double bestEvaluation = 0.0;

                        //Collect analysis data
                        Element baseStatistics = basePlan.getChild("statistics");
                        timeStat.setAttribute("temp", "");
                        planLengthStat.setAttribute("temp", "");
                        evaluationStat.setAttribute("temp", "");

                        int baselength = basePlan.getChild("plan").getChildren().size();
                                                
                        //check the validity of the plan
                        boolean isValid = true;
                        Element validity = basePlan.getChild("validity");
                        if (validity != null && validity.getAttributeValue("isValid").equals("false")){
                            baselength = 0;
                            isValid = false;
                        }


                        if (baselength > 0){
                            //1. Time (min) - starting with the base solution/plan
                            float baseTime = -1;
                            try {
                                baseTime = Float.parseFloat(baseStatistics.getChildText("toolTime"));
                            } catch (Exception e) {
                            }
                            if (baseTime > -1) {
                                timeStat.setAttribute("temp", Float.toString(baseTime));
                                bestTime = baseTime;                                
                            }
                            
                            //2. Plan length (min) - starting with the base solution/plan
                            bestPlanLength = baselength;
                            planLengthStat.setAttribute("temp", Integer.toString(baselength));

                            //3. Plan cost (min) - starting with the base solution/plan
                            double basePlanCost = evaluateCostAward(basePlanMetrics);
                            bestPlanCost = basePlanCost;
                            planCostStat.setAttribute("temp", Double.toString(bestPlanCost));

                            //4. Evaluation (max) -  starting with the base solution/plan
                            double eval = 0;
                            Element basePlanEvaluation = basePlan.getChild("evaluation");
                            if (basePlanEvaluation==null){
                                basePlanEvaluation = new Element("evaluation");
                                basePlanEvaluation.setAttribute("value", "");
                                basePlan.addContent(basePlanEvaluation);
                            }
                            if (!basePlanEvaluation.getAttributeValue("value").trim().equals("")){
                                eval = Double.parseDouble(basePlanEvaluation.getAttributeValue("value").trim());
                            }else{
                                eval = evaluatePlan(basePlanMetrics);
                            }
                            evaluationStat.setAttribute("temp", Double.toString(eval));
                            bestEvaluation = eval;

                            //double eval = evaluatePlan(basePlanMetrics);
                            //bestEvaluation = eval;
                            //evaluationStat.setAttribute("temp", Double.toString(eval));


                        }


                        //for each planner get its performnce from the other projects for the same domain and problem
                        for(int i=0;i<projects.size();i++){
                            Element eaComparableProject = projects.get(i);

                            Element comparablePlan = null;
                            try {
                                    XPath path = new JDOMXPath("domains/domain[name='"+eaDomain.getChildText("name")+
                                            "']/problems/problem[name='"+eaProblem.getChildText("name")+"']/plans/xmlPlan[planner/name='"+
                                            basePlanner.getChildText("name") +"' and planner/version='"+ basePlanner.getChildText("version") +"']");
                                    comparablePlan = (Element)path.selectSingleNode(eaComparableProject);
                            } catch (JaxenException e) {
                                    e.printStackTrace();
                            }

                            if (comparablePlan != null){
                                //comparable plan found

                                //Get data analysis nodes - Statistcs
                                Element eaProjStat = projectsStatistics.get(i+1);
                                Element compStatistics = comparablePlan.getChild("statistics");
                                Element eaTimeStat = eaProjStat.getChild("time");
                                Element eaPlanLengthStat = eaProjStat.getChild("planlength");
                                Element eaPlanCostStat = eaProjStat.getChild("plancost");
                                Element eaEvaluationStat = eaProjStat.getChild("evaluation");
                                //clear temp values
                                eaTimeStat.setAttribute("temp", "");
                                eaPlanLengthStat.setAttribute("temp", "");
                                eaPlanCostStat.setAttribute("temp", "");
                                eaEvaluationStat.setAttribute("temp", "");


                                //Insert row in the table
                                Element planMetrics = comparablePlan.getChild("metrics");
                                if (planMetrics != null && planMetrics.getChildren().size() > 0){

                                    //HTML
                                    String row = getHtmlMetricTableRow(planMetrics, comparablePlan);
                                    //Insert new row
                                    metricTable.append(row);

                                    //STATISTICS
                                    //Collect data analysis
                                    int theLength = comparablePlan.getChild("plan").getChildren().size();

                                    //check the validity of the plan
                                    boolean isTheValid = true;
                                    Element theValidity = comparablePlan.getChild("validity");
                                    if (theValidity != null && theValidity.getAttributeValue("isValid").equals("false")){
                                        theLength = 0;
                                        isTheValid = false;
                                    }

                                    if (theLength > 0){
                                        //1.Time (min)
                                        float theTime = -1;
                                        try {
                                            theTime = Float.parseFloat(compStatistics.getChildText("toolTime"));
                                        } catch (Exception e) {
                                        }

                                        if (theTime > -1) {
                                            eaTimeStat.setAttribute("temp", Float.toString(theTime));
                                            if(bestTime == -1){
                                                bestTime = theTime;
                                            }else{
                                                if (theTime < bestTime){bestTime = theTime;}
                                            }
                                            
                                        }

                                        //2.Plan length (min)
                                        eaPlanLengthStat.setAttribute("temp", Integer.toString(theLength));
                                        if (bestPlanLength == -1) {
                                            bestPlanLength = theLength;
                                        }else{
                                            if (theLength < bestPlanLength){bestPlanLength = theLength;}
                                        }


                                        //3. Plan cost (min)
                                        double thePlanCost = evaluateCostAward(planMetrics);
                                        eaPlanCostStat.setAttribute("temp", Double.toString(thePlanCost));
                                        if (bestPlanCost == -1) {
                                            bestPlanCost = thePlanCost;
                                        }else{
                                            if (thePlanCost < bestPlanCost){bestPlanCost = thePlanCost;}
                                        }
                                        


                                         //4 .Evaluation (max)
                                        double eval = 0;
                                        Element planEvaluation = comparablePlan.getChild("evaluation");
                                        if (planEvaluation==null){
                                            planEvaluation = new Element("evaluation");
                                            planEvaluation.setAttribute("value", "");
                                            comparablePlan.addContent(planEvaluation);
                                        }
                                        if (!planEvaluation.getAttributeValue("value").trim().equals("")){
                                            eval = Double.parseDouble(planEvaluation.getAttributeValue("value").trim());
                                        }else{
                                            eval = evaluatePlan(planMetrics);
                                        }
                                      
                                        //double eval = evaluatePlan(planMetrics);
                                        eaEvaluationStat.setAttribute("temp", Double.toString(eval));
                                        if (eval > bestEvaluation){bestEvaluation = eval;}
                                        
                                    }


                                }

                            }

                        }

                        //Check who (the model) was the best (time, plan length, evaluation, etc) for this planner
                        for (Iterator<Element> itp = projectsStatistics.iterator(); itp.hasNext();) {
                            Element eaStat = itp.next();

                            //1.time
                            Element eaTimeStat = eaStat.getChild("time");                            
                            String timeStr = eaTimeStat.getAttributeValue("temp");
                            if (!timeStr.trim().equals("")){
                                float theTime = -1;
                                try {
                                    theTime = Float.parseFloat(timeStr);
                                } catch (Exception e) {
                                }
                                //if this model/project reached the best time then score it
                                if (theTime == bestTime) {
                                    int timeCounter = Integer.parseInt(eaTimeStat.getAttributeValue("counter"));
                                    timeCounter++;
                                    eaTimeStat.setAttribute("counter", Integer.toString(timeCounter));
                                }


                            }
                            //2.plan length
                            Element eaPlanLengthStat = eaStat.getChild("planlength");
                            String planLengthStr = eaPlanLengthStat.getAttributeValue("temp");
                            if (!planLengthStr.trim().equals("")){
                                //if this model/project reached the best planLength then score it
                                int theLength = Integer.parseInt(planLengthStr);
                                if (theLength == bestPlanLength && bestPlanLength > 0) {
                                    int planLengthCounter = Integer.parseInt(eaPlanLengthStat.getAttributeValue("counter"));
                                    planLengthCounter++;
                                    eaPlanLengthStat.setAttribute("counter", Integer.toString(planLengthCounter));
                                }
                            }
                            
                            //3.plancost
                            Element eaPlanCostStat = eaStat.getChild("plancost");
                            String planCostStr = eaPlanCostStat.getAttributeValue("temp");
                            if (!planCostStr.trim().equals("")){
                                //if this model/project reached the best plan cost then score it
                                double thePlanCost = Double.parseDouble(planCostStr);
                                if (thePlanCost == bestPlanCost) {
                                    int planCostCounter = Integer.parseInt(eaPlanCostStat.getAttributeValue("counter"));
                                    planCostCounter++;
                                    eaPlanCostStat.setAttribute("counter", Integer.toString(planCostCounter));
                                }
                            }

                            //4.evaluation
                            Element eaEvaluationStat = eaStat.getChild("evaluation");
                            String planEvaluationStr = eaEvaluationStat.getAttributeValue("temp");
                            if (!planEvaluationStr.trim().equals("")){
                                //if this model/project reached the best evaluation then score it
                                double theEvaluation = Double.parseDouble(planEvaluationStr);
                                if (theEvaluation == bestEvaluation) {
                                    int evaluationCounter = Integer.parseInt(eaEvaluationStat.getAttributeValue("counter"));
                                    evaluationCounter++;
                                    eaEvaluationStat.setAttribute("counter", Integer.toString(evaluationCounter));
                                }
                            }



                        }

                    }


                    metricTable.append("</table>");
                    metricTable.append("<br>");
                    //System.out.print(metricTable.toString());


                    //Analyis of the collected data
                    if (projectsStatistics.size() > 0){


                        //CHART FOR THE PROBLEM

                        /*
                        //time alone - Pie graph (google)
                        StringBuilder timeChart = new StringBuilder();
                        String dataName = "timeDataD"+d+"P"+pb;
                        timeChart.append("  var "+dataName+" = new google.visualization.DataTable(); \n");
                        timeChart.append(dataName+".addColumn('string', 'Project'); \n");
                        timeChart.append(dataName+".addColumn('number', 'Times better'); \n");
                        timeChart.append(dataName+".addRows(" + projectsStatistics.size() + "); \n");

                        int sIndex = 0;
                        for (Iterator<Element> it = projectsStatistics.iterator(); it.hasNext();) {
                            Element element = it.next();

                            //time
                            //String pieceName = element.getChildText("name") + " ("+ element.getAttributeValue("id")+ ")";
                            String pieceName = "project "+ element.getAttributeValue("id");
                            timeChart.append(dataName+".setValue("+sIndex+", 0, '"+pieceName+"'); \n");
                            timeChart.append(dataName+".setValue("+sIndex+", 1, "+element.getChild("time").getAttributeValue("counter")+"); \n");
                            sIndex++;
                        }
                        String chartName = "timeChartD"+d+"P"+pb;
                        timeChart.append("var " + chartName + " = new google.visualization.PieChart(document.getElementById('time-graph-" + chartName + "'));\n");
                        timeChart.append(chartName + ".draw("+  dataName + ", {width: 400, height: 240, is3D: true, title:'Best Time Performance'});\n\n");
                        script.append(timeChart);
                        */



                        //Overview - Column Chart (google)
                        StringBuilder overviewChart = new StringBuilder();
                        String odataName = "dataD"+d+"P"+pb;
                        overviewChart.append("  var "+odataName+" = new google.visualization.DataTable(); \n");
                        overviewChart.append(odataName+".addColumn('string', 'Criteria'); \n");

                        StringBuilder overAllTimeData = new StringBuilder();
                        StringBuilder overAllPlanLengthData = new StringBuilder();
                        StringBuilder overAllEvaluationData = new StringBuilder();
                        int oIndex = 1;
                        for (Iterator<Element> it = projectsStatistics.iterator(); it.hasNext();) {
                            Element element = it.next();

                            //columns
                            //String pieceName = element.getChildText("name") + " ("+ element.getAttributeValue("id")+ ")";
                            String colName = "project "+ element.getAttributeValue("id");
                            overviewChart.append(odataName+".addColumn('number', '"+colName+"'); \n");


                            //Gathering data
                            overAllTimeData.append(odataName+".setValue(0, "+oIndex+", "+element.getChild("time").getAttributeValue("counter")+"); \n");
                            overAllPlanLengthData.append(odataName+".setValue(1, "+oIndex+", "+element.getChild("planlength").getAttributeValue("counter")+"); \n");
                            overAllEvaluationData.append(odataName+".setValue(2, "+oIndex+", "+element.getChild("plancost").getAttributeValue("counter")+"); \n");
                            overAllEvaluationData.append(odataName+".setValue(3, "+oIndex+", "+element.getChild("evaluation").getAttributeValue("counter")+"); \n");

                            //TODO: other criteria

                            oIndex++;
                        }

                        //Row is equals to the number of criteria (time, plan length, plancost, evaluation,...)
                        overviewChart.append(odataName+".addRows(4); \n");

                        //1.Time
                        overviewChart.append(odataName+".setValue(0, 0, 'Time'); \n");
                        overviewChart.append(overAllTimeData);

                        //2.Plan Length
                        overviewChart.append(odataName+".setValue(1, 0, 'Plan Length'); \n");
                        overviewChart.append(overAllPlanLengthData);

                        //3.Cost
                        overviewChart.append(odataName+".setValue(2, 0, 'Plan Cost'); \n");
                        overviewChart.append(overAllEvaluationData);

                        //3.Evaluation
                        overviewChart.append(odataName+".setValue(3, 0, 'Evaluation'); \n");
                        overviewChart.append(overAllEvaluationData);

                        //TODO: other criteria


                        String ochartName = "chartD"+d+"P"+pb;
                        overviewChart.append("var " + ochartName + " = new google.visualization.ColumnChart(document.getElementById('analysis-graph-" + ochartName + "'));\n");
                        overviewChart.append(ochartName + ".draw("+  odataName + ", {width: 750, height: 240, is3D: true, title:'Project Performance'});\n\n");

                        script.append(overviewChart);



                        //Addin the content below the table
                        //metricTable.append("<div id=\"time-graph-" + chartName + "\"></div> \n");
                        metricTable.append("<div id=\"analysis-graph-" + ochartName + "\"></div> \n");
                        metricTable.append("<br><br>");




                        //OVERALL STATISTICS - GATHERING DATA

                        //Check which project had the highest score in each criteria
                        
                        int bestTimeCounter = 0;
                        int bestPlanLengthCounter = 0;
                        int bestPlanCostCounter = 0;
                        int bestEvaluationCounter = 0;

                        //Clear overall temp values
                        for (int i = 0; i < OverAllProjectsStatistics.size(); i++) {
                             Element eaOverAllPrjSt = OverAllProjectsStatistics.get(i);
                             eaOverAllPrjSt.getChild("time").setAttribute("temp","");
                             eaOverAllPrjSt.getChild("planlength").setAttribute("temp","");
                             eaOverAllPrjSt.getChild("plancost").setAttribute("temp","");
                             eaOverAllPrjSt.getChild("evaluation").setAttribute("temp","");
                        }
                                
                        //Check the highest values (counters)
                        for (int i = 0; i < projectsStatistics.size(); i++) {
                            Element eaPrjSt = projectsStatistics.get(i);
                            Element eaOverAllPrjSt = OverAllProjectsStatistics.get(i);

                            //1. Time
                            int timeCounter = Integer.parseInt(eaPrjSt.getChild("time").getAttributeValue("counter"));
                            eaOverAllPrjSt.getChild("time").setAttribute("temp", Integer.toString(timeCounter));
                            if (timeCounter > bestTimeCounter){bestTimeCounter = timeCounter;}

                            //2. Plan length
                            int planLengthCounter = Integer.parseInt(eaPrjSt.getChild("planlength").getAttributeValue("counter"));
                            eaOverAllPrjSt.getChild("planlength").setAttribute("temp", Integer.toString(planLengthCounter));
                            if (planLengthCounter > bestPlanLengthCounter){bestPlanLengthCounter = planLengthCounter;}

                            //3. Plan Cost
                            int planCostCounter = Integer.parseInt(eaPrjSt.getChild("plancost").getAttributeValue("counter"));
                            eaOverAllPrjSt.getChild("plancost").setAttribute("temp", Integer.toString(planCostCounter));
                            if (planCostCounter > bestPlanCostCounter){bestPlanCostCounter = planCostCounter;}

                            //4. Evaluation
                            int evaluationCounter = Integer.parseInt(eaPrjSt.getChild("evaluation").getAttributeValue("counter"));
                            eaOverAllPrjSt.getChild("evaluation").setAttribute("temp", Integer.toString(evaluationCounter));
                            if (evaluationCounter > bestEvaluationCounter){bestEvaluationCounter = evaluationCounter;}
                        }
                        
                        //Score the best project concerning the criteria
                        for (int i = 0; i < OverAllProjectsStatistics.size(); i++) {
                             Element eaOverAllPrjSt = OverAllProjectsStatistics.get(i);

                             //1. Time
                             int theTimeCounter = Integer.parseInt(eaOverAllPrjSt.getChild("time").getAttributeValue("temp"));
                             if (theTimeCounter == bestTimeCounter && bestTimeCounter > 0) {
                                int timeCounter = Integer.parseInt(eaOverAllPrjSt.getChild("time").getAttributeValue("counter"));
                                timeCounter++;
                                eaOverAllPrjSt.getChild("time").setAttribute("counter", Integer.toString(timeCounter));
                             }

                             //2.Plan length
                             int thePlanLengthCounter = Integer.parseInt(eaOverAllPrjSt.getChild("planlength").getAttributeValue("temp"));
                             if (thePlanLengthCounter == bestPlanLengthCounter && bestPlanLengthCounter > 0) {
                                int planlengthCounter = Integer.parseInt(eaOverAllPrjSt.getChild("planlength").getAttributeValue("counter"));
                                planlengthCounter++;
                                eaOverAllPrjSt.getChild("planlength").setAttribute("counter", Integer.toString(planlengthCounter));
                             }

                             //3. Plan Cost
                             int thePlanCostCounter = Integer.parseInt(eaOverAllPrjSt.getChild("plancost").getAttributeValue("temp"));
                             if (thePlanCostCounter == bestPlanCostCounter && bestPlanCostCounter > 0) {
                                int eplancostCounter = Integer.parseInt(eaOverAllPrjSt.getChild("plancost").getAttributeValue("counter"));
                                eplancostCounter++;
                                eaOverAllPrjSt.getChild("plancost").setAttribute("counter", Integer.toString(eplancostCounter));
                             }

                             //4. Evaluation
                             int theEvaluationCounter = Integer.parseInt(eaOverAllPrjSt.getChild("evaluation").getAttributeValue("temp"));
                             if (theEvaluationCounter == bestEvaluationCounter && bestEvaluationCounter > 0) {
                                int evaluationCounter = Integer.parseInt(eaOverAllPrjSt.getChild("evaluation").getAttributeValue("counter"));
                                evaluationCounter++;
                                eaOverAllPrjSt.getChild("evaluation").setAttribute("counter", Integer.toString(evaluationCounter));
                             }

                        }



                    }




                    
                }

                // refresh the status bar
                int progressPercentage = (int)((double)++progressIndex/(double)numberOfProblems * 100);
                status.setText("Status: Generating report... ("+ progressPercentage +"%)");

            }

        }



        //Adding overall analysis to the scripts

        //OverAll Analysis - Column Chart (google)
        StringBuilder overAllChart = new StringBuilder();

        if (OverAllProjectsStatistics.size() > 1){
            
            //Overall Analysis as :          
            //Column Bar (google)
            String dataName = "dataOverAll";
            overAllChart.append("  var "+dataName+" = new google.visualization.DataTable(); \n");
            overAllChart.append(dataName+".addColumn('string', 'Criteria'); \n");

            StringBuilder overAllTimeData = new StringBuilder();
            StringBuilder overAllPlanLengthData = new StringBuilder();
            StringBuilder overAllPlanCostData = new StringBuilder();
            StringBuilder overAllEvaluationData = new StringBuilder();
            int oIndex = 1;
            for (int i = 0; i < OverAllProjectsStatistics.size(); i++) {
                Element element = OverAllProjectsStatistics.get(i);

                //columns
                //String pieceName = element.getChildText("name") + " ("+ element.getAttributeValue("id")+ ")";
                String colName = "project "+ element.getAttributeValue("id");
                overAllChart.append(dataName+".addColumn('number', '"+colName+"'); \n");


                //Gathering data
                overAllTimeData.append(dataName+".setValue(0, "+oIndex+", "+element.getChild("time").getAttributeValue("counter")+"); \n");
                overAllPlanLengthData.append(dataName+".setValue(1, "+oIndex+", "+element.getChild("planlength").getAttributeValue("counter")+"); \n");
                overAllPlanCostData.append(dataName+".setValue(2, "+oIndex+", "+element.getChild("plancost").getAttributeValue("counter")+"); \n");
                overAllEvaluationData.append(dataName+".setValue(3, "+oIndex+", "+element.getChild("evaluation").getAttributeValue("counter")+"); \n");

                //TODO: other criteria

                oIndex++;

            }

            //Row is equals to the number of criteria (time, plan length, plan cost, evaluation,...)
            overAllChart.append(dataName+".addRows(4); \n");

            //1. Time
            overAllChart.append(dataName+".setValue(0, 0, 'Time'); \n");
            overAllChart.append(overAllTimeData);

            //2. Plan Length
            overAllChart.append(dataName+".setValue(1, 0, 'Plan Length'); \n");
            overAllChart.append(overAllPlanLengthData);

            //3. Plan Cost
            overAllChart.append(dataName+".setValue(2, 0, 'Plan Cost'); \n");
            overAllChart.append(overAllPlanCostData);

            //4. Evaluation
            overAllChart.append(dataName+".setValue(3, 0, 'Evaluation'); \n");
            overAllChart.append(overAllEvaluationData);

            //TODO: other criteria


            String chartName = "chartOverAll";
            overAllChart.append("var " + chartName + " = new google.visualization.ColumnChart(document.getElementById('analysis-graph-overall'));\n");
            overAllChart.append(chartName + ".draw("+  dataName + ", {width: 750, height: 300, is3D: true, title:'Overall Project Performance'});\n\n");

            script.append(overAllChart);
        }





        
        //finishing script (google)
        script.append("} \n");
        script.append("           </script>\n");



        //HTML
        //1. HEAD
        html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"> \n");
        html.append("<html xmlns=\"http://www.w3.org/1999/xhtml\"> \n");
        html.append("<head> \n");
        html.append("	<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" /> \n");
        html.append("	<title>Planners Comparison Report</title> \n");
        html.append("	<link href=\"default.css\" rel=\"stylesheet\" type=\"text/css\" /> \n");
        html.append("	<script type=\"text/javascript\" src=\"http://www.google.com/jsapi\"></script> \n");

        html.append(script);
        html.append("</head> \n");


        //2. BODY
        html.append("<body> \n");

        html.append("	<div id=\"header\"> \n");
        html.append("		<div id=\"logo\"> \n");
        html.append("			<h1><span><a href=\"#\">project</a></span><a href=\"#\"> Comparison Report</a></h1>\n");
        html.append("			<h2><a href=\"#\">By itSIMPLE</a></h2>  \n");
        html.append("		</div> \n");
        html.append("		<div id=\"menu\"> \n");
        html.append("			<ul> \n");
        html.append("				<li class=\"first\"><a href=\"#metrics\" accesskey=\"1\" title=\"\">Metrics Anaysis</a></li> \n");
        //html.append("				<li><a href=\"#metrics\" accesskey=\"2\" title=\"\">Metrics Anaysis</a></li> \n");
        html.append("				<li><a href=\"#about\" accesskey=\"4\" title=\"\">About</a></li> \n");
        html.append("			</ul> \n");
        html.append("		</div> \n");
        html.append("	</div> \n");
        html.append("	<div id=\"splash\"><a href=\"#\"><img src=\"images/img4.jpg\" alt=\"\" width=\"877\" height=\"140\" /></a></div> \n");


        //Content
        //2.2 Introduction
        //add script that contains all tables and graphs
        html.append("	<div id=\"content\"> \n");
        html.append("		<div id=\"colOne\" style=\"width:100%\"> \n");
        html.append("		<h2 style=\"margin-top:0\"><a name=\"#\">Introduction</a></h2> \n");
        html.append("		<p>This <strong>project comparison report</strong> is a analysis interface for helping designers investigate the difference between models of the same domain.</p> \n");

        //2.2 Overall Analysis/Comparison
        if (OverAllProjectsStatistics.size() > 1){
            html.append("		<h2 style=\"margin-top:0\"><a name=\"#\">Overall Metric Analysis</a></h2> \n");
            html.append("<div id=\"analysis-graph-overall\"></div> \n");
            html.append("<br><br>");
        }

        //Metrics
        html.append("       <h2><a name=\"metrics\">Detailed Metrics Analysis</a></h2>\n");
        String metricsSummary = metricTable.toString();
        if (!metricsSummary.trim().equals("")){            
            html.append(metricTable);
        }
        else{
            html.append("		<p>No metrics were defined for the base/reference project.</p> \n");
            
        }


        //End of Content
        html.append("   </div> \n");



        //2.5 footer
        html.append("<div id=\"footer\"> \n");
        html.append("	<p>(c) 2010 itSIMPLE. Design by <a href=\"http://dlab.poli.usp.br/\">itSIMPLE team</a>.</p> \n");
        html.append("</div> \n");

        html.append("</body> \n");
        html.append("</html>");
        
        

        status.setText("Status: Done generating report!");
        ItSIMPLE.getInstance().appendOutputPanelText(" (!) Report generated! \n");

        return html.toString();
    }






    /**
     * This method generates a random color hex code
     * @return
     */
    public static String randomColor(){
        Random random = new Random();
        String color = "";
        for (int i = 0; i < 7; i++) {
            String current = Integer.toHexString(random.nextInt(256));
            color += current.substring(0, 1);

        }
        return color.toUpperCase();
    }













 /**
     * This is a provisory methos for counting the actions move, firelaser, and detonate bomb of the GoldMiner domain
     */
    public static String myAnalysis(Element planners, Element dataSet){

        String result ="";

//        List<String> plannersName = new ArrayList<String>();
//        List<String> plannersVersion = new ArrayList<String>();
//              
//        plannersName.add("SGPlan 5");
//        plannersVersion.add("5.2.2 Linux");
//        
//        plannersName.add("SGPlan 5");
//        plannersVersion.add("5.2.2 Linux");
        
        System.out.println("Planner - Version, Time, # Solved");
        
      
        
        for (Iterator it = planners.getChildren().iterator(); it.hasNext();) {
            Element planner = (Element)it.next();
            
            float totalTime = 0;
            int problemsSolved = 0;
            
            List<Element> plannerData = null;
            try {
                    XPath path = new JDOMXPath("project/domains/domain/problems/problem/plans/xmlPlan[planner/name='"+planner.getChildText("name")+"' and planner/version='"+planner.getChildText("version")+"']");
                    plannerData = path.selectNodes(dataSet);
            } catch (JaxenException e) {
                    e.printStackTrace();
            } 
            
            if (plannerData != null && plannerData.size() > 0){
                
                for (Iterator<Element> it1 = plannerData.iterator(); it1.hasNext();) {
                    Element plan = it1.next();
                                       
                    
                    int planLength = plan.getChild("plan").getChildren().size();
                    boolean isValid = true;

                    //check the validity of the plan
                    Element validity = plan.getChild("validity");
                    if (validity != null && !validity.getAttributeValue("isValid").equals("true")){
                        planLength = 0;
                        isValid = false;
                    }

                    //Counting problems solved
                    if(planLength > 0 && isValid){
                        problemsSolved++;
                    }                    

                    //Couting time (for all cases - solving a proble, timeout, invalid plan, etc,
                    String toolTimeStr = plan.getChild("statistics").getChildText("toolTime");
                    float toolTime = Float.parseFloat(toolTimeStr);
                    totalTime += toolTime;
                    
                }

                //Print data
                System.out.println(planner.getChildText("name") + " - " + planner.getChildText("version") + ", "+ Float.toString(totalTime) + ", "+ problemsSolved);
                
            }

        }
        /*
         * Analysis of FF and Metric FF in the Storage Case study (PhD) - Enforced Hill Climbed Failing
        System.out.println();
        System.out.println();
        
        System.out.println("FF Results:");
        System.out.println();
        
        List<Element> plannerData = null;
        try {
            //XPath path = new JDOMXPath("project/domains/domain/problems/problem/plans/xmlPlan[planner/name='FF 2.3']");
            XPath path = new JDOMXPath("project/domains/domain/problems/problem/plans/xmlPlan[planner/name='Metric-FF']");            
            plannerData = path.selectNodes(dataSet);
        } catch (JaxenException e) {
                e.printStackTrace();
        } 

        if (plannerData != null && plannerData.size() > 0){

            for (Iterator<Element> it1 = plannerData.iterator(); it1.hasNext();) {
                Element plan = it1.next();


                int planLength = plan.getChild("plan").getChildren().size();
                boolean isValid = true;

                //check the validity of the plan
                Element validity = plan.getChild("validity");
                if (validity != null && !validity.getAttributeValue("isValid").equals("true")){
                    planLength = 0;
                    isValid = false;
                }
                                

                //Counting problems solved
                if(planLength > 0 && isValid){
                    
                }                 
                
                boolean nooutput = false;
                String consoleOutput = plan.getChild("planner").getChildText("consoleOutput");
                if (consoleOutput == null){
                    consoleOutput = "";
                    nooutput = true;
                }
               
                boolean ehcFailed = false;
                int ehcFailedInt = 0;
                if (consoleOutput.contains("Enforced Hill-climbing failed")){
                    ehcFailed = true;
                    ehcFailedInt = 1;
                }
                String problem = plan.getChildText("problem");
                //Print
                if (!problem.equals("storage-10") && !problem.equals("storage-16") && !problem.equals("storage-20")){
                    //System.out.println(plan.getChildText("problem") + ", "+ planLength + ", "+ isValid +", "+ehcFailedInt);
                    System.out.println(planLength + ", "+ isValid +", "+ehcFailedInt);
                }
                
                
                if (nooutput){
                     //System.out.println("No output!");
                }


            }

            //Print data
            //System.out.println(planner.getChildText("name") + " - " + planner.getChildText("version") + ", "+ Float.toString(totalTime) + ", "+ problemsSolved);

        }

        */
        
        
        
 
       
        return result;

    }




    


    /**
     * This is a provisory method for counting the actions move, firelaser, and detonate bomb of the GoldMiner domain
     */
    public static String actionCounterGoldMiner(Element xmlPlan){
        String actionCounter = "";
        //1. Count move action
        List<?> moveActions = null;
        try {
                XPath path = new JDOMXPath("plan/action[@id='MOVE' or @id='move']");
                moveActions = path.selectNodes(xmlPlan);
        } catch (JaxenException e) {
                e.printStackTrace();
        }

        actionCounter = "Move: " + Integer.toString(moveActions.size()) + "\n";
        System.out.println("Move: "+ Integer.toString(moveActions.size()));
        
        //2. Count firelaser action
        List<?> fireActions = null;
        try {
                XPath path = new JDOMXPath("plan/action[@id='FIRELASER' or @id='firelaser']");
                fireActions = path.selectNodes(xmlPlan);
        } catch (JaxenException e) {
                e.printStackTrace();
        }

        actionCounter += "Laser: "+ Integer.toString(fireActions.size()) + "\n";
        System.out.println("Laser: "+ Integer.toString(fireActions.size()));
        
        //2. Count firelaser action
        List<?> detonateActions = null;
        try {
                XPath path = new JDOMXPath("plan/action[@id='DETONATEBOMB' or @id='detonatebomb']");
                detonateActions = path.selectNodes(xmlPlan);
        } catch (JaxenException e) {
                e.printStackTrace();
        }
        
        System.out.println("Bomb: "+ Integer.toString(detonateActions.size()));
        System.out.println("");

        actionCounter += "Bomb: "+ Integer.toString(detonateActions.size()) + "\n \n";
       
        return actionCounter;

    }




}