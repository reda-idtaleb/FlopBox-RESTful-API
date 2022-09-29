package com.repository;

import com.data.JsonDatabase;

public class RepositoryImpl implements Repository{
    /** The link to the database */
    protected static JsonDatabase dataWorker;

    public RepositoryImpl() {
        dataWorker = new JsonDatabase();
    }

    public RepositoryImpl(JsonDatabase dataWorker) {
        this.dataWorker = dataWorker;
    }

    @Override
    public void setDataWorker(JsonDatabase newdb) {
        dataWorker = newdb;
    }

    public JsonDatabase getDataWorker() {
        return dataWorker;
    }
}
