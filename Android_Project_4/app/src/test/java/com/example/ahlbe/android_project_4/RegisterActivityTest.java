package com.example.ahlbe.android_project_4;

import org.junit.Assert;
import org.junit.Test;

public class RegisterActivityTest
{
    @Test
    public void emptyFields()
    {
        RegisterActivity registerActivity = new RegisterActivity();

        Assert.assertTrue(registerActivity.isEmpty(""));
        Assert.assertFalse(registerActivity.isEmpty("123"));
    }
    @Test
    public void passwordMatch()
    {
        RegisterActivity registerActivity = new RegisterActivity();

        Assert.assertTrue(registerActivity.doPasswordMatch("hello", "hello"));
        Assert.assertFalse(registerActivity.doPasswordMatch("apple", "Apple"));
    }
}
