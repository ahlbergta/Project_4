package com.example.ahlbe.android_project_4;

public class InputValidator
{
    //Helper Methods

    /**
     * Return true if the @param is empty
     * @param text string from editText
     * @return boolean
     */
    protected static boolean isEmpty(String text)
    {
        return text.equals("");
    }

    /**
     * return true if @param 'pass1' and @param 'pass2' are the same
     * @param pass1 string from edittext
     * @param pass2 string from edittext
     * @return boolean
     */
    protected static boolean doPasswordMatch(String pass1, String pass2)
    {
        return pass1.equals(pass2);
    }

    /**
     * the method returns true is password has at least 8 characters and no semicolon
     * @param password string from editText
     * @return
     */
    protected static boolean isPasswordStrong(String password)
    {
        //Still needs updating
        String pattern = //At least one digit
//                "^[^;]" + //At least one lower case letter
                "^.{8,}$";
        return password.matches(pattern);

    }
}
