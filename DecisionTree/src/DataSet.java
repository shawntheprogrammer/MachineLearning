package HW2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataSet {
	public static List<String> attributeNames;
	public List<DataEntity> entities;
	
	public DataSet(String path) {
		List<String[]> rawData = getRawData(path);
		attributeNames = getAttributeNames(rawData.get(0));
		this.entities = getEntities(rawData);
	}
	
	// get the first column of the data
	private List<String> getAttributeNames(String[] strings) {
		List<String> attributes = new ArrayList<>();
		//the last index is class, omit it
		for(int i = 0; i < strings.length - 1; i++)
				attributes.add(strings[i]);
		return attributes;
	}

	//@parameter String the repository of the data
	//return List<Integer> list of integer type of the rawdata
	public List<String[]> getRawData(String path) {
		List<String[]> rawData = new ArrayList<>();  
		String csvFile = path;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
			    // use comma as separator
				String[] arr = line.split(cvsSplitBy);
				rawData.add(arr);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return	rawData;
	}
	
	//@parameter List<Integer>  raw data in the dataset has attributes and class at the same row
	//return List<DataEntity> a list of DataEntity in the dataset (seperate attributes and label)
	public List<DataEntity> getEntities (List<String[]> rawData) {
		List<DataEntity> entities = new ArrayList<>();
		
		//get attributes and label rawData.get(0) are the first column with column names
		for(int i = 1; i < rawData.size(); i++) {
			String[] arr = rawData.get(i);
			List<Integer> attributes = new ArrayList<>();
			for(int j = 0; j < arr.length - 1; j++) {
				attributes.add(Integer.valueOf(arr[j].trim()));
			}
			int label = Integer.valueOf(arr[arr.length-1].trim());
			DataEntity entity = new DataEntity(attributes, label);
			entities.add(entity);
		}
		
		return entities;
	}
}
