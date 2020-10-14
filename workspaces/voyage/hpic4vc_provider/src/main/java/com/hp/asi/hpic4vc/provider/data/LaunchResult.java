package com.hp.asi.hpic4vc.provider.data;

import java.util.Arrays;

public class LaunchResult {
    private LaunchTool[] launch_tools;

    public LaunchResult () {
        super();
    }

    public LaunchResult (LaunchTool[] launch_tools) {
        super();
        this.launch_tools = launch_tools;
    }

    public LaunchTool[] getLaunch_tools () {
        return launch_tools;
    }

    public void setLaunch_tools (LaunchTool[] launch_tools) {
        this.launch_tools = launch_tools;
    }

    @Override
    public String toString () {
        return "LaunchResult [launch_tools=" + Arrays.toString(launch_tools)
                + "]";
    }


}
