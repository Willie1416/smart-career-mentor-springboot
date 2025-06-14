package com.mentor.controller;

import com.mentor.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


// Class that extracts resume and job description and return them in a hashmap
@RestController
@RequestMapping("/api")
public class ResumeController {
    
    @Autowired
    private ResumeService resumeService;

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, String>> analyzeResume(
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("jobDesc") String jobDesc) throws IOException{

                String resumeText = resumeService.parseResume(resume);
                String jobText = jobDesc.trim();

                Map<String, String> result = new HashMap<>();
                result.put("resume", resumeText);
                result.put("job", jobText);
                
                return ResponseEntity.ok(result);
            }
}
