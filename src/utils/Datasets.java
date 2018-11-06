package utils;

import java.util.List;

import csv.Dataset;
import csv.Household;

public class Datasets {
	// Singletons for Datasets
	
	public static Dataset<Household> households;
	
	static {
		households = new Dataset<Household>(
			"C:\\Users\\makerspacestaff\\Desktop\\Household info 2014\\2014_rawhh_ukanon_ukda_data_dictionary.csv",
			Household.class,
			2348
		);
	}
}
