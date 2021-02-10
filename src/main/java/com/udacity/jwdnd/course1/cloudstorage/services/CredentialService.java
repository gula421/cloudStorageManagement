package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class CredentialService {
    private final CredentialMapper credentialMapper;
    private final EncryptionService encryptionService;

    public CredentialService(CredentialMapper credentialMapper, EncryptionService encryptionService) {
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    /**
     * create a new key for each new Credential
     * then save the encrypted password
     * @param newCredential
     * @return credentialId
     * */
    public int save(Credential newCredential){
        // the input has fields: url, username, rawPassword, userId
        // create a key and generate encryptPassword
        byte[] key = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        String encryptPassword = encryptionService.encryptValue(newCredential.getRawPassword(), encodedKey);

        // update newCredential
        newCredential.setKey(encodedKey);
        newCredential.setPassword(encryptPassword);

        // save newCredential into database
        // only url, username, encodedKey, encryptPassword, userId are saved
        return credentialMapper.insert(newCredential);
    }

    /**
     * Display all credentials
     * the saved encrypted passwords are displayed
     * add decryptPassword to each credential item
     * @param userId
     * */
    public List<Credential> getAllCredentials(int userId){
        List<Credential> list = credentialMapper.getCredentials(userId);
        for(Credential c : list){
            // set decrpyPassword
            String decryptPassword = encryptionService.decryptValue(c.getPassword(), c.getKey());
            c.setRawPassword(decryptPassword);
        }
        return list;
    }

    public Credential getCredentialById(int credentialId){
        return credentialMapper.getCredentialById(credentialId);
    }

    /**
     * for update a single credential
     * @param credential
     * */
    public void update(Credential credential){
        // rawPassword is typed by the user
        // convert rawPassword to encryptPassword
        String encryptPassword = encryptionService.encryptValue(credential.getRawPassword(), credential.getKey());
        credential.setPassword(encryptPassword);
        // update database
        credentialMapper.update(credential);
    }

    public String delete(int credentialId){
        // check the existence of Id
        if(credentialMapper.getCredentialById(credentialId)!=null){
            credentialMapper.delete(credentialId);
            return "Successfully deleted the credential";
        } else{
            return "The credential does not exist";
        }

    }

    public boolean isEntryExisted(String url, String username){
        Credential credentialByUrlAndUsername = credentialMapper.getCredentialByUrlAndUsername(url, username);
        return (credentialByUrlAndUsername != null);
    }


}
