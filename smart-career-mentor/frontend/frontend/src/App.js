import React, { useState } from 'react';
import axios from 'axios';
import './App.css';

const ResumeUpload = () => {
  const [file, setFile] = useState(null);
  const [parsedResume, setParsedResume] = useState(null);
  const [aiFeedback, setAiFeedback] = useState(null);
  const [jobDescription, setJobDescription] = useState('');

  const handleFileChange = (event) => {
    setFile(event.target.files[0]);
  };

  const handleJobDescriptionChange = (event) => {
    setJobDescription(event.target.value);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    
    const formData = new FormData();
    formData.append("resume", file);
    formData.append("jobDescription", jobDescription); // Add jobDescription to formData

    try {
      const response = await axios.post('http://localhost:8080/api/analyze', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      
      setParsedResume(response.data.parsedText);
      setAiFeedback(response.data.aiFeedback);
    } catch (error) {
      console.error("Error uploading file:", error);
    }
  };

  return (
    <div>
      <h2>Upload Resume</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="resume">Choose a resume to upload:</label>
          <input type="file" id="resume" accept=".pdf" onChange={handleFileChange} />
        </div>
        
        <div>
          <label htmlFor="jobDesc">Enter Job Description:</label>
          <textarea 
            id="jobDesc"
            value={jobDescription}
            onChange={handleJobDescriptionChange}
            placeholder="Enter the job description here..."
            rows="4"
            cols="50"
          />
        </div>
        
        <button type="submit">Upload and Analyze</button>
      </form>

      {parsedResume && (
        <div>
          <h3>Parsed Resume:</h3>
          <p>{parsedResume}</p>
        </div>
      )}

      {aiFeedback && (
        <div>
          <h3>AI Feedback:</h3>
          <p>{aiFeedback}</p>
        </div>
      )}
    </div>
  );
};

export default ResumeUpload;
