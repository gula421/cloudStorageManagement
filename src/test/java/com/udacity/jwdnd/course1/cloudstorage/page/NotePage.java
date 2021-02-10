package com.udacity.jwdnd.course1.cloudstorage.page;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.origin.SystemEnvironmentOrigin;

import java.util.ArrayList;
import java.util.List;

public class NotePage {
    @FindBy(id="nav-notes-tab")
    private WebElement navNotesTab;

    @FindBy(id = "add-note-btn")
    private WebElement  addNoteBtn;

    @FindBy(id = "note-title")
    private WebElement noteTitleTextEdit;

    @FindBy(id = "note-description")
    private WebElement noteDescriptionTextEdit;

    @FindBy(id = "note-save")
    private WebElement noteSaveBtn;

    @FindBy(className = "note-row")
    private List<WebElement> noteElements;

    private WebDriver driver;

    public NotePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickNoteTab(){
        navNotesTab.click();
    }

    public Note addOneNote(String noteTitle, String noteDescription) {
        // add note with ui
        new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(addNoteBtn)).click();
        new WebDriverWait(driver, 15).until(ExpectedConditions.visibilityOf(noteTitleTextEdit)).sendKeys(noteTitle);
        noteDescriptionTextEdit.sendKeys(noteDescription);
        noteSaveBtn.click();

        Note note = new Note();
        note.setNoteTitle(noteTitle);
        note.setNoteDescription(noteDescription);
        return note;
    }


    public int getRowCount(){
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(addNoteBtn));
        return noteElements.size();
    }

    public WebElement getNoteRow(String title, String description){
        for(WebElement noteElement : noteElements){
            WebElement titleElement = noteElement.findElement(By.className("note-title"));
            WebElement descriptionElement = noteElement.findElement(By.className("note-description"));
            if (titleElement.getText().equals(title) && descriptionElement.getText().equals(description)){
                return noteElement;
            }
        }
        return null;
    }

    public void deleteOneNote(String title, String description){
        // get current row
        WebElement currentRow = getNoteRow(title, description);
        WebElement deleteBtn = currentRow.findElement(By.className("note-delete"));

        //click delete button
        new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(deleteBtn)).click();
        new WebDriverWait(driver, 15).until(ExpectedConditions.alertIsPresent()).accept();
    }

    public void editOneNote(String oldTitle, String newTitle, String oldDescription,  String newDescription){
        WebElement noteRow = getNoteRow(oldTitle, oldDescription);
        WebElement editBtn = noteRow.findElement(By.className("edit-note-btn"));

        // click edit
        new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(editBtn)).click();
        // edit pop up window
       new WebDriverWait(driver, 15).until(ExpectedConditions.visibilityOf(noteTitleTextEdit)).clear();
       noteTitleTextEdit.sendKeys(newTitle);
       noteDescriptionTextEdit.clear();
       noteDescriptionTextEdit.sendKeys(newDescription);
       noteSaveBtn.click();
    }

}
