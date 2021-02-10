package com.udacity.jwdnd.course1.cloudstorage.page;

import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class CredentialPage {
    @FindBy(id="nav-credentials-tab")
    private WebElement navCredentials;

    @FindBy(id="add-credential-btn")
    private WebElement addCredentialBtn;

    @FindBy(className = "row-credential")
    private List<WebElement> rowCredentials;

    @FindBy(id = "save-credential-btn")
    private WebElement saveCredentialBtn;

    @FindBy(id = "credential-url")
    private WebElement credentialUrlTextEdit;

    @FindBy(id = "credential-username")
    private WebElement credentialUsernameTextEdit;

    @FindBy(id = "credential-password")
    private WebElement credentialPasswordTextEdit;

    private WebDriver driver;

    public CredentialPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickCredentialTab(){
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(navCredentials)).click();
    }

    public void addOneCredential(String url, String username, String password){
        new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(addCredentialBtn)).click();
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(credentialUrlTextEdit)).sendKeys(url);
        credentialUsernameTextEdit.sendKeys(username);
        credentialPasswordTextEdit.sendKeys(password);
        saveCredentialBtn.click();
    }

    public String encryptPassword(String password) {
        EncryptionService encryptionService = new EncryptionService();
        String encryptValue = encryptionService.encryptValue(password, TestUtil.createEncodedKey());
        return encryptValue;
    }

    public WebElement getCredentialRow(String url, String username){
        for (WebElement e: rowCredentials){
            WebElement urlElement = e.findElement(By.className("credential-url"));
            WebElement usernameElement = e.findElement(By.className("credential-username"));

            if (urlElement.getText().equals(url) &&
            usernameElement.getText().equals(username)){
              return e;
            }
        }
        return null;
    }

    public String getEncryptedPasswordFromRow(WebElement oneRowElement){
        return oneRowElement.findElement(By.className("credential-password")).getText();
    }

    public int getRowCount(){
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(addCredentialBtn));
        return rowCredentials.size();
    }

    public void editOneRow(String oldUrl, String oldUsername, String oldPassword, String newUrl, String newUsername, String newPassword){
        // get current row
        WebElement currentRow = getCredentialRow(oldUrl, oldUsername);
        WebElement editBtn = currentRow.findElement(By.className("edit-credential-btn"));

        // click edit
        new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(editBtn)).click();

        // enter new entry
        new WebDriverWait(driver, 15).until(ExpectedConditions.visibilityOf(credentialUrlTextEdit)).clear();
        credentialUrlTextEdit.sendKeys(newUrl);
        credentialUsernameTextEdit.clear();
        credentialUsernameTextEdit.sendKeys(newUsername);
        credentialPasswordTextEdit.clear();
        credentialPasswordTextEdit.sendKeys(newPassword);
        saveCredentialBtn.click();
    }

    public void deleteRow(String url, String username){
        // get current row
        WebElement currentRow = getCredentialRow(url, username);
        WebElement deleteBtn = currentRow.findElement(By.className("credential-delete"));

        //click delete button
        new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(deleteBtn)).click();
        new WebDriverWait(driver, 15).until(ExpectedConditions.alertIsPresent()).accept();
    }
}
