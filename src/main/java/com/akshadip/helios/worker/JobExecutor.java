package com.akshadip.helios.worker;


public interface JobExecutor {
    public void executeJob(String payload) throws Exception;
}
