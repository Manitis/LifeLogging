package com.example.lifelogging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment;
import com.google.android.gms.maps.model.LatLng;

import de.timroes.swipetodismiss.SwipeDismissList;

@SuppressLint("ValidFragment")
public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, OnClickListener, OnItemClickListener {

	private static SwipeDismissList dailySwipeList, habitSwipeList,
			todoSwipeList;

	ItemAdapter adapterForItemBeingEdited;
	int positionForItemBeingEdited = -1;
	PreferenceContainer preferences;
	ActivityItem item;
	LayoutInflater inflater;
	ActionBar actionBar;
	TextView tvCharHp, tvCharXp, tvLvlProf;
	ProgressBar pbCharHp, pbCharXp;
	Button bAddNewItem;
	EditText eAddNewItem;
	ListView dailyListView, habitListView, todoListView;
	double xp, hp, maxXp, maxHp;
	int lastSort = ItemAdapter.ALPHA, sortType;
	boolean direction = true;
	int level, unfinishedCount;
	final int SETTINGS_REQUEST_CODE = 1;
	final int EDIT_ITEM_REQUEST_CODE = 2;
	static final int DAILY = 0, HABIT = 1, TODO = 2;
	String[] profession = { "Peasant", "Jester", "Farmer", "Landlord",
			"Priest", "Magician", "Mayor", "Lord", "Count", "Mayor",
			"Councilman", "King", "Pope" };

	static ItemAdapter dailyItems, habitItems, todoItems;
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initialize();

	}

	// @Override
	// protected void onStop() {
	// super.onStop();
	// dailySwipeList.discardUndo();
	// habitSwipeList.discardUndo();
	// todoSwipeList.discardUndo();
	// }

	private void initialize() {
		preferences = new PreferenceContainer();
		xp = 0;
		maxXp = 50;
		hp = 50;
		maxHp = 50;
		level = 1;
		unfinishedCount = 0;
		dailyItems = new ItemAdapter(MainActivity.this,
				R.layout.list_layout_daily);
		habitItems = new ItemAdapter(MainActivity.this,
				R.layout.list_layout_habit);
		todoItems = new ItemAdapter(MainActivity.this,
				R.layout.list_layout_todo);

		dailyItems.add(new DailyItem("30 minutes of work out", 8, 4., this,
				new LatLng(55.675302, 12.489832)));
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
		bAddNewItem = (Button) findViewById(R.id.bAdd);
		bAddNewItem.setOnClickListener(this);
		inflater = getLayoutInflater();

		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section1)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section2)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_section3)
				.setTabListener(this));

		updateDisplay();
		preferences.writeUnfinishedValues();
	}

	public void startAlert() {
		Intent intent = new Intent(this, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				this.getApplicationContext(), 234324243, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, preferences.getNotificationHour());
		calendar.set(Calendar.MINUTE, preferences.getNotificationMinute());
		calendar.set(Calendar.SECOND, 00);

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
	}

	public void outputUnfinishedItemValues() {
		getUnfinishedCount();
		xpGainIfFinished();
		hpLossIfUnfinished();
	}

	public List<ActivityItem> getUnfinishedItems() {
		List<ActivityItem> result = new ArrayList<ActivityItem>();
		result = getUnfinishedItemsInList(dailyItems);
		result.addAll(getUnfinishedItemsInList(todoItems));
		return result;
	}

	public List<ActivityItem> getUnfinishedItemsInList(ItemAdapter list) {
		List<ActivityItem> result = new ArrayList<ActivityItem>();
		for (ActivityItem item : list.items) {
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
			hpLoss += item.hpPen();
		}
		return hpLoss;
	}

	private double xpGainIfFinished() {
		double xpReward = 0;
		List<ActivityItem> unfinishedItems = getUnfinishedItems();
		for (ActivityItem item : unfinishedItems) {
			xpReward += item.xpRew();
		}
		return xpReward;
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
		preferences.writeUnfinishedValues();
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
	public void onItemClick(AdapterView<?> parent, View v, int position,
			long rowId) {
		ItemAdapter adapter = (ItemAdapter) parent.getAdapter();
		View parentView = (View) v.getParent();
		int itemType = -1;
		int id = parentView.getId();
		switch (id) {
		case R.id.lvDaily:
			adapterForItemBeingEdited = dailyItems;
			itemType = DAILY;
			break;
		case R.id.lvHabit:
			adapterForItemBeingEdited = habitItems;
			itemType = HABIT;
			break;
		case R.id.lvTodo:
			adapterForItemBeingEdited = todoItems;
			itemType = TODO;
			break;
		}
		positionForItemBeingEdited = position;
		item = adapter.getItem(position);
		startActivityForResult(getOpenItemIntent(item, itemType),
				EDIT_ITEM_REQUEST_CODE);

	}

	private Intent getOpenItemIntent(ActivityItem item, int itemType) {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, ViewSingleItemActivity.class);
		intent.putExtras(item.getBundle());
		return intent;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_CANCELED) {
		} else {
			switch (requestCode) {
			case SETTINGS_REQUEST_CODE:
				preferences.update();
				break;
			case EDIT_ITEM_REQUEST_CODE:
				Bundle extras = intent.getExtras();
				ActivityItem item = null;
				int itemType = extras.getInt("itemType");
				switch (itemType) {
				case DAILY:
					item = new DailyItem(extras);
					break;
				case HABIT:
					item = new HabitItem(extras);
					break;
				case TODO:
					item = new TodoItem(extras);
					break;
				}
				adapterForItemBeingEdited
						.edit(positionForItemBeingEdited, item);
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		ListView lvParent;
		double xpRew = 0;
		double hpPen = 0;
		switch (v.getId()) {
		case (R.id.bAdd):
			newItemDialog();
			break;
		case (R.id.bDown):
			lvParent = (ListView) v.getParent().getParent();
			hpPen = habitItems.getItem(lvParent.getPositionForView(v)).hpPen();
			subtractHp(hpPen);
			break;
		case (R.id.bUp):
			lvParent = (ListView) v.getParent().getParent();
			xpRew = habitItems.getItem(lvParent.getPositionForView(v)).xpRew();
			addXp(xpRew);
			break;
		case (R.id.cDone):
			itemDone(v);
			break;
		}
	}

	private void itemDone(View v) {
		CheckBox itemCheckbox = (CheckBox) v;
		double xpRew = 0;
		ListView parent = (ListView) v.getParent().getParent();
		int position = parent.getPositionForView(v);
		ItemAdapter items = getListFromType(getItemType());
		View row = items.parent.getChildAt(position);
		ActivityItem item = (ActivityItem) items.getItem(position);
		xpRew = item.xpRew();
		if (itemCheckbox.isChecked()) {
			item.finished(true);
			addXp(xpRew);
		} else {
			item.finished(false);
			subtractXp(xpRew);
		}
		if (item.isFinished()) {
			row.setBackgroundColor(0x22222222);
		} else {
			row.setBackgroundColor(0x00000000);
		}
		items.sort(lastSort, direction);
	}

	public void newItemDialog() {
		final String name = eAddNewItem.getText().toString();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View myView = inflater.inflate(R.layout.daily_popup, null);
		final NumberPicker npXP = (NumberPicker) myView.findViewById(R.id.npXP);
		final NumberPicker npHP = (NumberPicker) myView.findViewById(R.id.npHP);
		final NumberPicker npDue = (NumberPicker) myView
				.findViewById(R.id.npDue);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				switch (getItemType()) {
				case DAILY:
					dailyItems.add(new DailyItem(name, npXP.getValue(), npHP
							.getValue(), MainActivity.this));
					break;
				case HABIT:
					habitItems.add(new HabitItem(name, npXP.getValue(), npHP
							.getValue(), MainActivity.this));
					break;
				case TODO:
					todoItems.add(new TodoItem(name, npXP.getValue(), npHP
							.getValue(), npDue.getValue(), MainActivity.this));
				}

			}
		});
		builder.setNegativeButton("Cancel", null);
		builder.setView(myView);
		builder.setTitle(name);
		builder.create().show();
	}

	private int getItemType() {
		return actionBar.getSelectedNavigationIndex();
	}

	private ItemAdapter getListFromType(int type) {
		switch (type) {
		case DAILY:
			return dailyItems;
		case HABIT:
			return habitItems;
		case TODO:
			return todoItems;
		}
		return null;
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
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		ItemAdapter items = null;
		switch (actionBar.getSelectedNavigationIndex()) {
		case DAILY:
			items = dailyItems;
			break;
		case HABIT:
			items = habitItems;
			break;
		case TODO:
			items = todoItems;
			break;
		}
		boolean shouldSort = false;
		switch (item.getItemId()) {
		case (R.id.menu_settings):
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, PreferenceActivity.class);
			startActivityForResult(intent, SETTINGS_REQUEST_CODE);
			break;
		case R.id.menuSortAlpha:
			sortType = ItemAdapter.ALPHA;
			shouldSort = true;
			break;
		case R.id.menuSortByCreated:
			sortType = ItemAdapter.CREATED;
			shouldSort = true;
			break;
		case R.id.menuSortByDueDate:
			sortType = ItemAdapter.DUE_DATE;
			shouldSort = true;
			break;
		case R.id.menuSortByHP:
			sortType = ItemAdapter.HP_PEN;
			shouldSort = true;
			break;
		case R.id.menuSortByLastCompleted:
			sortType = ItemAdapter.LAST_COMPLETED;
			shouldSort = true;
			break;
		case R.id.menuSortByXP:
			sortType = ItemAdapter.XP_REW;
			shouldSort = true;
			break;
		}
		if (shouldSort) {
			checkDirection(sortType);
			items.sort(sortType, direction);
			lastSort = sortType;
			if (direction) {
				System.out.println(String.format(
						"SortType: %d  -  LastSort: %d  -  Direction: true",
						sortType, lastSort));
			} else {
				System.out.println(String.format(
						"SortType: %d  -  LastSort: %d  -  Direction: false",
						sortType, lastSort));
			}
		}
		return true;
	}

	private void checkDirection(int sortType) {
		if (lastSort == sortType) {
			direction = !direction;
		} else {
			direction = true;
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		direction = false;
		Fragment fragment = new DummySectionFragment(this);
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

	public class PreferenceContainer {
		int alertHour, alertMinute;
		int[] alarmTime;
		SharedPreferences settings;
		final SharedPreferences.Editor editor;
		Uri notificationSound, defaultSound;
		boolean notificationEnabled;

		public PreferenceContainer() {
			settings = PreferenceManager
					.getDefaultSharedPreferences(MainActivity.this);
			editor = settings.edit();
			defaultSound = Settings.System.DEFAULT_ALARM_ALERT_URI;
			update();
		}

		public void writeUnfinishedValues() {
			editor.putInt("unfinishedCount",
					MainActivity.this.getUnfinishedCount());
			editor.putFloat("xpGainIfFinished",
					(float) MainActivity.this.xpGainIfFinished());
			editor.putFloat("hpLossIfUnfinished",
					(float) MainActivity.this.hpLossIfUnfinished());
			editor.commit();
		}

		private void update() {
			retrieveNotificationSettings();
		}

		private void retrieveNotificationSettings() {
			notificationEnabled = settings.getBoolean("notificationsEnabled",
					true);
			retrieveNotificationTime();
			retrieveNotificationSound();
		}

		private void retrieveNotificationTime() {
			String alert_time = settings.getString("notificationTime", "19:00");
			String[] pieces = alert_time.split(":");
			alertHour = Integer.parseInt(pieces[0]);
			alertMinute = Integer.parseInt(pieces[1]);
		}

		private void retrieveNotificationSound() {
			final String savedUri = settings.getString("notificationSound", "");
			if (savedUri.length() > 0) {
				// If the stored string is the bogus string...
				if (savedUri.equals("defaultRingtone")) {
					notificationSound = defaultSound;
					editor.putString("notificationSound",
							notificationSound.toString());
					editor.commit();
				} else {
					notificationSound = Uri.parse(savedUri);
				}
			}
		}

		public boolean isNotificationEnabled() {
			return notificationEnabled;
		}

		public int getNotificationHour() {
			return alertHour;
		}

		public int getNotificationMinute() {
			return alertMinute;
		}

		public Uri getNotificationSound() {
			return notificationSound;
		}
	}

	public static class DummySectionFragment extends Fragment {
		public static final String FRAGMENT_ID = "section_number";
		private LayoutInflater inflater;
		private ViewGroup container;
		private View fragmentView;
		private MainActivity mainActivity;
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

		public DummySectionFragment(MainActivity mainActivity) {
			this.mainActivity = mainActivity;
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
			listViewToFill.setOnItemClickListener(mainActivity);
			switch (itemType) {
			case DAILY:
				listViewToFill.setAdapter(dailyItems);
				dailySwipeList = getSwipeDismissList(dailyItems);
				break;
			case HABIT:
				listViewToFill.setAdapter(habitItems);
				habitSwipeList = getSwipeDismissList(habitItems);
				break;
			case TODO:
				listViewToFill.setAdapter(todoItems);
				todoSwipeList = getSwipeDismissList(todoItems);
				break;
			}

		}

		private SwipeDismissList getSwipeDismissList(final ItemAdapter adapter) {
			return new SwipeDismissList(listViewToFill,
					new SwipeDismissList.OnDismissCallback() {
						public SwipeDismissList.Undoable onDismiss(
								ListView listView, final int position) {
							final ActivityItem item = adapter.getItem(position);
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
