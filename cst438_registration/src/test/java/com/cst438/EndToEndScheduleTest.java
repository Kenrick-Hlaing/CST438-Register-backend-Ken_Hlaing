package com.cst438;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

/*
 * This example shows how to use selenium testing using the web driver 
 * with Chrome browser.
 * 
 *  - Buttons, input, and anchor elements are located using XPATH expression.
 *  - onClick( ) method is used with buttons and anchor tags.
 *  - Input fields are located and sendKeys( ) method is used to enter test data.
 *  - Spring Boot JPA is used to initialize, verify and reset the database before
 *      and after testing.
 *      
 *    Make sure that TEST_COURSE_ID is a valid course for TEST_SEMESTER.
 *    
 *    URL is the server on which Node.js is running.
 */

@SpringBootTest
public class EndToEndScheduleTest {

	public static final String CHROME_DRIVER_FILE_LOCATION = "C:/Users/kenri/Downloads/chromedriver-win64/chromedriver-win64/chromedriver.exe";

	public static final String URL = "http://localhost:3000";

	public static final String TEST_USER_EMAIL = "test@csumb.edu";

	public static final int TEST_COURSE_ID = 40442; 

	public static final String TEST_SEMESTER = "2021 Fall";

	public static final int SLEEP_DURATION = 1000; // 1 second.
	
	public static final String TEST_ADD_NAME = "Ethan Winters";
	
	public static final String TEST_ADD_EMAIL = "ewinters@csumb.edu";
	
	public static final String TEST_STATUS = "Active";
	
	public static final int TEST_STATUS_CODE = 1;
	
	public static final String TEST_UPDATE_NAME = "Leon Kennedy";
	
	public static final String TEST_UPDATE_EMAIL = "lkennedy@csumb.edu";
	
	public static final String TEST_UPDATE_STATUS = "Inactive";
	
	public static final int TEST_UPDATE_STATUS_CODE = 0;


	/*
	 * add course TEST_COURSE_ID to schedule for 2021 Fall semester.
	 */
	
	@Test
	public void addCourseTest() throws Exception {

	
		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {

			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);

			// select the last of the radio buttons on the list of semesters page.
			
			List<WebElement> weList = driver.findElements(By.xpath("//input"));
			// should be 3 elements in list.  click on last one for 2021 Fall
			weList.get(2).click();

			// Locate and click "View Schedule" button
			
			driver.findElement(By.id("viewSchedule")).click();
			Thread.sleep(SLEEP_DURATION);

			// Locate and click "Add Course" button which is the first and only button on the page.
			driver.findElement(By.id("addCourse")).click();
			Thread.sleep(SLEEP_DURATION);

			// enter course no and click Add button
			
			driver.findElement(By.id("courseId")).sendKeys(Integer.toString(TEST_COURSE_ID));
			driver.findElement(By.id("add")).click();
			Thread.sleep(SLEEP_DURATION);

			/*
			* verify that new course shows in schedule.
			* search for the title of the course in the updated schedule.
			*/ 
			
			WebElement we = driver.findElement(By.xpath("//tr[td='"+TEST_COURSE_ID+"']"));
			assertNotNull(we, "Test course title not found in schedule after successfully adding the course.");
			
			// drop the course
			WebElement dropButton = we.findElement(By.xpath("//button"));
			assertNotNull(dropButton);
			dropButton.click();
			
			// the drop course action causes an alert to occur.  
			WebDriverWait wait = new WebDriverWait(driver, 1);
            wait.until(ExpectedConditions.alertIsPresent());
            
            Alert simpleAlert = driver.switchTo().alert();
            simpleAlert.accept();
            
            // check that course is no longer in the schedule
            Thread.sleep(SLEEP_DURATION);
            assertThrows(NoSuchElementException.class, () -> {
            	driver.findElement(By.xpath("//tr[td='"+TEST_COURSE_ID+"']"));
            });			

		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
		}

	}
	
	@Test
	public void addStudentTest() throws Exception{
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		try {
			driver.get(URL);
	        // must have a short wait to allow time for the page to download 
			Thread.sleep(SLEEP_DURATION);
			
			WebElement adminLink = 
	                 driver.findElement(By.xpath("//a[@href='/admin']"));
			// go to admin page
			adminLink.click();
			Thread.sleep(SLEEP_DURATION);
			
			// Locate and click "Add Student" button.
			driver.findElement(By.id("addStudent")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// enter student name, email, statusCode, and status. Then click Add button
			driver.findElement(By.id("name")).sendKeys(TEST_ADD_NAME);
			driver.findElement(By.id("email")).sendKeys(TEST_ADD_EMAIL);
			driver.findElement(By.id("statusCode")).sendKeys(Integer.toString(TEST_STATUS_CODE));
			driver.findElement(By.id("status")).sendKeys(TEST_STATUS);
			driver.findElement(By.id("add")).click();
			Thread.sleep(SLEEP_DURATION);
			
			/*
			* verify that new student is in student table.
			* search for the student email in table.
			*/
			WebElement we = driver.findElement(By.xpath("//tr[td='"+TEST_ADD_EMAIL+"']"));
			assertNotNull(we, "Test student email not found in table after successfully adding the course.");
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
		}
	}
	
	@Test
	public void UpdateStudentTest() throws Exception{
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		try {
			driver.get(URL);
	        // must have a short wait to allow time for the page to download 
			Thread.sleep(SLEEP_DURATION);
			
			WebElement adminLink = 
	                 driver.findElement(By.xpath("//a[@href='/admin']"));
			// go to admin page
			adminLink.click();
			Thread.sleep(SLEEP_DURATION);
			
			// Locate correct student and click "Update Student" button.
			driver.findElement(By.xpath("//tr[4]")).findElement(By.id("updateStudent")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// enter new student name, email, statusCode, and status. Then click Add button
			driver.findElement(By.id("name")).sendKeys(TEST_UPDATE_NAME);
			driver.findElement(By.id("email")).sendKeys(TEST_UPDATE_EMAIL);
			driver.findElement(By.id("statusCode")).sendKeys(Integer.toString(TEST_UPDATE_STATUS_CODE));
			driver.findElement(By.id("status")).sendKeys(TEST_UPDATE_STATUS);
			driver.findElement(By.id("update")).click();
			Thread.sleep(SLEEP_DURATION);
			
			/*
			* verify that updated student is in student table.
			* search for the student by email and name in table.
			*/
			WebElement we = driver.findElement(By.xpath("//tr[td='"+TEST_UPDATE_EMAIL+"']"));
			assertNotNull(we, "Test student email not found in table after successfully updating the course.");
			
			WebElement we2 = driver.findElement(By.xpath("//tr[td='"+TEST_UPDATE_NAME+"']"));
			assertNotNull(we2, "Test student name not found in table after successfully updating the course.");
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
		}
	}
	
	@Test
	public void DeleteStudentTest() throws Exception{
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		try {
			driver.get(URL);
	        // must have a short wait to allow time for the page to download 
			Thread.sleep(SLEEP_DURATION);
			
			WebElement adminLink = 
	                 driver.findElement(By.xpath("//a[@href='/admin']"));
			// go to admin page
			adminLink.click();
			Thread.sleep(SLEEP_DURATION);
			
			// Locate correct student and click "Delete" button.
			driver.findElement(By.xpath("//tr[4]")).findElement(By.id("delete")).click();
			Thread.sleep(SLEEP_DURATION);
			
			/*
			* verify that deleted student is not in student table.
			* search for the student by email and name in table.
			*/
			List<WebElement> listTd = driver.findElements(By.xpath("//td"));
			assertEquals(listTd.size(), 3);
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.quit();
		}
	}
}
