package com.udacity.jwdnd.course1.cloudstorage.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignupPage {
    @FindBy(id = "inputFirstName")
    private WebElement inputFirstName;

    @FindBy(id="inputLastName")
    private WebElement inputLastName;

    @FindBy(id="inputUsername")
    private WebElement inputUsername;

    @FindBy(id="inputPassword")
    private WebElement inputPassword;

    @FindBy(id = "signup-btn")
    private WebElement submitButton;

    @FindBy(id = "success-msg")
    private WebElement successMsg;

    @FindBy(id = "error-msg")
    private WebElement errorMsg;

    @FindBy(id = "loginLink")
    private WebElement loginLink;

    public SignupPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void signupUser(String firstname, String lastname, String username, String password){
        inputFirstName.sendKeys(firstname);
        inputLastName.sendKeys(lastname);
        inputPassword.sendKeys(password);
        inputUsername.sendKeys(username);
        submitButton.click();
    }

    public boolean isSignupSuccessful(){
        return successMsg.isDisplayed();
    }

    public boolean hasSignupError(){
        return errorMsg.isDisplayed();
    }

}
