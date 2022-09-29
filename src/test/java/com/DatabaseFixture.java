package com;

import com.data.DataBaseInfo;
import com.data.JsonDatabase;

public class DatabaseFixture {
    public class DataBaseInfoMock extends DataBaseInfo{
		private static final String DUMMY_DB_PATH = ".dummy_db.json";

		DataBaseInfoMock () {
			super(DUMMY_DB_PATH);
		}
	}

	public class JsonDatabaseMock extends JsonDatabase{
		JsonDatabaseMock () {
			super(new DataBaseInfoMock());
		}
	}
}
