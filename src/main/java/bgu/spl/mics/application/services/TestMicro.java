package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BroadcastTerminate;

public class TestMicro extends MicroService {
    public TestMicro() {
        super("TestMicro");
    }


    @Override
    protected void initialize() {
        subscribeBroadcast(BroadcastTerminate.class, c->{terminate();});

    }
}
