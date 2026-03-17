package com.serkowski.controller;

import com.serkowski.model.text.TextRequest;
import com.serkowski.services.VectorStoreService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/etl")
public class ETLController {

    private final VectorStoreService vectorStoreService;

    public ETLController(VectorStoreService vectorStoreService) {
        this.vectorStoreService = vectorStoreService;
    }

    @PostMapping("/store")
    String store(@RequestParam("file") MultipartFile file) {
        vectorStoreService.storeAsVector(file.getResource());
        return "File " + file.getOriginalFilename() + " stored as vector successfully.";
    }
}
