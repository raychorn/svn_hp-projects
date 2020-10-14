package com.hp.asi.hpic4vc.server.provider.data;

public class ILO_Power
{
	private PowerReading present_power_reading;
	private PowerReading maximum_power_reading;
	private PowerReading minimum_power_reading;
	private PowerReading average_power_reading;

	public PowerReading getPresent_power_reading()
	{
		return present_power_reading;
	}

	public void setPresent_power_reading(PowerReading present_power_reading)
	{
		this.present_power_reading = present_power_reading;
	}

	public PowerReading getMaximum_power_reading() {
		return maximum_power_reading;
	}

	public void setMaximum_power_reading(PowerReading maximum_power_reading) {
		this.maximum_power_reading = maximum_power_reading;
	}

	public PowerReading getMinimum_power_reading() {
		return minimum_power_reading;
	}

	public void setMinimum_power_reading(PowerReading minimum_power_reading) {
		this.minimum_power_reading = minimum_power_reading;
	}

	public PowerReading getAverage_power_reading() {
		return average_power_reading;
	}

	public void setAverage_power_reading(PowerReading average_power_reading) {
		this.average_power_reading = average_power_reading;
	}
}
