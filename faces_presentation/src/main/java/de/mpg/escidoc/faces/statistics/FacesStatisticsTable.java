package de.mpg.escidoc.faces.statistics;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;

public class FacesStatisticsTable
{
    StatisticsHelper statisticsHelper = null;
    List<StatisticReportRecordVO> statiscticRecordFW = null;
    int tableSize = 12  ;
    String date = null;
    String requests = null;
    private Integer month = null;
    private Integer year = null;
    
    List<FacesStatisticsTable> list = null;
    
    /**
     * Public constructor. 
     * Construct a table from a statistic record retrieved by the FW.
     * @param statiscticRecordFW
     */
    public FacesStatisticsTable(List<StatisticReportRecordVO> statiscticRecordFW, Integer month, Integer year)
    {
        this.statiscticRecordFW = statiscticRecordFW;
        this.month = month;
        this.year = year;
        statisticsHelper =  new StatisticsHelper(this.statiscticRecordFW, this.month, this.year);
        list = new ArrayList<FacesStatisticsTable>();
    }
    
    /**
     * Private constructor.
     * Set parameters to be display.
     * @param date
     * @param requests
     */
    private FacesStatisticsTable(String date, String requests)
    {
        if (date != null)
        {
            this.date = date;
            if (this.date.length() == 6)
            {
                this.date = "0" + this.date;
            }
        }
        else
        {
            date = "No Data";
        }
        this.requests = requests.replace(".0", "");
    }
    
    public List<FacesStatisticsTable> getList()
    {
        for (int i = 0; i < tableSize; i++)
        {
            list.add(new FacesStatisticsTable(statisticsHelper.getDate(i),
                    statisticsHelper.getRequests(statisticsHelper.getDate(i)).toString()));
        }
        
        return list;
    }

    public void setList(List<FacesStatisticsTable> list)
    {
        this.list = list;
    }

    public List<StatisticReportRecordVO> getStatiscticRecordFW()
    {
        return statiscticRecordFW;
    }

    public void setStatiscticRecordFW(List<StatisticReportRecordVO> statiscticRecordFW)
    {
        this.statiscticRecordFW = statiscticRecordFW;
    }

    public String getRequests()
    {
        return requests;
    }

    public void setRequests(String requests)
    {
        this.requests = requests;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public int getTableSize()
    {
        return tableSize;
    }

    public void setTableSize(int tableSize)
    {
        this.tableSize = tableSize;
    }
}
