package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/")
@ControllerAdvice
public class HomeController {

    private FileService fileService;
    private NoteService noteService;
    private CredentialService credentialService;
    private UserService userService;
    private List<File> files;
    private List<Note> notes;
    private List<Credential> credentials;

    public HomeController(FileService fileService, NoteService noteService, CredentialService credentialService, UserService userService) {
        this.fileService = fileService;
        this.noteService = noteService;
        this.credentialService = credentialService;
        this.userService = userService;
    }

    @PostConstruct
    public void init(){
        // initialization
        files = new ArrayList<>();
        notes = new ArrayList<>();
        credentials = new ArrayList<>();
    }

    @GetMapping
    public String homeView(Authentication auth, Model model, HttpSession session){
        // active tab = file
        if (session.getAttribute("activeTab")==null){
            session.setAttribute("activeTab", "file");
        }
        // remove messages from other tabs
        removeMessageAttributes(session);

        // get user info
        Integer userId = userService.getUserIdByName(auth.getName());

        // add all files/notes/credentials belong to the user to model attribute
        addList(model, userId);

        // redirect to home
        return "home";
    }

    @PostMapping("/upload-file")
    public String uploadFile(MultipartFile fileUpload,
                             Authentication auth,
                             HttpSession session){
        // active tab = file
        session.setAttribute("activeTab","file");

        // get user info
        Integer userId = userService.getUserIdByName(auth.getName());

        // check if uploadFile is empty
        if (fileUpload.isEmpty()){
            session.removeAttribute("fileMessage");
            session.setAttribute("fileError","Please select a file to upload");
            return "redirect:/";
        }

        // check if filename already used
        if (!fileService.isFileNameAvailable(userId, fileUpload.getOriginalFilename())){
            session.removeAttribute("fileMessage");
            session.setAttribute("fileError","Please use a different filename");
            return "redirect:/";
        }

        try {
            byte[] data = fileUpload.getBytes();
            String filename = fileUpload.getOriginalFilename();
            String contentType = fileUpload.getContentType();
            String size = fileUpload.getSize()+"";

            // create a new File, set fields based on uploadFiles info
            File newFile = new File();
            newFile.setFileData(data);
            newFile.setFileSize(size);
            newFile.setFilename(filename);
            newFile.setContentType(contentType);
            newFile.setUserId(userId);

            // insert into database
            fileService.save(newFile);

            // add success message
            session.removeAttribute("fileError");
            session.setAttribute("fileMessage",filename + "is successfully uploaded.");

            return "redirect:/";

        } catch (IOException e) {
            e.printStackTrace();
            session.removeAttribute("fileMessage");
            session.setAttribute("fileError", e.getMessage());
        }

        return "redirect:/";
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    public String handleLargeFile(MultipartException ex,
                                  HttpSession session,
                                  Model model,
                                  Authentication auth){

        session.setAttribute("activeTab", "file");
        session.removeAttribute("fileMessage");
        session.setAttribute("fileError","File exceeds max size (2MB)");

        // add all files/notes/credentials belong to the user to model attribute
        // get user info
        Integer userId = userService.getUserIdByName(auth.getName());
        addList(model, userId);

        return "home";
    }

    @GetMapping("/view-file")
    public void viewFile(@RequestParam int fileId, HttpServletResponse response, HttpSession session) {

        // retrieve file from database
        File returnFile = fileService.getFileById(fileId);

        // prepare response if returnfile is not null
        if (returnFile != null){
            try {
                // set file to response
                response.setContentType(returnFile.getContentType());
                response.setContentLength(Integer.parseInt(returnFile.getFileSize()));
                response.setHeader("Content-Disposition","attachment; filename="+returnFile.getFilename());

                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(returnFile.getFileData());
                response.flushBuffer();
            } catch (IOException e) {
                e.printStackTrace();
                session.removeAttribute("fileMessage");
                session.setAttribute("fileError", e.getMessage());
            }
        }
    }

    @GetMapping("/delete-file")
    public String deleteFile(@RequestParam int fileId, Authentication auth, HttpSession session){
        // active tab = file
        session.setAttribute("activeTab","file");

        String deleteMessage = fileService.deleteFile(fileId);
        session.removeAttribute("fileError");
        session.setAttribute("fileMessage",deleteMessage);

        return "redirect:/";
    }

    /// note
    @PostMapping("/save-note")
    public String addNote(Authentication auth,
                          @RequestParam(required = false) Integer noteId,
                          @RequestParam String noteTitle,
                          @RequestParam String noteDescription,
                          HttpSession session){
        // set activeTab to note
        session.setAttribute("activeTab", "note");

        // get user info
        Integer userId = userService.getUserIdByName(auth.getName());
        // create a new note
        Note newNote = new Note();
        newNote.setUserId(userId);
        newNote.setNoteDescription(noteDescription);
        newNote.setNoteTitle(noteTitle);

        // save note into database
        if(noteId==null){
            noteService.save(newNote);
            session.setAttribute("noteMessage","Successfully created a new note.");
        } else {
            newNote.setNoteId(noteId);
            noteService.update(newNote);
            session.setAttribute("noteMessage", "Successfully updated the note.");
        }

        return "redirect:/";
    }

    @GetMapping("/delete-note")
    public String deleteNote(@RequestParam Integer noteId, Authentication auth, HttpSession session){
        // set activeTab to note
        session.setAttribute("activeTab", "note");

        String noteMessage = noteService.delete(noteId);
        session.setAttribute("noteMessage", noteMessage);

        return "redirect:/";
    }

    //// credential
    @PostMapping("/save-credential")
    public String saveCredential(Authentication auth,
                                 @RequestParam(required = false) Integer credentialId,
                                 @RequestParam String password,
                                 @RequestParam String url,
                                 @RequestParam String username,
                                 HttpSession session){
        // set activeTab to note
        session.setAttribute("activeTab","credential");

        // get user info
        Integer userId = userService.getUserIdByName(auth.getName());

        // check the existence of credential
        if(credentialId == null){

            // cannot create a new entry with existing url and username combination
            if(credentialService.isEntryExisted(url, username)){
                session.removeAttribute("credentialMessage");
                session.setAttribute("credentialError", username+" already existed in "+url);
                return "redirect:/";
            }

            // create a new Credential
            Credential newCredential =  new Credential();
            newCredential.setRawPassword(password);
            newCredential.setUrl(url);
            newCredential.setUsername(username);
            newCredential.setUserId(userId);
            credentialService.save(newCredential);
            session.removeAttribute("credentialError");
            session.setAttribute("credentialMessage","Successfully created a new credential.");

         } else {
            Credential updatedCredential = credentialService.getCredentialById(credentialId);
            updatedCredential.setRawPassword(password);
            updatedCredential.setUrl(url);
            updatedCredential.setUsername(username);
            credentialService.update(updatedCredential);
            session.removeAttribute("credentialError");
            session.setAttribute("credentialMessage","Successfully updated the credential.");
        }

        return "redirect:/";
    }

    @GetMapping("/delete-credential")
    public String deleteCredential(@RequestParam Integer credentialId, Authentication auth, HttpSession session){
        // set activeTab to note
        session.setAttribute("activeTab","credential");

        String deleteMsg = credentialService.delete(credentialId);

        Integer userId = userService.getUserIdByName(auth.getName());
        session.removeAttribute("credentialError");
        session.setAttribute("credentialMessage",deleteMsg);

        return "redirect:/";
    }

    // helper methods
    private void removeMessageAttributes(HttpSession session) {
        if (session.getAttribute("activeTab").equals("file")){
            session.removeAttribute("noteMessage");
            session.removeAttribute("credentialMessage");
            session.removeAttribute("credentialError");
        }else if (session.getAttribute("activeTab").equals("note")){
            session.removeAttribute("credentialMessage");
            session.removeAttribute("credentialError");
            session.removeAttribute("fileMessage");
            session.removeAttribute("fileError");
        }else if (session.getAttribute("activeTab").equals("credential")){
            System.out.println("===activeTab:credential, message: "+session.getAttribute("credentialMessage"));
            session.removeAttribute("fileMessage");
            session.removeAttribute("fileError");
            session.removeAttribute("noteMessage");
        }
    }

    private void addList(Model model, Integer userId) {
        files = fileService.getAllFiles(userId);
        notes = noteService.getAllNotes(userId);
        credentials = credentialService.getAllCredentials(userId);
        model.addAttribute("files", files);
        model.addAttribute("notes", notes);
        model.addAttribute("credentials", credentials);
    }
}
