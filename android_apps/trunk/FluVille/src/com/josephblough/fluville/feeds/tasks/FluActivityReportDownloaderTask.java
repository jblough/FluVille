package com.josephblough.fluville.feeds.tasks;

import com.josephblough.fluville.feeds.DataRetriever;
import com.josephblough.fluville.feeds.reports.FluReport;

import android.os.AsyncTask;

public class FluActivityReportDownloaderTask extends AsyncTask<Void, Void, FluReport> {

	@Override
	protected FluReport doInBackground(Void... params) {
		return DataRetriever.getFluActivityReport();
	}

	protected void onPostExecute(FluReport result) {
	}
}
