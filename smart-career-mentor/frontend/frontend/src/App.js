import React, { useState } from 'react';
import axios from 'axios';
import './App.css';

const ResumeUpload = () => {
  const [file, setFile] = useState(null);
  const [parsedResume, setParsedResume] = useState(null);
  const [aiFeedback, setAiFeedback] = useState(null);
  const [jobDescription, setJobDescription] = useState('');
  const [error, setError] = useState(null);

  const handleFileChange = (event) => {
    setFile(event.target.files[0]);
    setError(null);
  };

  const handleJobDescriptionChange = (event) => {
    setJobDescription(event.target.value);
    setError(null);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    
    if (!file) {
      setError('Please select a resume file');
      return;
    }

    if (!jobDescription.trim()) {
      setError('Please enter a job description');
      return;
    }

    const formData = new FormData();
    formData.append("resume", file);
    formData.append("jobDescription", jobDescription);

    try {
      const response = await axios.post('http://localhost:8080/api/analyze', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      
      setParsedResume(response.data.resume);
      setAiFeedback(response.data.gpt);
      setError(null);
    } catch (error) {
      console.error("Error uploading file:", error);
      setError(error.response?.data?.message || 'An error occurred while processing your request');
    }
  };

  return (
    <div className="resume-upload-container">
      <h2>Resume Analysis Tool</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="resume">Choose a resume (PDF):</label>
          <input 
            type="file" 
            id="resume" 
            accept=".pdf" 
            onChange={handleFileChange}
            className="file-input"
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="jobDesc">Enter Job Description:</label>
          <textarea 
            id="jobDesc"
            value={jobDescription}
            onChange={handleJobDescriptionChange}
            placeholder="Enter the job description here..."
            className="job-description-input"
          />
        </div>
        
        {error && <div className="error-message">{error}</div>}
        
        <button type="submit" className="submit-button">Analyze Resume</button>
      </form>

      {parsedResume && (
        <div className="result-section">
          <h3>Parsed Resume:</h3>
          <pre className="parsed-content">{parsedResume}</pre>
        </div>
      )}

      {aiFeedback && (
        <div className="result-section">
          <h3>AI Feedback:</h3>
          <pre className="feedback-content">{aiFeedback}</pre>
        </div>
      )}
    </div>
  );
};

export default ResumeUpload; 