package ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.example.familymap.R;
import model.Event;
import model.Model;
import model.Person;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PersonActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        Intent intent = getIntent();
        Model model = Model.initialize();
        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        Person person = model.getPerson(intent.getStringExtra("personID"));

        TextView displayFirstName = findViewById(R.id.display_first_name);
        displayFirstName.setText(person.getFirstName());

        TextView displayLastName = findViewById(R.id.display_last_name);
        displayLastName.setText(person.getLastName());

        TextView displayGender = findViewById(R.id.display_gender);
        if (person.getGender().contains("f")){
            displayGender.setText("Female");
        }
        else displayGender.setText("Male");

        List<Event> lifeEvents = model.getPersonEvents(person.getPersonID());
        List<Person> family = model.getFamily(person.getPersonID());
        Collections.sort(lifeEvents,  new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return o1.getYear() - o2.getYear();
            }
        });

        expandableListView.setAdapter(new ExpandableListAdapter(lifeEvents, family, person));
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter{
        private static final int LIFE_EVENTS_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;
        private final String FATHER_ID;
        private final String MOTHER_ID;
        private final String SPOUSE_ID;

        private final Person person;
        private final List<Event> events;
        private final List<Person> family;

        ExpandableListAdapter(List<Event> lifeEvents, List<Person> family, Person mainPerson) {
            events = lifeEvents;
            this.family = family;
            person = mainPerson;
            FATHER_ID = person.getFatherID();
            MOTHER_ID = person.getMotherID();
            SPOUSE_ID = person.getSpouseID();
        }

        @Override
        public int getGroupCount(){
            return 2;
        }
        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition){
                case LIFE_EVENTS_GROUP_POSITION:
                    return events.size();
                case FAMILY_GROUP_POSITION:
                    return family.size();
                default:
                    throw new IllegalArgumentException("Unrecognized Group Position: " + groupPosition);
            }
        }
        @Override
        public Object getGroup(int groupPosition){
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    return getString(R.string.life_events_title);
                case FAMILY_GROUP_POSITION:
                    return getString(R.string.family_title);
                default:
                    throw new IllegalArgumentException("Unrecognized Group Position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    return events.get(childPosition);
                case FAMILY_GROUP_POSITION:
                    return family.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.person_list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    titleView.setText(R.string.life_events_title);
                    break;
                case FAMILY_GROUP_POSITION:
                    titleView.setText(R.string.family_title);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView;

            switch(groupPosition) {
                case LIFE_EVENTS_GROUP_POSITION:
                    itemView = layoutInflater.inflate(R.layout.life_event_item_view, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                case FAMILY_GROUP_POSITION:
                    itemView = layoutInflater.inflate(R.layout.family_item_view, parent, false);
                    initializePersonView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }
        private void initializeEventView(View eventItemView, final int childPosition) {
            TextView eventInfo = eventItemView.findViewById(R.id.lifeEventInfo);
            Event thisEvent = events.get(childPosition);
            eventInfo.setText(thisEvent.getEventType() + ": "
                              + thisEvent.getCity() + ", " + thisEvent.getCountry()
                              + " (" + thisEvent.getYear() + ")");

            TextView personName = eventItemView.findViewById(R.id.eventPersonName);
            personName.setText(person.getFirstName() + " " + person.getLastName());

            eventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    Model model = Model.initialize();
                    model.setCurrentEvent(events.get(childPosition));
                    startActivity(intent);
                }
            });
        }
        private void initializePersonView(View personItemView, final int childPosition) {
            TextView fmName = personItemView.findViewById(R.id.familyMemberName);
            fmName.setText(family.get(childPosition).getFirstName()
                            + " " + family.get(childPosition).getLastName());

            TextView relationship = personItemView.findViewById(R.id.relationship);
            relationship.setText(getRelationship(family.get(childPosition).getPersonID()));

            personItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    intent.putExtra("personID", family.get(childPosition).getPersonID());
                    PersonActivity.this.startActivity(intent);
                }
            });
        }
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private String getRelationship(String familyMemberID){
            if (FATHER_ID != null){
                if (FATHER_ID.equals(familyMemberID)){
                    return "Father";
                }
            }
            if (MOTHER_ID != null){
                if (MOTHER_ID.equals(familyMemberID)){
                    return "Mother";
                }
            }
            if (SPOUSE_ID != null) {
                if (SPOUSE_ID.equals(familyMemberID)) {
                    return "Spouse";
                }
            }
            return "Child";
        }


    }
}
