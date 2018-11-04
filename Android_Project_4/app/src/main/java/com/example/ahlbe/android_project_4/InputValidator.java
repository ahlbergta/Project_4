package com.example.ahlbe.android_project_4;

public class InputValidator
{
    //Helper Methods

    /**
     * Return true if the @param is empty
     * @param text string
     * @return boolean
     */
    protected static boolean isEmpty(String text)
    {
        return text.equals("");
    }

    /**
     * return true if @param 'pass1' and @param 'pass2' are the same
     * @param pass1 string
     * @param pass2 string
     * @return boolean
     */
    protected static boolean doPasswordMatch(String pass1, String pass2)
    {
        return pass1.equals(pass2);
    }
    protected static boolean isPasswordStrong(String password)
    {
        //Regex for seeing if the password is 8 characters or longer
        String pattern = "^(?=.*[0-9])" + //At least one digit
                "(?=.*[a-z])" +           //At least one lower case letter
                "(?=.*[A-Z])" +           //At least one upper case letter
                "(?=.*[@#$%^&+=])" +      //At least one special character
                ".{8,}$";
        return password.matches(pattern);

    }
}
