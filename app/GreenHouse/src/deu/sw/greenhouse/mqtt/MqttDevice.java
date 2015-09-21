package deu.sw.greenhouse.mqtt;

import java.util.ArrayList;

public class MqttDevice {
	protected String name;
	protected ArrayList<String> subtopics = new ArrayList<String>();
	protected ArrayList<String> pubtopics = new ArrayList<String>();
	
	public MqttDevice() {
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getSubTopic(int id) {
		return this.subtopics.get(id);
	}
	
	public String getPubTopic(int id) {
		return this.pubtopics.get(id);
	}

}
