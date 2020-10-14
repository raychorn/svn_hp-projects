package com.hp.asi.hpic4vc.server.provider.data;


public class HostSummaryResult extends ResultBase
{	
    private String uid_status;
    private String backup_rom;
    private String total_memory;
    private String rom;
    private int total_nics;
    private String ilo_server_name;
    private String enclosure;
    private String total_cpu;
    private String uuid;
    private String total_storage;
    private String product_name;
    
    private HostStatus status;
    
    private Boolean hp_bundle_status;
    private String vmware_name;
    private String ilo_firmware_date;
    
    private ILO_Power ilo_power;
    
    private Boolean blade;
    private String bay;
    private String ilo_address;
    private String host_name;
    private String ilo_firmware_version;
    private String ilo_license_type;
    private String power_status;
    
    public HostSummaryResult()
    {
    	super();    	    
    	this.hp_bundle_status = false;
    }
        
	public String getUid_status()
	{
		return uid_status;
	}

	public void setUid_status(String uid_status)
	{
		this.uid_status = uid_status;
	}

	public String getBackup_rom()
	{
		return backup_rom;
	}

	public void setBackup_rom(String backup_rom)
	{
		this.backup_rom = backup_rom;
	}

	public String getTotal_memory()
	{
		return total_memory;
	}

	public void setTotal_memory(String total_memory)
	{
		this.total_memory = total_memory;
	}

	public String getRom()
	{
		return rom;
	}

	public void setRom(String rom)
	{
		this.rom = rom;
	}

	public int getTotal_nics()
	{
		return total_nics;
	}

	public void setTotal_nics(int total_nics)
	{
		this.total_nics = total_nics;
	}

	public String getIlo_server_name()
	{
		return ilo_server_name;
	}

	public void setIlo_server_name(String ilo_server_name)
	{
		this.ilo_server_name = ilo_server_name;
	}

	public String getEnclosure()
	{
		return enclosure;
	}

	public void setEnclosure(String enclosure)
	{
		this.enclosure = enclosure;
	}

	public String getTotal_cpu()
	{
		return total_cpu;
	}

	public void setTotal_cpu(String total_cpu)
	{
		this.total_cpu = total_cpu;
	}

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}

	public String getTotal_storage()
	{
		return total_storage;
	}

	public void setTotal_storage(String total_storage)
	{
		this.total_storage = total_storage;
	}

	public String getProduct_name()
	{
		return product_name;
	}

	public void setProduct_name(String product_name)
	{
		this.product_name = product_name;
	}

	public HostStatus getStatus()
	{
		return status;
	}

	public void setStatus(HostStatus status)
	{
		this.status = status;
	}

	public Boolean getHp_bundle_status()
	{
		return this.hp_bundle_status;
	}

	public void setHp_bundle_status(Boolean hp_bundle_status)
	{
		this.hp_bundle_status = hp_bundle_status;
	}

	public String getVmware_name()
	{
		return vmware_name;
	}

	public void setVmware_name(String vmware_name)
	{
		this.vmware_name = vmware_name;
	}

	public String getIlo_firmware_date()
	{
		return ilo_firmware_date;
	}

	public void setIlo_firmware_date(String ilo_firmware_date)
	{
		this.ilo_firmware_date = ilo_firmware_date;
	}

	public ILO_Power getIlo_power()
	{
		return ilo_power;
	}

	public void setIlo_power(ILO_Power ilo_power)
	{
		this.ilo_power = ilo_power;
	}

	public Boolean getBlade()
	{
		return blade;
	}

	public void setBlade(Boolean blade)
	{
		this.blade = blade;
	}

	public String getBay()
	{
		return bay;
	}

	public void setBay(String bay)
	{
		this.bay = bay;
	}

	public String getIlo_address()
	{
		return ilo_address;
	}

	public void setIlo_address(String ilo_address)
	{
		this.ilo_address = ilo_address;
	}

	public String getHost_name()
	{
		return host_name;
	}

	public void setHost_name(String host_name)
	{
		this.host_name = host_name;
	}

	public String getIlo_firmware_version()
	{
		String str = ilo_firmware_version + " " + ilo_firmware_date ;
		if(str != null && !str.contains("null"))
			return str ;
		else
			return "";
	}

	public void setIlo_firmware_version(String ilo_firmware_version)
	{
		this.ilo_firmware_version = ilo_firmware_version;
	}

	public String getIlo_license_type()
	{
		return ilo_license_type;
	}

	public void setIlo_license_type(String ilo_license_type)
	{
		this.ilo_license_type = ilo_license_type;
	}

	public String getPower_status() {
		return power_status;
	}

	public void setPower_status(String power_status) {
		this.power_status = power_status;
	}
    
    
}
