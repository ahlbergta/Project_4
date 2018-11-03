package com.example.ahlbe.android_project_4;

import org.junit.Assert;
import org.junit.Test;

public class InputValidatorTest
{
    @Test
    public void emptyFields()
    {


        Assert.assertTrue(InputValidator.isEmpty(""));
        Assert.assertFalse(InputValidator.isEmpty("123"));
    }
    @Test
    public void passwordMatch()
    {


        Assert.assertTrue(InputValidator.doPasswordMatch("hello", "hello"));
        Assert.assertFalse(InputValidator.doPasswordMatch("apple", "Apple"));
    }
    @Test
    public void isPasswordStrong()
    {
        Assert.assertTrue(InputValidator.isPasswordStrong("201823pP%"));
        Assert.assertTrue(InputValidator.isPasswordStrong("lioqw2P%"));
        Assert.assertFalse(InputValidator.isPasswordStrong("lP2skw")); //Test for password to short
        Assert.assertFalse(InputValidator.isPasswordStrong("uiw2qsdaP")); //Test for no special character
        Assert.assertFalse(InputValidator.isPasswordStrong("uioplsHw%"));//Test for no number
        Assert.assertFalse(InputValidator.isPasswordStrong("mnjxs2&l2")); //Test for no capital letter
    }
}
