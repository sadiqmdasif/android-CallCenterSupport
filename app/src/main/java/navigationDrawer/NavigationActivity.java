package navigationDrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.icddrb.callcentersupport.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import proto.net.client.AndroidNetCommunicationClientActivity;
import proto.net.client.ChatBox;

public class NavigationActivity extends AppCompatActivity {

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	protected Toolbar toolbar;

	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
    private int lastExpandedPosition = -1;
	private Intent intent;

	private static boolean isLaunch = true;
	protected FrameLayout frameLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation_drawer);

		//setup frame to show other activity
		frameLayout = (FrameLayout)findViewById(R.id.content_frame);
		// Setup toolbar (really FrameLayout)
		toolbar = (Toolbar) findViewById(R.id.navToolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("ICDDR,B");



		// Setup Drawer Icon
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
		drawerLayout.setDrawerListener(drawerToggle);
	//	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setHomeButtonEnabled(true);
		drawerToggle.syncState();

		// get the listview
		expListView = (ExpandableListView) findViewById(R.id.left_drawer);

		// preparing list data
		prepareListData();

		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

		// setting list adapter
		expListView.setAdapter(listAdapter);

		// Listview Group click listener
		expListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// Toast.makeText(getApplicationContext(),
				// "Group Clicked " + listDataHeader.get(groupPosition),
				// Toast.LENGTH_SHORT).show();
				return false;
			}
		});

		// Listview Group expanded listener

		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				Toast.makeText(getApplicationContext(),
						listDataHeader.get(groupPosition) + " Expanded",
						Toast.LENGTH_SHORT).show();


                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;


			}
		});

		// Listview Group collasped listener
		expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				Toast.makeText(getApplicationContext(),
						listDataHeader.get(groupPosition) + " Collapsed",
						Toast.LENGTH_SHORT).show();




			}
		});

		// Listview on child click listener
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(
						getApplicationContext(),
						listDataHeader.get(groupPosition)
								+ " : "
								+ listDataChild.get(
								listDataHeader.get(groupPosition)).get(
								childPosition), Toast.LENGTH_SHORT)
						.show();


                switch (groupPosition) {
                    case 0:
                        switch (childPosition){
							case 0:
								//here goes the action to do after clicking the sub items
								// first item of first menu = 0,0
								intent = new Intent(getApplicationContext(), AndroidNetCommunicationClientActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								getApplicationContext().startActivity(intent);
								break;
							case 1:
								//here goes the action to do after clicking the sub items
								// first item of first menu = 0,0
								intent = new Intent(getApplicationContext(), ChatBox.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								getApplicationContext().startActivity(intent);
								break;

						}

                    case 1:
                        switch (childPosition){
                            case 0:
                                //here goes the action to do after clicking the sub items
                                // first item of second menu = 1,0
                                break;
                        }
                }

				return false;
			}
		});


		if(isLaunch){
			/**
			 *Setting this flag false so that next time it will not open our first activity.
			 *We have to use this flag because we are using this BaseActivity as parent activity to our other activity.
			 *In this case this base activity will always be call when any child activity will launch.
			 */
		Log.d("Launch","Main menu");
			isLaunch = false;
			Intent intent= new Intent(getApplicationContext(),AndroidNetCommunicationClientActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

	/*
	 * Preparing the list data
	 */
	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		// Adding group data
		listDataHeader.add("First Menu");
		listDataHeader.add("Second Menu");
		listDataHeader.add("Third Menu");

		// Adding child data
		List<String> firstMenu = new ArrayList<String>();
		firstMenu.add("Main Menu");
		firstMenu.add("Sub Item 2");
		firstMenu.add("Sub Item 3");
		firstMenu.add("Sub Item 4");
		firstMenu.add("Sub Item 5");
		firstMenu.add("Sub Item 6");
		firstMenu.add("Sub Item 7");

		List<String> secondMenu  = new ArrayList<String>();
		secondMenu.add("Sub Item 1");
		secondMenu.add("Sub Item 2");
		secondMenu.add("Sub Item 3");
		secondMenu.add("Sub Item 4");
		secondMenu.add("Sub Item 5");
		secondMenu.add("Sub Item 6");

		List<String> thirdMenu = new ArrayList<String>();
		thirdMenu.add("Sub Item 1");
		thirdMenu.add("Sub Item 2");
		thirdMenu.add("Sub Item 3");
		thirdMenu.add("Sub Item 4");
		thirdMenu.add("Sub Item 5");

		listDataChild.put(listDataHeader.get(0), firstMenu); // Header, Child data
		listDataChild.put(listDataHeader.get(1), secondMenu);
		listDataChild.put(listDataHeader.get(2), thirdMenu);
	}
}
