package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.page.CredentialPage;
import com.udacity.jwdnd.course1.cloudstorage.page.LoginPage;
import com.udacity.jwdnd.course1.cloudstorage.page.SignupPage;
import com.udacity.jwdnd.course1.cloudstorage.page.TestUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CredentialTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    final String firstname = "Android";
    final String lastname = "Ha";
    final String username = "hello";
    final String password = "secret";


    private static WebDriver driver;
    private static boolean isSignup;
    private CredentialPage credentialPage;
    private SignupPage signupPage;
    private LoginPage loginPage;


    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
        isSignup = false;
    }

    @BeforeEach
    public void beforeEach() {
        driver = new ChromeDriver();
        baseUrl = "http://localhost:" + this.port;

        signup();
        login();
        goToHome();
    }

    @AfterEach
    public void afterEach() {
        if (this.driver != null) {
            driver.quit();
        }
    }

    @Test
    public void addOneCredential(){
        int rowNumberBeforeAdd = credentialPage.getRowCount();
        credentialPage.addOneCredential(TestUtil.url,TestUtil.username,TestUtil.password);

        // validate add one to the displayed list
        assertEquals(rowNumberBeforeAdd+1, credentialPage.getRowCount());

        // validate content
        // check the entry is shown in the list
        WebElement credentialRow = credentialPage.getCredentialRow(TestUtil.url, TestUtil.username);
        assertNotNull(credentialRow);
    }

    @Test
    public void editCredential() throws InterruptedException {
        String oldUrl = TestUtil.url2;
        String oldUsername = TestUtil.username2;
        String oldPassword = TestUtil.password2;
        String newUrl = "new"+TestUtil.url2;
        String newUsername = "new"+TestUtil.username2;
        String newPassword = "new"+TestUtil.password2;

        credentialPage.addOneCredential(oldUrl, oldUsername, oldPassword);
        int numRowBeforeEdit = credentialPage.getRowCount();
        Thread.sleep(1000);
        credentialPage.editOneRow(oldUrl, oldUsername, oldPassword, newUrl, newUsername, newPassword);
        int numRowAfterEdit = credentialPage.getRowCount();

        // make sure edit won't change the row number displayed
        assertEquals(numRowBeforeEdit, numRowAfterEdit);

        // make sure content is changed after edit
        WebElement credentialRow = credentialPage.getCredentialRow(newUrl, newUsername);
        assertNotNull(credentialRow);
    }

    @Test
    public void deleteCredential() throws InterruptedException {
        // first add one row
        credentialPage.addOneCredential(TestUtil.url3, TestUtil.username3, TestUtil.password3);
        Thread.sleep(1000);
        // then delete a row
        int numRowsBeforeDelete = credentialPage.getRowCount();
        credentialPage.deleteRow(TestUtil.url3, TestUtil.username3);
        int numRowsAfterDelete = credentialPage.getRowCount();

        assertEquals(numRowsBeforeDelete-1, numRowsAfterDelete);
    }

    private void goToHome() {
        driver.get(baseUrl+"/");
        credentialPage = new CredentialPage(driver);
        credentialPage.clickCredentialTab();
    }

    private void login() {
        driver.get(baseUrl+"/login");
        loginPage = new LoginPage(driver);
        loginPage.login(username, password);
    }

    private void signup() {
        if (isSignup){ return; }
        driver.get(baseUrl+"/signup");
        signupPage = new SignupPage(driver);
        signupPage.signupUser(firstname, lastname, username, password);
        isSignup = true;

    }
}
