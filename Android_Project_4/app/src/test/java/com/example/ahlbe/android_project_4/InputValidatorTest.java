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
        Assert.assertTrue(InputValidator.isPasswordStrong("20182300ppp"));
        Assert.assertFalse(InputValidator.isPasswordStrong("200202"));
    }
}
