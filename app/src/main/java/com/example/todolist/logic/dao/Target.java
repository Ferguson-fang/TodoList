package com.example.todolist.logic.dao;

import org.litepal.crud.DataSupport;

public class Target extends DataSupport {

    /**
     * 目标任务
     * */
    private String taskTarget;

    /**
     * 目标时间 long取毫秒值
     */
    private String timeTarget;
    /**
     * 过程时间
     * */
    private long process;
    public Target(){}

    public Target( String taskTarget, String timeTarget, long process) {
        this.taskTarget = taskTarget;
        this.timeTarget = timeTarget;
        this.process = process;
    }

    public long getProcess() {
        return process;
    }

    public void setProcess(long process) {
        this.process = process;
    }

    public String getTaskTarget() {
        return taskTarget;
    }

    public void setTaskTarget(String taskTarget) {
        this.taskTarget = taskTarget;
    }

    public String getTimeTarget() {
        return timeTarget;
    }

    public void setTimeTarget(String timeTarget) {
        this.timeTarget = timeTarget;
    }
}
