package de.mpg.imeji.presentation.admin;

import java.util.ArrayList;



import javax.faces.bean.ManagedBean;

import de.mpg.imeji.logic.controller.StatisticsController;

@ManagedBean
public class StorageStatisticsBean {
	
	private ArrayList<Institute> institutes = new ArrayList<>();
	
	public StorageStatisticsBean(){
		StatisticsController controller = new StatisticsController();
		for(String institute : controller.getAllInstitute()){
			institutes.add(new Institute(institute, controller.getUsedStorageSizeForInstitute(institute)));
		}
	}
	
	public ArrayList<Institute> getInstitutes(){
		return institutes;
		
	}
	
}
