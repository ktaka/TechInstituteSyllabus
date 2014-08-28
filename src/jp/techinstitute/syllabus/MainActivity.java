package jp.techinstitute.syllabus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity implements OnItemClickListener {

	private class CourseItem {
		Date date;
		String title;
		String teacher;
		String detail;
	}
	
	private List<CourseItem> itemList;
	private ItemAdapter adapter;
	private RequestQueue reqQueue;
	private static final String syllabusUrl = "https://dl.dropboxusercontent.com/u/1088314/tech_institute/2014/syllabus.json";

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itemList = new ArrayList<CourseItem>();
        adapter = 
            new ItemAdapter(getApplicationContext(), 0,
                itemList);
        ListView listView =
            (ListView)findViewById(R.id.listview);
        listView.setAdapter(adapter);

        reqQueue = Volley.newRequestQueue(this);
		getCourseData();
        
        listView.setOnItemClickListener(this);
    }

    private void getCourseData() {
    	Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray array = response.getJSONArray("course");
					setCourseArray(array);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		
		Response.ErrorListener errorListener = new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		};
		JsonObjectRequest jsonReq = new JsonObjectRequest(syllabusUrl, null, listener, errorListener);
		reqQueue.add(jsonReq);
    }
    
	private void setCourseArray(JSONArray array) throws JSONException {
		int num = array.length();
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		for(int i = 0; i < num; i++) {
			CourseItem item = new CourseItem();
			JSONObject obj = array.getJSONObject(i);
			String dateStr = obj.getString("date");
			Date date = null;
			try {
				date = inputDateFormat.parse(dateStr);
				item.date = date;
				item.title = obj.getString("title");
				item.teacher = obj.getString("teacher");
				item.detail = obj.getString("detail");
				itemList.add(item);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		adapter.notifyDataSetChanged();
	}

	public class ItemAdapter extends ArrayAdapter<CourseItem> {
		private LayoutInflater inflater;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");

		public ItemAdapter(Context context, int resource,
				List<CourseItem> objects) {
			super(context, resource, objects);
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = inflater.inflate(R.layout.lecture_row, null, false);
			TextView dateView = (TextView) view.findViewById(R.id.date); 
			TextView titleView = (TextView) view.findViewById(R.id.title);
			CourseItem item = getItem(position);
			dateView.setText(dateFormat.format(item.date));
			titleView.setText(item.title);
			return view;
		}

	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		CourseItem item = (CourseItem)arg0.getItemAtPosition(arg2);
		Intent intent = new Intent(this, CourseDetail.class);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		intent.putExtra("date", dateFormat.format(item.date));
		intent.putExtra("title", item.title);
		intent.putExtra("teacher", item.teacher);
		intent.putExtra("detail", item.detail);
		startActivity(intent);
	}
}
