package com.example.ablesson1;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GoogleMapFragment extends Fragment implements Constants {

    private TextView textAddress;
    private GoogleMap mMap;
    private List<Marker> markers = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_map, container, false);

        //окно показывающее адрес
        textAddress = view.findViewById(R.id.textAddress);
        //поиск введеного адреса на карте
        initSearchByAddress(view);

        //получаем карту из SupportMapFragment
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment));
        Objects.requireNonNull(mapFragment).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                //установим тип карты
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                //очистим карту от старых маркеров
                googleMap.clear();
                //установим координаты
                LatLng coordinates = new LatLng(55.3960825, 37.7685783);
                //поставим маркер
                googleMap.addMarker(new MarkerOptions().position(coordinates).title("Marker"));
                //установим позицию камеры
                CameraPosition position = CameraPosition.builder()
                        .target(coordinates)
                        .zoom(13)
                        .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
                //вешаем слушатель на долгое нажатие на карте
                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        //getAddress(latLng);
                        //addMarker(latLng);
                        showWeather(latLng);
                    }
                });

            }
        });
        return view;
    }

    // Получаем адрес по координатам
    //метод отключен т.к. мы сразу показываем погоду в данной точке
    private void getAddress(LatLng latLng) {
        final Geocoder geocoder = new Geocoder(getActivity());
        // Поскольку Geocoder работает по интернету, создаём отдельный поток
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    textAddress.post(new Runnable() {
                        @Override
                        public void run() {
                            textAddress.setText(addresses.get(0).getAddressLine(0));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //добавление метки на карту
    //метод отключен т.к. мы сразу показываем погоду в данной точке
    private void addMarker(LatLng latLng) {
        String title = Integer.toString(markers.size());
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title));
        markers.add(marker);
    }

    private void showWeather(LatLng latLng) {
        Parcel parcel;
        if (getArguments() != null) {
            parcel = (Parcel) getArguments().getSerializable(PARCEL);
            parcel.setCityName(null);
        } else {
            parcel = new Parcel(null);
        }
        parcel.setLatLng(latLng);
        CityFragment detail = CityFragment.create(parcel);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.first_fragment, detail);  // замена фрагмента
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack("Start");
        ft.commit();
        Log.d("MyLog", "GoogleMapFragment: showWeather: вызван");
    }

    //находит адрес на карте
    private void initSearchByAddress(View view) {
        //инициализируем посковую строку и кнопку поиска
        final EditText textSearch = view.findViewById(R.id.searchAddress);
        view.findViewById(R.id.buttonSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Geocoder geocoder = new Geocoder(getActivity());
                final String searchText = textSearch.getText().toString();
                // Операция получения занимает некоторое время и работает по
                // интернету. Поэтому её необходимо запускать в отдельном потоке
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Получаем координаты по адресу
                        try {
                            List<Address> addresses = geocoder.getFromLocationName(searchText, 1);
                            if (addresses.size() > 0) {
                                final LatLng location = new LatLng(addresses.get(0).getLatitude(),
                                        addresses.get(0).getLongitude());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mMap.addMarker(new MarkerOptions()
                                                .position(location)
                                                .title(searchText));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, (float) 15));
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}
