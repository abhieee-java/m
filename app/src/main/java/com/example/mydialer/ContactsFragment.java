package com.example.mydialer;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    private EditText firstNameInput, surnameInput, numberInput, companyInput, jobTitleInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        // 1. Find all the input boxes by their IDs
        firstNameInput = view.findViewById(R.id.input_first_name);
        surnameInput = view.findViewById(R.id.input_surname);
        numberInput = view.findViewById(R.id.input_number);
        companyInput = view.findViewById(R.id.input_company);
        jobTitleInput = view.findViewById(R.id.input_job_title);

        TextView saveBtn = view.findViewById(R.id.btn_save_contact);

        // 2. What happens when we click the checkmark
        saveBtn.setOnClickListener(v -> {
            String firstName = firstNameInput.getText().toString().trim();
            String surname = surnameInput.getText().toString().trim();
            String number = numberInput.getText().toString().trim();
            String company = companyInput.getText().toString().trim();
            String jobTitle = jobTitleInput.getText().toString().trim();

            if (number.isEmpty()) {
                Toast.makeText(getContext(), "Phone number is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Make sure we have permission before saving
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                saveContactToPhone(firstName, surname, number, company, jobTitle);
            } else {
                Toast.makeText(getContext(), "Missing Write Contacts Permission!", Toast.LENGTH_SHORT).show();
                // You can request permission here if needed
            }
        });

        return view;
    }

    // 3. The "Engine" that pushes the data to Android's system
    private void saveContactToPhone(String firstName, String surname, String phone, String company, String jobTitle) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        // Create a new raw contact
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // Add Name
        String fullName = firstName + " " + surname;
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, fullName.trim())
                .build());

        // Add Phone Number
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        // Add Company and Job Title (if they typed it)
        if (!company.isEmpty() || !jobTitle.isEmpty()) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                    .build());
        }

        // Execute the save
        try {
            requireContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(getContext(), "Contact Saved Successfully!", Toast.LENGTH_SHORT).show();
            
            // Clear the inputs after saving
            firstNameInput.setText("");
            surnameInput.setText("");
            numberInput.setText("");
            companyInput.setText("");
            jobTitleInput.setText("");
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error saving contact.", Toast.LENGTH_SHORT).show();
        }
    }
}
