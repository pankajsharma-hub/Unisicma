package com.example.unisicma;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;


public class CassandraConnector extends AsyncTask<String, String, ResultSet> {

    private Cluster cluster;
    ProgressDialog progressDialog;

    /**
     * Cassandra Session.
     */
    private Session session;
    Context context;

    public CassandraConnector(Context context) {
        this.context = context;
    }


    private Session getSession() {
        return this.session;
    }

    private ResultSet connect(final String node, final int port, String block, String phc, String sc, String facility) {


        cluster = Cluster.builder().withoutJMXReporting().addContactPoint(node).withPort(port).build();
        final Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
        for (final Host host : metadata.getAllHosts()) {
            System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }
        session = cluster.connect();
        session.execute(
                "INSERT INTO unisicma_db.facility_details (facility_id,block, phc, sc) VALUES (?,?,?,?)",
                facility, block, phc, sc
        );
        final String title = "Tanhaji";
        final int year = 2020;
        //  final ResultSet movieResults = getSession().execute(
        //         "SELECT * from firstkeyspace.movies WHERE title = ? AND year = ?", title, year);
        final ResultSet movieResults = getSession().execute(
                "SELECT * FROM unisicma_db.facility_details");

     /*   Row movieRow = movieResults.one();
        String title1 = movieRow.getString("title");
        int year1 = movieRow.getInt("year");
        String description = movieRow.getString("description");
        String mmpa = movieRow.getString("mmpa_rating");
        String dustin_rating = movieRow.getString("dustin_rating");
        System.out.printf("%s; Rating:%s; Year:%s\n", description, mmpa, year1);
     */


        cluster.close();
        return movieResults;
    }


    /**
     * Close cluster.
     */
    public void close() {
        cluster.close();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        // progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    }


    @Override
    protected ResultSet doInBackground(String... params) {
        String node = params[0];
        String block = params[1];
        String phc = params[2];
        String sc = params[3];
        String facility = params[4];
        int port = 9042;
        ResultSet resultSet = connect(node, port, block, phc, sc, facility);

        return resultSet;
    }


    @Override
    protected void onPostExecute(ResultSet rows) {
        super.onPostExecute(rows);
        progressDialog.dismiss();


        for (Row movieRow : rows) {

            if (rows.getAvailableWithoutFetching() == 100 && !rows.isFullyFetched())

                rows.fetchMoreResults();


        }

    }

}
