package com.smartcitytraveller.mobile.ui.panic;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.provider.ContactsContract;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.smartcitytraveller.mobile.R;
import com.smartcitytraveller.mobile.api.dto.NextOfKinConstants;
import com.smartcitytraveller.mobile.api.dto.NextOfKin;
import com.smartcitytraveller.mobile.api.dto.ProductDto;
import com.smartcitytraveller.mobile.api.dto.ResponseDTO;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.common.Util;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.google.android.material.snackbar.Snackbar;
import com.smartcitytraveller.mobile.ui.dashboard.DashboardFragment;
import com.smartcitytraveller.mobile.ui.profile.ProfileDetailsViewModel;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class NextOfKinFragment extends Fragment {

    private static final String TAG = NextOfKinFragment.class.getSimpleName();

    public final int PICK_CONTACT = 230;


    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;

    EditText editTextRelationship, editTextFirstName, editTextLastName, editTextMsisdn;
    Button buttonSave;

    FragmentManager fragmentManager;
    private ProfileDetailsViewModel profileDetailsViewModel;
    private SharedPreferencesManager sharedPreferencesManager;
    boolean relationshipDialogActive = false, phoneNumberDialogActive = false, initiatePanicButton = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            initiatePanicButton = arguments.getBoolean("initiatePanicButton");
        }
        profileDetailsViewModel = new ViewModelProvider(this).get(ProfileDetailsViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_next_of_kin, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        String authentication = sharedPreferencesManager.getAuthenticationToken();

        UserDto userDTO = sharedPreferencesManager.getUser();

        editTextRelationship = view.findViewById(R.id.edit_text_relationship);
        editTextFirstName = view.findViewById(R.id.edit_text_first_name);
        editTextLastName = view.findViewById(R.id.edit_text_last_name);
        editTextMsisdn = view.findViewById(R.id.edit_text_msisdn);

        if (userDTO.getNextOfKin() != null) {
            editTextRelationship.setText(userDTO.getNextOfKin().getRelationship());
            editTextFirstName.setText(userDTO.getNextOfKin().getFirstName());
            editTextLastName.setText(userDTO.getNextOfKin().getLastName());
            editTextMsisdn.setText(userDTO.getNextOfKin().getMsisdn());
        }

        imageViewProfileAvatar = view.findViewById(R.id.circular_image_view_avatar);
        Util.loadAvatar(userDTO, imageViewProfileAvatar);

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

        editTextRelationship.setInputType(InputType.TYPE_NULL);
        editTextRelationship.setOnTouchListener((v, event) -> {
            if (!relationshipDialogActive) {
                relationshipDialogActive = true;
                String[] categories = NextOfKinConstants.relationships.toArray(new String[0]);
                CharSequence[] options = new CharSequence[categories.length];
                for (int i = 0; i < categories.length; i++) {
                    options[i] = categories[i];
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.relationship));
                builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> {
                    relationshipDialogActive = true;
                    dialog.dismiss();
                });
                builder.setItems(options, (dialog, item) -> {
                    String option = (String) options[item];
                    editTextRelationship.setText(option);
                    relationshipDialogActive = false;
                });
                builder.show();
            }
            return false;
        });

        editTextMsisdn.setInputType(InputType.TYPE_NULL);
        editTextMsisdn.setOnTouchListener((v, event) -> {
            if (hasContactsPermission()) {
                if (!phoneNumberDialogActive) {
                    Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    startActivityForResult(i, PICK_CONTACT);
                    phoneNumberDialogActive = true;
                }
            } else {
                requestContactsPermission();
            }
            return false;
        });


        buttonSave = view.findViewById(R.id.button_save);
        buttonSave.setOnClickListener(v -> {
            String relationship = editTextRelationship.getText().toString();
            String firstName = editTextFirstName.getText().toString();
            String lastName = editTextLastName.getText().toString();
            String msisdn = editTextMsisdn.getText().toString();

            NextOfKin nextOfKin = userDTO.getNextOfKin() != null ? userDTO.getNextOfKin() :
                    new NextOfKin();
            nextOfKin.setId(nextOfKin.getId());
            nextOfKin.setRelationship(nextOfKin.getRelationship());
            nextOfKin.setFirstName(nextOfKin.getFirstName());
            nextOfKin.setLastName(nextOfKin.getLastName());
            nextOfKin.setMsisdn(nextOfKin.getMsisdn());

            if (!relationship.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !msisdn.isEmpty()) {
                pd.setMessage("Saving...");
                pd.show();
                userDTO.setNextOfKin(nextOfKin);
                profileDetailsViewModel.hitUpdateUserApi(getActivity(), authentication, userDTO).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
                    @Override
                    public void onChanged(ResponseDTO responseDTO) {
                        pd.dismiss();
                        switch (responseDTO.getStatus()) {
                            case "success":
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                if (initiatePanicButton) {
                                    PanicButtonFragment panicButtonFragment = new PanicButtonFragment();
                                    transaction.replace(R.id.container, panicButtonFragment, PanicButtonFragment.class.getSimpleName());
                                } else {
                                    DashboardFragment dashboardFragment = new DashboardFragment();
                                    transaction.replace(R.id.container, dashboardFragment, DashboardFragment.class.getSimpleName());
                                }
                                transaction.commit();
                                Snackbar.make(view, "Successfully added next of kin!", Snackbar.LENGTH_LONG).show();
                                break;
                            case "failed":
                            case "error":
                                Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            } else {
                if (relationship.isEmpty()) {
                    Snackbar.make(view, "Relationship cannot be null!", Snackbar.LENGTH_LONG).show();
                } else if (firstName.isEmpty()) {
                    Snackbar.make(view, "First Name cannot be null!", Snackbar.LENGTH_LONG).show();
                } else if (lastName.isEmpty()) {
                    Snackbar.make(view, "Last Name cannot be null!", Snackbar.LENGTH_LONG).show();
                } else if (msisdn.isEmpty()) {
                    Snackbar.make(view, "Mobile Number must be present!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean hasContactsPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestContactsPermission() {
        if (!hasContactsPermission()) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS}, PICK_CONTACT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                Cursor cursor = getContext().getContentResolver().query(contactUri, null, null, null, null);
                cursor.moveToFirst();
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String msisdn = cursor.getString(column);
                editTextMsisdn.setText(msisdn);
                phoneNumberDialogActive = false;
            }
        }
    }
}