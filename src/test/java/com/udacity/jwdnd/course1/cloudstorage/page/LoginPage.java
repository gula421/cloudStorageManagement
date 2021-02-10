package com.udacity.jwdnd.course1.cloudstorage.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {
    @FindBy(id = "inputUsername")
    private WebElement inputUsername;

    @FindBy(id="inputPassword")
    private WebElement inputPassword;

    @FindBy(id = "error-msg")
    private WebElement errorMsg;

    @FindBy(id = "logout-msg")
    private WebElement logoutMsg;

    @FindBy(id = "login-btn")
    private WebElement loginBtn;

    @FindBy(id = "logout-btn")
    private WebElement logoutBtn;

    public LoginPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void login(String username, String password){
        inputUsername.sendKeys(username);
        inputPassword.sendKeys(password);
        loginBtn.click();
    }

    public boolean hasLogoutMessage(){
        return logoutMsg.isDisplayed();
    }

    public boolean hasErrorMessage(){
        return errorMsg.isDisplayed();
    }

    public void logout(){
        logoutBtn.click();
    }


}
