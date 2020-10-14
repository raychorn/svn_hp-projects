package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.List;

public class VMHardware
{
	private int numCPU;
	private int memoryMB;
	private List<VMDisk> disks;
	
	public int getNumCPU()
	{
		return numCPU;
	}
	
	public void setNumCPU(int numCPU)
	{
		this.numCPU = numCPU;
	}

	public int getMemoryMB()
	{
		return memoryMB;
	}

	public void setMemoryMB(int memoryMB)
	{
		this.memoryMB = memoryMB;
	}

	public List<VMDisk> getDisks() {
		return disks;
	}

	public void setDisks(List<VMDisk> disks) {
		this.disks = disks;
	}
}
