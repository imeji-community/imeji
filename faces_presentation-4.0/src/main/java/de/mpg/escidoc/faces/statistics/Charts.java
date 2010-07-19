package de.mpg.escidoc.faces.statistics;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.myfaces.trinidad.model.ChartModel;

import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;

public class Charts  extends ChartModel
{
    List<StatisticReportRecordVO> statiscticRecordFW = null;
    List<String> seriesLabels = null;
    StatisticsHelper statisticsHelper = null;
    int chartSize = 12;
    String chartName = "Statistics";
    private List<String> labels = null; 
    private Integer month = null;
    private Integer year = null;
  
	/**
     * Create one chart for one year according to the statistics from FW
     * @param statiscticRecordFW
     * @param year
     */
    public Charts(List<StatisticReportRecordVO> statiscticRecordFW, String chartName, int month, int year)
    {
        this.statiscticRecordFW = statiscticRecordFW;
        
        if (month != 0) 
        {
        	this.month = month;
	}
        if (year != 0) 
        {
        	 this.year = year;
	}
        statisticsHelper =  new StatisticsHelper(this.statiscticRecordFW, this.month, this.year);
        this.chartName = chartName;
       
        initGroupLabels();
    }
    
    /**
     * Initialize the group labels
     */
    public void initGroupLabels()
    {
    	labels = new ArrayList<String>();
    	
    	if (month == null || year == null) 
    	{
    	    if ( this.getDateOfLastStatistics() != null)
    	    {
    		 labels.add(this.getDateOfLastStatistics());
    	    }
	}
    	else
    	{
    		labels.add(month + "/" + year);
    	}
        
        // Extract the month with Statistics value
        if (labels.size() > 0)
        {
            // Complete the list with next months
            if (labels.size() < chartSize)
            {
                String [] firstLabelSplited = labels.get(0).split("/");
                int firstMonth = Integer.parseInt(firstLabelSplited[0]);
                int firstYear =  Integer.parseInt(firstLabelSplited[1]);
                int size = labels.size();
                
                for (int i = 0; i < chartSize - size; i++)
                {
                    if (firstMonth - i > 1 )
                    {
                        labels.add(0, (firstMonth - i -1) + "/" + firstYear);
                    }
                    else
                    {
                        firstMonth = 12 + i + 1;
                        firstYear = firstYear - 1;
                        labels.add(0, "12/" + firstYear);
                    }
                }
            }
        }
        else
        {
            for (int i = 0; i < chartSize; i++)
            {
                labels.add("0");
            }
        }
    }
    
    /**
     * Returns the date of the last (newest) statistics stored according to the {@link StatisticReportRecordVO}
     * @return
     */
    public String getDateOfLastStatistics()
    {
    	List<Calendar> dateList = new ArrayList<Calendar>();
         
    	List<String> date = new ArrayList<String>(); 
    	
    	if (statiscticRecordFW.size() > 0)
         {
             for (int i = 0; i < statiscticRecordFW.size(); i++)
             {
                 String month = null;
                 String year = null;
                
                 for (int j = 0; j < statiscticRecordFW.get(i).getParamList().size(); j++)
                 {
                     if ("month".equals(statiscticRecordFW.get(i).getParamList().get(j).getName()))
                     {
                         month = statiscticRecordFW.get(i).getParamList().get(j).getParamValue().getValue();
                     }
                     if ("year".equals(statiscticRecordFW.get(i).getParamList().get(j).getName()))
                     {
                         year = statiscticRecordFW.get(i).getParamList().get(j).getParamValue().getValue();
                     }
                     if (month != null && year != null)
                     {
                         Calendar calendar = new GregorianCalendar();
                         calendar.set(Integer.parseInt(year), (Integer.parseInt(month) - 1), 1);
                         
                         if (!date.contains(month + "/" + year))
                         {
                             date.add(month + "/" + year);
                             dateList.add(calendar);
                         }
                     }
                 }
             }
             // Sort the list of month with statistics value
             Collections.sort(dateList);
             
             // Take the first month of the list
             SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/yyyy");
             
             return simpleDateFormat.format(dateList.get(dateList.size() - 1).getTime());
         }    
          
    	return null;
    }
    
   /**
    * Create a list with all the date having at least one request.
    */
    public List<String> getGroupLabels()
    {
        return labels;
    }

    /**
     * Create a list with the months number.
     */
    public List<String> getSeriesLabels()
    {
        seriesLabels = new ArrayList<String>(); 
        seriesLabels.add(chartName);
        return seriesLabels;
    }

    
    public List<List<Double>> getYValues()
    {
        List<List<Double>> chartValues = new ArrayList<List<Double>>();
        
          // iterate over the groups (years...)  
         for(int i = 0; i < labels.size(); i++)  
         {  
               List<Double> numbers = new ArrayList<Double>();  
               numbers.add(getNumberOfRequests(getGroupLabels().get(i))); 
               chartValues.add(numbers);  
         }
         
        return chartValues;
    }
    
    public List<String> getRequests()
    {
        return seriesLabels;
        
    }
    
    /**
     * Calculate the number of requests per month for each year of the current statiscticRecordFW
     * @param month
     * @param year
     * @return number of requests.
     */

    private Double getNumberOfRequests(String label)
    {
        Double numberOfRequests = new Double(0);
        Boolean monthBoolean = false;
        Boolean yearBoolean = false;
        
        if (!"".equals(label))
        {
            String[] labelSplited = label.split("/");
            
            String month  = labelSplited[0];
            String year = labelSplited[1];
                
            for (int i = 0; i < statiscticRecordFW.size(); i++)
            {
            	String monthtest = null;
            	String yeartest = null;
                for (int j = 0; j < statiscticRecordFW.get(i).getParamList().size(); j++)
                {
                    if (month.equals(statiscticRecordFW.get(i).getParamList().get(j).getParamValue().getValue().toString())
                            && "month".equals(statiscticRecordFW.get(i).getParamList().get(j).getName()))
                    {
                        monthBoolean = true;
                    }
                    if (year.equals(statiscticRecordFW.get(i).getParamList().get(j).getParamValue().getValue().toString())
                            && "year".equals(statiscticRecordFW.get(i).getParamList().get(j).getName()))
                    {
                        yearBoolean = true;
                    }
                }                
                
                if (yearBoolean && monthBoolean)
                {
                	for (int j = 0; j < statiscticRecordFW.get(i).getParamList().size(); j++)
                    {   
                        if ("requests".equals(statiscticRecordFW.get(i).getParamList().get(j).getName()))
                        {
                              numberOfRequests = numberOfRequests + 
                                  Double.parseDouble(statiscticRecordFW.get(i).getParamList().get(j).getParamValue().getValue());
                        }
                    }
                }
                yearBoolean = false;
                monthBoolean = false;
            }
        }
        else
        {
            numberOfRequests = new Double(0);
        }
        
        return numberOfRequests;
    }

    public List<StatisticReportRecordVO> getStatiscticRecordFW()
    {
        return statiscticRecordFW;
    }

    public void setStatiscticRecordFW(List<StatisticReportRecordVO> statiscticRecordFW)
    {
        this.statiscticRecordFW = statiscticRecordFW;
    }

    public void setSeriesLabels(List<String> seriesLabels)
    {
        this.seriesLabels = seriesLabels;
    }

    /**
     * Define the colors of the charts
     */
    public List<Color> getSeriesColors()
    {
        List<Color> colors = new ArrayList<Color>();
        Color green = new Color(0,100,0);

        for (int i = 0; i < getSeriesLabels().size(); i++)
        {
            colors.add(green);
        }
        
        return colors;
    }

    @Override
    public String getFootNote()
    {
        return "Powered by Escidoc.";
    }

    public int getChartSize()
    {
        return chartSize;
    }

    public void setChartSize(int chartSize)
    {
        this.chartSize = chartSize;
    }
    
    public int getLastMonth()
    {
    	return Integer.parseInt(labels.get(0).split("/")[0]);
    }
    
    public int getFirstMonth()
    {
    	return Integer.parseInt(labels.get(labels.size() - 1).split("/")[0]);
    }
    
    public int getLastYear()
    {
    	return Integer.parseInt(labels.get(0).split("/")[1]);
    }
    
    public int getFirstYear()
    {
    	return Integer.parseInt(labels.get(labels.size() - 1).split("/")[1]);
    }

}
