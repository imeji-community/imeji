package de.mpg.escidoc.faces.statistics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;

public class StatisticsHelper
{
    List<StatisticReportRecordVO> statiscticRecord = null;
    private Integer month = null;
    private Integer year = null;
    
    public StatisticsHelper()
    {
        // TODO Auto-generated constructor stub
    }
    
    public StatisticsHelper( List<StatisticReportRecordVO> statiscticRecord, Integer month, Integer year)
    {
        this.statiscticRecord = statiscticRecord;
        this.year = year;
        this.month = month;
    }
    
    public List<String> getMonths()
    {
        List<String> months = new ArrayList<String>();
        
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");
        
        return months;
    }
    
    public List<String> getYears()
    {
        List<String> years = new ArrayList<String>();
        
        if (statiscticRecord.size() > 0)
        {
            for (int i = 0; i < statiscticRecord.get(0).getParamList().size(); i++)
            {
                if ("year".equals(statiscticRecord.get(0).getParamList().get(i).getName()) 
                        && !years.contains("statiscticRecordFW.get(0).getParamList().get(i).getParamValue().getValue()"))
                {
                    years.add(statiscticRecord.get(0).getParamList().get(i).getParamValue().getValue());
                }
            }
        }
        
        return years;
    }
    
    /**
     * return the date according to the popsition in the table
     * @param pos
     * @return
     */
    public String getDate(int pos)
    {
        List<String> dates = new ArrayList<String>();
        // create a list with all the value of the months
        List<Calendar> dateList = new ArrayList<Calendar>();
        List<String> date = new ArrayList<String>();
//        
//        // Extract the month with statistics value
//        if (statiscticRecord.size() > 0)
//        {
//            for (int i = 0; i < statiscticRecord.size(); i++)
//            {
//                String month = null;
//                String year = null;
//                
//                for (int j = 0; j < statiscticRecord.get(i).getParamList().size(); j++)
//                {
//                    if ("month".equals(statiscticRecord.get(i).getParamList().get(j).getName()))
//                    {
//                        month = statiscticRecord.get(i).getParamList().get(j).getParamValue().getValue();
//                    }
//                    if ("year".equals(statiscticRecord.get(i).getParamList().get(j).getName()))
//                    {
//                        year = statiscticRecord.get(i).getParamList().get(j).getParamValue().getValue();
//                    }
//                    if (month != null && year != null)
//                    {
//                        Calendar calendar = new GregorianCalendar();
//                        calendar.set(Integer.parseInt(year), (Integer.parseInt(month) - 1), 1);
//                        if (!date.contains(month + "/" + year))
//                        {
//                            date.add(month + "/" + year);
//                            dateList.add(calendar);
//                        }
//                    }
//                }
//                month = null;
//                year = null;
//            }
//            // Sort the list of month with statistics value
//            Collections.sort(dateList);
//            
//            // Get the first month
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/yyyy");
//            dates.add(simpleDateFormat.format(dateList.get(dateList.size() -1 ).getTime()));
//            dates.clear();
//            dates.add("5/2010");
//        }
        
        dates.add(this.month + "/" + this.year);
        
        // Return the month wanted.
        if (pos < dates.size())
        {
            return dates.get(pos);
        }
        else if (dates.size() >0)
        {
            String [] firstLabelSplited = dates.get(0).split("/");
            int firstMonth = Integer.parseInt(firstLabelSplited[0]);
            int firstYear =  Integer.parseInt(firstLabelSplited[1]);
            
            if (firstMonth - pos > 0 )
            {
                return (firstMonth - pos) + "/" + firstYear;
            }
            else
            {
                // calculate the year
                firstYear = firstYear - ((pos + 1 - firstMonth) / 12) - 1;
                //calculate the month
                firstMonth = 12 + firstMonth - (pos % 12);
                return firstMonth + "/" + firstYear;
            }
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Return the number of requests for a date.
     * @param date : MM/yyyy
     * @return
     */
    public Double getRequests(String date)
    {
        if (date == null)
        {
            return new Double(0);
        }
        
        String[] dateSplited = date.split("/");
        String month = dateSplited[0];
        String year = dateSplited[1];
        
        Boolean monthBoolean = false;
        Boolean yearBoolean = false;
        
        Double numberOfRequests = new Double(0);
        
        for (int i = 0; i < statiscticRecord.size(); i++)
        {
            for (int j = 0; j < statiscticRecord.get(i).getParamList().size(); j++)
            {
                if (month.equals(statiscticRecord.get(i).getParamList().get(j).getParamValue().getValue().toString())
                        && "month".equals(statiscticRecord.get(i).getParamList().get(j).getName()))
                {
                    monthBoolean = true;
                }
                if (year.equals(statiscticRecord.get(i).getParamList().get(j).getParamValue().getValue().toString())
                        && "year".equals(statiscticRecord.get(i).getParamList().get(j).getName()))
                {
                    yearBoolean = true;
                }
            }
            if (yearBoolean && monthBoolean)
            {
                for (int j = 0; j < statiscticRecord.get(i).getParamList().size(); j++)
                {   
                    if ("requests".equals(statiscticRecord.get(i).getParamList().get(j).getName()))
                    {
                          numberOfRequests = numberOfRequests + 
                              Double.parseDouble(statiscticRecord.get(i).getParamList().get(j).getParamValue().getValue());
                    }
                }
            }
            monthBoolean = false;
            yearBoolean = false;
        }
        
        return numberOfRequests;
    }

    public List<StatisticReportRecordVO> getStatiscticRecord()
    {
        return statiscticRecord;
    }

    public void setStatiscticRecord(List<StatisticReportRecordVO> statiscticRecord)
    {
        this.statiscticRecord = statiscticRecord;
    }
    
   
    
}
