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
 * Created by mario and jaco on 03.10.15..
 */
public class FragmentFrom1 extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_from1, container, false);

        TextView number = (TextView) view.findViewById(R.id.tvBusnumber);
        departure = (TextView) view.findViewById(R.id.tvDeparture);
        NumberPicker np = (NumberPicker) view.findViewById(R.id.numberPicker);

        Bundle bundle = getActivity().getIntent().getExtras();
        num = bundle.getString("lineNum");
        number.setText(num);

        //Fill arrays
        helper = new DatabaseAdapter(getActivity().getApplicationContext());

        workdayList = new ArrayList<>(Arrays.asList(helper.getWorkday1(num)));
        if (helper.getSaturday1(num) != null){
            saturdayList = new ArrayList<>(Arrays.asList(helper.getSaturday1(num)));
        } else {
            saturdayList = null;
        }
        if (helper.getSunday1(num) != null) {
            sundayList = new ArrayList<>(Arrays.asList(helper.getSunday1(num)));
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

        System.out.println("Before timer "+saturdayList);

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
                name = new String[]{"Pećine;0","J.P. Kamova WTC;1","J.P. Kamova;3","Pećine Ž. Kolodvor;4","Sušački neboder;7","Fiumara;9","Trg RH;10","Riječki neboder;11","Brajda;12","Željeznički kolodvor;13","KBC Rijeka;14","Mlaka;16","Novi list;17","Toretta;20","Krnjevo Liburnijska;22","3. Maj;24","Liburnijska - V. Bratonje;26","Kantrida;27","Bazeni Kantrida;30","Dječja bolnica;30","Bivio;34"};
                break;
            case "1A":
                name = new String[]{"A.K. Miošića;0"," Sušački neboder;2","Fiumara;4","Trg RH;6","Riječki neboder;6","Brajda;8","Željeznički kolodvor;8","KBC Rijeka;10","Mlaka;11","Novi list;13","Toretta;15","Krnjevo Liburnijska;17","3. Maj;20","Liburnijska - V. Bratonje;21","Labinska ulica;23","OŠ Kantrida;23","Šparići;25","Ploče;26","Mate Balote;27","Marčeljeva Draga;28"};
                break;
            case "1B":
                name = new String[]{"Tower;0","Radnička;4","Podvežica centar;6","Z. Kučića III;8","KBC Sušak;10","Mihanovićeva;11","Drage Ščitara;13","Park heroja;15","Trsat;16","Josipa Kuflaneka;18","Slave Raškaj;18","Strmica;19"};
                break;
            case "2":
                name = new String[]{"Trsat;0","Trsat groblje;1","Slavka Krautzeka I;2","Slavka Krautzeka II;3","Teta Roža;4","Kumičićeva;6","Sušački neboder;8","Fiumara;10","Trg RH;12","Riječki neboder;12","Brajda;14","Željeznički kolodvor;14","KBC Rijeka;16","Mlaka;17","Novi List;19","Toretta;21","Krnjevo Zametska;23","Zametska;25","Baredice;26","Zamet centar;28","Ul. I.Č. Belog;29","Diračje;30","Dražice;33","Martinkovac I;35","Srdoči;37"};
                break;
            case "2A":
                name = new String[]{"A.K. Miošića;0","Sušački neboder;2","Fiumara;4","Trg RH;6","Riječki neboder;6","Brajda;8","Nikole Tesle;9","Potok;10","Štranga;11","Tehnički fakultet;12","R. Benčića;14","Toretta;17","Krnjevo Zametska;19","Zametska;21","Baredice;22","Zamet - Bože Vidasa;23","Zamet crkva;24","Zamet tržnica;26","Bože Vidasa;27","Ivana Zavidića;28"};
                break;
            case "3":
                name = new String[]{"A.K. Miošića;0","Sušački neboder;2","Ivana Grohovca;4","Žrtava fašizma;6","Pomerio park;7","F.I. Guardie;8","N. Tesle;9","KBC Rijeka;11","Mlaka;12","Novi list;14","Toretta;16","Krnjevo Zametska;18","Zametska;20","Becićeva;22","N. Cesta – B. Mohorić;23","Fantini;25","Pilepići;26","Drnjevići;28","J. Mohorića;29","Selinari;30","Šumc;30","Grbci;31"};
                break;
            case "3A":
                name = new String[]{"Jelačićev trg;0","Trg RH;2","Riječki neboder;2","Brajda;4","Željeznički kolodvor;4","KBC Rijeka;6","Mlaka;7","Novi List;9","Toretta;11","Krnjevo Zametska;13","Zametska;15","Baredice;16","Zamet B. Monjac;18","Braće Mohorić;20","N. Cesta - B. Mohorić;21","Fantini;23","Pilepići;24","Drnjevići;25","Mulci;26","Pužići;28","Trampov breg;29","Bezjaki;30"};
                break;
            case "4":
                name = new String[]{"Fiumara;0","Palazzo Modello;1","Trg RH;2","Riječki neboder;3","Manzzonijeva;4","1. maja;6","Tizianova;8","Belveder;9","Kozala groblje;10","Ante Kovačića;11","Kapitanovo;12","Kozala - Drenovski put;13","Kozala;15","Vinas;16","Brašćine okretište;17"};
                break;
            case "4A":
                name = new String[]{"Sv. Katarina;0","Katarina II;1","Katarina I;3","Brašćine;6","Internacionalnih brigada;7","Galenski laboratorij;9","Pulac I;10","Pulac II;12","Vrh Pulca;13"};
                break;
            case "5":
                name = new String[]{"Jelačićev trg;0","Trg RH;2","Riječki neboder;2","Manzzonijeva;4","1. maja;5","Osječka - F. Kresnika;7","Osječka - Mihačeva Draga;8","Osječka Lipa;10","Osječka zaobilaznica;11","Osječka - Drežnička;12","Osječka - Crkva;13","I.L. Ribara - S. Vukelića;14","I.L. Ribara - M. Ruslambega;15","Staro okretište;16","I.L. Ribara - I. Žorža;17","Bok;18","Severinska;19","OŠ F. Franković;21","Braće Hlača;22","Frkaševo;23","Drenova;24"};
                break;
            case "5A":
                name = new String[]{"Osječka - Drežnička;0","Osječka - Crkva;1","Škurinjska cesta I;3","Škurinje spomenik;5","Tibljaši;8"};
                break;
            case "5B":
                name = new String[]{"Drenova;0","Benaši – B. Francetića;2","B. Francetića – Pešćevac;3","B. Francetića – Tonići;3","B. Francetića;5","Kablarska cesta;6","Kablari;7","Petrci;9"};
                break;
            case "6":
                name = new String[]{"Podvežica;0","Podvežica centar;1","OŠ Vežica;2","Kvaternikova Tihovac;3","Kvaternikova;4","Kumičićeva;5","Sušački neboder;7","Fiumara;8","Trg RH;10","Riječki neboder;11","Brajda;12","Nikole Tesle;13","Potok;14","Štranga;15","Tehnički fakultet;16","Studentski dom;17","Čandekova;18","Turnić;20","Dom umirovljenika;22","G. Carabino;23","Vidovićeva;24","Novo naselje;25"};
                break;
            case "7":
                name = new String[]{"Gornja Vežica;0","F. Belulovića - Z. Kučića;1","Zdravka Kučića I;2","Zdravka Kučića II;3","Zdravka Kučića III;4","KBC Sušak;7","Teta Roža;9","Kumičićeva;10","Sušački neboder;13","Fiumara;14","Palazzo Modello;15","Trg RH;16","Riječki neboder;17","Brajda;18","Nikole Tesle;19","Potok;20","Štranga;21","Tehnički fakultet;22","Vukovarska;24","Podmurvice;26","Čepićka;27","Rujevica;27","Pehlin I;30","Pehlin škola;31","Pehlin II;32","Turkovo;34"};
                break;
            case "7A":
                name = new String[]{"Sveti križ;0","R. Petrovića II;2","R. Petrovića I;3","Sveta Ana;4","KBC Sušak;5","Teta Roža;8","Kumičićeva;9","Sušački neboder;11","Fiumara;13","Palazzo Modello;14","Trg RH;15","Riječki neboder;16","Brajda;17","Nikole Tesle;18","Potok;19","Štranga;20","Tehnički fakultet;21","Vukovarska;23","Podmurvice;25","Čepićka;25","Rujevica;26","Blažićevo;27","Pehlin dj. vrtić;28","Ul. Hosti;29","Hosti;30"};
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
                    name = new String[]{"Trsat;0","Trsat groblje;1","Slavka Krautzeka I;2","Slavka Krautzeka II;3","Mihanovićeva;4","Pošta;5","Paris;6","Vodosprema;7","Bobijevo;9","ZZZ;10","Fiumara;14","Trg RH;17","Riječki neboder;18","Brajda;19","Željeznički kolodvor;20","KBC Rijeka;21","Mlaka – Baračeva;23","Baračeva I;24","Baračeva II;26","Torpedo;28"};
                    break;
                }
                else{
                    name = new String[]{"Kampus;0","KBC Sušak;2","Mihanovićeva;3","Pošta;4","Paris;5","Vodosprema;6","Bobijevo;8","ZZZ;9","Fiumara;13","Trg RH;16","Riječki neboder;17","Brajda;18","Željeznički kolodvor;19","KBC Rijeka;20","Mlaka – Baračeva;22","Baračeva I;23","Baračeva II;25","Torpedo;27"};
                    break;
                }
            case "8A":
                name = new String[]{"Jelačićev trg;0","Fiumara;2","Piramida;4","Kumičićeva;6","Teta Roža;7","KBC Sušak;9","Radmila Matejčić;11","Sveučilišna avenija;13"};
                break;
            case "9":
                name = new String[]{"Delta;0","Piramida;2","Kumičićeva;3","D.Gervaisa III;4","D.Gervaisa II market;6","D.Gervaisa I Vulk.naselje;7","Radnička;9","OŠ Vežica;11","Podvežica  centar;12","Zdravka Kučića III;14","Sveta Ana;15","Draga pod Ohrušvom;17"," Orlići I;18","Draga Orlići II;19","Draga Brig – dom;20","Draga - Sv. Jakov;21","Draga – Tijani;22","Sv. Kuzam;23","Baraći;25"};
                break;
            case "13":
                name = new String[]{"Delta;0","Banska vrata;3","Donja Orehovica;5","Gornja Orehovica;6","Balda Fućka;8","Pašac I;10","Pašac II;11","Grohovski put;14","Grohovo;15"};
                break;
            case "KBC":
                name = new String[]{"KBC Sušak ulaz;0","Teta Roža;2","Kumičićeva;3","Sušački neboder;5","Fiumara;6","Trg RH;9","Riječki neboder;10","Brajda;11","Željeznički kolodvor;12","KBC 1 porta;14","KBC 2 hitna;15","KBC 3 Poliklinika;16"};
                break;
            default:
                System.out.print("error in getting pickerName");
                break;
        }
        return name;
    }

}