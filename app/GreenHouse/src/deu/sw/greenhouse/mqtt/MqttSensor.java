package deu.sw.greenhouse.mqtt;

import java.util.ArrayList;

import deu.sw.greenhouse.graph.TimeValue;

public class MqttSensor extends MqttDevice {
	private String textValue = "";
	private ArrayList<TimeValue> graphData;
	
	public MqttSensor(String name, String subtexttopic, String subgraphtopic) {
		this.name = name;
		this.subtopics.add(subtexttopic); //subtexttopic
		this.subtopics.add(subgraphtopic); //subgraphtopic
		this.graphData = new ArrayList<TimeValue>();
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getSubtextTopic() {
		return this.subtopics.get(0);
	}
	
	public String getSubgraphTopic() {
		return this.subtopics.get(1);
	}
	
	public String getTextValue() {
		return this.textValue;
	}
	
	public void setTextValue(String value) {
		this.textValue = value;
	}
	
	public ArrayList<TimeValue> getGraphData() {
		return this.graphData;
	}

}
