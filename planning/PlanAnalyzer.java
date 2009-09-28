/***
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
*
* Copyright (C) 2007,2008,2009 Universidade de Sao Paulo
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


package planning;

import itSIMPLE.ItSIMPLE;
import java.awt.Color;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import languages.xml.XMLUtilities;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.nfunk.jep.JEP;

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


        if (weights > 0){
            overallGrade = upExpression/weights;
        }

        return overallGrade;
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

        Element dataset = metric.getChild("dataset");


        //Case: FINAL VALUE
        int lastIndex = dataset.getChildren().size()-1;
        Element finalSet = (Element)dataset.getChildren().get(lastIndex);

        float finalValue = 0;
        try {
            finalValue = Float.parseFloat(finalSet.getAttributeValue("value"));
        } catch (Exception e) {
            System.out.println("System could not parse the last value");
            return 0;
        }

        //TODO: CASE min

        //TODO: CASE max

        //TODO: CASE average

                     
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



    public static String generatePlanReport(Element domain, Element problem, Element xmlPlan, Element metrics){
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

                //TODO: select the name of the chart. Variable, expression, actionCounter.
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


            dataXML = "<graph caption='Joint view of metrics' xAxisName='value' yAxisName='Steps' decimalPrecision='2' ";
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
        htmlfooter += "	<p>(c) 2009 itSIMPLE. Design by <a href=\"http://dlab.poli.usp.br/\">itSIMPLE team</a>.</p> \n";
        htmlfooter += "</div> \n";



        //complete html
        html += htmlhead;
        html += htmlcontent;
        html += htmlfooter;

        html += " </body> \n";
        html += "</html>" ;


        return html;
    }

    /**
     * This method generates a html for set of plans
     * @param generalXml
     * @return
     */
    public static String generateReport(Element generalXml){
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

        html.append("<table bgcolor=\"#EEEEEE\" cellpadding=\"5\" cellspacing=\"1\" width=\"100%\">\n");
        html.append("<tr style=\"background-color:#000066; color:#FFFFFF; height:35px;\" >\n");
        if (projects.size()>1){html.append("<th>Projects</th>\n");}
        html.append("<th>Domains</th>\n");
        html.append("<th>Problems</th>\n");
        html.append("<th>Planners</th>\n");
        html.append("<th width=\"50px\">Time</th>\n");
        html.append("<th width=\"50px\">Steps</th>\n");
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
                         if(plan.getChild("plan").getChildren().size() < 1){
                             html.append("<td bgcolor=\"#FFFFFF\" colspan=\"2\"><em>no plan found</em></td>\n");
                         }
                         else{
                            html.append("<td bgcolor=\"#FFFFFF\" align=\"center\">" + plan.getChild("statistics").getChildText("toolTime") + "</td>\n");
                            html.append("<td bgcolor=\"#FFFFFF\" align=\"center\">" + plan.getChild("plan").getChildren().size() + "</td>\n");
                         }
                         html.append("</tr>\n");
                     }

                }

            }

        }

        html.append("</table>\n");
        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }

    public static String generatePlannersComparisonReport(Element generalXml){

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

        //Content
        //2.1 Introduction
        html.append("	<div id=\"content\"> \n");
        html.append("		<div id=\"colOne\" style=\"width:100%\"> \n");
        html.append("		<h2 style=\"margin-top:0\">Introduction</h2> \n");
        html.append("		<p>This <strong>plan comparison report</strong> is a plan analysis interface for helping designers investigate solutions provided by automated planners.</p> \n");

        //2.2 Comparison Table
        html.append("           <h2><a name=\"table\">Comparison Table</a></h2>\n");

        html.append("           <script type='text/javascript'>\n");
        html.append("               google.load('visualization', '1', {packages:['table','linechart','columnchart']})\n");
        html.append("               google.setOnLoadCallback(drawTable)\n\n");
        html.append("               function drawTable() {\n");
        html.append("                   var data = new google.visualization.DataTable();\n");
        html.append("                   data.addColumn('string', 'Projects');\n");
        html.append("                   data.addColumn('string', 'Domains');\n");
        html.append("                   data.addColumn('string', 'Problems');\n");
        html.append("                   data.addColumn('string', 'Planners');\n");
        html.append("                   data.addColumn('number', 'Time');\n");
        html.append("                   data.addColumn('number', 'Steps');\n");

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
                         cells.append("data.setCell(" + number_of_rows + ",4," + plan.getChild("statistics").getChildText("toolTime") + ");\n");
                         if(plan.getChild("plan").getChildren().size() < 1) {
                            cells.append("data.setCell(" + number_of_rows + ",5,-1,' ');\n");
                         }
                         else{
                            cells.append("data.setCell(" + number_of_rows + ",5," + plan.getChild("plan").getChildren().size() + ");\n");
                         }
                         number_of_rows++;
                     }
                    problemId++;
                }
            }
        }

        html.append("data.addRows(" + number_of_rows + ");");
        html.append(cells.toString());
        html.append("               var table = new google.visualization.Table(document.getElementById('comparison-table'));\n");
        html.append("               table.draw(data, {showRowNumber: true,width:'100%'});\n");
        html.append("               drawChart();\n");
        html.append("}\n");



        //2.3 Comparison Graphs/Charts
        html.append("function drawChart(){\n");

        int data = 0;
        StringBuilder graphs_div = new StringBuilder();

        /*
         * Grouping graphs by project (project->domain->problems)
         */

        for(int p=0;p<projects.size();p++){
            Element project = projects.get(p);

            graphs_div.append("<h3>"+project.getChildText("name")+"</h3>");

            List<Element> domains = project.getChild("domains").getChildren("domain");

            //get the fist problem as a reference for creating the graph
            Element firstProblemAsReference = (Element)domains.get(0).getChild("problems").getChildren("problem").get(0);            
            List<Element> plans =firstProblemAsReference.getChild("plans").getChildren("xmlPlan");

            //get the amount of problems in the project
            List<?> totalProblems = null;
            try {
                    XPath path = new JDOMXPath("domains/domain/problems/problem");
                    totalProblems = path.selectNodes(project);
            } catch (JaxenException e) {
                    e.printStackTrace();
            }



            //Graph: Number of actions x Planners
            html.append("var data" + data + "S= new google.visualization.DataTable();\n");
            html.append("data" + data + "S.addColumn('string', 'Problems');\n");
            for(int pl=0;pl<plans.size();pl++){
                 Element plan = (Element)plans.get(pl);
                 html.append("data" + data + "S.addColumn('number', '"+ plan.getChild("planner").getChildText("name") + " - " + plan.getChild("planner").getChildText("version") +"');\n");
            }
            html.append("data" + data + "S.addRows("+totalProblems.size()+");\n");
            int probleId = 0;
            for(int d=0;d<domains.size();d++){

                List<Element> problems = domains.get(d).getChild("problems").getChildren("problem");
                for(int pb=0;pb<problems.size();pb++){
                   plans = problems.get(pb).getChild("plans").getChildren("xmlPlan");

                   //html.append("data" + data + "S.setValue("+pb+",0,'P"+pb+"');\n");
                   html.append("data" + data + "S.setValue("+probleId+",0,'P"+probleId+"');\n");
                   for(int pl=0;pl<plans.size();pl++){
                        Element plan = (Element)plans.get(pl);
                        //html.append("data" + data + "S.setValue("+pb+","+(pl+1)+","+ plan.getChild("plan").getChildren().size() +");\n");
                        html.append("data" + data + "S.setValue("+probleId+","+(pl+1)+","+ plan.getChild("plan").getChildren().size() +");\n");
                   }
                   probleId++;
                }

            }
            if (totalProblems.size()==1){
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
            for(int pl=0;pl<plans.size();pl++){
                 Element plan = (Element)plans.get(pl);
                 html.append("data" + data + "T.addColumn('number', '"+ plan.getChild("planner").getChildText("name") + " - " + plan.getChild("planner").getChildText("version") +"');\n");
            }
            html.append("data" + data + "T.addRows("+totalProblems.size()+");\n");
            probleId = 0;
            for(int d=0;d<domains.size();d++){

                List<Element> problems = domains.get(d).getChild("problems").getChildren("problem");
                for(int pb=0;pb<problems.size();pb++){
                   plans = problems.get(pb).getChild("plans").getChildren("xmlPlan");
                   //html.append("data" + data + "T.setValue("+pb+",0,'P"+pb+"');\n");
                   html.append("data" + data + "T.setValue("+probleId+",0,'P"+probleId+"');\n");
                   for(int pl=0;pl<plans.size();pl++){
                        Element plan = (Element)plans.get(pl);
                        //html.append("data" + data + "T.setValue("+pb+","+(pl+1)+","+ (plan.getChild("plan").getChildren().size()>0?plan.getChild("statistics").getChildText("toolTime"):0) +");\n");
                        html.append("data" + data + "T.setValue("+probleId+","+(pl+1)+","+ (plan.getChild("plan").getChildren().size()>0?plan.getChild("statistics").getChildText("toolTime"):0) +");\n");
                   }
                   probleId++;
                }

            }
            if (totalProblems.size()==1){
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
                html.append("data" + data + "S.addColumn('string', 'Problems');\n");

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





        //end of function drawChart
        html.append("}\n");

        html.append("           </script>\n");
        html.append("           <div id=\"comparison-table\"></div>\n");

        
        //2.3 Comparison Graphs
        html.append("           <h2><a name=\"graphs\">Comparison Graphs</a></h2>\n");
        html.append(graphs_div.toString());
        html.append("   </div> \n");

        //2.4 footer
        html.append("<div id=\"footer\"> \n");
        html.append("	<p>(c) 2009 itSIMPLE. Design by <a href=\"http://dlab.poli.usp.br/\">itSIMPLE team</a>.</p> \n");
        html.append("</div> \n");

        html.append("</body> \n");
        html.append("</html>") ;

        return html.toString();
    }




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
                        "<TR><TD><font size=3 face=arial><b>Make Span: </b>"+ statistics.getChildText("makeSpan")+
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
        info += "<TR><TD bgcolor='gray'><font size=3 face=arial color='FFFFFF'>" +
                        "<b>Planner Console Output</b></TD></TR>"+
                        "<TR><TD><font size=4 face=courier>" +
                        planner.getChildText("consoleOutput").replaceAll("\n", "<br>")+"</font><p></TD></TR>";

        info += "</TABLE>";

        info += "</body>\n";
        info += "</html>\n";


     	return info;
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