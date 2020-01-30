package ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.familymap.R;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import model.Event;
import model.Model;
import model.Person;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFrag extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private ImageView gender;
    private TextView personName;
    private TextView eventDetails;
    private Map<Marker, Event> markersToEvents;
    private Event selectedEvent;
    private LinearLayout display;
    private List<Polyline> lines;
    private Model model;

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        //Iconify.with(new FontAwesomeModule());
        //setHasOptionsMenu(true);
        View view = layoutInflater.inflate(R.layout.map_frag, container, false);

        gender = (ImageView) view.findViewById(R.id.gender);
        personName = view.findViewById(R.id.personName);
        eventDetails = view.findViewById(R.id.eventDetails);

        markersToEvents = new HashMap<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }
/*    @Override
    public void OnCreateOptionsMenu(Menu menu, MenuInflater inflater){
    inflater.inflate(R.menu.menu, menu);
    MenuItem personMenuItem = menu.findItem(R.id.personMenuItem);
    personMenuItem.setIcon(new IconDrawable(this,
            FontAwesomeIcons.fa_user)
            .colorRes(R.color.colorPrimary)
            .actionBarSize());
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        model = Model.initialize();


        createMarkers();
        setMarkerListener();

        if (model.getCurrentEvent() != null){
            LatLng currentEvent = new LatLng(model.getCurrentEvent().getLatitude(), model.getCurrentEvent().getLongitude());
            map.animateCamera(CameraUpdateFactory.newLatLng(currentEvent));
            selectedEvent = model.getCurrentEvent();
            clickEvent(selectedEvent);
        }
    }

    private void createMarkers() {
        Map<String, Event> events = model.getEvents();
        Map<String, Model.MapColor> typeColors = model.getEventTypeColors();
        System.out.println(events.size());
        for (Event event : events.values()){
            LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
            MarkerOptions options = new MarkerOptions()
                    .position(location)
                    //.icon(BitmapDescriptorFactory.defaultMarker(30));
                    .icon(BitmapDescriptorFactory.defaultMarker(typeColors.get(event.getEventType().toLowerCase()).getColorValue()));

            Marker marker = map.addMarker(options);
            markersToEvents.put(marker, event);

        }

    }

    void setMarkerListener() {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Event thisEvent = markersToEvents.get(marker);
                model.setCurrentEvent(thisEvent);
                selectedEvent = thisEvent;
                clickEvent(thisEvent);
                return false;
            }
        });
    }

    private void clickEvent(Event thisEvent){
        Person thisPerson = model.getPeople().get(thisEvent.getPersonID());
        LatLng location = new LatLng(thisEvent.getLatitude(), thisEvent.getLongitude());

        personName.setText(thisPerson.getFirstName() + " " + thisPerson.getLastName());
        eventDetails.setText(thisEvent.getEventType().toUpperCase() + ": " + thisEvent.getCity() + ", "
                + thisEvent.getCountry() + " (" + thisEvent.getYear() + ")");

        map.animateCamera(CameraUpdateFactory.newLatLng(location));

        if (thisPerson.getGender().contains("f")){
            gender.setImageResource(R.drawable.female);
        }
        else {
            gender.setImageResource(R.drawable.male);
        }
        setDisplayListener();
    }


    void setDisplayListener() {
        display = getView().findViewById(R.id.display);
        display.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //switch to Person Activity
                Intent intent = new Intent(getActivity(), PersonActivity.class);
                intent.putExtra("personID", selectedEvent.getPersonID());
                getActivity().startActivity(intent);
            }
        });
    }
}
