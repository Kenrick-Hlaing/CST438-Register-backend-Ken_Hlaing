package com.cst438.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;
import java.security.Principal;

@RestController
@CrossOrigin
public class StudentController {
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	
	@Autowired
	GradebookService gradebookService;
	
	// Adds a student
	@PostMapping("/student")
	@Transactional
	public Student addStudent( @RequestBody StudentDTO studentDto ) {
		Student newStudent = new Student();
		newStudent.setName(studentDto.name());
		newStudent.setEmail(studentDto.email());
		newStudent.setStatusCode(studentDto.statusCode());
		newStudent.setStatus(studentDto.status());
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		newStudent.setPassword(encoder.encode(studentDto.password()));
		newStudent.setRole(studentDto.role());
		return studentRepository.save(newStudent);
	}
	
	// Deletes a student
	@DeleteMapping("/student/{student_id}")
	@Transactional
	public boolean deleteStudent(  @PathVariable int student_id ) {
		List<Enrollment> studentEnrollments = enrollmentRepository.findAllByStudentId(student_id);
		for (Enrollment enrollment : studentEnrollments) {
	        if (enrollmentRepository.existsById(enrollment.getEnrollment_id())) {
	            enrollmentRepository.deleteById(enrollment.getEnrollment_id());
	        }
	    }
		if (studentRepository.existsById(student_id)) {
            studentRepository.deleteById(student_id);
            return true;
        }
        return false;
	}
	
	// Updates a student
	@PutMapping("/student/{student_id}")
	@Transactional
	public boolean updateStudent(  @PathVariable int student_id, @RequestBody StudentDTO studentDto  ) {
		Student student = studentRepository.findById(student_id).orElse(null);
		if (student != null) {
            student.setName(studentDto.name());
    		student.setEmail(studentDto.email());
    		student.setStatusCode(studentDto.statusCode());
    		student.setStatus(studentDto.status());
            studentRepository.save(student);
            return true;
        } else {
            return false;
        }
	}
	
	// Lists all students
	@GetMapping("/students")
    public List<Student> getAllStudents() {
        List<Student> students = (List<Student>) studentRepository.findAll();
        return students;
    }
	
	// Get Student By Email
	@GetMapping("/student/{email}")
	public Student getStudentByEmail(@PathVariable String email) {
		Student student = studentRepository.findByEmail(email);
	    return student;
	}
}
