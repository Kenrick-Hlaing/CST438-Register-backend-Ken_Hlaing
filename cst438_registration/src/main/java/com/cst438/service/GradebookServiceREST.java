package com.cst438.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;

@Service
@ConditionalOnProperty(prefix = "gradebook", name = "service", havingValue = "rest")
@RestController
public class GradebookServiceREST implements GradebookService {

	private RestTemplate restTemplate = new RestTemplate();

	//@Value("${gradebook.url}")
	private static String gradebook_url="http://localhost:8081/enrollment";

	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
		System.out.println("Start Message "+ student_email +" " + course_id); 
	
		// TODO use RestTemplate to send message to gradebook service
		EnrollmentDTO enrollmentDTO = new EnrollmentDTO(0, student_email, student_name, course_id);
		ResponseEntity<EnrollmentDTO> response = restTemplate.postForEntity(gradebook_url, enrollmentDTO, EnrollmentDTO.class);

        if (response.getStatusCodeValue() == 200) {
            System.out.println("Enrollment successful");
        } else {
            System.err.println("Enrollment failed. HTTP Status: " + response.getStatusCodeValue());
        }
	}
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	/*
	 * endpoint for final course grades
	 */
	@PutMapping("/course/{course_id}")
	@Transactional
	public void updateCourseGrades( @RequestBody FinalGradeDTO[] grades, @PathVariable("course_id") int course_id) {
		System.out.println("Grades received "+grades.length);
		
		//TODO update grades in enrollment records with grades received from gradebook service
		for(int i = 0; i < grades.length; i++) {
			Enrollment enrollment = enrollmentRepository.findByEmailAndCourseId(grades[i].studentEmail(), course_id);
			if(enrollment != null) {
				enrollment.setCourseGrade(grades[i].grade());
				enrollmentRepository.save(enrollment);
			}
		}
		
	}
}
