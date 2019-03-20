package com.soniya.sellersapp;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupValidation extends ValidationHelper implements TextWatcher {

    EditText editTextView = null;

    boolean typeRepeatPass = false;

    String newPassword="";

    public SignupValidation(EditText view)  {

        this.editTextView = view;

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

        //if(editTextView.getText().hashCode() == s.hashCode()){
            //Log.i("soni-", "its "+editTextView.getInputType() );
        //}

        if(editTextView.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD && editTextView.getTag()!=null && editTextView.getTag().equals("repeatPassword")){
            //Log.i("soni-", "its rep password");
            //validateRepeatPassword();

        }
        else if(editTextView.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD)  {
            //Log.i("soni-", "its password");
            newPassword = editTextView.getText().toString();
            //Log.i("soni-newPassword", newPassword);
            validatePassword();

        }

        /*else if(editTextView.getInputType() == InputType.TYPE_TEXT_VARIATION_PERSON_NAME){
            //Log.i("soni-", "its username");
            validateUsername();

            //should call db to get uname and match to avoid duplicate username
            //isUsernameAvailable();

        }
*/

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
        if(pass.length() >= 8 && isValidPassword(pass))    {
            //passwordMsg = "Password must contain at least 1 Upper case, 1 Lower case letter, 1 Symbol & 1 Number!";
            editTextView.setError("Password must contain at least 1Uppercase, 1Lowercase letter, 1Symbol & 1Number!");
        }

        if(pass.length() < 8){
            //passwordMsg = "Password should be at least 6 characters!";
            editTextView.setError("Password should be at least 6 characters!");
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
