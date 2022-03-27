package com.smartcitytraveller.mobile.ui.qrcode;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartcitytraveller.mobile.R;
import com.smartcitytraveller.mobile.api.dto.ProfileDto;
import com.smartcitytraveller.mobile.common.Common;
import com.smartcitytraveller.mobile.database.SharedPreferencesManager;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class QRCodeFragment extends Fragment {
    private static final String TAG = QRCodeFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar, imageViewQRCode;
    TextView textViewProfileFullName;

    SharedPreferencesManager sharedPreferencesManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_q_r_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(getActivity());

        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        ProfileDto profileDTO = sharedPreferencesManager.getProfile();

        imageViewProfileAvatar = view.findViewById(R.id.image_view_profile_avatar);
        imageViewQRCode = view.findViewById(R.id.image_view_qr_code);
        textViewProfileFullName = view.findViewById(R.id.text_view_full_name);

        Map<String, String> qrData = new HashMap<>();
        qrData.put("msisdn", profileDTO.getMsisdn());
        qrData.put("firstName", profileDTO.getFirstName());
        qrData.put("lastName", profileDTO.getLastName());
        String contents = new Gson().toJson(qrData);

        Bitmap bitmap = Common.generateQRCode(contents);
        imageViewQRCode.setImageBitmap(bitmap);
        Common.loadAvatar(profileDTO.isAvatarAvailable(), imageViewProfileAvatar, profileDTO.getId());
        String fullName = profileDTO.getFirstName() + " " + profileDTO.getLastName();
        textViewProfileFullName.setText(fullName);

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }
}