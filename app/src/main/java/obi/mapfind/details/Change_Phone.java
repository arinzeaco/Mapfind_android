package obi.mapfind.details;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import java.util.concurrent.TimeUnit;
import obi.mapfind.BaseActivity;
import obi.mapfind.R;

public class Change_Phone extends BaseActivity {
    private Button btnOK;
    private RelativeLayout coordinatorLayout;
   EditText editTxtOldNumber;
    private String[] countryCode;
    private String[] countriesArray;
    private ArrayAdapter<String> countriesAdapter;
    private SearchableSpinner spinnerCountries;
    private String selectedCountryCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean mVerificationInProgress = false;
    private Button verify;
    private FirebaseAuth mAuth;

    private static final String TAG = "PhoneAuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_phone);
        initToolbar("Enter new Number", "");


    /*---------------------------------------------------------------------------
     |                      FIND REFERENCE TO ALL VIEWS
     *---------------------------------------------------------------------------*/
        btnOK = findViewById(R.id.verify);
        editTxtOldNumber = findViewById(R.id.editTxtOldNumber);
        spinnerCountries = findViewById(R.id.spinnerCountries);
        coordinatorLayout = findViewById(R.id
                .layouts);
        mAuth = FirebaseAuth.getInstance();

        editTxtOldNumber.setText("08179746491");
    /*---------------------------------------------------------------------------
     |                          SEARCHABLE SPINNER
     *---------------------------------------------------------------------------*/
        countryCode = new String[]{"+93", "+355", "+213", "+1684", "+376", "+244", "+1264", "+0", "+1268", "+54", "+374", "+297", "+61", "+43", "+994", "+1242", "+973", "+880", "+1246", "+375", "+32", "+501", "+229", "+1441", "+975", "+591", "+387", "+267", "+0", "+55", "+246", "+673", "+359", "+226", "+257", "+855", "+237", "+1", "+238", "+1345", "+236", "+235", "+56", "+86", "+61", "+672", "+57", "+269", "+242", "+242", "+682", "+506", "+225", "+385", "+53", "+357", "+420", "+45", "+253", "+1767", "+1809", "+670", "+593", "+20", "+503", "+240", "+291", "+372", "+251", "+61", "+500", "+298", "+679", "+358", "+33", "+594", "+689", "+0", "+241", "+220", "+995", "+49", "+233", "+350", "+30", "+299", "+1473", "+590", "+1671", "+502", "+44", "+224", "+245", "+592", "+509", "+0", "+504", "+852", "+36", "+354", "+91", "+62", "+98", "+964", "+353", "+972", "+39", "+1876", "+81", "+44", "+962", "+7", "+254", "+686", "+850", "+82", "+965", "+996", "+856", "+371", "+961", "+266", "+231", "+218", "+423", "+370", "+352", "+853", "+389", "+261", "+265", "+60", "+960", "+223", "+356", "+44", "+692", "+596", "+222", "+230", "+269", "+52", "+691", "+373", "+377", "+976", "+1664", "+212", "+258", "+95", "+264", "+674", "+977", "+599", "+31", "+687", "+64", "+505", "+227", "+234", "+683", "+672", "+1670", "+47", "+968", "+92", "+680", "+970", "+507", "+675", "+595", "+51", "+63", "+0", "+48", "+351", "+1787", "+974", "+262", "+40", "+70", "+250", "+290", "+1869", "+1758", "+508", "+1784", "+684", "+378", "+239", "+966", "+221", "+381", "+248", "+232", "+65", "+421", "+386", "+44", "+677", "+252", "+27", "+0", "+211", "+34", "+94", "+249", "+597", "+47", "+268", "+46", "+41", "+963", "+886", "+992", "+255", "+66", "+228", "+690", "+676", "+1868", "+216", "+90", "+7370", "+1649", "+688", "+256", "+380", "+971", "+44", "+1", "+1", "+598", "+998", "+678", "+39", "+58", "+84", "+1284", "+1340", "+681", "+212", "+967", "+38", "+260", "+263"};
        countriesArray = new String[]{"+93 Afghanistan", "+355 Albania", "+213 Algeria", "+1684 American Samoa", "+376 Andorra", "+244 Angola", "+1264 Anguilla", "+0 Antarctica", "+1268 Antigua And Barbuda", "+54 Argentina", "+374 Armenia", "+297 Aruba", "+61 Australia", "+43 Austria", "+994 Azerbaijan", "+1242 Bahamas The", "+973 Bahrain", "+880 Bangladesh", "+1246 Barbados", "+375 Belarus", "+32 Belgium", "+501 Belize", "+229 Benin", "+1441 Bermuda", "+975 Bhutan", "+591 Bolivia", "+387 Bosnia and Herzegovina", "+267 Botswana", "+0 Bouvet Island", "+55 Brazil", "+246 British Indian Ocean Territory", "+673 Brunei", "+359 Bulgaria", "+226 Burkina Faso", "+257 Burundi", "+855 Cambodia", "+237 Cameroon", "+1 Canada", "+238 Cape Verde", "+1345 Cayman Islands", "+236 Central African Republic", "+235 Chad", "+56 Chile", "+86 China", "+61 Christmas Island", "+672 Cocos (Keeling) Islands", "+57 Colombia", "+269 Comoros", "+242 Republic Of The Congo", "+242 Democratic Republic Of The Congo", "+682 Cook Islands", "+506 Costa Rica", "+225 Cote D'Ivoire (Ivory Coast)", "+385 Croatia (Hrvatska)", "+53 Cuba", "+357 Cyprus", "+420 Czech Republic", "+45 Denmark", "+253 Djibouti", "+1767 Dominica", "+1809 Dominican Republic", "+670 East Timor", "+593 Ecuador", "+20 Egypt", "+503 El Salvador", "+240 Equatorial Guinea", "+291 Eritrea", "+372 Estonia", "+251 Ethiopia", "+61 External Territories of Australia", "+500 Falkland Islands", "+298 Faroe Islands", "+679 Fiji Islands", "+358 Finland", "+33 France", "+594 French Guiana", "+689 French Polynesia", "+0 French Southern Territories", "+241 Gabon", "+220 Gambia The", "+995 Georgia", "+49 Germany", "+233 Ghana", "+350 Gibraltar", "+30 Greece", "+299 Greenland", "+1473 Grenada", "+590 Guadeloupe", "+1671 Guam", "+502 Guatemala", "+44 Guernsey and Alderney", "+224 Guinea", "+245 Guinea-Bissau", "+592 Guyana", "+509 Haiti", "+0 Heard and McDonald Islands", "+504 Honduras", "+852 Hong Kong S.A.R.", "+36 Hungary", "+354 Iceland", "+91 India", "+62 Indonesia", "+98 Iran", "+964 Iraq", "+353 Ireland", "+972 Israel", "+39 Italy", "+1876 Jamaica", "+81 Japan", "+44 Jersey", "+962 Jordan", "+7 Kazakhstan", "+254 Kenya", "+686 Kiribati", "+850 Korea North", "+82 Korea South", "+965 Kuwait", "+996 Kyrgyzstan", "+856 Laos", "+371 Latvia", "+961 Lebanon", "+266 Lesotho", "+231 Liberia", "+218 Libya", "+423 Liechtenstein", "+370 Lithuania", "+352 Luxembourg", "+853 Macau S.A.R.", "+389 Macedonia", "+261 Madagascar", "+265 Malawi", "+60 Malaysia", "+960 Maldives", "+223 Mali", "+356 Malta", "+44 Man (Isle of)", "+692 Marshall Islands", "+596 Martinique", "+222 Mauritania", "+230 Mauritius", "+269 Mayotte", "+52 Mexico", "+691 Micronesia", "+373 Moldova", "+377 Monaco", "+976 Mongolia", "+1664 Montserrat", "+212 Morocco", "+258 Mozambique", "+95 Myanmar", "+264 Namibia", "+674 Nauru", "+977 Nepal", "+599 Netherlands Antilles", "+31 Netherlands The", "+687 New Caledonia", "+64 New Zealand", "+505 Nicaragua", "+227 Niger", "+234 Nigeria", "+683 Niue", "+672 Norfolk Island", "+1670 Northern Mariana Islands", "+47 Norway", "+968 Oman", "+92 Pakistan", "+680 Palau", "+970 Palestinian Territory Occupied", "+507 Panama", "+675 Papua new Guinea", "+595 Paraguay", "+51 Peru", "+63 Philippines", "+0 Pitcairn Island", "+48 Poland", "+351 Portugal", "+1787 Puerto Rico", "+974 Qatar", "+262 Reunion", "+40 Romania", "+70 Russia", "+250 Rwanda", "+290 Saint Helena", "+1869 Saint Kitts And Nevis", "+1758 Saint Lucia", "+508 Saint Pierre and Miquelon", "+1784 Saint Vincent And The Grenadines", "+684 Samoa", "+378 San Marino", "+239 Sao Tome and Principe", "+966 Saudi Arabia", "+221 Senegal", "+381 Serbia", "+248 Seychelles", "+232 Sierra Leone", "+65 Singapore", "+421 Slovakia", "+386 Slovenia", "+44 Smaller Territories of the UK", "+677 Solomon Islands", "+252 Somalia", "+27 South Africa", "+0 South Georgia", "+211 South Sudan", "+34 Spain", "+94 Sri Lanka", "+249 Sudan", "+597 Suriname", "+47 Svalbard And Jan Mayen Islands", "+268 Swaziland", "+46 Sweden", "+41 Switzerland", "+963 Syria", "+886 Taiwan", "+992 Tajikistan", "+255 Tanzania", "+66 Thailand", "+228 Togo", "+690 Tokelau", "+676 Tonga", "+1868 Trinidad And Tobago", "+216 Tunisia", "+90 Turkey", "+7370 Turkmenistan", "+1649 Turks And Caicos Islands", "+688 Tuvalu", "+256 Uganda", "+380 Ukraine", "+971 United Arab Emirates", "+44 United Kingdom", "+1 United States", "+1 United States Minor Outlying Islands", "+598 Uruguay", "+998 Uzbekistan", "+678 Vanuatu", "+39 Vatican City State (Holy See)", "+58 Venezuela", "+84 Vietnam", "+1284 Virgin Islands (British)", "+1340 Virgin Islands (US)", "+681 Wallis And Futuna Islands", "+212 Western Sahara", "+967 Yemen", "+38 Yugoslavia", "+260 Zambia", "+263 Zimbabwe"};
        countriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countriesArray);
        spinnerCountries.setAdapter(countriesAdapter);
        spinnerCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountryCode = countryCode[spinnerCountries.getSelectedItemPosition()];
                System.out.println(selectedCountryCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    /*---------------------------------------------------------------------------
     |                             OKAY BUTTON
     *---------------------------------------------------------------------------*/
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPhoneNumberVerification(formatNumber(selectedCountryCode, editTxtOldNumber.getText().toString()));

            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;

            }


            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    Toast.makeText(Change_Phone.this, "nat a vailid phone number", Toast.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {

                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                //  Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(Change_Phone.this, "worked", Toast.LENGTH_LONG).show();

                OTPDialogNewNumber otpDialogNewNumber = new OTPDialogNewNumber(Change_Phone.this);
                otpDialogNewNumber.numberwithout =  editTxtOldNumber.getText().toString();
                otpDialogNewNumber.number = formatNumber(selectedCountryCode, editTxtOldNumber.getText().toString());
                otpDialogNewNumber.verifyid = verificationId;
                otpDialogNewNumber.show("verify");
            }
        };
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
        //mStatusText.setVisibility(View.INVISIBLE);
    }


}