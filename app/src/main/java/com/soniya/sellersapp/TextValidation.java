package com.soniya.sellersapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextValidation extends ValidationHelper implements TextWatcher {

    EditText editTextView = null;

    boolean typeRepeatPass = false;

    String newPassword="";

    Context context;

    public TextValidation(EditText view)  {

        this.editTextView = view;

    }

    public TextValidation(EditText view, Context context) {
        this.editTextView = view;
        this.context = context;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        /*if(editTextView.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD && editTextView.getTag()!=null && editTextView.getTag().equals("repeatPassword"))  {
            Log.i("soni-", "before repPassword change");
            isValidPassword(newPassword);
        }*/
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

            validate();
    }

    @Override
    public void afterTextChanged(Editable s) {

        if(editTextView.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD && editTextView.getTag()== null)   {
            newPassword = editTextView.getText().toString();
            //Log.i("soni-newPassword", newPassword);
        }

        validate();

    }

    public void validate() {



        if (editTextView != null && editTextView.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD
                && editTextView.getTag() != null && editTextView.getTag().equals("repeatPassword")) {
            SharedPreferences pref = context.getSharedPreferences("com.soniya.sellersapp", Context.MODE_PRIVATE);
            String curr_pwd = pref.getString("curr_pwd", "");
           // Log.i("soni-validtn ", "saved pwd in pref = " + curr_pwd);
            if(editTextView.getText().toString().equals(curr_pwd))  {
                Toast.makeText(context, "Password matched!", Toast.LENGTH_SHORT).show();
            }

        }
        else if(editTextView != null && editTextView.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD)  {
            newPassword = editTextView.getText().toString();
            validatePassword();

        }

    }

    private void validateRepeatPassword() {
        String repPass = editTextView.getText().toString();

        if (repPass.equals(newPassword)) {
            //editTextView.set
            Log.i("soni-repPass", "matched "+ newPassword);
            editTextView.setError(null);
        }

        else{
            editTextView.setError("Password does not match!");
        }

    }

    private void validateUsername() {
        String username = editTextView.getText().toString();
        if(username.length()<6) {
            editTextView.setError("Username should be at least 6 characters long!");
        }
        else {
            Pattern pattern;
            Matcher matcher;

            final String usernamerules = ".*[@$!%*?&/].*";
            pattern = Pattern.compile(usernamerules);
            matcher = pattern.matcher(username);

            if (matcher.matches()) {
                editTextView.setError("Invalid characters in Username!");
            }
        }
    }

    private void validatePassword(){
        String pass = editTextView.getText().toString();

        if(pass.length() < 8){
            //passwordMsg = "Password should be at least 6 characters!";
            editTextView.setError("Password should be at least 8 characters!");
        }

        if(pass.length() >= 8 && !isValidPassword(pass))    {
            //passwordMsg = "Password must contain at least 1 Upper case, 1 Lower case letter, 1 Symbol & 1 Number!";
            editTextView.setError("Password must contain at least 1Uppercase, 1Lowercase letter, 1Symbol & 1Number!");
        }



    }

    private boolean isValidPassword(String pass){
        Pattern pattern;
        Matcher matcher;

        //Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character
        final String pwdrules = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

        //final String pwdrules = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$";
        pattern = Pattern.compile(pwdrules);
        matcher = pattern.matcher(pass);

        return matcher.matches();
    }

}
