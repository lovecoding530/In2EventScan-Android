package com.in2event.in2eventscan.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;
import com.in2event.in2eventscan.R;
import com.in2event.in2eventscan.activities.MainActivity;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CodeSannerFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private ZXingScannerView mScannerView;

    private Context context;

    private OnScanCodeListener mListener;

    public CodeSannerFragment() {
        // Required empty public constructor
    }

    public static CodeSannerFragment newInstance(Context context) {
        CodeSannerFragment fragment = new CodeSannerFragment();
        fragment.context = context;
        fragment.mListener = (OnScanCodeListener) context;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_code_sanner, container, false);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        ViewGroup contentFrame = view.findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(context);
        contentFrame.addView(mScannerView);

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        mListener.onScanCode(rawResult.getText());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mScannerView.startCamera();
                } else {
                    Toast.makeText(context, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public interface OnScanCodeListener {
        // TODO: Update argument type and name
        void onScanCode(String code);
    }
}
