package com.smartcitytraveller.mobile.ui.panic;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.smartcitytraveller.mobile.R;
import com.smartcitytraveller.mobile.api.dto.NextOfKinConstants;
import com.smartcitytraveller.mobile.api.dto.UserDto;
import com.smartcitytraveller.mobile.common.Util;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.google.android.material.snackbar.Snackbar;
import com.smartcitytraveller.mobile.ui.dashboard.DashboardFragment;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class NextOfKinFragment extends Fragment {

    private static final String TAG = NextOfKinFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;

    EditText editTextRelationship, editTextFirstName, editTextLastName, editTextMsisdn;
    Button buttonSave;

    FragmentManager fragmentManager;
    private SharedPreferencesManager sharedPreferencesManager;
    boolean dialogActive = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        editTextMsisdn.setInputType(InputType.TYPE_NULL);

        imageViewProfileAvatar = view.findViewById(R.id.circular_image_view_avatar);
        Util.loadAvatar(userDTO, imageViewProfileAvatar);

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

        editTextRelationship.setInputType(InputType.TYPE_NULL);
        editTextRelationship.setOnTouchListener((v, event) -> {
            if (!dialogActive) {
                dialogActive = true;
                String[] categories = NextOfKinConstants.relationships.toArray(new String[0]);
                CharSequence[] options = new CharSequence[categories.length];
                for (int i = 0; i < categories.length; i++) {
                    options[i] = categories[i];
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.relationship));
                builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> {
                    dialogActive = true;
                    dialog.dismiss();
                });
                builder.setItems(options, (dialog, item) -> {
                    String option = (String) options[item];
                    editTextRelationship.setText(option);
                    dialogActive = false;
                });
                builder.show();
            }
            return false;
        });


        buttonSave = view.findViewById(R.id.button_save);
        buttonSave.setOnClickListener(v -> {
            String relationship = editTextRelationship.getText().toString();
            String firstName = editTextFirstName.getText().toString();
            String lastName = editTextLastName.getText().toString();
            String msisdn = editTextMsisdn.getText().toString();


            if (!relationship.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !msisdn.isEmpty()) {
                // TODO SAVE
                DashboardFragment dashboardFragment = new DashboardFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, dashboardFragment, DashboardFragment.class.getSimpleName());
                transaction.commit();

            } else {
                if (relationship.isEmpty()) {
                    Snackbar.make(view, "Relationship cannot be null!", Snackbar.LENGTH_LONG).show();
                } else if (firstName.isEmpty()) {
                    Snackbar.make(view, "First Name cannot be null!", Snackbar.LENGTH_LONG).show();
                } else if (lastName.isEmpty()) {
                    Snackbar.make(view, "Last Name cannot be null!", Snackbar.LENGTH_LONG).show();
                } else if (msisdn.isEmpty()) {
                    Snackbar.make(view, "Mobile Number must be greater than zero!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}