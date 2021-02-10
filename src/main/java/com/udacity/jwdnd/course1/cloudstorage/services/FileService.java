package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {
    private final FileMapper fileMapper;

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public int save(File file){
        int fileId = fileMapper.insert(file);
        return fileId;
    }

    public File getFileById(int fileId){
        return fileMapper.getFileById(fileId);
    }

    public List<File> getAllFiles(int userId){
        return fileMapper.getFiles(userId);
    }

    public String deleteFile(int fileId){
        // handle  non-existing fileId
        if (fileMapper.getFileById(fileId)!=null) {
            fileMapper.delete(fileId);
            return "Successfully deleted the file.";
        }
        return "file doesn't exist";
    }

    public boolean isFileNameAvailable(int userId, String fileName){
        return fileMapper.getFileByFileName(userId, fileName)==null;
    }
}
