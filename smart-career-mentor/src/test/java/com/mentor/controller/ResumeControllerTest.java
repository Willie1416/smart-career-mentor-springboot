package com.mentor.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.mentor.service.ResumeService;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ResumeController.class)
public class ResumeControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResumeService resumeService;

    @Test
    void testAnalyzeResume() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "resume",
            "resume.pdf",
            "applications/pdf",
            "This is a test resume content".getBytes()
        );

        // Mock the behavior of resumeService.parseResume() to return a predefined string
        when(resumeService.parseResume(file)).thenReturn("This is a test resume content");

        mockMvc.perform(multipart("/api/analyze")
                        .file(file)
                        .param("jobDesc", "Looking for a Java backend engineer"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resume").value("This is a test resume content"))
                    .andExpect(jsonPath("$.job").value("Looking for a Java backend engineer"));
    }
}