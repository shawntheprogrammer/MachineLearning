package HW2;

import java.util.List;

public class DataEntity{
	public List<Integer> attributes;
	public int label;
	
	public DataEntity(List<Integer> attributes, int label) {
		this.attributes = attributes;
		this.label = label;
	}
	
	//@para int the index of the attributes to be retrieved
	//return int the value of that attribute
	public int getValue(int index) {
		return attributes.get(index);
	}
}