package com.hp.asi.hpic4vc.server.provider.data;

public class IloPower {
	
	private MaximumPower maximum_power_reading;
	private MinimumPower minimum_power_reading;
	private PresentPowerReading present_power_reading;
	private AveragePowerReading average_power_reading;
	private String memory_slots;
	
	public MaximumPower getMaximum_power_reading() {
		return maximum_power_reading;
	}
	public void setMaximum_power_reading(MaximumPower maximum_power_reading) {
		this.maximum_power_reading = maximum_power_reading;
	}
	public MinimumPower getMinimum_power_reading() {
		return minimum_power_reading;
	}
	public void setMinimum_power_reading(MinimumPower minimum_power_reading) {
		this.minimum_power_reading = minimum_power_reading;
	}
	public PresentPowerReading getPresent_power_reading() {
		return present_power_reading;
	}
	public void setPresent_power_reading(PresentPowerReading present_power_reading) {
		this.present_power_reading = present_power_reading;
	}
	public AveragePowerReading getAverage_power_reading() {
		return average_power_reading;
	}
	public void setAverage_power_reading(AveragePowerReading average_power_reading) {
		this.average_power_reading = average_power_reading;
	}
	public String getMemory_slots() {
		return memory_slots;
	}
	public void setMemory_slots(String memory_slots) {
		this.memory_slots = memory_slots;
	}
	
	
}
