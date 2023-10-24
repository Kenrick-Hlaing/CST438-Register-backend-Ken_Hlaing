package  com.cst438.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService  {
	@Autowired
	private StudentRepository repository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Student currentStudent = repository.findByEmail(email); 

		UserBuilder builder = null;
		if (currentStudent!=null) {
			builder = org.springframework.security.core.userdetails.User.withUsername(email);
			builder.password(currentStudent.getPassword());
			builder.roles(currentStudent.getRole());
		} else {
			throw new UsernameNotFoundException("User not found.");
		}

		return builder.build();	    
	}
}