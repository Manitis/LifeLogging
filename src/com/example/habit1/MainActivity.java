package com.example.habit1;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.timroes.swipetodismiss.SwipeDismissList;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, OnClickListener {

	private static SwipeDismissList dailySwipeList, habitSwipeList, todoSwipeList;
	boolean isNotificationEnabled = true;
	ActionBar actionBar;
	TextView tvCharHp, tvCharXp, tvLvlProf;
	ProgressBar pbCharHp, pbCharXp;
	Button bAddNewItem, bAlert;
	EditText eAddNewItem;
	double xp, hp, maxXp, maxHp;
	int level, unfinished_count;
	int alert_hour, alert_minute, alert_count;
	Uri notif_sound = null;
	int test = 0;
	int[] pref_alarmTime;
	SharedPreferences settings;
	final int SETTINGS_REQUEST_CODE = 1;
	String[] profession = { "Peasant", "Jester", "Farmer", "Landlord",
			"Priest", "Magician", "Mayor", "Lord", "Count", "Mayor",
			"Councilman", "King", "Pope" };
	static ActivityItemArrayAdapter aaDailyAdpt, aaTodoAdpt, aaHabitAdpt;
	static List<ActivityItem> dailyItems = new ArrayList<ActivityItem>();
	static List<ActivityItem> habitItems = new ArrayList<ActivityItem>();
	static List<ActivityItem> todoItems = new ArrayList<ActivityItem>();
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initialize();

	}

	@Override
	protected void onStop() {
		super.onStop();
		// Throw away all pending undos.
		dailySwipeList.discardUndo();
		habitSwipeList.discardUndo();
		todoSwipeList.discardUndo();
	}
	
	private void initialize() {
		loadPreferences();
		xp = 0;
		maxXp = 50;
		hp = 50;
		maxHp = 50;
		level = 1;
		alert_count = 0;
		unfinished_count = 0;
		dailyItems.add(new DailyItem("30 minutes of work out", 8, 4., this));
		dailyItems.add(new DailyItem("Go to bed before 23", 5, 2, this));
		dailyItems.add(new DailyItem("30 minutes of productivity", 8, 6, this));
		dailyItems.add(new DailyItem("30 minutes of homework", 6, 3, this));
		dailyItems.add(new DailyItem("Work on the big project", 10, 6, this));
		dailyItems.add(new DailyItem("Clean apartment for 20 minutes", 4, 1.5,
				this));

		todoItems
				.add(new TodoItem("Buy milk on the way home", 5, 6, 0.5, this));
		todoItems
				.add(new TodoItem("Email report when at work", 5, 6, 0.5, this));
		todoItems.add(new TodoItem("Get car repaired", 10, 10, 5, this));
		todoItems.add(new TodoItem("Finish Android app", 40, 30, 20, this));

		habitItems.add(new HabitItem("Don\'t eat fast food", 5, 5, this));
		habitItems.add(new HabitItem("Study for school", 8, 5, this));
		habitItems.add(new HabitItem("Go to bed early", 4, 3, this));
		habitItems.add(new HabitItem("Jog to work", 8, 6, this));

		tvLvlProf = (TextView) findViewById(R.id.tvLvlProf);
		tvCharHp = (TextView) findViewById(R.id.tvCharHP);
		pbCharHp = (ProgressBar) findViewById(R.id.pbCharHP);
		tvCharXp = (TextView) findViewById(R.id.tvCharXP);
		pbCharXp = (ProgressBar) findViewById(R.id.pbCharXP);

		eAddNewItem = (EditText) findViewById(R.id.eNewItem);
		bAlert = (Button) findViewById(R.id.bAlert);
		bAlert.setOnClickListener(this);
		bAddNewItem = (Button) findViewById(R.id.bAdd);
		bAddNewItem.setOnClickListener(this);

		// Set up the action bar to show tabs.
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// For each of the sections in the app, add a tab to the action bar.
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section1)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section2)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section3)
				.setTabListener(this));

		updateDisplay();
		writeUnfinishedValues();

	}

	public void startAlert() {
		Intent intent = new Intent(this, MyBroadcastReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				this.getApplicationContext(), 234324243, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, alert_hour);
		calendar.set(Calendar.MINUTE, alert_minute);
		calendar.set(Calendar.SECOND, 00);
		
		
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
		calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
		alarmManager.set(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 2 * 1000, pendingIntent);
	}

	public void outputUnfinishedItemValues() {
		getUnfinishedCount();
		xpGainIfFinished();
		hpLossIfUnfinished();
	}

	public void writeUnfinishedValues() {
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		final SharedPreferences.Editor saveEditor = settings.edit();
		saveEditor.putInt("unfinishedCount", getUnfinishedCount());
		saveEditor.putFloat("xpGainIfFinished", (float) xpGainIfFinished());
		saveEditor.putFloat("hpLossIfUnfinished", (float) hpLossIfUnfinished());
		saveEditor.commit();
	}

	public List<ActivityItem> getUnfinishedItems() {
		List<ActivityItem> result = new ArrayList<ActivityItem>();
		result = getUnfinishedItemsInList(dailyItems);
		result.addAll(getUnfinishedItemsInList(todoItems));
		return result;
	}

	public List<ActivityItem> getUnfinishedItemsInList(
			List<ActivityItem> inputList) {
		List<ActivityItem> result = new ArrayList<ActivityItem>();
		for (ActivityItem item : inputList) {
			if (item.isDueToday() && !item.isFinished()) {
				result.add(item);
			}
		}
		return result;
	}

	public int getUnfinishedCount() {
		int result = getUnfinishedItems().size();

		return result;
	}

	private double hpLossIfUnfinished() {
		double hpLoss = 0;
		List<ActivityItem> unfinishedItems = getUnfinishedItems();
		for (ActivityItem item : unfinishedItems) {
			hpLoss += item.getHpPen();
		}
		return hpLoss;
	}

	private double xpGainIfFinished() {
		double xpReward = 0;
		List<ActivityItem> unfinishedItems = getUnfinishedItems();
		for (ActivityItem item : unfinishedItems) {
			xpReward += item.getXpRew();
		}
		return xpReward;
	}

	private void loadPreferences() {
		getNotificationSettings();
	}

	private void getNotificationSettings() {
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		isNotificationEnabled = settings.getBoolean("checkbox_preference",
				false);
		getNotificationTime();
		getNotificationSound();
	}

	private void getNotificationTime() {
		String alert_time = settings.getString("pref_notify_time", "19:00");
		String[] pieces = alert_time.split(":");
		alert_hour = Integer.parseInt(pieces[0]);
		alert_minute = Integer.parseInt(pieces[1]);
	}

	private void getNotificationSound() {
		final String savedUri = settings.getString("pref_notif_sound", "");
		if (savedUri.length() > 0) {
			// If the stored string is the bogus string...
			if (savedUri.equals("defaultRingtone")) {
				notif_sound = Settings.System.DEFAULT_ALARM_ALERT_URI;
				final SharedPreferences.Editor saveEditor = settings.edit();
				saveEditor.putString("alarm", notif_sound.toString());
				saveEditor.commit();
			} else {
				notif_sound = Uri.parse(savedUri);
			}
		}
	}

	public void updateDisplay() {
		if (level <= profession.length) {
			tvLvlProf.setText("Level " + level + " " + profession[level - 1]);
		} else {
			tvLvlProf.setText("Level " + level + " "
					+ profession[profession.length - 1]);
		}
		tvCharXp.setText("XP: " + xp + "/50.0");
		pbCharXp.setProgress((int) xp);
		tvCharHp.setText("HP: " + hp + "/50.0");
		pbCharHp.setProgress((int) hp);
		writeUnfinishedValues();
	}

	public void addXp(double amount) {
		xp = xp + amount;
		if (xp > maxXp) {
			level += 1;
			xp = xp - maxXp;
		}
		updateDisplay();
	}

	public void subtractXp(double amount) {
		xp = xp - amount;
		if (xp < 0) {
			level -= 1;
			xp = xp + maxXp;
		}
		updateDisplay();
	}

	public void addHp(double amount) {
		hp = hp + amount;
		updateDisplay();
	}
	
	public void subtractHp(double amount) {
		hp = hp - amount;
		updateDisplay();
	}


	@Override
	public void onClick(View v) {
		ListView lvParent;
		double xpRew = 0;
		double hpPen = 0;
		switch (v.getId()) {
		case (R.id.bAlert):
			startAlert();
			break;
		case (R.id.bAdd):
			String itemText = eAddNewItem.getText().toString();

			switch (actionBar.getSelectedNavigationIndex()) {
			case (0):
				newDailyDialog(itemText);
				break;
			case (1):
				newHabitDialog(itemText);
				break;
			case (2):
				newTodoDialog(itemText);
				break;
			}
			break;
		case (R.id.bDown):
			lvParent = (ListView) v.getParent().getParent();
			hpPen = habitItems.get(lvParent.getPositionForView(v)).getHpPen();
			subtractHp(hpPen);
			break;
		case (R.id.bUp):
			lvParent = (ListView) v.getParent().getParent();
			xpRew = habitItems.get(lvParent.getPositionForView(v)).getXpRew();
			addXp(xpRew);
			break;
		case (R.id.cDailyDone):
			dailyDone(v);
			break;
		case (R.id.cTodoDone):
			todoDone(v);
			break;
		}
	}

	private void dailyDone(View v) {
		ListView lvParent;
		CheckBox itemCheckbox;
		double xpRew = 0;
		lvParent = (ListView) v.getParent().getParent();
		DailyItem item = (DailyItem) dailyItems.get(lvParent
				.getPositionForView(v));
		xpRew = item.getXpRew();
		itemCheckbox = (CheckBox) v;
		if (itemCheckbox.isChecked()) {
			item.setFinished(true);
			addXp(xpRew);
		} else {
			item.setFinished(false);
			subtractXp(xpRew);
		}
	}

	private void todoDone(View v) {
		ListView lvParent;
		CheckBox itemCheckbox;
		double xpRew = 0;
		lvParent = (ListView) v.getParent().getParent();
		TodoItem item = (TodoItem) todoItems
				.get(lvParent.getPositionForView(v));
		xpRew = item.getXpRew();
		itemCheckbox = (CheckBox) v;
		if (itemCheckbox.isChecked()) {
			item.setFinished(true);
			addXp(xpRew);
		} else {
			item.setFinished(false);
			subtractXp(xpRew);
		}
	}

	public void newDailyDialog(final String itemName) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		View myView = null;
		myView = inflater.inflate(R.layout.daily_popup, null);

		final NumberPicker npXP = (NumberPicker) myView.findViewById(R.id.npXP);
		final NumberPicker npHP = (NumberPicker) myView.findViewById(R.id.npHP);
		npXP.setMinValue(1);
		npXP.setMaxValue(10);
		npXP.setWrapSelectorWheel(false);
		npXP.setValue(5);
		npHP.setMinValue(1);
		npHP.setMaxValue(10);
		npHP.setWrapSelectorWheel(false);
		npHP.setValue(5);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dailyItems.add(new DailyItem(itemName, npXP.getValue(), npHP
						.getValue(), MainActivity.this));
				aaDailyAdpt.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
		builder.setView(myView);
		builder.setTitle(itemName);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void newHabitDialog(final String itemName) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		View myView = null;
		myView = inflater.inflate(R.layout.daily_popup, null);

		final NumberPicker npXP = (NumberPicker) myView.findViewById(R.id.npXP);
		final NumberPicker npHP = (NumberPicker) myView.findViewById(R.id.npHP);
		npXP.setMinValue(1);
		npXP.setMaxValue(10);
		npXP.setWrapSelectorWheel(false);
		npXP.setValue(5);
		npHP.setMinValue(1);
		npHP.setMaxValue(10);
		npHP.setWrapSelectorWheel(false);
		npHP.setValue(5);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				habitItems.add(new HabitItem(itemName, npXP.getValue(), npHP
						.getValue(), MainActivity.this));
				aaHabitAdpt.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
		builder.setView(myView);
		builder.setTitle(itemName);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void newTodoDialog(final String itemName) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		View myView = null;
		myView = inflater.inflate(R.layout.todo_popup, null);

		final NumberPicker npXP = (NumberPicker) myView.findViewById(R.id.npXP);
		final NumberPicker npHP = (NumberPicker) myView.findViewById(R.id.npHP);
		final NumberPicker npDue = (NumberPicker) myView
				.findViewById(R.id.npDue);
		npXP.setMinValue(1);
		npXP.setMaxValue(10);
		npXP.setWrapSelectorWheel(false);
		npXP.setValue(5);
		npHP.setMinValue(1);
		npHP.setMaxValue(10);
		npHP.setWrapSelectorWheel(false);
		npHP.setValue(5);
		npDue.setMinValue(1);
		npDue.setMaxValue(30);
		npDue.setWrapSelectorWheel(false);
		npDue.setValue(5);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				todoItems.add(new TodoItem(itemName, npXP.getValue(), npHP
						.getValue(), npDue.getValue(), MainActivity.this));
				aaTodoAdpt.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Do nothing
					}
				});
		builder.setView(myView);
		builder.setTitle(itemName);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
		case SETTINGS_REQUEST_CODE:
			loadPreferences();
			break;
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.menu_settings):
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, PreferenceActivity.class);
			startActivityForResult(intent, SETTINGS_REQUEST_CODE);
			break;
		}
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, show the tab contents in the
		// container view.
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.FRAGMENT_ID, tab.getPosition());
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		public static final String FRAGMENT_ID = "section_number";
		private LayoutInflater inflater;
		private ViewGroup container;
		private View fragmentView;
		private ListView listViewToFill;
		private int itemType;
		private final int DAILY = 0;
		private final int HABIT = 1;
		private final int TODO = 2;
		private int[] mainLayout = { R.layout.daily, R.layout.habit,
				R.layout.todo };
		private int[] listviewID = { R.id.lvDaily, R.id.lvHabit, R.id.lvTodo };
		private int[] listviewItemLayout = { R.layout.list_layout_daily,
				R.layout.list_layout_habit, R.layout.list_layout_todo };

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			this.inflater = inflater;
			this.container = container;
			itemType = getArguments().getInt(FRAGMENT_ID);
			adaptListView(itemType);
			return fragmentView;
		}

		private void adaptListView(int itemType) {
			fragmentView = inflater.inflate(mainLayout[itemType], container,
					false);
			listViewToFill = (ListView) fragmentView
					.findViewById(listviewID[itemType]);
			switch (itemType) {
			case DAILY:
				aaDailyAdpt = new ActivityItemArrayAdapter(dailyItems,
						listviewItemLayout[itemType], getActivity());
				listViewToFill.setAdapter(aaDailyAdpt);
				dailySwipeList = getSwipeDismissList(aaDailyAdpt);
				break;
			case HABIT:
				aaHabitAdpt = new ActivityItemArrayAdapter(habitItems,
						listviewItemLayout[itemType], getActivity());
				listViewToFill.setAdapter(aaHabitAdpt);
				habitSwipeList = getSwipeDismissList(aaHabitAdpt);
				break;
			case TODO:
				aaTodoAdpt = new ActivityItemArrayAdapter(todoItems,
						listviewItemLayout[itemType], getActivity());
				listViewToFill.setAdapter(aaTodoAdpt);
				todoSwipeList = getSwipeDismissList(aaTodoAdpt);
				break;
			}
			

		}
		
		private SwipeDismissList getSwipeDismissList(final ActivityItemArrayAdapter adapter){
			return new SwipeDismissList(listViewToFill,
					new SwipeDismissList.OnDismissCallback() {
						public SwipeDismissList.Undoable onDismiss(
								ListView listView, final int position) {
							final ActivityItem item = adapter
									.getItem(position);
							adapter.remove(item);
							return new SwipeDismissList.Undoable() {

								@Override
								public String getTitle() {
									return item.name + " deleted";
								}

								@Override
								public void undo() {
									adapter.insert(item, position);
								}

								@Override
								public void discard() {
									Log.w("DISCARD", "item " + item.name
											+ " now finally discarded");
								}
							};

						}
					}, SwipeDismissList.UndoMode.MULTI_UNDO);
		}
		
	}

}
