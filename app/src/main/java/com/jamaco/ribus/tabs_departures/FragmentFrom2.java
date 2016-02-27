package com.jamaco.ribus.tabs_departures;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.jamaco.ribus.R;
import com.jamaco.ribus.database.DatabaseAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by mario on 03.10.15..
 */
public class FragmentFrom2 extends Fragment {

    TextView departure;

    String num;
    String[] stationsList;
    ArrayList<String> pickerName = new ArrayList<>(Collections.singletonList("--pick a station--"));
    ArrayList<Integer> pickerTime = new ArrayList<>(Collections.singletonList(0));
    ArrayList<String> increasedTime, workdayList, saturdayList, sundayList;

    DatabaseAdapter helper;
    final Handler calculate1 = new Handler();
    boolean calculate1isRunning = false;
    final Handler calculate2 = new Handler();
    boolean calculate2isRunning = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater lf = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_from2, container, false);

        TextView number = (TextView) view.findViewById(R.id.tvBusnumber);
        departure = (TextView) view.findViewById(R.id.tvDeparture);
        NumberPicker np = (NumberPicker) view.findViewById(R.id.numberPicker);

        Bundle bundle = getActivity().getIntent().getExtras();
        num = bundle.getString("lineNum");
        number.setText(num);

        //Fill arrays
        helper = new DatabaseAdapter(getActivity().getApplicationContext());

        workdayList = new ArrayList<>(Arrays.asList(helper.getWorkday2(num)));
        if (helper.getSaturday2(num) != null){
            saturdayList = new ArrayList<>(Arrays.asList(helper.getSaturday2(num)));
        } else {
            saturdayList = null;
        }
        if (helper.getSunday2(num) != null) {
            sundayList = new ArrayList<>(Arrays.asList(helper.getSunday2(num)));
        } else {
            sundayList = null;
        }
        stationsList = dataForPicker(num);


        for (String singleElement : stationsList) {
            String[] dividedElement = singleElement.split(";");
            String addName = dividedElement[0];
            Integer addTime = Integer.valueOf(dividedElement[1]);

            pickerName.add(addName);
            pickerTime.add(addTime);
        }

        final String[] convertedPickerName = pickerName.toArray(new String[pickerName.size()]);
        final Integer[] convertedPickerTime = pickerTime.toArray(new Integer[pickerTime.size()]);

        np.setMinValue(0);
        np.setMaxValue(convertedPickerName.length - 1);
        np.setDisplayedValues(convertedPickerName);
        np.setWrapSelectorWheel(false);
        np.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, final int i, final int i1) {

                final int addTime = convertedPickerTime[i1];

                Runnable runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        calculate1isRunning = true;
                        timeCalculate(addTime);
                        calculate1.postDelayed(this, 1000);
                    }
                };

                Runnable runnable2 = new Runnable() {
                    @Override
                    public void run() {
                        timeCalculate(addTime);
                        calculate2isRunning = true;
                        calculate2.postDelayed(this, 1000);
                    }
                };

                if (i1 == 0) {
                    if (calculate1isRunning) {
                        calculate1isRunning = false;
                        calculate1.removeCallbacksAndMessages(null);
                    } else if (calculate2isRunning) {
                        calculate2isRunning = false;
                        calculate2.removeCallbacksAndMessages(null);
                    }
                    departure.setText("");
                } else {

                    if (calculate1isRunning) {
                        calculate1isRunning = false;
                        calculate1.removeCallbacksAndMessages(null);
                        calculate2isRunning = true;
                        calculate2.postDelayed(runnable2, 0);
                    } else if (calculate2isRunning) {
                        calculate2isRunning = false;
                        calculate2.removeCallbacksAndMessages(null);
                        calculate1isRunning = true;
                        calculate1.postDelayed(runnable1, 0);
                    } else {
                        calculate1isRunning = true;
                        calculate1.postDelayed(runnable1, 0);
                    }
                }
            }
        });

        return view;
    }


    // Calculating function
    private void timeCalculate(int addTime){

        //empty increased array every time func is called
        increasedTime = new ArrayList<>();
        ArrayList<String > originalTime = new ArrayList<>();

        //Holidays in 2015
        List<String> holidays = Arrays.asList("01.01.", "06.01.", "06.04.", "01.05.", "04.06.", "22.06.", "25.06.", "05.08.", "15.08.", "08.10.", "25.12.", "26.12.");

        //Take today date
        Date todayDateAndTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.");
        String date = dateFormat.format(todayDateAndTime);

        int dayNumber;

        if (holidays.contains(date)){
            dayNumber = 1;
        } else {
            Calendar calendar = new GregorianCalendar();
            dayNumber = calendar.get(Calendar.DAY_OF_WEEK);
        }

        switch (dayNumber){
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                originalTime = workdayList;
                break;
            case 7:
                originalTime = saturdayList;
                break;
            case 1:
                originalTime = sundayList;
                break;
            default:
                System.out.println("Error with fetching day number.");
        }


        if (originalTime == null){
            departure.setText("This bus does not drive today.");
        }
        //TIME CALCULATION
        else {
            //Take time in hh:mm format
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String nowTime = timeFormat.format(todayDateAndTime);

            String cleanOriginalTime;

            //Time increasing
            for (String singleOriginalTime : originalTime) {
                //Clean times from chars
                if (singleOriginalTime.contains("G")){
                    cleanOriginalTime = singleOriginalTime.replace("G","");
                }
                else if (singleOriginalTime.contains("*")){
                    cleanOriginalTime = singleOriginalTime.replace("*","");
                }
                else {
                    cleanOriginalTime = singleOriginalTime;
                }
                //Add minutes required for bus to arrive to selected station
                try {
                    Date toTime1 = timeFormat.parse(cleanOriginalTime);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(toTime1);
                    cal.add(Calendar.MINUTE, addTime);
                    String calculated = timeFormat.format(cal.getTime());
                    increasedTime.add(calculated);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            String arrival1 = null;
            String arrival2 = null;
            String[] arrayIncreasedTime = increasedTime.toArray(new String[increasedTime.size()]);

            //compare current time with list of times
            for (int i=0; i<originalTime.size(); i++){

                try {
                    Date convertedNowTime = timeFormat.parse(nowTime);
                    Date convertedIncreasedTime = timeFormat.parse(arrayIncreasedTime[i]);

                    if (convertedNowTime.before(convertedIncreasedTime)){
                        arrival1 = arrayIncreasedTime[i];

                        if (i+1 < originalTime.size()){
                            arrival2 = arrayIncreasedTime[i+1];
                        }
                        //if all second buses were gone
                        else {
                            arrival2 = null;
                        }
                        //time is found
                        break;
                    }
                    //else, all buses were gone
                    else {
                        arrival1 = null;
                        arrival2 = null;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            //PRINT ON SCREEN
            Date arrivalnow1;
            Date arrivalnow2;
            long difference1;
            long difference2;
            int min1;
            int min2;
            int min3;
            int hours1;
            int hours2;

            if (arrival1 == null){
                departure.setText("There are no more buses today on this line.");
            } else {
                //First bus comes, second not
                if(arrival2 == null){
                    try {
                        Date formatedNowTime = timeFormat.parse(nowTime);
                        arrivalnow1 = timeFormat.parse(arrival1);
                        difference1 = (arrivalnow1.getTime() - formatedNowTime.getTime()) / (60 * 1000);
                        if (difference1 < 60){
                            min1 = (int) difference1;
                            departure.setText("Bus is departing in approximately:\n"+min1+" min (at: "+arrival1+").\nAnd it is the last bus of the day!");
                        } else {
                            hours1 = (int) difference1/60;
                            min1 = (int) difference1%60;
                            departure.setText("Bus is departing in approximately:\n"+hours1+" h, "+min1+" min (at: "+arrival1+").\nAnd it is the last bus of the day!");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                //Both buses comes
                else {
                    Date formatedNowTime = null;
                    try {
                        formatedNowTime = timeFormat.parse(nowTime);
                        arrivalnow1 = timeFormat.parse(arrival1);
                        arrivalnow2 = timeFormat.parse(arrival2);
                        difference1 = (arrivalnow1.getTime() - formatedNowTime.getTime()) / (60 * 1000);
                        difference2 = (arrivalnow2.getTime() - formatedNowTime.getTime()) / (60 * 1000);


                        if (difference2 < 0) {

                            String midnight = "00:00";
                            Date midnightTime = timeFormat.parse(midnight);

                            String minuteToMidnight = "23:59";
                            Date minuteToMidnightTime = timeFormat.parse(minuteToMidnight);

                            long midnightToArrival = (arrivalnow2.getTime() - midnightTime.getTime()) / (60 * 1000);
                            long currentToMidnight = (minuteToMidnightTime.getTime() - formatedNowTime.getTime()) / (60*1000);

                            difference2 = midnightToArrival + currentToMidnight + 1;
                        }

                        if (difference1 < 60) {
                            min1 = (int) difference1;
                            if (difference2 < 60){
                                min2 = (int) difference2;
                                departure.setText("Bus is departing in approximately:\n"+min1+" min (at: "+arrival1+").\nNext one departs in approximately:\n"+min2+" min (at: "+arrival2+").");
                            } else {
                                hours2 = (int) difference2/60;
                                min3 = (int) difference2%60;
                                departure.setText("Bus is departing in approximately:\n"+min1+" min (at: "+arrival1+").\nNext one departs in approximately:\n"+hours2+" h, "+min3+" min (at: "+arrival2+").");
                            }
                        } else {
                            hours1 = (int) difference1/60;
                            min1 = (int) difference1%60;
                            hours2 = (int) difference2/60;
                            min2 = (int) difference2%60;
                            departure.setText("Bus is departing in approximately:\n"+hours1+" h, "+min1+" min (at: "+arrival1+").\nNext one departs in approximately:\n"+hours2+" h, "+min2+" min (at: "+arrival2+").");
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String[] dataForPicker(String a){

        String[] name = {};

        switch (a){
            case "1":
                name = new String[]{"Bivio;0","Dječja bolnica;3","Bazeni kantrida;4","Kantrida;6","Liburnijska - V. Bratonje;8","3. Maj;10","Krnjevo Liburnijska;11","Krnjevo Zvonimirova;12","Toretta;14","Novi list;16","Mlaka;18","KBC Rijeka;19","Željeznički kolodvor;20","Brajda;21","Žabica;22","Riva;23","Tržnica;24","Fiumara;26","Piramida - Pećine;28","OŠ Pećine;30","Hotel Jadran;31","Hotel Park;33","Pećine;34"};
                break;
            case "1A":
                name = new String[]{"Marčeljeva Draga;0","Mate Balote;1","Ploče;2","Šparići;3","OŠ Kantrida;4","Labinska ulica;5","Liburnijska - V. Bratonje;7","3. Maj;9","Krnjevo Liburnijska;10","Krnjevo Zvonimirova;11","Toretta;13","Novi List;15","Mlaka;17","KBC Rijeka;18","Željeznički kolodvor;19","Brajda;20","Žabica;21","Riva;22","Tržnica;23","Fiumara;25","A.K. Miošića;25"};
                break;
            case "1B":
                name = new String[]{"Strmica;0","Rose Leard;1","Vrlije;1","Trsat;3","Trsat groblje;4","S. Krautzeka I;5","S. Krautzeka II;6","M. Kontuša;8","OŠ Podvežica;9","Radnička;10","Tower;12"};
                break;
            case "2":
                name = new String[]{"Srdoči;0","Blečići;1","Martinkovac;3","Dražice;5","Diračje;7","Ul. I.Č. Belog;8","Zamet centar;10","Baredice;11","Zametska;12","Krnjevo Zvonimirova;15","Toretta;17","Novi List;19","Mlaka;20","KBC Rijeka;22","Željeznički kolodvor;23","Brajda;24","Žabica;25","Riva;26","Tržnica;27","Fiumara;28","Piramida;31","Kumičićeva;33","Teta Roža;34","Mihanovićeva;35","Pošta;36","Paris;37","J. Rakovca;37","Vidikovac;38","Trsat crkva;40","Trsat;40"};
                break;
            case "2A":
                name = new String[]{"Ivana Zavidića;0","Bože Vidasa;1","Zamet tržnica;1","Zamet crkva;4","Zamet - Bože Vidasa;5","Baredice;6","Zametska;7","Krnjevo Zvonimirova;10","Toretta;11","R.Benčića;14","Tehnički fakultet;15","Štranga;17","Potok;18","Željeznički kolodvor;19","Brajda;20","Žabica;21","Riva;22","Tržnica;23","Fiumara;24","A.K. Miošića;25"};
                break;
            case "3":
                name = new String[]{"Grbci;0","Starci;0","Zamet groblje;1","N. Cesta – B. Mohorić;2","Becićeva;4","Zametska;8","Krnjevo Zvonimirova;10","Toreta;12","Novi list;14","Mlaka;16","KBC Rijeka;17","Željeznički kolodvor;18","Manzzonijeva;19","F.I. Guardie;19","Pomerio Park;21","Žrtava fašizma;21","Novi most;23","A.K. Miošiča;24"};
                break;
            case "3A":
                name = new String[]{"Bezjaki;0","Trampov breg;1","Pužići;2","Mulci;4","Drnjevići;5","Pilepići;7","Fantini;8","Braće Mohorić;11","Zamet B. Monjac;13","Baredice;14","Zametska;15","Krnjevo Zvonimirova;18","Toretta;19","Novi List;21","Mlaka;23","KBC Rijeka;24","Željeznički kolodvor;26","Brajda;27","Žabica;28","Riva;29","Tržnica;30","Jelačićev trg;30"};
                break;
            case "4":
                name = new String[]{"Brašćine;0","Kozala – Drenovski put;2","Kapitanovo;3","A. Kovačića;4","Kozala groblje;5","Laginjina;6","Guvernerova palača;9","Fiumara;11"};
                break;
            case "4A":
                name = new String[]{"Vrh Pulca;0","Pulac II;2","Pulac I;3","Galenski laboratorij;4","Internacionalnih brigada;5","Brašćine;8","Katarina I;9","Katarina II;11","Sv. Katarina;12"};
                break;
            case "5":
                name = new String[]{"Drenova;0","Frkaševo;1","Braće Hlača;2","OŠ F. Franković;3","Severinska;5","Bok;6","I.L. Ribara - I. Žorža;7","Staro okretište;8","I.L. Ribara - M. Rustambega;9","I.L. Ribara - S. Vukelića;10","Osječka - Crkva;10","Osječka - Drežnička;11","Osječka zaobilaznica;13","Osječka Lipa;14","Osječka - C. Ilijassich;15","Osječka - Mihačeva Draga;16","Osječka - F. Kresnika;17","1. maja;19","Nikole Tesle;20","Brajda;22","Žabica;23","Riva;24","Tržnica;25","Jelačićev trg;25"};
                break;
            case "5A":
                name = new String[]{"Tibljaši;0","Škurinjska cesta II;2","Škurinje spomenik;3","Škurinjska cesta I;5","Škurinje škola;6","Osječka - Crkva;8","Osječka - Drežnička;8"};
                break;
            case "5B":
                name = new String[]{"Petrci;0"," Kablari;2","Kablarska cesta;3","B. Francetića;4","B. Francetića – Tonići;6","B. Francetića – Pešćevac;6","Benaši - B. Francetića;7","Drenova;9"};
                break;
            case "6":
                name = new String[]{"Novo naselje;0","Nova cesta;1","Turnić;3","Čandekova;5","Studentski dom;6","Štranga;9","Potok;10","Željeznički kolodvor;11","Brajda;12","Žabica;13","Riva;14","Tržnica;15","Fiumara;16","Piramida;18","Kumičićeva;20","Kvaternikova;21","Kvaternikova Tihovac;22","OŠ Vežica;23","Podvežica centar;23","Podvežica;24"};
                break;
            case "7":
                name = new String[]{"Turkovo;0","Pehlin II;3","Pehlin I;5","Rujevica;7","Podmurvice;8","Vukovarska;10","Tehnički fakultet;11","Štranga;12","Potok;14","Željeznički kolodvor;15","Brajda;16","Žabica;17","Riva;18","Tržnica;19","Fiumara;20","Piramida;23","Kumičićeva;25","Teta Roža;26","KBC Sušak;28","Sveta Ana;29","Franje Belulovića;30","Gornja Vežica;32"};
                break;
            case "7A":
                name = new String[]{"Hosti;0","Ul. Hosti;1","Pehlin dj. vrtić;2","Blažićevo;3","Rujevica;5","Podmurvice;6","Vukovarska;8","Tehnički fakultet;9","Štranga;10","Potok;12","Željeznički kolodvor;13","Brajda;14","Žabica;15","Riva;16","Tržnica;17","Fiumara;18","Piramida;21","Kumičićeva;22","Teta Roža;24","KBC Sušak;25","Sveta Ana;27","R. Petrovića I;28","R. Petrovića II;29","Sveti križ;31"};
                break;
            case "8":

                /**
                 Broj koji vraca dateOfWeek je:
                 1 - nedjelja
                 2 - ponedjeljak
                 3 - utorak
                 4 - srijeda
                 5 - cetvrtak
                 6 - petak
                 7 - subota
                 */

                Calendar calendar = new GregorianCalendar(); //Calendar.getInstance();
                int dateOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                if(dateOfWeek == 7 || dateOfWeek == 1){
                    name = new String[]{"Torpedo;0","Baračeva II;1","Baračeva I;3","Mlaka;5","KBC Rijeka;6","Željeznički kolodvor;7","Brajda;8","Žabica;9","Riva;10","Tržnica;11","Titov trg;13","ZZZ;15","Mažuranićev trg;17","Bobijevo;18","Vodosprema;19","Paris;20","J. Rakovca;21","Vidikovac;22","Trsat crkva;23","Trsat;24"};
                    break;
                }
                else{
                    name = new String[]{"Torpedo;0","Baračeva II;1","Baračeva I;3","Mlaka;5","KBC Rijeka;6","Željeznički kolodvor;7","Brajda;8","Žabica;9","Riva;10","Tržnica;11","Titov trg;13","ZZZ;15","Mažuranićev trg;17","Bobijevo;18","Vodosprema;19","Paris;20","J. Rakovca;21","Vidikovac;22","Trsat crkva;23","Trsat;24","Trsat groblje;25","Sveučilišna avenija;26","Kampus;27"};
                    break;
                }
            case "8A":
                name = new String[]{"Sveučilišna avenija;0","Slavka Krautzeka I;1","Slavka Krautzeka II;1","Teta Roža;3","Kumičićeva;4","Sušački neboder;7","Fiumara;8","Jelačićev trg;11"};
                break;
            case "9":
                name = new String[]{"Baraći;0","Sv. Kuzam;2","Draga – Tijani;3","Draga - Sv. Jakov;5","Draga Brig – dom;6","Draga Orlići II;7","Draga Orlići I;8","Draga pod Ohrušvom;9","Sveta Ana;10","KBC Sušak;11","Martina Kontuša;13","OŠ Vežica;14","Radnička;15","D.Gervaisa I Vulk.naselje;16","D.Gervaisa II market;17","D.Gervaisa III;18","Kumičićeva;19","Sušački neboder;21","Fiumara;23","Delta;25"};
                break;
            case "13":
                name = new String[]{"Grohovo;0","Pašac II;5","Pašac I;6","Balda Fućka;7","Gornja Orehovica;9","Donja Orehovica;10","Banska vrata;12","Fiumara;14","Delta;15"};
                break;
            case "KBC":
                name = new String[]{"KBC 3 Poliklinika;0","Nikole Tesle;3","Željeznički kolodvor;4","Brajda;5","Žabica;6","Riva;7","Tržnica;8","Fiumara;9","Piramida;11","Kumičićeva;12","Teta Roža;13","KBC Sušak ulaz;15"};
                break;
            default:
                System.out.print("error in getting pickerName");
                break;
        }
        return name;
    }
}