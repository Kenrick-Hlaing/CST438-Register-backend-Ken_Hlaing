package com.cst438;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.cst438.domain.StudentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class JUnitTestStudent {
	@Autowired
	private MockMvc mvc;
	
	@Transactional
	@Test
	public void listStudents() throws Exception {
		MockHttpServletResponse response;
		
		// Get request for list of students
		response = mvc.perform(MockMvcRequestBuilders.get("/students")
				.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		// verify that all students are in list 
		StudentDTO[] dto_list = fromJsonString(response.getContentAsString(), StudentDTO[].class);
		
		// Starting students should be 3
		assertEquals(3, dto_list.length);
	}
	
	@Transactional
	@Test
	public void addStudent() throws Exception {
		String jsonRequest = "{ \"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"statusCode\": 1, \"status\": \"Active\" }";
		MockHttpServletResponse response;
		
		// Add student John Doe john.doe@example.com 1 Active
		response = mvc.perform(MockMvcRequestBuilders.post("/student")
				.contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
				.andReturn()
				.getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		// Get request for list of students
		response = mvc.perform(MockMvcRequestBuilders.get("/students")
				.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		// Pull student list
		StudentDTO[] dto_list = fromJsonString(response.getContentAsString(), StudentDTO[].class);
		
		boolean found = false;		
		for (StudentDTO st : dto_list) {
			if (st.name().equals("John Doe")) {
				found = true;
				break;
			}
		}
		assertTrue(found);
		// Students should be now 4
		assertNotEquals(3, dto_list.length);
	}
	
	@Transactional
	@Test
	public void updateStudent() throws Exception {
		System.out.println("HEEEEEEELLLO");
		String jsonRequest = "{ \"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"statusCode\": 1, \"status\": \"Active\" }";
		MockHttpServletResponse response;
		
		// Add student John Doe john.doe@example.com 1 Active
		response = mvc.perform(MockMvcRequestBuilders.post("/student")
				.contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
				.andReturn()
				.getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		// Update Student John Doe ID:4
		response = mvc.perform(MockMvcRequestBuilders.put("/student/6/status?newStatus=Inactive")
				.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		// Get request for list of students
		response = mvc.perform(MockMvcRequestBuilders.get("/students")
				.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		// Pull student list
		StudentDTO[] dto_list = fromJsonString(response.getContentAsString(), StudentDTO[].class);
		
		boolean found = false;		
		for (StudentDTO st : dto_list) {
			if (st.name().equals("John Doe") && st.status().equals("Inactive")) {
				found = true;
			}
		}
		assertTrue(found);
	}
	
	@Transactional
	@Test
	public void deleteStudent() throws Exception {
		String jsonRequest = "{ \"name\": \"John Doe\", \"email\": \"john.doe@example.com\", \"statusCode\": 1, \"status\": \"Active\" }";
		MockHttpServletResponse response;
		
		// Add student John Doe john.doe@example.com 1 Active
		response = mvc.perform(MockMvcRequestBuilders.post("/student")
				.contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
				.andReturn()
				.getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		// Get request for list of students
		response = mvc.perform(MockMvcRequestBuilders.get("/students")
				.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
			
		// verify that all students are in list 
		StudentDTO[] dto_list = fromJsonString(response.getContentAsString(), StudentDTO[].class);
				
		// Starting students should be 4
		assertEquals(4, dto_list.length);
		
		// Delete John Doe from database
		response = mvc.perform(MockMvcRequestBuilders.delete("/student/4")
				.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
				assertEquals(200, response.getStatus());
		
		// Get request for list of students
		response = mvc.perform(MockMvcRequestBuilders.get("/students")
				.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
				
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
				
		// Pull student list
		dto_list = fromJsonString(response.getContentAsString(), StudentDTO[].class);
				
		boolean found = false;		
		for (StudentDTO st : dto_list) {
			if (st.name() == "John Doe") {
				found = true;
			}
		}
		assertFalse(found);	
	}
	
	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
