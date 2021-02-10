package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.page.LoginPage;
import com.udacity.jwdnd.course1.cloudstorage.page.NotePage;
import com.udacity.jwdnd.course1.cloudstorage.page.SignupPage;
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
public class NoteTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    final String firstname = "Android";
    final String lastname = "Ha";
    final String username = "hello";
    final String password = "secret";

    private static WebDriver driver;
    private static boolean isSignup;
    private SignupPage signupPage;
    private LoginPage loginPage;
    private NotePage notePage;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
        isSignup = false;
    }

    @BeforeEach
    public void beforeEach() {
        driver = new ChromeDriver();
        baseUrl = "http://localhost:" + this.port;

        // signup
        signup();
        // log in
        login();
        // enter home, prepare notePage
        goToHome();

    }

    @AfterEach
    public void afterEach() {
        if (this.driver != null) {
            driver.quit();
        }
    }


    @Test
    public void addOneNote() throws InterruptedException {
        int numBeforeAdd = notePage.getRowCount();
        // add a new note
        Note note = notePage.addOneNote("addOneNoteTitle", "addOneNoteDescription");
        int numAfterAdd = notePage.getRowCount();

        // verify the number of rows added
        assertEquals(numBeforeAdd+1, numAfterAdd);

        // verify if its shown
        WebElement noteRow = notePage.getNoteRow(note.getNoteTitle(), note.getNoteDescription());
        assertNotNull(noteRow);
    }

    @Test
    public void addMultipleNotes() throws InterruptedException {
        int numNotesAdded = 2;
        int numBeforeAdd = notePage.getRowCount();
        for(int i=0; i<numNotesAdded; i++){
            notePage.addOneNote("multipleNoteTitle"+i, "multipleNoteDescription"+i);
            Thread.sleep(1000);
        }
        int numAfterAdd = notePage.getRowCount();
        assertEquals(numBeforeAdd+numNotesAdded, numAfterAdd);
    }

    @Test
    public void editNote() throws InterruptedException {
        String oldTitle = "editNoteTitle";
        String newTitle = "new editNoteTitle";
        String oldDescription = "editNoteDescription";
        String newDescription = "new editNoteDescription";


        // add 1 note
        notePage.addOneNote(oldTitle,oldDescription);
        int numBeforeEdit = notePage.getRowCount();

        // edit it
        Thread.sleep(1000);
        notePage.editOneNote(oldTitle, newTitle,oldDescription, newDescription);
        int numAftereEdit = notePage.getRowCount();

        // verify the size
        assertEquals(numBeforeEdit, numAftereEdit);

        // verify content
        WebElement noteRow = notePage.getNoteRow(newTitle, newDescription);
        assertNotNull(noteRow);

    }

    @Test
    public void deleteNote() throws InterruptedException {
        String title = "deleteNote Title";
        String description = "deleteNote Description";

        // add 2 row
        notePage.addOneNote(title, description);
        Thread.sleep(1000);
        notePage.addOneNote(title+"not delete", description+"not delete");

        // delete
        int numBeforeDelete = notePage.getRowCount();
        Thread.sleep(1000);
        notePage.deleteOneNote(title, description);
        int numAfterDelete = notePage.getRowCount();
//
        // verify size
        assertEquals(numBeforeDelete-1, numAfterDelete);

        // verify appearance
        WebElement noteRow = notePage.getNoteRow(title, description);
        assertNull(noteRow);
    }

    private void goToHome() {
        driver.get(baseUrl+"/");
        notePage = new NotePage(driver);
        notePage.clickNoteTab();
    }

    private void login() {
        driver.get(baseUrl+"/login");
        loginPage = new LoginPage(driver);
        loginPage.login(username, password);
    }

    private void signup() {
        if (isSignup){return;}
        driver.get(baseUrl+"/signup");
        signupPage = new SignupPage(driver);
        signupPage.signupUser(firstname, lastname, username, password);
        isSignup = true;
    }
}
