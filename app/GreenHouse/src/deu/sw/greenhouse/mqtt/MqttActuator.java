package deu.sw.greenhouse.mqtt;

public class MqttActuator extends MqttDevice {
	
	public MqttActuator(String name, String subtexttopic, String pubtopic) {
		this.name = name;
		this.subtopics.add(subtexttopic);
		this.pubtopics.add(pubtopic);
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getSubtextTopic() {
		return this.subtopics.get(0);
	}
	
	public String getPubTopic() {
		return this.pubtopics.get(0);
	}

}
