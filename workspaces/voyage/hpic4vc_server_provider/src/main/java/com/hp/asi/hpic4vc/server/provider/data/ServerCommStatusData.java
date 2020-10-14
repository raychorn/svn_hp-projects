package com.hp.asi.hpic4vc.server.provider.data;

public class ServerCommStatusData {
	public String status;
	public String last_update;

	public ServerCommStatusData(){
		
	}

	@Override
    public String toString () {
        return "ServerCommStatusData [status=" +  status + ", last_update=" + last_update + "]";
    }

}

