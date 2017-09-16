/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cdv.sampling.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cdv.sampling.bean.SampleBean;

final class SamplingDB extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_NAME = "cdv_sampling.db";

    private static final String CREATE_SAMPLE_INFO = ""
            + "CREATE TABLE " + SampleBean.TABLE + "("
            + SampleBean.ID_ + " INTEGER AUTO_INCREMENT,"
            + SampleBean.NO_ + " TEXT NOT NULL,"
            + SampleBean.NAME_ + " TEXT,"
            + SampleBean.COUNT_ + " INTEGER,"
            + SampleBean.BASE_ + " TEXT NOT NULL,"
            + SampleBean.SOURCE_COMPANY + " TEXT,"
            + SampleBean.SOURCE_TEL + " TEXT,"
            + SampleBean.SOURCE_PERSON + " TEXT,"
            + SampleBean.SOURCE_TYPE + " TEXT,"
            + SampleBean.SOURCE_TEL + " TEXT,"
            + SampleBean.QUARANTINE_NO + " TEXT,"
            + SampleBean.BOOTH_OWNER + " TEXT,"
            + SampleBean.REMARK_ + " TEXT,"
            + "PRIMARY KEY (" + SampleBean.ID_ + ")"
            + ")";

    public SamplingDB(Context context) {
        super(context, DB_NAME, null /* factory */, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SAMPLE_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
